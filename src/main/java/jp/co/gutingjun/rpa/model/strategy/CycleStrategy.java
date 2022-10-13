package jp.co.gutingjun.rpa.model.strategy;

import jp.co.gutingjun.common.util.DateUtil;
import jp.co.gutingjun.rpa.common.CycleTypeEnum;
import jp.co.gutingjun.rpa.common.FrequencyTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/** 周期策略 */
@Data
@NoArgsConstructor
public class CycleStrategy implements IBotStrategy {
  /** 策略名称 */
  private String name;

  /** 策略周期类型 */
  private CycleTypeEnum cycleType;

  /** 周期间隔 */
  private int cycleInteval;

  /** 策略有效期开始时间 */
  @NotNull private LocalDateTime cycleBeginTime;

  /** 策略有效期结束时间 */
  private LocalDateTime cycleEndTime;

  /**
   * <b>周期内打指定执行点</b><br>
   * - 周期为月：指定执行点为数字，第n天或最后一天，最后一天用99表示<br>
   * - 周期为周：指定执行点为数字，代表星期n，其中m=[1, 7]，对应[星期一,星期日] 标准：ISO-8601<br>
   * - 周期为天：指定执行点为间隔天数
   */
  private String[] timeInCycle;

  /**
   * <b>一天内只发生一次的时间点</b><br>
   * 时间点格式：HH:mm:ss
   */
  private LocalTime timeInADay;

  /** 一天内发生频率类型 */
  private FrequencyTypeEnum frequencyType;

  /**
   * <b>一天内发生频率</b><br>
   * 如【频率类型 】= 小时，【频率】 = 2<br>
   * 即为一天内每2小时触发一次
   */
  private int cycleInADay;

  /**
   * <b>一天内按频率发生的起始时间点</b><br>
   * 时间点格式：HH:mm:ss
   */
  @NotNull private LocalTime startTimeInADay;

  /**
   * <b>一天内按频率发生的结束时间点</b><br>
   * 时间点格式：HH:mm:ss
   */
  private LocalTime endTimeInADay;

  @Override
  public String getName() {
    if (StringUtils.isBlank(name)) {
      name = "周期策略";
    }
    return name;
  }

  @Override
  public void setName(String value) {
    name = value;
  }

  @Override
  public boolean validate(Object source, Object refValue) {
    // 上行对象未传入检查对象，即取服务器时间进行判断
    LocalDateTime sourceValue = null;
    if (source instanceof LocalDateTime) {
      sourceValue = (LocalDateTime) source;
    } else if (source instanceof Date) {
      sourceValue = DateUtil.getLocalDateTime((Date) source);
    }

    // 清除毫秒影响
    sourceValue = sourceValue.withNano(0);

    // 取系统时间，方法结束前不重新取值，以免发生毫秒进位误差
    LocalDateTime now =
        refValue instanceof LocalDateTime ? (LocalDateTime) refValue : LocalDateTime.now();
    // 清除毫秒影响
    now = now.withNano(0);
    if (sourceValue == null) {
      sourceValue = now;
    }

    // 先匹配天
    boolean todayRun = false;
    // 在周期有效期内：策略有效期结束时间(cycleEndTime)为空，即为长期有效
    if ((sourceValue.isEqual(getCycleBeginTime()) || sourceValue.isAfter(getCycleBeginTime()))
        && (getCycleEndTime() == null
            || (sourceValue.isEqual(getCycleEndTime()))
            || (sourceValue.isBefore(getCycleEndTime())))) {
      if (cycleType.equals(CycleTypeEnum.MONTH)) {
        // 按月执行
        todayRun = isTodayRunningByMonth(sourceValue, now);
      } else if (cycleType.equals(CycleTypeEnum.WEEK)) {
        // 按周执行
        todayRun = isTodayRunByWeek(sourceValue);
      } else if (cycleType.equals(CycleTypeEnum.DAY)) {
        // 按天执行
        todayRun = isTodayRun(sourceValue);
      }
    }

    if (todayRun) {
      if (!frequencyType.equals(FrequencyTypeEnum.ONCE)) {
        // 取当前日期可执行范围
        LocalDateTime curDateStartTime =
            LocalDateTime.parse(now.toLocalDate() + "T" + getStartTimeInADay());
        LocalDateTime curDateEndTime =
            LocalDateTime.parse(
                now.toLocalDate()
                    + "T"
                    + (getEndTimeInADay() == null ? "23:59:59" : getEndTimeInADay().toString()));

        // 取当前日期拼接时间
        LocalDateTime checkTime = curDateStartTime;
        while (sourceValue.isAfter(checkTime)) {
          // 一天内按频率发生
          if (getFrequencyType().equals(FrequencyTypeEnum.HOUR)) {
            checkTime = checkTime.plusHours(getCycleInADay());
          } else if (getFrequencyType().equals(FrequencyTypeEnum.MINUTE)) {
            checkTime = checkTime.plusMinutes(getCycleInADay());
          } else if (getFrequencyType().equals(FrequencyTypeEnum.SECOND)) {
            checkTime = checkTime.plusSeconds(getCycleInADay());
          }
        }

        return sourceValue.equals(checkTime)
                && (checkTime.isEqual(curDateStartTime) || checkTime.isAfter(curDateStartTime))
                && (checkTime.isEqual(curDateEndTime) || checkTime.isBefore(curDateEndTime));
      } else {
        // 当天仅执行一次，检查执行时间是否匹配
        // 一天只发生一次时间不为空
        // 取当前日期拼接时间
        LocalDateTime checkTime = LocalDateTime.parse(now.toLocalDate() + "T" + getTimeInADay());
        return checkTime.isEqual(sourceValue);
      }
    }

    return false;
  }

  private boolean isTodayRun(LocalDateTime sourceValue) {
    boolean todayRun = false;
    LocalDate checkDate = getCycleBeginTime().toLocalDate();
    if (getCycleInteval() > 0) {
      while (sourceValue.toLocalDate().isAfter(checkDate)) {
        checkDate = checkDate.plusDays(getCycleInteval());
      }
    }
    // 当天不执行，直接返回False
    if (checkDate.isEqual(sourceValue.toLocalDate())) {
      todayRun = true;
    }
    return todayRun;
  }

  private boolean isTodayRunByWeek(LocalDateTime sourceValue) {
    boolean todayRun = false;
    // 当周是否执行
    LocalDate checkBeginDate =
        getCycleBeginTime().toLocalDate().minusDays(getCycleBeginTime().getDayOfMonth() - 1);
    LocalDate checkEndDate =
        getCycleBeginTime().toLocalDate().plusDays(7 - getCycleBeginTime().getDayOfMonth() + 1);
    while (sourceValue.toLocalDate().isAfter(checkBeginDate)) {
      checkBeginDate = checkBeginDate.plusWeeks(getCycleInteval());
      checkEndDate = checkEndDate.plusWeeks(getCycleInteval());
    }
    // 当周执行，再判断当天是否执行
    if ((sourceValue.toLocalDate().isAfter(checkBeginDate)
            || sourceValue.toLocalDate().isEqual(checkBeginDate))
        && (sourceValue.toLocalDate().isBefore(checkEndDate)
            || sourceValue.toLocalDate().isEqual(checkEndDate))) {
      for (String day : getTimeInCycle()) {
        if (Integer.valueOf(day) == sourceValue.getDayOfWeek().getValue()) {
          todayRun = true;
          break;
        }
      }
    }
    return todayRun;
  }

  private boolean isTodayRunningByMonth(LocalDateTime sourceValue, LocalDateTime now) {
    boolean todayRun = false;
    LocalDate checkDate = getCycleBeginTime().toLocalDate();
    // 当月是否执行
    while (Long.parseLong(checkDate.getYear() + String.format("%02d", checkDate.getMonthValue()))
        < Long.parseLong(
            sourceValue.getYear() + String.format("%02d", sourceValue.getMonthValue()))) {
      checkDate = checkDate.plusMonths(getCycleInteval());
    }
    // 如当月执行，再判断当天是否执行
    if (Long.parseLong(checkDate.getYear() + String.format("%02d", checkDate.getMonthValue()))
        == Long.parseLong(
            sourceValue.getYear() + String.format("%02d", sourceValue.getMonthValue()))) {
      for (String day : getTimeInCycle()) {
        LocalDate dateOfMonth =
            LocalDate.parse(
                now.toLocalDate().getYear()
                    + "-"
                    + String.format("%02d", now.toLocalDate().getMonthValue())
                    + "-"
                    + (day.equals("99")
                        ? now.toLocalDate().getDayOfMonth()
                        : String.format("%02d", Integer.valueOf(day))));
        if (sourceValue.toLocalDate().equals(dateOfMonth)) {
          todayRun = true;
          break;
        }
      }
    }
    return todayRun;
  }
}

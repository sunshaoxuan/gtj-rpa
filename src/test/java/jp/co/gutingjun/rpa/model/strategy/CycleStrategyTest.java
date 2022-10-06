package jp.co.gutingjun.rpa.model.strategy;

import jp.co.gutingjun.rpa.common.CycleTypeEnum;
import jp.co.gutingjun.rpa.common.FrequencyTypeEnum;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

class CycleStrategyTest {

  @Test
  /** 测试按月执行策略 */
  void validateByMonth() {
    LocalDateTime now = LocalDateTime.now();
    now = now.withNano(0);

    // 按年(即按每12个月)
    CycleStrategy cycleStrategy = new CycleStrategy();
    cycleStrategy.setCycleType(CycleTypeEnum.MONTH);
    cycleStrategy.setCycleInteval(12);
    cycleStrategy.setFrequencyType(FrequencyTypeEnum.ONCE);
    cycleStrategy.setCycleBeginTime(now.minusYears(1));
    cycleStrategy.setCycleEndTime(now.plusYears(1));
    cycleStrategy.setTimeInADay(now.toLocalTime());
    cycleStrategy.setTimeInCycle(new String[] {"99", String.format("%02d", now.getDayOfMonth())});
    Assert.assertEquals(true, cycleStrategy.validate(now, now));

    // 按小时
    cycleStrategy.setFrequencyType(FrequencyTypeEnum.HOUR);
    cycleStrategy.setStartTimeInADay(now.toLocalTime().minusHours(12));
    cycleStrategy.setCycleInADay(1);
    Assert.assertEquals(true, cycleStrategy.validate(now, now));

    // 按分钟
    cycleStrategy.setFrequencyType(FrequencyTypeEnum.MINUTE);
    cycleStrategy.setStartTimeInADay(now.toLocalTime().minusMinutes(10));
    cycleStrategy.setCycleInADay(1);
    Assert.assertEquals(true, cycleStrategy.validate(now, now));

    // 按每个月
    cycleStrategy.setFrequencyType(FrequencyTypeEnum.ONCE);
    cycleStrategy.setCycleInteval(1);
    Assert.assertEquals(true, cycleStrategy.validate(now, now));

    // 按每2个月
    cycleStrategy.setFrequencyType(FrequencyTypeEnum.ONCE);
    cycleStrategy.setCycleInteval(2);
    Assert.assertEquals(true, cycleStrategy.validate(now, now));

    // 按每2个月
    cycleStrategy.setFrequencyType(FrequencyTypeEnum.ONCE);
    cycleStrategy.setCycleInteval(3);
    Assert.assertEquals(true, cycleStrategy.validate(now, now));

    // 按每5个月
    cycleStrategy.setFrequencyType(FrequencyTypeEnum.ONCE);
    cycleStrategy.setCycleInteval(5);
    Assert.assertEquals(false, cycleStrategy.validate(now, now));

    // 按每个月最后一天
    cycleStrategy.setFrequencyType(FrequencyTypeEnum.ONCE);
    cycleStrategy.setCycleInteval(1);
    Assert.assertEquals(
        true,
        cycleStrategy.validate(
            now.with(TemporalAdjusters.lastDayOfMonth()),
            now.with(TemporalAdjusters.lastDayOfMonth())));

    Assert.assertEquals("周期策略", cycleStrategy.getName());
  }

  @Test
  /** 测试按周执行策略 */
  void validateByWeek() {
    LocalDateTime now = LocalDateTime.now();
    now = now.withNano(0);

    // 按每周
    CycleStrategy cycleStrategy = new CycleStrategy();
    cycleStrategy.setCycleType(CycleTypeEnum.WEEK);
    cycleStrategy.setCycleInteval(1);
    cycleStrategy.setFrequencyType(FrequencyTypeEnum.ONCE);
    cycleStrategy.setCycleBeginTime(now.minusMonths(1));
    cycleStrategy.setCycleEndTime(now.plusMonths(1));
    cycleStrategy.setTimeInADay(now.toLocalTime());
    cycleStrategy.setTimeInCycle(
        new String[] {String.format("%02d", now.getDayOfWeek().getValue())});
    Assert.assertEquals(true, cycleStrategy.validate(now, now));

    cycleStrategy.setFrequencyType(FrequencyTypeEnum.HOUR);
    cycleStrategy.setStartTimeInADay(now.toLocalTime().minusHours(12));
    cycleStrategy.setCycleInADay(1);
    Assert.assertEquals(true, cycleStrategy.validate(now, now));

    // 按每2周
    cycleStrategy.setFrequencyType(FrequencyTypeEnum.ONCE);
    cycleStrategy.setCycleInteval(2);
    Assert.assertEquals(true, cycleStrategy.validate(now, now));

    // 按每3周
    cycleStrategy.setFrequencyType(FrequencyTypeEnum.ONCE);
    cycleStrategy.setCycleInteval(3);
    Assert.assertEquals(true, cycleStrategy.validate(now, now));

    // 按每5周
    cycleStrategy.setFrequencyType(FrequencyTypeEnum.ONCE);
    cycleStrategy.setCycleInteval(5);
    Assert.assertEquals(false, cycleStrategy.validate(now, now));

    // 按每8周
    cycleStrategy.setFrequencyType(FrequencyTypeEnum.ONCE);
    cycleStrategy.setCycleInteval(8);
    Assert.assertEquals(false, cycleStrategy.validate(now, now));
  }

  @Test
  /** 测试按天执行策略 */
  void validateByDay() {
    LocalDateTime now = LocalDateTime.now();
    now = now.withNano(0);

    // 按每天
    CycleStrategy cycleStrategy = new CycleStrategy();
    cycleStrategy.setCycleType(CycleTypeEnum.DAY);
    cycleStrategy.setCycleInteval(1);
    cycleStrategy.setFrequencyType(FrequencyTypeEnum.ONCE);
    cycleStrategy.setCycleBeginTime(now.minusMonths(1));
    cycleStrategy.setCycleEndTime(now.plusMonths(1));
    cycleStrategy.setTimeInADay(now.toLocalTime());
    cycleStrategy.setTimeInCycle(
        new String[] {String.format("%02d", now.getDayOfWeek().getValue())});
    Assert.assertEquals(true, cycleStrategy.validate(now, now));

    cycleStrategy.setFrequencyType(FrequencyTypeEnum.HOUR);
    cycleStrategy.setStartTimeInADay(now.toLocalTime().minusHours(12));
    cycleStrategy.setCycleInADay(1);
    Assert.assertEquals(true, cycleStrategy.validate(now, now));

    // 按每2天
    cycleStrategy.setFrequencyType(FrequencyTypeEnum.ONCE);
    cycleStrategy.setCycleInteval(2);
    Assert.assertEquals(false, cycleStrategy.validate(now, now));

    // 按每3天
    cycleStrategy.setFrequencyType(FrequencyTypeEnum.ONCE);
    cycleStrategy.setCycleInteval(3);
    Assert.assertEquals(false, cycleStrategy.validate(now, now));

    // 按每5天
    cycleStrategy.setFrequencyType(FrequencyTypeEnum.ONCE);
    cycleStrategy.setCycleInteval(5);
    Assert.assertEquals(false, cycleStrategy.validate(now, now));

    // 按每8天
    cycleStrategy.setFrequencyType(FrequencyTypeEnum.ONCE);
    cycleStrategy.setCycleInteval(8);
    Assert.assertEquals(false, cycleStrategy.validate(now, now));
  }
}
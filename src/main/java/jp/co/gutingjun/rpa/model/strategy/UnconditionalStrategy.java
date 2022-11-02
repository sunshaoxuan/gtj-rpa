package jp.co.gutingjun.rpa.model.strategy;

import org.apache.commons.lang.StringUtils;

/**
 * 无条件通过策略
 *
 * @author sunsx
 * */
public class UnconditionalStrategy implements IBotStrategy {
  /** 策略名称 */
  private String name;

  @Override
  public String getName() {
    if (StringUtils.isBlank(name)) {
      name = "无条件执行策略";
    }
    return name;
  }

  @Override
  public void setName(String value) {
    name = value;
  }

  @Override
  public boolean validate(Object source, Object refValue) {
    return true;
  }
}
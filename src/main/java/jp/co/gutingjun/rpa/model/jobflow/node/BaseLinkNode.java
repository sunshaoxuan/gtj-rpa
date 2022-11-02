package jp.co.gutingjun.rpa.model.jobflow.node;

import jp.co.gutingjun.rpa.model.jobflow.condition.ICondition;
import jp.co.gutingjun.rpa.model.jobflow.condition.LogicalConditionModel;

import java.util.Map;
import java.util.Optional;

/**
 * 基本连线节点
 *
 * @author sunsx
 */
public class BaseLinkNode extends LinkNodeModel {
  ICondition ruleCondition = null;

  @Override
  public ICondition getRuleCondition() {
    return ruleCondition;
  }

  /**
   * 设置规则条件
   *
   * @param ruleCondition
   */
  public void setRuleCondition(ICondition ruleCondition) {
    this.ruleCondition = ruleCondition;
  }

  /**
   * 根据条件集合，设置规则条件
   *
   * @param conditionMap
   */
  public void setRuleCondition(Map<String, Object> conditionMap) {
    if (conditionMap != null && conditionMap.size() > 0) {
      Optional<Map.Entry<String, Object>> rootCondition =
          conditionMap.entrySet().stream().findFirst();
      LogicalConditionModel condition =
          LogicalConditionModel.getCondition(
              rootCondition.get().getKey(),
              rootCondition.get().getValue(),
              getParent().getActions() == null || getParent().getActions().length == 0
                  ? null
                  : getParent().getActions()[0]);
      setRuleCondition(condition);
    }
  }

  @Override
  public boolean eval() {
    if (getRuleCondition() == null) {
      return true;
    } else {
      return getRuleCondition().getValue();
    }
  }
}

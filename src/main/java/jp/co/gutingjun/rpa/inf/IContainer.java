package jp.co.gutingjun.rpa.inf;

import java.util.Map;

/**
 * 容器
 *
 * @param <T> 父容器类型
 */
public interface IContainer<T extends IContainer> {
  /** 获取当前对象的容器实例 */
  IContainer getContainer();

  /** 获取当前对象所属容器层叠关系中的顶层容器 */
  IContainer getTopContainer();

  /** 获取当前对象的父容器 */
  T getParentContainer();

  /** 获取当前容器的环境设置 */
  Map<String, Object> getContext();
}

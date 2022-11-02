package jp.co.gutingjun.rpa.exception;

/**
 * 非法工作节点异常
 *
 * @author sunsx
 * */
public class InvalidJobNodeException extends Exception {
  @Override
  public String getMessage() {
    return "Invalid job node found:  job node must connect with linker.";
  }
}

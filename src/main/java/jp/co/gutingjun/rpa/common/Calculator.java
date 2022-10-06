package jp.co.gutingjun.rpa.common;

import org.apache.commons.lang.StringUtils;

import java.util.Stack;

public class Calculator {
  public static double calculate(String formula) {
    Stack<String> numStack = new Stack<>();
    Stack<String> oprStack = new Stack<>();

    String numbers = "0123456789.";
    String operators = "+-*/%^&!|";

    byte[] bytes = formula.getBytes();
    String curNum = "";
    String curOpr = "";

    for (int i = 0; i < bytes.length; i++) {
      String cur = new String(new byte[] {bytes[i]});
      if (i == 0) {
        if (operators.contains(cur) && !cur.equals("+") && !cur.equals("-")) {
          throw new RuntimeException("符号出错。");
        }
      }

      if (cur.equals(" ")) {
        // 空格跳过
        continue;
      } else if (numbers.contains(cur)) {
        // 同一个数字多个小数点错误
        if (curNum.contains(".") && cur.equals(".")) {
          throw new RuntimeException("小数点错误。");
        }

        // 当前字符是数字
        curNum += cur;
      } else if (operators.contains(cur)) {
        // 当前字符是操作符
        if (StringUtils.isBlank(curNum)) {
          // 数字为空
          if ((cur.equals("+") || cur.equals("-"))
              && (numStack.size() == 0 || numStack.peek().equals("("))) {
            if (cur.equals("-")) {
              // 负号，加到数字里
              curNum += cur;
            }
            continue;
          }

          if (numStack.size() >= 2 && !numStack.peek().equals("(")) {
            if (cur.equals("+") || cur.equals("-") || curOpr.equals("*") || curOpr.equals("/")) {
              String rightNum = numStack.pop();
              if (!numStack.peek().equals("(")) {
                String leftNum = numStack.pop();
                curNum = eval(leftNum, curOpr, rightNum);
                numStack.push(curNum);
                curNum = "";
              } else {
                numStack.push(rightNum);
                oprStack.push(curOpr);
              }
            } else {
              oprStack.push(curOpr);
            }
          }
          curOpr = cur;

        } else {
          // 数字不空，压栈
          numStack.push(curNum);
          curNum = "";
          if (StringUtils.isBlank(curOpr)) {
            // 操作符为空
            curOpr = cur;
            continue;
          } else {
            // 操作符不为空
            if ((cur.equals("+") || cur.equals("-"))
                || (!curOpr.equals("+") && !curOpr.equals("-"))) {
              // 当前是+- 或 前一个是非+-
              // 把前一个计算出结果，并压栈
              curNum = numStack.pop();
              String lastNum = numStack.pop();
              curNum = eval(lastNum, curOpr, curNum);
              numStack.push(curNum);
              curNum = "";
              curOpr = cur;
            } else {
              // 上一个是+-。当前非+-，数字和前一个符号压栈
              if (!StringUtils.isBlank(curNum)) {
                numStack.push(curNum);
                curNum = "";
              }
              oprStack.push(curOpr);
              curOpr = cur;
            }
          }
        }
      } else if (cur.equals("(")) {
        // 当前是左括号，所有数字符号不空的都压栈
        if (!StringUtils.isBlank(curNum)) {
          if (StringUtils.isBlank(curOpr)) {
            // 数字不为空，操作符为空，报错，数字不能与括号相邻
            throw new RuntimeException("计算式错误。");
          }
          numStack.push(curNum);
          curNum = "";
        }
        if (!StringUtils.isBlank(curOpr)) {
          oprStack.push(curOpr);
          curOpr = "";
        }
        numStack.push("(");
      } else if (cur.equals(")")) {
        // 当前是右括号，出栈所有数字和符号并计算，直到出现左括号
        if (StringUtils.isBlank(curNum) || (!curNum.contains("-") && StringUtils.isBlank(curOpr))) {
          // 当前数字为空，直接遇到操作符，错误
          curNum = numStack.pop();
        }

        while (numStack.size() > 0 && numStack.peek() != "(") {
          if (StringUtils.isBlank(curOpr)) {
            curOpr = oprStack.pop();
          }
          String lastNum = numStack.pop();
          curNum = eval(lastNum, curOpr, curNum);
          curOpr = "";
        }
        // 左括号出栈，结果压栈
        numStack.pop();
        numStack.push(curNum);
        curNum = "";
        if ((numStack.size() == 0 || !numStack.peek().equals("(")) && oprStack.size() > 0) {
          curOpr = oprStack.pop();
        }
      }
    }

    while (numStack.size() > 0) {
      String rightNum = "";
      String leftNum = "";
      String opr = "";

      if (!StringUtils.isBlank(curNum)) {
        if ((curOpr.equals("+") || curOpr.equals("-"))) {
          if (numStack.size() > 1 && oprStack.size() > 0) {
            rightNum = numStack.pop();
            leftNum = numStack.pop();
            opr = oprStack.pop();
            numStack.push(eval(leftNum, opr, rightNum));
          } else if (numStack.size() == 1 && oprStack.size() == 0) {
            rightNum = curNum;
            leftNum = numStack.pop();
            curNum = eval(leftNum, curOpr, rightNum);
            curOpr = "";
          }
        } else {
          rightNum = curNum;
          leftNum = numStack.pop();
          curNum = eval(leftNum, curOpr, rightNum);
          curOpr = oprStack.size() > 0 ? oprStack.pop() : "";
        }
      } else {
        if (numStack.size() > 1) {
          rightNum = numStack.pop();
          leftNum = numStack.pop();
          curNum = eval(leftNum, curOpr, rightNum);
          curOpr = oprStack.size() > 0 ? oprStack.pop() : "";
        } else {
          curNum = numStack.pop();
        }
      }
    }

    if (oprStack.size() == 0) {
      if (curOpr.equals("-")) {
        curNum = eval(curNum, "*", "-1");
      } else if (!StringUtils.isBlank(curOpr) && !curOpr.equals("+")) {
        throw new RuntimeException("计算式错误。");
      }
    } else {
      throw new RuntimeException("计算式错误。");
    }

    return Double.parseDouble(curNum);
  }

  private static String eval(String leftNumber, String operator, String rightNumber) {
    if (leftNumber.startsWith(".")) {
      leftNumber = "0" + leftNumber;
    }

    if (rightNumber.startsWith(".")) {
      rightNumber = "0" + rightNumber;
    }

    double val = 0;
    if (operator.equals("+")) {
      val = Double.parseDouble(leftNumber) + Double.parseDouble(rightNumber);
    } else if (operator.equals("-")) {
      val = Double.parseDouble(leftNumber) - Double.parseDouble(rightNumber);
    } else if (operator.equals("*")) {
      val = Double.parseDouble(leftNumber) * Double.parseDouble(rightNumber);
    } else if (operator.equals("/")) {
      if (Double.parseDouble(rightNumber) == 0) {
        throw new RuntimeException("除以0错误。");
      }
      val = Double.parseDouble(leftNumber) / Double.parseDouble(rightNumber);
    } else if (operator.equals("%")) {
      val = Double.parseDouble(leftNumber) % Double.parseDouble(rightNumber);
    } else if (operator.equals("^")) {
      val = Math.pow(Double.parseDouble(leftNumber), Double.parseDouble(rightNumber));
    } else {
      throw new RuntimeException("计算出错：不支持的计算。");
    }
    return String.valueOf(val);
  }
}
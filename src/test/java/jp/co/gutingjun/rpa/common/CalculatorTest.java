package jp.co.gutingjun.rpa.common;

import org.junit.Test;

public class CalculatorTest {
  @Test
  public void calculatePlus() {
    Double rst = Calculator.calculate("1+1");
    org.junit.jupiter.api.Assertions.assertEquals(2, rst);
    rst = Calculator.calculate("1+1 +1");
    org.junit.jupiter.api.Assertions.assertEquals(3, rst);
    rst = Calculator.calculate("1+2+3+4");
    org.junit.jupiter.api.Assertions.assertEquals(10, rst);
    rst = Calculator.calculate("1+2+3+(4+5)");
    org.junit.jupiter.api.Assertions.assertEquals(15, rst);
    rst = Calculator.calculate("1+2+(3+4)+5");
    org.junit.jupiter.api.Assertions.assertEquals(15, rst);
    rst = Calculator.calculate("1+(2+3)+4+5");
    org.junit.jupiter.api.Assertions.assertEquals(15, rst);
    rst = Calculator.calculate("1+(2+3+4)+5");
    org.junit.jupiter.api.Assertions.assertEquals(15, rst);
    rst = Calculator.calculate("1+(2+(3+4)+5)");
    org.junit.jupiter.api.Assertions.assertEquals(15, rst);
    rst = Calculator.calculate("(1+2+3+4+5)");
    org.junit.jupiter.api.Assertions.assertEquals(15, rst);
    rst = Math.round(Calculator.calculate(".1+.2") * 100D)/100D;
    org.junit.jupiter.api.Assertions.assertEquals(0.3, rst);
      rst = Math.round(Calculator.calculate("+1+.2") * 100D)/100D;
      org.junit.jupiter.api.Assertions.assertEquals(1.2, rst);
  }

  @Test
  public void calculateMinus() {
    Double rst = Calculator.calculate("1-1");
    org.junit.jupiter.api.Assertions.assertEquals(0, rst);
    rst = Calculator.calculate("+1-1-1");
    org.junit.jupiter.api.Assertions.assertEquals(-1, rst);
    rst = Calculator.calculate("5-3-2");
    org.junit.jupiter.api.Assertions.assertEquals(0, rst);
    rst = Calculator.calculate("5-(3-2)");
    org.junit.jupiter.api.Assertions.assertEquals(4, rst);
    rst = Calculator.calculate("10-5-(3-2)");
    org.junit.jupiter.api.Assertions.assertEquals(4, rst);
    rst = Calculator.calculate("10-(5-3)-2");
    org.junit.jupiter.api.Assertions.assertEquals(6, rst);
    rst = Calculator.calculate("10-(5-3-2)");
    org.junit.jupiter.api.Assertions.assertEquals(10, rst);
    rst = Calculator.calculate("(10-5)-3- 2");
    org.junit.jupiter.api.Assertions.assertEquals(0, rst);
    rst = Calculator.calculate("(10-5-3-2)");
    org.junit.jupiter.api.Assertions.assertEquals(0, rst);
      rst = Math.round(Calculator.calculate("-1+.2") * 100D)/100D;
      org.junit.jupiter.api.Assertions.assertEquals(-0.8, rst);
  }

  @Test
  public void calculatePlusMinus() {
    Double rst = Calculator.calculate("1-1+1");
    org.junit.jupiter.api.Assertions.assertEquals(1, rst);
    rst = Calculator.calculate("1+1-1");
    org.junit.jupiter.api.Assertions.assertEquals(1, rst);
    rst = Calculator.calculate("5-3+2");
    org.junit.jupiter.api.Assertions.assertEquals(4, rst);
    rst = Calculator.calculate("5-(3+2)");
    org.junit.jupiter.api.Assertions.assertEquals(0, rst);
    rst = Calculator.calculate("10-(5+(3-2))");
    org.junit.jupiter.api.Assertions.assertEquals(4, rst);
    rst = Calculator.calculate("10-(5+ 3)-2");
    org.junit.jupiter.api.Assertions.assertEquals(0, rst);
    rst = Calculator.calculate("10-(5+3-2)");
    org.junit.jupiter.api.Assertions.assertEquals(4, rst);
    rst = Calculator.calculate("(10-5)+(3-2)");
    org.junit.jupiter.api.Assertions.assertEquals(6, rst);
    rst = Calculator.calculate("10-((5-3)-2)");
    org.junit.jupiter.api.Assertions.assertEquals(10, rst);
  }

  @Test
  public void calculateMultiply() {
    Double rst = Calculator.calculate("1*1");
    org.junit.jupiter.api.Assertions.assertEquals(1, rst);
    rst = Calculator.calculate("1*1*2");
    org.junit.jupiter.api.Assertions.assertEquals(2, rst);
    rst = Calculator.calculate("5*3*2");
    org.junit.jupiter.api.Assertions.assertEquals(30, rst);
    rst = Calculator.calculate("5*(3*2)");
    org.junit.jupiter.api.Assertions.assertEquals(30, rst);
    rst = Calculator.calculate("10*( 5*(3*2))");
    org.junit.jupiter.api.Assertions.assertEquals(300, rst);
    rst = Calculator.calculate("10*(5*3)*2");
    org.junit.jupiter.api.Assertions.assertEquals(300, rst);
    rst = Calculator.calculate("10* (5*3*2)");
    org.junit.jupiter.api.Assertions.assertEquals(300, rst);
    rst = Calculator.calculate("(10*5)*(3*2)");
    org.junit.jupiter.api.Assertions.assertEquals(300, rst);
    rst = Calculator.calculate("10*((5*3)*2)");
    org.junit.jupiter.api.Assertions.assertEquals(300, rst);
  }

  @Test
  public void calculateDivide() {
    Double rst = Calculator.calculate("1/1");
    org.junit.jupiter.api.Assertions.assertEquals(1, rst);
    rst = Calculator.calculate("1/1/2");
    org.junit.jupiter.api.Assertions.assertEquals(0.5, rst);
    rst = Calculator.calculate("5/2/2");
    org.junit.jupiter.api.Assertions.assertEquals(1.25, rst);
    rst = Calculator.calculate("5/(2/2)");
    org.junit.jupiter.api.Assertions.assertEquals(5, rst);
    rst = Calculator.calculate("10/( 5/(2/ 2))");
    org.junit.jupiter.api.Assertions.assertEquals(2, rst);
    rst = Calculator.calculate("10/(5/2)/2");
    org.junit.jupiter.api.Assertions.assertEquals(2, rst);
    rst = Calculator.calculate("10/(5/2/2)");
    org.junit.jupiter.api.Assertions.assertEquals(8, rst);
    rst = Calculator.calculate("(10/5)/(2/2)");
    org.junit.jupiter.api.Assertions.assertEquals(2, rst);
    rst = Calculator.calculate("10/((5/2)/2)");
    org.junit.jupiter.api.Assertions.assertEquals(8, rst);
  }

  @Test
  public void calculateMultiplyDivide() {
    Double rst = Calculator.calculate("1/1*1");
    org.junit.jupiter.api.Assertions.assertEquals(1, rst);
    rst = Calculator.calculate("1/1*2");
    org.junit.jupiter.api.Assertions.assertEquals(2, rst);
    rst = Calculator.calculate("5*2/2");
    org.junit.jupiter.api.Assertions.assertEquals(5, rst);
    rst = Calculator.calculate("5*(2/2)");
    org.junit.jupiter.api.Assertions.assertEquals(5, rst);
    rst = Calculator.calculate("10/(5*(2/2))");
    org.junit.jupiter.api.Assertions.assertEquals(2, rst);
    rst = Calculator.calculate("10/(5*2)/2");
    org.junit.jupiter.api.Assertions.assertEquals(0.5, rst);
    rst = Calculator.calculate("10/(5*2/2)");
    org.junit.jupiter.api.Assertions.assertEquals(2, rst);
    rst = Calculator.calculate("(10*5)/(2*2)");
    org.junit.jupiter.api.Assertions.assertEquals(12.5, rst);
    rst = Calculator.calculate("10/((5*2)*2)");
    org.junit.jupiter.api.Assertions.assertEquals(0.5, rst);
  }

  @Test
  public void calculatePlusMinusMultiplyDivide() {
    Double rst = Calculator.calculate("1+1*1");
    org.junit.jupiter.api.Assertions.assertEquals(2, rst);
    rst = Calculator.calculate("1+1*2");
    org.junit.jupiter.api.Assertions.assertEquals(3, rst);
    rst = Calculator.calculate("5-2*2");
    org.junit.jupiter.api.Assertions.assertEquals(1, rst);
    rst = Calculator.calculate("5+(2/2)");
    org.junit.jupiter.api.Assertions.assertEquals(6, rst);
    rst = Calculator.calculate("10*(5-(2/2))");
    org.junit.jupiter.api.Assertions.assertEquals(40, rst);
    rst = Calculator.calculate("10*(5*2)+2");
    org.junit.jupiter.api.Assertions.assertEquals(102, rst);
    rst = Calculator.calculate("10/(5-2/2)");
    org.junit.jupiter.api.Assertions.assertEquals(2.5, rst);
    rst = Calculator.calculate("(10+5)/(2+2)");
    org.junit.jupiter.api.Assertions.assertEquals(3.75, rst);
    rst = Calculator.calculate("10-((5+2)*2)");
    org.junit.jupiter.api.Assertions.assertEquals(-4, rst);
  }

  @Test
  public void calculateSpecial() {
    Double rst = Calculator.calculate("1+(-1)");
    org.junit.jupiter.api.Assertions.assertEquals(0, rst);
    rst = Calculator.calculate("1+1*(-2)");
    org.junit.jupiter.api.Assertions.assertEquals(-1, rst);
    rst = Calculator.calculate("-5-2*2");
    org.junit.jupiter.api.Assertions.assertEquals(-9, rst);
    rst = Calculator.calculate("5+(-2*2)");
    org.junit.jupiter.api.Assertions.assertEquals(1, rst);
  }

  @Test
  public void calculateOther() {
    Double rst = Calculator.calculate("1%1");
    org.junit.jupiter.api.Assertions.assertEquals(0, rst);
    rst = Calculator.calculate("2*(3%2)");
    org.junit.jupiter.api.Assertions.assertEquals(2, rst);
    rst = Calculator.calculate("2^2");
    org.junit.jupiter.api.Assertions.assertEquals(4, rst);
    rst = Calculator.calculate("10^2");
    org.junit.jupiter.api.Assertions.assertEquals(100, rst);
  }

  @Test
  public void exceptionDevideZero() {
    try {
      Calculator.calculate("1/0");
    } catch (Exception ex) {
      org.junit.jupiter.api.Assertions.assertEquals(ex.getMessage(), "除以0错误。");
    }
  }

  @Test
  public void exceptionNotSupportOperator() {
    try {
      Calculator.calculate("1&2");
    } catch (Exception ex) {
      org.junit.jupiter.api.Assertions.assertEquals(ex.getMessage(), "计算出错：不支持的计算。");
    }

      try {
          Calculator.calculate("&2");
      } catch (Exception ex) {
          org.junit.jupiter.api.Assertions.assertEquals(ex.getMessage(), "符号出错。");
      }

      try {
          Calculator.calculate("(1+1)2");
      } catch (Exception ex) {
          org.junit.jupiter.api.Assertions.assertEquals(ex.getMessage(), "计算出错：不支持的计算。");
      }
  }

  @Test
  public void exceptionDocs(){
      try {
          Calculator.calculate("1..2+2");
      } catch (Exception ex) {
          org.junit.jupiter.api.Assertions.assertEquals(ex.getMessage(), "小数点错误。");
      }
  }
}
package jp.co.gutingjun.rpa.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/** 订单查询数据传输对象 */
@Data
@NoArgsConstructor
public class OrderRangeDTO {
  private String beginDate;
  private String endDate;
}

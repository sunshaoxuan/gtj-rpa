package jp.co.gutingjun.rpa.rest;

import lombok.Data;
import lombok.NoArgsConstructor;

/** 房间状态数据传输对象 */
@Data
@NoArgsConstructor
public class RoomStatusDTO {
  private Long hotelId;
  private Long roomTypeId;
  private String beginDate;
  private String endDate;
  private Integer status;
  private Long roomId;
}

package jp.co.gutingjun.rpa.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/** 酒店查询信息数据传输对象 */
@Data
@NoArgsConstructor
public class HotelInfoDTO {
  private Long hotelId;
  private Map<String, String> valueMap;
}

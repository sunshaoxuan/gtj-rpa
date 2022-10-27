package jp.co.gutingjun.rpa.model.log;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LogData {
  private Long botId;
  private Long botInstanceId;
  private String nodeName;
  private String actionName;
  private String name;
  private LocalDateTime logTime;
  private String message;
  private String level;
}

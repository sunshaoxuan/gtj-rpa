package jp.co.gutingjun.rpa.config;

import jp.co.gutingjun.common.util.AesEncryptUtils;
import jp.co.gutingjun.common.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 机器人平台基础配置
 *
 * @author sunsx
 */
@Slf4j
public class BotConfig {
  /** TOKEN过期小时数 */
  public static final int TK_EXPIRE_HOURS = 72;
  /** TOKEN公钥 */
  public static final String TK_PUB_KEY = "59xv55/nOpFko/zOzAuF3h/hucmyrXyk";

  public static final String US_PUB_KEY = "zWeZaEn6vOAuuH90NYxjUe6U5xGWDGyD";
  /** 运行池大小 */
  private static final int executionPoolSize = 10;
  /** 设置数据文件存放路径 */
  private static final String configFilePath = "..//config//";
  /** 用户设置文件名 */
  private static final String userConfigFile = "userconfig.cfg";

  public static int getExecutionPoolSize() {
    return executionPoolSize;
  }

  /**
   * 取用户设置文件路径
   *
   * @return
   */
  public static String getUserConfigFile() {
    return configFilePath.concat(userConfigFile);
  }

  /**
   * 从用户设置文件中读取用户信息
   *
   * @return
   */
  public static HashMap getUsers() {
    HashMap userData = null;

    try {
      if (Files.exists(Paths.get(getUserConfigFile()))) {
        byte[] loadedData = Files.readAllBytes(Paths.get(getUserConfigFile()));

        if (loadedData != null && loadedData.length > 0) {
          userData =
              (HashMap)
                  SerializationUtils.deserialize(
                      Base64.getDecoder()
                          .decode(
                              AesEncryptUtils.decrypt(new String(loadedData), US_PUB_KEY)
                                  .getBytes()));
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }

    return userData;
  }

  /**
   * 将用户信息存储至用户设置文件中
   *
   * @param userData
   */
  public static void saveUsers(HashMap userData) {
    byte[] saveData = SerializationUtils.serialize(userData);
    try {
      if (!Files.exists(Paths.get(configFilePath))) {
        Files.createDirectory(Paths.get(configFilePath));
      }

      Path path = null;
      if (!Files.exists(Paths.get(getUserConfigFile()))) {
        path = Files.createFile(Paths.get(getUserConfigFile()));
      } else {
        path = Paths.get(getUserConfigFile());
      }

      // log
      List<Map<String, Object>> userObj = new ArrayList<Map<String, Object>>();
      log.info(JsonUtils.map2JSON(userObj));

      // wrhite users data to file
      Files.write(
          path,
          AesEncryptUtils.encrypt(Base64.getEncoder().encodeToString(saveData), US_PUB_KEY)
              .getBytes());
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public static String toHexString(byte[] byteArray) {
    if (byteArray == null || byteArray.length < 1)
      throw new IllegalArgumentException("this byteArray must not be null or empty");

    final StringBuilder hexString = new StringBuilder();
    for (int i = 0; i < byteArray.length; i++) {
      if ((byteArray[i] & 0xff) < 0x10) // 0~F前面不零
      hexString.append("0");
      hexString.append(Integer.toHexString(0xFF & byteArray[i]));
    }
    return hexString.toString().toLowerCase();
  }

  public static byte[] toByteArray(String hexString) {
    if (StringUtils.isEmpty(hexString))
      throw new IllegalArgumentException("this hexString must not be empty");

    hexString = hexString.toLowerCase();
    final byte[] byteArray = new byte[hexString.length() / 2];
    int k = 0;
    for (int i = 0; i < byteArray.length; i++) { // 因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
      byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
      byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
      byteArray[i] = (byte) (high << 4 | low);
      k += 2;
    }
    return byteArray;
  }

  public static void loadDefaultBots(){

  }
}

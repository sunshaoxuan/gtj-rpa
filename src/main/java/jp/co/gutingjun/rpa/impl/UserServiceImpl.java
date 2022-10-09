package jp.co.gutingjun.rpa.impl;

import jp.co.gutingjun.common.util.AesEncryptUtils;
import jp.co.gutingjun.rpa.common.CommonUtils;
import jp.co.gutingjun.rpa.config.BotConfig;
import jp.co.gutingjun.rpa.inf.IUserService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.eclipse.jetty.util.security.Credential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {
  @Autowired JavaMailSender mailSender;
  @Autowired RedisService redisService;

  @Override
  public void createUser(String username, String email, String password, boolean isAdmin)
      throws RuntimeException {
    HashMap userMap = BotConfig.getUsers();

    if (userMap == null) {
      userMap = new HashMap();
    }

    if (userMap.containsKey(username)) {
      throw new RuntimeException("User already exists.");
    } else {
      if (StringUtils.isBlank(password)) {
        throw new RuntimeException("password can not be blank.");
      } else {
        if (StringUtils.isBlank(email)) {
          throw new RuntimeException("Email can not be blank.");
        }
      }
    }

    userMap.put(username, new HashMap<>());
    ((HashMap) userMap.get(username)).put("id", CommonUtils.getNextID());
    ((HashMap) userMap.get(username)).put("password", password);
    ((HashMap) userMap.get(username)).put("isadmin", isAdmin);
    ((HashMap) userMap.get(username)).put("removed", false);
    resetUserEmail(username, email, userMap);
    BotConfig.saveUsers(userMap);

    sendVerifyEmail(username, email, (String) ((HashMap) userMap.get(username)).get("verifycode"));
  }

  @Override
  public Long getUserId(String username) {
    Long id = -1L;
    if (username.equalsIgnoreCase("ROOT")) {
      id = 99999L;
    } else {
      HashMap userMap = BotConfig.getUsers();
      id = userMap.containsKey(username) ? (Long) ((HashMap) userMap.get(username)).get("id") : -1L;
    }

    return id;
  }

  private void sendVerifyEmail(String username, String email, String verifyCode) {
    try {
      sendEmail(
          email,
          "GTJ-RPA Email Verify",
          "Your verify code is: "
              + verifyCode
              + "\n\nThis email was sent by Gutingjun RPA verify process,  "
              + "please DONOT tell anyone the verify code to avoid security problems.");
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  private void sendEmail(String to, String subject, String text)
      throws MessagingException, UnsupportedEncodingException {
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
    helper.setFrom("jesse.ni@51fanxing.co.jp", "谷町君");
    helper.setTo(to);
    helper.setSubject(subject);
    helper.setText(text);
    mailSender.send(mimeMessage);
  }

  @Override
  public void removeUser(String username) {
    HashMap userMap = BotConfig.getUsers();
    if (userMap != null && userMap.containsKey(username)) {
      ((HashMap) userMap.get(username)).put("removed", true);
      BotConfig.saveUsers(userMap);
    } else {
      throw new RuntimeException("Modify failed: User not found.");
    }
  }

  @Override
  public void modifiyUser(String username, String email, String password) {
    HashMap userMap = BotConfig.getUsers();
    boolean isModified = false;
    if (userMap != null && userMap.containsKey(username)) {
      if (!StringUtils.isBlank(password)) {
        ((HashMap) userMap.get(username)).put("password", password);
        isModified = true;
      }

      if (!StringUtils.isBlank(email)) {
        if (!email.equals(userMap.get(username))) {
          resetUserEmail(username, email, userMap);
          isModified = true;
          sendVerifyEmail(
              username, email, (String) ((HashMap) userMap.get(username)).get("verifycode"));
        }
      }

      if (isModified) {
        BotConfig.saveUsers(userMap);
      }
    } else {
      throw new RuntimeException("Modify failed: User not found.");
    }
  }

  private void resetUserEmail(String username, String email, HashMap userMap) {
    ((HashMap) userMap.get(username)).put("email", email);
    ((HashMap) userMap.get(username)).put("verified", false);
    ((HashMap) userMap.get(username))
        .put("verifycode", String.valueOf(RandomUtils.nextInt(100000, 999999)));
  }

  @Override
  public void verityEmail(String username, String verifyCode) {
    HashMap userMap = BotConfig.getUsers();
    if (userMap != null && userMap.containsKey(username)) {
      if (verifyCode.equals(((HashMap) userMap.get(username)).get("verifycode"))) {
        ((HashMap) userMap.get(username)).put("verified", true);
        ((HashMap) userMap.get(username)).put("verifycode", null);
        BotConfig.saveUsers(userMap);
      } else {
        throw new RuntimeException("Verify failed: Verify code not matched.");
      }
    } else {
      throw new RuntimeException("Modify failed: User not found.");
    }
  }

  @Override
  public String login(String username, String password) {
    String pwd = Credential.MD5.digest("123qwe");

    if (pwd.startsWith("MD5:")) {
      pwd = pwd.replace("MD5:", "");
    }

    if (password.startsWith("MD5:")) {
      password = password.replace("MD5:", "");
    }

    String token = "";
    try {
      if (username.equalsIgnoreCase("ROOT") && password.equals(pwd)) {
        token = getToken(username, password);
      } else {
        HashMap userData = BotConfig.getUsers();
        if (userData != null) {
          if (userData.containsKey(username)) {
            if ((boolean) ((HashMap) userData.get(username)).get("removed")) {
              throw new RuntimeException("Login faild: User not found.");
            } else if (!(boolean) ((HashMap) userData.get(username)).get("verified")) {
              throw new RuntimeException("User has not been activated.");
            } else if (password.equals(((HashMap) userData.get(username)).get("password"))) {
              token = getToken(username, password);
            } else {
              throw new RuntimeException("Login faild: User/Password not matched.");
            }
          } else {
            throw new RuntimeException("Login faild: User not found.");
          }
        } else {
          throw new RuntimeException("Login faild: User setting not found.");
        }
      }

      redisService.set(token, username);
      return token;
    } catch (Exception e) {
      return e.getMessage();
    }
  }

  @Override
  public void logout(String token) {
    redisService.delete(token);
  }

  private String getToken(String username, String password) throws Exception {
    StringBuilder seed = new StringBuilder();
    seed.append(
        UUID.randomUUID()
            + username
            + UUID.randomUUID()
            + password
            + UUID.randomUUID()
            + LocalDateTime.now());
    return new String(
        AesEncryptUtils.encrypt(seed.reverse().toString(), BotConfig.TK_PUB_KEY).getBytes());
  }
}

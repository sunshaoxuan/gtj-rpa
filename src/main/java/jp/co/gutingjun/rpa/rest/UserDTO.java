package jp.co.gutingjun.rpa.rest;

import lombok.Data;

/**
 * 用户数据
 *
 * @author sunsx
 */
@Data
public class UserDTO {
    private String username;
    private String password;
    private String email;
    private boolean admin;
    private String verifycode;
}

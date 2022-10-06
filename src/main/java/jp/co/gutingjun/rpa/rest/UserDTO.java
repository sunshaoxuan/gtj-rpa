package jp.co.gutingjun.rpa.rest;

import lombok.Data;

@Data
public class UserDTO {
    private String username;
    private String password;
    private String email;
    private boolean admin;
    private String verifycode;
}

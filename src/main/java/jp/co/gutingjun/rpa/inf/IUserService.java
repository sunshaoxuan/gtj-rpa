package jp.co.gutingjun.rpa.inf;

public interface IUserService {
    /**
     * 创建用户
     * @param username 用户名
     * @param email 电子邮件
     * @param password 密码
     * @param isAdmin 是否管理员
     */
    void createUser(String username, String email, String password, boolean isAdmin) throws RuntimeException;

    /**
     * 删除用户（不实际删除，只禁用）
     * @param username 用户名
     */
    void removeUser(String username);

    /**
     * 修改用户信息
     * @param username 用户名
     * @param email 电子邮件
     * @param password 密码
     */
    void modifiyUser(String username, String email, String password);

    /**
     * 验证邮箱
     *
     * @param username  用户名
     * @param verifyCode 邮箱验证码
     */
    void verityEmail(String username, String verifyCode);

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return Token
     */
    String login(String username, String password);

    /**
     * 按照token登出
     *
     * @param token
     * @return
     */
    void logout(String token);
}

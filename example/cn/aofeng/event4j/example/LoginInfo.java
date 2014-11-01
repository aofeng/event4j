package cn.aofeng.event4j.example;

/**
 * 用户登陆信息。
 * 
 * @author <a href="mailto:aofengblog@163.com">聂勇</a>
 */
public class LoginInfo {
    
    private long loginTime;   // 登陆时间，单位：毫秒
    
    private String ip;   // 用户登陆的来源IP
    
    private String userName;   // 用户登陆使用的账号名
    
    private int resultCode;   // 登陆结果。1表示成功，其他表示失败

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

}

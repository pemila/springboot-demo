package com.pemila.boot.aop.model;

/**
 * @author 月在未央
 * @date 2019/6/11 10:29
 */
public class SysLog {
    private int id;
    private String userName;
    private String operation;
    private Long time;
    private String method;
    private String param;
    private String ip;
    private Long createTime;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getOperation() {
        return operation;
    }
    public void setOperation(String operation) {
        this.operation = operation;
    }
    public Long getTime() {
        return time;
    }
    public void setTime(Long time) {
        this.time = time;
    }
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}

package com.pemila.boot.cache.redis.model;

import java.io.Serializable;

/**
 * @author 月在未央
 * @date 2019/6/10 15:23
 */
public class Student implements Serializable {
    private String no;
    private String name;
    private String sex;

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}


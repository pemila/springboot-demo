package com.pemila.boot.aop.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 月在未央
 * @date 2019/6/11 11:26
 */
public class HttpContextUtil {

    public static HttpServletRequest getHttpServletRequest(){
        return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
    }
}

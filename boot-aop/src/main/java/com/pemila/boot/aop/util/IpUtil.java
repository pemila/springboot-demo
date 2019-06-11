package com.pemila.boot.aop.util;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 月在未央
 * @date 2019/6/11 11:30
 */
public class IpUtil {


    private static final String COMMA = ",";
    private static final String UNKNOWN = "unknown";

    private static final String[] HEAD_PARAMS = {"x-forwarded-for","Proxy-Client-IP",
            "WL-Proxy-Client-IP","HTTP_CLIENT_IP","HTTP_X_FORWARDED_FOR","X-Real-IP"};

    /**
     * 获取用户真实IP地址，
     * 不直接使用request.getRemoteAddr()的原因是有可能用户使用了代理软件方式避免真实IP地址,
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值
     * @return ip
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = null;
        for (String headParam : HEAD_PARAMS) {
            ip = request.getHeader(headParam);
            if (ip != null && ip.length() != 0 && !UNKNOWN.equalsIgnoreCase(ip)) {
                // 多次反向代理后会有多个ip值，第一个ip才是真实ip
                if (ip.contains(COMMA)) {
                    ip = ip.split(COMMA)[0];
                }
                break;
            }
        }
        return ip == null||ip.isEmpty()?request.getRemoteAddr():ip;
    }




}

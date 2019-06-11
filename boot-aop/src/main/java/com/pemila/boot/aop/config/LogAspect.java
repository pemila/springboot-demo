package com.pemila.boot.aop.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pemila.boot.aop.dao.SysLogDao;
import com.pemila.boot.aop.model.SysLog;
import com.pemila.boot.aop.util.HttpContextUtil;
import com.pemila.boot.aop.util.IpUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 月在未央
 * @date 2019/6/11 10:46
 */
@Component
@Aspect
public class LogAspect {

    @Autowired
    private SysLogDao sysLogDao;

    /** 定义切入点为 @Log 注解*/
    @Pointcut("@annotation(com.pemila.boot.aop.config.Log)")
    public void pointcut(){}

    /** 切入方式为 环绕*/
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint){
        Object result = null;
        long beginTime = System.currentTimeMillis();
        try {
            // 执行方法
            result = joinPoint.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        // 执行耗时
        long time = System.currentTimeMillis()-beginTime;
        // 保存日志
        saveLog(joinPoint,time);
        return result;
    }

    private void saveLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SysLog sysLog = new SysLog();
        Log logAnnotation = method.getAnnotation(Log.class);
        if(logAnnotation!=null){
            // 获取注解上的描述
            sysLog.setOperation(logAnnotation.value());
        }
        // 请求的类名和方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        sysLog.setMethod(className+"."+methodName);
        // 请求参数
        Object[] args = joinPoint.getArgs();
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        String[] paramNames = u.getParameterNames(method);
        if(args != null){
            Map<String,Object> paramMap = new HashMap<>();
            for(int i=0;i<args.length;i++){
                paramMap.put(paramNames[i],args[i]);
            }
            sysLog.setParam(objectToJson(paramMap));
        }
        // 获取ip地址
        HttpServletRequest request = HttpContextUtil.getHttpServletRequest();
        sysLog.setIp(IpUtil.getIpAddr(request));
        // 填充其他参数
        sysLog.setTime(time);
        sysLog.setUserName("pemila");
        sysLog.setCreateTime(System.currentTimeMillis());
        sysLogDao.saveSysLog(sysLog);
    }

    private String objectToJson(Object o){
        ObjectMapper mapper = new ObjectMapper();
        String res = null;
        try {
            res = mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return res;
    }
}

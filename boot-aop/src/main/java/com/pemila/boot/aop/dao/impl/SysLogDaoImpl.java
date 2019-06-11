package com.pemila.boot.aop.dao.impl;

import com.pemila.boot.aop.dao.SysLogDao;
import com.pemila.boot.aop.model.SysLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author 月在未央
 * @date 2019/6/11 10:33
 */
@Repository("sysLogDao")
public class SysLogDaoImpl implements SysLogDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void saveSysLog(SysLog log) {
        String sql = "insert into sys_log (user_name,operation,time,method,params,ip,create_time) " +
                "values(:userName,:operation,:time,:method,:param,:ip,:createTime)";
        NamedParameterJdbcTemplate  namedTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        namedTemplate.update(sql,new BeanPropertySqlParameterSource(log));
    }
}

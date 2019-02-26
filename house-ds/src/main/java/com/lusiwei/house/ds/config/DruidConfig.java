package com.lusiwei.house.ds.config;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.google.common.collect.Lists;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DruidConfig {
    @ConfigurationProperties(prefix = "spring.druid")
    @Bean(initMethod = "init",destroyMethod = "close")
    public DruidDataSource datasource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setProxyFilters(Lists.newArrayList(statFilter()));
        return druidDataSource;
    }
    @Bean
    public Filter statFilter(){
        StatFilter filter=new StatFilter();
        filter.setLogSlowSql(true);
        filter.setSlowSqlMillis(5000);
        filter.setMergeSql(true);
        filter.setDbType("mysql");
        return filter;
    }
    @Bean
    public ServletRegistrationBean<StatViewServlet> servletRegistrationBean(){
        return new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
    }
}

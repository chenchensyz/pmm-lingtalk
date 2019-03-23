package cn.com.cybertech.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
//@EnableConfigurationProperties(DataSourceProperties.class)
@PropertySource(value = {"classpath:/resources/config/db.properties"}, encoding = "utf-8", ignoreResourceNotFound = true)
public class DataSourceAutoConfiguration {

    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {
        //可以在此处调用相关接口获取数据库的配置信息进行 DataSource 的配置
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(env.getProperty("pmmanage.db.url"));
        dataSource.setUsername(env.getProperty("pmmanage.db.username"));
//        dataSource.setPassword(env.getProperty("pmmanage.db.password"));
        dataSource.setPassword("cyber");
        dataSource.setDriverClassName(env.getProperty("pmmanage.db.driverClassName"));
        dataSource.setMaxActive(Integer.valueOf(env.getProperty("pmmanage.db.maxPoolSize")));
        dataSource.setMinIdle(Integer.valueOf(env.getProperty("pmmanage.db.minPoolSize")));
        dataSource.setValidationQuery(env.getProperty("pmmanage.db.testQuery"));
        dataSource.setDbType("com.alibaba.druid.pool.DruidDataSource");
        return dataSource;
    }

}

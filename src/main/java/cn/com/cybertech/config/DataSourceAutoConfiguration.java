package cn.com.cybertech.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;

@Configuration
@EnableConfigurationProperties(DataSourceProperties.class)
//@PropertySource(value = {"classpath:/resources/config/db.properties"}, encoding = "utf-8", ignoreResourceNotFound = true)
public class DataSourceAutoConfiguration {

    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {
        //可以在此处调用相关接口获取数据库的配置信息进行 DataSource 的配置
        DruidDataSource dataSource = new DruidDataSource();
        try {
            dataSource.setUrl(env.getProperty("spring.datasource.url"));
            dataSource.setUsername(env.getProperty("spring.datasource.username"));
            String password = env.getProperty("spring.datasource.password");
            dataSource.setPassword(new String(Base64.decodeBase64(password), "UTF-8"));
            dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
            dataSource.setMaxWait(Integer.valueOf(env.getProperty("spring.datasource.max-wait")));
            dataSource.setMaxActive(Integer.valueOf(env.getProperty("spring.datasource.max-idle")));
            dataSource.setMinIdle(Integer.valueOf(env.getProperty("spring.datasource.min-idle")));
            dataSource.setDbType(env.getProperty("spring.datasource.type"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return dataSource;
    }
}

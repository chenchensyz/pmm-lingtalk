package cn.com.cybertech.config.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 类名：RedisConfig
 */
@Configuration
//@PropertySource(value = {"classpath:/resources/config/redis.properties"}, encoding = "utf-8", ignoreResourceNotFound = true)
public class RedisConfig {
    Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Autowired
    private Environment env;

    @Bean
    public JedisPool redisPoolFactory() {
        String host = env.getProperty("spring.redis.host");
        int port = Integer.valueOf(env.getProperty("spring.redis.port"));
        int timeout = Integer.valueOf(env.getProperty("spring.redis.timeout"));
        logger.info("redis地址：{}:{}", host, port);
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(Integer.valueOf(env.getProperty("spring.redis.jedis.pool.max-idle")));
        jedisPoolConfig.setMaxWaitMillis(Integer.valueOf(env.getProperty("spring.redis.jedis.pool.max-wait")));
        jedisPoolConfig.setMaxTotal(Integer.valueOf(env.getProperty("spring.redis.jedis.pool.max-active")));
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout);
        return jedisPool;
    }

}
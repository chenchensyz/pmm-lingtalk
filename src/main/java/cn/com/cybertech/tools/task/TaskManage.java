package cn.com.cybertech.tools.task;

import cn.com.cybertech.tools.CodeUtil;
import cn.com.cybertech.tools.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

@Component
/**
 * 开启定时任务的注解
 */
@EnableScheduling
public class TaskManage {

    @Autowired
    private JedisPool jedisPool;

    //每天凌晨1点执行一次：0 0 1 * * ?
    @Scheduled(cron = "0 0 1 * * ?")
    public void cleanSession() {
        Jedis jedis = jedisPool.getResource();
        jedis.select(CodeUtil.REDIS_DBINDEX);
        Set<String> keys = jedis.keys(CodeUtil.REDIS_PREFIX + "*");
        try {
            for (String key : keys) {
                try {
                    String timestamp = jedis.hget(key, "timestamp");
                    long currentTime = System.currentTimeMillis();
                    //session只保存1天
                    if (currentTime - Long.valueOf(timestamp) > 1000 * 60 * 60 * 24) {
                        jedis.del(key);
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
            System.out.println("key:" );
        }
    }

    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -3);
        String format = DateUtil.format(calendar.getTime(), DateUtil.YMD_DASH_WITH_TIME);
        System.out.println(calendar.getTime().getTime());
    }
}
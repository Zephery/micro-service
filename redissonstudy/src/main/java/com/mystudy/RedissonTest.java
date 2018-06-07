package com.mystudy;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Zephery
 * Time: 2018/3/16 18:41
 * Description:
 */
public class RedissonTest implements Runnable {
    //logger
    private static final Logger logger = LoggerFactory.getLogger(RedissonTest.class);
    private static RedissonClient redisson;
    private static int count = 10000;

    private static void init() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://119.23.46.71:6340")
                .setPassword("root")
                .setDatabase(10);
        redisson = Redisson.create(config);
    }

    @Override
    public void run() {
        RLock lock = redisson.getLock("anyLock");
        lock.lock();
        lock.tryLock();
        count--;
        System.out.println(count);
        lock.unlock();
    }

    public static void main(String[] args) throws InterruptedException {
        init();
        for (int i = 0; i < 100; i++) {
            new Thread(new RedissonTest()).start();
            TimeUnit.SECONDS.sleep(2);
        }
    }
}
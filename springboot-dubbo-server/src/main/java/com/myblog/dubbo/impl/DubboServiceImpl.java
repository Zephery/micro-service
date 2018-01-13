package com.myblog.dubbo.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.myblog.dubbo.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * @author Zephery
 * @since 2018/1/13 15:58
 */
@Service
public class DubboServiceImpl implements DubboService {
    //logger
    private static final Logger logger = LoggerFactory.getLogger(DubboServiceImpl.class);
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    @SuppressWarnings("unchecked")
    public void insertMethodTime(String methodName, Long time) {
        redisTemplate.opsForHash().put("method", methodName, time);
    }
}
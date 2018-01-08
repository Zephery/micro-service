package com.myblog.demo.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.myblog.demo.DemoService;

/**
 * @author Zephery
 * @since 2018/1/5 9:55
 */
@Service
public class DemoServiceImpl implements DemoService {
    @Override
    public String sayHello(String hello) {
        return hello + "world";
    }
}
package com.myblog.demo.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.myblog.demo.DemoService;

/**
 * Created with IntelliJ IDEA.
 * User: Zephery
 * Time: 2018/1/6 19:57
 * Description:
 */
@Service
public class DemoServiceImpl implements DemoService {
    public String sayHello(String name) {
        return name + "world";
    }
}
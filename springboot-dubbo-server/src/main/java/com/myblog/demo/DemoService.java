package com.myblog.demo;

import com.myblog.model.Blog;

/**
 * @author Zephery
 * @since 2018/1/5 9:55
 */
public interface DemoService {
    public String sayHello(String hello);

    public Blog get(Integer blogid);
}
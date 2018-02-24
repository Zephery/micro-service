package com.myblog.demo.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.myblog.dao.IBlogDao;
import com.myblog.demo.DemoService;
import com.myblog.model.Blog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * @author Zephery
 * @since 2018/1/5 9:55
 */
@Service
public class DemoServiceImpl implements DemoService {
    //logger
    private static final Logger logger = LoggerFactory.getLogger(DemoServiceImpl.class);
    @Resource
    private IBlogDao blogDao;

    @Override
    public String sayHello(String hello) {
        return hello + "world";
    }

    @Override
    public Blog get(Integer blogid) {
        Blog blog = blogDao.findOne(blogid);
        return blog;
    }
}
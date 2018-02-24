package com.myblog.dubbo.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.myblog.dao.IBlogDao;
import com.myblog.dubbo.DubboService;
import com.myblog.model.Blog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private IBlogDao blogDao;

    @Override
    public String sayHello(String hello) {
        return hello + "world";
    }

    @Override
    public Blog get(Integer blogid) {
        return blogDao.findOne(blogid);
    }
}
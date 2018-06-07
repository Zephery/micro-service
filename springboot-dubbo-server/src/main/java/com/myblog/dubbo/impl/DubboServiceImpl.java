package com.myblog.dubbo.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.myblog.dao.IBlogDao;
import com.myblog.dao.IWeiboDao;
import com.myblog.dubbo.DubboService;
import com.myblog.model.Blog;
import com.myblog.model.Weibo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Zephery
 * @since 2018/1/13 15:58
 */
@Service(
        version = "1.0.0",
        application = "${dubbo.application.id}",
        protocol = "${dubbo.protocol.id}",
        registry = "${dubbo.registry.id}"
)
public class DubboServiceImpl implements DubboService {
    //logger
    private static final Logger logger = LoggerFactory.getLogger(DubboServiceImpl.class);

    @Resource
    private IBlogDao blogDao;
    @Resource
    private IWeiboDao weiboDao;

    @Override
    public String sayHello(String hello) {
        return hello + "world";
    }

    @Override
    public Blog get(Integer blogid) {
        return blogDao.findOne(blogid);
    }

    @Override
    public List<Weibo> getAllWeiboToday() {
        return weiboDao.getWeibosToday();
    }
}
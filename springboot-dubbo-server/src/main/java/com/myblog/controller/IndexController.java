package com.myblog.controller;

import com.google.gson.Gson;
import com.myblog.dao.IBlogDao;
import com.myblog.dao.IWeiboDao;
import com.myblog.model.Blog;
import com.myblog.model.Weibo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Zephery
 * @since 2018/1/8 14:14
 */
@RestController
public class IndexController {
    //logger
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
    @Resource
    private IBlogDao blogDao;
    @Resource
    private IWeiboDao weiboDao;

    @GetMapping("/hello")
    public String hello() {
        return "hell world";
    }

    @GetMapping("/get")
    public String get(Integer blogid) {
        Blog blog = blogDao.getOne(blogid);
        Gson gson = new Gson();
        return blog.getSummary();
    }

    @GetMapping("/getWeibo")
    public String getWeibo() {
        List<Weibo> weibos = weiboDao.getWeibosToday();
        return "aaaa";
    }
}
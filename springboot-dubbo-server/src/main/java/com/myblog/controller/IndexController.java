package com.myblog.controller;

import com.google.gson.Gson;
import com.myblog.dao.IBlogDao;
import com.myblog.model.Blog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
}
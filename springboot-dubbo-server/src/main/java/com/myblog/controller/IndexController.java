package com.myblog.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Zephery
 * @since 2018/1/8 14:14
 */
@RestController
public class IndexController {
    //logger
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @GetMapping("/hello")
    public String hello() {
        return "hell world";
    }
}
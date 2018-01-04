package com.myblog.demo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Zephery
 * @since 2018/1/3 14:40
 */
public class Provider {
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[]{"dubbo-demo-provider.xml"});
        context.start();
        // press any key to exit
        System.in.read();
    }
}
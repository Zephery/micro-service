package com.myfast.springbootfastdfs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
//@Import(FdfsClientConfig.class)
public class SpringbootFastdfsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootFastdfsApplication.class, args);
    }

    @RequestMapping("/hello")
    public String hello() {
        return "hello";
    }
}

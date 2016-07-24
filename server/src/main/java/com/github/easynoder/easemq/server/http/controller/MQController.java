package com.github.easynoder.easemq.server.http.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Desc:
 * Author:easynoder
 * Date:16/7/17
 * E-mail:easynoder@outlook.com
 */
@RestController
@EnableAutoConfiguration
public class MQController {

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    @ResponseBody
    public String sayHello() {
        return "hello easemq!";
    }


    public static void main(String[] args) {
        SpringApplication.run(MQController.class, args);
    }
}

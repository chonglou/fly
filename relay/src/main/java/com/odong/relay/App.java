package com.odong.relay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {
    public static void main(String[] args) {
        logger.info("启动");
        new ClassPathXmlApplicationContext("spring/*.xml");

    }

    private final static Logger logger = LoggerFactory.getLogger(App.class);
}

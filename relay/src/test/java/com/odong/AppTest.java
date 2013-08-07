package com.odong;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {

    @Test
    public void test0() {
        try {
            Thread.sleep(1000 * 60 * 5);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeTest
    public void init() {
        try {
            ctx = new ClassPathXmlApplicationContext("spring/*.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ApplicationContext ctx;
}

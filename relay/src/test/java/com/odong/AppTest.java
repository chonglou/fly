package com.odong;


import com.odong.relay.model.Log;
import com.odong.relay.util.LogService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;

/**
 * Unit test for simple App.
 */
public class AppTest {

    @Test
    public void testLog(){
        LogService ls = ctx.getBean(LogService.class);
        for(int i=0; i<100; i++){
            ls.add(1, "测试 "+i);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        for(Log l : ls.list(1, 99)){
            System.out.println(l.getId()+"\t"+format.format(l.getCreated())+"\t"+l.getMessage());
        }
    }
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

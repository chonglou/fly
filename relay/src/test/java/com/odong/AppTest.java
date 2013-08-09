package com.odong;


import com.odong.relay.Server;
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
            Server.get().init();
            Server.get().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

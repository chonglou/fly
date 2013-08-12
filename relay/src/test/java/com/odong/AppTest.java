package com.odong;


import com.odong.relay.Server;
import com.odong.relay.model.Log;
import com.odong.relay.util.StoreHelper;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {

    //@Test
    void testDb(){
        try{
            StoreHelper sh = Server.get().getBean(StoreHelper.class);
            for(int i=0;i<100;i++){
                sh.addLog("task://aaa", "message "+i);
            }
            for(Log l : sh.listLog(100)){
                System.out.println(l.getId()+"\t"+l.getMessage()+"\t"+l.getCreated());
            }
            System.out.println("###############################");
            for(Log l : sh.listLog(6)){
                System.out.println(l.getId()+"\t"+l.getMessage()+"\t"+l.getCreated());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //@Test
    public void testGUI() {

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

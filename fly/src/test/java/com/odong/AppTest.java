package com.odong;

import com.odong.fly.App;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class AppTest

{
    /*
    //@Test
    public void testJms() {
        TaskSender sender = Server.get().bean(TaskSender.class);
        for (int i = 0; i < 10; i++) {
            sender.send(UUID.randomUUID().toString());
            log(i, UUID.randomUUID().toString().length());
        }
    }

    //@Test
    public void testCamera() {
        try {
            CameraUtil cu = Server.get().bean(CameraUtil.class);
            int device = 10;
            String dvi = cu.randomName();
            cu.open(device);
            cu.start(device, dvi, 15);
            cu.stop(device);
            String png = cu.randomName();
            cu.photo(device, png);
            cu.close(device);

            log(dvi, png);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //@Test
    public void testOs() {
        log(System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("os.arch"));
    }

    //@Test
    public void testEncrypt() {
        try {
            EncryptHelper eh = Server.get().bean(EncryptHelper.class);
            String msg = "123654";
            System.out.println(eh.encrypt(msg));

            String code = eh.encode(msg);
            System.out.println(code);
            System.out.println(eh.decode(code));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //@Test
    public void testDb() {
        try {
            StoreHelper sh = Server.get().bean(StoreHelper.class);
            for (int i = 0; i < 100; i++) {
                sh.addLog("message " + i);
            }
            for (Log l : sh.listLog(100)) {
                System.out.println(l.getId() + "\t" + l.getMessage() + "\t" + l.getCreated());
            }
            System.out.println("###############################");
            for (Log l : sh.listLog(6)) {
                System.out.println(l.getId() + "\t" + l.getMessage() + "\t" + l.getCreated());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //@Test
    public void testCsv() {
        try {
            FileHelper fh = Server.get().bean(FileHelper.class);
            int size = 5;
            Csv csv = new Csv("/tmp/测试.csv", 100);
            for (int i = 0; i < size; i++) {
                com.odong.core.file.csv.Column column = new com.odong.core.file.csv.Column("列-" + i, com.odong.core.file.csv.Column.Type.STRING);
                for (int j = 0; j < 100; j++) {
                    column.addItem(String.format("(%02d, %02d)", j, i));
                }
                csv.addColumn(column);
            }
            fh.write(csv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //@Test
    public void testExcel() {
        try {
            FileHelper fh = Server.get().bean(FileHelper.class);
            Excel excel = new Excel("/tmp/测试.xls");
            int size = 100;
            for (int i = 0; i < 3; i++) {
                Table table = new Table("表格-" + i, size);
                for (int j = 0; j < 5; j++) {
                    Column column = new Column("列" + j);
                    for (int k = 0; k < size; k++) {
                        column.addCell(new Cell(String.format("(页-%02d,列-%02d,行-%02d)", i, j, k)));
                    }
                    table.addColumn(column);
                }
                excel.addTable(table);
            }

            fh.write(excel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void init0() {
        try {
            Server.get().init();
            Server.get().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void log(Object... objects) {
        JsonHelper jh = Server.get().bean(JsonHelper.class);
        for (Object obj : objects) {
            System.out.println(jh.object2json(obj));
        }
    }
*/
    @Test
    public void testWindow() {

        try {
            Thread.sleep(1000 * 60 * 5);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @BeforeTest
    public void init() {

        try {
            App.main(new String[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

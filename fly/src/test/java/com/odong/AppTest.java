package com.odong;

import com.odong.core.file.FileHelper;
import com.odong.core.file.csv.Csv;
import com.odong.core.file.excel.Cell;
import com.odong.core.file.excel.Column;
import com.odong.core.file.excel.Excel;
import com.odong.core.file.excel.Table;
import com.odong.core.util.EncryptHelper;
import com.odong.fly.Server;
import com.odong.fly.model.Log;
import com.odong.fly.util.StoreHelper;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class AppTest

{
    //@Test
    public void testEncrypt() {
        try {
            EncryptHelper eh = Server.get().getBean(EncryptHelper.class);
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
            StoreHelper sh = Server.get().getBean(StoreHelper.class);
            for (int i = 0; i < 100; i++) {
                sh.addLog("task://aaa", "message " + i);
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
            FileHelper fh = Server.get().getBean(FileHelper.class);
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
            FileHelper fh = Server.get().getBean(FileHelper.class);
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

    //@Test
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
            Server.get().init();
            Server.get().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

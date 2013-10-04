package com.odong;

import com.odong.avi.AVIOutputStream;
import com.odong.fly.App;
import org.testng.annotations.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import java.util.Random;

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
    //@Test
    public void testWindow() {

        try {
            Thread.sleep(1000 * 60 * 60 * 24);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //@Test
    public void testAvi(){
        try {
            write2avi(new File("/tmp/jpg.avi"), AVIOutputStream.VideoFormat.JPG, 24, 1f);
            write2avi(new File("/tmp/png.avi"), AVIOutputStream.VideoFormat.PNG, 24, 1f);
            write2avi(new File("/tmp/raw.avi"), AVIOutputStream.VideoFormat.RAW, 24, 1f);
            write2avi(new File("/tmp/rle8.avi"), AVIOutputStream.VideoFormat.RLE, 8, 1f);
            //test(new File("avidemo-rle4.avi"), AVIOutputStream.VideoFormat.RLE, 4, 1f);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    //@BeforeTest
    public void init() {

        try {
            App.main(new String[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private  void write2avi(File file, AVIOutputStream.VideoFormat format, int depth, float quality) throws IOException {
        System.out.println("Writing " + file);
        AVIOutputStream out = null;
        Graphics2D g = null;
        try {
            out = new AVIOutputStream(file, format, depth);
            out.setVideoCompressionQuality(quality);

            out.setTimeScale(1);
            out.setFrameRate(30);

            Random rnd = new Random(0); // use seed 0 to get reproducable output
            BufferedImage img;
            switch (depth) {
                case 24:
                default: {
                    img = new BufferedImage(320, 160, BufferedImage.TYPE_INT_RGB);
                    break;
                }
                case 8: {
                    byte[] red = new byte[256];
                    byte[] green = new byte[256];
                    byte[] blue = new byte[256];
                    for (int i = 0; i < 255; i++) {
                        red[i] = (byte) rnd.nextInt(256);
                        green[i] = (byte) rnd.nextInt(256);
                        blue[i] = (byte) rnd.nextInt(256);
                    }
                    rnd.setSeed(0); // set back to 0 for reproducable output
                    img = new BufferedImage(320, 160, BufferedImage.TYPE_BYTE_INDEXED, new IndexColorModel(8, 256, red, green, blue));
                    break;
                }
                case 4: {
                    byte[] red = new byte[16];
                    byte[] green = new byte[16];
                    byte[] blue = new byte[16];
                    for (int i = 0; i < 15; i++) {
                        red[i] = (byte) rnd.nextInt(16);
                        green[i] = (byte) rnd.nextInt(16);
                        blue[i] = (byte) rnd.nextInt(16);
                    }
                    rnd.setSeed(0); // set back to 0 for reproducable output
                    img = new BufferedImage(320, 160, BufferedImage.TYPE_BYTE_BINARY, new IndexColorModel(4, 16, red, green, blue));
                    break;
                }
            }
            g = img.createGraphics();
            g.setBackground(Color.WHITE);
            g.clearRect(0, 0, img.getWidth(), img.getHeight());

            for (int i = 0; i < 100; i++) {
                g.setColor(new Color(rnd.nextInt()));
                g.fillRect(rnd.nextInt(img.getWidth() - 30), rnd.nextInt(img.getHeight() - 30), 30, 30);
                out.writeFrame(img);
            }

        } finally {
            if (g != null) {
                g.dispose();
            }
            if (out != null) {
                out.close();
            }
        }
    }

}

package com.odong.fly.camera;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-22
 * Time: 上午10:22
 */
public abstract class CameraUtil {

    public abstract Set<Integer> getStatus();

    public abstract boolean hasOpen();

    public abstract boolean isRecorder(int device);

    public abstract void start(int device, String file, int rate) throws IOException;

    public abstract void stop(int device) throws IOException;

    public abstract void photo(int device, String file) throws IOException;


    public abstract void open(int device) throws IOException;

    public abstract void close(int device) throws IOException;

    public abstract void init();

    public abstract void destroy();

    public abstract boolean isOpen(int device);

    public abstract Set<Integer> listDevice();


    public String randomName() {
        return "var/camera/" + UUID.randomUUID().toString();
    }

    protected void writeImage(String name, BufferedImage image) throws IOException {
        ImageIO.write(image, "png", new File(name));
    }

}

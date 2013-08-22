package com.odong.fly.camera;

import com.odong.fly.model.Photo;
import com.odong.fly.util.StoreHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-22
 * Time: 上午11:26
 */
@Component
public final class PhotoHelper {
    public void save(BufferedImage image) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(image, FORMAT, out);
            storeHelper.addPhoto(out.toByteArray());
        } catch (IOException e) {
            logger.error("保存图片到数据库出错", e);
        }
    }

    public void write(long photo, String name) {
        Photo p = storeHelper.getPhoto(photo);
        try (ByteArrayInputStream in = new ByteArrayInputStream(p.getImage())) {
            BufferedImage image = ImageIO.read(in);
            ImageIO.write(image, FORMAT, new File(name));
        } catch (IOException e) {
            logger.error("保存图片到文件出错", e);
        }

    }

    @Resource
    private StoreHelper storeHelper;
    private final static String FORMAT = "png";
    private final static Logger logger = LoggerFactory.getLogger(PhotoHelper.class);

    public void setStoreHelper(StoreHelper storeHelper) {
        this.storeHelper = storeHelper;
    }
}

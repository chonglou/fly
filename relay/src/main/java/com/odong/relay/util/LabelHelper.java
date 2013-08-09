package com.odong.relay.util;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.awt.*;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-7
 * Time: 上午11:17
 */
@Component
public class LabelHelper {

    public Image getIconImage() {
        return Toolkit.getDefaultToolkit().getImage(getClass().getResource("/tray.png"));
    }

    public String getMessage(String key, Locale locale) {
        return messageSource.getMessage("lbl." + key, null, locale);
    }

    public String getMessage(String key) {
        return messageSource.getMessage("lbl." + key, null, Locale.CHINESE);
    }

    @Resource
    private MessageSource messageSource;

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}

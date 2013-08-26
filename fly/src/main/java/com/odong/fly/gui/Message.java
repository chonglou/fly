package com.odong.fly.gui;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.awt.*;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-25
 * Time: 下午12:21
 */
@Component("gui.message")
public class Message {
    public Image getIcon() {
        return icon;
    }


    public String getMessage(String key) {
        return messageSource.getMessage("lbl." + key, null, locale);
    }

    @PostConstruct
    void init() {
        locale = Locale.getDefault();
        icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/tray.png"));
    }

    @Resource
    private MessageSource messageSource;
    private Locale locale;
    private Image icon;

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}

package com.odong.core.util;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.jasypt.util.text.BasicTextEncryptor;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 上午10:54
 */
public class EncryptHelper {

    public String encode(String plain) {
        return bte.encrypt(plain);
    }

    public String decode(String encrypt) {
        return bte.decrypt(encrypt);
    }

    public String encrypt(String plain) {
        return spe.encryptPassword(plain);
    }

    public boolean check(String plain, String encrypt) {
        return spe.checkPassword(plain, encrypt);
    }


    public void init() {
        if (appKey == null || appKey.length() < 20) {
            throw new IllegalArgumentException("app.key长度不应小于20位");
        }
        spe = new StrongPasswordEncryptor();
        bte = new BasicTextEncryptor();
        bte.setPassword(appKey);
    }

    private StrongPasswordEncryptor spe;
    private BasicTextEncryptor bte;
    private String appKey;

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }


}

package com.odong.relay.util;

import com.odong.relay.model.Log;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-7
 * Time: 下午10:34
 */
public interface StoreHelper {
    void put(String key, String val);

    String get(String key);

    void pop(String key);

    List<Log> list(int port, int size);

    void add(int port, String message);
}
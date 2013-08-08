package com.odong.relay.util;

import com.odong.relay.model.Log;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-7
 * Time: 下午10:34
 */
public interface LogService {
    List<Log> list(int port, int size);
    void add(int port, String message);
}

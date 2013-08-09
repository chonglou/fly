package com.odong.relay.util.impl;

import com.odong.relay.model.Item;
import com.odong.relay.model.Log;
import com.odong.relay.model.Setting;
import com.odong.relay.util.StoreHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-7
 * Time: 下午10:35
 */
@Service
public class StoreHelperDerbyImpl implements StoreHelper {
    @Override
    public void put(String key, String val) {
        execute(get(key) == null ?
                String.format("INSERT INTO %s(k,v) VALUES('%s', '%s')", settingTableName, key, val) :
                String.format("UPDATE %s SET v='%s',version=version+1 WHERE k='%s'", settingTableName, val, key)
        );
    }

    @Override
    public String get(String key) {
        final Setting setting = new Setting();
        execute(String.format("SELECT k,v FROM %s WHERE k='%s'", settingTableName, key), 1, new Callback() {
            @Override
            public void loop(ResultSet rs) throws SQLException {
                setting.setKey(rs.getString("k"));
                setting.setVal(rs.getString("v"));
            }
        });
        return setting.getVal();  //
    }

    @Override
    public void pop(String key) {
        execute(String.format("DELETE FROM %s WHERE k='%s'", settingTableName, key));
    }

    @Override
    public void addItem(String task, String request, String response) {
        execute(String.format("INSERT INTO %s(task,request,response) VALUES('%s','%s','%s')", itemTableName, task, request, response));
    }

    @Override
    public List<Item> listItem(String task) {
        final List<Item> items = new ArrayList<>();
        execute(String.format("SELECT id,task,request,response,created FROM '%s' WHERE task='%s' ", itemTableName, task),
                null,
                new Callback() {
                    @Override
                    public void loop(ResultSet rs) throws SQLException {
                        Item item = new Item();
                        item.setId(rs.getLong("id"));
                        item.setTask(rs.getString("task"));
                        item.setResponse(rs.getString("request"));
                        item.setResponse(rs.getString("response"));
                        item.setCreated(rs.getTimestamp("created"));
                        items.add(item);
                    }
                });
        return items;  //
    }

    @Override
    public List<Log> listLog(int len) {
        final List<Log> logs = new ArrayList<>();
        execute(String.format("SELECT id,task,message,created FROM %s WHERE ORDER BY id DESC", logTableName), len, new Callback() {
            @Override
            public void loop(ResultSet rs) throws SQLException {
                Log l = new Log();
                l.setId(rs.getLong("id"));
                l.setTask(rs.getString("task"));
                l.setMessage(rs.getString("message"));
                l.setCreated(rs.getTimestamp("created"));
                logs.add(l);
            }
        });
        return logs;  //
    }

    @Override
    public void addLog(String task, String message) {
        execute(String.format("INSERT INTO %s(task, message) VALUES ('%s','%s')", logTableName, task, message));
    }

    @PostConstruct
    void init() throws Exception {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            connection = DriverManager.getConnection("jdbc:derby:" + dbName + ";create=true");
            logger.info("连接数据库");

            checkTable(logTableName, "id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY, task VARCHAR(255), message VARCHAR(1024) NOT NULL, created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");
            checkTable(settingTableName, "k VARCHAR(255) NOT NULL PRIMARY KEY, v VARCHAR(8000) NOT NULL, created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, version BIGINT NOT NULL DEFAULT 0");
            checkTable(itemTableName, "id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY, task VARCHAR(255) NOT NULL, request VARCHAR(255) NOT NULL, response VARCHAR(1024), created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");

        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException e) {
            logger.error("连接数据库出错", e);
            throw e;
        }
    }

    private void checkTable(String tableName, String struct) {
        try {
            DatabaseMetaData dmd = connection.getMetaData();
            ResultSet rs = dmd.getTables(null, "APP", tableName, null);
            if (rs.next()) {
                logger.info("数据库表{}已存在", tableName);
            } else {
                logger.info("创建数据库表{}不存在，准备创建...", tableName);
                execute(String.format("CREATE TABLE %s(%s)", tableName, struct));

            }
            rs.close();
        } catch (SQLException e) {
            logger.error("数据库错误", e);
        }
    }


    @PreDestroy
    void destroy() {
        try {
            DriverManager.getConnection("jdbc:derby:" + dbName + ";shutdown=true");
            logger.info("关闭数据库");

        } catch (SQLException e) {
            logger.error("关闭数据库出错", e);
        }
    }

    private void execute(String sql, Object... params) {
        try (PreparedStatement s = connection.prepareStatement(sql)) {
            for (int i = 1; i <= params.length; i++) {
                s.setObject(i, params[i]);
            }
            s.execute();
        } catch (SQLException e) {


            logger.error("执行SQL[{}]出错", sql, e);
        }
    }


    private void execute(String sql) {
        //logger.debug(sql);
        try (Statement s = connection.createStatement()) {
            s.execute(sql);
        } catch (SQLException e) {

            logger.error("执行SQL[{}]出错", sql, e);
        }

    }

    private void execute(String sql, Object[] params, Integer size, Callback cb) {
        //logger.debug(sql);
        try (PreparedStatement s = connection.prepareStatement(sql)) {
            if (size != null) {
                s.setMaxRows(size);
            }
            for (int i = 1; i <= params.length; i++) {
                s.setObject(i, params[i]);
            }
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                cb.loop(rs);
            }
        } catch (SQLException e) {
            logger.error("查询SQL[{}]出错", sql, e);
        }
    }

    private void execute(String sql, Integer size, Callback cb) {
        //logger.debug(sql);
        try (Statement s = connection.createStatement()) {
            if (size != null) {
                s.setMaxRows(size);
            }
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                cb.loop(rs);
            }
        } catch (SQLException e) {
            logger.error("查询SQL[{}]出错", sql, e);
        }
    }

    interface Callback {
        void loop(ResultSet rs) throws SQLException;
    }


    private Connection connection;
    private final static String dbName = "var/db";
    private final static String logTableName = "LOGS";
    private final static String settingTableName = "ENV";
    private final static String itemTableName = "ITEMS";
    private final static Logger logger = LoggerFactory.getLogger(StoreHelperDerbyImpl.class);


}

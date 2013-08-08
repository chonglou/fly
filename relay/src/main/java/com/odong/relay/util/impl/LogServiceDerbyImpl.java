package com.odong.relay.util.impl;

import com.odong.relay.model.Log;
import com.odong.relay.util.LogService;
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
public class LogServiceDerbyImpl implements LogService {
    @Override
    public List<Log> list(int port, int size) {
        final List<Log> logs = new ArrayList<>();
        execute(String.format("SELECT id,port,message,created FROM %s WHERE port=%d ORDER BY id DESC", tableName, port), size, new Callback() {
            @Override
            public void loop(ResultSet rs) throws SQLException {
                Log l = new Log();
                l.setId(rs.getLong("id"));
                l.setPort(rs.getInt("port"));
                l.setMessage(rs.getString("message"));
                l.setCreated(rs.getTimestamp("created"));
                logs.add(l);
            }
        });
        return logs;  //
    }

    @Override
    public void add(int port, String message) {
        execute(String.format("INSERT INTO %s(message, port) VALUES ('%s', %d)", tableName, message, port));
    }

    @PostConstruct
    void init() throws Exception {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            connection = DriverManager.getConnection("jdbc:derby:var/db;create=true");
            logger.info("连接数据库");

            DatabaseMetaData dmd = connection.getMetaData();
            ResultSet rs = dmd.getTables(null, "APP", tableName, null);
            if (rs.next()) {
                logger.info("数据库表{}已存在", tableName);
            } else {
                execute(String.format("CREATE TABLE %s(ID BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY, port INTEGER, message VARCHAR(8000) NOT NULL, created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP )", tableName));
                logger.info("创建数据库表{}", tableName);
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException e) {
            logger.error("连接数据库出错", e);
            throw e;
        }
    }


    @PreDestroy
    void destroy() {
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
            logger.info("关闭数据库");

        } catch (SQLException e) {
            logger.error("关闭数据库出错", e);
        }
    }

    private void execute(String sql) {
        logger.debug(sql);
        try (Statement s = connection.createStatement()) {
            s.execute(sql);
        } catch (SQLException e) {

            logger.error("执行SQL出错", sql, e);
        }

    }

    private void execute(String sql, Integer size, Callback cb) {
        logger.debug(sql);
        try (Statement s = connection.createStatement()) {
            if (size != null) {
                s.setMaxRows(size);
            }
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                cb.loop(rs);
            }
        } catch (SQLException e) {
            logger.error("查询SQL出错", e);
        }
    }

    interface Callback {
        void loop(ResultSet rs) throws SQLException;
    }


    private Connection connection;
    private final static String tableName = "LOGS";
    private final static Logger logger = LoggerFactory.getLogger(LogServiceDerbyImpl.class);


}

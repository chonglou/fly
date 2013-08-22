package com.odong.fly.util.impl;

import com.odong.core.util.JsonHelper;
import com.odong.fly.job.Task;
import com.odong.fly.model.Item;
import com.odong.fly.model.Log;
import com.odong.fly.model.Photo;
import com.odong.fly.util.StoreHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-12
 * Time: 下午3:05
 */
@Repository
public class StoreHelperJdbcImpl implements StoreHelper {


    @Override
    public void addTask(Task t) {
        put(taskId2Key(t.getId()), jsonHelper.object2json(t));
    }

    @Override
    public Task getTask(String id) {
        return jsonHelper.json2object(get(taskId2Key(id)), Task.class);
    }

    @Override
    public List<Task> listTask(Date begin, Date end) {
        return jdbcTemplate.query("SELECT k,v FROM ENV WHERE k LIKE '?%%' AND created>=? AND created<=?", new Object[]{"task://", begin, end}, new RowMapper<Task>() {
            @Override
            public Task mapRow(ResultSet resultSet, int i) throws SQLException {
                return jsonHelper.json2object(resultSet.getString("v"), Task.class);  //
            }
        });  //
    }

    @Override
    public void put(String key, String val) {
        jdbcTemplate.update(
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM ENV WHERE k=?", new Object[]{key},
                        Integer.class) == 0 ?
                        "INSERT INTO ENV(v,k) VALUES(?, ?)" :
                        "UPDATE ENV SET v=? WHERE k=?", val, key);

    }

    @Override
    public String get(String key) {
        return jdbcTemplate.queryForObject("SELECT v FROM %s WHERE k=?", new Object[]{key}, String.class);  //
    }

    @Override
    public void pop(String key) {
        jdbcTemplate.update("DELETE FROM ENV WHERE k=?", key);
    }

    @Override
    public void addItem(String taskId, String request, String response) {
        jdbcTemplate.update("INSERT INTO ITEMS(task,request,response) VALUES(?,?,?)", taskId, request, response);
    }

    @Override
    public List<Item> listItem(String taskId) {
        return jdbcTemplate.query("SELECT id,task,request,response,created FROM ITEMS WHERE task=? ", new Object[]{taskId}, new RowMapper<Item>() {
            @Override
            public Item mapRow(ResultSet resultSet, int i) throws SQLException {
                Item item = new Item();
                item.setId(resultSet.getLong("id"));
                item.setTask(resultSet.getString("task"));
                item.setResponse(resultSet.getString("request"));
                item.setResponse(resultSet.getString("response"));
                item.setCreated(resultSet.getTimestamp("created"));
                return item;  //
            }
        });
    }

    @Override
    public List<Log> listLog(final int len) {
        return jdbcTemplate.query(
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps = connection.prepareStatement("SELECT id,task,message,created FROM LOGS  ORDER BY id DESC");
                        ps.setMaxRows(len);
                        return ps;  //
                    }
                },
                new RowMapper<Log>() {
                    @Override
                    public Log mapRow(ResultSet resultSet, int i) throws SQLException {
                        Log log = new Log();
                        log.setId(resultSet.getLong("id"));
                        log.setTask(resultSet.getString("task"));
                        log.setMessage(resultSet.getString("message"));
                        log.setCreated(resultSet.getTimestamp("created"));
                        return log;  //
                    }
                }
        );
        /*
        return jdbcTemplate.query("SELECT id,task,message,created FROM LOGS  ORDER BY id DESC LIMIT 10",  new RowMapper<Log>() {
            @Override
            public Log mapRow(ResultSet resultSet, int i) throws SQLException {
                Log log = new Log();
                log.setId(resultSet.getLong("id"));
                log.setTask(resultSet.getString("task"));
                log.setMessage(resultSet.getString("message"));
                log.setCreated(resultSet.getTimestamp("created"));
                return log;  //
            }
        });  //
        */
    }


    @Override
    public void addLog(String task, String message) {
        jdbcTemplate.update("INSERT INTO LOGS(task, message) VALUES (?,?)", task, message);
    }

    @Override
    public Photo getPhoto(long id) {
        return null;  //
    }

    @Override
    public void addPhoto(byte[] image) {
        //
    }

    @PostConstruct
    synchronized void init() {
        jdbcTemplate.execute(new ConnectionCallback<Object>() {
            @Override
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException {
                DatabaseMetaData dmd = connection.getMetaData();
                databaseProductName = dmd.getDatabaseProductName();
                databaseProductVersion = dmd.getDatabaseProductVersion();
                logger.info("使用数据库[{},{}]", databaseProductName, databaseProductVersion);
                return null;  //
            }
        });
        Map<String, String> map = new HashMap<>();
        map.put("LOGS", "id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY, task VARCHAR(255), message VARCHAR(1024) NOT NULL, created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");
        map.put("ENV", "k VARCHAR(255) NOT NULL PRIMARY KEY, v VARCHAR(8000) NOT NULL, created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, version BIGINT NOT NULL DEFAULT 0");
        map.put("ITEMS", "id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY, task VARCHAR(255) NOT NULL, request VARCHAR(255) NOT NULL, response VARCHAR(1024), created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");
        for (String tableName : map.keySet()) {
            if (isTableExist(tableName)) {
                logger.info("数据库表{}已存在", tableName);
            } else {
                logger.info("创建数据库表{}不存在，准备创建...", tableName);
                jdbcTemplate.execute(String.format("CREATE TABLE %s(%s)", tableName, map.get(tableName)));
            }
        }


    }


    private boolean isTableExist(final String tableName) {
        return jdbcTemplate.execute(new ConnectionCallback<Boolean>() {
            @Override
            public Boolean doInConnection(Connection connection) throws SQLException, DataAccessException {

                DatabaseMetaData dmd = connection.getMetaData();
                ResultSet rs = dmd.getTables(null, schema, tableName, new String[]{"TABLE", "VIEW"});
                boolean exist = rs.next();
                rs.close();
                return exist;
            }
        });
    }

    private String taskId2Key(String id) {
        return "task://" + id;
    }

    private String databaseProductName;
    private String databaseProductVersion;
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private JsonHelper jsonHelper;
    @Value("${db.schema}")
    private String schema;
    private final static Logger logger = LoggerFactory.getLogger(StoreHelperJdbcImpl.class);

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public void setJsonHelper(JsonHelper jsonHelper) {
        this.jsonHelper = jsonHelper;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}

package com.odong.fly.util.impl;

import com.odong.core.util.JsonHelper;
import com.odong.fly.model.Log;
import com.odong.fly.model.Task;
import com.odong.fly.model.item.CameraItem;
import com.odong.fly.model.item.SerialItem;
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
    public void addOnOffTask(String id, String portName, int channel, Date begin, Date end, Long total, int onSpace, int offSpace) {
        addTask(id, Task.Type.ON_OFF,
                String.format("onoff://%s:%d/onSpace=%d&offSpace=%d", portName, channel, onSpace, offSpace),
                Boolean.FALSE.toString(),
                begin, end, total, null);
    }

    @Override
    public void addPhotoTask(String id, int device, Date begin, Date end, Long total, int space) {
        addTask(id, Task.Type.PHOTO,
                String.format("photo://%d", device),
                null,
                begin, end, total, space);
    }

    @Override
    public void addVideoTask(String id, int device, int rate, Date begin, Date end, Long total, int onSpace, int offSpace) {
        addTask(id, Task.Type.VIDEO,
                String.format("video://%d/rate=%d&onSpace=%d%offSpace=%d", device, rate, onSpace, offSpace),
                null,
                begin, end, total, null);
    }

    @Override
    public Task getTask(String taskId) {
        return jdbcTemplate.queryForObject("SELECT * FROM TASKS WHERE id=?", mapperTask(), taskId);
    }

    @Override
    public void setTaskState(String taskId, Task.State state) {
        jdbcTemplate.update("UPDATE TASKS SET state=? WHERE id=?", state.name(), taskId);
    }

    @Override
    public List<Task> listTask(Date begin, Date end) {
        return jdbcTemplate.query("SELECT * FROM TASKS WHERE created>=? AND created<=?", mapperTask(), begin, end);  //
    }

    @Override
    public List<Task> listTask(Task.State state) {
        return jdbcTemplate.query("SELECT * FROM TASKS WHERE state=?", mapperTask(), state.name());
    }

    @Override
    public List<Task> listTask(Task.Type type) {
        return jdbcTemplate.query("SELECT * FROM TASKS WHERE type_=?", mapperTask(), type.name());  //
    }

    @Override
    public void startUp(String taskId) {
        jdbcTemplate.update("UPDATE TASKS SET state=?,lastStartUp=?,index=index+1 WHERE id=?", Task.State.PROCESSING.name(), taskId, new Date());
    }

    @Override
    public void shutDown(String taskId, String temp, Task.State state) {
        jdbcTemplate.update("UPDATE TASKS SET temp=?, state=?, lastShutDown=?, WHERE id=?", temp, state.name(), new Date(), taskId);
    }

    @Override
    public void addSerialItem(String taskId, String request, String response) {
        jdbcTemplate.update("INSERT INTO SERIALS_ITEMS(task, request, response) VALUES(?,?,?)", taskId, request, response);
    }

    @Override
    public void addCameraItem(String taskId, String file) {
        jdbcTemplate.update("INSERT INTO CAMERA_ITEMS(task, file) VALUES(?,?)", taskId, file);
    }

    @Override
    public List<SerialItem> listSerialItem(String taskId) {
        return jdbcTemplate.query("SELECT * FROM SERIAL_ITEMS WHERE task=?", mapperSerialItem(), taskId);  //
    }

    @Override
    public List<CameraItem> listCameraItem(String taskId) {
        return jdbcTemplate.query("SELECT * FROM CAMERA_ITEMS WHERE task=?", mapperCameraItem(), taskId);
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
    public List<Log> listLog(int len) {
        return jdbcTemplate.query(pageStatementCreator(len), mapperLog());
    }


    @Override
    public void addLog(String message) {
        jdbcTemplate.update("INSERT INTO LOGS(message) VALUES (?)", message);
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
        map.put("LOGS", "id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY, " +
                "message VARCHAR(1024) NOT NULL, " +
                "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");

        map.put("ENV", "k VARCHAR(255) NOT NULL PRIMARY KEY, " +
                "v VARCHAR(8000) NOT NULL, " +
                "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "version BIGINT NOT NULL DEFAULT 0");
        map.put("CAMERA_ITEMS", "id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY, " +
                "task VARCHAR(255) NOT NULL, " +
                "file VARCHAR(255) NOT NULL, " +
                "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");
        map.put("SERIAL_ITEMS", "id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY, " +
                "task VARCHAR(255) NOT NULL, " +
                "request VARCHAR(255) NOT NULL, " +
                "response VARCHAR(255) , " +
                "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");

        map.put("TASKS", "id VARCHAR(255) NOT NULL PRIMARY KEY, " +
                "type_ VARCHAR(255) NOT NULL, " +
                "state VARCHAR(255) NOT NULL, " +
                "request VARCHAR(500) NOT NULL, " +
                "temp VARCHAR(500), " +
                "begin_ TIMESTAMP NOT NULL, " +
                "end_ TIMESTAMP NOT NULL, " +
                "lastStartUp TIMESTAMP, " +
                "lastShutDown TIMESTAMP, " +
                "total BIGINT, " +
                "index BIGINT NOT NULL DEFAULT 0, " +
                "space_ INTEGER, " +
                "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");

        logger.info("开始检查数据库");
        for (String tableName : map.keySet()) {
            if (isTableExist(tableName)) {
                logger.info("数据库表{}已存在", tableName);
            } else {
                logger.info("创建数据库表{}不存在，准备创建...", tableName);
                jdbcTemplate.execute(String.format("CREATE TABLE %s(%s)", tableName, map.get(tableName)));
                logger.info("成功创建数据库{}", tableName);
            }
        }
        logger.info("数据库检查通过");

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

    private PreparedStatementCreator pageStatementCreator(final int maxRows) {
        return new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement("SELECT id,message,created FROM LOGS  ORDER BY id DESC");
                ps.setMaxRows(maxRows);
                return ps;  //
            }
        };
    }

    private void addTask(String id, Task.Type type, String request, String temp, Date begin, Date end, Long total, Integer space) {
        jdbcTemplate.update("INSERT INTO TASKS(id,type_,state,request,temp,begin_,end_,total,space_) VALUES(?,?,?,?,?,?,?,?,?)",
                id, type.name(), Task.State.SUBMIT.name(), request, temp, begin, end, total, space);
    }

    private RowMapper<SerialItem> mapperSerialItem() {
        return new RowMapper<SerialItem>() {
            @Override
            public SerialItem mapRow(ResultSet resultSet, int i) throws SQLException {
                SerialItem item = new SerialItem();
                item.setId(resultSet.getLong("id"));
                item.setTask(resultSet.getString("task"));
                item.setRequest(resultSet.getString("request"));
                item.setResponse(resultSet.getString("response"));
                item.setCreated(resultSet.getTimestamp("created"));
                return item;  //
            }
        };
    }

    private RowMapper<Log> mapperLog() {
        return new RowMapper<Log>() {
            @Override
            public Log mapRow(ResultSet resultSet, int i) throws SQLException {
                Log log = new Log();
                log.setId(resultSet.getLong("id"));
                log.setMessage(resultSet.getString("message"));
                log.setCreated(resultSet.getTimestamp("created"));
                return log;  //
            }
        };
    }

    private RowMapper<CameraItem> mapperCameraItem() {
        return new RowMapper<CameraItem>() {
            @Override
            public CameraItem mapRow(ResultSet resultSet, int i) throws SQLException {
                CameraItem item = new CameraItem();
                item.setId(resultSet.getLong("id"));
                item.setTask(resultSet.getString("task"));
                item.setFile(resultSet.getString("file"));
                item.setCreated(resultSet.getTimestamp("crated"));
                return item;  //
            }
        };
    }

    private RowMapper<Task> mapperTask() {
        return new RowMapper<Task>() {
            @Override
            public Task mapRow(ResultSet resultSet, int i) throws SQLException {
                Task task = new Task();
                task.setId(resultSet.getString("id"));
                task.setType(Task.Type.valueOf(resultSet.getString("type_")));
                task.setState(Task.State.valueOf(resultSet.getString("state")));
                task.setRequest(resultSet.getString("request"));
                task.setTemp(resultSet.getString("temp"));
                task.setBegin(resultSet.getTimestamp("begin_"));
                task.setEnd(resultSet.getTimestamp("end_"));
                task.setLastStartUp(resultSet.getTime("lastStartUp"));
                task.setLastShutDown(resultSet.getTimestamp("lastShutDown"));
                task.setTotal(resultSet.getLong("total"));
                task.setIndex(resultSet.getLong("index"));
                task.setSpace(resultSet.getInt("space_"));
                task.setCreated(resultSet.getTimestamp("created"));
                return task;  //
            }
        };
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

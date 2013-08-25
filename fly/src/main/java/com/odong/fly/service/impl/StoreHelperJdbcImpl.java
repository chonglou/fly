package com.odong.fly.service.impl;

import com.odong.core.util.JsonHelper;
import com.odong.fly.model.Log;
import com.odong.fly.model.Task;
import com.odong.fly.model.item.CameraItem;
import com.odong.fly.model.item.Item;
import com.odong.fly.model.item.SerialItem;
import com.odong.fly.model.request.OnOffRequest;
import com.odong.fly.model.request.PhotoRequest;
import com.odong.fly.model.request.Request;
import com.odong.fly.model.request.VideoRequest;
import com.odong.fly.service.StoreHelper;
import com.odong.fly.widget.ProgressBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-12
 * Time: 下午3:05
 */
@Service
public class StoreHelperJdbcImpl implements StoreHelper {


    @Override
    public void addOnOffTask(String id, String portName, int channel, Date begin, Date end, long total, int onSpace, int offSpace) {
        addTask(id, Task.Type.ON_OFF,
                new OnOffRequest(portName, channel, onSpace, offSpace),
                Boolean.FALSE.toString(),
                begin, end, total);
    }

    @Override
    public void addPhotoTask(String id, int deviceId, String deviceName, Date begin, Date end, long total, int space) {
        addTask(id, Task.Type.PHOTO,
                new PhotoRequest(deviceId, deviceName, space),
                null,
                begin, end, total);
    }

    @Override
    public void addVideoTask(String id, int deviceId, String deviceName, int rate, Date begin, Date end) {
        addTask(id, Task.Type.VIDEO,
                new VideoRequest(deviceId, deviceName, rate),
                null,
                begin, end, 1);
    }

    @Override
    public Task getTask(String taskId) {
        return jdbcTemplate.queryForObject("SELECT * FROM TASKS WHERE id=?", mapperTask(), taskId);
    }


    @Override
    public Task getAvailSerialTask(String portName, int channel) {

        for (Task t : listAvailableTask(Task.Type.ON_OFF)) {
            OnOffRequest r = (OnOffRequest) t.getRequest();
            if (r.getPortName().equals(portName) && r.getChannel() == channel) {
                return t;
            }
        }
        return null;
    }


    @Override
    public List<Task> listSerialTask(String portName, Task.State... states) {
        List<Task> tasks = new ArrayList<>();

        for (Task t : listTask(states)) {
            if (t.getType() == Task.Type.ON_OFF) {
                OnOffRequest r = (OnOffRequest) t.getRequest();
                if (r.getPortName().equals(portName)) {
                    tasks.add(t);
                }
            }
        }
        return tasks;  //
    }

    @Override
    public void setOnOffTaskInfo(String taskId, Date begin, Date end, long total, int onSpace, int offSpace) {
        Task task = getTask(taskId);

        OnOffRequest request = (OnOffRequest) task.getRequest();
        request.setOffSpace(offSpace);
        request.setOnSpace(onSpace);
        jdbcTemplate.update("UPDATE TASKS set begin_=?,end_=?,total=?,request=? WHERE id=?", begin, end, total, jsonHelper.object2json(request), taskId);
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
    public List<Task> listRunnableTask() {
        return jdbcTemplate.query("SELECT * FROM TASK WHERE nextRun>=? AND state=?", mapperTask(), new Date(), Task.State.SUBMIT.name());  //
    }

    @Override
    public List<Task> listTask(Task.State... states) {

        String sql = "SELECT * FROM TASKS WHERE ";
        String[] ss = new String[states.length];
        for (int i = 0; i < states.length; i++) {
            if (i > 0) {
                sql += " OR ";
            }
            sql += " state=? ";
            ss[i] = states[i].name();
        }
        return jdbcTemplate.query(sql, mapperTask(), ss);
    }

    @Override
    public List<Task> listAvailableTask(Task.Type... types) {
        String[] ss = new String[types.length + 1];
        String sql = "SELECT * FROM TASKS WHERE (";
        for (int i = 0; i < types.length; i++) {
            ss[i] = types[i].name();
            if (i > 0) {
                sql += " OR ";
            }
            sql += " type_=? ";
        }
        sql += ") AND state!=?";
        ss[types.length] = Task.State.DELETE.name();

        return jdbcTemplate.query(sql, mapperTask(), ss);
    }


    @Override
    public void setTaskStartUp(String taskId, Date nextRun) {
        jdbcTemplate.update("UPDATE TASKS SET lastStartUp=?, nextRun=?, index=index+1 WHERE id=?", new Date(), nextRun, taskId);
    }

    @Override
    public void setTaskShutDown(String taskId, String lastStatus) {

        if (lastStatus == null) {
            jdbcTemplate.update("UPDATE TASKS SET   lastShutDown=? WHERE id=?", new Date(), taskId);
        } else {
            jdbcTemplate.update("UPDATE TASKS SET lastStatus=?, lastShutDown=? WHERE id=?", lastStatus, new Date(), taskId);
        }
    }

    @Override
    public void filterTask() {
        jdbcTemplate.update("UPDATE TASKS SET state=? WHERE state=? AND end_<=?", Task.State.DONE.name(), Task.State.SUBMIT.name(), new Date());
    }

    @Override
    public void addSerialItem(String id, String taskId, String request, String response, String reason) {
        jdbcTemplate.update("INSERT INTO SERIALS_ITEMS(id, task, type_, reason, request, response) VALUES(?,?,?)",
                id, taskId, reason == null ? Item.Type.SUCCESS.name() : Item.Type.FAIL.name(), reason, request, response);
    }

    @Override
    public void addCameraItem(String id, String taskId, String file, String reason) {
        jdbcTemplate.update("INSERT INTO CAMERA_ITEMS(task, type_, reason, file) VALUES(?,?)",
                id, taskId, reason == null ? Item.Type.SUCCESS.name() : Item.Type.FAIL.name(), reason, file);
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
        ProgressBar.get().set(50);
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
        ProgressBar.get().set(60);
        Map<String, String> map = new HashMap<>();
        map.put("LOGS", "id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY, " +
                "message VARCHAR(1024) NOT NULL, " +
                "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");

        map.put("ENV", "k VARCHAR(255) NOT NULL PRIMARY KEY, " +
                "v VARCHAR(8000) NOT NULL, " +
                "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "version BIGINT NOT NULL DEFAULT 0");
        map.put("CAMERA_ITEMS", "id CHAR(36) NOT NULL PRIMARY KEY, " +
                "task CHAR(36) , " +
                "type_ VARCHAR(255) NOT NULL, " +
                "reason VARCHAR(255) NOT NULL, " +
                "file VARCHAR(255) NOT NULL, " +
                "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");
        map.put("SERIAL_ITEMS", "id CHAR(36) NOT NULL PRIMARY KEY, " +
                "task CHAR(36) , " +
                "type_ VARCHAR(255) NOT NULL, " +
                "reason VARCHAR(255) NOT NULL, " +
                "request VARCHAR(255) NOT NULL, " +
                "response VARCHAR(255) , " +
                "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");

        map.put("TASKS", "id CHAR(36) NOT NULL PRIMARY KEY, " +
                "type_ VARCHAR(255) NOT NULL, " +
                "state VARCHAR(255) NOT NULL, " +
                "request VARCHAR(500) NOT NULL, " +
                "lastStatus VARCHAR(500), " +
                "begin_ TIMESTAMP NOT NULL, " +
                "end_ TIMESTAMP NOT NULL, " +
                "lastStartUp TIMESTAMP, " +
                "lastShutDown TIMESTAMP, " +
                "total BIGINT, " +
                "index BIGINT NOT NULL DEFAULT 0, " +
                "nextRun TIMESTAMP NOT NULL, " +
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

    private void addTask(String id, Task.Type type, Request request, String temp, Date begin, Date end, long total) {
        jdbcTemplate.update("INSERT INTO TASKS(id,type_,state,request,temp,begin_,end_,total,nextRun) VALUES(?,?,?,?,?,?,?,?,?)",
                id, type.name(), Task.State.SUBMIT.name(), jsonHelper.object2json(request), temp, begin, end, total, begin);
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
                item.setId(resultSet.getString("id"));
                item.setTask(resultSet.getString("task"));
                item.setType(Item.Type.valueOf(resultSet.getString("type_")));
                item.setReason(resultSet.getString("reason"));
                item.setFile(resultSet.getString("file"));
                item.setCreated(resultSet.getTimestamp("crated"));
                return item;  //
            }
        };
    }

    private RowMapper<SerialItem> mapperSerialItem() {
        return new RowMapper<SerialItem>() {
            @Override
            public SerialItem mapRow(ResultSet resultSet, int i) throws SQLException {
                SerialItem item = new SerialItem();
                item.setId(resultSet.getString("id"));
                item.setTask(resultSet.getString("task"));
                item.setType(Item.Type.valueOf(resultSet.getString("type_")));
                item.setReason(resultSet.getString("reason"));
                item.setRequest(resultSet.getString("request"));
                item.setResponse(resultSet.getString("response"));
                item.setCreated(resultSet.getTimestamp("created"));
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
                Task.Type type = Task.Type.valueOf(resultSet.getString("type_"));
                task.setType(type);
                task.setState(Task.State.valueOf(resultSet.getString("state")));
                String request = resultSet.getString("request");
                switch (type) {
                    case ON_OFF:
                        task.setRequest(jsonHelper.json2object(request, OnOffRequest.class));
                        break;
                    case PHOTO:
                        task.setRequest(jsonHelper.json2object(request, PhotoRequest.class));
                        break;
                    case VIDEO:
                        task.setRequest(jsonHelper.json2object(request, VideoRequest.class));
                        break;
                }
                task.setLastStatus(resultSet.getString("lastStatus"));
                task.setBegin(resultSet.getTimestamp("begin_"));
                task.setEnd(resultSet.getTimestamp("end_"));
                task.setLastStartUp(resultSet.getTime("lastStartUp"));
                task.setLastShutDown(resultSet.getTimestamp("lastShutDown"));
                task.setTotal(resultSet.getLong("total"));
                task.setIndex(resultSet.getLong("index"));
                task.setNextRun(resultSet.getDate("nextRun"));
                task.setCreated(resultSet.getTimestamp("created"));
                return task;  //
            }
        };
    }

    private String databaseProductName;
    private String databaseProductVersion;
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

    @Resource
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
}

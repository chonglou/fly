package com.odong.fly.job;

import com.odong.fly.MyException;
import com.odong.fly.model.Task;
import com.odong.fly.model.item.SerialItem;
import com.odong.fly.model.request.OnOffRequest;
import com.odong.fly.serial.Command;
import com.odong.fly.serial.OnOff;
import com.odong.fly.serial.SerialPort;
import com.odong.fly.serial.SerialUtil;
import com.odong.fly.service.StoreHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-9
 * Time: 上午11:18
 */
public class OnOffRunner implements Runnable {
    public OnOffRunner(String taskId, StoreHelper storeHelper, SerialUtil serialUtil) {
        this.taskId = taskId;
        this.storeHelper = storeHelper;
        this.serialUtil = serialUtil;
    }

    @Override
    public void run() {
        Task task = storeHelper.getTask(taskId);
        OnOffRequest oor = (OnOffRequest) task.getRequest();
        boolean on = Boolean.valueOf(task.getTemp());

        //判断时间间隔
        if (
                task.getLastStartUp() == null ||
                        new Date().getTime() >= task.getLastStartUp().getTime() + oor.offset(on)
                ) {

            storeHelper.setStartUp(task.getId());

            final Command command = new OnOff(oor.getChannel(), !on);
            String response = null;
            String reason = null;

            if (serialUtil.isOpen(oor.getPortName())) {
                try {
                    response = serialUtil.send(oor.getPortName(), command);
                    logger.debug("串口[{},{}]", command.toString(), response);
                } catch (Exception e) {
                    reason = e.getMessage() == null ? "NULL" : e.getMessage();
                    logger.error("执行任务[{}]出错", taskId, e);
                }

            } else {
                reason = MyException.Type.SERIAL_PORT_NOT_VALID.name();
            }

            storeHelper.addSerialItem(taskId, command.toString(), response, reason);
        }

    }

    private String taskId;
    private StoreHelper storeHelper;
    private SerialUtil serialUtil;
    private final static Logger logger = LoggerFactory.getLogger(OnOffRunner.class);
}

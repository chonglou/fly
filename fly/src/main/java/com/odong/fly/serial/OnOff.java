package com.odong.fly.serial;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-9
 * Time: 上午8:47
 */
public class OnOff implements Command {
    public OnOff(int channel, boolean flag) {
        this.channel = channel;
        this.flag = flag;
    }

    private static final long serialVersionUID = 6867109418353748407L;
    public final int channel;
    public final boolean flag;


    @Override
    public byte[] toBytes() {
        //FIXME 命令格式
        return toString().getBytes();
    }

    @Override
    public SerialPort.Type getType() {
        return SerialPort.Type.ON_OFF;
    }

    @Override
    public String toString() {
        return String.format("FF %02x %s", channel, flag ? "01" : "00");
    }

}

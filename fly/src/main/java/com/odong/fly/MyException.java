package com.odong.fly;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-8
 * Time: 下午9:41
 */
public class MyException extends Exception {
    private static final long serialVersionUID = 4798846834545312479L;
    private Type type;

    @Override
    public String getMessage() {
        return type.name();
    }

    public MyException(Type type) {
        super(type.name());
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        SERIAL_PORT_NOT_EXIST,
        SERIAL_PORT_IN_USE,
        SERIAL_CHANNEL_IN_USE,
        SERIAL_PORT_NOT_VALID,
        SERIAL_PORT_UNSUPPORTED,
        SERIAL_PORT_IO_ERROR,
    }

}

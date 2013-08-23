package com.odong.fly.model.request;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-22
 * Time: 下午8:58
 */
public class OnOffRequest extends Request {
    public long offset(boolean on){
        return 1000*(on?onSpace:offSpace);
    }
    @Deprecated
    public OnOffRequest() {
    }

    public OnOffRequest(String portName, int channel, int onSpace, int offSpace) {
        this.portName = portName;
        this.channel = channel;
        this.onSpace = onSpace;
        this.offSpace = offSpace;
    }

    private String portName;
    private int channel;
    private int onSpace;
    private int offSpace;
    private static final long serialVersionUID = 1675965204275811709L;

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getOnSpace() {
        return onSpace;
    }

    public void setOnSpace(int onSpace) {
        this.onSpace = onSpace;
    }

    public int getOffSpace() {
        return offSpace;
    }

    public void setOffSpace(int offSpace) {
        this.offSpace = offSpace;
    }
}

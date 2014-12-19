package edu.chalmers.lanchat;

/**
 * Created by Daniel on 12/19/2014.
 */
public class AdminMessage extends Message {
    public static enum MessageType {
        IP_NOTIFICATION;
    }

    private MessageType type;
    private String data;

    public AdminMessage(MessageType type, String data) {
        super(AdminMessage.class.getName());
        this.type = type;
        this.data = data;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

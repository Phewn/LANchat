package edu.chalmers.lanchat;

/**
 * Defines what an admin message looks like and what different kinds exists.
 */
public class AdminMessage extends Message {
    public static enum Type {
        IP_NOTIFICATION;
    }

    private Type type;
    private String data;

    public AdminMessage(Type type, String data) {
        super(AdminMessage.class.getName());
        this.type = type;
        this.data = data;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

package edu.chalmers.lanchat;

import java.util.Date;

/**
 * Created by Daniel on 12/19/2014.
 */
public class ChatMessage extends Message {

    private String name;
    private String message;

    public ChatMessage() {
        super(ChatMessage.class.getName());
    }

    public ChatMessage(String name, String message) {
        super(ChatMessage.class.getName());
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

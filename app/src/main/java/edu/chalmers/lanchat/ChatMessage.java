package edu.chalmers.lanchat;

import java.util.Date;

/**
 * Defines what an ordinary chat message looks like and what different kinds exists.
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

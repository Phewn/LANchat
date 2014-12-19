package edu.chalmers.lanchat;

import com.google.gson.Gson;

/**
 * Superclass for all messages. Defines a class name field which has to be set in order to
 * deserialize the message according to the subclass later.
 */
public class Message {

    // Stores the runtime class name
    public final String className;

    public Message(String className) {
        this.className = className;
    }

    /**
     * Convert the message to json using the gson library.
     * @return a json representation of the message.
     */
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}

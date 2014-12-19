package edu.chalmers.lanchat;

import com.google.gson.Gson;

/**
 * Created by Daniel on 12/19/2014.
 */
public class Message {

    public final String className;

    public Message(String className) {
        this.className = className;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}

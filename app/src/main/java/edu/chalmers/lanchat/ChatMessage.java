package edu.chalmers.lanchat;

import android.graphics.Color;

/**
 * Created by oliver on 14-12-17.
 */
public class ChatMessage {
    private String name;
    private String message;
    private int color;
    private float popularity = 0;

    public ChatMessage(int color){
        name = "Anonymous";
        message = "";
        this.color = color;
    }

    public ChatMessage(String name, int color){
        if (name != ""){
            this.name = name;
        }
        else {
            this.name = "Anonymous";
        }
        this.color = color;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public void setColor(int color){
        this.color = color;
    }

    public void setPopularity(float i){
        this.popularity = i;
    }

    public String getName(){
        return name;
    }

    public String getMessage(){
        return message;
    }

    public int getColor(){
        return color;
    }

    public float getPopularity(){
        return popularity;
    }
}

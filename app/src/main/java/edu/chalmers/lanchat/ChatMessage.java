package edu.chalmers.lanchat;

import android.graphics.Color;

public class ChatMessage extends Message {
    private String name;
    private String message;
    private int color = Color.WHITE;
    private float popularity = 1;
    private float stdTextSize = 14;
    private float textSize = 14;
    private float popMultiple = 5;

    public ChatMessage(int color){
        super(ChatMessage.class.getName());
        name = "Anonymous";
        message = "";
        this.color = color;
    }

    public ChatMessage(String message) {
        super(ChatMessage.class.getName());
        setName(null);
        setMessage(message);
    }

    public ChatMessage(String name, String message) {
        super(ChatMessage.class.getName());
        setName(name);
        setMessage(message);
    }

    public ChatMessage(String name, int color) {
        super(ChatMessage.class.getName());
        setName(name);
        setColor(color);
    }

    public void setName(String name){
        if (name == null || name == "") {
            this.name = "Anonymous: ";
        } else {
            this.name = name + ": ";
        }
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setColor(int color){
        this.color = color;
    }

    public void setPopularity(float i){
        this.popularity = i;
        textSize = (float) (stdTextSize + popMultiple*Math.log(i));
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

    public float getTextSize(){
        return textSize;
    }
}

package edu.chalmers.lanchat;

import android.graphics.Color;

public class ChatMessage extends Message {
    private String name;
    private String message;
    private int color = Color.YELLOW;
    private float popularity = 1;
    private static final float STD_TEXT_SIZE = 14;
    private float popMultiple = 5;
    private float textSize = 14;

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
        this.textSize = textSize;
        if( name == "" ){
            setName("Anonymous");
        } else {
            setName(name);
        }
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
        this.textSize = (float) (STD_TEXT_SIZE + popMultiple*Math.log(i));
    }

    public void incPopularity(int amount) {
        setPopularity(this.popularity + amount);
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

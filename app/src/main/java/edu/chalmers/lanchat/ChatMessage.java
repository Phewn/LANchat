package edu.chalmers.lanchat;


public class ChatMessage {
    private String name;
    private String message;
    private int color;
    private float popularity = 0;
    private float stdTextSize = 14;
    private float textSize = 14;
    private float popMultiple = 5;

    public ChatMessage(int color){
        name = "Anonymous";
        message = "";
        this.color = color;
    }

    public ChatMessage(String name, int color){
        if (name != ""){
            this.name = name + ": ";
        }
        else {
            this.name = "Anonymous: ";
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
        textSize = stdTextSize + (i*popMultiple);
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

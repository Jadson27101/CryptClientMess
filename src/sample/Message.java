package sample;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {

    private Integer id;
    private String name;
    private String message;
    private String clientIP;
    public Message(Integer id, String name, String message, String clientIP){
        this.id = id;
        this.name = name;
        this.message = message;
        this.clientIP = clientIP;
    }
    public Integer getId() {
        return id;
    }

    public String getClientIP(){
        return clientIP;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }
}


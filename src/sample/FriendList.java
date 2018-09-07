package sample;

import java.io.Serializable;

public class FriendList implements Serializable {
    private String IP;
    private String name;

    public FriendList(String IP, String name){
        this.IP = IP;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getIP() {
        return IP;
    }
}

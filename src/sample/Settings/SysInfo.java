package sample.Settings;

import java.io.Serializable;

public class SysInfo implements Serializable {
    private String IP;
    private String userName;
    private int port;

    public SysInfo(String IP, String userName, int port){
        this.IP = IP;
        this.userName = userName;
        this.port = port;
    }
    public SysInfo(){

    }
    public String getIP(){
        return IP;
    }

    public String getUserName(){
        return userName;
    }

    public int getPort(){
        return port;
    }
}

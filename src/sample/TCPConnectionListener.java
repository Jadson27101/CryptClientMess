package sample;

import sample.connectTCP;

public interface TCPConnectionListener {

    void onConnectionReady(connectTCP connectTCP);
    void onReceiveString(connectTCP connectTCP, Message value);
    void onDisconnect(connectTCP connectTCP);
    void onException(connectTCP connectTCP, Exception e);
    void onSysConnect(connectTCP connectTCP, String value);
}

package sample;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class connectTCP {
    private final Socket socket;
    private final Thread rxThread;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final TCPConnectionListener eventListener;
    private DataOutputStream dos;
    public static String nameOfFriend;
    public connectTCP(TCPConnectionListener eventListener, String IP, int port) throws IOException{
        this(eventListener, new Socket(IP, port));
    }

    public connectTCP(TCPConnectionListener eventListener, Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        dos = new DataOutputStream(socket.getOutputStream());
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.onConnectionReady(connectTCP.this);
                    while (!rxThread.isInterrupted()){
                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                            Message m = (Message) ois.readObject();
                            writeToBinaryFile(m);
                            eventListener.onReceiveString(connectTCP.this, m);
                    }
                } catch (IOException e) {
                    eventListener.onException(connectTCP.this, e);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    eventListener.onDisconnect(connectTCP.this);
                }
            }
        });
        rxThread.start();
    }

    public synchronized void sendMessage(Message message){
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void writeToBinaryFile(Message message) throws IOException {
        File file = new File("Res/Chats/" + nameOfFriend +".bin");

        FileOutputStream fos = null;
        ObjectOutputStream ois = null;

        if (!file.exists()) {
            file.createNewFile();
             fos = new FileOutputStream(file);
             ois = new ObjectOutputStream(fos);

        }else{
            fos = new FileOutputStream(file,true);
            ois = new AppendingObjectOutputStream(fos);
        }
            ois.writeObject(message);
        ois.flush();
        ois.close();
        fos.close();
    }

    public synchronized void disconnect(){
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(connectTCP.this, e);
        }
    }
    @Override
    public String toString(){
       return "TCPConnection: " + socket.getInetAddress() + " " + socket.getPort();
    }
}

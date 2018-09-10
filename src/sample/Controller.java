package sample;

/**
 * Sample Skeleton for "sample.fxml" Controller Class
 * You can copy and paste this code into your favorite IDE
 **/


import java.io.*;
import java.util.*;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import sample.Settings.SysInfo;


public class Controller implements TCPConnectionListener {
    @FXML // fx:id="btn"
    private Button connect_btn; // Value injected by FXMLLoader
    @FXML
    private Button send_btn;
    @FXML
    private TextArea connect_status_bar;
    @FXML
    private TextArea chat_area;
    @FXML
    private TextField message_field;
    @FXML
    private MenuItem create_chat;
    @FXML
    private MenuItem system_settings;
    @FXML
    private ListView<String> friend_list;
    @FXML
    private MenuItem aboutAuthor;
    @FXML // This method is called by the FXMLLoader when initialization is complete
    public connectTCP connection;
    public static ArrayList<FriendList> friendList = new ArrayList<>();
    private Thread updateFriendListThread;
    private String userName;

    public void initialize() {

        chat_area.setEditable(false);
        connect_status_bar.setEditable(false);
        chat_area.textProperty().addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> {
            chat_area.setScrollTop(Double.MAX_VALUE);
        });

        connect_status_bar.textProperty().addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> {
            connect_status_bar.setScrollTop(Double.MAX_VALUE);
        });

        connect_btn.setOnAction(event -> {
            try {
                File f = new File("Res/SysInfo.bin");
                SysInfo sysInfo = readSysInfoFromFile(f);
                System.out.println(sysInfo.getIP() + ": " + sysInfo.getPort());
                userName = sysInfo.getUserName();
                connection = new connectTCP(this, sysInfo.getIP(), sysInfo.getPort());
            } catch (IOException e) {
                printSysMsg("Connection exception" + e);
            }

        });
        send_btn.setOnAction(event -> {
            String msg = message_field.getText();
            if (connectTCP.nameOfFriend == null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("Please, enter friend!");

                alert.showAndWait();
            }else{
            for (int i = 0; i < friendList.size(); i++) {
                if (friendList.get(i).getName().equals(connectTCP.nameOfFriend)) {
                    Message m = new Message(15, userName, message_field.getText(), "/" + friendList.get(i).getIP());
                    if (msg.equals("")) return;
                    message_field.setText(null);
                    connection.sendMessage(m);
                }
            }
            }
        });

        create_chat.setOnAction(event -> {
            try {
                FXMLDocumentController("/fxml_resources/add_friend_frame.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        system_settings.setOnAction(event -> {
            try {
                FXMLDocumentController("/fxml_resources/settings.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        friend_list.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            connectTCP.nameOfFriend = friend_list.getSelectionModel().getSelectedItem();
            File f = new File("Res/Chats/" + connectTCP.nameOfFriend + ".bin");
            chat_area.clear();
            if (f.exists()) {
                ArrayList<Message> messages = readMessageFromFile(f);
                if (!messages.isEmpty()) {
                    for (int i = 0; i < messages.size(); i++) {
                        printMsg(messages.get(i));
                    }
                }
            }
        });
        aboutAuthor.setOnAction(event -> {
            try {
                FXMLDocumentController("/fxml_resources/about.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        updateFriendListThread = new Thread(() -> {
            File f = new File("Res/friendList.bin");
            if (f.exists()) {
                while (true) {
                    try {
                        updateFriendList(f);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        updateFriendListThread.setDaemon(true);
        updateFriendListThread.start();
    }
    //open new Scene
    private void FXMLDocumentController(String path) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource(path));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public synchronized void updateFriendList(File file) {
        ArrayList<FriendList> fl = readFriendListFromFile(file);
        if (friendList.size() != fl.size() && !friendList.containsAll(fl)) {
            if (friendList.isEmpty()) {
                friendList = new ArrayList<>(fl);
                for (int i = 0; i < friendList.size(); i++) {
                    friend_list.getItems().add(friendList.get(i).getName());

                }
            } else {
                friendList = new ArrayList<>(fl);
                friend_list.getItems().add(friendList.get(friendList.size() - 1).getName());
            }
        }
    }
    //Read object Message from bin File
    private synchronized ArrayList<Message> readMessageFromFile(File file) {
        ArrayList<Message> m = new ArrayList<>();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            while (true) {
                try {
                    m.add((Message) objectInputStream.readObject());
                } catch (EOFException e) {
                    break;
                }
            }
            fileInputStream.close();
            objectInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //System.out.println(m.get(0).getIP());
        return m;
    }

    //Read object SysInfo from bin File
    private SysInfo readSysInfoFromFile(File file) {
        SysInfo sysInfo = new SysInfo();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            while (true) {
                try {
                    sysInfo = (SysInfo) objectInputStream.readObject();
                } catch (EOFException e) {
                    break;
                }
            }
            fileInputStream.close();
            objectInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //System.out.println(m.get(0).getIP());
        return sysInfo;
    }

    //read object Friendlist from file
    private ArrayList<FriendList> readFriendListFromFile(File file) {
        ArrayList<FriendList> m = new ArrayList<>();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            while (true) {
                try {
                    m.add((FriendList) objectInputStream.readObject());
                } catch (EOFException e) {
                    break;
                }
            }
            fileInputStream.close();
            objectInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return m;
    }


    @Override
    public void onConnectionReady(connectTCP connectTCP) {
        connectStatus("Connection ready..");
    }

    @Override
    public void onReceiveString(connectTCP connectTCP, Message value) {
        printMsg(value);
    }

    @Override
    public void onDisconnect(connectTCP connectTCP) {
        connectStatus("Connection close..");
    }

    @Override
    public void onException(connectTCP connectTCP, Exception e) {
        printSysMsg("Connection exception" + e);
    }

    @Override
    public void onSysConnect(connectTCP connectTCP, String value) {
        printSysMsg(value);
    }

    private synchronized void printMsg(Message msg) {
        chat_area.appendText(msg.getName() + ": " + msg.getMessage() + "\n");
    }

    private synchronized void connectStatus(String msg) {
        connect_status_bar.appendText(msg + "\n");
    }

    private synchronized void printSysMsg(String msg) {
        chat_area.appendText(msg + "\n");
    }
}


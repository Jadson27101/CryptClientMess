package sample.AddFriend;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.*;

import java.io.*;


public class AddFriendController implements closeFrame {
    @FXML
    Button cancel;
    @FXML
    Button save;
    @FXML
    TextField enter_name;
    @FXML
    TextField enter_IP;

    public void initialize() {
        save.setOnAction(event -> {
            try {
                writeToBinaryFile(new FriendList(enter_IP.getText(), enter_name.getText()));
                enter_name.clear();
                enter_IP.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
       });
        cancel.setOnAction(event -> {
            closeScene(cancel);
        });

    }
    private void writeToBinaryFile(FriendList friendList) throws IOException { ;
        File file = new File("Res/friendList.bin");

        FileOutputStream fos;
        ObjectOutputStream ois;

        if (!file.exists()) {
            file.createNewFile();
            fos = new FileOutputStream(file);
            ois = new ObjectOutputStream(fos);

        }else{
            fos = new FileOutputStream(file,true);
            ois = new AppendingObjectOutputStream(fos);
        }
        ois.writeObject(friendList);
        ois.flush();
        fos.close();
        ois.close();
    }

    @Override
    public void closeScene(Button button) {
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }
}

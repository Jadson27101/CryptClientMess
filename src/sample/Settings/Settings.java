package sample.Settings;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.AppendingObjectOutputStream;
import sample.closeFrame;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


public class Settings implements closeFrame {
    @FXML
    TextField port;
    @FXML
    TextField user_name;
    @FXML
    TextField ip_field;
    @FXML
    Button cancel;
    @FXML
    Button save;
    public void initialize() {
        save.setOnAction(event -> {
            int int_port = Integer.parseInt(port.getText());
            try {
                writeToBinaryFile(new SysInfo(ip_field.getText(), user_name.getText(), int_port));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        cancel.setOnAction(event -> {
            closeScene(cancel);
        });
    }
    private void writeToBinaryFile(SysInfo friendList) throws IOException {
        File file = new File("Res/SysInfo.bin");

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
        //ois.writeObject(friendList1);
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

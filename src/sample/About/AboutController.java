package sample.About;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import sample.closeFrame;

public class AboutController implements closeFrame {
    @FXML Button cancel;
    public void initialize() {
        cancel.setOnAction(event -> {
            closeScene(cancel);
        });
    }
    @Override
    public void closeScene(Button button) {
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }
}

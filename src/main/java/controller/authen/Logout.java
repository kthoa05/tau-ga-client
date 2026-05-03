package controller.authen;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Logout {

    @FXML
    private Button yesButton;

    @FXML
    private Button noButton;

    @FXML
    void handleLogout(ActionEvent event) {
        System.out.println("Đăng xuất...");
        Stage stage = (Stage) yesButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void handleCancel(ActionEvent event) {
        Stage stage = (Stage) noButton.getScene().getWindow();
        stage.close();
    }
}

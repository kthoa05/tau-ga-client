package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.QuetQrUtils;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/tauga/view/loading/Loading.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hệ thống đặt vé tàu");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void init() {
        try {
            QuetQrUtils.start(
                    8000,
                    "/Users/lethoa/Documents/keystore.p12",
                    "password123"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
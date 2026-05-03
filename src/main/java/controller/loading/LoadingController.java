package controller.loading;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.util.Objects;

public class LoadingController {

    @FXML
    private AnchorPane root;        // ⬅️ thêm root để thao tác layout

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label lblPercent;

    @FXML
    private ImageView imgLoading;

    private double progress = 0;

    @FXML
    public void initialize() {
        progressBar.setStyle("-fx-accent: #52CCF6;");
        Image image = new Image(
                Objects.requireNonNull(
                        getClass().getResource("/tauga/img/loading/loading.jpg")
                ).toExternalForm()
        );
        imgLoading.setImage(image);
        imgLoading.setPreserveRatio(true);
        imgLoading.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                imgLoading.fitWidthProperty().bind(newScene.widthProperty());
                imgLoading.fitHeightProperty().bind(newScene.heightProperty());
            }
        });

        startLoading();
    }

    private void startLoading() {
        Timeline timeline = new Timeline();

        KeyFrame keyFrame = new KeyFrame(Duration.millis(50), e -> {
            progress += 0.01;
            progressBar.setProgress(progress);
            lblPercent.setText((int) (progress * 100) + "%");

            if (progress >= 1) {
                timeline.stop();
                openLogin();
            }
        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void openLogin() {
        try {
            Parent rootLogin = FXMLLoader.load(
                    Objects.requireNonNull(
                            getClass().getResource("/tauga/view/authen/DangNhap.fxml")
                    )
            );

            AnchorPane currentRoot =
                    (AnchorPane) progressBar.getScene().getRoot();

            currentRoot.getChildren().setAll(rootLogin);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.indi.llk.Interface_Controller;

import com.indi.llk.Window_Controller.AboutWindowManager;
import com.indi.llk.Game_Core_Controller.GameSettings;
import com.indi.llk.Window_Controller.SettingsWindowManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class Initial_Interface {
    @FXML
    private Label LinkGameText;
    @FXML
    private AnchorPane ID_GROUND;
    @FXML
    private Button IDC_BTN_BASIC;
    @FXML
    private Button IDC_BTN_RELAX;
    @FXML
    private Button IDC_BTN_LEVEL;
    @FXML
    private Button IDC_BTN_SETTINGS;
    @FXML
    private Button IDC_BTN_ABOUT;

    private GameSettings settings = GameSettings.getInstance();
    private MediaPlayer mediaPlayer;

    @FXML
    public void initialize() {
        try {
            // 设置背景图片
            String imageUrl = getClass().getResource("/com/indi/llk/image/LianLianKan.png").toExternalForm();
            ID_GROUND.setStyle("-fx-background-image: url('" + imageUrl + "'); " +
                    "-fx-background-size: cover; " +
                    "-fx-background-repeat: no-repeat;");

            // 加载CSS样式
            ID_GROUND.getStylesheets().add(
                    getClass().getResource("/com/indi/llk/styles.css").toExternalForm()
            );

            // 设置按钮点击事件
            setupButton(IDC_BTN_BASIC, "/com/indi/llk/normal-game-view.fxml", false);
            setupButton(IDC_BTN_RELAX, "/com/indi/llk/casual-game-view.fxml", false);
            setupButton(IDC_BTN_LEVEL, "/com/indi/llk/level-game-view.fxml", false);
            setupButton(IDC_BTN_SETTINGS, "/com/indi/llk/settings-view.fxml", true);
            IDC_BTN_ABOUT.setOnAction(actionEvent -> {
                AboutWindowManager.getInstance().showAboutWindow(ID_GROUND.getScene().getWindow());
            });

        } catch (Exception e) {
            System.err.println("Failed to load resources: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void setupButton(Button button, String fxmlPath, boolean isModal) {
        button.setOnAction(e -> handleButtonClick(fxmlPath, isModal));
    }

    private void handleButtonClick(String fxmlPath, boolean isModal) {
        try {
            if(isModal){
                SettingsWindowManager.getInstance().showSettingsWindow(ID_GROUND.getScene().getWindow());
            }else
            {// 其他窗口的处理保持不变
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
                Scene scene = new Scene(fxmlLoader.load());
                Stage mainStage = (Stage) ID_GROUND.getScene().getWindow();
                mainStage.setScene(scene);
                mainStage.setResizable(false);

                if (mainStage.getIcons().isEmpty()) {
                    Image icon = new Image(getClass().getResourceAsStream("/com/indi/llk/image/icon.png"));
                    mainStage.getIcons().add(icon);
                }
            }
        } catch (Exception e) {
            System.err.println("Error handling button click: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
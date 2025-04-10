package com.indi.llk.Window_Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class About_Controller {
    @FXML
    private ImageView gameIcon;
    @FXML
    private Button closeButton;

    @FXML
    public void initialize() {
        try {
            Image icon = new Image(getClass().getResourceAsStream("/com/indi/llk/image/icon_about.png"));
            gameIcon.setImage(icon);
        } catch (Exception e) {
            System.err.println("Failed to load icon: " + e.getMessage());
        }
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}

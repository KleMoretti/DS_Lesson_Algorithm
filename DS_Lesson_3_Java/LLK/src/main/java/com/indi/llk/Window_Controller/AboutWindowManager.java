package com.indi.llk.Window_Controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class AboutWindowManager {
    private static AboutWindowManager instance;

    private AboutWindowManager() {}

    public static AboutWindowManager getInstance() {
        if (instance == null) {
            instance = new AboutWindowManager();
        }
        return instance;
    }

    public void showAboutWindow(Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/indi/llk/about-view.fxml"));
            Stage aboutStage = new Stage();
            aboutStage.setScene(new Scene(loader.load()));
            aboutStage.setTitle("About");
            aboutStage.setResizable(false);

            // Make the about window modal
            aboutStage.initModality(Modality.WINDOW_MODAL);
            aboutStage.initOwner(owner);

            // Set window icon
            Image icon = new Image(getClass().getResourceAsStream("/com/indi/llk/image/icon.png"));
            aboutStage.getIcons().add(icon);

            aboutStage.showAndWait();
        } catch (Exception e) {
            System.err.println("Error loading about view: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
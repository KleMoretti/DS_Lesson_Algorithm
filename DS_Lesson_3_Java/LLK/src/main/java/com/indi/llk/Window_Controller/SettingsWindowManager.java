package com.indi.llk.Window_Controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class SettingsWindowManager {
    private static SettingsWindowManager instance;

    private SettingsWindowManager() {}

    public static SettingsWindowManager getInstance() {
        if (instance == null) {
            instance = new SettingsWindowManager();
        }
        return instance;
    }

    public void showSettingsWindow(Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/indi/llk/settings-view.fxml"));
            Stage settingsStage = new Stage();
            settingsStage.setScene(new Scene(loader.load()));
            settingsStage.setTitle("Settings");
            settingsStage.setResizable(false);

            // Make the settings window modal
            settingsStage.initModality(Modality.WINDOW_MODAL);
            settingsStage.initOwner(owner);

            // Set window icon
            Image icon = new Image(getClass().getResourceAsStream("/com/indi/llk/image/icon.png"));
            settingsStage.getIcons().add(icon);

            settingsStage.showAndWait();
        } catch (Exception e) {
            System.err.println("Error loading settings view: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
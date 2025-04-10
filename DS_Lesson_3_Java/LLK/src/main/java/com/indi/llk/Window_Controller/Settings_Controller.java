package com.indi.llk.Window_Controller;

import com.indi.llk.Game_Core_Controller.GameSettings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class Settings_Controller {
    @FXML
    private CheckBox bgmCheckBox;
    @FXML
    private CheckBox sfxCheckBox;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Label volumeLabel;
    @FXML
    private ComboBox<String> difficultyComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private GameSettings settings;
    private double initialVolume;
    private boolean initialBgmEnabled;
    private boolean initialSfxEnabled;

    @FXML
    public void initialize() {
        settings = GameSettings.getInstance();

        // Initialize controls with current settings
        volumeSlider.setValue(settings.getVolume());
        bgmCheckBox.setSelected(settings.isBgmEnabled());
        sfxCheckBox.setSelected(settings.isSfxEnabled());

        // Store initial values for cancel operation
        initialVolume = settings.getVolume();
        initialBgmEnabled = settings.isBgmEnabled();
        initialSfxEnabled = settings.isSfxEnabled();

        // Setup volume slider listener
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int percentage = (int) (newVal.doubleValue() * 100);
            volumeLabel.setText(percentage + "%");
        });

        difficultyComboBox.getItems().addAll("Easy", "Normal", "Hard");
        difficultyComboBox.setValue("Normal");
    }

    @FXML
    private void saveSettings() {
        settings.setVolume(volumeSlider.getValue());
        settings.setBgmEnabled(bgmCheckBox.isSelected());
        settings.setSfxEnabled(sfxCheckBox.isSelected());
        closeWindow();
    }

    @FXML
    private void closeWindow() {

        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
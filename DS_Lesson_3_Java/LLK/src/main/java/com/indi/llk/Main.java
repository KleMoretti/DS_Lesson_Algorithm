package com.indi.llk;

import com.indi.llk.Game_Core_Controller.GameSettings;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private static MediaPlayer backgroundMusic;

    @Override
    public void start(Stage stage) throws IOException {
        initializeBackgroundMusic();

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("initial-interface.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 600);
        Image icon = new Image(getClass().getResourceAsStream("/com/indi/llk/image/icon.png"));
        stage.getIcons().add(icon);


        stage.setResizable(false);
        stage.setTitle("Link Game");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(event -> {
            if (backgroundMusic != null) {
                backgroundMusic.stop();
            }
        });
    }

    private void initializeBackgroundMusic() {
        try {
            Media sound = new Media(getClass().getResource("/com/indi/llk/Audio/background_audio.mp3").toExternalForm());
            backgroundMusic = new MediaPlayer(sound);

            backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);

            GameSettings settings = GameSettings.getInstance();
            backgroundMusic.volumeProperty().bind(settings.volumeProperty());

            if (settings.isBgmEnabled()) {
                backgroundMusic.play();
            }

            settings.bgmEnabledProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    backgroundMusic.play();
                } else {
                    backgroundMusic.pause();
                }
            });
        } catch (Exception e) {
            System.err.println("Error loading background music: " + e.getMessage());
        }
    }

    public static MediaPlayer getBackgroundMusic() {
        return backgroundMusic;
    }

    public static void main(String[] args) {
        launch();
    }
}
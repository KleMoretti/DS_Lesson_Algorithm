package com.indi.llk.Interface_Controller;

import com.indi.llk.Game_Core_Controller.GameBoard;
import com.indi.llk.Game_Core_Controller.GameTimer;
import com.indi.llk.Window_Controller.HelpWindowManager;
import com.indi.llk.Window_Controller.SettingsWindowManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;

public class Normal_Mode_Controller {
    @FXML
    private Button IDC_BTN_TIPS ;
    @FXML
    private AnchorPane ID_NORMAL_GAME_GROUND;
    @FXML
    private Button IDC_BTN_SETTINGS;
    @FXML
    private Button IDC_BTN_START;
    @FXML
    private Button IDC_BTN_PAUSE;
    @FXML
    private Button IDC_BTN_HELP;
    @FXML
    private Button IDC_BTN_RETURN;
    @FXML
    private Button IDC_BTN_RESET;


    private GameBoard gameBoard;
    private boolean isGameStarted = false;
    private GameTimer gameTimer;
    private Label timerLabel;

    @FXML
    public void initialize() {
        System.out.println("Normal Mode Controller Initialized");
        try {
            String imageUrl = getClass().getResource("/com/indi/llk/image/Game_Background.png").toExternalForm();
            ID_NORMAL_GAME_GROUND.setStyle("-fx-background-image: url('" + imageUrl + "'); " +
                    "-fx-background-size: contain; " +
                    "-fx-background-repeat: no-repeat;");

            createTimerLabel();

            // 设置按钮点击事件
            IDC_BTN_SETTINGS.setOnAction(e ->
                    SettingsWindowManager.getInstance().showSettingsWindow(
                            ID_NORMAL_GAME_GROUND.getScene().getWindow()
                    )
            );

            IDC_BTN_HELP.setOnAction(e -> {
                HelpWindowManager.getInstance().showHelpWindow(
                        ID_NORMAL_GAME_GROUND.getScene().getWindow());
            });

            IDC_BTN_RETURN.setOnAction(e->{
                returnInitGame();
            });

            IDC_BTN_START.setOnAction(e -> startGame());

            IDC_BTN_PAUSE.setOnAction(e -> togglePause());
            IDC_BTN_PAUSE.setDisable(true);

            IDC_BTN_TIPS.setOnAction(e -> showHint());
            IDC_BTN_TIPS.setDisable(true);

            IDC_BTN_RESET.setOnAction(e->startGame());

        } catch (Exception e) {
            System.err.println("Failed to load background image: " + e.getMessage());
        }
    }

    private void createTimerLabel() {
        timerLabel = new Label("00:00");
        timerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        timerLabel.setTextFill(Color.WHITE);
        timerLabel.setStyle("-fx-background-color: rgba(0,0,0,0.4); -fx-padding: 5px 10px; -fx-background-radius: 5px;");
        timerLabel.setAlignment(Pos.CENTER);
        timerLabel.setPrefWidth(80);

        AnchorPane.setTopAnchor(timerLabel, 40.0);
        AnchorPane.setRightAnchor(timerLabel, 450.0);

        // Initially hidden
        timerLabel.setVisible(false);

        // Add to the game area
        ID_NORMAL_GAME_GROUND.getChildren().add(timerLabel);

        // Initialize the timer
        gameTimer = new GameTimer(timerLabel);
    }

    private void startGame() {
        if (!isGameStarted || gameBoard == null) {
            gameBoard = new GameBoard(ID_NORMAL_GAME_GROUND);
            gameBoard.initializeBoard(8,8);
            gameBoard.generatePuzzle();

            // 设置游戏完成监听器
            gameBoard.setGameCompleteListener(() -> {
                gameTimer.pause(); // 停止计时器
                IDC_BTN_TIPS.setDisable(true);
            });

            IDC_BTN_START.setDisable(true);
            IDC_BTN_PAUSE.setDisable(false);
            IDC_BTN_TIPS.setDisable(false);
            isGameStarted = true;

            startTimer();
        }
        else{
            gameBoard.resetBoard();
            gameBoard.generatePuzzle();

            resetTimer();
        }
    }

    private void returnInitGame(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/indi/llk/initial-interface.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ID_NORMAL_GAME_GROUND.getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            if (stage.getIcons().isEmpty()) {
                Image icon = new Image(getClass().getResourceAsStream("/com/indi/llk/image/icon.png"));
                stage.getIcons().add(icon);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void togglePause() {
        if (isGameStarted && gameBoard != null) {
            if (gameBoard.isPaused()) {
                gameBoard.resumeGame();
                IDC_BTN_PAUSE.setText("暂停");
                resumeTimer();
            } else {
                gameBoard.pauseGame();
                IDC_BTN_PAUSE.setText("继续");
                pauseTimer();
            }
        }
    }

    private void showHint() {
        if (isGameStarted && gameBoard != null ) {
            gameBoard.findMatchingPair();
        }
    }

    private void startTimer() {
        timerLabel.setVisible(true);
        gameTimer.reset();
        gameTimer.start();
    }

    private void pauseTimer() {
        gameTimer.pause();
    }

    private void resumeTimer() {
        gameTimer.resume();
    }

    private void resetTimer() {
        gameTimer.reset();
        timerLabel.setVisible(true);
        gameTimer.start();
    }
}
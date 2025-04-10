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

public class Level_Mode_Controller {
    @FXML
    private Button IDC_BTN_TIPS;
    @FXML
    private AnchorPane ID_LEVEL_GAME_GROUND;
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

    private GameTimer gameTimer;
    private boolean isGameStarted = false;
    private Label timerLabel;
    private Label scoreLabel;
    private Label levelLabel;

    private int currentLevel = 1;
    private int currentScore = 0;
    private int timeLimit = 300;

    public void initialize() {
        System.out.println("Level Mode Controller Initialized");
        try {
            String imageUrl = getClass().getResource("/com/indi/llk/image/Game_Background.png").toExternalForm();
            ID_LEVEL_GAME_GROUND.setStyle("-fx-background-image: url('" + imageUrl + "'); " +
                    "-fx-background-size: contain; " +
                    "-fx-background-repeat: no-repeat;");

            createTimerLabel();
            createScoreLabel();
            createLevelLabel();

            // 设置按钮点击事件
            IDC_BTN_SETTINGS.setOnAction(e ->
                    SettingsWindowManager.getInstance().showSettingsWindow(
                            ID_LEVEL_GAME_GROUND.getScene().getWindow()
                    )
            );

            IDC_BTN_HELP.setOnAction(e -> {
                HelpWindowManager.getInstance().showHelpWindow(
                        ID_LEVEL_GAME_GROUND.getScene().getWindow());
            });

            IDC_BTN_RETURN.setOnAction(e -> {
                returnInitGame();
            });

            IDC_BTN_START.setOnAction(e -> startGame());

            IDC_BTN_PAUSE.setOnAction(e -> togglePause());
            IDC_BTN_PAUSE.setDisable(true);

            IDC_BTN_TIPS.setOnAction(e -> showHint());
            IDC_BTN_TIPS.setDisable(true);

            IDC_BTN_RESET.setOnAction(e -> resetCurrentLevel());

        } catch (Exception e) {
            System.err.println("Failed to load resources: " + e.getMessage());
            e.printStackTrace();
        }



    }

    private void createLevelLabel() {
        levelLabel = new Label("Level: " + currentLevel);
        levelLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        levelLabel.setTextFill(Color.WHITE);
        levelLabel.setStyle("-fx-background-color: rgba(0,0,0,0.4); -fx-padding: 5px 10px; -fx-background-radius: 5px;");

        AnchorPane.setTopAnchor(levelLabel, 80.0);
        AnchorPane.setRightAnchor(levelLabel, 450.0);

        ID_LEVEL_GAME_GROUND.getChildren().add(levelLabel);

    }

    private void createScoreLabel() {
        scoreLabel = new Label("Score: " + currentScore);
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setStyle("-fx-background-color: rgba(0,0,0,0.4); -fx-padding: 5px 10px; -fx-background-radius: 5px;");
        scoreLabel.setAlignment(Pos.CENTER);
        scoreLabel.setPrefWidth(150);

        AnchorPane.setTopAnchor(scoreLabel, 80.0);
        AnchorPane.setLeftAnchor(scoreLabel, 400.0);

        ID_LEVEL_GAME_GROUND.getChildren().add(scoreLabel);

    }

    private void createTimerLabel() {
        timerLabel = new Label(String.format("%02d:%02d", timeLimit / 60, timeLimit % 60));
        timerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        timerLabel.setTextFill(Color.WHITE);
        timerLabel.setStyle("-fx-background-color: rgba(0,0,0,0.4); -fx-padding: 5px 10px; -fx-background-radius: 5px;");
        timerLabel.setAlignment(Pos.CENTER);
        timerLabel.setPrefWidth(100);

        AnchorPane.setTopAnchor(timerLabel, 40.0);
        AnchorPane.setRightAnchor(timerLabel, 450.0);

        timerLabel.setVisible(true);

        ID_LEVEL_GAME_GROUND.getChildren().add(timerLabel);

        gameTimer = new GameTimer(timerLabel);
    }

    private void startGame() {
        if (!isGameStarted || gameBoard == null) {
            initializeGameForLevel(currentLevel);

            IDC_BTN_START.setDisable(true);
            IDC_BTN_PAUSE.setDisable(false);
            IDC_BTN_TIPS.setDisable(false);
            isGameStarted = true;

            startTimer();
        } else {
            resetCurrentLevel();
        }
    }

    private void initializeGameForLevel(int level) {
        // 根据关卡设置游戏难度
        int rows = 6 + (level / 2);
        int cols = 6 + (level / 2);
        rows = Math.min(rows, 12); // 限制最大尺寸
        cols = Math.min(cols, 16);

        // 调整时间限制
        timeLimit = 400 - (level * 5);
        timeLimit = Math.max(timeLimit, 60); // 最少60秒

        // 每次进入新关卡时重新创建游戏板，避免状态残留
        if (gameBoard != null) {
            // 移除旧游戏板的所有组件
            gameBoard.clearBoard();
        } else {
            gameBoard = new GameBoard(ID_LEVEL_GAME_GROUND);
        }

        // 可以在这里调整游戏板尺寸和配置
        gameBoard.initializeBoard(rows, cols);
        gameBoard.generatePuzzle();

        // 设置游戏完成监听器
        gameBoard.setGameCompleteListener(() -> {
            endLevel(true); // 成功通关
        });

        // 更新关卡显示
        levelLabel.setText("关卡: " + currentLevel);

        // 重置计时器设置
        gameTimer.setCountdownMode(true, timeLimit, () -> {
            endLevel(false);
        });
    }

    private void endLevel(boolean success) {
        gameTimer.pause(); // 停止计时器
        IDC_BTN_TIPS.setDisable(true);

        if (success) {
            // 计算得分：剩余时间 × 10 + 基础分
            int remainingTime = gameTimer.getRemainingSeconds();
            int timeBonus = remainingTime * 10;
            int levelBonus = currentLevel * 100;
            int totalScore = timeBonus + levelBonus;

            currentScore += totalScore;
            scoreLabel.setText("分数: " + currentScore);

            // 显示过关信息
            showLevelCompleteDialog(totalScore);

            // 进入下一关
            currentLevel++;
            IDC_BTN_START.setText("下一关");
            IDC_BTN_START.setDisable(false);
        } else {
            // 失败，显示失败对话框
            showLevelFailedDialog();
            IDC_BTN_START.setText("重试");
            IDC_BTN_START.setDisable(false);
        }
    }


    private void showLevelCompleteDialog(int score) {
        Label messageLabel = new Label("恭喜通关！\n得分: " + score + "\n点击'下一关'继续");
        messageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        messageLabel.setTextFill(Color.GOLD);
        messageLabel.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-padding: 20px; -fx-background-radius: 10px;");

        AnchorPane.setTopAnchor(messageLabel, 200.0);
        AnchorPane.setLeftAnchor(messageLabel, 200.0);

        ID_LEVEL_GAME_GROUND.getChildren().add(messageLabel);

        // 几秒后自动移除消息
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> {
                    ID_LEVEL_GAME_GROUND.getChildren().remove(messageLabel);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showLevelFailedDialog() {
        Label messageLabel = new Label("时间到！\n本关挑战失败\n点击重试重新开始");
        messageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        messageLabel.setTextFill(Color.RED);
        messageLabel.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-padding: 20px; -fx-background-radius: 10px;");

        AnchorPane.setTopAnchor(messageLabel, 200.0);
        AnchorPane.setLeftAnchor(messageLabel, 200.0);

        ID_LEVEL_GAME_GROUND.getChildren().add(messageLabel);

        // 几秒后自动移除消息
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> {
                    ID_LEVEL_GAME_GROUND.getChildren().remove(messageLabel);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void resetCurrentLevel() {
        if (gameBoard != null) {
            gameBoard.resetBoard();
            gameBoard.generatePuzzle();

            // 重置计时器
            resetTimer();
            IDC_BTN_PAUSE.setDisable(false);
            IDC_BTN_TIPS.setDisable(false);
        }
    }

    private void returnInitGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/indi/llk/initial-interface.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ID_LEVEL_GAME_GROUND.getScene().getWindow();
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
        if (isGameStarted && gameBoard != null) {
            gameBoard.findMatchingPair();
            // 使用提示会减少分数
            currentScore = Math.max(0, currentScore - 10);
            scoreLabel.setText("分数: " + currentScore);
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

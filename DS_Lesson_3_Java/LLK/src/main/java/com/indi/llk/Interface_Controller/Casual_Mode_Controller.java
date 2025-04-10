package com.indi.llk.Interface_Controller;

import com.indi.llk.Game_Core_Controller.GameBoard;
import com.indi.llk.Window_Controller.HelpWindowManager;
import com.indi.llk.Window_Controller.SettingsWindowManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;

public class Casual_Mode_Controller {
    @FXML
    private Button IDC_BTN_TIPS;
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
    private boolean isPaused = false;

    // 用于调整游戏难度的滑块
    private Slider rowsSlider;
    private Slider colsSlider;
    private Label rowsLabel;
    private Label colsLabel;

    // 默认设置较少的图片数量
    private int rowCount = 4;
    private int colCount = 6;

    @FXML
    public void initialize() {
        System.out.println("Casual Mode Controller Initialized");
        try {
            String imageUrl = getClass().getResource("/com/indi/llk/image/Game_Background.png").toExternalForm();
            ID_NORMAL_GAME_GROUND.setStyle("-fx-background-image: url('" + imageUrl + "'); " +
                    "-fx-background-size: contain; " +
                    "-fx-background-repeat: no-repeat;");

            createDifficultyControls();

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

            IDC_BTN_RETURN.setOnAction(e -> returnInitGame());

            IDC_BTN_START.setOnAction(e -> startGame());

            IDC_BTN_PAUSE.setOnAction(e -> togglePause());
            IDC_BTN_PAUSE.setDisable(true);

            IDC_BTN_TIPS.setOnAction(e -> showHint());
            IDC_BTN_TIPS.setDisable(true);

            IDC_BTN_RESET.setOnAction(e -> resetGame());
            IDC_BTN_RESET.setDisable(true);

        } catch (Exception e) {
            System.err.println("Failed to load resources: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createDifficultyControls() {
        // 创建行数滑块
        rowsLabel = new Label("行数: " + rowCount);
        rowsLabel.setTextFill(Color.WHITE);
        rowsSlider = new Slider(2, 10, rowCount);
        rowsSlider.setShowTickMarks(true);
        rowsSlider.setShowTickLabels(true);
        rowsSlider.setMajorTickUnit(2);
        rowsSlider.setMinorTickCount(1);
        rowsSlider.setBlockIncrement(1);
        rowsSlider.setSnapToTicks(true);
        rowsSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            rowCount = newVal.intValue();
            rowsLabel.setText("行数: " + rowCount);
        });

        // 创建列数滑块
        colsLabel = new Label("列数: " + colCount);
        colsLabel.setTextFill(Color.WHITE);
        colsSlider = new Slider(2, 14, colCount);
        colsSlider.setShowTickMarks(true);
        colsSlider.setShowTickLabels(true);
        colsSlider.setMajorTickUnit(2);
        colsSlider.setMinorTickCount(1);
        colsSlider.setBlockIncrement(1);
        colsSlider.setSnapToTicks(true);
        colsSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            colCount = newVal.intValue();
            colsLabel.setText("列数: " + colCount);
        });

        // 创建容器
        VBox rowBox = new VBox(5, rowsLabel, rowsSlider);
        VBox colBox = new VBox(5, colsLabel, colsSlider);

        VBox controlBox = new VBox(15, rowBox, colBox);
        controlBox.setPadding(new Insets(10));
        controlBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-background-radius: 10;");
        controlBox.setMinWidth(200);
        controlBox.setAlignment(Pos.CENTER);

        // 放置在屏幕左侧
        AnchorPane.setLeftAnchor(controlBox, 15.0);
        AnchorPane.setTopAnchor(controlBox, 15.0);

        ID_NORMAL_GAME_GROUND.getChildren().add(controlBox);
    }

    private void startGame() {
        if (!isGameStarted || gameBoard == null) {
            if (gameBoard != null) {
                gameBoard.clearBoard();
            } else {
                gameBoard = new GameBoard(ID_NORMAL_GAME_GROUND);
            }

            // 计算适合的图片尺寸
            calculateAndSetTileSize();

            // 使用用户调整的行列数初始化游戏
            gameBoard.initializeBoard(rowCount, colCount);
            gameBoard.generatePuzzle();

            // 设置游戏完成监听器
            gameBoard.setGameCompleteListener(() -> {
                showGameCompleteMessage();
            });

            IDC_BTN_START.setText("重新开始");
            IDC_BTN_PAUSE.setDisable(false);
            IDC_BTN_TIPS.setDisable(false);
            IDC_BTN_RESET.setDisable(false);
            isGameStarted = true;

            // 游戏开始后禁用滑块
            rowsSlider.setDisable(true);
            colsSlider.setDisable(true);

        } else {
            resetGame();
        }
    }

    /**
     * 根据行列数计算合适的图片尺寸
     */
    private void calculateAndSetTileSize() {
        // 获取窗口尺寸
        double windowWidth = ID_NORMAL_GAME_GROUND.getWidth();
        double windowHeight = ID_NORMAL_GAME_GROUND.getHeight();

        // 预留右侧按钮区域和左侧控制面板
        double rightButtonSpace = 250;
        double leftControlSpace = 100;

        // 计算可用空间
        double availableWidth = windowWidth - rightButtonSpace - leftControlSpace;
        double availableHeight = windowHeight - 50;  // 上下各预留50px

        // 计算图块尺寸
        double tileWidth = Math.min(availableWidth / colCount - 2, 50);
        double tileHeight = Math.min(availableHeight / rowCount - 2, 50);
        double tileSize = Math.min(tileWidth, tileHeight);
        tileSize = Math.max(tileSize, 30);  // 最小不小于30

        // 计算棋盘总宽高
        double boardWidth = colCount * (tileSize + 3) - 3;  // 考虑间隔
        double boardHeight = rowCount * (tileSize + 3) - 3;

        // 计算居中位置
        double startX = leftControlSpace + (availableWidth - boardWidth) / 2;
        double startY = (windowHeight - boardHeight) / 2;

        // 设置图片尺寸和位置
        gameBoard.setTileSize((int)tileSize);
        gameBoard.setGameAreaPosition(startX, startY);
    }

    private void togglePause() {
        if (isGameStarted && gameBoard != null) {
            if (isPaused) {
                gameBoard.resumeGame();
                IDC_BTN_PAUSE.setText("暂停");
            } else {
                gameBoard.pauseGame();
                IDC_BTN_PAUSE.setText("继续");
            }
            isPaused = !isPaused;
        }
    }

    private void resetGame() {
        if (gameBoard != null) {
            gameBoard.resetBoard();
            gameBoard.generatePuzzle();

            // 重置后启用滑块
            rowsSlider.setDisable(false);
            colsSlider.setDisable(false);

            // 恢复按钮状态
            IDC_BTN_START.setText("开始游戏");
            IDC_BTN_PAUSE.setText("暂停");
            IDC_BTN_PAUSE.setDisable(true);
            IDC_BTN_TIPS.setDisable(true);
            IDC_BTN_RESET.setDisable(true);

            isGameStarted = false;
            isPaused = false;
        }
    }

    private void showHint() {
        if (isGameStarted && gameBoard != null) {
            gameBoard.findMatchingPair();
        }
    }

    private void returnInitGame() {
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

    private void showGameCompleteMessage() {
        Label messageLabel = new Label("恭喜！游戏完成！");
        messageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        messageLabel.setTextFill(Color.GOLD);
        messageLabel.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-padding: 20px; -fx-background-radius: 10px;");

        AnchorPane.setTopAnchor(messageLabel, 200.0);
        AnchorPane.setLeftAnchor(messageLabel, 300.0);

        ID_NORMAL_GAME_GROUND.getChildren().add(messageLabel);

        // 几秒后自动移除消息
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> {
                    ID_NORMAL_GAME_GROUND.getChildren().remove(messageLabel);
                    // 游戏完成后重置
                    resetGame();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
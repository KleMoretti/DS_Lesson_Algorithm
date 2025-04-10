package com.indi.llk.Game_Core_Controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameBoard {
    private final AnchorPane gameContainer;
    private GridPane boardGrid;
    private boolean isPaused;
    private int ROWS = 8;
    private int COLS = 8;
    private int TILE_SIZE = 50;
    private double startX = 90;
    private double startY = 90;
    private GameTile[][] tiles;
    private GameTile firstSelected = null;
    private GameTile secondSelected = null;
    private MediaPlayer eliminateSound;
    private GameSettings settings;
    private Rectangle[][] highlightBorders;

    // 添加提示相关变量
    private GameTile hintTile1;
    private GameTile hintTile2;
    private Rectangle hintBorder1;
    private Rectangle hintBorder2;

    public GameBoard(AnchorPane container) {
        this.gameContainer = container;
        this.isPaused = false;
        this.tiles = new GameTile[ROWS][COLS];
        this.settings = GameSettings.getInstance();
        initializeSound();
    }

    public void setTileSize(int size) {
        this.TILE_SIZE = Math.max(size, 30); // 最小不小于30
    }

    private void initializeSound() {
        try {
            Media sound = new Media(getClass().getResource("/com/indi/llk/Audio/eliminate.mp3").toExternalForm());
            eliminateSound = new MediaPlayer(sound);
            eliminateSound.volumeProperty().bind(settings.volumeProperty());
        } catch (Exception e) {
            System.err.println("Error loading eliminate sound: " + e.getMessage());
        }
    }

    public void setGameAreaPosition(double x, double y) {
        this.startX = x;
        this.startY = y;
    }

    public void initializeBoard(int rows, int cols) {
        ROWS = rows;
        COLS = cols;
        tiles = new GameTile[ROWS][COLS];

        boardGrid = new GridPane();

        boardGrid.setHgap(3);
        boardGrid.setVgap(3);

        highlightBorders = new Rectangle[ROWS][COLS];

        AnchorPane.setTopAnchor(boardGrid, startY);
        AnchorPane.setLeftAnchor(boardGrid, startX);

        gameContainer.getChildren().add(boardGrid);
    }

    public void generatePuzzle() {
        boardGrid.getChildren().clear();

        int availableTiles = 16;

        List<Integer> imageindices = new ArrayList<>();
        for (int i = 0; i < ROWS * COLS / 2; i++) {
            imageindices.add(i % availableTiles);
            imageindices.add(i % availableTiles);
        }
        Collections.shuffle(imageindices);

        int index = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int imageIndex = imageindices.get(index++);
                GameTile tile = createTile(imageIndex, row, col);
                tiles[row][col] = tile;
                boardGrid.add(tile.getImageView(), col, row);
            }
        }
    }

    private GameTile createTile(int imageIndex, int row, int col) {
        String imagePath = String.format("/com/indi/llk/image/tile_%d.png", imageIndex);
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(TILE_SIZE);
        imageView.setFitHeight(TILE_SIZE);

        GameTile tile = new GameTile(imageView, row, col, imageIndex);

        imageView.setOnMouseClicked(e -> {
            if (!isPaused && tile.isActive()) {
                handleTileClick(tile);
            }
        });

        return tile;
    }

    private void handleTileClick(GameTile tile) {
        if (firstSelected == null) {
            firstSelected = tile;
            highlightTile(firstSelected, true);
        } else if (secondSelected == null && tile != firstSelected) {
            secondSelected = tile;
            highlightTile(secondSelected, true);

            if (canConnect(firstSelected, secondSelected)) {
                eliminateTiles();
            } else {
                // 延迟取消选中
                new Thread(() -> {
                    try {
                        Thread.sleep(300);
                        javafx.application.Platform.runLater(() -> {
                            highlightTile(firstSelected, false);
                            highlightTile(secondSelected, false);
                            firstSelected = null;
                            secondSelected = null;
                        });
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }).start();
            }
        }
    }

    private void highlightTile(GameTile tile, boolean highlight) {
        int row = tile.getRow();
        int col = tile.getCol();

        // 懒加载方式创建边框
        if (highlightBorders[row][col] == null) {
            Rectangle border = new Rectangle(TILE_SIZE - 2.0, TILE_SIZE - 2.0);
            border.setFill(Color.TRANSPARENT);
            border.setStroke(Color.RED);
            border.setStrokeWidth(2);
            border.setVisible(false);

            // 预先添加到GridPane中
            boardGrid.add(border, col, row);
            highlightBorders[row][col] = border;
        }

        // 仅切换可见性
        highlightBorders[row][col].setVisible(highlight);
    }

    private boolean canConnect(GameTile first, GameTile second) {
        if (first.getImageIndex() != second.getImageIndex()) {
            return false;
        }
        // 检查是否可以用最多3条线连接
        return checkLineConnection(first, second);
    }

    private boolean checkLineConnection(GameTile start, GameTile end) {
        // 直线连接
        if (canConnectDirect(start, end)) {
            return true;
        }

        // 一次转弯
        if (canConnectOneCorner(start, end)) {
            return true;
        }

        // 两次转弯
        return canConnectTwoCorners(start, end);
    }

    private boolean canConnectDirect(GameTile start, GameTile end) {
        if (start.getRow() == end.getRow()) {
            // 水平连接
            int minCol = Math.min(start.getCol(), end.getCol());
            int maxCol = Math.max(start.getCol(), end.getCol());
            for (int col = minCol + 1; col < maxCol; col++) {
                // 检查边界
                if (!isOutOfBounds(start.getRow(), col) && tiles[start.getRow()][col].isActive()) {
                    return false;
                }
            }
            return true;
        }

        if (start.getCol() == end.getCol()) {
            // 垂直连接
            int minRow = Math.min(start.getRow(), end.getRow());
            int maxRow = Math.max(start.getRow(), end.getRow());
            for (int row = minRow + 1; row < maxRow; row++) {
                if (!isOutOfBounds(row, start.getCol()) && tiles[row][start.getCol()].isActive()) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    private boolean canConnectOneCorner(GameTile start, GameTile end) {
        // 尝试通过一个转角点连接
        Point corner1 = new Point(start.getRow(), end.getCol());
        Point corner2 = new Point(end.getRow(), start.getCol());

        // 检查corner1是否有效
        if (isValidCorner(corner1)) {
            if (canConnectDirect(start, new GameTile(null, corner1.row, corner1.col, 0)) &&
                    canConnectDirect(new GameTile(null, corner1.row, corner1.col, 0), end)) {
                return true;
            }
        }

        // 检查corner2是否有效
        if (isValidCorner(corner2)) {
            if (canConnectDirect(start, new GameTile(null, corner2.row, corner2.col, 0)) &&
                    canConnectDirect(new GameTile(null, corner2.row, corner2.col, 0), end)) {
                return true;
            }
        }

        return false;
    }

    private boolean isValidCorner(Point corner) {
        if (isOutOfBounds(corner.row, corner.col)) {
            return true; // 注意：边界外的点视为有效空格
        }
        return !tiles[corner.row][corner.col].isActive();
    }

    private boolean canConnectTwoCorners(GameTile start, GameTile end) {
        // 检查所有可能的空格点
        for (int row = -1; row <= ROWS; row++) {
            for (int col = -1; col <= COLS; col++) {
                // 如果是在棋盘范围外或者是空格
                if (isOutOfBounds(row, col) || !tiles[row][col].isActive()) {
                    GameTile corner = new GameTile(null, row, col, 0);
                    if (canConnectOneCorner(start, corner) && canConnectOneCorner(corner, end)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // 添加边界检查方法
    private boolean isOutOfBounds(int row, int col) {
        return row < 0 || row >= ROWS || col < 0 || col >= COLS;
    }

    private void eliminateTiles() {
        if (settings.isSfxEnabled() && eliminateSound != null) {
            eliminateSound.stop();
            eliminateSound.play();
        }

        firstSelected.eliminate();
        secondSelected.eliminate();

        // 隐藏图片而不是移除
        if (firstSelected.getImageView() != null) {
            firstSelected.getImageView().setVisible(false);
        }
        if (secondSelected.getImageView() != null) {
            secondSelected.getImageView().setVisible(false);
        }

        // 隐藏高亮边框
        if (highlightBorders[firstSelected.getRow()][firstSelected.getCol()] != null) {
            highlightBorders[firstSelected.getRow()][firstSelected.getCol()].setVisible(false);
        }
        if (highlightBorders[secondSelected.getRow()][secondSelected.getCol()] != null) {
            highlightBorders[secondSelected.getRow()][secondSelected.getCol()].setVisible(false);
        }

        firstSelected = null;
        secondSelected = null;

        checkGameComplete();
    }

    // 查找可连接的一对图块
    public boolean findMatchingPair() {
        // 清除之前的提示
        clearHint();

        // 遍历所有图块寻找可连接的一对
        for (int r1 = 0; r1 < ROWS; r1++) {
            for (int c1 = 0; c1 < COLS; c1++) {
                GameTile tile1 = tiles[r1][c1];
                if (!tile1.isActive()) {
                    continue;
                }

                for (int r2 = 0; r2 < ROWS; r2++) {
                    for (int c2 = 0; c2 < COLS; c2++) {
                        GameTile tile2 = tiles[r2][c2];
                        // 跳过同一个图块或不活跃的图块
                        if (!tile2.isActive() || (r1 == r2 && c1 == c2)) {
                            continue;
                        }

                        // 检查是否相同图案且可连接
                        if (tile1.getImageIndex() == tile2.getImageIndex() && canConnect(tile1, tile2)) {
                            // 找到可连接的一对
                            hintTile1 = tile1;
                            hintTile2 = tile2;
                            showHint();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // 显示提示高亮
    private void showHint() {
        // 创建闪烁边框
        hintBorder1 = createHighlightBorder(hintTile1.getRow(), hintTile1.getCol(), Color.YELLOW);
        hintBorder2 = createHighlightBorder(hintTile2.getRow(), hintTile2.getCol(), Color.YELLOW);

        boardGrid.getChildren().addAll(hintBorder1, hintBorder2);

        // 创建闪烁动画
        Timeline blinkTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    hintBorder1.setVisible(true);
                    hintBorder2.setVisible(true);
                }),
                new KeyFrame(Duration.seconds(0.1), e -> {
                    hintBorder1.setVisible(false);
                    hintBorder2.setVisible(false);
                })
        );
        blinkTimeline.setCycleCount(6); // 闪烁3次
        blinkTimeline.play();
    }

    // 清除提示高亮
    public void clearHint() {
        if (hintBorder1 != null) {
            boardGrid.getChildren().remove(hintBorder1);
            hintBorder1 = null;
        }
        if (hintBorder2 != null) {
            boardGrid.getChildren().remove(hintBorder2);
            hintBorder2 = null;
        }
        hintTile1 = null;
        hintTile2 = null;
    }

    private Rectangle createHighlightBorder(int row, int col, Color color) {
        Rectangle border = new Rectangle(
                TILE_SIZE - 2.0, TILE_SIZE - 2.0);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(color);
        border.setStrokeWidth(2);
        border.setArcWidth(10);
        border.setArcHeight(10);

        GridPane.setRowIndex(border, row);
        GridPane.setColumnIndex(border, col);

        return border;
    }

    public void clearBoard() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (tiles[row][col] != null) {
                    tiles[row][col].eliminate();
                    boardGrid.getChildren().remove(tiles[row][col].getImageView());
                }
            }
        }
        firstSelected = null;
        secondSelected = null;
    }

    public interface GameCompleteListener {
        void onGameComplete();
    }

    private GameCompleteListener gameCompleteListener;

    public void setGameCompleteListener(GameCompleteListener listener) {
        this.gameCompleteListener = listener;
    }

    private void checkGameComplete() {
        boolean complete = true;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (tiles[row][col].isActive()) {
                    complete = false;
                    break;
                }
            }
            if (!complete) {
                break;
            }
        }

        if (complete) {
            // 游戏胜利逻辑
            System.out.println("Game Complete!");
            isPaused = true; // 暂停游戏

            // 通知外部监听器（可以用来停止计时器）
            if (gameCompleteListener != null) {
                gameCompleteListener.onGameComplete();
            }

            // 在界面上显示成功信息
            javafx.application.Platform.runLater(() -> {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        Alert.AlertType.CONFIRMATION);

                alert.setTitle("恭喜");
                alert.setHeaderText("游戏完成");
                alert.setContentText("你已成功完成所有配对！");

                alert.getDialogPane().setGraphic(
                        new ImageView(new Image(getClass().getResourceAsStream("/com/indi/llk/image/icon_success.png")))
                );

                alert.show();
            });
        }
    }

    private static class Point {
        int row, col;

        Point(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    public void resetBoard() {
        boardGrid.getChildren().clear();
        firstSelected = null;
        secondSelected = null;
        isPaused = false;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void pauseGame() {
        isPaused = true;
        boardGrid.setVisible(false);
    }

    public void resumeGame() {
        isPaused = false;
        boardGrid.setVisible(true);
    }
}

class GameTile {
    private final ImageView imageView;
    private final int row;
    private final int col;
    private final int imageIndex;
    private boolean active = true;

    public GameTile(ImageView imageView, int row, int col, int imageIndex) {
        this.imageView = imageView;
        this.row = row;
        this.col = col;
        this.imageIndex = imageIndex;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getImageIndex() {
        return imageIndex;
    }

    public boolean isActive() {
        return active;
    }

    public void eliminate() {
        active = false;
    }
}


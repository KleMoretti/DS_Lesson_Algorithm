package com.indi.llk.Game_Core_Controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class GameTimer {
    private Timeline timeline;
    private int seconds = 0;
    private final Label timerLabel;


    public GameTimer(Label timerLabel) {
        this.timerLabel = timerLabel;
        String initialSeconds = timerLabel.getText();
        if (initialSeconds != null && !initialSeconds.isEmpty()) {
            try {
                this.seconds = Integer.parseInt(initialSeconds);
                initialize(this.seconds);
            } catch (NumberFormatException e) {
                System.err.println("Invalid initial seconds format: " + initialSeconds);
                initialize();
            }
        } else {
            initialize();
        }
    }

    private void initialize() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            seconds++;
            updateDisplay();
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        updateDisplay();
    }

    private void initialize(int seconds) {
        this.seconds = seconds;
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            this.seconds--;
            updateDisplay();
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        updateDisplay();
    }

    public void start() {
        timeline.play();
    }

    public void pause() {
        timeline.pause();
    }

    public void resume() {
        timeline.play();
    }


    private void updateDisplay() {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, secs));
    }

    public int getElapsedSeconds() {
        return seconds;
    }

    public void reset() {
        timeline.stop();
        seconds = isCountdownMode ? initialSeconds : 0;
        updateDisplay();
    }

    // 倒计时相关变量
    private boolean isCountdownMode = false;
    private int initialSeconds = 0;
    private Runnable countdownFinishAction = null;

    // 设置倒计时模式
    public void setCountdownMode(boolean isCountdown, int seconds, Runnable finishAction) {
        this.isCountdownMode = isCountdown;
        this.initialSeconds = seconds;
        this.countdownFinishAction = finishAction;
        this.seconds = seconds;

        // 重新初始化计时器
        timeline.stop();
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (isCountdownMode) {
                this.seconds--;
                updateDisplay();

                if (this.seconds <= 0 && countdownFinishAction != null) {
                    timeline.stop();
                    countdownFinishAction.run();
                }
            } else {
                this.seconds++;
                updateDisplay();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        updateDisplay();
    }

    // 获取倒计时剩余秒数
    public int getRemainingSeconds() {
        return seconds;
    }
}
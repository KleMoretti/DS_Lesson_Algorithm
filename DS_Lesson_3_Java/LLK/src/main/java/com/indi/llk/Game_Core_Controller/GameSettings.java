package com.indi.llk.Game_Core_Controller;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.BooleanProperty;

public class GameSettings {
    private static final GameSettings instance = new GameSettings();

    private final DoubleProperty volumeProperty = new SimpleDoubleProperty(0.5);
    private final BooleanProperty bgmEnabledProperty = new SimpleBooleanProperty(true);
    private final BooleanProperty sfxEnabledProperty = new SimpleBooleanProperty(true);

    private GameSettings() {}

    public static GameSettings getInstance() {
        return instance;
    }

    public double getVolume() {
        return volumeProperty.get();
    }

    public void setVolume(double volume) {
        volumeProperty.set(volume);
    }

    public DoubleProperty volumeProperty() {
        return volumeProperty;
    }

    public boolean isBgmEnabled() {
        return bgmEnabledProperty.get();
    }

    public void setBgmEnabled(boolean enabled) {
        bgmEnabledProperty.set(enabled);
    }

    public BooleanProperty bgmEnabledProperty() {
        return bgmEnabledProperty;
    }

    public boolean isSfxEnabled() {
        return sfxEnabledProperty.get();
    }

    public void setSfxEnabled(boolean enabled) {
        sfxEnabledProperty.set(enabled);
    }

    public BooleanProperty sfxEnabledProperty() {
        return sfxEnabledProperty;
    }
}
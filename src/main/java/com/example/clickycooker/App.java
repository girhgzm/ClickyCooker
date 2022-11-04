package com.example.clickycooker;

import com.almasb.fxgl.app.CursorInfo;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;

public class App extends GameApplication {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(800);
        gameSettings.setHeight(500);
        gameSettings.setTitle("Clicky Cooker");
        gameSettings.setDefaultCursor(new CursorInfo("cursor.png", 20, 20));
    }


    @Override
    protected void initGame() {

        try {
            GameManager gm = new GameManager();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
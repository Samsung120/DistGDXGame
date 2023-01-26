package com.distgdx.game;

import com.badlogic.gdx.Game;

public class MyGame extends Game {
    ScreenGame screenGame;

    @Override
    public void create() {
        screenGame = new ScreenGame();
        setScreen(screenGame);
    }
}

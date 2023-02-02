package com.distgdx.game;

import static com.distgdx.game.MyGame.SCR_HEIGHT;
import static com.distgdx.game.MyGame.SCR_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;

public class ScreenIntro implements Screen {
    MyGame g;
    Texture imgBG;

    TextButton btnPlay, btnSettings, btnAbout, btnExit;

    public ScreenIntro(MyGame context){
        g = context;
        btnPlay = new TextButton(g.fontLarge, "PLAY", 500, 600);
        btnSettings = new TextButton(g.fontLarge, "SETTINGS", 500, 500);
        btnAbout = new TextButton(g.fontLarge, "ABOUT", 500, 400);
        btnExit = new TextButton(g.fontLarge, "EXIT", 500, 300);
        imgBG = new Texture("boloto1.jpg");
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // обработка касаний
        if(Gdx.input.justTouched()) {
            g.touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            g.camera.unproject(g.touch);
            if(btnPlay.hit(g.touch.x, g.touch.y)){
                g.setScreen(g.screenGame);
            }
            if(btnSettings.hit(g.touch.x, g.touch.y)){
                g.setScreen(g.screenSettings);
            }
            if(btnAbout.hit(g.touch.x, g.touch.y)){
                g.setScreen(g.screenAbout);
            }
            if(btnExit.hit(g.touch.x, g.touch.y)){
                Gdx.app.exit();
            }
        }

        // отрисовка графики
        g.camera.update();
        g.batch.setProjectionMatrix(g.camera.combined);
        g.batch.begin();
        g.batch.draw(imgBG, 0, 0, SCR_WIDTH, SCR_HEIGHT);
        btnPlay.font.draw(g.batch, btnPlay.text, btnPlay.x, btnPlay.y);
        btnSettings.font.draw(g.batch, btnSettings.text, btnSettings.x, btnSettings.y);
        btnAbout.font.draw(g.batch, btnAbout.text, btnAbout.x, btnAbout.y);
        btnExit.font.draw(g.batch, btnExit.text, btnExit.x, btnExit.y);
        g.batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        imgBG.dispose();
    }
}

package com.distgdx.game;

import static com.distgdx.game.MyGame.SCR_HEIGHT;
import static com.distgdx.game.MyGame.SCR_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;

public class ScreenSettings implements Screen {
    MyGame g;
    Texture imgBG;

    TextButton btnMosquitos, btnSound, btnMusic, btnLanguage, btnBack;

    public ScreenSettings(MyGame context){
        g = context;
        btnMosquitos = new TextButton(g.fontLarge, "NUMBER MOSQUITOS", 200, 600);
        btnSound = new TextButton(g.fontLarge, "SOUND ON", 200, 500);
        btnMusic = new TextButton(g.fontLarge, "MUSIC ON", 200, 400);
        btnLanguage = new TextButton(g.fontLarge, "LANGUAGE ENG", 200, 300);
        btnBack = new TextButton(g.fontLarge, "BACK", 200, 200);
        imgBG = new Texture("boloto2.jpg");
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
            if(btnMosquitos.hit(g.touch.x, g.touch.y)){

            }
            if(btnSound.hit(g.touch.x, g.touch.y)){

            }
            if(btnMusic.hit(g.touch.x, g.touch.y)){

            }
            if(btnLanguage.hit(g.touch.x, g.touch.y)){

            }
            if(btnBack.hit(g.touch.x, g.touch.y)){
                g.setScreen(g.screenIntro);
            }
        }

        // отрисовка графики
        g.camera.update();
        g.batch.setProjectionMatrix(g.camera.combined);
        g.batch.begin();
        g.batch.draw(imgBG, 0, 0, SCR_WIDTH, SCR_HEIGHT);
        btnMosquitos.font.draw(g.batch, btnMosquitos.text, btnMosquitos.x, btnMosquitos.y);
        btnSound.font.draw(g.batch, btnSound.text, btnSound.x, btnSound.y);
        btnMusic.font.draw(g.batch, btnMusic.text, btnMusic.x, btnMusic.y);
        btnLanguage.font.draw(g.batch, btnLanguage.text, btnLanguage.x, btnLanguage.y);
        btnBack.font.draw(g.batch, btnBack.text, btnBack.x, btnBack.y);
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

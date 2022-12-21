package com.distgdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class MyGame extends ApplicationAdapter {
	public static final int SCR_WIDTH = 1280, SCR_HEIGHT = 720;

	SpriteBatch batch;
	OrthographicCamera camera;
	Vector3 touch;
	BitmapFont font;

	Texture[] imgMosq = new Texture[11];
	Texture imgBG;

	Sound[] sndMosq = new Sound[6];

	Mosquito[] mosq = new Mosquito[21];
	int kills;
	long timeStart, timeFromStart;

	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCR_WIDTH, SCR_HEIGHT);
		touch = new Vector3();

		generateFont();

		imgBG = new Texture("boloto.jpg");
		for (int i = 0; i < imgMosq.length; i++) {
			imgMosq[i] = new Texture("mosq"+i+".png");
		}

		for (int i = 0; i < sndMosq.length; i++) {
			sndMosq[i] = Gdx.audio.newSound(Gdx.files.internal("cheep"+i+".mp3"));
		}

		for (int i = 0; i < mosq.length; i++) {
			mosq[i] = new Mosquito();
		}

		timeStart = TimeUtils.millis();
	}

	void generateFont(){
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("wellwaitfree.otf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 50;
		parameter.color = new Color().set(1, 0.9f, 0.3f, 1);
		parameter.borderColor = Color.BLACK;
		parameter.borderWidth = 2;
		parameter.borderStraight = true;
		//parameter.characters = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>";
		String s = "";
		for (char i = 0x20; i < 0x7B; i++) s += i;
		for (char i = 0x401; i < 0x452; i++) s += i;
		parameter.characters = s;
		font = generator.generateFont(parameter);
		generator.dispose();
	}

	@Override
	public void render () {
		// обработка касаний
		if(Gdx.input.justTouched()) {
			touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touch);
			for (int i = mosq.length-1; i >= 0; i--) {
				if(mosq[i].isAlive && mosq[i].hit(touch.x, touch.y)){
					kills++;
					//sndMosq[MathUtils.random(sndMosq.length-1)].play();
					break;
				}
			}
		}

		// игровые события
		for (int i = 0; i < mosq.length; i++) mosq[i].move();

		timeFromStart = TimeUtils.millis() - timeStart;
		String timeStr = timeFromStart/1000/60/60+":"+timeFromStart/1000/60%60/10+timeFromStart/1000/60%60%10+":"+timeFromStart/1000%60/10+timeFromStart/1000%60%10;
		// возрожджение комаров
		/*for (int i = 0; i < mosq.length; i++) {
			if(!mosq[i].isAlive) {
				if(MathUtils.random(1000) == 5) mosq[i].reBorn();
			}
		}*/

		// отрисовка графики
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(imgBG, 0, 0, SCR_WIDTH, SCR_HEIGHT);
		for (int i = 0; i < mosq.length; i++) {
			batch.draw(imgMosq[mosq[i].phase], mosq[i].scrX(), mosq[i].scrY(), mosq[i].width, mosq[i].height, 0, 0, 500, 500, mosq[i].isFlip(), false);
		}
		font.draw(batch, "Kills: "+kills, 10, SCR_HEIGHT-10);
		font.draw(batch, timeStr, SCR_WIDTH-250, SCR_HEIGHT-10);
		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
		for (int i = 0; i < imgMosq.length; i++) {
			imgMosq[i].dispose();
		}
		imgBG.dispose();
		for (int i = 0; i < sndMosq.length; i++) {
			imgMosq[i].dispose();
		}
		font.dispose();
	}
}
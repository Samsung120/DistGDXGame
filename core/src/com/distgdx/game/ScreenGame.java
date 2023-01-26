package com.distgdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
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

public class ScreenGame implements Screen {
	public static final int SCR_WIDTH = 1280, SCR_HEIGHT = 720;

	SpriteBatch batch;
	OrthographicCamera camera;
	Vector3 touch;
	BitmapFont font;
	InputKeyboard keyboard;

	Texture[] imgMosq = new Texture[11];
	Texture imgBG;

	Sound[] sndMosq = new Sound[6];

	Mosquito[] mosq = new Mosquito[5];
	int kills;
	long timeStart, timeFromStart;
	Player[] players = new Player[6];

	TextButton btnRestart, btnExit;

	// состояние игры
	public static final int PLAY_GAME = 0, ENTER_NAME = 1, SHOW_TABLE = 2;
	int state = PLAY_GAME;

	public ScreenGame () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCR_WIDTH, SCR_HEIGHT);
		touch = new Vector3();
		keyboard = new InputKeyboard(SCR_WIDTH, SCR_HEIGHT, 8);

		generateFont();

		btnRestart = new TextButton(font, "RESTART", 10, 50);
		btnExit = new TextButton(font, "EXIT", SCR_WIDTH-150, 50);

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

		for (int i = 0; i < players.length; i++) {
			players[i] = new Player("Noname");
		}

		loadTableOfRecords();

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

	void gameRestart(){
		state = PLAY_GAME;
		for (int i = 0; i < mosq.length; i++) {
			mosq[i] = new Mosquito();
		}
		kills = 0;
		timeStart = TimeUtils.millis();
	}

	void gameOver(){
		state = SHOW_TABLE;
		players[players.length-1].time = timeFromStart;
		players[players.length-1].name = keyboard.getText();
		//players[players.length-1].name = generateRndName();
		sortTable();
		saveTableOfRecords();
	}

	String timeToString(long time){
		return time/1000/60/60+":"+time/1000/60%60/10+time/1000/60%60%10+":"+time/1000%60/10+time/1000%60%10;
	}

	void sortTable(){
		for (int i = 0; i < players.length; i++) {
			if(players[i].time == 0) players[i].time = Long.MAX_VALUE;
		}
		boolean flag = true;
		while (flag) {
			flag = false;
			for (int i = 0; i < players.length - 1; i++) {
				if (players[i].time > players[i + 1].time) {
					flag = true;
					Player p = players[i];
					players[i] = players[i + 1];
					players[i + 1] = p;
				}
			}
		}
		for (int i = 0; i < players.length; i++) {
			if(players[i].time == Long.MAX_VALUE) players[i].time = 0;
		}
	}

	String showTableOfRecords(){
		String s = "  Таблица рекордов:\n\n";
		for (int i = 0; i < players.length-1; i++) {
			s += i+1+" "+players[i].name+"......."+timeToString(players[i].time)+"\n";
		}
		return s;
	}

	void saveTableOfRecords(){
		try {
			Preferences prefs = Gdx.app.getPreferences("Table Of Records");
			for (int i = 0; i < players.length; i++) {
				prefs.putString("name" + i, players[i].name);
				prefs.putLong("time" + i, players[i].time);
			}
			prefs.flush();
		} catch (Exception ignored){
		}
	}

	void loadTableOfRecords(){
		try {
			Preferences prefs = Gdx.app.getPreferences("Table Of Records");
			for (int i = 0; i < players.length; i++) {
				players[i].name = prefs.getString("name" + i, "No info");
				players[i].time = prefs.getLong("time" + i, 0);
			}
		} catch (Exception ignored){
		}
	}

	String generateRndName(){
		String name = "";
		name += (char)MathUtils.random('A', 'Z');
		String s = "bcdfghjklmnpqrstvwxz";
		String g = "aeiouy";
		for (int i = 0; i < 3; i++) {
			name += g.charAt(MathUtils.random(g.length()-1));
			name += s.charAt(MathUtils.random(s.length()-1));
		}
		return name;
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		// обработка касаний
		if(Gdx.input.justTouched()) {
			touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touch);
			if(state == SHOW_TABLE){
				if(btnRestart.hit(touch.x, touch.y)) gameRestart();
				if(btnExit.hit(touch.x, touch.y)) Gdx.app.exit();
			}
			if(state == ENTER_NAME) {
				keyboard.hit(touch.x, touch.y);
				if (keyboard.endOfEdit()) gameOver();
			}
			if(state == PLAY_GAME) {
				for (int i = mosq.length - 1; i >= 0; i--) {
					if (mosq[i].isAlive && mosq[i].hit(touch.x, touch.y)) {
						kills++;
						sndMosq[MathUtils.random(sndMosq.length - 1)].play();
						if (kills == mosq.length) state = ENTER_NAME;
						break;
					}
				}
			}
		}

		// игровые события
		for (int i = 0; i < mosq.length; i++) mosq[i].move();

		if(state == PLAY_GAME) timeFromStart = TimeUtils.millis() - timeStart;

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
		font.draw(batch, timeToString(timeFromStart), SCR_WIDTH-250, SCR_HEIGHT-10);
		if(state == SHOW_TABLE){
			font.draw(batch, showTableOfRecords(), SCR_WIDTH/4f, SCR_HEIGHT/4f*3);
			font.draw(batch, btnRestart.text, btnRestart.x, btnRestart.y);
			font.draw(batch, btnExit.text, btnExit.x, btnExit.y);
		}
		if(state == ENTER_NAME){
			keyboard.draw(batch);
		}
		batch.end();
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
		keyboard.dispose();
	}
}

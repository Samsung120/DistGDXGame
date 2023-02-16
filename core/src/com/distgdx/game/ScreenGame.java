package com.distgdx.game;

import static com.distgdx.game.MyGame.SCR_HEIGHT;
import static com.distgdx.game.MyGame.SCR_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Arrays;
import java.util.Comparator;

public class ScreenGame implements Screen {
	MyGame g;

	Texture[] imgMosq = new Texture[11];
	Texture imgBG;

	Sound[] sndMosq = new Sound[6];
	Music music;

	Mosquito[] mosq;
	int kills;
	long timeStart, timeFromStart;
	Player[] players = new Player[6];

	TextButton btnRestart, btnBack;

	// состояние игры
	public static final int PLAY_GAME = 0, ENTER_NAME = 1, SHOW_TABLE = 2;
	int state = PLAY_GAME;

	public ScreenGame (MyGame context) {
		g = context;
		mosq = new Mosquito[g.numMosquitos];

		btnRestart = new TextButton(g.font, "RESTART", 10, 50);
		btnBack = new TextButton(g.font, "BACK", SCR_WIDTH-150, 50);

		imgBG = new Texture("boloto0.jpg");
		for (int i = 0; i < imgMosq.length; i++) {
			imgMosq[i] = new Texture("mosq"+i+".png");
		}

		for (int i = 0; i < sndMosq.length; i++) {
			sndMosq[i] = Gdx.audio.newSound(Gdx.files.internal("sound/cheep"+i+".mp3"));
		}
		music = Gdx.audio.newMusic(Gdx.files.internal("sound/IDBones.mp3"));
		music.setLooping(true);
		music.setVolume(0.4f);

		for (int i = 0; i < players.length; i++) {
			players[i] = new Player("Noname");
		}

		loadTableOfRecords();
	}

	void gameStart(){
		state = PLAY_GAME;
		mosq = new Mosquito[g.numMosquitos];
		for (int i = 0; i < mosq.length; i++) {
			mosq[i] = new Mosquito();
		}
		kills = 0;
		if(g.musicOn) music.play();
		timeStart = TimeUtils.millis();
	}

	void gameOver(){
		state = SHOW_TABLE;
		players[players.length-1].time = timeFromStart;
		players[players.length-1].name = g.keyboard.getText();
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
		class Cmp implements Comparator<Player>{
			@Override
			public int compare(Player p1, Player p2) {
				if(p1.time>p2.time) return 1;
				if(p1.time<p2.time) return -1;
				return 0;
			}
		}
		Arrays.sort(players, new Cmp());
		/*boolean flag = true;
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
		}*/
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
		gameStart();
	}

	@Override
	public void render(float delta) {
		// обработка касаний
		if(Gdx.input.justTouched()) {
			g.touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			g.camera.unproject(g.touch);
			if(state == SHOW_TABLE){
				if(btnRestart.hit(g.touch.x, g.touch.y)) gameStart();
				if(btnBack.hit(g.touch.x, g.touch.y)) {
					g.setScreen(g.screenIntro);
				}
			}
			if(state == ENTER_NAME) {
				if (g.keyboard.endOfEdit(g.touch.x, g.touch.y)) gameOver();
			}
			if(state == PLAY_GAME) {
				for (int i = mosq.length - 1; i >= 0; i--) {
					if (mosq[i].isAlive && mosq[i].hit(g.touch.x, g.touch.y)) {
						kills++;
						if(g.soundOn) sndMosq[MathUtils.random(sndMosq.length - 1)].play();
						if (kills == mosq.length) state = ENTER_NAME;
						break;
					}
				}
				if(btnBack.hit(g.touch.x, g.touch.y)) {
					g.setScreen(g.screenIntro);
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
		g.camera.update();
		g.batch.setProjectionMatrix(g.camera.combined);
		g.batch.begin();
		g.batch.draw(imgBG, 0, 0, SCR_WIDTH, SCR_HEIGHT);
		for (int i = 0; i < mosq.length; i++) {
			g.batch.draw(imgMosq[mosq[i].phase], mosq[i].scrX(), mosq[i].scrY(), mosq[i].width, mosq[i].height, 0, 0, 500, 500, mosq[i].isFlip(), false);
		}
		g.font.draw(g.batch, "Kills: "+kills, 10, SCR_HEIGHT-10);
		g.font.draw(g.batch, timeToString(timeFromStart), SCR_WIDTH-250, SCR_HEIGHT-10);
		if(state == PLAY_GAME) {
			g.font.draw(g.batch, btnBack.text, btnBack.x, btnBack.y);
		}
		if(state == SHOW_TABLE){
			g.font.draw(g.batch, showTableOfRecords(), SCR_WIDTH/4f, SCR_HEIGHT/4f*3);
			g.font.draw(g.batch, btnRestart.text, btnRestart.x, btnRestart.y);
			g.font.draw(g.batch, btnBack.text, btnBack.x, btnBack.y);
		}
		if(state == ENTER_NAME){
			g.keyboard.draw(g.batch);
		}
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
		music.stop();
	}

	@Override
	public void dispose () {
		for (int i = 0; i < imgMosq.length; i++) {
			imgMosq[i].dispose();
		}
		imgBG.dispose();
		for (int i = 0; i < sndMosq.length; i++) {
			imgMosq[i].dispose();
		}
		music.dispose();
		g.keyboard.dispose();
	}
}

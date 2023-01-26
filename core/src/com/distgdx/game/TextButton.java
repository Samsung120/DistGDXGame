package com.distgdx.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class TextButton {
    float x, y;
    float width, height;
    String text;
    BitmapFont font;

    public TextButton(BitmapFont font, String text, float x, float y) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.font = font;
        GlyphLayout gl = new GlyphLayout(font, text);
        width = gl.width;
        height = gl.height;
    }

    boolean hit(float tx, float ty){
        return x < tx && tx < x+width && y-height < ty && ty < y;
    }
}

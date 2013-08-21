package com.mojang.mojam.giraffe.entity.graphic;

import com.mojang.mojam.giraffe.Util;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;

public class BlinkExit extends Graphic {
    private static final SpriteSheet SHEET = Util.loadSpriteSheet("blinkflash.png", 94, 95);

    private final Animation animation;

    public BlinkExit(float x, float y) {
        super(x, y);

        animation = new Animation(false);
        animation.setLooping(false);
        animation.addFrame(SHEET.getSprite(0, 0), 40);
    }

    @Override
    public void draw(Graphics g) {
        animation.draw(x - 47, y - 47);
    }

    @Override
    public void update(int delta) {
        animation.update(delta);
    }

    @Override
    public boolean isFinished() {
        return animation.isStopped();
    }
}

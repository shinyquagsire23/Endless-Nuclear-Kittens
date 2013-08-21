package com.mojang.mojam.giraffe.entity.graphic;

import com.mojang.mojam.giraffe.Util;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;

public class BlinkParticle extends Graphic {
    private static final SpriteSheet SHEET = Util.loadSpriteSheet("whiteparticle.png", 16, 16);

    private final Animation animation;

    private static final int MAX_TIME = 1000;


    public BlinkParticle(float x, float y, float targetX, float targetY, float percent) {
        super(x, y);

        int totalTime = (int) ((float) MAX_TIME * percent);

        animation = new Animation(SHEET, 50);
        animation.setLooping(false);

        int frameCount = animation.getFrameCount();
        for (int i = 0; i < frameCount; i++) {
            animation.setDuration(i, totalTime / frameCount);
        }

        dx = (targetX - x) / totalTime;
        dy = (targetY - y) / totalTime;
    }

    @Override
    public void draw(Graphics g) {
        animation.draw(x - 8, y - 8);
    }

    @Override
    public void update(int delta) {
        x += dx * delta;
        y += dy * delta;

        animation.update(delta);
    }

    @Override
    public boolean isFinished() {
        return animation.isStopped();
    }
}

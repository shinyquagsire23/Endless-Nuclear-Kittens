package com.mojang.mojam.giraffe.entity.graphic;

import com.mojang.mojam.giraffe.Util;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;

import java.util.Random;

public class BulletHit extends Graphic {
    private static final SpriteSheet SHEET = Util.loadSpriteSheet("bullethit.png", 32, 32);
    private static final Random RANDOM = new Random();

    private final Animation animation;
    private final int direction;

    public BulletHit(float x, float y) {
        super(x, y);

        animation = new Animation(SHEET, 40);
        animation.setDuration(0, 20);
        animation.setDuration(1, 20);
        animation.setLooping(false);

        direction = RANDOM.nextInt(4);
    }

    @Override
    public void draw(Graphics g) {
        g.rotate(x, y, 90 * direction);
        animation.draw(x - 16, y - 16);
        g.rotate(x, y, -90 * direction);
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

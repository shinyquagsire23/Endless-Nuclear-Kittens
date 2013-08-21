package com.mojang.mojam.giraffe.entity.projectile;

import com.mojang.mojam.giraffe.Util;
import com.mojang.mojam.giraffe.entity.graphic.Graphic;
import com.mojang.mojam.giraffe.entity.graphic.Poof;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Circle;

import java.util.Random;

public class ShotgunPellet extends BaseProjectile {
    private static final SpriteSheet SHEET = Util.loadSpriteSheet("shotgun_bullet.png", 32, 32);
    private static final Random RANDOM = new Random();
    private static final int SIZE = 6;
    private final int damage;
    private final int direction;
    private Animation animation;
    private final Animation animation2;
    private int age = 0;

    public ShotgunPellet(float x, float y) {
        super(x, y, new Circle(x, y, SIZE / 2));
        damage = 15;

        // Sparkly bit
        animation = new Animation();
        animation.addFrame(SHEET.getSprite(0, 0), 10);
        animation.addFrame(SHEET.getSprite(1, 0), 10);
        int sheetSize = SHEET.getHorizontalCount();
        for (int i = 2; i < sheetSize - 2; i++) {
            animation.addFrame(SHEET.getSprite(i, 0), 10 + RANDOM.nextInt(10));
        }
        animation.setLooping(false);
        animation.setAutoUpdate(false);

        // Remaining flight
        animation2 = new Animation();
        animation2.addFrame(SHEET.getSprite(sheetSize - 2, 0), 20);
        animation2.addFrame(SHEET.getSprite(sheetSize - 1, 0), 20);
        animation2.setLooping(true);
        animation2.setAutoUpdate(false);

        direction = RANDOM.nextInt(4);
    }

    public void draw(Graphics g) {
        g.rotate(x, y, 90 * direction);
        animation.draw(x - 16, y - 16);
        g.rotate(x, y, -90 * direction);
    }

    @Override
    public void update(int delta) {
        super.update(delta);
        age += delta;
        animation.update(delta);
        if (animation.isStopped()) animation = animation2;

        if (age > 200) {
            dx *= Math.pow(0.95, delta / 2);
            dy *= Math.pow(0.95, delta / 2);
        }
    }

    public ShotgunPellet setSpeed(float angle, float speed) {
        speed = .5f * speed + .5f * (float) Math.random() * speed;

        dx = -(float) Math.cos(Math.toRadians(angle)) * speed;
        dy = -(float) Math.sin(Math.toRadians(angle)) * speed;

        return this;
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public Graphic onPoof() {
        return new Poof(x, y);
    }

    @Override
    public boolean isFinished() {
        return age > 350;
    }
}

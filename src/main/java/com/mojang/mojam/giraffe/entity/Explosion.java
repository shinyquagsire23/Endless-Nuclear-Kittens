package com.mojang.mojam.giraffe.entity;

import com.mojang.mojam.giraffe.Game;
import com.mojang.mojam.giraffe.Util;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Circle;

import java.math.BigInteger;

public class Explosion extends BaseEntity {

    private static final int SIZE = 128;
    private static final SpriteSheet SPRITE_SHEET = Util.loadSpriteSheet("kittensplosion.png", SIZE, SIZE);

    private float size;
    private float speed;
    private int timePassed;
    private BigInteger score; //Apparently not lazy!
    protected Animation explosion;

    private final int delay;
    private boolean hadDelay = false;

    private int chain;

    public Explosion(float x, float y, float size, long score, int delay, int chain) {
        this(x, y, size, BigInteger.valueOf(score), delay, chain);

    }

    public Explosion(float x, float y, float size, BigInteger score, int delay, int chain) {
        super(x, y, new Circle(x, y, 5 + size / 10));
        this.delay = delay;

        if (this.delay > 0) {
            collider.setRadius(0);
            hadDelay = true;
        }

        this.chain = chain;
        this.size = size;
        this.score = score;
        speed = 1.0f + size / 50f;
        explosion = new Animation(SPRITE_SHEET, 30);
        explosion.setAutoUpdate(false);
        explosion.setLooping(false);

        if (chain == 0) {
            Game.playSound(Game.SOUND_EXPLOSION, 1.0f, 0.3f);
        } else {
            Game.playSound(Game.SOUND_CHAIN_EXPLOSION, Math.min(2.5f, 1.0f + chain * 0.25f), 0.3f);
        }
    }

    public float getDecreasedSize() {
        return Math.max(size, 40) * 0.85f;
    }

    public BigInteger getScore() {
        return score;
    }

    public int getChain() {
        return chain;
    }

    @Override
    public void update(int delta) {
        timePassed += delta;

        if (timePassed < delay) {
            return;
        }

        if (hadDelay) {
            hadDelay = false;
            collider.setRadius(5 + size / 10);
        }

        if (speed > size / 80f) {
            speed -= delta / 25f;
        } else {
            speed = size / 80f;
        }
        if (collider.getRadius() < size) {
            collider.setRadius(collider.radius + delta / 12f * speed);
        }
        collider.setCenterX(x);
        collider.setCenterY(y);
        explosion.update(delta);
    }

    @Override
    public boolean isFinished() {
        return timePassed - delay > 800;
    }

    public void draw(Graphics g) {
        if (timePassed < delay) return;

        g.setLineWidth(3);
        float intensity = 1 - ((timePassed - delay) / 800f);
        g.setDrawMode(Graphics.MODE_NORMAL);
        g.setColor(new Color(1, 1, 1, 0.3f * intensity));
        g.draw(collider);
        explosion.draw(x - explosion.getWidth() / 2, y - explosion.getHeight() / 2);
        int sqrt = (int) Math.sqrt(score.bitLength() > 63 ? Long.MAX_VALUE : score.longValue());
        int scoreLevel = Math.min(sqrt, Game.FONT_SCORES.length - 1);
        g.setFont(Game.FONT_SCORES[scoreLevel]);
        g.setColor(new Color(0, 0, 0, 1 - intensity));

        g.scale(1 / 2f, 1 / 2f);
        g.drawString("" + score, (int) (x - scoreLevel / 4) * 2, (int) (y - scoreLevel / 2 - (1 - intensity * 0.4f) * 48) * 2);
        g.setColor(new Color(1.0f, 1.0f, 1.0f, 1 - intensity));
        g.drawString("" + score, (int) (x - scoreLevel / 4 + 1) * 2, (int) (y - scoreLevel / 2 + 1 - (1 - intensity * 0.4f) * 48) * 2);
        g.scale(2f, 2f);
        g.setFont(Game.FONT);
    }
}

package com.mojang.mojam.giraffe.entity.enemy;

import com.mojang.mojam.giraffe.Game;
import com.mojang.mojam.giraffe.entity.BaseEntity;
import com.mojang.mojam.giraffe.entity.Entity;
import com.mojang.mojam.giraffe.entity.Explosion;
import com.mojang.mojam.giraffe.entity.Hurtable;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class AbstractEnemy extends BaseEntity implements Hurtable {
    protected final Random random = new Random();
    protected int health = 100;
    private boolean flashNext;
    private int flashDuration;
    private List<Entity> ignoreList = new ArrayList<Entity>();

    public AbstractEnemy(Vector2f startPos, int colliderSize, int health) {
        super(startPos.getX(), startPos.getY(), new Circle(startPos.getX(), startPos.getY(), colliderSize));
        this.health = health;
        init();
    }

    public void init() {
    }

    @Override
    public void update(int delta) {
        super.update(delta);
        if (flashNext) {
            flashDuration += delta;
            if (flashDuration > getFlashDuration()) {
                flashNext = false;
                flashDuration = 0;
            }
        }
    }

    protected int getFlashDuration() {
        return 75;
    }

    @Override
    public int getExplosionDelay() {
        return 0;
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void onDeath() {
    }

    public void draw(Graphics g) {
        // Boundingbox!~
        //g.setColor(new Color(0.6f, 0, 0));
        //g.draw(collider);
        drawEnemy(g, flashNext);
    }

    public void drawShadow(Graphics g) {
        g.setColor(new Color(0, 0, 0, 0.3f));
        float radius = collider.getRadius() * 1.5f;
        g.fillOval(x - radius / 2f, y + radius / 2f, radius, radius / 2f);
    }

    public abstract void drawEnemy(Graphics g, boolean flash);

    public boolean hurt(Entity source, int damage, float dx, float dy) {
        if (source instanceof Explosion) {
            if (ignoreList.contains(source)) {
                return false;
            } else {
                ignoreList.add(source);
            }
        }
        Vector2f normalized = new Vector2f(dx, dy).normalise();
        tryMove(Game.INSTANCE.getWorld(), normalized.x * damage / 6f, normalized.y * damage / 6f);
        health -= damage;
        flashNext = true;
        flashDuration = 0;
        return health <= 0;
    }

    public int getHealth() {
        return health;
    }

    public int getDamageOnCollision() {
        return 10 + (int) (Math.random() * 11);
    }

    public abstract float getSpawnChance(int time);

    public abstract AbstractEnemy copy();
}

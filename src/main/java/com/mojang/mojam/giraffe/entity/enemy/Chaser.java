package com.mojang.mojam.giraffe.entity.enemy;

import java.util.Random;

import com.mojang.mojam.giraffe.Game;
import com.mojang.mojam.giraffe.Util;
import com.mojang.mojam.giraffe.animator.Direction;
import com.mojang.mojam.giraffe.animator.DirectionalAnimator;
import com.mojang.mojam.giraffe.animator.DirectionalType;
import com.mojang.mojam.giraffe.entity.Mattis;
import com.mojang.mojam.giraffe.entity.graphic.KittenPoof;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class Chaser extends AbstractEnemy {
    private static final float SPEED = 1 / 6f;
    private final DirectionalAnimator animator;
    private Mattis mattis;
    private Mattis[] mattisi;
    private double seekAngle;
    private double angle;
    private double turnspeed;

    public Chaser(Vector2f startPos, Mattis[] mattis2) {
        super(startPos, 48 / 4, 100);
        Random r = new Random();
        int target = 0;
        while(mattis2[target].isDead())
        	target = r.nextInt(Game.numPlayers);
        this.mattis = mattis2[target];
        this.mattisi = mattis2;

        animator = new DirectionalAnimator(DirectionalType.FOUR_ROT, true);
        animator.load("kitten_01.png", 48, 48);

        animator.setAnimation(Direction.SOUTH_EAST, 0, 0, 8, 40);
        animator.setAnimation(Direction.NORTH_EAST, 8, 0, 8, 40);
        animator.autoFill();
    }

    @Override
    public AbstractEnemy copy() {
        return new Chaser(getPosition(), mattisi);
    }

    @Override
    public void init() {
        seekAngle = angle = random.nextInt(4) * Math.PI / 2f + Math.PI / 4f;
        dx = (float) Math.cos(angle) * SPEED;
        dy = (float) Math.sin(angle) * SPEED;
        turnspeed = 0.001 + 0.003 * Math.random();
    }

    @Override
    public void drawEnemy(Graphics g, boolean flash) {
        animator.draw(x, y, flash);
    }

    @Override
    public void update(int delta) {
        super.update(delta);
        seekAngle = Math.atan2(mattis.getY() - y, mattis.getX() - x);
        for (int i = 0; i < delta; i++) {
            angle = Math.toRadians(Util.lerpDegrees(Math.toDegrees(angle), Math.toDegrees(seekAngle), 0.001 + turnspeed));
        }
        dx = (float) Math.cos(angle) * SPEED;
        dy = (float) Math.sin(angle) * SPEED;

        animator.setRotationFromMovementDeltas(dx, dy);
        animator.update(delta);
    }

    @Override
    public void collisionX(Rectangle bounds, float x, float newX) {
        dx = -dx;
        this.x = x;
    }

    @Override
    public void collisionY(Rectangle bounds, float y, float newY) {
        dy = -dy;
        this.y = y;
    }

    @Override
    public float getSpawnChance(int time) {
        int spawnInterval = 2 * 60 * 1000;
        return (time % spawnInterval) / (float) spawnInterval * time / (10 * 60 * 1000.0f); //Peak spawn rate every 2 minutes, increasing up to 10 minutes
    }

    @Override
    public int getExplosionDelay() {
        return KittenPoof.EXPLOSION_DELAY;
    }

    @Override
    public void onDeath() {
    	Game.INSTANCE.getWorld().addFrag();
        Game.INSTANCE.getWorld().addEntity(new KittenPoof(x, y, dx, dy));
    }
}

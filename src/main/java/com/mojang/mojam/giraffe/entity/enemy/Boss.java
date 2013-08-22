package com.mojang.mojam.giraffe.entity.enemy;

import java.util.Random;

import com.mojang.mojam.giraffe.Game;
import com.mojang.mojam.giraffe.Util;
import com.mojang.mojam.giraffe.animator.Direction;
import com.mojang.mojam.giraffe.animator.DirectionalAnimator;
import com.mojang.mojam.giraffe.animator.DirectionalType;
import com.mojang.mojam.giraffe.entity.Mattis;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class Boss extends AbstractEnemy {
    private final DirectionalAnimator animator;

    private float speed = 1 / 16f;
    private Mattis mattis;
    private Mattis[] mattisi;
    private double seekAngle;
    private double angle;
    private double turnspeed;

    public Boss(Vector2f startPos, Mattis[] mattis2) {
        super(startPos, 32, 2500);
        Random r = new Random();
        int target = 0;
        while(mattis2[target].isDead())
        	target = r.nextInt(Game.numPlayers);
        this.mattis = mattis2[target];
        this.mattisi = mattis2;

        animator = new DirectionalAnimator(DirectionalType.FOUR_ROT, true);
        animator.load("kitten_bot.png", 78, 80);

        animator.setAnimation(Direction.SOUTH_EAST, 0, 0, 8, 40);
        animator.setAnimation(Direction.NORTH_EAST, 0, 1, 2, 40);
        animator.autoFill();

        speed += (float) (Math.random() * 1 / 10f);
    }

    @Override
    public AbstractEnemy copy() {
        return new Boss(getPosition(), mattisi);
    }

    @Override
    public void init() {
        seekAngle = angle = random.nextInt(4) * Math.PI / 2f + Math.PI / 4f;
        dx = (float) Math.cos(angle) * speed;
        dy = (float) Math.sin(angle) * speed;
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
        dx = (float) Math.cos(angle) * speed;
        dy = (float) Math.sin(angle) * speed;

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
        return 0;
    }

    @Override
    protected int getFlashDuration() {
        return 50;
    }

    @Override
    public int getDamageOnCollision() {
        return 40 + (int) (Math.random() * 21);
    }

    public void onDeath()
    {
    	Game.INSTANCE.getWorld().addBossFrag();
    }
}

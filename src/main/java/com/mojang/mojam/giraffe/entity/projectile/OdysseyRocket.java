package com.mojang.mojam.giraffe.entity.projectile;

import com.mojang.mojam.giraffe.Game;
import com.mojang.mojam.giraffe.Util;
import com.mojang.mojam.giraffe.animator.Direction;
import com.mojang.mojam.giraffe.animator.DirectionalAnimator;
import com.mojang.mojam.giraffe.animator.DirectionalType;
import com.mojang.mojam.giraffe.entity.Entity;
import com.mojang.mojam.giraffe.entity.graphic.Graphic;
import com.mojang.mojam.giraffe.entity.graphic.MissileTrail;
import com.mojang.mojam.giraffe.entity.graphic.Poof;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Vector2f;

public class OdysseyRocket extends BaseProjectile {
    private static final int SIZE = 6;
    private final int damage;
    private final DirectionalAnimator animator;

    private double seekDegrees;
    private double angleDegrees;
    private double turnspeed = Math.random() * 0.02f;
    private float speed;

    private Entity target;

    private int time;

    Vector2f lastTargetPos = new Vector2f(-1, -1);

    private int expirationTime = 1200;

    private int trailDelay = 10;
    private int timeUntilTrail = trailDelay;

    public OdysseyRocket(float x, float y) {
        super(x, y, new Circle(x, y, SIZE / 2));
        damage = 34;
        turnspeed = 0;

        animator = new DirectionalAnimator(DirectionalType.EIGHT, true);
        animator.load("missile.png", 42, 42);

        animator.setAnimation(Direction.SOUTH, 0, 0, 2, 20);
        animator.setAnimation(Direction.SOUTH_EAST, 2, 0, 2, 20);
        animator.setAnimation(Direction.EAST, 4, 0, 2, 20);
        animator.setAnimation(Direction.NORTH_EAST, 6, 0, 2, 20);
        animator.setAnimation(Direction.NORTH, 8, 0, 2, 20);
        animator.autoFill();

        expirationTime += (int) (Math.random() * 1000);
    }

    @Override
    public void update(int delta) {
        super.update(delta);

        if (time > 100 && time < 200) {
            target = Game.INSTANCE.getWorld().getClosestEnemy(this, 300);
        }

        if (target != null) {
            seekDegrees = Math.toDegrees(Math.atan2(y - target.getY(), x - target.getX()));
            for (int i = 0; i < delta; i++) {
                angleDegrees = Util.lerpDegrees(angleDegrees, seekDegrees, turnspeed);
            }
            dx = -(float) Math.cos(Math.toRadians(angleDegrees)) * speed;
            dy = -(float) Math.sin(Math.toRadians(angleDegrees)) * speed;
            Vector2f targetPos = new Vector2f(target.getX(), target.getY());
            if (targetPos.equals(lastTargetPos)) {
                expirationTime -= delta;
            }
            lastTargetPos = targetPos;
        }
        turnspeed = Math.min(0.9f, turnspeed + delta / 80000.0f);
        speed = Math.min(speed + delta / 5000.0f, 1.5f);
        time += delta;

        timeUntilTrail -= delta;
        if (timeUntilTrail < 0) {
            Game.INSTANCE.getWorld().addEntity(new MissileTrail(x, y));
            timeUntilTrail = trailDelay;
        }

        animator.setRotation(angleDegrees);
        animator.update(delta);
    }

    public void draw(Graphics g) {
        animator.draw(x, y);
    }

    public OdysseyRocket setSpeed(float angleDegrees, float speed) {
        this.speed = speed * 0.3f + (float) Math.random() * 0.5f;
        this.angleDegrees = seekDegrees = angleDegrees;
        dx = -(float) Math.cos(Math.toRadians(angleDegrees)) * speed;
        dy = -(float) Math.sin(Math.toRadians(angleDegrees)) * speed;

        return this;
    }

    public int getDamage() {
        return damage;
    }

    public boolean isFinished() {
        return time >= expirationTime;
    }

    @Override
    public Graphic onPoof() {
        return new Poof(x, y);
    }
}

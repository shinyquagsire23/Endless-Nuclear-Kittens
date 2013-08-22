package com.mojang.mojam.giraffe.entity.enemy;

import com.mojang.mojam.giraffe.Game;
import com.mojang.mojam.giraffe.animator.Direction;
import com.mojang.mojam.giraffe.animator.DirectionalAnimator;
import com.mojang.mojam.giraffe.animator.DirectionalType;
import com.mojang.mojam.giraffe.entity.graphic.KittenPoof;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class RandomWalker extends AbstractEnemy {
    private static final float SPEED = 1 / 5f;
    private final DirectionalAnimator animator;
    private double seekAngle;
    private double angle;
    private double turnspeed;
    private int turnTime;
    private int age;

    public RandomWalker(Vector2f startPos) {
        super(startPos, 48 / 4, 100);

        animator = new DirectionalAnimator(DirectionalType.FOUR_ROT, true);
        animator.load("kitten_04.png", 48, 48);

        animator.setAnimation(Direction.SOUTH_EAST, 0, 0, 8, 40);
        animator.setAnimation(Direction.NORTH_EAST, 8, 0, 8, 40);
        animator.autoFill();
    }

    @Override
    public AbstractEnemy copy() {
        return new RandomWalker(getPosition());
    }

    @Override
    public void init() {
        seekAngle = angle = random.nextInt(4) * Math.PI / 2f + Math.PI / 4f;
        dx = (float) Math.cos(angle) * SPEED;
        dy = (float) Math.sin(angle) * SPEED;
    }

    @Override
    public void drawEnemy(Graphics g, boolean flash) {
        animator.draw(x, y, flash);
    }

    @Override
    public void update(int delta) {
        super.update(delta);

        age += delta;

        if (age > turnTime) {
            age = 0;
            turnTime = 500 + (int) (Math.random() * 1000);
            angle = Math.random() * 360;
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
        if (time < 1000 * 60) {
            return 0f;
        }

        time -= 1000 * 60;
        int spawnInterval = (int) (2.5f * 60 * 1000);
        return (time % spawnInterval) / (float) spawnInterval * time / (10 * 60 * 1000.0f); //Peak spawn rate every 2.5 minutes, increasing up to 10 minutes
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

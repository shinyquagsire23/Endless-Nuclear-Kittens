package com.mojang.mojam.giraffe.entity.enemy;

import com.mojang.mojam.giraffe.Game;
import com.mojang.mojam.giraffe.animator.Direction;
import com.mojang.mojam.giraffe.animator.DirectionalAnimator;
import com.mojang.mojam.giraffe.animator.DirectionalType;
import com.mojang.mojam.giraffe.entity.graphic.KittenPoof;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class RandomBouncer extends AbstractEnemy {
    private static final float SPEED = 10f;

    private final DirectionalAnimator animator;

    public RandomBouncer(Vector2f startPos) {
        super(startPos, 48 / 4, 200);

        animator = new DirectionalAnimator(DirectionalType.FOUR_ROT, true);
        animator.load("kitten_fat.png", 48, 48);

        animator.setAnimation(Direction.SOUTH_EAST, 0, 0, 9, 40);
        animator.setAnimation(Direction.NORTH_EAST, 9, 0, 9, 40);
        animator.autoFill();
    }

    @Override
    public AbstractEnemy copy() {
        return new RandomBouncer(getPosition());
    }

    @Override
    public void init() {
        randomizeSpeed();
    }

    @Override
    public void update(int delta) {
        super.update(delta);

        animator.setRotationFromMovementDeltas(dx, dy);
        animator.update(delta);
    }

    @Override
    public void drawEnemy(Graphics g, boolean flash) {
        animator.draw(x, y, flash);
    }

    @Override
    public void collisionX(Rectangle bounds, float x, float newX) {
        randomizeSpeed();
        this.x = x;
    }

    @Override
    public void collisionY(Rectangle bounds, float y, float newY) {
        randomizeSpeed();
        this.y = y;
    }

    private void randomizeSpeed() {
        double angle = random.nextFloat() * Math.PI * 2;
        dx = (float) Math.cos(angle) / SPEED;
        dy = (float) Math.sin(angle) / SPEED;
    }

    @Override
    public float getSpawnChance(int time) {
        int spawnInterval = 1 * 60 * 1000;
        return (time % spawnInterval) / (float) spawnInterval * time / (5 * 60 * 1000.0f); //Peak spawn rate every minute, increasing up to 5 minutes
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

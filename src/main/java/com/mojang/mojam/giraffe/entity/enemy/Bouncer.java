package com.mojang.mojam.giraffe.entity.enemy;

import com.mojang.mojam.giraffe.Game;
import com.mojang.mojam.giraffe.animator.Direction;
import com.mojang.mojam.giraffe.animator.DirectionalAnimator;
import com.mojang.mojam.giraffe.animator.DirectionalType;
import com.mojang.mojam.giraffe.entity.graphic.KittenPoof;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class Bouncer extends AbstractEnemy {
    private final DirectionalAnimator animator;

    private static final float SPEED = 1 / 16f;

    public Bouncer(Vector2f startPos) {
        super(startPos, 48 / 4, 100);

        animator = new DirectionalAnimator(DirectionalType.FOUR_ROT, true);
        animator.load("kitten_02.png", 48, 48);

        animator.setAnimation(Direction.SOUTH_EAST, 0, 0, 8, 40);
        animator.setAnimation(Direction.NORTH_EAST, 8, 0, 8, 40);
        animator.autoFill();
    }

    @Override
    public AbstractEnemy copy() {
        return new Bouncer(getPosition());
    }

    @Override
    public void update(int delta) {
        super.update(delta);

        animator.setRotationFromMovementDeltas(dx, dy);
        animator.update(delta);
    }

    @Override
    public void init() {
        double angle = random.nextInt(4) * Math.PI / 2f + Math.PI / 4f;

        dx = (float) Math.cos(angle) * SPEED;
        dy = (float) Math.sin(angle) * SPEED;
    }

    @Override
    public void drawEnemy(Graphics g, boolean flash) {
        animator.draw(x, y, flash);
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
        return time / (5 * 60 * 1000.0f); //Linear increase up till full spawn rate at 5 minutes
    }

    @Override
    public int getExplosionDelay() {
        return KittenPoof.EXPLOSION_DELAY;
    }

    @Override
    public void onDeath() {
        Game.INSTANCE.getWorld().addEntity(new KittenPoof(x, y, dx, dy));
    }
}

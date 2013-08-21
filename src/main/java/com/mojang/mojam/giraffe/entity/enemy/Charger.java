package com.mojang.mojam.giraffe.entity.enemy;

import com.mojang.mojam.giraffe.Game;
import com.mojang.mojam.giraffe.animator.Direction;
import com.mojang.mojam.giraffe.animator.DirectionalAnimator;
import com.mojang.mojam.giraffe.animator.DirectionalType;
import com.mojang.mojam.giraffe.entity.Mattis;
import com.mojang.mojam.giraffe.entity.graphic.KittenPoof;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class Charger extends AbstractEnemy {
    private final DirectionalAnimator nuclearAnimator;
    private final DirectionalAnimator animator;
    private DirectionalAnimator current;

    private Mattis mattis;
    private double angle;
    private boolean charged = false;
    private int time = 0;
    private float speed = 0.1f;
    private boolean seenMattis;

    private static final int CHARGING_TIME = 2000;
    private static final int KAMIKAZE_TIME = 1000;

    public Charger(Vector2f startPos, Mattis mattis) {
        super(startPos, 48 / 4, 40);
        this.mattis = mattis;

        animator = new DirectionalAnimator(DirectionalType.FOUR_ROT, true);
        animator.load("kitten_05.png", 48, 48);

        animator.setAnimation(Direction.SOUTH_EAST, 0, 0, 8, 40);
        animator.setAnimation(Direction.NORTH_EAST, 8, 0, 8, 40);
        animator.autoFill();

        nuclearAnimator = new DirectionalAnimator(DirectionalType.FOUR_ROT, true);
        nuclearAnimator.load("kitten_nuclear.png", 48, 48);
        nuclearAnimator.setAnimation(Direction.SOUTH_EAST, 0, 0, 8, 40);
        nuclearAnimator.setAnimation(Direction.NORTH_EAST, 8, 0, 8, 40);
        nuclearAnimator.autoFill();

        current = animator;
    }

    @Override
    public AbstractEnemy copy() {
        return new Charger(getPosition(), mattis);
    }

    @Override
    public void init() {
        reset();
    }

    @Override
    public void drawEnemy(Graphics g, boolean flash) {
        float x = this.x;
        float y = this.y;
        if (time > 0 && time < CHARGING_TIME) {
            //flash = true;

            float i = 48 / 4f;
            float scale = i * (float) (time) / (float) CHARGING_TIME;

            float offsetX = (float) (i / 2f - Math.random() * scale);
            float offsetY = (float) (i / 5f - Math.random() * scale);

            x = x - offsetX;
            y = y - offsetY;
        }

        current.draw(x, y, flash);
    }

    @Override
    public void update(int delta) {
        super.update(delta);

        // OMG A MATTIS! MURDER!
        if (!seenMattis && mattis.getPosition().distanceSquared(getPosition()) < 200 * 200) {
            seenMattis = true;
            dx = 0;
            dy = 0;
            current = nuclearAnimator;
        }

        // As soon as we see mattis, start charging
        if (seenMattis) {
            time += delta;
        }

        // Reset everything (including time) to 0 if we've done our attack
        if (time > CHARGING_TIME + KAMIKAZE_TIME) {
            reset();
        } else if (time > CHARGING_TIME) {
            // Mark ourselves charged and hone in onto mattis
            if (!charged) {
                charged = true;
                // lock angle to wherever mattis is
                angle = Math.atan2(mattis.getY() - y, mattis.getX() - x);
            }
            speed = Math.min(speed + delta / 300.0f, 0.6f);
            dx = (float) Math.cos(angle) * speed;
            dy = (float) Math.sin(angle) * speed;
        } else {
            // If we're not charging -- always point at mattis
            angle = Math.atan2(mattis.getY() - y, mattis.getX() - x);
        }

        // Inverse the angle for the proper rotation from the pov of the kitten
        current.setRotation(180 + Math.toDegrees(angle));

        // Walk towards mattis.
        if (!seenMattis) {
            dx = (float) Math.cos(angle) * speed / 3f;
            dy = (float) Math.sin(angle) * speed / 3f;
        }

        current.update(delta);
    }

    private void reset() {
        charged = false;
        speed = 0.1f;
        dx = 0;
        dy = 0;
        time = 0;
        seenMattis = false;
        current = animator;
    }

    @Override
    public void collisionX(Rectangle bounds, float x, float newX) {
        reset();
        this.x = x;
    }

    @Override
    public void collisionY(Rectangle bounds, float y, float newY) {
        reset();
        this.y = y;
    }

    @Override
    public float getSpawnChance(int time) {
        return time / (10 * 60 * 1000.0f); //Linear increase up till full spawn rate at 10 minutes
    }

    @Override
    public int getExplosionDelay() {
        return KittenPoof.EXPLOSION_DELAY;
    }

    @Override
    public void onDeath() {
        Game.INSTANCE.getWorld().addEntity(new KittenPoof(x, y, (float) Math.cos(angle), (float) Math.sin(angle)));
    }
}

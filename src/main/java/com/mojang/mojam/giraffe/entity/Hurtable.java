package com.mojang.mojam.giraffe.entity;

import org.newdawn.slick.Graphics;

public interface Hurtable extends CollidingEntity {
    boolean hurt(Entity source, int damage, float dx, float dy);

    int getHealth();

    int getDamageOnCollision();

    int getExplosionDelay();

    void onDeath();

    void drawShadow(Graphics g);
}

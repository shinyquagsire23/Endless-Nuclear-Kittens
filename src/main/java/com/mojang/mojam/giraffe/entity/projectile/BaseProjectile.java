package com.mojang.mojam.giraffe.entity.projectile;

import com.mojang.mojam.giraffe.entity.BaseEntity;
import org.newdawn.slick.geom.Circle;

public abstract class BaseProjectile extends BaseEntity implements Projectile {
    public BaseProjectile(float x, float y, Circle collider) {
        super(x, y, collider);
    }

    public BaseProjectile setSpeed(float angle, float speed) {
        dx = -(float) Math.cos(Math.toRadians(angle)) * speed;
        dy = -(float) Math.sin(Math.toRadians(angle)) * speed;

        return this;
    }
}

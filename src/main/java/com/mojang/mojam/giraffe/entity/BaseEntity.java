package com.mojang.mojam.giraffe.entity;

import com.mojang.mojam.giraffe.Game;
import com.mojang.mojam.giraffe.Util;
import com.mojang.mojam.giraffe.World;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public abstract class BaseEntity implements CollidingEntity {
    protected float x;
    protected float y;
    protected float dx;
    protected float dy;

    protected final Circle collider;

    public BaseEntity(float x, float y, Circle collider) {
        this.x = x;
        this.y = y;
        this.collider = collider;
    }

    @Override
    public void update(int delta) {
        float oldX = x;
        float oldY = y;
        float newX = x + dx * delta;
        float newY = y + dy * delta;
        x = newX;
        y = newY;
        Rectangle bounds = Game.INSTANCE.getWorld().getBounds();
        Circle collider = getCollider();
        if (!Util.contains(bounds, new Circle(newX, oldY, collider.radius))) {
            collisionX(bounds, oldX, newX);
        }
        if (!Util.contains(bounds, new Circle(oldX, newY, collider.radius))) {
            collisionY(bounds, oldY, newY);
        }
        collider.setCenterX(x);
        collider.setCenterY(y);
    }

    @Override
    public Circle getCollider() {
        return collider;
    }

    public void collisionX(Rectangle bounds, float x, float newX) {
    }

    public void collisionY(Rectangle bounds, float y, float newY) {
    }

    public boolean tryMove(World world, float x, float y) {
        if (world.inBounds(new Circle(x, y, collider.radius))) {
            this.x = x;
            this.y = y;
            return true;
        }

        return false;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public float getDx() {
        return dx;
    }

    @Override
    public float getDy() {
        return dy;
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    public Vector2f getPosition() {
        return new Vector2f(x, y);
    }

    public BaseEntity setPosition(Vector2f pos) {
        x = pos.x;
        y = pos.y;
        return this;
    }
}

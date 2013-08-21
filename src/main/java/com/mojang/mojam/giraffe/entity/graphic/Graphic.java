package com.mojang.mojam.giraffe.entity.graphic;

import com.mojang.mojam.giraffe.entity.Entity;
import org.newdawn.slick.Graphics;

public abstract class Graphic implements Entity {
    protected float x;
    protected float y;
    protected float dx = 0;
    protected float dy = 0;

    protected Graphic(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
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

    public void drawGUI(Graphics g) {}
}

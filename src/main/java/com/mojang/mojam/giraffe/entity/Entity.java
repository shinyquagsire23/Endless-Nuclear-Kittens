package com.mojang.mojam.giraffe.entity;

import org.newdawn.slick.Graphics;

public interface Entity {
    void draw(Graphics g);

    void update(int delta);

    float getX();

    float getY();

    float getDx();

    float getDy();

    boolean isFinished();
}

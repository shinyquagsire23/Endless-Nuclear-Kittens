package com.mojang.mojam.giraffe.entity.graphic;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;

public class Poof extends Graphic {
    private static final int MAX_AGE = 450;
    private int age = 0;

    private final Circle circle;

    public Poof(float x, float y) {
        super(x, y);
        circle = new Circle(x, y, 5);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(new Color(1, 1, 1, .5f * (1 - age / (float) MAX_AGE)));
        g.fill(circle);
    }

    @Override
    public void update(int delta) {
        circle.setRadius(circle.getRadius() + delta / 150f);
        age += delta;
    }

    @Override
    public boolean isFinished() {
        return age > MAX_AGE;
    }
}

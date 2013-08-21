package com.mojang.mojam.giraffe.entity.graphic;

import com.mojang.mojam.giraffe.animator.Direction;
import com.mojang.mojam.giraffe.animator.DirectionalAnimator;
import com.mojang.mojam.giraffe.animator.DirectionalType;
import org.newdawn.slick.Graphics;

public class MissileTrail extends Graphic {
    private final DirectionalAnimator animator;

    private int startDelay = 50;
    private int age = 0;

    public MissileTrail(float x, float y) {
        super(x, y);

        animator = new DirectionalAnimator(DirectionalType.TWO, false);
        animator.load("rocketsmoketrail.png", 32, 32);

        animator.setAnimation(Direction.EAST, 0, 0, 13, 40);
        animator.autoFill();

        animator.randomRotation();
    }

    @Override
    public void draw(Graphics g) {
        if (age > startDelay) {
            animator.draw(x, y);
        }
    }

    @Override
    public void update(int delta) {
        age += delta;
        if (age > startDelay) {
            animator.update(delta);
        }
    }

    @Override
    public boolean isFinished() {
        return animator.isStopped();
    }
}

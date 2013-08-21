package com.mojang.mojam.giraffe.entity.graphic;

import com.mojang.mojam.giraffe.animator.Direction;
import com.mojang.mojam.giraffe.animator.DirectionalAnimator;
import com.mojang.mojam.giraffe.animator.DirectionalType;
import org.newdawn.slick.Graphics;

import java.util.Random;

public class KittenPoof extends Graphic {
    private static final Random RANDOM = new Random();
    public static final int EXPLOSION_DELAY = 200;

    private String[] images = new String[]{
            "kittensplode_01.png",
            "kittensplode_02.png",
            "kittensplode_03.png",
            "kittensplode_04.png"
    };
    private final DirectionalAnimator animator;

    public KittenPoof(float x, float y, float dx, float dy) {
        super(x, y);

        animator = new DirectionalAnimator(DirectionalType.TWO, false);
        animator.load(images[RANDOM.nextInt(images.length)], 64, 64);
        animator.setAnimation(Direction.EAST, 0, 0, 5, 40);
        animator.autoFill();

        animator.setRotationFromMovementDeltas(dx, dy);
    }

    @Override
    public void draw(Graphics g) {
        animator.draw(x, y);
    }

    @Override
    public void update(int delta) {
        animator.update(delta);
    }

    @Override
    public boolean isFinished() {
        return animator.isStopped();
    }
}

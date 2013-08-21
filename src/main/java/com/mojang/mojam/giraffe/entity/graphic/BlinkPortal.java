package com.mojang.mojam.giraffe.entity.graphic;

import com.mojang.mojam.giraffe.animator.Direction;
import com.mojang.mojam.giraffe.animator.DirectionalAnimator;
import com.mojang.mojam.giraffe.animator.DirectionalType;
import org.newdawn.slick.Graphics;

public class BlinkPortal extends Graphic {
    // We can do this -- we're just having this used once
    private static final DirectionalAnimator ANIMATOR = new DirectionalAnimator(DirectionalType.EIGHT, false);

    static {
        ANIMATOR.load("blink_01.png", 78, 80);
        ANIMATOR.appendGlobalFrame(0, 0, 40);
        ANIMATOR.setAnimation(Direction.EAST, 1, 0, 1, 40);
        ANIMATOR.setAnimation(Direction.NORTH_EAST, 2, 0, 1, 40);
        ANIMATOR.autoFill();
    }

    public BlinkPortal(float x, float y, float targetX, float targetY) {
        super(x, y);

        ANIMATOR.setRotationFromMovementDeltas(targetX - x, targetY - y);
    }

    @Override
    public void draw(Graphics g) {
        ANIMATOR.draw(x, y);
    }

    @Override
    public void update(int delta) {
        ANIMATOR.update(delta);
    }

    @Override
    public boolean isFinished() {
        return ANIMATOR.isStopped();
    }
}

package com.mojang.mojam.giraffe.entity;

import com.mojang.mojam.giraffe.Util;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class NukeExplosion extends Explosion {

    public NukeExplosion(float x, float y, float size, long score) {
        super(x, y, size, score, 0, 0);
        explosion = new Animation(Util.loadSpriteSheet(Util.loadImage("kittensplosion.png", Color.white).getScaledCopy(4f), 512, 512), 30);
        explosion.setAutoUpdate(false);
        explosion.setLooping(false);
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
    }

    @Override
    public float getDecreasedSize() {
        return 100;
    }
}

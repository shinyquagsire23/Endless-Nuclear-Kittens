package com.mojang.mojam.giraffe.entity.projectile;

import com.mojang.mojam.giraffe.Util;
import com.mojang.mojam.giraffe.entity.Mattis;
import com.mojang.mojam.giraffe.entity.graphic.BulletHit;
import com.mojang.mojam.giraffe.entity.graphic.Graphic;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Circle;

public class Bullet extends BaseProjectile {
    private static final Image BULLET = Util.loadImage("bullet_01_16.png", Color.white);

    private final int damage;
    private final Mattis owner;

    public Bullet(float x, float y, Mattis owner) {
        super(x, y, new Circle(x, y, 5));
        damage = 12;
        this.owner = owner;
    }

    public void draw(Graphics g) {
        float angle = (float) Math.toDegrees(Math.atan2(dy, dx) + Math.PI / 2);

        g.rotate(x, y, angle);
        g.drawImage(BULLET, x - 8, y - 8);
        g.rotate(x, y, -angle);
    }

    public int getOwnerNum()
    {
    	return owner.getPlayerNum();
    }

    public int getDamage() {
        return damage;
    }

    public boolean isFinished() {
        return false;
    }

    @Override
    public Graphic onPoof() {
        return new BulletHit(x, y);
    }
}

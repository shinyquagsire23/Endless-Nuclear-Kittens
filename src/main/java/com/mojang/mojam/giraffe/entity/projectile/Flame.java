package com.mojang.mojam.giraffe.entity.projectile;

import com.mojang.mojam.giraffe.Util;
import com.mojang.mojam.giraffe.entity.Mattis;
import com.mojang.mojam.giraffe.entity.graphic.Graphic;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;

public class Flame extends BaseProjectile {
    private final float rotation;
    private int age = 0;
    private Animation anim;
    private final Mattis owner;

    public Flame(float x, float y, float angle, Mattis owner) {
        super(x, y, new Circle(x, y, 5));
        this.owner = owner;
        this.rotation = Math.random() < 0.5 ? 90 : 0;
        anim = new Animation(Util.loadSpriteSheet("flamey.png", 50, 52), 25);
        anim.setAutoUpdate(false);
        anim.setLooping(true);

        setSpeed(angle, 0.4f);
    }

    @Override
    public void update(int delta) {
        age += delta;

        collider.setRadius(5f * (float) age / 35f);
        anim.update(delta);

        super.update(delta);
    }

    @Override
    public boolean isFinished() {
        return age > 300;
    }

    @Override
    public int getDamage() {
        return 200;
    }

    @Override
    public Graphic onPoof() {
        return null;
    }

    @Override
    public void draw(Graphics g) {
        g.rotate(x, y, rotation);
        anim.draw(x - 26, y - 26);
        g.rotate(x, y, -rotation);
    }

	
	public int getOwnerNum()
	{
		return owner.getPlayerNum();
	}
}

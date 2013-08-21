package com.mojang.mojam.giraffe.entity;

import com.mojang.mojam.giraffe.Game;
import com.mojang.mojam.giraffe.WeaponSlot;
import com.mojang.mojam.giraffe.weapon.HealthBox;
import com.mojang.mojam.giraffe.weapon.Weapon;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;

public class Pickup extends BaseEntity {
    private final Weapon weapon;

    private static final int DURATION = 5000;
    private static final int FADE_TIME = 250;

    private int time = 0;

    private Animation anim;

    public Pickup(float x, float y, Weapon weapon) {
        super(x, y, new Circle(x, y, 16));
        this.weapon = weapon;
        anim = new Animation(weapon.getPickupSheet(), 40);
        anim.setLooping(true);
        anim.setAutoUpdate(false);
    }

    @Override
    public void update(int delta) {
        time += delta;
        anim.update(delta);
    }

    public void draw(Graphics g) {
        float alpha = 1;
        if (time > DURATION - FADE_TIME) {
            alpha = 1 - (time - (DURATION - FADE_TIME)) / (float) FADE_TIME;
        }
        anim.draw(x - 38 / 2, y - 59 / 2, new Color(1, 1, 1, alpha));
    }

    public String getPickupString() {
        return weapon.getName();
    }

    public void onPickup(Mattis mattis) {
        Game.playSound(Game.SOUND_PICKUP, 1.0f, 0.5f);

        if (weapon instanceof HealthBox) {
            mattis.setHealth(Math.min(100, mattis.getHealth() + weapon.getPower()));
            return;
        }

        final WeaponSlot slot = weapon.getSlot();
        if (!mattis.hasWeapon(slot)) {
            mattis.setWeapon(weapon.copy());
        } else {
            Weapon currentWeapon = mattis.getWeapon(slot);
            if (currentWeapon.getClass().equals(this.weapon.getClass())) {
                currentWeapon.setPower(weapon.getPower() + currentWeapon.getPower());
            } else {
                mattis.setWeapon(weapon.copy());
            }
        }
    }

    @Override
    public boolean isFinished() {
        return time >= DURATION;
    }
}

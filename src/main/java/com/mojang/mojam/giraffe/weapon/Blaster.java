package com.mojang.mojam.giraffe.weapon;

import com.mojang.mojam.giraffe.Game;
import com.mojang.mojam.giraffe.WeaponSlot;
import com.mojang.mojam.giraffe.entity.CollidingEntity;
import com.mojang.mojam.giraffe.entity.Mattis;
import com.mojang.mojam.giraffe.entity.projectile.Bullet;
import com.mojang.mojam.giraffe.entity.projectile.Projectile;

import java.util.ArrayList;
import java.util.List;

public class Blaster extends AbstractWeapon {
    private final float SPACING = 11f;

    public Blaster(final Mattis[] mattis, int power) {
        super(mattis, power, "box_blaster.png");
    }

    @Override
    public int getFireDelay() {
        return 100;
    }

    @Override
    public boolean hasAmmo() {
        return true;
    }

    @Override
    public List<? extends CollidingEntity> shoot(int player) {
        Game.playSound(Game.SOUND_BLASTER, 1.0f, 0.1f);
        final List<Projectile> result = new ArrayList<Projectile>();
        int bullets = power;

        final float middle = (float) (bullets - 1) / 2f * SPACING;

        for (int i = 0; i < bullets; i++) {
            final float offset = SPACING * i - middle;
            final Projectile bullet = new Bullet(mattis[player].getGunX(offset), mattis[player].getGunY(offset),mattis[player]).setSpeed(mattis[player].getRotation(), 0.8f);
            result.add(bullet);
        }

        return result;
    }

    @Override
    public WeaponSlot getSlot() {
        return WeaponSlot.PRIMARY;
    }

    @Override
    public String toString() {
        return getName() + " " + power;
    }

    @Override
    public String getName() {
        return "Blaster";
    }

    @Override
    public void setPower(int power) {
        this.power = Math.min(4, power);
    }

    @Override
    public Weapon copy() {
        return new Blaster(mattis, power);
    }

    @Override
    public double getWeight() {
        return 1f;
    }
}

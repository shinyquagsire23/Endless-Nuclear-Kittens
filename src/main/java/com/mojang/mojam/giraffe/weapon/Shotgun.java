package com.mojang.mojam.giraffe.weapon;

import com.mojang.mojam.giraffe.Game;
import com.mojang.mojam.giraffe.WeaponSlot;
import com.mojang.mojam.giraffe.entity.CollidingEntity;
import com.mojang.mojam.giraffe.entity.Mattis;
import com.mojang.mojam.giraffe.entity.projectile.Projectile;
import com.mojang.mojam.giraffe.entity.projectile.ShotgunPellet;

import java.util.ArrayList;
import java.util.List;

public class Shotgun extends AbstractWeapon {
    private static final int MAX_LEVEL = 4;
    private static final int SPREAD_PER_POWER = 5;
    private static final int SPREAD_DECREASE_PER_OVERPOWER = 3;

    public Shotgun(Mattis[] mattis, int power) {
        super(mattis, power, "box_shotgun.png");
    }

    @Override
    public int getFireDelay() {
        return 1000;
    }

    @Override
    public boolean hasAmmo() {
        return true;
    }

    @Override
    public List<? extends CollidingEntity> shoot(int player) {
        Game.playSound(Game.SOUND_SHOTGUN);
        mattis[player].knockback(16);
        List<Projectile> result = new ArrayList<Projectile>();

        int bullets = 12 + 3 * Math.min(MAX_LEVEL, power);
        int cone = 55;

        float spread = (float) cone / bullets;

        for (int i = 0; i < bullets; i++) {
            final ShotgunPellet bullet = new ShotgunPellet(mattis[player].getGunX(), mattis[player].getGunY());
            bullet.setSpeed(mattis[player].getRotation() + spread * i - (float) cone / 2, 0.8f);
            result.add(bullet);
        }
        return result;
    }

    @Override
    public WeaponSlot getSlot() {
        return WeaponSlot.SECONDARY;
    }

    @Override
    public void setPower(int power) {
        this.power = Math.min(MAX_LEVEL, power);
    }

    @Override
    public Weapon copy() {
        return new Shotgun(mattis, power);
    }

    @Override
    public String toString() {
        return "Shotgun " + power;
    }

    @Override
    public double getWeight() {
        return 4f;
    }
}

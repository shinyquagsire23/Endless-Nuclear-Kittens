package com.mojang.mojam.giraffe.weapon;

import com.mojang.mojam.giraffe.Game;
import com.mojang.mojam.giraffe.WeaponSlot;
import com.mojang.mojam.giraffe.entity.CollidingEntity;
import com.mojang.mojam.giraffe.entity.Entity;
import com.mojang.mojam.giraffe.entity.Mattis;
import com.mojang.mojam.giraffe.entity.projectile.OdysseyRocket;
import com.mojang.mojam.giraffe.entity.projectile.Projectile;

import java.util.ArrayList;
import java.util.List;

public class Iliad extends AbstractWeapon {
    private static final int MAX_LEVEL = 16;
    private static final int SPREAD_PER_POWER = 10;

    public Iliad(Mattis[] mattis, int power) {
        super(mattis, power, "box_homing.png");
    }

    @Override
    public int getFireDelay() {
        return Math.max(1000, 3000 - power * 350);
    }

    @Override
    public boolean hasAmmo() {
        return true;
    }

    @Override
    public List<? extends CollidingEntity> shoot(int player) {
        Game.playSound(Game.SOUND_ROCKETLAUNCH);
        List<Projectile> result = new ArrayList<Projectile>();

        int bullets = 2 + Math.min(MAX_LEVEL, power);
        int cone = 20 + Math.min(MAX_LEVEL, power) * SPREAD_PER_POWER;

        float spread = (float) cone / bullets;

        for (int i = 0; i < bullets; i++) {
            final OdysseyRocket bullet = new OdysseyRocket(mattis[player].getGunX(), mattis[player].getGunY(),mattis[player]).setSpeed(mattis[player].getRotation() + spread * i - (float) cone / 2, 0.5f);
            result.add(bullet);
        }
        return result;
    }

    @Override
    public WeaponSlot getSlot() {
        return WeaponSlot.SECONDARY;
    }

    @Override
    public Weapon copy() {
        return new Iliad(mattis, power);
    }

    @Override
    public void setPower(int power) {
        this.power = Math.min(power, MAX_LEVEL);
    }

    @Override
    public String toString() {
        return getName() + " " + power;
    }

    @Override
    public String getName() {
        return "Homeros";
    }

    @Override
    public double getWeight() {
        return 3f;
    }
}

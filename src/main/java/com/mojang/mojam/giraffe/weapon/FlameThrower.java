package com.mojang.mojam.giraffe.weapon;

import com.mojang.mojam.giraffe.Game;
import com.mojang.mojam.giraffe.WeaponSlot;
import com.mojang.mojam.giraffe.entity.CollidingEntity;
import com.mojang.mojam.giraffe.entity.Mattis;
import com.mojang.mojam.giraffe.entity.projectile.Flame;

import java.util.Arrays;
import java.util.List;

public class FlameThrower extends AbstractWeapon {

    public FlameThrower(Mattis[] mattis, int power) {
        super(mattis, power, "box_flamethrower.png");
    }

    @Override
    public int getFireDelay() {
        return 20;
    }

    @Override
    public boolean hasAmmo() {
        return power > 0;
    }

    @Override
    public List<? extends CollidingEntity> shoot(int player) {
        Game.playSound(Game.SOUND_FLAMETHROWER, 1.0f, 0.03f);
        power--;
        final Flame flame = new Flame(mattis[player].getGunX(), mattis[player].getGunY(), mattis[player].getRotation());

        return Arrays.asList(flame);
    }

    @Override
    public WeaponSlot getSlot() {
        return WeaponSlot.SECONDARY;
    }

    @Override
    public Weapon copy() {
        return new FlameThrower(mattis, power);
    }

    @Override
    public String toString() {
        return getName() + " [" + power + "]";
    }

    @Override
    public String getName() {
        return "Flamer";
    }

    @Override
    public double getWeight() {
        return 2f;
    }
}

package com.mojang.mojam.giraffe.weapon;

import com.mojang.mojam.giraffe.WeaponSlot;
import com.mojang.mojam.giraffe.entity.CollidingEntity;
import com.mojang.mojam.giraffe.entity.Mattis;
import com.mojang.mojam.giraffe.entity.NukeExplosion;

import java.util.Arrays;
import java.util.List;

public class Nuke extends AbstractWeapon {

    private static final int LESS_THAN_NINE_THOUSAND = 1001;

    private static final int MAX_POWER = 5;
    public Nuke(Mattis[] mattis, int power) {
        super(mattis, power, "box_nuke.png");
    }

    @Override
    public int getFireDelay() {
        return 1000;
    }

    @Override
    public boolean hasAmmo() {
        return power > 0;
    }

    @Override
    public List<? extends CollidingEntity> shoot(int player) {
        power--;

        return Arrays.asList(new NukeExplosion(mattis[player].getX(), mattis[player].getY(), LESS_THAN_NINE_THOUSAND, 2));
    }

    @Override
    public void setPower(int power) {
        this.power = Math.min(MAX_POWER, power);
    }

    @Override
    public WeaponSlot getSlot() {
        return WeaponSlot.PANIC;
    }

    @Override
    public Weapon copy() {
        return new Nuke(mattis, power);
    }

    @Override
    public String toString() {
        return getName() + " (" + power + ")";
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName() ;
    }

    @Override
    public double getWeight() {
        return 1f;
    }
}

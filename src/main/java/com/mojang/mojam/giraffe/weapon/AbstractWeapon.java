package com.mojang.mojam.giraffe.weapon;

import com.mojang.mojam.giraffe.Util;
import com.mojang.mojam.giraffe.entity.Mattis;
import org.newdawn.slick.Color;
import org.newdawn.slick.SpriteSheet;

public abstract class AbstractWeapon implements Weapon {
    protected final Mattis mattis;
    protected int power;
    protected SpriteSheet pickupSheet;

    public AbstractWeapon(Mattis mattis, int power, String pickupSheetPath) {
        this.mattis = mattis;
        setPower(power);
        pickupSheet = Util.loadSpriteSheet(pickupSheetPath, 38, 59);
    }

    @Override
    public int getPower() {
        return power;
    }

    @Override
    public void setPower(int power) {
        this.power = power;
    }

    @Override
    public String toString() {
        return getName() + " [" + power + "]";
    }

    @Override
    public SpriteSheet getPickupSheet() {
        return pickupSheet;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}

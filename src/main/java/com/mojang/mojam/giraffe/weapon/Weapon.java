package com.mojang.mojam.giraffe.weapon;

import com.mojang.mojam.giraffe.WeaponSlot;
import com.mojang.mojam.giraffe.entity.CollidingEntity;
import org.newdawn.slick.SpriteSheet;

import java.util.List;

public interface Weapon {
    int getFireDelay();

    boolean hasAmmo();

    List<? extends CollidingEntity> shoot(int player);

    SpriteSheet getPickupSheet();

    WeaponSlot getSlot();

    void setPower(int units);

    int getPower();

    Weapon copy();

    double getWeight();

    String getName();
}

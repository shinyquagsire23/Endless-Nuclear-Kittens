package com.mojang.mojam.giraffe.weapon;

import com.mojang.mojam.giraffe.WeaponSlot;
import com.mojang.mojam.giraffe.entity.CollidingEntity;
import com.mojang.mojam.giraffe.entity.Mattis;

import java.util.Collections;
import java.util.List;

public class HealthBox extends AbstractWeapon {
    public HealthBox(final Mattis[] mattis, int power) {
        super(mattis, power, "box_health.png");
    }

    @Override
    public int getFireDelay() {
        return 0;
    }

    @Override
    public boolean hasAmmo() {
        return true;
    }

    @Override
    public List<? extends CollidingEntity> shoot(int player) {
        return Collections.emptyList();
    }

    @Override
    public WeaponSlot getSlot() {
        return null;
    }

    @Override
    public Weapon copy() {
        return new HealthBox(mattis, power);
    }

    @Override
    public double getWeight() {
        return 1f;
    }

    @Override
    public String getName() {
        return "Hugs box";
    }
}

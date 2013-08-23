package com.mojang.mojam.giraffe.entity.projectile;

import com.mojang.mojam.giraffe.entity.CollidingEntity;
import com.mojang.mojam.giraffe.entity.graphic.Graphic;

public interface Projectile extends CollidingEntity {
    int getDamage();
    int getOwnerNum();
    Graphic onPoof();
}

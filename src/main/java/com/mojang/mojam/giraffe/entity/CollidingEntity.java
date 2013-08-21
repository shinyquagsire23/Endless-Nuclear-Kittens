package com.mojang.mojam.giraffe.entity;

import org.newdawn.slick.geom.Circle;

public interface CollidingEntity extends Entity {
    Circle getCollider();
}

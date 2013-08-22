package com.mojang.mojam.giraffe;

import com.mojang.mojam.giraffe.entity.Mattis;
import com.mojang.mojam.giraffe.entity.Pickup;
import com.mojang.mojam.giraffe.weapon.*;

import java.util.ArrayList;
import java.util.List;

public class PickupSpawner {
    final List<Weapon> weapons = new ArrayList<Weapon>();

    public PickupSpawner(Mattis[] mattis) {
        weapons.add(new Blaster(mattis, 1));
        weapons.add(new Shotgun(mattis, 1));
        weapons.add(new Nuke(mattis, 1));
        weapons.add(new Blink(mattis, 1));
        weapons.add(new Iliad(mattis, 1));
        weapons.add(new FlameThrower(mattis, 150));
        weapons.add(new HealthBox(mattis, 20));
    }

    public Pickup spawnPickup(float x, float y) {
        double totalWeight = 0.0d;
        for (Weapon w : weapons) {
            totalWeight += w.getWeight();
        }
        int randomIndex = -1;
        double random = Math.random() * totalWeight;
        for (int i = 0; i < weapons.size(); i++) {
            random -= weapons.get(i).getWeight();
            if (random <= 0.0d) {
                randomIndex = i;
                break;
            }
        }
        Weapon weapon = weapons.get(randomIndex);
        return new Pickup(x, y, weapon);
    }
}

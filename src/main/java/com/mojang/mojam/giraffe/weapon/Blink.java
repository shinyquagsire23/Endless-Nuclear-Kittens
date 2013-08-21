package com.mojang.mojam.giraffe.weapon;

import com.mojang.mojam.giraffe.Game;
import com.mojang.mojam.giraffe.Util;
import com.mojang.mojam.giraffe.WeaponSlot;
import com.mojang.mojam.giraffe.entity.CollidingEntity;
import com.mojang.mojam.giraffe.entity.Explosion;
import com.mojang.mojam.giraffe.entity.Mattis;
import com.mojang.mojam.giraffe.entity.graphic.BlinkExit;
import com.mojang.mojam.giraffe.entity.graphic.BlinkParticle;
import com.mojang.mojam.giraffe.entity.graphic.BlinkPortal;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Blink extends AbstractWeapon {
    private static final int RANGE = 250;
    private static final int MAX_POWER = 5;
    public static final Random RANDOM = new Random();
    private Rectangle collider;

    public Blink(Mattis mattis, int power) {
        super(mattis, power, "box_teleport.png");
    }

    private Rectangle getCollider() {
        if (collider != null) return collider;

        final Rectangle bounds = Game.INSTANCE.getWorld().getBounds();

        float radius = mattis.getCollider().getRadius();
        float minX = bounds.getMinX() + radius;
        float minY = bounds.getMinY() + radius;

        collider = new Rectangle(minX, minY, bounds.getWidth() - 2 * radius, bounds.getHeight() - 2 * radius);

        return collider;
    }

    @Override
    public int getFireDelay() {
        return 6000 - power * 1000;
    }

    @Override
    public boolean hasAmmo() {
        return true;
    }

    @Override
    public List<? extends CollidingEntity> shoot() {
        Game.playSound(Game.SOUND_TELEPORT);
        // Project Mattis forward and explode
        float x = mattis.getX();
        float y = mattis.getY();

        float rotation = mattis.getRotation();
        float dx = RANGE * (float) Math.cos(Math.PI + Math.toRadians(rotation));
        float dy = RANGE * (float) Math.sin(Math.PI + Math.toRadians(rotation));

        Point destination = new Point(x + dx, y + dy);

        float targetX;
        float targetY;
        if (!getCollider().contains(destination)) {
            Line line = new Line(x, y, dx, dy, true);

            Vector2f intersection = getIntersection(line);
            targetX = intersection.x;
            targetY = intersection.y;
        } else {
            targetX = destination.getX();
            targetY = destination.getY();
        }

        mattis.setX(targetX);
        mattis.setY(targetY);

        Game.INSTANCE.getWorld().addEntity(getBlinkParticles(x, y, targetX, targetY));
        Game.INSTANCE.getWorld().addEntity(new BlinkPortal(x, y, targetX, targetY));
        Game.INSTANCE.getWorld().addEntity(new BlinkExit(targetX, targetY));

        return Arrays.asList(new Explosion(targetX, targetY, 150, 8, 0, 0));
    }

    private List<BlinkParticle> getBlinkParticles(float x, float y, float targetX, float targetY) {
        List<BlinkParticle> result = new ArrayList<BlinkParticle>();

        float deltaX = (targetX - x) / 100f;
        float deltaY = (targetY - y) / 100f;

        int delta = 40;
        for (int i = 0; i < 4; i++) {
            int dx = RANDOM.nextInt(delta) - 20;
            int dy = RANDOM.nextInt(delta) - 20;
            int start = 10 + RANDOM.nextInt(30);
            int end = 10 + RANDOM.nextInt(30);
            result.add(new BlinkParticle(x + dx + deltaX * start, y + dy + deltaY * start, targetX + dx - deltaX * end, targetY + dy - deltaY * end, (100f - start - end) / 100f));
        }

        return result;
    }

    private Vector2f getIntersection(Line line) {
        float minX = collider.getMinX();
        float minY = collider.getMinY();
        float maxX = collider.getMaxX();
        float maxY = collider.getMaxY();

        // Top
        Vector2f top = line.intersect(new Line(minX, minY, maxX, minY), true);
        if (top != null) return top;
        // Bottom
        Vector2f bottom = line.intersect(new Line(minX, maxY, maxX, maxY), true);
        if (top != bottom) return bottom;
        // Left
        Vector2f left = line.intersect(new Line(minX, minY, minX, maxY), true);
        if (top != left) return left;
        // Right
        Vector2f right = line.intersect(new Line(maxX, minY, maxX, maxY), true);
        if (top != right) return right;
        return top;
    }

    @Override
    public void setPower(int power) {
        this.power = Math.min(MAX_POWER, power);
    }

    @Override
    public WeaponSlot getSlot() {
        return WeaponSlot.SECONDARY;
    }

    @Override
    public Weapon copy() {
        return new Blink(mattis, power);
    }

    @Override
    public String toString() {
        return getName() + " " +power;
    }

    @Override
    public String getName() {
        return "Translocator";
    }

    @Override
    public double getWeight() {
        return 3f;
    }
}

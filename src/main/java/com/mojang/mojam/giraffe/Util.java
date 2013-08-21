package com.mojang.mojam.giraffe;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;

public class Util {
    public static Image loadImage(String name) {
        try {
            Image img = new Image(name);
            img.setFilter(Image.FILTER_NEAREST);
            return img;
        } catch (SlickException e) {
            throw new RuntimeException("Unable to load image: " + name, e);
        }
    }
    public static Image loadImage(String name, Color white) {
        try {
            Image img = new Image(name, white);
            img.setFilter(Image.FILTER_NEAREST);
            return img;
        } catch (SlickException e) {
            throw new RuntimeException("Unable to load image: " + name, e);
        }
    }

    public static SpriteSheet loadSpriteSheet(Image image, int width, int height) {
        SpriteSheet sheet = new SpriteSheet(image, width, height);
        sheet.setFilter(Image.FILTER_NEAREST);
        return sheet;
    }

    public static SpriteSheet loadSpriteSheet(String name, int width, int height) {
        try {
            SpriteSheet sheet = new SpriteSheet(name, width, height, Color.white);
            sheet.setFilter(Image.FILTER_NEAREST);
            return sheet;
        } catch (SlickException e) {
            throw new RuntimeException("Unable to load spritesheet: " + name, e);
        }
    }

    public static double lerpDegrees(double start, double end, double amount) {
        double difference = Math.abs(end - start);
        if (difference > 180) {
            if (end > start) {
                start += 360;
            } else {
                end += 360;
            }
        }
        double value = (start + ((end - start) * amount));
        double rangeZero = 360;
        if (value >= 0 && value <= 360) {
            return value;
        }
        return (value % rangeZero);
    }

    public static boolean contains(Rectangle r, Circle c) {
        if (c.getCenterX() - r.getX() < c.radius || c.getCenterY() - r.getY() < c.radius || r.getMaxX() - c.getCenterX() < c.radius || r.getMaxY() - c.getCenterY() < c.radius) {
            return false;
        }
        return true;
    }

    public static Image rotate(Image image, int angle) {
        image.rotate(angle);
        return image;
    }

    public static int getDirectionForRotation(double rotation) {
        return (int) ((((int) rotation + 360) % 360 + 45 / 2f) / 45) % 8;
    }
}

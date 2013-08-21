package com.mojang.mojam.giraffe.entity.graphic;

import com.mojang.mojam.giraffe.Game;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

public class PickupGraphic extends Graphic {
    private static final int MAX_AGE = 1500;
    private int totalTime = 0;
    private int age = 0;
    private int delay;

    private String pickupName;
    private Vector2f screensize;

    public PickupGraphic(Vector2f screensize, String pickupName, int delay) {
        super(0, 0);
        this.pickupName = pickupName;
        this.screensize = screensize;
        this.delay = delay;
    }

    @Override
    public void draw(Graphics g) {
    }

    @Override
    public void drawGUI(Graphics g) {
        if (totalTime <= delay) {
            return;
        }
        float factor = (age < MAX_AGE / 2 ? 1 : 1 - (age - MAX_AGE / 2) / (MAX_AGE / 2f));
        g.setColor(new Color(1, 1, 1, 1f * factor));
        g.setFont(Game.FONT_MENU);
        g.scale(1 / 2f, 1 / 2f);
        g.drawString(pickupName, (pickupName.equals("Translocator") ? 70 : 10) + screensize.x / 2 - Game.FONT_MENU.getWidth(pickupName) / 2, screensize.y - 48 - (int) ((1 - factor) * 48));
        g.scale(2f, 2f);
        g.setFont(Game.FONT);
    }

    @Override
    public void update(int delta) {
        totalTime += delta;
        if (totalTime > delay) {
            age += delta;
        }
    }

    @Override
    public boolean isFinished() {
        return age > MAX_AGE;
    }
}

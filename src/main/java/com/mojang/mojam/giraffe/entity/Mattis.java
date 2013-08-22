package com.mojang.mojam.giraffe.entity;

import com.mojang.mojam.giraffe.Game;
import com.mojang.mojam.giraffe.Util;
import com.mojang.mojam.giraffe.WeaponSlot;
import com.mojang.mojam.giraffe.World;
import com.mojang.mojam.giraffe.animator.Direction;
import com.mojang.mojam.giraffe.animator.DirectionalAnimator;
import com.mojang.mojam.giraffe.animator.DirectionalType;
import com.mojang.mojam.giraffe.weapon.*;

import org.lwjgl.input.Controllers;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.zzl.minegaming.engine.MultiControls;
import org.zzl.minegaming.engine.XBOXButtons;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mattis extends BaseEntity implements Hurtable {

    private static final int INVINCIBILITY_TIME_AFTER_BLINK = 250;
    private static final int RED_FLASH_DURATION = 200;

    private final DirectionalAnimator muzzleAnimator;
    private final DirectionalAnimator idleAnimator;
    private final DirectionalAnimator shootingAnimator;
    private final DirectionalAnimator walkingAnimator;
    private final DirectionalAnimator shotgunMuzzleAnimator;
    private DirectionalAnimator currentAnimator;

    private int time = 0;
    private float rotation;
    private boolean isShooting;

    private final Map<WeaponSlot, Weapon> weapons = new HashMap<WeaponSlot, Weapon>();
    private final Map<WeaponSlot, Integer> lastShot = new HashMap<WeaponSlot, Integer>();

    private Vector2f screensize;

    private int health = 100;
    private boolean flash;
    private int flashDuration;
    private int muzzleAnimDuration = -1;
    private int player = 0;
    private boolean isDead = false;

    private int timeSinceBlink = INVINCIBILITY_TIME_AFTER_BLINK;
    private DirectionalAnimator currentMuzzleAnimator;

    public Mattis(float x, float y, Vector2f screensize, int player) {
        super(x, y, new Circle(x, y, 10));
        this.screensize = screensize;
        this.player = player;

        muzzleAnimator = new DirectionalAnimator(DirectionalType.EIGHT, true);
        muzzleAnimator.load("muzzleflash_48.png", 48, 48);

        muzzleAnimator.setAnimation(Direction.SOUTH, 0, 0, 3, 30);
        muzzleAnimator.setAnimation(Direction.SOUTH_EAST, 0, 1, 3, 30);
        muzzleAnimator.setAnimation(Direction.EAST, 0, 2, 3, 30);
        muzzleAnimator.setAnimation(Direction.NORTH_EAST, 0, 3, 3, 30);
        muzzleAnimator.setAnimation(Direction.NORTH, 0, 4, 3, 30);
        muzzleAnimator.autoFill();

        shotgunMuzzleAnimator = new DirectionalAnimator(DirectionalType.EIGHT, true);
        shotgunMuzzleAnimator.load("shotgunblast_02.png", 95, 95);

        shotgunMuzzleAnimator.setAnimation(Direction.EAST, 0, 0, 1, 20);
        shotgunMuzzleAnimator.setAnimation(Direction.SOUTH, 1, 0, 1, 20);
        shotgunMuzzleAnimator.setAnimation(Direction.SOUTH_EAST, 2, 0, 1, 20);
        shotgunMuzzleAnimator.autoFill();

        int duration = 40;
        idleAnimator = new DirectionalAnimator(DirectionalType.EIGHT, true);
        idleAnimator.load("mattis" + (player > 0 ? player + 1 : "") + ".png", 48, 48);
        idleAnimator.setAnimation(Direction.SOUTH, 0, 0, 1, duration);
        idleAnimator.setAnimation(Direction.SOUTH_EAST, 0, 3, 1, duration);
        idleAnimator.setAnimation(Direction.EAST, 0, 6, 1, duration);
        idleAnimator.setAnimation(Direction.NORTH_EAST, 0, 9, 1, duration);
        idleAnimator.setAnimation(Direction.NORTH, 0, 12, 1, duration);
        idleAnimator.autoFill();

        shootingAnimator = new DirectionalAnimator(DirectionalType.EIGHT, true);
        shootingAnimator.load("mattis" + (player > 0 ? player + 1 : "") + ".png", 48, 48);

        shootingAnimator.setAnimation(Direction.SOUTH, 0, 1, 1, duration);
        shootingAnimator.setAnimation(Direction.SOUTH_EAST, 0, 4, 1, duration);
        shootingAnimator.setAnimation(Direction.EAST, 0, 7, 1, duration);
        shootingAnimator.setAnimation(Direction.NORTH_EAST, 0, 10, 1, duration);
        shootingAnimator.setAnimation(Direction.NORTH, 0, 13, 1, duration);
        shootingAnimator.autoFill();


        walkingAnimator = new DirectionalAnimator(DirectionalType.EIGHT, true);
        walkingAnimator.load("mattis" + (player > 0 ? player + 1 : "") + ".png", 48, 48);

        walkingAnimator.setAnimation(Direction.SOUTH, 0, 2, 8, duration);
        walkingAnimator.setAnimation(Direction.SOUTH_EAST, 0, 5, 8, duration);
        walkingAnimator.setAnimation(Direction.EAST, 0, 8, 8, duration);
        walkingAnimator.setAnimation(Direction.NORTH_EAST, 0, 11, 8, duration);
        walkingAnimator.setAnimation(Direction.NORTH, 0, 14, 8, duration);
        walkingAnimator.autoFill();

        currentAnimator = idleAnimator;
        currentMuzzleAnimator = muzzleAnimator;

        //setWeapon(new Blink(this, 5));
        //setWeapon(new Blaster(this, 1));
    }

    @Override
    public void update(int delta) {
        super.update(delta);
        for (int i = 0; i < delta; i++) {
            dx *= 0.99f;
            dy *= 0.99f;
        }
        time += delta;
        currentAnimator.update(delta);
        if (flash) {
            flashDuration += delta;
            if (flashDuration > RED_FLASH_DURATION) {
                flash = false;
                flashDuration = 0;
            }
        }
        if (muzzleAnimDuration >= 0) {
            currentMuzzleAnimator.setRotation(rotation);
            currentMuzzleAnimator.update(delta);
            muzzleAnimDuration += delta;
            if (muzzleAnimDuration >= 150) {
                muzzleAnimDuration = -1;
            }
        }
        timeSinceBlink += delta;
    }

    @Override
    public boolean isFinished() {
        return false;
    }
    
    public boolean isDead()
    {
    	return isDead;
    }
    
    public void kill()
    {
    	isDead = true;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
        currentAnimator = walkingAnimator;

        if (Math.abs(dx) < 0.1f && Math.abs(dy) < 0.1f) {
            if (isShooting) {
                currentAnimator = shootingAnimator;
            } else {
                currentAnimator = idleAnimator;
            }
        }
        currentAnimator.setRotation(rotation);
        currentMuzzleAnimator.setRotation(rotation);
    }

    public void collisionX(Rectangle bounds, float x, float newX) {
        dx = 0;
        this.x = x;
    }

    public void collisionY(Rectangle bounds, float y, float newY) {
        dy = 0;
        this.y = y;
    }

    public void drawShadow(Graphics g) {
        g.setColor(new Color(0, 0, 0, 0.3f));
        g.fillOval(x - 12, y + 10, 24, 12);
    }

    public void draw(Graphics g) {
        int direction = Util.getDirectionForRotation(rotation);
        boolean muzzleUp = direction > Direction.WEST.getIndex() && direction < Direction.EAST.getIndex();

        float muzzleX = (int) x + gunOffsetX[direction];
        float muzzleY = (int) y + gunOffsetY[direction];

        if (muzzleAnimDuration >= 0 && muzzleUp) {
            currentMuzzleAnimator.draw(muzzleX, muzzleY);
        }

        if (timeSinceBlink < INVINCIBILITY_TIME_AFTER_BLINK) {
            currentAnimator.draw(x, y, true, new Color(0, 0.6f, 1.0f));
        } else {
            currentAnimator.draw(x, y, flash, Color.red);
        }

        if (muzzleAnimDuration >= 0 && !muzzleUp) {
            currentMuzzleAnimator.draw(muzzleX, muzzleY);
        }
    }

    public void drawGUI(Graphics g) {
        g.setLineWidth(2);
        g.pushTransform();
        g.translate(0, -64*player);

        WeaponSlot[] weaponSlots = WeaponSlot.values();
        for (int count = 0; count < weaponSlots.length; count++) {
            WeaponSlot slot = weaponSlots[count];

            Weapon weapon = getWeapon(slot);

            int barWidth = 0;
            int barHeight = 8;
            int rowHeight = barHeight + 4;
            int margin = 6;
            float actualScreenSize = screensize.y / 2f;

            int barLocation = count + 1;
            int textWidth = 0;

            if (weapon != null) {
                barWidth = 60;
                int duration = time - lastShot.get(slot);

                // Background
                g.setColor(new Color(0, 0, 0, 0.3f));
                g.fillRect(margin + textWidth, actualScreenSize - margin - rowHeight * barLocation, barWidth, barHeight);

                // Edge
                g.setColor(Game.playerColors[player]);
                g.drawRect(margin + textWidth, actualScreenSize - margin - rowHeight * barLocation, barWidth, barHeight);

                // Bar
                int delay = weapon.getFireDelay();
                g.fillRect(margin + 2 + textWidth, actualScreenSize - margin - rowHeight * barLocation + 2, (barWidth - 4) * Math.min(delay, duration) / (float) delay, barHeight - 4);
            }

            // Draw weapon name
            {
                float offX = margin * (barWidth > 0 ? 2 : 1) + barWidth + textWidth;
                float offY = actualScreenSize - margin - rowHeight * barLocation;
                g.translate(offX, offY);
                g.scale(1 / 2f, 1 / 2f);
                g.setFont(Game.FONT);
                if (weapon == null) 
                {
                    if (slot == WeaponSlot.SECONDARY) 
                    {
                        g.drawString("NO " + slot.toString().toUpperCase() + "", 0, 0);
                    }
                } 
                else 
                {
                    g.drawString(weapon.toString(), 0, 0);
                }
                g.scale(2f, 2f);
                g.translate(-offX, -offY);
            }
        }

        g.scale(1 / 2f, 1 / 2f);
        Font hpFont = Game.FONT_LARGE;
        g.setFont(hpFont);
        String hpString = health + "%";
        g.drawString(hpString, screensize.x - 16 - hpFont.getWidth(hpString), screensize.y - 48);
        g.scale(2f, 2f);
        g.popTransform();
        g.setLineWidth(1);

    }

    public void setWeapon(Weapon weapon) {
        final WeaponSlot slot = weapon.getSlot();
        weapons.put(slot, weapon);
        lastShot.put(slot, 0);
    }

    public List<? extends CollidingEntity> shoot(WeaponSlot slot) {
        if (weapons.containsKey(slot)) {
            int duration = time - lastShot.get(slot);
            Weapon weapon = weapons.get(slot);
            if (duration > weapon.getFireDelay()) {
                if (weapon.hasAmmo()) {
                    muzzleAnimDuration = 0;

                    if (weapon instanceof Shotgun || weapon instanceof Iliad) {
                        currentMuzzleAnimator = shotgunMuzzleAnimator;
                    } else {
                        currentMuzzleAnimator = muzzleAnimator;
                    }

                    lastShot.put(slot, time);
                    List<? extends CollidingEntity> entities = weapon.shoot(player);

                    if (weapon instanceof Blink) {
                        timeSinceBlink = 0;
                    }

                    if (!weapon.hasAmmo()) {
                        weapons.remove(slot);
                    }
                    return entities;
                }
            }
        }
        return null;
    }

    public void handleInput(Input input, World world) {
    	if(isDead)
    		return;
        float speed = 1 / 4.0f;

        float dy = 0f;
        if ((player == 0 && MultiControls.getMode() == MultiControls.P2X_CONTROLLER ? input.isKeyDown(Input.KEY_W) : MultiControls.getAxisValue(player, XBOXButtons.AX_LEFT_STICK_Y) < 0)) {
            dy -= speed;
        }
        if ((player == 0 && MultiControls.getMode() == MultiControls.P2X_CONTROLLER ? input.isKeyDown(Input.KEY_S) : MultiControls.getAxisValue(player, XBOXButtons.AX_LEFT_STICK_Y) > 0)) {
            dy += speed;
        }
        this.dy = dy;

        float dx = 0f;
        if ((player == 0 && MultiControls.getMode() == MultiControls.P2X_CONTROLLER ? input.isKeyDown(Input.KEY_A) : MultiControls.getAxisValue(player, XBOXButtons.AX_LEFT_STICK_X) < 0)) {
            dx -= speed;
        }
        if ((player == 0 && MultiControls.getMode() == MultiControls.P2X_CONTROLLER ? input.isKeyDown(Input.KEY_D) : MultiControls.getAxisValue(player, XBOXButtons.AX_LEFT_STICK_X) > 0)) {
            dx += speed;
        }
        this.dx = dx;

        final Vector2f cam = world.getCameraPosition();
        //float offsetX = (this.x - cam.x) - input.getAbsoluteMouseX() / 2f;
        //float offsetY = (this.y - cam.y) - input.getAbsoluteMouseY() / 2f;
        float offsetX = (this.x - cam.x) - Game.cursorPos[player].x / 2f;
        float offsetY = (this.y - cam.y) - Game.cursorPos[player].y / 2f;
        setRotation((float) Math.toDegrees(Math.atan2(offsetY, offsetX)));

        isShooting = false;
        if ((player == 0 && MultiControls.getMode() == MultiControls.P2X_CONTROLLER ? input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) : MultiControls.getController(player).getAxisValue(XBOXButtons.AX_RIGHT_TRIGGER) > -1)) {
            List<? extends CollidingEntity> entities = shoot(WeaponSlot.PRIMARY);
            world.addEntity(entities);
            isShooting = entities == null || !entities.isEmpty();
        }
        if ((player == 0 && MultiControls.getMode() == MultiControls.P2X_CONTROLLER ? input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON) : MultiControls.getController(player).getAxisValue(XBOXButtons.AX_LEFT_TRIGGER) > -1)) {
            List<? extends CollidingEntity> entities = shoot(WeaponSlot.SECONDARY);
            world.addEntity(entities);
            isShooting = entities == null || !entities.isEmpty();
        }
        if ((player == 0 && MultiControls.getMode() == MultiControls.P2X_CONTROLLER ? input.isKeyPressed(Input.KEY_SPACE) : MultiControls.getController(player).isButtonPressed(XBOXButtons.BTN_RIGHT_THUMB))) {
            List<? extends CollidingEntity> entities = shoot(WeaponSlot.PANIC);
            world.addEntity(entities);
            isShooting = entities == null || !entities.isEmpty();
        }
    }

    public float getRotation() {
        return rotation;
    }

    public int getPlayerNum()
    {
    	return player;
    }
    
    private final float[] gunOffsetX = new float[]{-17, -18, 4.5f, 16, 16, 9, -4.5f, -10};
    private final float[] gunOffsetY = new float[]{7, -3, -10, -2, 7, 11, 12, 11};

    public float getGunX() {
        return getGunX(0);
    }

    public float getGunY() {
        return getGunY(0);
    }

    public float getGunX(float offset) {
        return getGunX(offset, 90);
    }

    public float getGunY(float offset) {
        return getGunY(offset, 90);
    }

    public float getGunX(float offset, int offsetAngle) {
        return (float) (x + gunOffsetX[Util.getDirectionForRotation(rotation)] + offset * Math.cos(Math.toRadians(rotation + offsetAngle)));
    }

    public float getGunY(float offset, int offsetAngle) {
        return (float) (y + gunOffsetY[Util.getDirectionForRotation(rotation)] + offset * Math.sin(Math.toRadians(rotation + offsetAngle)));
    }

    public Weapon getWeapon(WeaponSlot slot) {
        return weapons.get(slot);
    }

    public boolean hasWeapon(WeaponSlot slot) {
        return null != getWeapon(slot);
    }

    public boolean hurt(Entity source, int damage, float dx, float dy) {
        if (timeSinceBlink > INVINCIBILITY_TIME_AFTER_BLINK) {
            Weapon primary = weapons.get(WeaponSlot.PRIMARY);
            primary.setPower(Math.max(primary.getPower() - 1, 1));
            health = Math.max(0, health - damage);
            flash = true;
            flashDuration = 0;
        }
        return health <= 0;
    }

    public int getHealth() {
        return health;
    }

    public int getDamageOnCollision() {
        return 0;
    }

    @Override
    public int getExplosionDelay() {
        return 0;
    }

    @Override
    public void onDeath() {
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void knockback(float force) {
        float newX = x + (float) Math.cos(Math.toRadians(rotation)) * force;
        float newY = y + (float) Math.sin(Math.toRadians(rotation)) * force;
        tryMove(Game.INSTANCE.getWorld(), newX, newY);
    }

    public void setHealth(int health) {
        this.health = health;
    }
}

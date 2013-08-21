package com.mojang.mojam.giraffe;

import com.mojang.mojam.giraffe.entity.*;
import com.mojang.mojam.giraffe.entity.enemy.Boss;
import com.mojang.mojam.giraffe.entity.graphic.Graphic;
import com.mojang.mojam.giraffe.entity.graphic.PickupGraphic;
import com.mojang.mojam.giraffe.entity.projectile.OdysseyRocket;
import com.mojang.mojam.giraffe.entity.projectile.Projectile;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {
    private static final double DROP_SPAWN_CHANCE = 0.1;
    public static final BigInteger TWO = BigInteger.valueOf(2);
    private final Vector2f screensize;
    private final Mattis mattis;
    private final Rectangle bounds;

    private List<Hurtable> enemies = new ArrayList<Hurtable>();
    private List<Projectile> projectiles = new ArrayList<Projectile>();
    private List<Explosion> explosions = new ArrayList<Explosion>();
    private List<Pickup> pickups = new ArrayList<Pickup>();
    private List<Graphic> graphics = new ArrayList<Graphic>();

    private Vector2f seekCam = new Vector2f();
    private Vector2f cam = new Vector2f();

    private BigInteger score = BigInteger.valueOf(0);

    private int shakeDuration = -1;
    private Vector2f shake = new Vector2f(0, 0);

    private PickupSpawner pickupSpawner;

    private static final int SPRITE_W = 0;
    private static final int SPRITE_NW = 1;
    private static final int SPRITE_N = 2;
    private static final int SPRITE_NE = 3;
    private static final int SPRITE_E = 4;
    private static final int SPRITE_SE = 5;
    private static final int SPRITE_S = 6;
    private static final int SPRITE_SW = 7;
    private static final int SPRITE_C = 8;
    private static final int NUM_SPRITES = 9;

    private Image[] lasergrid = new Image[NUM_SPRITES];

    private int pickupDelay = 0;

    public World(Vector2f screensize, Mattis mattis) {
        this.screensize = screensize;
        this.mattis = mattis;
        pickupSpawner = new PickupSpawner(mattis);

        bounds = new Rectangle(0, 0, 256 * 3, 256 * 2);
        cam.x = seekCam.x = -screensize.x / 4.0f + mattis.getX();
        cam.y = seekCam.y = -screensize.y / 4.0f + mattis.getY();

        SpriteSheet sheet = Util.loadSpriteSheet("lasergrid_sheet.png", 128, 128);
        lasergrid[SPRITE_C] = sheet.getSprite(0, 0);
        lasergrid[SPRITE_N] = sheet.getSprite(1, 0);
        lasergrid[SPRITE_S] = sheet.getSprite(1, 0).getFlippedCopy(false, true);
        lasergrid[SPRITE_E] = sheet.getSprite(2, 0);
        lasergrid[SPRITE_W] = sheet.getSprite(2, 0).getFlippedCopy(true, false);
        lasergrid[SPRITE_NW] = sheet.getSprite(3, 0);
        lasergrid[SPRITE_NE] = sheet.getSprite(3, 0).getFlippedCopy(true, false);
        lasergrid[SPRITE_SW] = sheet.getSprite(3, 0).getFlippedCopy(false, true);
        lasergrid[SPRITE_SE] = sheet.getSprite(3, 0).getFlippedCopy(true, true);
    }

    public BigInteger getScore() {
        return score;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Rectangle getSpawnBounds() {
        return new Rectangle(bounds.getX() + 100, bounds.getY() + 100, bounds.getMaxX() - 200, bounds.getMaxY() - 200);
    }

    public Vector2f getCameraPosition() {
        return cam.copy().add(shake);
    }

    public boolean update(int delta) {

        if (shakeDuration >= 0) {
            shakeDuration += delta;
            float amount = 2 + 1 * (1 - shakeDuration / 250f);
            shake = new Vector2f((float) Math.random() * amount - amount * 2, (float) Math.random() * amount - amount * 2);
            if (shakeDuration >= 250) {
                shakeDuration = -1;
                shake = new Vector2f(0, 0);
            }
        }

        pickupDelay *= 0.99f;

        seekCam.x = -screensize.x / 4.0f + mattis.getX();
        seekCam.y = -screensize.y / 4.0f + mattis.getY();
        seekCam.x = Math.max(bounds.getX() - 100, Math.min(bounds.getMaxX() - screensize.x / 2f + 100, seekCam.x));
        seekCam.y = Math.max(bounds.getY() - 100, Math.min(bounds.getMaxY() - screensize.y / 2f + 100, seekCam.y));

        if (cam.distanceSquared(seekCam) > 1f) {
            for (int i = 0; i < delta; i++) {
                cam.x += (seekCam.x - cam.x) * 0.004f;
                cam.y += (seekCam.y - cam.y) * 0.004f;
            }
        }

        List<Projectile> deadProjectiles = new ArrayList<Projectile>();
        List<Hurtable> deadEnemies = new ArrayList<Hurtable>();
        List<Explosion> deadExplosions = new ArrayList<Explosion>();
        List<Pickup> deadPickups = new ArrayList<Pickup>();
        List<Graphic> deadGraphics = new ArrayList<Graphic>();

        List<Explosion> newExplosions = new ArrayList<Explosion>();
        for (Explosion explosion : explosions) {
            explosion.update(delta);
            for (Hurtable other : enemies) {
                if (explosion.getCollider().intersects(other.getCollider())) {
                    if (other.hurt(explosion, 100, 0, 0)) {
                        int explosionDelay = other.getExplosionDelay();
                        newExplosions.add(new Explosion(other.getX(), other.getY(), explosion.getDecreasedSize(), explosion.getScore().multiply(TWO), explosionDelay, explosion.getChain() + 1)); //Woo
                        if (explosionDelay > 0) {
                            other.onDeath();
                        }
                        deadEnemies.add(other);
                        shakeDuration = 0;
                    }
                }
            }
            if (explosion.isFinished()) {
                score = score.add(explosion.getScore());
                deadExplosions.add(explosion);
            }
        }
        explosions.addAll(newExplosions);

        for (Hurtable hurtable : enemies) {
            hurtable.update(delta);
            if (hurtable.getCollider().intersects(mattis.getCollider())) {
                boolean dead = mattis.hurt(hurtable, hurtable.getDamageOnCollision(), 0, 0);
                if (dead) {
                    return true;
                }
                explode(hurtable, 30, 2);
                deadEnemies.add(hurtable);
            }
        }

        for (Pickup pickup : pickups) {
            pickup.update(delta);
            if (pickup.getCollider().intersects(mattis.getCollider())) {
                graphics.add(new PickupGraphic(screensize, pickup.getPickupString(), pickupDelay));
                pickupDelay += 1000;
                pickup.onPickup(mattis);
                deadPickups.add(pickup);
            }
            if (pickup.isFinished()) {
                deadPickups.add(pickup);
            }
        }

        mattis.update(delta);

        projectile:
        for (Projectile projectile : projectiles) {
            projectile.update(delta);
            if (!bounds.contains(projectile.getX(), projectile.getY()) || projectile.isFinished()) {
                deadProjectiles.add(projectile);
                if (projectile.isFinished() && projectile instanceof OdysseyRocket) {
                    addEntity(new Explosion(projectile.getX(), projectile.getY(), 20, 8, 0, 0));
                }
                continue; // Done with this one -- bye!
            }

            for (Hurtable target : enemies) {
                if (!deadEnemies.contains(target) && target.getCollider().intersects(projectile.getCollider())) {
                    Game.playSound(Game.SOUND_BULLET_HIT, 1.0f, 0.8f);
                    addEntity(projectile.onPoof());

                    deadProjectiles.add(projectile);

                    if (target.hurt(projectile, projectile.getDamage(), projectile.getDx(), projectile.getDy())) {
                        explode(target, 100, 8);
                        deadEnemies.add(target);
                    }

                    continue projectile; // Just one hit per bullet
                }
            }
        }

        for (Hurtable deadEnemy : deadEnemies) {
            if (Math.random() < DROP_SPAWN_CHANCE) {
                pickups.add(pickupSpawner.spawnPickup(deadEnemy.getX(), deadEnemy.getY()));
            }
        }

        for (Graphic graphic : graphics) {
            graphic.update(delta);
            if (graphic.isFinished()) {
                deadGraphics.add(graphic);
            }
        }

        graphics.removeAll(deadGraphics);
        pickups.removeAll(deadPickups);
        projectiles.removeAll(deadProjectiles);
        enemies.removeAll(deadEnemies);
        explosions.removeAll(deadExplosions);

        return false;
    }

    public void draw(Graphics g) {
        float tx = Math.round(cam.x + shake.x);
        float ty = Math.round(cam.y + shake.y);

        g.translate(-tx, -ty);
        g.setDrawMode(Graphics.MODE_ADD_ALPHA);
        for (int x = -64; x < bounds.getWidth() + 64; x += 128) {
            for (int y = -64; y < bounds.getHeight() + 64; y += 128) {
                boolean west = x < 0;
                boolean east = x >= bounds.getWidth() - 64;
                boolean north = y < 0;
                boolean south = y >= bounds.getHeight() - 64;
                int sprite = SPRITE_C;
                if (west && north) {
                    sprite = SPRITE_NW;
                } else if (east && north) {
                    sprite = SPRITE_NE;
                } else if (south && west) {
                    sprite = SPRITE_SW;
                } else if (south && east) {
                    sprite = SPRITE_SE;
                } else if (west) {
                    sprite = SPRITE_W;
                } else if (east) {
                    sprite = SPRITE_E;
                } else if (south) {
                    sprite = SPRITE_S;
                } else if (north) {
                    sprite = SPRITE_N;
                }
                g.drawImage(lasergrid[sprite], x, y, new Color(1, 1, 1, 0.3f));
            }
        }
        g.setDrawMode(Graphics.MODE_NORMAL);
        for (Hurtable hurtable : enemies) {
            hurtable.drawShadow(g);
        }
        mattis.drawShadow(g);
        for (Pickup pickup : pickups) {
            pickup.draw(g);
        }
        g.setDrawMode(Graphics.MODE_ADD);
        for (Explosion explosion : explosions) {
            explosion.draw(g);
        }
        g.setDrawMode(Graphics.MODE_NORMAL);
        for (Graphic graphic : graphics) {
            graphic.draw(g);
        }
        for (Projectile projectile : projectiles) {
            projectile.draw(g);
        }
        for (Hurtable hurtable : enemies) {
            hurtable.draw(g);
        }
        mattis.draw(g);
        g.translate(tx, ty);
        for (Graphic graphic : graphics) {
            graphic.drawGUI(g);
        }
        mattis.drawGUI(g);
    }

    public void addEntity(List<? extends Entity> list) {
        if (list == null) return;
        for (Entity entity : list) {
            addEntity(entity);
        }
    }

    public void addEntity(Entity entity) {
        if (entity == null) return;

        if (entity instanceof Projectile) {
            projectiles.add((Projectile) entity);
        } else if (entity instanceof Hurtable) {
            enemies.add((Hurtable) entity);
        } else if (entity instanceof Explosion) {
            explosions.add((Explosion) entity);
        } else if (entity instanceof Graphic) {
            graphics.add((Graphic) entity);
        } else {
            System.out.println("Unable to add type: " + entity);
        }
    }

    private void explode(Hurtable enemy, int size, int score) {
        int explosionDelay = enemy.getExplosionDelay();
        explosions.add(new Explosion(enemy.getX(), enemy.getY(), size, score, explosionDelay, 0));
        if (explosionDelay > 0) {
            enemy.onDeath();
        }
        shakeDuration = 0;
    }

    public Hurtable getClosestEnemy(Entity origin, int maxRange) {
        Hurtable closestEnemy = null;
        float bestDistSquared = Float.MAX_VALUE;
        for (Hurtable e : enemies) {
            float distSquared = new Vector2f(e.getX(), e.getY()).distanceSquared(new Vector2f(origin.getX(), origin.getY()));
            if (distSquared < bestDistSquared && distSquared < maxRange * maxRange) {
                bestDistSquared = distSquared;
                closestEnemy = e;
            }
        }
        return closestEnemy;
    }

    public boolean inBounds(Circle circle) {
        return Util.contains(bounds, circle);
    }

    public boolean isBossAlive() {
        //Bit hacky...
        for (Hurtable hurtable : enemies) {
            if (hurtable instanceof Boss) {
                return true;
            }
        }
        return false;
    }
}

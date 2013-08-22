package com.mojang.mojam.giraffe;

import com.mojang.mojam.giraffe.entity.Mattis;
import com.mojang.mojam.giraffe.entity.enemy.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class Spawner {

    private static final int BOUNCER = 0;
    private static final int RANDOM_BOUNCER = 1;
    private static final int CHARGER = 2;
    private static final int CHASER = 3;
    private static final int RANDOM_WALKER = 4;
    private static final int BOSS = 5;

    private static final int BOSS_SPAWN_INTERVAL = 20000;
    private static final int REGULAR_SPAWN_INTERVAL = 100;

    private int totalTime;
    private int timeSinceRegular = 0;
    private int timeSinceBoss = 0;
    private int nextEventType = 0;
    private int bossCount = 0;

    private final World world;
    private final Mattis[] mattis;

    private List<AbstractEnemy> enemyTypes = new ArrayList<AbstractEnemy>();

    public Spawner(World world, Mattis[] mattis) {
        this.world = world;
        this.mattis = mattis;

        Vector2f zero = new Vector2f(0, 0);
        enemyTypes.add(new Bouncer(zero));
        enemyTypes.add(new RandomBouncer(zero));
        enemyTypes.add(new Charger(zero, mattis));
        enemyTypes.add(new Chaser(zero, mattis));
        enemyTypes.add(new RandomWalker(zero));
        enemyTypes.add(new Boss(zero, mattis));
    }

    public void update(int delta) {

        totalTime += delta;
        spawnBoss(delta);
        spawnRegulars(delta);
    }

    private void createAndAddEnemy(int type, Vector2f position) {
        AbstractEnemy ae = enemyTypes.get(type).copy();
        ae.setPosition(position);
        world.addEntity(ae);
    }

    private void spawnRegulars(int delta) {

        timeSinceRegular += delta;
        float generalSpawnFactor = (world.isBossAlive() ? 0.2f : 1f);
        while (timeSinceRegular > REGULAR_SPAWN_INTERVAL) {
            for (AbstractEnemy e : enemyTypes) {
                if (Math.random() < e.getSpawnChance(totalTime) * generalSpawnFactor) {
                    Random r = new Random();
                    Vector2f spawnPosition = getSpawnPosition(world.getSpawnBounds(), mattis[r.nextInt(Game.numPlayers)]);
                    world.addEntity(e.copy().setPosition(spawnPosition));
                }
            }
            timeSinceRegular -= REGULAR_SPAWN_INTERVAL;
        }

    }

    private void spawnBoss(int delta) {
        timeSinceBoss += delta;
        if (timeSinceBoss > BOSS_SPAWN_INTERVAL) {
            Vector2f centerPos = new Vector2f(world.getBounds().getCenterX(), world.getBounds().getCenterY());
            List<Vector2f> spawnPositions;
            switch (nextEventType) {
                case 0:
                    //Spawn chasers in a circle
                    spawnPositions = getCirclePositions(centerPos, 180, 12);
                    for (Vector2f pos : spawnPositions) {
                        createAndAddEnemy(CHASER, pos);
                    }
                    break;
                case 1:
                    //Spawn chargers in a circle
                    spawnPositions = getCirclePositions(centerPos, 180, 16);
                    for (Vector2f pos : spawnPositions) {
                        createAndAddEnemy(CHARGER, pos);
                    }
                    break;
                case 2:
                    //Spawn robokitten
                    float distance = (bossCount == 0 ? 0 : 200);
                    spawnPositions = getCirclePositions(centerPos, distance, bossCount + 1);
                    for (Vector2f pos : spawnPositions) {
                        createAndAddEnemy(BOSS, pos);
                    }
                    bossCount++;
                    break;
                case 3:
                    //Spawn bouncers along the edges
                    spawnPositions = getCirclePositions(centerPos, 250, 24);
                    for (Vector2f pos : spawnPositions) {
                        createAndAddEnemy(BOUNCER, pos);
                    }
                    break;
                case 4:
                    //Spawn chasers + chargers in a square
                    spawnPositions = getCirclePositions(centerPos, 180, 12);
                    for (Vector2f pos : spawnPositions) {
                        createAndAddEnemy(CHARGER, pos);
                    }
                    spawnPositions = getCirclePositions(centerPos, 250, 16);
                    for (Vector2f pos : spawnPositions) {
                        createAndAddEnemy(CHASER, pos);
                    }
                    break;
            }
            nextEventType = (nextEventType + 1) % 5;
            timeSinceBoss = 0;
        }
    }

    private List<Vector2f> getCirclePositions(Vector2f center, float radius, float numPositions) {
        List<Vector2f> positions = new ArrayList<Vector2f>();
        float angle = 0;
        for (int i = 0; i < numPositions; i++) {
            Vector2f desiredPos = new Vector2f(center.x + radius * (float) Math.cos(angle), center.y + radius * (float) Math.sin(angle));
            Random r = new Random();
            positions.add(getSpawnPosition(world.getSpawnBounds(), mattis[r.nextInt(Game.numPlayers)], desiredPos));
            angle += Math.PI * 2 / numPositions;
        }
        return positions;
    }

    private Vector2f getSpawnPosition(Rectangle spawnBounds, Mattis mattis) {
        return getSpawnPosition(spawnBounds, mattis, mattis.getPosition().copy());
    }

    private Vector2f getSpawnPosition(Rectangle spawnBounds, Mattis mattis, Vector2f desiredSpawnPos) {
        Vector2f mattisPos = mattis.getPosition();
        Vector2f spawnPos = desiredSpawnPos;
        while (spawnPos.distanceSquared(mattisPos) < 150 * 150) {
            spawnPos = new Vector2f((float) (spawnBounds.getX() + Math.random() * spawnBounds.getWidth()), (float) (spawnBounds.getY() + Math.random() * spawnBounds.getHeight()));
        }
        return spawnPos;
    }
}

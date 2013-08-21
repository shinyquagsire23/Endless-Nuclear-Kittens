package com.mojang.mojam.giraffe.animator;

import com.mojang.mojam.giraffe.Util;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

import java.util.*;

public class DirectionalAnimator {

    private static final Random RANDOM = new Random();

    private final DirectionalType type;

    private Set<Direction> usedDirections = new TreeSet<Direction>();
    private final Animation[] animations = new Animation[8];
    private int current;
    private final int[] indices;

    private SpriteSheet sheet;
    private float centerX;
    private float centerY;


    public DirectionalAnimator(DirectionalType type, boolean looping) {
        this.type = type;

        // Setup internals
        final List<Direction> validDirections = type.getValidDirections();
        this.indices = new int[validDirections.size()];
        this.current = validDirections.get(0).getIndex();

        int i = 0;
        for (final Direction direction : validDirections) {
            int index = direction.getIndex();
            animations[index] = new Animation(false); // Never autoupdate!
            animations[index].setLooping(looping);
            indices[i++] = index;
        }
    }

    public boolean isStopped() {
        return animations[current].isStopped();
    }


    public void load(String name, int width, int height) {
        load(name, width, height, null);
    }

    public void load(String name, int width, int height, Float scale) {
        boolean scaled = scale != null;
        Image image = Util.loadImage(name, Color.white);

        if (scaled) {
            height = (int) (height * scale);
            width = (int) (width * scale);
            image = image.getScaledCopy(scale);
        }

        sheet = Util.loadSpriteSheet(image, width, height);
        centerX = (float) width / 2f;
        centerY = (float) height / 2f;
    }

    public void setAnimation(Direction direction, int sheetX, int sheetY, int horizontalCount, int... durations) {
        validateDirection(direction);
        validateSheet();

        usedDirections.add(direction);

        Animation animation = animations[direction.getIndex()];
        for (int i = 0; i < horizontalCount; i++) {
            // Default to the 'last given' if we ran out of duration values
            int duration = durations[(i < durations.length ? i : durations.length - 1)];

            animation.addFrame(sheet.getSprite(sheetX + i, sheetY), duration);
        }
    }

    public void update(long delta) {
        animations[current].update(delta);
    }

    public void randomRotation() {
        current = indices[RANDOM.nextInt(indices.length)];
    }

    // Return a number 0-359.99...
    private double normalizeDegrees(double angleDegrees) {
        return ((angleDegrees % 360) + 360) % 360;
    }

    public void setRotation(double angleDegrees) {
        switch (type) {
            case EIGHT: // 0-45-90-135-180-225-270-315 -> 0,2,3,4,5,6,7,8
                double v1 = normalizeDegrees(angleDegrees + 22.5f);
                current = (int) (v1 / 45);
                break;
            case FOUR: // 45-135-225-315 -> 0,2,4,6
                double v2 = normalizeDegrees(angleDegrees + 45);
                current = 2 * (int) (v2 / 90);
                break;
            case FOUR_ROT: // 0-90-180-270 -> 1,3,5,7
                double v3 = normalizeDegrees(angleDegrees);
                current = 1 + 2 * (int) (v3 / 90);
                break;
            case TWO: // 0-180 -> 0, 4
                double v = normalizeDegrees(angleDegrees);
                current = 4 * (int) (v / 180);
                break;
            default:
                throw new RuntimeException("setRotation(dx,dy) called for unknown type: " + type);
        }
    }

    public void setRotationFromMovementDeltas(float dx, float dy) {
        setRotation(Math.toDegrees(Math.atan2(-dy, -dx)));
    }

    public void draw(float x, float y) {
        draw(x, y, false);
    }

    public void draw(float x, float y, boolean flash) {
        draw(x, y, flash, Color.white);
    }

    public void draw(float x, float y, boolean flash, Color color) {
        if (flash) {
            animations[current].drawFlash(x - centerX, y - centerY, 2 * centerX, 2 * centerY, color);
        } else {
            animations[current].draw(x - centerX, y - centerY);
        }
    }

    public void appendGlobalFrame(int sheetX, int sheetY, int duration) {
        for (int index : indices) {
            animations[index].addFrame(sheet.getSprite(sheetX, sheetY), duration);
        }
    }

    private void validateDirection(Direction direction) {
        if (!type.getValidDirections().contains(direction)) {
            throw new RuntimeException("Invalid direction: " + direction + " given for DirectionType: " + type);
        }
    }

    private void validateSheet() {
        if (sheet == null) {
            throw new RuntimeException("Invalid operation called, no sheet loaded, use: DirectionalAnimator.load(String, I, I)");
        }
    }

    public void autoFill() {
        List<Direction> missingDirections = new ArrayList<Direction>();

        for (int index : indices) {
            if (!usedDirections.contains(Direction.getBy(index))) {
                missingDirections.add(Direction.getBy(index));
            }
        }

        int lastMissingCount = Integer.MAX_VALUE;
        outer:
        while (lastMissingCount != missingDirections.size()) {
            lastMissingCount = missingDirections.size();
            // For any of the leftover directions
            for (Direction missingDirection : missingDirections) {
                // Find a transformation we can do -- in the preferred order as defined in the Transformation
                for (Transformation transformation : Transformation.values()) {
                    // Figure out if we can get to the target
                    for (Way way : transformation.getWaysTo(missingDirection)) {
                        // See if we have any needed data
                        if (usedDirections.contains(way.getFrom())) {
                            doTransformation(transformation, way);
                            missingDirections.remove(missingDirection);
                            continue outer;
                        }
                    }
                }
            }
        }

        if (!missingDirections.isEmpty()) {
            throw new RuntimeException("Unable to get directions: " + missingDirections);
        }
    }

    private void doTransformation(Transformation transformation, Way way) {
        final Direction to = way.getTo();
        final Direction from = way.getFrom();
        transformation.apply(animations[from.getIndex()], animations[to.getIndex()]);
        usedDirections.add(to);
    }
}


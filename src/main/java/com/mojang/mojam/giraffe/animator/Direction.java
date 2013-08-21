package com.mojang.mojam.giraffe.animator;

import java.util.HashMap;
import java.util.Map;

public enum Direction {
    NORTH(2),
    NORTH_EAST(3),
    EAST(4),
    SOUTH_EAST(5),
    SOUTH(6),
    SOUTH_WEST(7),
    WEST(0),
    NORTH_WEST(1);

    private final int index;
    private static final Map<Integer, Direction> BY_INDEX = new HashMap<Integer, Direction>();

    static {
        for (Direction direction : values()) {
            BY_INDEX.put(direction.getIndex(), direction);
        }
    }

    Direction(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static Direction getBy(int index) {
        return BY_INDEX.get(index);
    }
}

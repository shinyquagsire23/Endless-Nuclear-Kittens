package com.mojang.mojam.giraffe.animator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum DirectionalType {
    TWO(Direction.EAST, Direction.WEST),
    FOUR(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST),
    FOUR_ROT(Direction.NORTH_EAST, Direction.SOUTH_EAST, Direction.SOUTH_WEST, Direction.NORTH_WEST),
    EIGHT(Direction.values());

    private final ArrayList<Direction> validDirections;

    DirectionalType(Direction... validDirections) {
        this.validDirections = new ArrayList<Direction>();
        Collections.addAll(this.validDirections, validDirections);
    }

    public List<Direction> getValidDirections() {
        return Collections.unmodifiableList(validDirections);
    }
}

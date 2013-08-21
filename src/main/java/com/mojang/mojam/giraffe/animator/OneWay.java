package com.mojang.mojam.giraffe.animator;

import java.util.Arrays;
import java.util.List;

public class OneWay implements Ways, Way {
    private final Direction from;
    private final Direction to;

    public OneWay(Direction from, Direction to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public List<OneWay> getWays() {
        return Arrays.asList(this);
    }

    @Override
    public Direction getFrom() {
        return from;
    }

    @Override
    public Direction getTo() {
        return to;
    }
}

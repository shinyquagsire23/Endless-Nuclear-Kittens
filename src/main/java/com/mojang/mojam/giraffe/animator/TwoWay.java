package com.mojang.mojam.giraffe.animator;

import java.util.Arrays;
import java.util.List;

public class TwoWay implements Ways {
    private final List<OneWay> ways;

    public TwoWay(Direction from, Direction to) {
        ways = Arrays.asList(new OneWay(from, to), new OneWay(to, from));
    }

    @Override
    public List<OneWay> getWays() {
        return ways;
    }
}

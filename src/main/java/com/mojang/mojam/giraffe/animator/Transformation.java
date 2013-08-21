package com.mojang.mojam.giraffe.animator;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;

import java.util.ArrayList;
import java.util.List;

public enum Transformation implements Ways {
    FLIP_HORIZONTAL(
            new TwoWay(Direction.EAST, Direction.WEST),
            new TwoWay(Direction.NORTH_EAST, Direction.NORTH_WEST),
            new TwoWay(Direction.SOUTH_EAST, Direction.SOUTH_WEST)
    ) {
        @Override
        public Image doTransformation(Image source) {
            return source.getFlippedCopy(true, false);
        }
    },
    FLIP_VERTICAL(
            new TwoWay(Direction.NORTH, Direction.SOUTH),
            new TwoWay(Direction.NORTH_EAST, Direction.SOUTH_EAST),
            new TwoWay(Direction.NORTH_WEST, Direction.SOUTH_WEST)
    ) {
        @Override
        public Image doTransformation(Image source) {
            return source.getFlippedCopy(false, true);
        }
    },
    ROTATE_90_CW(
            new OneWay(Direction.NORTH, Direction.EAST),
            new OneWay(Direction.EAST, Direction.SOUTH),
            new OneWay(Direction.SOUTH, Direction.WEST),
            new OneWay(Direction.WEST, Direction.NORTH)
    ) {
        @Override
        public Image doTransformation(Image source) {
            final Image copy = source.copy();
            copy.rotate(90);
            return copy;
        }
    },
    ROTATE_90_CCW(
            new OneWay(Direction.NORTH, Direction.WEST),
            new OneWay(Direction.WEST, Direction.SOUTH),
            new OneWay(Direction.SOUTH, Direction.EAST),
            new OneWay(Direction.EAST, Direction.NORTH)
    ) {
        @Override
        public Image doTransformation(Image source) {
            final Image copy = source.copy();
            copy.rotate(-90);
            return copy;
        }
    },
    ROTATE_45_CW(
            new OneWay(Direction.NORTH, Direction.NORTH_EAST),
            new OneWay(Direction.NORTH_EAST, Direction.EAST),
            new OneWay(Direction.EAST, Direction.SOUTH_EAST),
            new OneWay(Direction.SOUTH_EAST, Direction.SOUTH),
            new OneWay(Direction.SOUTH, Direction.SOUTH_WEST),
            new OneWay(Direction.SOUTH_WEST, Direction.WEST),
            new OneWay(Direction.WEST, Direction.NORTH_WEST),
            new OneWay(Direction.NORTH_WEST, Direction.NORTH)
    ) {
        @Override
        public Image doTransformation(Image source) {
            final Image copy = source.copy();
            copy.rotate(45);
            return copy;
        }
    },
    ROTATE_45_CCW(
            new OneWay(Direction.NORTH, Direction.NORTH_WEST),
            new OneWay(Direction.NORTH_WEST, Direction.WEST),
            new OneWay(Direction.WEST, Direction.SOUTH_WEST),
            new OneWay(Direction.SOUTH_WEST, Direction.SOUTH),
            new OneWay(Direction.SOUTH, Direction.SOUTH_EAST),
            new OneWay(Direction.SOUTH_EAST, Direction.EAST),
            new OneWay(Direction.EAST, Direction.NORTH_EAST),
            new OneWay(Direction.NORTH_EAST, Direction.NORTH)
    ) {
        @Override
        public Image doTransformation(Image source) {
            final Image copy = source.copy();
            copy.rotate(-45);
            return copy;
        }
    };

    private final List<OneWay> ways = new ArrayList<OneWay>();

    Transformation(Ways... ways) {
        for (Ways way : ways) {
            this.ways.addAll(way.getWays());
        }
    }

    public List<Way> getWaysTo(Direction target) {
        final List<Way> result = new ArrayList<Way>();
        for (OneWay way : ways) {
            if (way.getTo() == target) {
                result.add(way);
            }
        }
        return result;
    }

    @Override
    public List<OneWay> getWays() {
        return ways;
    }

    public void apply(Animation source, Animation target) {
        // Only work on the unique frames -- target could have 'globally added ones'
        for (int i = target.getFrameCount(); i < source.getFrameCount(); i++) {
            target.addFrame(doTransformation(source.getImage(i)), source.getDuration(i));
        }
    }

    public abstract Image doTransformation(Image source);
}

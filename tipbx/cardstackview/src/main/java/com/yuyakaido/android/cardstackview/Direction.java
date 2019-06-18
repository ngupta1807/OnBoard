package com.yuyakaido.android.cardstackview;

import java.util.Arrays;
import java.util.List;

public enum Direction {
    Left,
    Right,
    Top,
    Bottom;

    public static final List<Direction> HORIZONTAL = Arrays.asList(Direction.Left, Direction.Right);
    //public static final List<Direction> VERTICALTOP = Arrays.asList(Direction.Top);
    public static final List<Direction> VERTICAL = Arrays.asList(Direction.Bottom,Direction.Top);
    //public static final List<Direction> FREEDOM = Arrays.asList(Direction.values());
    public static final List<Direction> FREEDOM = Arrays.asList(Direction.Top);
}

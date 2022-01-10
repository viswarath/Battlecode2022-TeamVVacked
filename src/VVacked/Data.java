package VVacked;

import battlecode.common.*;
import java.util.Random;

public class Data {
    public static int turnCount = 1;
    static final Random rng = new Random(6147);
    public static final Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST,
    };

    public static Direction spawnDir = Direction.CENTER;
    
}

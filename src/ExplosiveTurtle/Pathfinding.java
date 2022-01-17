package ExplosiveTurtle;


import battlecode.common.*;
import java.util.Random;

public class Pathfinding {
    public static Direction basicMove(RobotController rc, MapLocation target) throws GameActionException {
        Direction dir = rc.getLocation().directionTo(target);
        // System.out.println(dir);
        if (dir == null) {
            return Direction.CENTER;
        } else if (rc.canMove(dir)) {
            return dir;
        } else {
            Direction attemptDir = Direction.CENTER;
            for (int i = 1; i < 8; i++) {
                switch (i) {
                    case 1:
                        attemptDir = dir.rotateRight();
                        break;
                    case 2:
                        attemptDir = dir.rotateLeft();
                        break;
                    case 3:
                        attemptDir = dir.rotateRight().rotateRight();
                        break;
                    case 4:
                        attemptDir = dir.rotateLeft().rotateLeft();
                        break;
                    case 5:
                        attemptDir = dir.opposite().rotateRight();
                        break;
                    case 6:
                        attemptDir = dir.opposite().rotateLeft();
                        break;
                    case 7:
                        attemptDir = dir.opposite();
                        break;
                    default:
                        break;
                }
                if (rc.canMove(attemptDir)) {
                    return attemptDir;
                }
            }
        }
        return Direction.CENTER;
    }

    public static Direction basicBuild(RobotController rc, MapLocation target, RobotType type) throws GameActionException {
        Direction dir = rc.getLocation().directionTo(target);
        // System.out.println(dir);
        if (dir == null) {
            return Direction.CENTER;
        } else if (rc.canBuildRobot(type, dir) && !rc.canSenseRobotAtLocation(rc.getLocation().add(dir))) {
            return dir;
        } else {
            Direction attemptDir = Direction.CENTER;
            for (int i = 1; i < 8; i++) {
                switch (i) {
                    case 1:
                        attemptDir = dir.rotateRight();
                        break;
                    case 2:
                        attemptDir = dir.rotateLeft();
                        break;
                    case 3:
                        attemptDir = dir.rotateRight().rotateRight();
                        break;
                    case 4:
                        attemptDir = dir.rotateLeft().rotateLeft();
                        break;
                    case 5:
                        attemptDir = dir.opposite().rotateRight();
                        break;
                    case 6:
                        attemptDir = dir.opposite().rotateLeft();
                        break;
                    case 7:
                        attemptDir = dir.opposite();
                        break;
                    default:
                        break;
                }
                if (rc.canBuildRobot(type, attemptDir) && !rc.canSenseRobotAtLocation(rc.getLocation().add(attemptDir))) {
                    System.out.println(attemptDir);
                    return attemptDir;
                }
            }
        }
        return Direction.CENTER;
    }

    public static Direction getSemiRandomDir(RobotController rc, int baseDir) throws GameActionException {
        int num = Data.rng.nextInt(3);
        switch (baseDir) {
            case 0:
                if (num==1)
                    return Direction.NORTHWEST;
                else if (num==2)
                    return Direction.NORTH;
                else 
                    return Direction.NORTHEAST;
            case 1:
                if (num==1)
                    return Direction.NORTH;
                else if (num==2)
                    return Direction.NORTHEAST;
                else 
                    return Direction.EAST;
            case 2:
                if (num==1)
                    return Direction.NORTHEAST;
                else if (num==2)
                    return Direction.EAST;
                else 
                    return Direction.SOUTHEAST;
            case 3:
                if (num==1)
                    return Direction.EAST;
                else if (num==2)
                    return Direction.SOUTHEAST;
                else 
                    return Direction.SOUTH;
            case 4:               
                if (num==1)
                    return Direction.SOUTHEAST;
                else if (num==2)
                    return Direction.SOUTH;
                else 
                    return Direction.SOUTHWEST;
            case 5:
                if (num==1)
                    return Direction.SOUTH;
                else if (num==2)
                    return Direction.SOUTHWEST;
                else 
                    return Direction.WEST;
            case 6:
                if (num==1)
                    return Direction.SOUTHWEST;
                else if (num==2)
                    return Direction.WEST;
                else 
                    return Direction.NORTHWEST;
            case 7:
                if (num==1)
                    return Direction.WEST;
                else if (num==2)
                    return Direction.NORTHWEST;
                else 
                    return Direction.NORTH;
        }
        return Direction.CENTER;
    }

    public static Direction[] getFacingArray(RobotController rc, int baseDir) throws GameActionException {
        switch (baseDir) {
            case 0:
                return new Direction[] {Direction.NORTHWEST, Direction.NORTH, Direction.NORTHEAST};
            case 1:
                return new Direction[] {Direction.NORTH, Direction.NORTHEAST, Direction.EAST};
            case 2:
                return new Direction[] {Direction.NORTHEAST, Direction.EAST, Direction.SOUTHEAST};
            case 3:
                return new Direction[] {Direction.EAST, Direction.SOUTHEAST, Direction.SOUTH};
            case 4:               
                return new Direction[] {Direction.SOUTHEAST, Direction.SOUTH, Direction.SOUTHWEST};
            case 5:
                return new Direction[] {Direction.SOUTH, Direction.SOUTHWEST, Direction.WEST};
            case 6:
                return new Direction[] {Direction.SOUTHWEST, Direction.WEST, Direction.NORTHWEST};
            case 7:
                return new Direction[] {Direction.WEST, Direction.NORTHWEST, Direction.NORTH};
        }
        return new Direction[] {Direction.CENTER};
    }
}

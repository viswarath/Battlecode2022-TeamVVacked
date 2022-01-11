public class Pathfinding {
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
    }
}

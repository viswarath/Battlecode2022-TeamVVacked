package VVacked;

import battlecode.common.*;

public class Miner {
    public static void run(RobotController rc) throws GameActionException{
        //nearby bots
        RobotInfo[] robots = rc.senseNearbyRobots();
        //current MapLocation
        MapLocation loc = rc.getLocation();
        //direction of movement
        Direction move = Direction.CENTER;

        for (RobotInfo robot : robots){
            if (robot.getTeam() == rc.getTeam() && robot.getType() == RobotType.ARCHON){
                MapLocation base = robot.getLocation();
                move = base.directionTo(loc);
            }
        }

        if (rc.canMove(move)){
            rc.move(move);
        }

    }
}

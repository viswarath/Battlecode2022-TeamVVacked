package VVacked;

import battlecode.common.*;

public class Miner {
    public static void run(RobotController rc) throws GameActionException{
        //nearby bots
        RobotInfo[] robots = rc.senseNearbyRobots();
        //current MapLocation
        MapLocation loc = rc.getLocation();

        while (rc.canMove(Data.moveDir) == false){
            Data.moveDir = Data.moveDir.rotateRight();
        }
        rc.move(Data.moveDir);

    }

    public static void init(RobotController rc) throws GameActionException{
        RobotInfo[] robots = rc.senseNearbyRobots();
            for (RobotInfo robot : robots){
                if (robot.getTeam() == rc.getTeam() && robot.getType() == RobotType.ARCHON){
                    MapLocation base = robot.getLocation();
                    Data.moveDir = base.directionTo(rc.getLocation());
                }
        }
    }
}

package ExplosiveTurtle;


import battlecode.common.*;

public class Builder {
    public static MapLocation baseLoc;
    static void run(RobotController rc) throws GameActionException{
        if (rc.canRepair(baseLoc)){
            rc.repair(baseLoc);
        }

    }

    static void init(RobotController rc){
        for (RobotInfo robot: rc.senseNearbyRobots(2,rc.getTeam())){
            if(robot.getType() == RobotType.ARCHON){
                baseLoc = robot.getLocation();
            }
        }
    }
}

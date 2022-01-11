package VVacked;

import battlecode.common.*;

public class Miner {
    //direction of movement
    public Direction move = Direction.CENTER;
    public boolean foundLocation = false;
    public MapLocation targetLocation;

    public static void run(RobotController rc) throws GameActionException{
        if (!foundLocation){
            MapLocation[] leadLocations = rc.senseNearbyLocationsWithLead(2);
            for (MapLocation loc : leadLoactions){
                if (rc.senseLead(loc) > 1){
                    RobotInfo[] robots = rc.senseNearbyRobots();
                }
            }
        }

        if (rc.canMove(move) && !foundLocation){
            rc.move(move);
        }
    }

    public static void init(RobotController rc) throwsa GameActionException{
        //nearby bots
        RobotInfo[] robots = rc.senseNearbyRobots();
        //current MapLocation
        MapLocation loc = rc.getLocation();

        for (RobotInfo robot : robots){
            if (robot.getTeam() == rc.getTeam() && robot.getType() == RobotType.ARCHON){
                MapLocation base = robot.getLocation();
                move = base.directionTo(loc);
            }
        }
    }
}

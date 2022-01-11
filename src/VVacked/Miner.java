package VVacked;

import battlecode.common.*;

public class Miner {
    //direction of movement
    public static Direction move = Direction.CENTER;
    public static boolean foundLocation = false;
    public MapLocation targetLocation;

    public static void run(RobotController rc) throws GameActionException{
        if (!foundLocation){
            MapLocation[] leadLocations = rc.senseNearbyLocationsWithLead();
            for (MapLocation loc : leadLocations){
                if ((rc.senseLead(loc) > 1) && (rc.senseRobotAtLocation(loc) == null)){
                    RobotInfo[] robots = rc.senseNearbyRobots();
                }
            }
        }

        if (rc.canMove(move) && !foundLocation){
            rc.move(move);
        }
    }

    public static void init(RobotController rc) throws GameActionException{
        //nearby bots
        RobotInfo[] robots = rc.senseNearbyRobots();
        //current MapLocation
        MapLocation loc = rc.getLocation();

        for(RobotInfo robot: robots){
            if (robot.getType() = RobotType.ARCHON && robot.getTeam() == rc.getTeam()){

            }
        }
    }
}

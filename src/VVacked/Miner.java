package VVacked;

import battlecode.common.*;

public class Miner {
    //direction of movement
    public static Direction move = Direction.CENTER;
    public static boolean foundLeadLocation = false;
    public MapLocation targetLocation;

    public static void run(RobotController rc) throws GameActionException{
        if (!foundLeadLocation){
            MapLocation[] leadLocations = rc.senseNearbyLocationsWithLead();
            for (MapLocation loc : leadLocations){
                if ((rc.senseLead(loc) > 1) && (rc.senseRobotAtLocation(loc) == null)){
                    rc.move(rc.getLocation().directionTo(loc));
                    if (rc.canMineLead(loc)){
                        rc.mineLead(loc);
                    }
                    if (rc.getLocation() == loc){
                        foundLeadLocation = true;
                    }
                }
            }
        }

        if (rc.canMove(Data.moveDir) && !foundLeadLocation){
            rc.move(Data.moveDir);
        }
    }

    public static void init(RobotController rc) throws GameActionException{
        //nearby bots
        RobotInfo[] robots = rc.senseNearbyRobots();
        //current MapLocation
        MapLocation loc = rc.getLocation();

        for(RobotInfo robot: robots){
            if (robot.getType() == RobotType.ARCHON && robot.getTeam() == rc.getTeam()){
                MapLocation base = robot.getLocation();
                Data.moveDir = base.directionTo(loc);
            }
        }
    }
}

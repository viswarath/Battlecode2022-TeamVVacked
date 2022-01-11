package VVacked;

import battlecode.common.*;

public class Miner {
    //direction of movement
    public static Direction move = Direction.CENTER;
    public static boolean foundLeadLocation = false;
    public static MapLocation targetLocation;

    public static void run(RobotController rc) throws GameActionException{
        MapLocation[] leadLocations = rc.senseNearbyLocationsWithLead(3);
        if (!foundLeadLocation){
            forLoop:
            for (MapLocation loc : leadLocations){
                if ((rc.senseLead(loc) > 1) && (rc.senseRobotAtLocation(loc) == null)){
                    targetLocation = loc;
                    break forLoop;
                }
            }
        }

        Direction dir = Pathfinding.basicMove(rc, targetLocation);
        if (rc.canMineLead(targetLocation)){
            foundLeadLocation = true;
            if (rc.senseLead(targetLocation) < 2){
                foundLeadLocation = false;
            } else {
                while (rc.canMineLead(targetLocation)){
                    rc.mineLead(targetLocation);
                }
            }
        } else if (rc.canMove(dir)){
            rc.move(dir);   
        } 
        else if (rc.canMove(move)){
            rc.move(move);
        }
    }

    public static void init(RobotController rc) throws GameActionException{
        //nearby bots
        RobotInfo[] robots = rc.senseNearbyRobots();
        //current MapLocation
        MapLocation loc = rc.getLocation();
        //set default target
        targetLocation = loc;

        for(RobotInfo robot: robots){
            if (robot.getType() == RobotType.ARCHON && robot.getTeam() == rc.getTeam()){
                MapLocation base = robot.getLocation();
                move = base.directionTo(loc);
            }
        }
    }
}

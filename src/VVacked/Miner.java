package VVacked;

import battlecode.common.*;

public class Miner {
    //direction of movement
    public static Direction move = Direction.CENTER;
    public static boolean foundLeadLocation = false;
    public static MapLocation targetLocation;

    public static void run(RobotController rc) throws GameActionException{
        MapLocation[] leadLocations = rc.senseNearbyLocationsWithLead();

        if (!foundLeadLocation){
            forLoop:
            for (MapLocation loc : leadLocations){
                if ((rc.senseLead(loc) > 1) && (rc.senseRobotAtLocation(loc) == null)){
                    targetLocation = loc;
                    foundLeadLocation = true;
                    break forLoop;              
                }
            }
        }

        Direction dir = Pathfinding.basicMove(rc, targetLocation);
        if (rc.canMineLead(targetLocation) && rc.senseLead(targetLocation) > 1){
            while (rc.senseLead(targetLocation) > 1 && rc.canMineLead(targetLocation)){
                foundLeadLocation = false;
                rc.mineLead(targetLocation);
            }
        } else if (rc.canMove(dir) && foundLeadLocation){
            rc.move(dir);   
        } 
        else if (rc.canMove(move)){
            forLoop:
            for (int i = 0; i < Data.directions.length; i++){
                if (Data.directions[i] == move){
                    rc.move(Pathfinding.getSemiRandomDir(rc, i+1));
                    break forLoop;
                }
            }
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

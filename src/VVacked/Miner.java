package VVacked;

import battlecode.common.*;

public class Miner {
    //direction of movement
    public static Direction move = Direction.CENTER;
    public static boolean foundLeadLocation = false;
    public static MapLocation targetLocation;
    //public static boolean closeMiner = false;
    public static boolean reverseMiner = false;
    public static int directionIndex = 0;

    public static void run(RobotController rc) throws GameActionException{
        MapLocation[] leadLocations = rc.senseNearbyLocationsWithLead();
        // if (closeMiner){
        //     leadLocations = rc.senseNearbyLocationsWithLead(2);
        // } else{
        //     leadLocations = rc.senseNearbyLocationsWithLead();
        // }

        if (reverseMiner){
            for(int i = 0; i < leadLocations.length / 2; i++)
            {
                MapLocation temp = leadLocations[i];
                leadLocations[i] = leadLocations[leadLocations.length - i - 1];
                leadLocations[leadLocations.length - i - 1] = temp;
            }
        }
        
        if (!rc.onTheMap(rc.getLocation().add(move))) {
            move = move.opposite().rotateRight();
            forLoop:
            for (int i = 0; i < Data.directions.length; i++){
                if (Data.directions[i] == move){
                    directionIndex = i;
                    break forLoop;
                }
            }
        }

        if (!foundLeadLocation){
            Direction[] facingArray = Pathfinding.getFacingArray(rc, directionIndex);
            forLoop:
            for (MapLocation loc : leadLocations){
                if (rc.canSenseLocation(loc)){
                    if ((rc.senseLead(loc) > 1) && (rc.senseRobotAtLocation(loc) == null)){
                        for (Direction dir : facingArray){
                            if (rc.getLocation().directionTo(loc) == dir){
                                targetLocation = loc;
                                System.out.println("(" + targetLocation.x + "' " + targetLocation.y + "), " + rc.getID());
                                foundLeadLocation = true;
                                break forLoop; 
                            }
                        }             
                    }
                }
            }
        }

        Direction dir = Pathfinding.basicMove(rc, targetLocation);
        if (rc.canSenseLocation(targetLocation)){
            while (rc.senseLead(targetLocation) > 1 && rc.canMineLead(targetLocation)){
                MapLocation me = rc.getLocation();
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        MapLocation mineLocation = new MapLocation(me.x + dx, me.y + dy);
                        // Notice that the Miner's action cooldown is very low.
                        // You can mine multiple times per turn!
                        while (rc.canMineGold(mineLocation)) {
                            rc.mineGold(mineLocation);
                        }
                        while (rc.canMineLead(mineLocation) && rc.senseLead(mineLocation) > 1) {
                            rc.mineLead(mineLocation);
                        }
                    }
                }
                if (rc.senseLead(targetLocation) < 2){
                    foundLeadLocation = false;
                }
            }
            if (rc.canMove(dir) && foundLeadLocation && !rc.getLocation().isAdjacentTo(targetLocation)){
                rc.move(dir);   
            }
        } else{
            foundLeadLocation = false;
        }

        if (!foundLeadLocation){
            rc.move(Pathfinding.getSemiRandomDir(rc, directionIndex));
        }
    }

    public static void init(RobotController rc) throws GameActionException{
        //nearby bots
        RobotInfo[] robots = rc.senseNearbyRobots();
        //current MapLocation
        MapLocation loc = rc.getLocation();
        //set default target
        targetLocation = loc;

        MapLocation baseLoc = rc.getLocation();

        for(RobotInfo robot: robots){
            if (robot.getType() == RobotType.ARCHON && robot.getTeam() == rc.getTeam()){
                MapLocation base = robot.getLocation();
                baseLoc = base;
                move = base.directionTo(loc);
            }
        }

        int rand = Data.rng.nextInt(3);
        // if (rand == 0){
        //     closeMiner = true;
        // }
        rand = Data.rng.nextInt(3);
        if (baseLoc.x < rc.getMapWidth()/2.0){
            if (rand == 0){
                reverseMiner = true;
            }
        } else{
            if (rand != 0){
                reverseMiner = true;
            }
        }

        forLoop:
        for (int i = 0; i < Data.directions.length; i++){
            if (Data.directions[i] == move){
                directionIndex = i;
                break forLoop;
            }
        }
    }
}

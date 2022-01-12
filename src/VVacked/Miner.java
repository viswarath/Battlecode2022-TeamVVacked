package VVacked;

import battlecode.common.*;

public class Miner {
    //direction of movement
    public static Direction move = Direction.CENTER;
    public static boolean foundLeadLocation = false;
    public static MapLocation targetLocation;

    //miner that reads from the lead loacation array in reverse order
    public static boolean reverseMiner = false;

    //index of move variable in Data.directions[]
    public static int directionIndex = 0;

    public static void run(RobotController rc) throws GameActionException{
        MapLocation[] leadLocations = rc.senseNearbyLocationsWithLead();

        //reverses lead locations array if miner is a reverse miner
        if (reverseMiner){
            for(int i = 0; i < leadLocations.length / 2; i++)
            {
                MapLocation temp = leadLocations[i];
                leadLocations[i] = leadLocations[leadLocations.length - i - 1];
                leadLocations[leadLocations.length - i - 1] = temp;
            }
        }
        
        //turns around and to the right if add the edge of the map
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

        //finds a lead location to target for mining if one is not already found
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

        //pathfinds a direction to the target location
        Direction dir = Pathfinding.basicMove(rc, targetLocation);

        //if enemy robot nearby then run away and reset lead location target
        RobotInfo[] robots = rc.senseNearbyRobots();
        for(RobotInfo robot: robots){
            if (robot.getType() == RobotType.SOLDIER && robot.getTeam() != rc.getTeam()){

            }
        }

        //mines lead or moves towards target
        if (rc.canSenseLocation(targetLocation)){
            //mines until it cannot mine anymore spaces around it
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
            //if not adjacent to target move towards the target
            if (rc.canMove(dir) && foundLeadLocation && !rc.getLocation().isAdjacentTo(targetLocation)){
                rc.move(dir);   
            }
        } else{
            foundLeadLocation = false;
        }

        //move in default location if no lead locations found
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

        //find base and set move to away from base
        for(RobotInfo robot: robots){
            if (robot.getType() == RobotType.ARCHON && robot.getTeam() == rc.getTeam()){
                MapLocation base = robot.getLocation();
                baseLoc = base;
                move = base.directionTo(loc);
            }
        }

        //choose whether to reverse targeting order for lead (favoring away from the corners for more mining)
        int rand = Data.rng.nextInt(3);
        if (baseLoc.x < rc.getMapWidth()/2.0){
            if (rand == 0){
                reverseMiner = true;
            }
        } else{
            if (rand != 0){
                reverseMiner = true;
            }
        }

        //set direction index
        forLoop:
        for (int i = 0; i < Data.directions.length; i++){
            if (Data.directions[i] == move){
                directionIndex = i;
                break forLoop;
            }
        }
    }
}

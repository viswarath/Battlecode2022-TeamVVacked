package VVacked;

import battlecode.common.*;

public class Soldier {

    public static MapLocation currentTarget;
    public static boolean archonsFound = false;

    public static void run(RobotController rc) throws GameActionException{
        setTargetArchon(rc);

        //archons found are put in these spots, dead archons have value of 9999
        if (rc.readSharedArray(12) != 0 && rc.readSharedArray(13) != 0 && rc.readSharedArray(14) != 0 && rc.readSharedArray(15) != 0){
            archonsFound = true;
        }

        if (!archonsFound){
            RobotInfo[] robots = rc.senseNearbyRobots();
            forLoop:
            for (RobotInfo robot : robots){
                if (robot.type == RobotType.ARCHON && robot.team != rc.getTeam()){
                    for (int i = 12; i < 16; i++){
                        if (rc.readSharedArray(i) == 0){
                            rc.writeSharedArray(i, robot.getLocation().x*100 + robot.getLocation().y);
                            break forLoop;
                        }
                    }
                }
            }
        } else{
            if (rc.canSenseLocation(currentTarget)){
                if (rc.canSenseRobotAtLocation(currentTarget)){
                    if (rc.senseRobotAtLocation(currentTarget).type == RobotType.ARCHON && rc.senseRobotAtLocation(currentTarget).team != rc.getTeam()){
                        
                    }
                }
            }
        }

        Direction dir = Pathfinding.basicMove(rc, currentTarget);
        if (dir != Direction.CENTER){
            rc.move(dir);
        }
    }

    public static void setTargetArchon(RobotController rc) throws GameActionException{
        MapLocation closest = null;
        for (int i = 0; i < 12; i++){
            if (rc.readSharedArray(i) != 0){
                MapLocation loc = Data.readMapLocationFromSharedArray(rc, rc.readSharedArray(i));
                if (closest == null){
                    closest = loc;
                } else if (rc.getLocation().distanceSquaredTo(loc) < rc.getLocation().distanceSquaredTo(closest)){
                    closest = loc;
                }
            }
        }
        currentTarget = closest;
    }
    
    public static void init(RobotController rc) throws GameActionException{
    }   
}

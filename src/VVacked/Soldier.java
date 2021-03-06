package VVacked;

import battlecode.common.*;

public class Soldier {

    public static MapLocation currentTarget;
    public static int currentArchonIndex;
    public static boolean archonsFound = false;

    public static boolean attackingArchon = false;
    public static MapLocation attackTarget;

    public static void run(RobotController rc) throws GameActionException{
        setTargetArchon(rc);

        //archons found are put in these spots, dead archons have value of 9999
        if (rc.readSharedArray(12) != 0 && rc.readSharedArray(13) != 0 && rc.readSharedArray(14) != 0 && rc.readSharedArray(15) != 0){
            archonsFound = true;
        }

        if (!attackingArchon){
            forLoop:
            for (int i = 12; i < 16; i++){
                if (rc.readSharedArray(i) != 0){
                    attackTarget = Data.readMapLocationFromSharedArray(rc, rc.readSharedArray(i));
                    attackingArchon = true;
                    break forLoop;
                }
            }
        }

        if (!archonsFound){
            RobotInfo[] robots = rc.senseNearbyRobots();
            for (RobotInfo robot : robots){
                if (robot.type == RobotType.ARCHON && robot.team != rc.getTeam()){
                    addArchonToSharedArray(rc, robot.getLocation());
                }
            }
        } 

        if (rc.canSenseLocation(currentTarget) && !attackingArchon){
            if (rc.canSenseRobotAtLocation(currentTarget)){
                if (rc.senseRobotAtLocation(currentTarget).type == RobotType.ARCHON && rc.senseRobotAtLocation(currentTarget).team != rc.getTeam()){
                    if (!archonsFound){
                        addArchonToSharedArray(rc, currentTarget);
                    }
                    if (rc.canAttack(currentTarget)){
                        rc.attack(currentTarget);
                    }
                } else{
                    if (rc.readSharedArray(currentArchonIndex) != 0){
                        rc.writeSharedArray(currentArchonIndex, 0);
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

        Direction dir = Direction.CENTER;
        if (!attackingArchon){
            dir = Pathfinding.basicMove(rc, currentTarget);
        } else{
            dir = Pathfinding.basicMove(rc, attackTarget);
        }

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
                    currentArchonIndex = i;
                    closest = loc;
                } else if (rc.getLocation().distanceSquaredTo(loc) < rc.getLocation().distanceSquaredTo(closest)){
                    currentArchonIndex = i;
                    closest = loc;
                }
            }
        }
        currentTarget = closest;
    }

    public static void addArchonToSharedArray(RobotController rc, MapLocation loc) throws GameActionException{
        int intLocation = loc.x*100 + loc.y;
        forLoop:
        for (int i = 12; i < 16; i++){
            if (rc.readSharedArray(i) == 0){
                rc.writeSharedArray(i, intLocation);
                break forLoop;
            }
        }
    }    
}

package VVacked;

import battlecode.common.*;

public class Archon {

    public static int directionIndex = 0;
    public static int firstMinerPhaseEnd = 16;
    public static int quadrant = 0;
    public static boolean closerToCenter = false;
    public static int minersSpawned = 0;
    
    public static void run(RobotController rc) throws GameActionException{
        Direction build = getSpawnDir(rc, RobotType.MINER);
        if (build != Direction.CENTER){
            rc.setIndicatorString("Trying to build a miner");
            if (minersSpawned < 16){
                rc.buildRobot(RobotType.MINER, build);
                minersSpawned += 1;
            }
            directionIndex+=1;
            if (directionIndex == Data.directions.length){
                directionIndex = 0;
            }
        }
    }


    public static Direction getSpawnDir(RobotController rc, RobotType type) throws GameActionException{
        for (int i = directionIndex; i < Data.directions.length; i++){
            if (rc.canBuildRobot(type, Data.directions[i]) == true){
                return Data.directions[i];
            }
        }
        for (int i = 0; i < directionIndex; i++){
            if (rc.canBuildRobot(type, Data.directions[i]) == true){
                return Data.directions[i];
            }
        }
        return Direction.CENTER;
    }

    public static void init(RobotController rc) throws GameActionException{
        int height = rc.getMapHeight();
        int width = rc.getMapWidth();
        MapLocation currentLocation = rc.getLocation();
        MapLocation mapCorner = new MapLocation(0,0);
        MapLocation center = new MapLocation(width/2, height/2);

        if (currentLocation.x > width/2.0 && currentLocation.y > height){
            quadrant = 1;
            mapCorner = new MapLocation(width, height);
        } else if (currentLocation.x < width/2.0 && currentLocation.y > height){
            quadrant = 2;
            mapCorner = new MapLocation(0, height);
        } else if (currentLocation.x < width/2.0 && currentLocation.y < height){
            quadrant = 3;
        } else{
            quadrant = 4;
            mapCorner = new MapLocation(width, 0);
        }

        if (currentLocation.distanceSquaredTo(mapCorner) > currentLocation.distanceSquaredTo(center) + 3){
            closerToCenter = true;
        }
    }
}



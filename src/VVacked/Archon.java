package VVacked;

import battlecode.common.*;

public class Archon {

    public static int directionIndex = 0;
    public static Direction build;
    //for scouts
    public static MapLocation enemyBaseXReflection;
    public static MapLocation enemyBaseYReflection;
    public static MapLocation enemyBaseBothReflection;
    public static Boolean foundEnemyBase;
    //the first archon must push the zeros in the shared array to the right
    public static Boolean firstArchon = false;

    public static void run(RobotController rc) throws GameActionException{
        if (rc.getID() == 2 || rc.getID() == 3){
            firstArchon = true;
        }

        // part 2 of 3 round plan
        if (rc.getRoundNum() == 2){
            //check if the estimated locations are nearby
            for (int i = 0; i < rc.getArchonCount()*3;i++){
                
            }
        }
        if (rc.getRoundNum() < 10){
            //scouting phase
            build = getSpawnDir(rc, RobotType.SOLDIER);
            rc.setIndicatorString("Trying to build a soldier");
            rc.buildRobot(RobotType.SOLDIER, build);
        }
        else{
            build = getSpawnDir(rc, RobotType.MINER);
            if (build != Direction.CENTER){
                rc.setIndicatorString("Trying to build a miner");
                rc.buildRobot(RobotType.MINER, build);
            }
        }
        directionIndex+=1;
        if (directionIndex == Data.directions.length){
            directionIndex = 0;
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
        Data.baseLoc = rc.getLocation();
        Data.totalNumArchon = rc.getArchonCount();
        //scouting phase!
        rc.writeSharedArray(0, 0);

        int mapWidth = rc.getMapWidth()-1;
        int mapHeight = rc.getMapHeight()-1;
        //list of possible enemy mapLocations reflections
        enemyBaseXReflection = new MapLocation(mapWidth-Data.baseLoc.x,Data.baseLoc.y);
        enemyBaseYReflection = new MapLocation(Data.baseLoc.x,mapHeight-Data.baseLoc.y);
        enemyBaseBothReflection = new MapLocation(mapWidth-Data.baseLoc.x,mapHeight-Data.baseLoc.y);

        //write locations to shared array from index 1 to (num of archons*3)
        for(int i = 1; i < Data.totalNumArchon*3; i++){
            if(rc.readSharedArray(i) == 0){
                rc.writeSharedArray(i, Communication.convertLocToInt(rc, enemyBaseXReflection));
                rc.writeSharedArray(i, Communication.convertLocToInt(rc, enemyBaseYReflection));
                rc.writeSharedArray(i, Communication.convertLocToInt(rc, enemyBaseBothReflection));
            }
        }
    }
}



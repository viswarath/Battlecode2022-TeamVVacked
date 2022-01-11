package VVacked;

import battlecode.common.*;

public class Archon {

    public static int directionIndex = 0;
    
    public static void run(RobotController rc) throws GameActionException{
        Direction build = getSpawnDir(rc, RobotType.MINER);
        if (build != Direction.CENTER){
            rc.setIndicatorString("Trying to build a miner");
            rc.buildRobot(RobotType.MINER, build);
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
}



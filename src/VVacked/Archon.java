package VVacked;

import battlecode.common.*;

public class Archon {
    public static void run(RobotController rc) throws GameActionException{
        Direction build = getSpawnDir(rc, RobotType.MINER);
        int roundNum = rc.getRoundNum();
        if (roundNum <= 9 && build != Direction.CENTER){
            rc.setIndicatorString("Trying to build a miner");
            rc.buildRobot(RobotType.MINER, build);
            
        }
    }


    public static Direction getSpawnDir(RobotController rc, RobotType type) throws GameActionException{
        for (int i = 0; i < Direction.values().length; i++){
            if (rc.canBuildRobot(type, Direction.values()[i]) == true){
                return Direction.values()[i];
            }
        }
    return Direction.CENTER;
    }
}



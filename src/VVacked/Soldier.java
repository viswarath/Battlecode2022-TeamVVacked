package VVacked;

import battlecode.common.*;

public class Soldier {
    public static boolean scoutPhase = true;

    public static void run(RobotController rc) throws GameActionException{
        int signal = -1;
        if (scoutPhase){
            signal = rc.readSharedArray(0);
            if(signal/10 == 0){
                //scouting phase!
                //get next scouting coordinate from the next four digits after signal
                //Direction move = Pathfinding.basicMove();
                //if(rc.canMove(move)){
                    rc.setIndicatorString("moving to possible enemy base");
                    //System.out.print("possible base at "+ enemyBaseXReflection.x + "," + enemyBaseXReflection.y);
                    //rc.move(move);
                //}
            }
        }
    }
    public static void init(RobotController rc){
        for (RobotInfo robot : rc.senseNearbyRobots(3, rc.getTeam())) {
            if (robot.getType() == RobotType.ARCHON) {
                Data.baseLoc = robot.getLocation();
            }
        }
    }
}

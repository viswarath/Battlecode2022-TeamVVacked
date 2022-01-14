package ExplosiveTurtle;
import battlecode.common.*;

public class Soldier {
    public static MapLocation baseLocation;   
    public static int innerRad = 2;
    public static int outerRad = 16;
    public static void run(RobotController rc) throws GameActionException{
        //set the perpendicular direction to the archon
        Direction perpenDir = rc.getLocation().directionTo(baseLocation).rotateRight().rotateRight();
        //awayDir is away from base toDir is to base
        Direction awayDir = baseLocation.directionTo(rc.getLocation());
        Direction toDir = awayDir.opposite();
        int distanceToBase = rc.getLocation().distanceSquaredTo(baseLocation);

        if(distanceToBase < innerRad){
            rc.move(Pathfinding.basicMove(rc, rc.getLocation().add(awayDir)));
        } else if(distanceToBase >= innerRad && distanceToBase <= outerRad){
            rc.move(Pathfinding.basicMove(rc, rc.getLocation().add(perpenDir)));
        } else if(distanceToBase > outerRad){
            rc.move(Pathfinding.basicMove(rc, rc.getLocation().add(toDir)));
        }
    }

    public static void init(RobotController rc){
        for (RobotInfo robot: rc.senseNearbyRobots()){
            if(robot.getType() == RobotType.ARCHON && robot.getTeam() == rc.getTeam()){
                baseLocation = robot.getLocation();
            }
        }
    }
}

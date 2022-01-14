package ExplosiveTurtle;
import battlecode.common.*;

public class Soldier {
    public static MapLocation baseLocation;   
    public static int innerRad = 16;
    public static int outerRad = 24;
    public static int healingRad = 20;
    public static RobotInfo attackBot;
    public static void run(RobotController rc) throws GameActionException{
        //set the perpendicular direction to the archon
        Direction perpenDir = rc.getLocation().directionTo(baseLocation).rotateRight().rotateRight();
        //awayDir is away from base toDir is to base
        Direction awayDir = baseLocation.directionTo(rc.getLocation());
        Direction toDir = awayDir.opposite();
        int distanceToBase = rc.getLocation().distanceSquaredTo(baseLocation);
        MapLocation moveTo = null;

        //setting the move location based on distance away from the movement ring
        if(distanceToBase < innerRad){
            moveTo = rc.getLocation().add(awayDir);
        } else if(distanceToBase >= innerRad && distanceToBase <= outerRad){
            moveTo = rc.getLocation().add(perpenDir);
        } else if(distanceToBase > outerRad){
            moveTo = rc.getLocation().add(toDir);
        }

        //setting the move location because of low health takes precedence!
        if (rc.getHealth() < 25){
            if(distanceToBase > healingRad){
                moveTo = rc.getLocation().add(toDir);
            }else if(distanceToBase <= healingRad){
                moveTo = rc.getLocation().add(perpenDir);
            }       
        }

        Direction temp = perpenDir;
        while(rc.onTheMap(moveTo) == false){
            moveTo = rc.getLocation().add(temp.rotateLeft());
        }
        
        rc.move(Pathfinding.basicMove(rc, moveTo));

        int maxHealth = 60;

        for (RobotInfo robot: rc.senseNearbyRobots(13, rc.getTeam().opponent())){
            if(robot.getType() == RobotType.SOLDIER && rc.canAttack(robot.getLocation())){
                if(robot.getHealth() < maxHealth){
                    attackBot = robot;
                }
            }
        }
        if(rc.canAttack(attackBot.getLocation())){
            rc.attack(attackBot.getLocation());
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

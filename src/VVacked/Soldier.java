package VVacked;

import battlecode.common.*;

public class Soldier {

    public static MapLocation currentTarget; //current scouting target from 0-11 in shared array
    public static int currentArchonIndex0; //index of current target in shared array from 0-11
    public static int currentArchonIndex12; //index of attackTarget in shared array from 12-15
    public static boolean archonsFound = false; //have all archons been found

    public static boolean targetingArchon = false; //is soldier targeting an enemy archon
    public static boolean enemyArchonNearby = false; //is enemy archon close enough to attack
    public static MapLocation attackTarget; //location of the targeted enemy archon from 12-15

    public static void run(RobotController rc) throws GameActionException{
        setTargetArchon(rc);
        System.out.println(currentTarget);

        //archons found are put in these spots, dead archons have value of 9999
        if (rc.readSharedArray(12) != 0 && rc.readSharedArray(13) != 0 && rc.readSharedArray(14) != 0 && rc.readSharedArray(15) != 0){
            archonsFound = true;
        }

        //if targeted archon is dead then untarget
        if (rc.readSharedArray(currentArchonIndex12) == 9999){
            targetingArchon = false;
            enemyArchonNearby = false;
            System.out.println("SET TO FALSE");
        }

        //if all archons have not been found it searches for nearby enemy archons and adds to 12-15
        if (!archonsFound){
            RobotInfo[] robots = rc.senseNearbyRobots();
            for (RobotInfo robot : robots){
                if (robot.type == RobotType.ARCHON && robot.team != rc.getTeam()){
                    addArchonToSharedArray(rc, robot.getLocation());
                }
            }
        } 

        //checks if any enemy archons have been found and if so sets its target to that archon
        if (!targetingArchon){
            forLoop:
            for (int i = 12; i < 16; i++){
                if (rc.readSharedArray(i) != 0 && rc.readSharedArray(i) != 9999){
                    MapLocation loc = Data.readMapLocationFromSharedArray(rc, rc.readSharedArray(i));
                    attackTarget = loc;
                    currentArchonIndex12 = i;
                    forLoop2:
                    for (int j = 0; j < 12; j++){
                        if (loc == Data.readMapLocationFromSharedArray(rc, rc.readSharedArray(j))){
                            currentArchonIndex0 = j;
                            break forLoop2;
                        }
                    }
                    targetingArchon = true;
                    System.out.print("SET TO TRUE");
                    break forLoop;
                }
            }
        }

        //if not currently targeting an archon and can sense archon at target location from 0-11, add to shared 12-15
        if (rc.canSenseLocation(currentTarget) && !targetingArchon){
            if (rc.canSenseRobotAtLocation(currentTarget)){
                if (rc.senseRobotAtLocation(currentTarget).type == RobotType.ARCHON && rc.senseRobotAtLocation(currentTarget).team != rc.getTeam()){
                    if (!archonsFound){
                        addArchonToSharedArray(rc, currentTarget);
                    }
                    //attack archon if able
                    attackAndCheckArchon(rc);
                } else{ //no archon at point so change shared array index of target to 0
                    if (rc.readSharedArray(currentArchonIndex0) != 0){
                        rc.writeSharedArray(currentArchonIndex0, 0);
                    }
                }
            }
        } else if (targetingArchon){ //if currently targeting an archon, attack if possible
            attackAndCheckArchon(rc);
        }

        //if not able to attack an enemy archon but is targeting an enemy archon, attack nearby soldiers
        if (targetingArchon && !enemyArchonNearby){
            RobotInfo[] robots = rc.senseNearbyRobots();
            for (RobotInfo robot : robots){
                if (robot.type == RobotType.SOLDIER && robot.getTeam() != rc.getTeam()){
                    if (rc.canAttack(robot.getLocation())){
                        rc.attack(robot.getLocation());
                    }
                }
            }
        }

        //moves towards either found archon or guessed location of archon
        Direction dir = Direction.CENTER;
        if (!targetingArchon){
            System.out.println("SCOUTING");
            dir = Pathfinding.basicMove(rc, currentTarget);
        } else{
            System.out.println("ATTACKING");
            dir = Pathfinding.basicMove(rc, attackTarget);
        }

        if (dir != Direction.CENTER){
            rc.move(dir);
        }
    }

    //sets the guessed lcoation of the enemy archon to move towards
    public static void setTargetArchon(RobotController rc) throws GameActionException{
        MapLocation closest = null;
        for (int i = 0; i < 12; i++){
            if (rc.readSharedArray(i) != 0){
                MapLocation loc = Data.readMapLocationFromSharedArray(rc, rc.readSharedArray(i));
                if (closest == null){
                    currentArchonIndex0 = i;
                    closest = loc;
                } else if (rc.getLocation().distanceSquaredTo(loc) < rc.getLocation().distanceSquaredTo(closest)){
                    currentArchonIndex0 = i;
                    closest = loc;
                }
            }
        }
        currentTarget = closest;
    }

    //adds enemy archon to 12-15 in shared array
    public static void addArchonToSharedArray(RobotController rc, MapLocation loc) throws GameActionException{
        int intLocation = loc.x*100 + loc.y;
        forLoop:
        for (int i = 12; i < 16; i++){
            if (rc.readSharedArray(i) == 0 && rc.readSharedArray(i) != intLocation){
                rc.writeSharedArray(i, intLocation);
                break forLoop;
            } else if (rc.readSharedArray(i) == intLocation){
                break forLoop;
            }
        }
    }   
    
    //attack enemy archon and see if the archon died
    public static void attackAndCheckArchon(RobotController rc) throws GameActionException{
        boolean archonLocationsCorrect = false;
        RobotInfo[] robots = rc.senseNearbyRobots();
        for (RobotInfo robot : robots){
            if (robot.getType() == RobotType.ARCHON && robot.getTeam() != rc.getTeam()){
                MapLocation enemyLoc = robot.getLocation();
                if (rc.canAttack(robot.getLocation())){
                    rc.attack(enemyLoc);
                    enemyArchonNearby = true;
                }

                for (int i = 12; i < 16; i++){
                    if (Data.readMapLocationFromSharedArray(rc, rc.readSharedArray(i)) == enemyLoc){
                        archonLocationsCorrect = true;
                    }
                }

                if (!rc.canSenseRobotAtLocation(enemyLoc)){
                    System.out.println("\n\n\nENEMY ARCHON DIED\n\n\n");
                    System.out.println(rc.readSharedArray(12));
                    System.out.println(rc.readSharedArray(13));
                    System.out.println(rc.readSharedArray(14));
                    System.out.println(rc.readSharedArray(15));
                    //enemy archon died
                    if (rc.readSharedArray(currentArchonIndex0) != 0){
                        rc.writeSharedArray(currentArchonIndex0, 0);
                    }
                    if (rc.readSharedArray(currentArchonIndex12) != 9999){
                        rc.writeSharedArray(currentArchonIndex12, 9999);
                    }
                }

                if (!archonLocationsCorrect){
                    //update list somehow to correct locations
                    //or start roaming algo if none nearby
                }
            }
        }
    }
}

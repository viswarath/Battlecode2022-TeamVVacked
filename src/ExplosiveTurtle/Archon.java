package ExplosiveTurtle;

import battlecode.common.*;

public class Archon {

    public static int radialDirectionIndex = 0;
    public static int firstMinerPhaseEnd = 16;
    public static int minersSpawned = 0;

    public static boolean enemyArchonNearby = false;
    public static boolean checkedGuessedLocs = false;
    public static MapLocation nearbyEnemyArchonLocation;

    public static int defaultMinerNumber = 2;
    public static int maxMinerSpawns = 16;
    public static int nearbyLeadLocations = 0;

    public static int maxSoldierSpawns = 15;
    public static int soldiersSpawned = 0;

    public static int maxBuilderSpawns = 1;
    public static int builderSpawned = 0;
    
    public static void run(RobotController rc) throws GameActionException{

        Direction build = Direction.CENTER;

        /**
         * delete nearby guessed locations in the shared array
         * different from checking around for enemy locations this takes less precendence
         * can only take place on round three because the archons need the second round to input guessed locations
        **/
        if (!checkedGuessedLocs && rc.getRoundNum() > 2){
            for (int i = 0;i < 12; i++){
                MapLocation check = null;
                int intLoc = rc.readSharedArray(i);
                if (intLoc != 0){
                    check = Data.readMapLocationFromSharedArray(rc, intLoc);
                    if(rc.canSenseLocation(check)){
                        if(rc.canSenseRobotAtLocation(check)){
                            if(rc.senseRobotAtLocation(check).getTeam() == rc.getTeam()){
                                rc.writeSharedArray(i, 0);
                            }
                        } else{
                            rc.writeSharedArray(i, 0);
                        }
                    }
                }
            }
            checkedGuessedLocs = true;
            //print to check updated locations
            System.out.print(rc.getID());
            for(int i = 0;i <12; i++){
                System.out.print("( " +rc.readSharedArray(i) + " )");
            }
        }

        build = getRadialSpawnDir(rc, RobotType.MINER);
        if (minersSpawned < maxMinerSpawns && rc.canBuildRobot(RobotType.MINER, build)){
            rc.buildRobot(RobotType.MINER, build);
            minersSpawned += 1;
        } else if(soldiersSpawned < maxSoldierSpawns && rc.canBuildRobot(RobotType.SOLDIER, build)){
            rc.buildRobot(RobotType.SOLDIER, build);
            soldiersSpawned+= 1;
        } else if (builderSpawned < maxBuilderSpawns && rc.getRoundNum() > 500){
            rc.buildRobot(RobotType.BUILDER, build);
            builderSpawned += 1;
        }

        if (build != Direction.CENTER){
            radialDirectionIndex+=1;
            if (radialDirectionIndex == Data.directions.length){
                radialDirectionIndex = 0;
            }
        }

        for (RobotInfo robot: rc.senseNearbyRobots(20, rc.getTeam())){
            if(robot.getHealth() < 30 && robot.getType() == RobotType.SOLDIER){
                if (rc.canRepair(robot.getLocation())){
                    rc.repair(robot.getLocation());
                    System.out.print("repairing");
                }
            }
        }
        /*
        if (build != Direction.CENTER){
            if (minersSpawned < maxMinerSpawns && rc.canBuildRobot(RobotType.MINER, build)){
                rc.buildRobot(RobotType.MINER, build);
                minersSpawned += 1;
            } else if(soldiersSpawned < maxSoldierSpawns && rc.canBuildRobot(RobotType.SOLDIER, build)){
                rc.buildRobot(RobotType.SOLDIER, build);
            }
            radialDirectionIndex+=1;
            if (radialDirectionIndex == Data.directions.length){
                radialDirectionIndex = 0;
            }
        }
        */
    }

    public static Direction getMinerSpawnDir(RobotController rc) throws GameActionException{
        //RobotType type = RobotType.MINER;
        MapLocation[] leadLocations = rc.senseNearbyLocationsWithLead();
        MapLocation maxLeadLocation = null;
        for (int i = 0; i < leadLocations.length; i++){
            if (maxLeadLocation == null){
                maxLeadLocation = leadLocations[i];
            } else{
                if (rc.senseLead(maxLeadLocation) < rc.senseLead(leadLocations[i])){
                    maxLeadLocation = leadLocations[i];
                }
            }
            System.out.print(maxLeadLocation);
        }
        return Pathfinding.basicBuild(rc, maxLeadLocation, RobotType.MINER);
    }


    public static Direction getRadialSpawnDir(RobotController rc, RobotType type) throws GameActionException{
        for (int i = radialDirectionIndex; i < Data.directions.length; i++){
            if (rc.canBuildRobot(type, Data.directions[i]) == true){
                return Data.directions[i];
            }
        }
        if (radialDirectionIndex != 0){
            for (int i = 0; i < radialDirectionIndex; i++){
                if (rc.canBuildRobot(type, Data.directions[i]) == true){
                    return Data.directions[i];
                }
            }
        }
        return getRadialSpawnDir(rc, RobotType.MINER);
    }

    public static void addPossibleEnemyArchonLocations(RobotController rc, int buffer) throws GameActionException{
        int reflectedX = (rc.getMapWidth()-1) - rc.getLocation().x;
        int reflectedY = (rc.getMapHeight()-1) - rc.getLocation().y;          
        rc.writeSharedArray(0 + buffer, reflectedX*100 + rc.getLocation().y);
        rc.writeSharedArray(1 + buffer, (rc.getLocation().x)*100 + reflectedY);
        rc.writeSharedArray(2 + buffer, reflectedX*100 + reflectedY);
    }

    public static void init(RobotController rc) throws GameActionException{
        nearbyLeadLocations = rc.senseNearbyLocationsWithLead().length;
        maxMinerSpawns = nearbyLeadLocations + defaultMinerNumber;
        firstMinerPhaseEnd = nearbyLeadLocations + defaultMinerNumber;
        if (firstMinerPhaseEnd > maxMinerSpawns){
            firstMinerPhaseEnd = maxMinerSpawns;
        }

        //check if enemy base is within sensing range of archon
        for(RobotInfo robot: rc.senseNearbyRobots()){
            if (robot.getType() == RobotType.ARCHON && robot.getTeam() != rc.getTeam()){
                enemyArchonNearby = true;
                nearbyEnemyArchonLocation = robot.getLocation();
            }
        }

        if (enemyArchonNearby){
            forLoop:
            for(int i = 0; i < 12; i++){
                if (nearbyEnemyArchonLocation == Data.readMapLocationFromSharedArray(rc, rc.readSharedArray(i))){
                    for (int j = 12; j < 16; j++){
                        if (rc.readSharedArray(j) == 0){
                            int loc = nearbyEnemyArchonLocation.x*100 + nearbyEnemyArchonLocation.y;
                            rc.writeSharedArray(j, loc);
                            break forLoop;
                        }
                    }
                }
            }
        }

        //add possible enemy archon locations
        if (rc.readSharedArray(0) == 0){
            addPossibleEnemyArchonLocations(rc, 0);
        } else if (rc.readSharedArray(3) == 0){
            addPossibleEnemyArchonLocations(rc, 3);
        } else if (rc.readSharedArray(6) == 0){
            addPossibleEnemyArchonLocations(rc, 6);
        } else{
            addPossibleEnemyArchonLocations(rc, 9);
        }
        /**
         * print to check guessed locations!
        System.out.print(rc.getID());
        for(int i = 0;i <12; i++){
            System.out.print("( " +rc.readSharedArray(i) + " )");
        }
        **/
    }
}



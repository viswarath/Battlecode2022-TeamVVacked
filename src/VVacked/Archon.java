package VVacked;

import battlecode.common.*;

public class Archon {

    public static int radialDirectionIndex = 0;
    public static int firstMinerPhaseEnd = 16;
    public static int minersSpawned = 0;

    public static boolean enemyArchonNearby = false;

    public static int defaultMinerNumber = 3;
    public static int maxMinerSpawns = 16;
    public static int nearbyLeadLocations = 0;
    
    public static void run(RobotController rc) throws GameActionException{
        Direction build = Direction.CENTER;
        if (minersSpawned < 16){
            build = getMinerSpawnDir(rc);
        } else{
            build = getRadialSpawnDir(rc, RobotType.SOLDIER);
        }
        if (build != Direction.CENTER){
            if (minersSpawned < 16){
                rc.buildRobot(RobotType.MINER, build);
                minersSpawned += 1;
            } else{
                rc.buildRobot(RobotType.SOLDIER, build);
            }
            radialDirectionIndex+=1;
            if (radialDirectionIndex == Data.directions.length){
                radialDirectionIndex = 0;
            }
        }
    }

    public static Direction getMinerSpawnDir(RobotController rc) throws GameActionException{
        RobotType type = RobotType.MINER;
        MapLocation[] leadLocations = rc.senseNearbyLocationsWithLead();
        int rand = Data.rng.nextInt(leadLocations.length);
        for (int i = rand; i < Data.directions.length; i++){
            if (rc.canBuildRobot(type, Data.directions[i]) == true){
                return Data.directions[i];
            }
        }
        if (rand != 0){
            for (int i = 0; i < radialDirectionIndex; i++){
                if (rc.canBuildRobot(type, Data.directions[i]) == true){
                    return Data.directions[i];
                }
            }
        }
        return Direction.CENTER;
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

    public static void init(RobotController rc) throws GameActionException{
        nearbyLeadLocations = rc.senseNearbyLocationsWithLead().length;
        firstMinerPhaseEnd = nearbyLeadLocations + defaultMinerNumber;
        if (firstMinerPhaseEnd > maxMinerSpawns){
            firstMinerPhaseEnd = maxMinerSpawns;
        }

        RobotInfo[] robots = rc.senseNearbyRobots();

        //check if enemy base is within sensing range of archon
        for(RobotInfo robot: robots){
            if (robot.getType() == RobotType.ARCHON && robot.getTeam() != rc.getTeam()){
                enemyArchonNearby = true;
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
    }

    public static void addPossibleEnemyArchonLocations(RobotController rc, int buffer) throws GameActionException{
        int reflectedX = rc.getMapWidth() - 1 - rc.getLocation().x;
        int reflectedY = rc.getMapHeight() - 1 - rc.getLocation().y;
        rc.writeSharedArray(0 + buffer, reflectedX*100 + rc.getLocation().y);
        rc.writeSharedArray(1 + buffer, rc.getLocation().x + reflectedY);
        rc.writeSharedArray(2 + buffer, reflectedX*100 + reflectedY);
    }
}



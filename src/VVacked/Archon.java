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
        Direction build = Direction.CENTER;
        if (rc.getID() == 2 || rc.getID() == 3){
            firstArchon = true;
        }

        // part 2 of 3 round plan
        if (rc.getRoundNum() == 2){
            //check if the estimated locations are nearby
            for (int i = 0; i < rc.getArchonCount()*3;i++){
                
            }
        }
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
        directionIndex+=1;
        if (directionIndex == Data.directions.length){
            directionIndex = 0;
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

    public static void init(RobotController rc){
        nearbyLeadLocations = rc.senseNearbyLocationsWithLead().length;
        firstMinerPhaseEnd = nearbyLeadLocations + defaultMinerNumber;
        if (firstMinerPhaseEnd > maxMinerSpawns){
            firstMinerPhaseEnd = maxMinerSpawns;
        }
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
        forLoop:
        for(int i = 1; i < Data.totalNumArchon*3; i++){
            if(rc.readSharedArray(i) == 0){
                rc.writeSharedArray(i, Communication.convertLocToInt(rc, enemyBaseXReflection));
                rc.writeSharedArray(i+1, Communication.convertLocToInt(rc, enemyBaseYReflection));
                rc.writeSharedArray(i+2, Communication.convertLocToInt(rc, enemyBaseBothReflection));
                break forLoop;
            }
        }

        RobotInfo[] robots = rc.senseNearbyRobots();

        //check if enemy base is within sensing range of archon
        for(RobotInfo robot: robots){
            if (robot.getType() == RobotType.ARCHON && robot.getTeam() != rc.getTeam()){
                enemyArchonNearby = true;
            }
        }
    }
}



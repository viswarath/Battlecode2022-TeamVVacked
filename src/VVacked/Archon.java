package VVacked;

import battlecode.common.*;

public class Archon {

    //public static int radialDirectionIndex = 0; //index to help spawn around in a circle
    public static int minersSpawned = 0; //number of miners spawned
    public static int robotsSpawned = 0; //number of robots spawned
    public static int minerRatio = 3; //spawn miners every this number of soldiers

    public static boolean enemyArchonNearby = false;
    public static boolean checkedGuessedLocs = false;
    public static MapLocation nearbyEnemyArchonLocation;

    public static int defaultMinerNumber = 2; //minimum number of miners to spawn
    public static int maxMinerSpawns = 11; //max number of miners to spawn
    public static int nearbyLeadLocations = 0; //number of lead deposits nearby

    //explosive turtle shit
    public static int soldiersInCircle = 15;
    public static boolean startCooldown = false;
    public static int lookForCircleCooldown = 2;

    public static int leadBeforeBuild;
    
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

        //EXPLOSIVE TURTLE CHECK
        RobotInfo[] robots = rc.senseNearbyRobots();
        int soldiersNearby = 0;
        for (RobotInfo robot : robots){
            if (robot.type == RobotType.SOLDIER && robot.getTeam() == rc.getTeam()){
                soldiersNearby++;
            }
        }
        if (soldiersNearby >= soldiersInCircle){
            forLoop:
            for (int i = 57; i < 64; i+=2){
                if (rc.readSharedArray(i) == rc.getID()){
                    rc.writeSharedArray(i+1, 1);
                    break forLoop;
                }
            }
            startCooldown = true;
        }
        if (startCooldown){
            lookForCircleCooldown--;
            if (lookForCircleCooldown == 0){
                startCooldown = false;
                lookForCircleCooldown = 2;
                rc.writeSharedArray(64, 0);
            }
        }

        //checks lead available
        if (rc.getTeamLeadAmount(rc.getTeam()) > leadBeforeBuild){
            rc.writeSharedArray(40, 1);
        } else if (rc.getTeamLeadAmount(rc.getTeam()) < 51){
            rc.writeSharedArray(40, 0);
        }

        if (minersSpawned < maxMinerSpawns){
            build = getMinerSpawnDir(rc);
        } else{
            if (robotsSpawned%minerRatio != 0){
                build = getSoldierSpawnDir(rc);
            } else{
                build = getMinerSpawnDir(rc);
            }
        }
        if (build != Direction.CENTER){
            if (minersSpawned < maxMinerSpawns && rc.canBuildRobot(RobotType.MINER, build)){
                rc.buildRobot(RobotType.MINER, build);
                minersSpawned += 1;
            } else if(rc.readSharedArray(40) == 1){
                if (robotsSpawned%minerRatio != 0){
                    rc.buildRobot(RobotType.SOLDIER, build);
                } else{
                    rc.buildRobot(RobotType.MINER, build);
                }
                robotsSpawned+=1;
            }
            //radialDirectionIndex+=1;
            // if (radialDirectionIndex == Data.directions.length){
            //     radialDirectionIndex = 0;
            // }
        }
    }

    public static Direction getMinerSpawnDir(RobotController rc) throws GameActionException{
        //RobotType type = RobotType.MINER;
        MapLocation[] leadLocations = rc.senseNearbyLocationsWithLead();
        MapLocation maxLeadLocation = new MapLocation(rc.getLocation().x+1, rc.getLocation().y);
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


    // public static Direction getRadialSpawnDir(RobotController rc, RobotType type) throws GameActionException{
    //     for (int i = radialDirectionIndex; i < Data.directions.length; i++){
    //         if (rc.canBuildRobot(type, Data.directions[i]) == true){
    //             return Data.directions[i];
    //         }
    //     }
    //     if (radialDirectionIndex != 0){
    //         for (int i = 0; i < radialDirectionIndex; i++){
    //             if (rc.canBuildRobot(type, Data.directions[i]) == true){
    //                 return Data.directions[i];
    //             }
    //         }
    //     }
    //     return getRadialSpawnDir(rc, RobotType.MINER);
    // }

    public static Direction getSoldierSpawnDir(RobotController rc)throws GameActionException{
        MapLocation closest = null;
        for (int i = 0; i < 12; i++){
            if (rc.readSharedArray(i) != 0){
                MapLocation loc = Data.readMapLocationFromSharedArray(rc, rc.readSharedArray(i));
                if (closest == null){
                    closest = loc;
                } else if (rc.getLocation().distanceSquaredTo(loc) < rc.getLocation().distanceSquaredTo(closest)){
                    closest = loc;
                }
            }
        }
        return Pathfinding.basicBuild(rc, closest, RobotType.SOLDIER);
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
        int temp = nearbyLeadLocations + defaultMinerNumber;
        if (temp < maxMinerSpawns){
            maxMinerSpawns = temp;
        }

        RobotInfo[] robots = rc.senseNearbyRobots();

        //check if enemy base is within sensing range of archon
        for(RobotInfo robot: robots){
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

        //add id to 57-63 for explosive turtle checking
        forLoop:
        for (int i = 57; i < 64; i+=2){
            if (rc.readSharedArray(i) == 0){
                rc.writeSharedArray(i, rc.getID());
                break forLoop;
            }
        }

        leadBeforeBuild = 50*rc.getArchonCount();
    }
}



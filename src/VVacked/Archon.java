package VVacked;

import battlecode.common.*;

public class Archon {

    //public static int radialDirectionIndex = 0; //index to help spawn around in a circle
    public static int minersSpawned = 0; //number of miners spawned
    public static int robotsSpawned = 0; //number of robots spawned in miner phase
    public static int minerRatio = 3; //spawn miners every this number of soldiers

    public static boolean enemyArchonNearby = false;
    public static boolean checkedGuessedLocs = false;
    public static MapLocation nearbyEnemyArchonLocation;

    public static int defaultMinerNumber = 4; //minimum number of miners to spawn
    public static int maxMinerSpawns = 11; //max number of miners to spawn
    public static int nearbyLeadLocations = 0; //number of lead deposits nearby

    //explosive turtle shit
    public static int soldiersInCircle = 15;
    public static boolean startCooldown = false;
    public static int lookForCircleCooldown = 2;

    public static int leadBeforeBuild;

    public static int soldiertoMinerCount = 0;
    public static int soldierToMinerRatio = 3;
    public static boolean minerPhaseEnd = false;

    //public int minerSpawnIndex
    
    public static void run(RobotController rc) throws GameActionException{

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
            // System.out.print(rc.getID());
            // for(int i = 0;i <12; i++){
            //     System.out.print("( " +rc.readSharedArray(i) + " )");
            // }
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
            for (int i = 56; i < 63; i+=2){
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
                forLoop:
                for (int i = 56; i < 63; i+=2){
                    if (rc.readSharedArray(i) == rc.getID()){
                        rc.writeSharedArray(i+1, 0);
                        break forLoop;
                    }
                }
            }
        }

        //checks lead available
        if (rc.getTeamLeadAmount(rc.getTeam()) > leadBeforeBuild){
            rc.writeSharedArray(40, 1);
        } else if (rc.getTeamLeadAmount(rc.getTeam()) < 16){
            rc.writeSharedArray(40, 0);
        }

        //gets build direction and builds
        BuildLogic(rc);
    }

    private static void BuildLogic(RobotController rc) throws GameActionException {
        Direction build;
        if (minersSpawned < maxMinerSpawns){
            if (soldiertoMinerCount%soldierToMinerRatio == 0){
                build = getMinerSpawnDir(rc);
            } else{
                build = getSoldierSpawnDir(rc);
            }
        } else{
            checkForMinerPhaseEnd(rc);
            if (robotsSpawned%minerRatio != 0){
                build = getSoldierSpawnDir(rc);
            } else{
                build = getMinerSpawnDir(rc);
            }
        }
        //actually builds
        if (build != Direction.CENTER){
            if (minersSpawned < maxMinerSpawns){
                if (soldiertoMinerCount%soldierToMinerRatio != 0){
                    if (rc.canBuildRobot(RobotType.MINER, build)){
                        rc.buildRobot(RobotType.MINER, build);
                        System.out.println("SPAWNING MINER IN MINER PHASE");
                        minersSpawned++;
                        soldiertoMinerCount++;
                    }
                } else{
                    if (rc.canBuildRobot(RobotType.SOLDIER, build)){
                        rc.buildRobot(RobotType.SOLDIER, build);
                        System.out.println("SPAWNING SOLDIER IN MINER PHASE");
                        soldiertoMinerCount++;
                    }
                }
                
            } else if(rc.readSharedArray(40) == 1){
                if (robotsSpawned%minerRatio != 0){
                    rc.buildRobot(RobotType.SOLDIER, build);
                    robotsSpawned+=1;
                } else{
                    rc.buildRobot(RobotType.MINER, build);
                    minersSpawned++;
                    robotsSpawned+=1;
                }
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
        }

        Direction dir = Pathfinding.basicBuild(rc, maxLeadLocation, RobotType.MINER);
        //if (minersSpawned % 3 != 0){
            return dir;
        //} else{

        //}
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

    public static void checkForMinerPhaseEnd(RobotController rc) throws GameActionException{
        minerPhaseEnd = true;
        forLoop:
        for (int i = 41; i < 48; i+=2){
            if (rc.readSharedArray(i) == rc.getID()){
                if (rc.readSharedArray(i+1) != 1){
                    rc.writeSharedArray(i+1, 1);
                }
                break forLoop;
            }
        }
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

        //add id to 56-63 for explosive turtle checking
        forLoop:
        for (int i = 56; i < 63; i+=2){
            if (rc.readSharedArray(i) == 0){
                rc.writeSharedArray(i, rc.getID());
                break forLoop;
            }
        }

        //add id to 41-48 for soldier scouts
        forLoop:
        for (int i = 41; i < 48; i+=2){
            if (rc.readSharedArray(i) == 0){
                rc.writeSharedArray(i, rc.getID());
                break forLoop;
            }
        }

        leadBeforeBuild = 75*rc.getArchonCount();

        int area = rc.getMapHeight()*rc.getMapWidth();
        if (area <900){
            soldiersInCircle = 10;
        } else if (area < 1600){
            soldiersInCircle = 15;
        } else if (area < 2500){
            soldiersInCircle = 20;
        } else{
            soldiersInCircle = 25;
        }

        //set randoms
        rc.writeSharedArray(38, Data.rng.nextInt(4));
        rc.writeSharedArray(38, Data.rng.nextInt(3));
    }
}



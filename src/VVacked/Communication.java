package VVacked;

import battlecode.common.*;

public class Communication {
    public static MapLocation convertIntToLoc(RobotController rc,int digits) throws GameActionException{
        int x = digits/100;
        int y = digits%100;
        MapLocation loc = new MapLocation(x,y);
        return loc;
    }
    public static int convertLocToInt(RobotController rc,MapLocation loc) throws GameActionException{
        int x = loc.x;
        int y = loc.y;
        return x*100+y;
    }
}

package VVacked;

import battlecode.common.*;
import java.util.Random;


/**
 * RobotPlayer is the class that describes your main robot strategy.
 * The run() method inside this class is like your main function: this is what we'll call once your robot
 * is created!
 */
public strictfp class RobotPlayer {
    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * It is like the main function for your robot. If this method returns, the robot dies!
     *
     * @param rc  The RobotController object. You use it to perform actions from this robot, and to get
     *            information on its current status. Essentially your portal to interacting with the world.
     **/

    /**
     * We will use this variable to count the number of turns this robot has been alive.
     * You can use static variables like this to save any information you want. Keep in mind that even though
     * these variables are static, in Battlecode they aren't actually shared between your robots.
     */

    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {
        switch (rc.getType()) {
            case ARCHON:     //Archon.init(rc);  break;
            case MINER:      Miner.init(rc);   break;
            case SOLDIER:    Soldier.init(rc); break;
            case LABORATORY: //Laboratory.init(rc); break;
            case WATCHTOWER: //Watchtower.init(rc); break;
            case BUILDER:    //Builder.init(rc); break;
            case SAGE:       //Sage.init(rc); break;
            default:
                break;
        }
        

        while (true) {

            Data.turnCount += 1;

            try {
                switch (rc.getType()) {

                    case ARCHON:        Archon.run(rc);         break;
                    case MINER:         Miner.run(rc);          break;
                    case SOLDIER:       Soldier.run(rc);        break;
                    case LABORATORY:    //Laboratory.run(rc);     break;
                    case WATCHTOWER:    //Watchtower.run(rc);     break;
                    case BUILDER:      // Builder.run(rc);        break;
                    case SAGE:          //Sage.run(rc);           break;
                }

            } catch (GameActionException e) {
                // Oh no! It looks like we did something illegal in the Battlecode world. You should
                // handle GameActionExceptions judiciously, in case unexpected events occur in the game
                // world. Remember, uncaught exceptions cause your robot to explode!
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();

            } catch (Exception e) {
                // Oh no! It looks like our code tried to do something bad. This isn't a
                // GameActionException, so it's more likely to be a bug in our code.
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();

            } finally {
                // Signify we've done everything we want to do, thereby ending our turn.
                // This will make our code wait until the next turn, and then perform this loop again.
                Clock.yield();
            }
            // End of loop: go back to the top. Clock.yield() has ended, so it's time for another turn!
        }
    }
}

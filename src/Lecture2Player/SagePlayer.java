package Lecture2Player;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

import java.util.Random;

public class SagePlayer {

    static final Direction[] directions = RobotPlayer.directions;
    static final Random rng = new Random();

    static void runSage(RobotController rc) throws GameActionException {
        Direction dir = directions[rng.nextInt(directions.length)];
        if (rc.canMove(dir)) {
            rc.move(dir);
            //System.out.println("I moved!");
        }
    }
}

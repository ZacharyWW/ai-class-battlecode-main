package Lecture2Player;

import battlecode.common.*;

import java.util.Random;

public class ArchonPlayer {
    static final Direction[] directions = RobotPlayer.directions;
    static final Random rng = new Random();
    /**
     * Run a single turn for an Archon.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    static void runArchon(RobotController rc) throws GameActionException {
        // Pick a direction to build in.
        Direction dir = directions[rng.nextInt(directions.length)];
        Team us = rc.getTeam();
        Team opponent = rc.getTeam().opponent();
        int radius = rc.getType().visionRadiusSquared;


        if (rng.nextBoolean() && rc.senseNearbyRobots(radius, us).length > rc.senseNearbyRobots(radius, opponent).length) {
            rc.setIndicatorString("Miner pregnancy");
            if (rc.canBuildRobot(RobotType.MINER, dir)) {
                rc.buildRobot(RobotType.MINER, dir);
            }
        } else {
            rc.setIndicatorString("Soldier pregnancy");
            if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
                rc.buildRobot(RobotType.SOLDIER, dir);
            }
        }

        if(rc.senseNearbyRobots(radius, us).length > rc.senseNearbyRobots(radius, opponent).length){
            rc.setIndicatorString("Soldier pregnancy");
            dir = directions[rng.nextInt(directions.length)];
            if(rc.canBuildRobot(RobotType.SOLDIER, dir)){
                rc.buildRobot(RobotType.SOLDIER, dir);
            }
        }

        dir = directions[rng.nextInt(directions.length)];
        
        rc.setIndicatorString("Sage pregnancy");
        if(rc.canBuildRobot(RobotType.SAGE, dir)){
            rc.buildRobot(RobotType.SAGE, dir);
        }
    }

}

package SamplePlayerWithBSWL;

import battlecode.common.*;

import java.util.*;

public class ArchonPlayer {
    static final Direction[] directions = RobotPlayer.directions;
    static final Random rng = new Random();

    static int miners = 0, soldiers = 0, builders = 0, sages = 0;

    /**
     * Run a single turn for an Archon.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    static void runArchon(RobotController rc) throws GameActionException {
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

        if(rc.getTeamGoldAmount(us) > rc.getTeamLeadAmount(opponent) + 50){
            rc.setIndicatorString("Sage pregnancy");
            dir = directions[rng.nextInt(directions.length)];
            if(rc.canBuildRobot(RobotType.SAGE, dir)){
                rc.buildRobot(RobotType.SAGE, dir);
            }
        }
        if(rc.getTeamLeadAmount(us) > rc.getTeamLeadAmount(opponent) + 500){
            dir = directions[rng.nextInt(directions.length)];
            rc.setIndicatorString("Builder pregnancy");
            if(rc.canBuildRobot(RobotType.BUILDER, dir)){
                rc.buildRobot(RobotType.BUILDER, dir);
            }
        }
    }



}

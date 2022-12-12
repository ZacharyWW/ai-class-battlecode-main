package SamplePlayerWithBSWL;

import battlecode.common.*;

import java.util.Random;

import static SamplePlayerWithBSWL.MoveStrategy.move;

public class SagePlayer {

    static final Direction[] directions = RobotPlayer.directions;
    static final Random rng = new Random();

    static Direction exploreDir = null;

    static final AnomalyType[] anomalies = {AnomalyType.ABYSS, AnomalyType.CHARGE, AnomalyType.FURY, AnomalyType.VORTEX};

    static void runSage(RobotController rc) throws GameActionException {

        if(exploreDir == null){
            exploreDir = directions[rng.nextInt(directions.length)];
        }

        if(!isNearSelfArchon(rc)){
            AnomalyType targetAnomaly = anomalies[rng.nextInt(anomalies.length)];
            if(rc.canEnvision(targetAnomaly)){
                rc.envision(targetAnomaly);
            }
        }

        move(rc, exploreDir);
    }

    static boolean isNearSelfArchon(RobotController rc) throws GameActionException{
        RobotInfo[] nearbyRobots = rc.senseNearbyRobots();

        for(RobotInfo robot : nearbyRobots){
            if(robot.team==rc.getTeam() && robot.type == RobotType.ARCHON)
                return true;
        }

        return false;
    }
}

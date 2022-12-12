package SamplePlayerWithBSWL;

import battlecode.common.*;

import java.util.*;

import static SamplePlayerWithBSWL.MoveStrategy.move;

strictfp class BuilderPlayer {

    static Direction[] directions = RobotPlayer.directions;
    static Random rng = RobotPlayer.rng;

    static void runBuilder(RobotController rc, int turnCount) throws GameActionException{


        MapLocation me = rc.getLocation();
        for(int dx = -1; dx <2; dx++){
            for(int dy = -1; dy < 2; dy++){
                MapLocation repLocation = new MapLocation(me.x + dx, me.y + dy);
                while(rc.canRepair(repLocation)){
                    rc.repair(repLocation);
                }
            }
        }

        Team us = rc.getTeam();

        RobotInfo[] nearby = rc.senseNearbyRobots();
        Direction dir = null;
        int targetDist = Integer.MAX_VALUE;

        for(RobotInfo robot : nearby){
            if(robot.team.equals(us) && robot.type.isBuilding() && robot.health < robot.type.getMaxHealth(robot.level)){
                if(targetDist > me.distanceSquaredTo(robot.location)){
                    targetDist = me.distanceSquaredTo(robot.location);
                    dir = me.directionTo(robot.location);
                }
            }
        }
        if(dir != null){
            if(rc.canMove(dir)){
                rc.move(dir);
            }
            else{
                move(rc);
            }
        }
        else{
            move(rc);
        }

        dir = directions[rng.nextInt(directions.length)];

        int safety = SensingNearby.senseSafety(rc, rc.getLocation().add(dir));

        if(safety > 0){
            if(turnCount % 75 == 0 && rc.getTeamLeadAmount(us) > 10000 && rc.canBuildRobot(RobotType.LABORATORY, dir)){
                rc.setIndicatorString("Laboratory pregnancy");
                rc.buildRobot(RobotType.LABORATORY, dir);
            }
            else if(turnCount % 50 == 0 && rc.getTeamLeadAmount(us) > 7000 && rc.canBuildRobot(RobotType.WATCHTOWER, dir)){
                rc.setIndicatorString("WatchTower pregnancy");
                rc.buildRobot(RobotType.WATCHTOWER, dir);
            }
        }
        else {
            if(turnCount % 50 == 0 && rc.getTeamLeadAmount(us) > 7000 && rc.canBuildRobot(RobotType.WATCHTOWER, dir)){
                rc.setIndicatorString("WatchTower pregnancy");
                rc.buildRobot(RobotType.WATCHTOWER, dir);
            }
        }
    }

}

package SamplePlayerWithBSWL;

import battlecode.common.*;
//import java.util.Random;

strictfp class WatchTowerPlayer {
    //static Direction[] directions = RobotPlayer.directions;
    //static Random rng = new Random();

    static void runWatchTower(RobotController rc) throws GameActionException{
        int radius = rc.getType().visionRadiusSquared;
        MapLocation toAttack = null;
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, rc.getTeam().opponent());
        if(enemies.length > 0){
            toAttack = enemies[0].location;
            if(rc.canAttack(toAttack)){
                rc.attack(toAttack);
                rc.setIndicatorString("Attacking");
            }
        }
    }
}

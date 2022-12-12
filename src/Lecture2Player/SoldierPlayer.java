package Lecture2Player;

import battlecode.common.*;

import java.util.Random;

public class SoldierPlayer {
    static final Direction[] directions = RobotPlayer.directions;
    static final Random rng = new Random();
    /**
     * Run a single turn for a Soldier.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    static void runSoldier(RobotController rc) throws GameActionException {
        // Try to attack someone
        int radius = rc.getType().visionRadiusSquared;
        Team opponent = rc.getTeam().opponent();
        MapLocation me = rc.getLocation();
        MapLocation toAttack = null;
        int targetDistance = Integer.MAX_VALUE;
        Direction targetDir = null;

        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);

        for(RobotInfo enemy : enemies){
            if(me.distanceSquaredTo(enemy.location) < targetDistance){
                toAttack = enemy.location;
                targetDir = me.directionTo(enemy.location);
            }
        }

        if(toAttack != null && rc.canAttack(toAttack)){
            rc.attack(toAttack);
        }

        // Also try to move toward it
        if (toAttack != null) {
            if(rc.canAttack(toAttack)){
                rc.attack(toAttack);
                //System.out.println("Target attacked!");
            }
            else if(rc.canMove(targetDir)){
                rc.move(targetDir);
                //System.out.println("Target locked");
            }
            else{
                Direction dir = directions[rng.nextInt(directions.length)];
                if (rc.canMove(dir)) {
                    rc.move(dir);
                    //System.out.println("I moved!");
                }
            }
        }
        else{
            Direction dir = directions[rng.nextInt(directions.length)];
            if (rc.canMove(dir)) {
                rc.move(dir);
                //System.out.println("I moved!");
            }
        }

    }

}

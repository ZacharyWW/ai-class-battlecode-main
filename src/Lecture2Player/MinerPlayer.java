package Lecture2Player;

import battlecode.common.*;

import java.util.Random;

class MinerPlayer {

    static final Direction[] directions = RobotPlayer.directions;
    static final Random rng = new Random();

    /**
     * Run a single turn for a Miner.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    public static void runMiner(RobotController rc) throws GameActionException {
        // Try to mine on squares around us.
        MapLocation me = rc.getLocation();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                MapLocation mineLocation = new MapLocation(me.x + dx, me.y + dy);
                // Notice that the Miner's action cooldown is very low.
                // You can mine multiple times per turn!
                while (rc.canMineGold(mineLocation) && rc.senseLead(mineLocation) > 100) {
                    rc.mineGold(mineLocation);
                }
                while (rc.canMineLead(mineLocation)) {
                    rc.mineLead(mineLocation);
                }
            }
        }

        int visionRadius = rc.getType().visionRadiusSquared;
        MapLocation[] seeableLocations = rc.getAllLocationsWithinRadiusSquared(me, visionRadius);
        MapLocation targetLocation = null;
        int targetDistance = Integer.MAX_VALUE;
        Direction targetdir = null;

        for(MapLocation activeLocation : seeableLocations ){
            if(rc.senseGold(activeLocation) > 0 || rc.senseLead(activeLocation) > 100){
                if(me.distanceSquaredTo(activeLocation) < targetDistance){
                    targetLocation = activeLocation;
                    targetDistance = me.distanceSquaredTo(activeLocation);
                    targetdir = me.directionTo(activeLocation);
                }
            }
        }

        if(targetLocation != null){
            if(rc.canMove(targetdir)) {
                rc.move(targetdir);
                //System.out.println("Target locked!");
            }
        }
        else {
            // Also try to move randomly.
            Direction dir = directions[rng.nextInt(directions.length)];
            if (rc.canMove(dir)) {
                rc.move(dir);
                //System.out.println("I moved!");
            }
        }
    }
}

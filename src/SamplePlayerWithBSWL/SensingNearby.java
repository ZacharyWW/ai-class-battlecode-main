package SamplePlayerWithBSWL;

import battlecode.common.*;

public class SensingNearby {
    static int senseSafety(RobotController rc, MapLocation location){

        int radius = rc.getType().visionRadiusSquared;

        RobotInfo[] allies = rc.senseNearbyRobots(location, radius, rc.getTeam());
        RobotInfo[] enemies = rc.senseNearbyRobots(location, radius, rc.getTeam().opponent());

        int allies_HP = findAttackingHP(allies), enemy_HP = findAttackingHP(enemies);

        return allies_HP - enemy_HP;
    }

    static int findAttackingHP(RobotInfo[] robots){
        int total_HP = 0;
        for(RobotInfo robot : robots){
            if(RobotPlayer.canAttack(robot)){
                total_HP += robot.getHealth();
            }
        }
        return total_HP;
    }

    static MapLocation senseArchonToAttack(RobotController rc){
        RobotInfo[] robotsNearby = rc.senseNearbyRobots();
        for(RobotInfo robot : robotsNearby){
            if(robot.type == RobotType.ARCHON && robot.team == rc.getTeam().opponent()){
                return robot.location;
            }
            if(robot.type == RobotType.ARCHON && robot.team == rc.getTeam() && senseSafety(rc, robot.location) < 10)
                return robot.location;
        }
        return null;
    }
}

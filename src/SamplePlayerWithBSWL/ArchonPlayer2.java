package SamplePlayerWithBSWL;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class ArchonPlayer2 {
    static int miners = 0, soldiers = 0, builders = 0, sages = 0;

    static void runArchon(RobotController rc) throws GameActionException{

        RobotType toBuild = RobotType.SOLDIER;

        if( rc.getTeamGoldAmount(rc.getTeam()) > rc.getTeamGoldAmount(rc.getTeam().opponent()) + 50 ){
            buildRobot(rc, RobotType.SAGE);
        }

        if(SensingNearby.senseSafety(rc, rc.getLocation()) > 0){
            rc.setIndicatorString("SAFE " + miners + " " + soldiers + " "+ builders + " " + sages);
            if(miners < 5){
                toBuild = RobotType.MINER;
            } else if (soldiers < 10){
                toBuild = RobotType.SOLDIER;
            } else if (builders < 1){
                toBuild = RobotType.BUILDER;
            } else if (miners < soldiers * 9/10 && rc.getTeamLeadAmount(rc.getTeam()) < 5000){
                toBuild = RobotType.MINER;
            } else if (builders < soldiers / 30){
                toBuild = RobotType.BUILDER;
            }
        }
        buildRobot(rc, toBuild);

    }

    static void buildRobot(RobotController rc, RobotType type) throws GameActionException {
        Direction[] dirs = MoveStrategy.lowestRubble(rc);
        int built = 0;
        for(Direction dir : dirs){
            if(rc.canBuildRobot(type, dir)){
                rc.buildRobot(type, dir);
                built = 1;
                break;
            }
        }

        if(true){
            switch (type){
                case BUILDER: builders++; break;
                case MINER: miners++; break;
                case SAGE: sages++; break;
                case SOLDIER: soldiers++; break;
            }
        }
    }

}

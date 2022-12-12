package SamplePlayerWithBSWL;

import battlecode.common.*;

import java.util.Arrays;
import java.util.Random;

strictfp class MoveStrategy {
    static Direction[] directions = RobotPlayer.directions;
    static Random rng = RobotPlayer.rng;

    static void move(RobotController rc) throws GameActionException{

        Direction[] dirs = lowestRubble(rc);
        for(int i = 0; i < dirs.length; i++){
            if(rc.canMove(dirs[i])){
                rc.move(dirs[i]);
                return;
            }
        }
    }
    static void move(RobotController rc, Direction targetDir) throws GameActionException{
        if(rc.canMove(targetDir)){
            rc.move(targetDir);
            return;
        }
        Direction[] dirs = lowestRubble(rc);
        for(int i = 0; i < dirs.length; i++){
            if(rc.canMove(dirs[i])){
                rc.move(dirs[i]);
                return;
            }
        }
    }
    static void move(RobotController rc, Direction targetDir, Direction exploreDir) throws GameActionException{
        if(rc.canMove(targetDir)){
            rc.move(targetDir);
            return;
        }
        else if(rc.canMove(exploreDir)){
            rc.move(exploreDir);
            return;
        }
        Direction[] dirs = lowestRubble(rc);
        for(int i = 0; i < dirs.length; i++){
            if(rc.canMove(dirs[i])){
                rc.move(dirs[i]);
                return;
            }
        }
    }

    static Direction[] lowestRubble(RobotController rc) throws GameActionException{
        Direction[] dirs = Arrays.copyOf(directions, directions.length);
        Arrays.sort(dirs, (a,b)-> {
            if(getRubble(rc, a) - getRubble(rc, b) != 0)
                return getRubble(rc, a) - getRubble(rc, b);
            else{
                if(rng.nextBoolean())
                    return 1;
                else
                    return -1;
            }
        });
        return dirs;
    }
    static int getRubble(RobotController rc, Direction dir){
        try{
            return(rc.senseRubble(rc.getLocation().add(dir)));
        }
        catch (GameActionException e){
            return 0;
        }
    }
}

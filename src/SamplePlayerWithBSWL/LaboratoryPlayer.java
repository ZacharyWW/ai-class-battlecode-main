package SamplePlayerWithBSWL;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class LaboratoryPlayer {
    static void runLaboratory(RobotController rc) throws GameActionException {
        if(rc.getTeamLeadAmount(rc.getTeam()) > 5000 && rc.canTransmute()){
            rc.transmute();
        }
    }
}

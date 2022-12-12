package strat1;

import static strat1.RobotPlayer.myTeam;
import static strat1.RobotPlayer.rc;

// Utility imports
import battlecode.common.GameActionException;

// Labaratory class used to transmute lead to gold.
public strictfp class Laboratory {
    // Determine the rate at which lead can be transmuted. 
    // The laboratory will only transmute lead if the transmutation rate is below 10.
	private static final int THRESHOLD = 10;

    // Uses current round number to determine the amount of lead needed to transmute. 
    // If the round number is less than 1000, the threshold is 125, 
    // if it is less than 1500, the threshold is 250, 
    // and if it is greater than 1500, the threshold is 500.
	private static double calculateLeadThreshold() {
		if (rc.getRoundNum() < 1000) return 125;
		if (rc.getRoundNum() < 1500) return 250;
		return 500;
	}

    // Checks if the Laboratory has enough lead and if the 
    // transmutation rate is below the threshold. If both of 
    // these conditions are met, then it will use the 
    // calculateLeadThreshold and canTransmute functions to determine if it is able to transmute 
    // lead into gold.
	public static void run() throws GameActionException {
		if (
            rc.isActionReady() &&
            rc.getTeamLeadAmount(myTeam) >= calculateLeadThreshold() &&
			rc.getTransmutationRate() <= THRESHOLD &&
            rc.canTransmute()
        ) {
            rc.transmute();
		}
	}
}
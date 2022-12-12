package strat1;

import battlecode.common.*;

import java.util.Random;

import battlecode.common.*;

// Robot Player class used to control our side of hte game.
public strictfp class RobotPlayer {

	// toggle logs
	public static final boolean DEBUG = false;

	// Global variables
	public static RobotController rc;
	public static Team myTeam, enemyTeam;
	public static RobotType myType;
	public static int archonCount;
	public static double leadIncome = 0;
	public static double goldIncome = 0;

	// Directions (used by many classes throughout the strategy)
	public static final Direction[] directions = {
			Direction.NORTH,
			Direction.NORTHEAST,
			Direction.EAST,
			Direction.SOUTHEAST,
			Direction.SOUTH,
			Direction.SOUTHWEST,
			Direction.WEST,
			Direction.NORTHWEST,
	};

	// Constant values.
	private static final double LEAD_BETA = 0.33; // used to calculate lead income
	private static final double GOLD_BETA = 0.33; // used to calculate gold income

	// Variables used to calculate income.
	// Stores the last round's lead and gold amounts.
	private static int lastRoundLead = 0;
	private static int lastRoundGold = 0;

	// Updates the income variables.
	private static void updateIncome() {
		leadIncome = LEAD_BETA * (rc.getTeamLeadAmount(myTeam) - lastRoundLead) + (1 - LEAD_BETA) * leadIncome;
		goldIncome = GOLD_BETA * (rc.getTeamGoldAmount(myTeam) - lastRoundGold) + (1 - GOLD_BETA) * goldIncome;
		lastRoundLead = rc.getTeamLeadAmount(myTeam);
		lastRoundGold = rc.getTeamGoldAmount(myTeam);
	}


	// Run the code :D
	public static void run (RobotController robotController) throws GameActionException {
		rc = robotController;
		myTeam = rc.getTeam();
		enemyTeam = myTeam.opponent();
		myType = rc.getType();
		
		switch (myType) {
			case MINER:
				Miner.init();
				break;
			// case SOLDIER:
			// 	Soldier.init();
			// 	break;
			case BUILDER:
				Builder.init();
				break;
			case ARCHON:
				Archon.init();
				break;
		}

		while (true) {
			switch (myType) {
				case LABORATORY:
					Laboratory.run();
					break;
				case MINER:
					Miner.run();
					break;
				case BUILDER:
					Builder.run();
					break;
				// case SOLDIER:
				// 	Soldier.run();
				// 	break;
				// case WATCHTOWER:
				// 	Watchtower.run();
				// 	break;
				case ARCHON:
					Archon.run();
					break;
			}

			updateIncome();

			Clock.yield();
		}
	}
}
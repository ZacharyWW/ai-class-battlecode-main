package strat1;

// Import Global Varaibles from RobotPlayer
import static strat1.RobotPlayer.archonCount;
import static strat1.RobotPlayer.rc;
import static strat1.util.Functions.getBits;

// All Battlecode utils
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import strat1.helpers.GoldMiningHelper;
import strat1.helpers.LeadMiningHelper;
import strat1.helpers.MovementHelper;

// Miner class that mines lead and gold
public strictfp class Miner {

    // Creator Archon information
	private static MapLocation creatorArchonLocation;
	private static int creatorArchonIndex;

    // Current pointed direction
	private static Direction currentDirection;

    // Mine gold and lead
	public static void run() throws GameActionException {
        
        // Attempt to mine gold if it is within range
		GoldMiningHelper.mineGold();

        // If the miner can move, attempt to find gold
		if (rc.isMovementReady()) {
			MapLocation goldLocation = GoldMiningHelper.getGoldLocation();

            // If gold is found, move towards it
			if (goldLocation != null) {
				MovementHelper.tryMove(rc.getLocation().directionTo(goldLocation), false);
			}
		}
        
        // Attempt to mine lead if possible
		if (LeadMiningHelper.canMineLead()) {
			LeadMiningHelper.mineLead();
		}

        // If the miner can move, attempt to find lead
		if (rc.isMovementReady()) {
			Direction leadDirection = LeadMiningHelper.spotLead();

            // If lead is found, move towards it
			if (leadDirection != null) {
				MovementHelper.tryMove(leadDirection, false);

            // If lead cannot be found, just keep moving in the current direction.
			} else {
				MovementHelper.tryMove(currentDirection, false);
			}
		}
	}

    // Initialize the miner
	public static void init() throws GameActionException {
		for (int i = 32; i < 32 + archonCount; ++i) {
			int value = rc.readSharedArray(i);
			creatorArchonLocation = new MapLocation(getBits(value, 6, 11), getBits(value, 0, 5));
			currentDirection = creatorArchonLocation.directionTo(rc.getLocation());
			if (rc.getLocation().distanceSquaredTo(creatorArchonLocation) <= 2) {
				creatorArchonIndex = i - 32;
				break;
			}
		}
	}
}
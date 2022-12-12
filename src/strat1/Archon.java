package strat1;

import static strat1.RobotPlayer.archonCount;
import static strat1.RobotPlayer.directions;
import static strat1.RobotPlayer.myTeam;
import static strat1.RobotPlayer.myType;
import static strat1.RobotPlayer.rc;
import static strat1.util.Functions.getBits;
import static strat1.util.Functions.setBits;

// Utilities used by the archon contract. We also need everything from the RobotPlayer class.

// Import Custom Utils
import java.util.Random;

// Import BattleCode Utils
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import strat1.helpers.CommsHelper;
import strat1.helpers.SpawnHelper;

public class Archon {
    // Create a new random number generator.
    private static final Random rng = new Random(rc.getID());

    // Used to determine when the Archon should start building new robots.
	private static final int BUILD_THRESHOLD = 80;

    // Used to determine the ideal portion of miners, soldiers, and builders to build.
	private static double minersRatio = 0.3;
	private static double soldiersRatio = 0.65;
	private static double buildersRatio = 0.05;

    // Dynamic variables used during the game.
	private static int buildDirectionIndex = 0;
	private static int myIndex;

	private static int droidsBuilt = 0;
	private static int minersBuilt = 0;
	private static int soldiersBuilt = 0;
	private static int buildersBuilt = 0;

    // Used to determine whether enemy archons are symmetrically placed.
	private static boolean[] isPossibleEnemyArchonSymmetry;
	private static int symmetryIndex = 0;

    // Uses data about the location of nearby lead deposits and existing miners to determine the best direction to spawn a new miner.
	private static Direction getBestMinerDirection() throws GameActionException {
		// Sense nearby robots and count the number of miners in each direction.
		int[] minersInDirection = new int[8];
		for (RobotInfo robot : rc.senseNearbyRobots(myType.visionRadiusSquared, myTeam)) {
			if (robot.type == RobotType.MINER) {
				++minersInDirection[rc.getLocation().directionTo(robot.location).ordinal()];
			}
		}

		// Find the location of nearby lead and add it to our leadInDirection array.
		int[] leadInDirection = new int[8];
		for (MapLocation loc : rc.senseNearbyLocationsWithLead(myType.visionRadiusSquared)) {
			Direction toLead = rc.getLocation().directionTo(loc);
			if (toLead != Direction.CENTER) {
				leadInDirection[toLead.ordinal()] += rc.senseLead(loc);
			}
		}

		// Find the best direction to spawn a miner. Use the following equation:
		// priority = theta1 * lead - theta2 * miners.
		// This basically enables us to find the ideal spot with lots of lead and few miners.
		// Credit: dernosmirc on github
		double theta1 = 0.9;
		double theta2 = 20;
		double maxPriority = -theta2 * 1000;
		Direction optimalDirection = Direction.NORTHEAST; // Default direction.

		// Calculate the priority for each direction and choose the one with the highest priority.
		for (Direction dir : directions) {
			double priority = theta1 * leadInDirection[dir.ordinal()] - theta2 * minersInDirection[dir.ordinal()];
			if (priority > maxPriority) {
				maxPriority = priority;
				optimalDirection = dir;
			}
		}

		// Use the spawn helper to determine the best direction to spawn a miner.
		return SpawnHelper.getForceSpawnDirection(optimalDirection);
	}

	// Get the index of the archon that should build the next droid.
	private static int getBuildArchonIndex() throws GameActionException {
		// Set an extremely high value so that any archons droids are set to minDroids.
		int minDroids = 100000;

		// Iterate over each archon.
		int index = 0;
		for (int i = 10; i < 10 + archonCount; ++i) {
			int droids = getBits(rc.readSharedArray(i), 0, 15);
			if (droids < minDroids) {
				minDroids = droids;
				index = i - 10;
			}
		}
		
		// Calculate the index.
		return index;
	}

	// Update the total number of droids built by the archon.
	private static void updateDroidsBuilt() throws GameActionException {
		// Write this value to the shared array.
		rc.writeSharedArray(10 + myIndex, droidsBuilt);
	}

	// Archon syymmetry detection. Credit: dernosmirc on github
	private static void broadcastSymmetry() throws GameActionException {
		if (CommsHelper.foundEnemyArchon()) {
			return;
		}

		int value = getBits(rc.readSharedArray(5), 3 * myIndex, 3 * myIndex + 2);
		if ((value & 0b1) != 0) {
			isPossibleEnemyArchonSymmetry[0] = false;
		}
		if ((value & 0b10) != 0) {
			isPossibleEnemyArchonSymmetry[1] = false;
		}
		if ((value & 0b100) != 0) {
			isPossibleEnemyArchonSymmetry[2] = false;
		}

		for (int i = 0; i < 3; ++i) {
			if (symmetryIndex == 3) {
				symmetryIndex = 0;
			}
			if (isPossibleEnemyArchonSymmetry[symmetryIndex]) {
				CommsHelper.updateSymmetry(myIndex, symmetryIndex);
				++symmetryIndex;
				return;
			}

			++symmetryIndex;
		}

		CommsHelper.updateSymmetry(myIndex, 3);
	}

	public static void run() throws GameActionException {
		int lead = rc.getTeamLeadAmount(myTeam);
		int archonIndex = getBuildArchonIndex();
		if (archonIndex != myIndex) {
			if (lead >= 2 * BUILD_THRESHOLD) {}
			else return;
		}

		if (rc.isActionReady() && lead >= BUILD_THRESHOLD) {
			RobotType spawnType = RobotType.BUILDER;
			if (droidsBuilt < 2) {
				spawnType = RobotType.MINER;
			} else if (soldiersBuilt < 3) {
				spawnType = RobotType.SOLDIER;
			} else {
				double randomNumber = rng.nextDouble();
				if (randomNumber < soldiersRatio) {
					spawnType = RobotType.SOLDIER;
				} else if (randomNumber < soldiersRatio + minersRatio) {
					spawnType = RobotType.MINER;
				} else {
					spawnType = RobotType.BUILDER;
				}
			}

			if (spawnType == RobotType.MINER) {
				Direction spawnDirection = getBestMinerDirection();
				if (spawnDirection != Direction.CENTER && rc.canBuildRobot(RobotType.MINER, spawnDirection)) {
					rc.buildRobot(RobotType.MINER, spawnDirection);
					++droidsBuilt;
					++minersBuilt;
					updateDroidsBuilt();
				}
			} else {
				for (int i = 0; i < directions.length; ++i) {
					if (rc.canBuildRobot(spawnType, directions[buildDirectionIndex])) {
						rc.buildRobot(spawnType, directions[buildDirectionIndex]);
						++droidsBuilt;
						if (spawnType == RobotType.SOLDIER) {
							++soldiersBuilt;
							broadcastSymmetry();
						} else if (spawnType == RobotType.BUILDER) {
							++buildersBuilt;
						}
						updateDroidsBuilt();
						break;
					}

					++buildDirectionIndex;
					if (buildDirectionIndex == directions.length)
						buildDirectionIndex = 0;
				}
			}
		}
	}

	// Init function credit: dernosmirc on github
	public static void init() throws GameActionException {
		archonCount = rc.getArchonCount();
		for (int i = 32; i < 32 + archonCount; ++i) {
			int value = rc.readSharedArray(i);
			if (getBits(value, 15, 15) == 0) {
				value = setBits(0, 15, 15, 1);
				value = setBits(value, 6, 11, rc.getLocation().x);
				value = setBits(value, 0, 5, rc.getLocation().y);
				myIndex = i - 32;
				rc.writeSharedArray(i, value);
				break;
			}
		}

		isPossibleEnemyArchonSymmetry = new boolean[3];
		isPossibleEnemyArchonSymmetry[0] = isPossibleEnemyArchonSymmetry[1] = isPossibleEnemyArchonSymmetry[2] = true;
	}

}
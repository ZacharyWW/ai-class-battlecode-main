package strat1;

import static strat1.RobotPlayer.rc;
import static strat1.util.Functions.getBits;

// Import helper classes
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import strat1.helpers.BuildingHelper;
import strat1.helpers.MovementHelper;
import strat1.util.Functions;

// Builder droid used to heal buildings and build new ones.
public strictfp class Builder {
    // Struct containing information about a Robot.
    // It includes the created location and the type of the robot.
    public static class BuildInfo {
        public MapLocation location;
		public RobotType type;
		public BuildInfo(RobotType t, MapLocation loc) {
			type = t;
			location = loc;
		}
    }

    // Struct containing information about the builder's Archon Location.
    private static MapLocation creatorArchonLocation;
	private static int creatorArchonIndex;
	public static Direction currentDirection;
	private static BuildInfo nextBuilding;

    // Run code.
    // This is the main method that is called by RobotPlayer.
    public static void run() throws GameActionException {
        // credit: dernosmirc on github
        // Repair the building if possible.
		if (rc.isActionReady()) {
			MapLocation repair = BuildingHelper.getRepairLocation();
			if (repair != null && rc.canRepair(repair)) {
				rc.repair(repair);
			} else {
				if (nextBuilding != null) {
					Direction buildDirection = rc.getLocation().directionTo(nextBuilding.location);
					if (
							rc.getLocation().isWithinDistanceSquared(nextBuilding.location, 2) &&
									rc.canBuildRobot(nextBuilding.type, buildDirection)
					) {
						rc.buildRobot(nextBuilding.type, buildDirection);
					}
				}
			}
		}

        // Move the builder if possible.
        // The goal is to move the builder away from the Archon.
		if (rc.isMovementReady()) {
			Direction direction = BuildingHelper.getAntiArchonDirection(creatorArchonLocation);
			if (direction != null) {
				MovementHelper.tryMove(direction, false);
			} else {
				direction = BuildingHelper.getRepairDirection();
				if (direction != null) {
					MovementHelper.tryMove(direction, true);
				} else {
					direction = BuildingHelper.getPerpendicular(creatorArchonLocation);
					if (direction != null) {
						MovementHelper.tryMove(direction, false);
					}
				}
			}
		}
	}

    // Initialize builder. Called after a new builder is created.
    public static void init() throws GameActionException {
		int archonCount = 0;
		for (int i = 32; i < 36; ++i) {
			int value = rc.readSharedArray(i);
			if (getBits(value, 15, 15) == 1) {
				++archonCount;
				MapLocation archonLocation = new MapLocation(getBits(value, 6, 11), getBits(value, 0, 5));
				if (rc.getLocation().distanceSquaredTo(archonLocation) <= 2) {
					creatorArchonLocation = new MapLocation(archonLocation.x, archonLocation.y);
					creatorArchonIndex = i - 32;
					currentDirection = creatorArchonLocation.directionTo(rc.getLocation());
					nextBuilding = new BuildInfo(
							RobotType.WATCHTOWER,
							Functions.translate(creatorArchonLocation, currentDirection, 2)
					);
				}

			} else {
				break;
			}
		}
	}


}
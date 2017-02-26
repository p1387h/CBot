package unitControlModule.stateFactories.updater;

import java.util.ArrayList;
import java.util.List;

import bwapi.Position;
import bwapi.TilePosition;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Region;
import core.Core;
import unitControlModule.stateFactories.actions.SimpleUnitAvailableActions;
import unitControlModule.stateFactories.actions.executableActions.AttackMoveAction;
import unitControlModule.stateFactories.actions.executableActions.AttackUnitAction;
import unitControlModule.stateFactories.actions.executableActions.RetreatFromNearestUnitAction;
import unitControlModule.stateFactories.actions.executableActions.ScoutBaseLocationAction;
import unitControlModule.unitWrappers.PlayerUnit;
import unitTrackerModule.EnemyUnit;
import unitTrackerModule.UnitTrackerModule;

/**
 * SimpleActionUpdater.java --- Updater for updating a
 * {@link SimpleUnitAvailableActions} instance.
 * 
 * @author P H - 26.02.2017
 *
 */
public class SimpleActionUpdater extends GeneralActionUpdater {

	public SimpleActionUpdater(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	public void update(PlayerUnit playerUnit) {
		if(this.playerUnit.currentState == PlayerUnit.UnitStates.ENEMY_MISSING) {
			this.baselocationScoutingConfiguration();
		} else {
			this.attackMoveToNearestKnownUnitConfiguration();
			
			((AttackUnitAction) this.getActionFromInstance(AttackUnitAction.class)).setTarget(this.playerUnit.nearestEnemyUnitInConfidenceRange);
			((RetreatFromNearestUnitAction) this.getActionFromInstance(RetreatFromNearestUnitAction.class)).setTarget(this.playerUnit.nearestEnemyUnitInConfidenceRange);
		}
		
	}

	/**
	 * Function for the unit to configure its AttackMoveAction to the nearest
	 * enemy Unit (can be a building).
	 */
	protected void attackMoveToNearestKnownUnitConfiguration() {
		TilePosition closestUnitTilePosition = null;

		List<EnemyUnit> enemyUnits = new ArrayList<EnemyUnit>(UnitTrackerModule.getInstance().enemyUnits);
		enemyUnits.addAll(UnitTrackerModule.getInstance().enemyBuildings);

		// Find the closest unit of the known ones
		for (EnemyUnit unit : enemyUnits) {
			if (closestUnitTilePosition == null || this.playerUnit.getUnit()
					.getDistance(unit.getLastSeenTilePosition().toPosition()) < this.playerUnit.getUnit()
							.getDistance(closestUnitTilePosition.toPosition())) {
				closestUnitTilePosition = unit.getLastSeenTilePosition();
			}
		}
		((AttackMoveAction) this.getActionFromInstance(AttackMoveAction.class)).setTarget(closestUnitTilePosition);
	}
	
	
	/**
	 * If no enemy buildings are are being known of the Bot has to search for
	 * them. Find the closest BaseLocation with a timeStamp:
	 * <p>
	 * currentTime - timeStamp >= timePassed
	 */
	protected void baselocationScoutingConfiguration() {
		Position closestReachableBasePosition = null;

		for (BaseLocation location : BWTA.getBaseLocations()) {
			Region baseRegion = ((BaseLocation) location).getRegion();

			if (PlayerUnit.BASELOCATIONS_SEARCHED.get(location) != null && this.playerUnit.getUnit().hasPath(baseRegion.getCenter())) {
				if ((closestReachableBasePosition == null && Core.getInstance().getGame().elapsedTime()
						- PlayerUnit.BASELOCATIONS_SEARCHED.get(location) >= PlayerUnit.BASELOCATIONS_TIME_PASSED)
						|| (Core.getInstance().getGame().elapsedTime()
								- PlayerUnit.BASELOCATIONS_SEARCHED.get(location) >= PlayerUnit.BASELOCATIONS_TIME_PASSED
								&& this.playerUnit.getUnit().getDistance(location) < this.playerUnit.getUnit()
										.getDistance(closestReachableBasePosition))) {
					closestReachableBasePosition = baseRegion.getCenter();
				}
			}
		}
		
		((ScoutBaseLocationAction) this.getActionFromInstance(ScoutBaseLocationAction.class)).setTarget(closestReachableBasePosition);
	}
}

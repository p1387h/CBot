package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UnitType;

/**
 * ConstructActionTerran_ScienceFacilitiy.java --- Construction action for a
 * Terran_Science_Facility Unit.
 * 
 * @author P H - 22.09.2017
 *
 */
public class ConstructActionTerran_ScienceFacilitiy extends ConstructBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public ConstructActionTerran_ScienceFacilitiy(Object target) {
		super(target);

		this.addToGameStates(GameState.Building_Units);
		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Gas_Units);
		this.addToGameStates(GameState.Expensive_Units);
		this.addToGameStates(GameState.Flying_Units);
		this.addToGameStates(GameState.Support_Units);
		this.addToGameStates(GameState.Technology_Focused);
		this.addToGameStates(GameState.Upgrade_Focused);
		
		this.addToGameStates(GameState.ResearchFlyingUnits);
		this.addToGameStates(GameState.UpgradeFlyingUnits);
		
		this.addToGameStates(GameState.SpecifiBuilding_Terran_Science_Facility);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return UnitType.Terran_Science_Facility;
	}

	@Override
	public int defineMaxSimulationOccurrences() {
		return 1;
	}

}
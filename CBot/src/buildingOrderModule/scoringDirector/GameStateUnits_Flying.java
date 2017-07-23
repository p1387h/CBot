package buildingOrderModule.scoringDirector;

import buildingOrderModule.buildActionManagers.BuildActionManager;

// TODO: UML ADD NOT PUBLIC
/**
 * GameStateUnits_Flying.java --- A GameState focused on flying Units.
 * 
 * @author P H - 16.07.2017
 *
 */
class GameStateUnits_Flying extends GameState {

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		
		// TODO: WIP REMOVE
		System.out.println("GameState FlyingUnits: " + scoringDirector.defineFixedScoreUnitsFlying());
		
		return scoringDirector.defineFixedScoreUnitsFlying();
	}
	
}

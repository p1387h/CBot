package buildingOrderModule.scoringDirector;

// TODO: UML ADD NOT PUBLIC
/**
 * GameStateUnits_Building.java --- A GameState focused on buildings.
 * 
 * @author P H - 16.07.2017
 *
 */
class GameStateUnits_Building extends GameState {

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, GameStateCurrentInformation currenInformation) {
		double score = scoringDirector.defineDesiredBuildingsPercent()
				- (currenInformation.getCurrentBuildingsPercent() - scoringDirector.defineDesiredBuildingsPercent());

		// TODO: WIP REMOVE
		System.out.println("GameState Buildings: " + score);

		return score;
	}

}
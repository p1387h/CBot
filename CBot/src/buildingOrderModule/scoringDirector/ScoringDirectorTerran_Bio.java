package buildingOrderModule.scoringDirector;

// TODO: UML ADD
/**
 * ScoringDirectorTerran_Bio.java --- A ScoringDirector whose goal is to score
 * Bio Units and the corresponding upgrades / technologies.
 * 
 * @author P H - 17.07.2017
 *
 */
public class ScoringDirectorTerran_Bio extends ScoringDirector {

	// -------------------- Functions

	@Override
	protected double defineDesiredBuildingsPercent() {
		return 0.33;
	}

	@Override
	protected double defineDesiredCombatUnitsPercent() {
		return 0.50;
	}

	@Override
	protected double defineFixedScoreUnitsBio() {
		return 1.0;
	}

	@Override
	protected double defineFixedScoreUnitsFlying() {
		return 0.;
	}

	@Override
	protected double defineFixedScoreUnitsHealer() {
		return 1.0;
	}

	@Override
	protected double defineFixedScoreUnitsSupport() {
		return 1.0;
	}
	
	// ------------------------------ Getter / Setter

}

package buildingOrderModule.scoringDirector;

// TODO: UML ADD NOT PUBLIC
/**
 * GameStateUnits_Expensive.java --- A GameState focused on training / building
 * expensive Units.
 * 
 * @author P H - 16.07.2017
 *
 */
class GameStateUnits_Expensive extends GameStateGradualChange {

	// The score of the starting iteration. Initialized with a low value since
	// the Bot should only produce expensive Units in the mid- / endgame.
	private static double ScoreStarting = 0.;
	// The rate at which the score will change. The rate is is then applied for
	// each X frames that passed since the last iteration.
	private static double Rate = 0.1;
	// The frames after the rate is applied.
	private static double FrameDiff = 2000;

	// The maximum score that can be returned.
	private double scoreMax = 0.5;

	public GameStateUnits_Expensive() {
		super(GameStateUnits_Expensive.ScoreStarting, GameStateUnits_Expensive.Rate,
				GameStateUnits_Expensive.FrameDiff);
	}

	// -------------------- Functions

	@Override
	protected boolean canIterationRateApply(double score) {
		return score < this.scoreMax;
	}

	// TODO: WIP REMOVE
	@Override
	protected double generateScore(ScoringDirector scoringDirector, GameStateCurrentInformation currenInformation) {
		double value = super.generateScore(scoringDirector, currenInformation);

		// TODO: WIP REMOVE
		System.out.println("GameState Expensive: " + value);

		return value;
	}

}
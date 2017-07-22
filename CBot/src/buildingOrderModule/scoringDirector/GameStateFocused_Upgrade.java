package buildingOrderModule.scoringDirector;

import java.util.HashMap;
import java.util.function.BiConsumer;

import bwapi.UpgradeType;

// TODO: UML ADD NOT PUBLIC
/**
 * GameStateFocused_Upgrade.java --- A GameState focused on upgrading Units.
 * 
 * @author P H - 16.07.2017
 *
 */
class GameStateFocused_Upgrade extends GameStateGradualChangeWithReset {

	// The starting score of this GameState.
	private static double ScoreStart = 0.;
	// The rate that is applied to the score in each iteration.
	private static double Rate = 0.1;
	// The frames after the rate is applied.
	private static double FrameDiff = 200;

	// The number of upgrades performed by the Bot.
	private int upgradeCountPrev = 0;

	public GameStateFocused_Upgrade() {
		super(ScoreStart, Rate, FrameDiff);
	}

	// -------------------- Functions

	@Override
	protected boolean shouldReset(ScoringDirector scoringDirector, GameStateCurrentInformation currenInformation) {
		int upgradeCountCurrent = this.extractTotalNumberOfUpgrades(currenInformation.getCurrentUpgrades());
		boolean reset = false;

		// Reset the score after the previously stored number of upgrades
		// does not match the current number of upgrades. This means that a
		// new upgrade has been performed and therefore the Bot does not
		// immediately need to do another one.
		if (upgradeCountCurrent != this.upgradeCountPrev) {
			this.upgradeCountPrev = upgradeCountCurrent;
			reset = true;
		}

		return reset;
	}

	// TODO: UML ADD
	/**
	 * Function for extracting the total number of Upgrades a HashMap contains.
	 * This function sums up the stored Integers and returns them.
	 * 
	 * @param hashMap
	 *            the HashMap whose Upgrades are being counted.
	 * @return the sum of all stored Integers of the provided HashMap.
	 */
	private int extractTotalNumberOfUpgrades(HashMap<UpgradeType, Integer> hashMap) {
		// Wrapper needed since the BiConsumer can only operate on final types.
		class CountWrapper {
			public int count = 0;
		}

		final CountWrapper counter = new CountWrapper();

		hashMap.forEach(new BiConsumer<UpgradeType, Integer>() {

			@Override
			public void accept(UpgradeType upgradeType, Integer integer) {
				counter.count += integer;
			}
		});

		return counter.count;
	}

	@Override
	protected boolean isTresholdReached(double score) {
		// The threshold is never reached. The rate is constantly applied to the
		// score and therefore the Bot is bound to upgrade things eventually.
		return false;
	}

	// TODO: WIP REMOVE
	@Override
	protected double generateScore(ScoringDirector scoringDirector, GameStateCurrentInformation currenInformation) {
		double value = super.generateScore(scoringDirector, currenInformation);

		// TODO: WIP REMOVE
		System.out.println("GameState UpgradeFocused: " + value);

		return value;
	}

}
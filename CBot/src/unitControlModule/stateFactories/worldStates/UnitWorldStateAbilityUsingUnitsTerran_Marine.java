package unitControlModule.stateFactories.worldStates;

import javaGOAP.GoapState;

/**
 * UnitWorldStateAbilityUsingUnitsTerran_Marine.java --- WorldState for a
 * Terran_Marine using its corresponding abilities.
 * 
 * @author P H - 24.06.2017
 *
 */
public class UnitWorldStateAbilityUsingUnitsTerran_Marine extends UnitWorldStateAbilityUsingUnits {

	public UnitWorldStateAbilityUsingUnitsTerran_Marine() {
		this.add(new GoapState(0, "isStimmed", false));
		this.add(new GoapState(0, "mayUseStimPack", false));
	}
}

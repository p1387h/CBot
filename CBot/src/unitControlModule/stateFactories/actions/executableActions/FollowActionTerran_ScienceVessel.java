package unitControlModule.stateFactories.actions.executableActions;

import javaGOAP.GoapState;
import unitControlModule.unitWrappers.PlayerUnitTerran_ScienceVessel;

// TODO: UML ADD
/**
 * FollowActionTerran_ScienceVessel.java --- A follow action for
 * {@link PlayerUnitTerran_ScienceVessel}s with which the Unit is able to follow
 * another one around. The Terran_Science_Vessel defines an order in which the
 * existing Units are checked for compatibility. The executing Unit then
 * performs this Action (= Follows the Unit) until it is in support range which
 * in this Class is defined as the support pixel range of the
 * {@link PlayerUnitTerran_ScienceVessel} Class.
 * 
 * @author P H - 23.09.2017
 *
 */
public class FollowActionTerran_ScienceVessel extends FollowAction {

	/**
	 * @param target
	 *            type: Unit
	 */
	public FollowActionTerran_ScienceVessel(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "isNearSupportableUnit", true));
		this.addPrecondition(new GoapState(0, "isNearSupportableUnit", false));
	}

	// -------------------- Functions

	@Override
	protected int defineDistanceToTarget() {
		return PlayerUnitTerran_ScienceVessel.getSupportPixelDistance();
	}

}
package unitControlModule.stateFactories.actions.executableActions.abilities;

import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

// TODO: UML ADD
/**
 * AbilityActionTechTargetNone.java --- Action for simply performing an ability
 * without any specific target.
 * 
 * @author P H - 23.06.2017
 *
 */
public abstract class AbilityActionTechTargetNone extends AbilityActionGeneralSuperclass {

	/**
	 * @param target
	 *            type: Null
	 */
	public AbilityActionTechTargetNone(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().useTech(this.ability);
	}
}
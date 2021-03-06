package unitControlModule.stateFactories.updater;

import javaGOAP.GoapAction;
import unitControlModule.stateFactories.actions.executableActions.GroupingAtPositionActionBaseEntrance;
import unitControlModule.stateFactories.actions.executableActions.RetreatActionInPreviousAdjacentRegion;
import unitControlModule.stateFactories.actions.executableActions.RetreatActionSteerInRetreatVectorDirection;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * GeneralActionUpdater.java --- Superclass for updating most AvailableActions.
 * 
 * @author P H - 26.02.2017
 *
 */
public abstract class ActionUpdaterGeneral implements Updater {

	private boolean initializationMissing = true;
	protected PlayerUnit playerUnit;

	private RetreatActionSteerInRetreatVectorDirection retreatActionSteerInRetreatVectorDirection;
	private RetreatActionInPreviousAdjacentRegion retreatActionInPreviousAdjacentRegion;
	private GroupingAtPositionActionBaseEntrance groupingAtPositionActionBaseEntrance;

	public ActionUpdaterGeneral(PlayerUnit playerUnit) {
		this.playerUnit = playerUnit;
	}

	// -------------------- Functions

	@Override
	public void update(PlayerUnit playerUnit) {
		// Get the references to all used actions.
		if (this.initializationMissing) {
			this.init();
			this.initializationMissing = false;
		}

		// Update the retreating action of the Unit. This is the default
		// retreating Action a Unit can perform. If another one is desired, this
		// function needs to be overwritten.
		if (this.playerUnit.currentState == PlayerUnit.UnitStates.ENEMY_KNOWN) {
			this.retreatActionSteerInRetreatVectorDirection.setTarget(this.playerUnit.getAttackingEnemyUnitToReactTo());
			this.retreatActionInPreviousAdjacentRegion.setTarget(this.playerUnit.getAttackingEnemyUnitToReactTo());
		}

		// Needed to prevent error messages.
		this.groupingAtPositionActionBaseEntrance.setTarget(new Object());
	}

	/**
	 * Function used for retrieving the references to the different actions.
	 * This function should be overwritten by subclasses to provide further
	 * functionalities.
	 */
	protected void init() {
		this.retreatActionSteerInRetreatVectorDirection = ((RetreatActionSteerInRetreatVectorDirection) this
				.getActionFromInstance(RetreatActionSteerInRetreatVectorDirection.class));
		this.retreatActionInPreviousAdjacentRegion = ((RetreatActionInPreviousAdjacentRegion) this
				.getActionFromInstance(RetreatActionInPreviousAdjacentRegion.class));
		this.groupingAtPositionActionBaseEntrance = ((GroupingAtPositionActionBaseEntrance) this
				.getActionFromInstance(GroupingAtPositionActionBaseEntrance.class));
	}

	/**
	 * Get the GoapAction from the availableActions HashSet that is an instance
	 * of the specific class.
	 * 
	 * @param instanceClass
	 *            the class of which an instance is being searched in the
	 *            availableActions HashSet.
	 * @return the action that is an instance of the given class.
	 */
	protected <T> GoapAction getActionFromInstance(Class<T> instanceClass) {
		GoapAction actionMatch = null;

		for (GoapAction action : this.playerUnit.getAvailableActions()) {
			if (instanceClass.isInstance(action) && instanceClass.equals(action.getClass())) {
				actionMatch = action;

				break;
			}
		}
		return actionMatch;
	}
}

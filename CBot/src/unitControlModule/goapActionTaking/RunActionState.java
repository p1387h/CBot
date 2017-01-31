package unitControlModule.goapActionTaking;

import java.util.Queue;

/**
 * RunActionState.java --- State on the FSM Stack
 * 
 * @author P H - 28.01.2017
 */
final class RunActionState implements IFSMState {

	private Queue<GoapAction> currentActions;
	private FSM fsm;

	/**
	 * @param fsm
	 *            the FSM on which all states are being stacked.
	 * @param currentActions
	 *            the Queue of actions to be taken in order to archive a goal.
	 */
	RunActionState(FSM fsm, Queue<GoapAction> currentActions) {
		this.fsm = fsm;
		this.currentActions = currentActions;
	}

	// -------------------- Functions

	/**
	 * Cycle trough all actions until an invalid one or the end of the Queue is
	 * reached. A false return type here causes the FSM to pop the state from
	 * its stack.
	 * 
	 * @see unitControlModule.goapActionTaking.IFSMState#runGoapAction(unitControlModule.goapActionTaking.GoapUnit)
	 */
	@Override
	public boolean runGoapAction(GoapUnit goapUnit) throws Exception {
		boolean workingOnQueue = false;

		if (this.currentActions.peek().isDone(goapUnit)) {
			this.currentActions.poll();
		}

		if (!this.currentActions.isEmpty()) {
			GoapAction currentAction = this.currentActions.peek();

			if (currentAction.target == null) {
				throw new Exception("Target is null!");
			} else if (currentAction.requiresInRange(goapUnit) && !currentAction.isInRange(goapUnit)) {
				this.fsm.pushStack(new MoveToState(currentAction));
			} else if (currentAction.checkProceduralPrecondition(goapUnit) && !currentAction.performAction(goapUnit)) {
				throw new Exception("Action not possible (ProceduralPrecondition)!");
			}

			workingOnQueue = true;
		}
		return workingOnQueue;
	}

	// ------------------------------ Getter / Setter

	Queue<GoapAction> getCurrentActions() {
		return this.currentActions;
	}
}

package unitControlModule.goapActionTaking;

import java.util.Queue;

interface PlanCreatedEventListener {
	public void onPlanCreated(Queue<GoapAction> plan);
}

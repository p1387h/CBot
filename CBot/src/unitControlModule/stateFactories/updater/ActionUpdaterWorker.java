package unitControlModule.stateFactories.updater;

import unitControlModule.stateFactories.actions.AvailableActionsWorker;
import unitControlModule.stateFactories.actions.executableActions.worker.MoveToNearestCenterAction;
import unitControlModule.stateFactories.actions.executableActions.worker.ScoutBaseLocationWorkerAction;
import unitControlModule.stateFactories.actions.executableActions.worker.UnloadGasAction;
import unitControlModule.stateFactories.actions.executableActions.worker.UnloadMineralsAction;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * ActionUpdaterWorker.java --- Updater for updating a
 * {@link AvailableActionsWorker} instance.
 * 
 * @author P H - 25.03.2017
 *
 */
public class ActionUpdaterWorker extends ActionUpdaterDefault {

	private UnloadMineralsAction unloadMineralsAction;
	private UnloadGasAction unloadGasAction;
	private ScoutBaseLocationWorkerAction scoutBaseLocationWorkerAction;
	private MoveToNearestCenterAction moveToNearestCenterAction;

	public ActionUpdaterWorker(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	public void update(PlayerUnit playerUnit) {
		super.update(playerUnit);

		// TODO: Possible Change: Only perform once.
		this.unloadMineralsAction.setTarget(new Object());
		this.unloadGasAction.setTarget(new Object());

		this.moveToNearestCenterAction.setTarget(this.playerUnit.getClosestCenter());
	}

	@Override
	protected void init() {
		super.init();

		this.unloadMineralsAction = ((UnloadMineralsAction) this.getActionFromInstance(UnloadMineralsAction.class));
		this.unloadGasAction = ((UnloadGasAction) this.getActionFromInstance(UnloadGasAction.class));

		this.scoutBaseLocationWorkerAction = ((ScoutBaseLocationWorkerAction) this
				.getActionFromInstance(ScoutBaseLocationWorkerAction.class));

		this.moveToNearestCenterAction = ((MoveToNearestCenterAction) this
				.getActionFromInstance(MoveToNearestCenterAction.class));
	}

	@Override
	protected void baselocationScoutingConfiguration() {
		this.scoutBaseLocationWorkerAction.setTarget(findClosestReachableBasePosition());
	}

}

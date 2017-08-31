package buildingOrderModule.stateFactories.updater;

import java.util.HashMap;
import java.util.HashSet;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoringDirector;
import buildingOrderModule.scoringDirector.ScoringDirectorTerran_Bio;
import buildingOrderModule.simulator.ActionType;
import buildingOrderModule.simulator.TypeWrapper;
import buildingOrderModule.stateFactories.actions.AvailableActionsSimulationQueueTerran;
import core.Core;
import javaGOAP.GoapAction;

/**
 * ActionUpdaterSimulationQueueTerran.java --- Updater for updating a
 * {@link AvailableActionsSimulationQueueTerran} instance.
 * 
 * @author P H - 14.07.2017
 *
 */
public class ActionUpdaterSimulationQueueTerran extends ActionUpdaterSimulationQueue {

	public ActionUpdaterSimulationQueueTerran(BuildActionManager buildActionManager) {
		super(buildActionManager);
	}

	// -------------------- Functions

	@Override
	protected HashSet<ActionType> generateAllAvailableActionTypes(BuildActionManager manager) {
		HashSet<ActionType> availableActionTypes = new HashSet<>();
		HashSet<GoapAction> availableActions = manager.getAvailableActions();
		Integer playerCenterCount = manager.getCurrentGameInformation().getCurrentUnitCounts()
				.get(Core.getInstance().getPlayer().getRace().getCenter());
		Integer playerRefineryCount = manager.getCurrentGameInformation().getCurrentUnitCounts()
				.get(Core.getInstance().getPlayer().getRace().getRefinery());

		// Get all the Types plus their amount that are currently produced and
		// inside the action Queue. Used to prevent i.e. building two centers.
		HashMap<TypeWrapper, Integer> usedActionTypes = this.extractAllProducedTypes();
		// Also extract the types that are currently inside the
		// InformationStorage Queues:
		HashMap<TypeWrapper, Integer> forwardedActionTypes = this.extractAllForwardedTypes();

		// Transform each available Action into a ActionType.
		for (GoapAction goapAction : availableActions) {
			try {
				ActionType actionType = (ActionType) goapAction;

				// Some ActionTypes required special treatment regarding the
				// adding towards the available ActionTypes HashSet.
				switch (actionType.defineResultType().toString()) {
				case "Terran_Command_Center":
					if (!usedActionTypes.containsKey(actionType.defineResultType())
							&& !forwardedActionTypes.containsKey(actionType.defineResultType())) {
						availableActionTypes.add(actionType);
					}
					break;
				case "Terran_Refinery":
					// Only allow the construction of refineries when the center
					// count is larger than the refinery count.
					if (!usedActionTypes.containsKey(actionType.defineResultType())
							&& !forwardedActionTypes.containsKey(actionType.defineResultType())
							&& (playerRefineryCount == null || (playerCenterCount != null && playerRefineryCount != null
									&& playerCenterCount > playerRefineryCount))) {
						availableActionTypes.add(actionType);
					}
					break;
				case "Tank_Siege_Mode":
					if (!usedActionTypes.containsKey(actionType.defineResultType())
							&& !forwardedActionTypes.containsKey(actionType.defineResultType())) {
						availableActionTypes.add(actionType);
					}
					break;
				// Only allow the construction of a single machine shop.
				case "Terran_Machine_Shop":
					if ((manager.getInformationStorage().getCurrentGameInformation().getCurrentUnitCounts()
							.get(actionType.defineResultType().getUnitType()) == null
							|| manager.getInformationStorage().getCurrentGameInformation().getCurrentUnitCounts()
									.get(actionType.defineResultType().getUnitType()).equals(0))
							&& !usedActionTypes.containsKey(actionType.defineResultType())
							&& !forwardedActionTypes.containsKey(actionType.defineResultType())) {
						availableActionTypes.add(actionType);
					}
					break;
				default:
					availableActionTypes.add(actionType);
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}

		return availableActionTypes;
	}

	@Override
	protected ScoringDirector defineScoringDirector() {
		return new ScoringDirectorTerran_Bio();
	}

}

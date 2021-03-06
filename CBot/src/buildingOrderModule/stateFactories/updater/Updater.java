package buildingOrderModule.stateFactories.updater;

import buildingOrderModule.buildActionManagers.BuildActionManager;

public interface Updater {
	/**
	 * General update function which gets called when the implementing classes
	 * should update their corresponding values / references.
	 * 
	 * @param manager
	 *            the BuildActionManager whose properties may change.
	 */
	public void update(BuildActionManager manager);
}

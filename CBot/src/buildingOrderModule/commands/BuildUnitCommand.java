package buildingOrderModule.commands;

import buildingOrderModule.BuildingOrderModule;
import bwapi.UnitType;
import core.Core;

/**
 * BuildBuildingCommand.java --- Superclass for all Commands that base
 * themselves on constructing a unit.
 * 
 * @author P H - 25.03.2017
 *
 */
public class BuildUnitCommand extends BuildCommand {
	
	private Integer pointTimerStart;
	
	public BuildUnitCommand(UnitType assignedUnit) {
		super(assignedUnit);
	}
	
	public BuildUnitCommand(UnitType assignedUnit, int timeWait) {
		super(assignedUnit, timeWait);
	}
	
	// -------------------- Functions
	
	@Override
	public void execute() {
		BuildingOrderModule.getInstance().buildUnit(this.assignedUnit);
	}

	@Override
	public boolean requirementMatched() {
		// Once the time gets checked the first time, set it to the elapsed time
		// to calculate the difference in later iterations
		if (this.pointTimerStart == null) {
			this.pointTimerStart = Core.getInstance().getGame().elapsedTime();
		}
		
		// If a certain of time has passed execute the command
		if (this.assignedValue <= Core.getInstance().getGame().elapsedTime() - this.pointTimerStart) {
			return true;
		} else {
			return false;
		}
	}

}

package buildingOrderModule;

import buildingOrderModule.commands.BuildBuildingCommandSupplyCurrent;
import buildingOrderModule.commands.BuildBuildingCommandWorkerCount;
import buildingOrderModule.commands.BuildUnitCommand;
import bwapi.UnitType;

/**
 * BuildingCommandManagerTestingPurpose.java --- A CommandManager for testing
 * the worker implementation in a test-environment.
 * 
 * @author P H - 25.03.2017
 *
 */
public class BuildingCommandManagerTestingPurpose extends BuildingCommandManager {

	public BuildingCommandManagerTestingPurpose() {
		this.addCommand(new BuildBuildingCommandWorkerCount(UnitType.Terran_Barracks, 11));
		for (int i = 0; i < 15; i++) {
			this.addCommand(new BuildUnitCommand(UnitType.Terran_Marine));
		}
		this.addCommand(new BuildBuildingCommandWorkerCount(UnitType.Terran_Barracks, 13));
		this.addCommand(new BuildBuildingCommandSupplyCurrent(UnitType.Terran_Refinery, 21));
		this.addCommand(new BuildBuildingCommandSupplyCurrent(UnitType.Terran_Command_Center, 24));
		this.addCommand(new BuildBuildingCommandSupplyCurrent(UnitType.Terran_Factory, 26));
		this.addCommand(new BuildBuildingCommandSupplyCurrent(UnitType.Terran_Factory, 28));
		for (int i = 0; i < 0; i++) {
			this.addCommand(new BuildUnitCommand(UnitType.Terran_Vulture));
		}
	}
}
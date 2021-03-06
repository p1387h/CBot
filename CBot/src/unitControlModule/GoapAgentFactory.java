package unitControlModule;

import bwapi.Unit;
import informationStorage.InformationStorage;
import javaGOAP.DefaultGoapAgent;
import javaGOAP.GoapAgent;
import unitControlModule.unitWrappers.PlayerUnitFactory;

/**
 * GoapAgentFactory.java --- Factory used to create GoapAgents based on the
 * provided UnitType.
 * 
 * @author P H - 26.02.2017
 *
 */
public class GoapAgentFactory {

	private PlayerUnitFactory playerUnitFactory;

	public GoapAgentFactory(InformationStorage informationStorage) {
		this.playerUnitFactory = new PlayerUnitFactory(informationStorage);
	}

	// -------------------- Functions

	/**
	 * Main function for creating a GoapAgent using a Unit and a general
	 * InformationStorage.
	 * 
	 * @param unit
	 *            the Unit that the GoapAgent is assigned to.
	 * @return a GoapAgent based on the provided information or null if the
	 *         UnitType of the provided Unit is not known.
	 * @throws UnknownUnitTypeException
	 *             Exception indicating that the provided Unit has a not
	 *             supported UnitType.
	 */
	public GoapAgent createAgent(Unit unit) throws UnknownUnitTypeException {
		GoapAgent agent = null;

		// TODO: Add more Classes
		switch (unit.getType().toString()) {
		case "Terran_Marine":
			agent = new DefaultGoapAgent(this.playerUnitFactory.createMarine(unit));
			break;
		case "Terran_Firebat":
			agent = new DefaultGoapAgent(this.playerUnitFactory.createFirebat(unit));
			break;
		case "Terran_Medic":
			agent = new DefaultGoapAgent(this.playerUnitFactory.createMedic(unit));
			break;
		case "Terran_Vulture":
			agent = new DefaultGoapAgent(this.playerUnitFactory.createVulture(unit));
			break;
		case "Terran_Siege_Tank_Tank_Mode":
			agent = new DefaultGoapAgent(this.playerUnitFactory.createSiegeTank(unit));
			break;
		case "Terran_Siege_Tank_Siege_Mode":
			agent = new DefaultGoapAgent(this.playerUnitFactory.createSiegeTankSiegeMode(unit));
			break;
		case "Terran_SCV":
			agent = new DefaultGoapAgent(this.playerUnitFactory.createSCV(unit));
			break;
		case "Terran_Wraith":
			agent = new DefaultGoapAgent(this.playerUnitFactory.createWraith(unit));
			break;
		case "Terran_Goliath":
			agent = new DefaultGoapAgent(this.playerUnitFactory.createGoliath(unit));
			break;
		case "Terran_Science_Vessel":
			agent = new DefaultGoapAgent(this.playerUnitFactory.createScienceVessel(unit));
			break;
		default:
			throw new UnknownUnitTypeException(unit.getType());
		}

		return agent;
	}
}

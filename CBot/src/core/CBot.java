package core;

import buildingOrderModule.BuildingOrderModule;
import bwapi.BWEventListener;
import bwapi.Game;
import bwapi.Mirror;
import bwapi.Player;
import bwapi.Position;
import bwapi.Unit;
import informationStorage.InformationStorage;
import unitControlModule.UnitControlModule;
import unitTrackerModule.UnitTrackerModule;

/**
 * CBot.java --- The bot-class itself of which an instance gets created. This
 * class receives all events from the BWAPI-Eventlistener.
 * 
 * @author P H - 18.03.2017
 *
 */
public class CBot implements BWEventListener {
	private static CBot instance;

	private Mirror mirror = new Mirror();
	private Game game;
	private boolean started = false;
	private boolean firstFrameOver = false;
	private boolean addedUnits = false;

	private UnitTrackerModule unitTrackerModule;
	private UnitControlModule unitControlModule;
	private BuildingOrderModule buildingOrderModule;

	// Information storage across multiple modules
	private InformationStorage informationStorage = new InformationStorage();
	
	private CBot() {

	}

	// -------------------- Functions

	/**
	 * Singleton function.
	 * 
	 * @return instance of the class.
	 */
	public static CBot getInstance() {
		if (instance == null) {
			instance = new CBot();
		}
		return instance;
	}

	/**
	 * Run the bot.
	 */
	public void run() {
		try {
			this.mirror.getModule().setEventListener(this);
			this.mirror.startGame();

			System.out.println("---RUN: success---");
		} catch (Exception e) {
			System.out.println("---RUN: failed---");
			e.printStackTrace();
		}
	}

	// -------------------- Eventlisteners

	// ------------------------------ BWEventlistener
	@Override
	public void onStart() {
		try {
			if (!Init.init(this.mirror)) {
				throw new Exception();
			}

			this.game = Core.getInstance().getGame();
			this.started = true;

			this.unitTrackerModule = new UnitTrackerModule(this.informationStorage);
			this.unitControlModule = new UnitControlModule(this.informationStorage);
			this.buildingOrderModule = new BuildingOrderModule(this.informationStorage);

			System.out.println("---STARTUP: success---");
		} catch (Exception e) {
			System.out.println("---STARTUP: failed---");
			e.printStackTrace();
		}
	}

	@Override
	public void onFrame() {
		try {
			if (!this.addedUnits && this.started) {
				System.out.println("Assigned Units:");

				// Add all known Units to the UnitControl
				for (Unit unit : this.game.self().getUnits()) {
					if (!unit.getType().isNeutral()) {
						this.unitControlModule.addToUnitControl(unit);
						System.out.println("  - " + unit.getType());
					}
				}

				this.addedUnits = true;
			}

			if (this.started) {
				Display.showGameInformation(this.game);
				Display.showUnits(game, this.game.self().getUnits());

				this.unitTrackerModule.update();
				this.buildingOrderModule.update();
				this.unitControlModule.update();
			}

			// Needed to prevent the API from adding the Units at the beginning
			// of the game via the onUnitComplete event, which causes it to add
			// one single Unit two times.
			if (!firstFrameOver) {
				firstFrameOver = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUnitCreate(Unit unit) {
		if (this.firstFrameOver && unit.getPlayer() == this.game.self()) {
			this.unitControlModule.addToBuildingsBeingCreated(unit);
		}
	}

	@Override
	public void onUnitComplete(Unit unit) {
		if (this.firstFrameOver && unit.getPlayer() == this.game.self()) {
			this.unitControlModule.addToUnitControl(unit);
		}
	}

	@Override
	public void onUnitDestroy(Unit unit) {
		if (unit.getPlayer() == this.game.self()) {
			this.unitControlModule.removeUnitFromUnitControl(unit);
		}
	}

	@Override
	public void onEnd(boolean arg0) {

	}

	@Override
	public void onNukeDetect(Position arg0) {

	}

	@Override
	public void onPlayerDropped(Player arg0) {

	}

	@Override
	public void onPlayerLeft(Player arg0) {

	}

	@Override
	public void onReceiveText(Player arg0, String arg1) {

	}

	@Override
	public void onSaveGame(String arg0) {

	}

	@Override
	public void onSendText(String arg0) {

	}

	@Override
	public void onUnitDiscover(Unit arg0) {

	}

	@Override
	public void onUnitEvade(Unit arg0) {

	}

	@Override
	public void onUnitHide(Unit arg0) {

	}

	@Override
	public void onUnitMorph(Unit unit) {
		// Constructing a refinery is a morphing action!
		if (this.firstFrameOver && unit.getPlayer() == this.game.self()
				&& unit.getType() == this.game.self().getRace().getRefinery()) {
			this.unitControlModule.addToBuildingsBeingCreated(unit);
		}
	}

	@Override
	public void onUnitRenegade(Unit arg0) {

	}

	@Override
	public void onUnitShow(Unit arg0) {

	}

	// -------------------- Getter / Setter

	public UnitTrackerModule getUnitTrackerModule() {
		return unitTrackerModule;
	}

	public UnitControlModule getUnitControlModule() {
		return unitControlModule;
	}

	public BuildingOrderModule getBuildingOrderModule() {
		return buildingOrderModule;
	}
	
	public InformationStorage getInformationStorage() {
		return informationStorage;
	}
}
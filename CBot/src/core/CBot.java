package core;

import java.util.ArrayList;
import java.util.List;

import buildingOrderModule.BuildingOrderModule;
import bwapi.BWEventListener;
import bwapi.Game;
import bwapi.Mirror;
import bwapi.Player;
import bwapi.Position;
import bwapi.Unit;
import bwta.BWTA;
import informationStorage.InformationStorage;
import unitControlModule.UnitControlModule;
import unitTrackerModule.UnitTrackerModule;
import workerManagerConstructionJobDistribution.WorkerManagerConstructionJobDistribution;
import workerManagerResourceSpotAllocation.WorkerManagerResourceSpotAllocation;

/**
 * CBot.java --- The Bot-class itself of which an instance gets created. This
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

	private WorkerManagerResourceSpotAllocation workerManagerResourceSpotAllocation;
	private WorkerManagerConstructionJobDistribution workerManagerConstructionJobDistribution;

	// Information storage across multiple modules.
	private InformationStorage informationStorage;

	// Threads that must be finished before shutting down the main Thread.
	private List<Thread> finishingThreads = new ArrayList<Thread>();

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
	 * Run the Bot.
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

	/**
	 * Function for adding a Thread to the pool of Threads that must be waited
	 * for when the game ends.
	 * 
	 * @param thread
	 *            the Thread that must be waited for.
	 */
	public void addToThreadFinishing(Thread thread) {
		this.finishingThreads.add(thread);
	}

	/**
	 * Function for removing a Thread from the pool of Threads that must be
	 * waited for when the game ends.
	 * 
	 * @param thread
	 *            the Thread that is going to be removed.
	 */
	public void removeFromThreadFinishing(Thread thread) {
		this.finishingThreads.remove(thread);
	}

	/**
	 * Function for waiting for all stored Threads to finish. Causes the main
	 * Thread to pause until all Threads are terminated.
	 */
	public void waitForAllThreads() {
		// Wait for all stored Threads to finish.
		for (Thread thread : this.finishingThreads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// -------------------- Eventlisteners

	// ------------------------------ BWEventlistener
	@Override
	public void onStart() {
		try {
			this.informationStorage = new InformationStorage();
			
			if (!Init.init(this.mirror, this.informationStorage)) {
				throw new Exception();
			}

			this.game = Core.getInstance().getGame();
			this.started = true;

			this.workerManagerResourceSpotAllocation = new WorkerManagerResourceSpotAllocation(this.informationStorage);
			this.workerManagerConstructionJobDistribution = new WorkerManagerConstructionJobDistribution(
					this.informationStorage);

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
				System.out.println("\nAssigned Units:");

				// Add all known Units to the UnitControl
				for (Unit unit : this.game.self().getUnits()) {
					if (!unit.getType().isNeutral()) {
						this.unitControlModule.addToUnitControl(unit);
						System.out.println("  - " + unit.getType());
					}
				}
				System.out.println("\n");

				this.addedUnits = true;
			}

			if (this.started) {
				Display.showGameInformation(this.informationStorage);
				Display.showUnits(this.game.self().getUnits());

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
			if (unit.getType().isBuilding()) {
				this.workerManagerConstructionJobDistribution.addToCurrentlyConstructedBuildings(unit);
			}
		}
	}

	@Override
	public void onUnitComplete(Unit unit) {
		if (this.firstFrameOver && unit.getPlayer() == this.game.self()) {
			this.unitControlModule.addToUnitControl(unit);

			// Add refineries to the manager as available gathering sources.
			if (unit.getType().isRefinery()) {
				this.workerManagerResourceSpotAllocation.addRefinery(unit);
			}
		}
	}

	@Override
	public void onUnitDestroy(Unit unit) {
		if (unit.getPlayer() == this.game.self()) {
			this.unitControlModule.removeUnitFromUnitControl(unit);

			// Remove the building from the construction manager. This is due to
			// it may be currently being in construction.
			if (unit.isBeingConstructed()) {
				this.workerManagerConstructionJobDistribution.removeBuilding(unit);
			}

			// Remove any destroyed refineries from the manager.
			if (unit.getType().isRefinery()) {
				this.workerManagerResourceSpotAllocation.removeRefinery(unit);
			}
		}
	}

	@Override
	public void onEnd(boolean isWinner) {
		this.waitForAllThreads();
		
		this.started = false;
		this.firstFrameOver = false;
		this.addedUnits = false;
		
		BWTA.cleanMemory();
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
			this.workerManagerConstructionJobDistribution.addToCurrentlyConstructedBuildings(unit);
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

	public WorkerManagerResourceSpotAllocation getWorkerManagerResourceSpotAllocation() {
		return workerManagerResourceSpotAllocation;
	}

	public WorkerManagerConstructionJobDistribution getWorkerManagerConstructionJobDistribution() {
		return workerManagerConstructionJobDistribution;
	}

	public InformationStorage getInformationStorage() {
		return informationStorage;
	}
}
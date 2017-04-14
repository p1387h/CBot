package unitControlModule.unitWrappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.BiConsumer;

import bwapi.Unit;
import bwapi.UnitType;
import unitControlModule.ResourceReserver;

/**
 * PlayerUnitWorker.java --- Wrapper for a general worker Unit.
 * 
 * @author P H - 29.03.2017
 *
 */
public abstract class PlayerUnitWorker extends PlayerUnit {

	protected static final int MAX_NUMBER_MINING = 2;
	protected static final int MAX_NUMBER_GATHERING_GAS = 0; // TODO: 3
	protected static final int PIXEL_GATHER_SEARCH_RADIUS = 350;
	protected static final int CONSTRUCTION_COUNTER_MAX = 5;

	// Mapped: gathering sources (Units) -> Units (worker)
	// Each gathering source holds the Units that are currently working on it.
	public static HashMap<Unit, ArrayList<Unit>> mappedAccessibleGatheringSources = new HashMap<Unit, ArrayList<Unit>>();
	// Used to prevent double mapping of the same gathering source in one cycle.
	public static HashMap<Unit, ArrayList<Unit>> mappedSourceContenders = new HashMap<Unit, ArrayList<Unit>>();
	public static Queue<UnitType> buildingQueue = new LinkedList<>();
	public static HashMap<Unit, UnitType> mappedBuildActions = new HashMap<>();
	public static HashSet<Unit> buildingsBeingCreated = new HashSet<Unit>();

	// Building related stuff
	protected boolean constructingFlag = false;
	protected int personalReservedMinerals = 0;
	protected int personalReservedGas = 0;

	public enum ConstructionState {
		IDLE, AWAIT_CONFIRMATION, CONFIRMED
	}

	protected ConstructionState currentConstructionState = ConstructionState.IDLE;
	protected int constructionCounter = 0;

	protected Unit closestFreeMineralField;
	protected Unit closestFreeGasSource;
	protected UnitType assignedBuildingType;

	public PlayerUnitWorker(Unit unit) {
		super(unit);
	}

	// -------------------- Functions

	/**
	 * Should be called at least one time from the sub class if overwritten. It
	 * is updating all necessary information regarding various tasks the worker
	 * can execute as well as shared information between all workers.
	 * 
	 * @see unitControlModule.unitWrappers.PlayerUnit#customUpdate()
	 */
	@Override
	protected void customUpdate() {
		this.updateMappedSourceContenders();
		this.updateConstructionState();
		this.updateCurrentActionInformation();
	}

	/**
	 * Function for removing any previously contended gathering spots by the
	 * worker. This is needed since the spots must be reassigned if the Unit for
	 * example starts constructing a building.
	 */
	protected void updateMappedSourceContenders() {
		if (mappedSourceContenders.containsKey(this.closestFreeMineralField)) {
			mappedSourceContenders.get(this.closestFreeMineralField).remove(this.unit);
		}
		if (mappedSourceContenders.containsKey(this.closestFreeGasSource)) {
			mappedSourceContenders.get(this.closestFreeGasSource).remove(this.unit);
		}
	}

	/**
	 * Function for updating the construction state of a worker Unit. This is
	 * needed for every worker that is / was currently constructing a building
	 * and therefore was assigned a building type and a specific amount of
	 * resources. These information need to be removed from the worker to
	 * prevent a clogging of resources and buildings. Also this function acts as
	 * a safety feature since it queues all construction jobs the Unit is / was
	 * not able to fulfill in a certain amount of time in the general
	 * construction queue again. This way all queued buildings actually get
	 * constructed.
	 */
	protected void updateConstructionState() {
		// Wait for the confirmation until either a limit is reached or the
		// confirmation was given
		if (this.currentConstructionState == ConstructionState.AWAIT_CONFIRMATION) {
			if (this.constructionCounter < CONSTRUCTION_COUNTER_MAX) {
				this.constructionCounter++;
			} else {
				this.constructionCounter = 0;
				this.currentConstructionState = ConstructionState.IDLE;

				this.resetAwaitedConstruction();
			}

			if (this.assignedBuildingType != null
					&& mappedBuildActions.getOrDefault(this.unit, null) == this.assignedBuildingType) {
				this.constructionCounter = 0;
				this.currentConstructionState = ConstructionState.CONFIRMED;
			}
		}
		// No "else if" since it will be executed in one cycle this way
		if (this.currentConstructionState == ConstructionState.CONFIRMED) {
			// Remove failed / finished construction jobs. No iteration counter
			// here, since
			// this functionality would be overridden by the
			// ActionUpdaterWorker.
			// -> Safety feature, so that no Unit holds a order and does not
			// execute
			// it because as soon as a building location is occupied, the
			// building gets added back into the building queue.
			if (this.assignedBuildingType != null && mappedBuildActions.getOrDefault(this.unit, null) == null) {
				this.currentConstructionState = ConstructionState.IDLE;

				this.resetAwaitedConstruction();
			}
		}
	}

	/**
	 * Function for updating all information regarding possible work the worker
	 * can do. This is either the assigning of a building for construction or a
	 * gathering source for either minerals or gas.
	 */
	protected void updateCurrentActionInformation() {
		// Get a building from the building Queue and reset actions if possible
		if (!this.unit.isGatheringGas() && !PlayerUnitWorker.buildingQueue.isEmpty()
				&& ResourceReserver.canAffordConstruction(PlayerUnitWorker.buildingQueue.peek())
				&& this.currentConstructionState == ConstructionState.IDLE) {
			// Reset first or the assigned building type will be removed!
			this.resetActions();
			this.assignedBuildingType = PlayerUnitWorker.buildingQueue.poll();

			// Reserve the resources for the construction.
			ResourceReserver.reserveMinerals(this.assignedBuildingType.mineralPrice());
			ResourceReserver.reserveGas(this.assignedBuildingType.gasPrice());
			this.personalReservedMinerals = this.assignedBuildingType.mineralPrice();
			this.personalReservedGas = this.assignedBuildingType.gasPrice();

			// Await the confirmation of the construction (by mapping the Unit
			// to a UnitType)
			this.currentConstructionState = ConstructionState.AWAIT_CONFIRMATION;
		}
		// Find a gathering source.
		else {
			final Unit mappedUnit = this.unit;
			final HashSet<Unit> mappedSource = new HashSet<>();

			// Get all assigned gathering source(s) for this Unit.
			mappedAccessibleGatheringSources.forEach(new BiConsumer<Unit, ArrayList<Unit>>() {
				@Override
				public void accept(Unit unit, ArrayList<Unit> set) {
					if (set.contains(mappedUnit)) {
						mappedSource.add(unit);
					}
				}
			});

			if (mappedSource.isEmpty()) {
				this.markContenders();
			}
		}
	}

	/**
	 * Function for resetting everything assigned for a construction of a
	 * building. If the construction flag was not set, the UnitType is queued
	 * again since the building was not constructed / did not start being
	 * constructed.
	 */
	protected void resetAwaitedConstruction() {
		// Flag is not set = construction has not started
		if (!this.constructingFlag) {
			buildingQueue.add(this.assignedBuildingType);

			// TODO: REMOVE extra Information
			System.out.println("Queued again: " + this.unit + " " + this.assignedBuildingType);
		} else {
			this.constructingFlag = false;
		}

		// TODO: Needed Change: Reset minerals as soon as the construction
		// starts to prevent a resource lock
		// Reset any reserved resources
		ResourceReserver.freeMinerals(this.assignedBuildingType.mineralPrice());
		ResourceReserver.freeGas(this.assignedBuildingType.gasPrice());
		this.personalReservedMinerals = 0;
		this.personalReservedGas = 0;

		this.assignedBuildingType = null;
	}

	/**
	 * Mark a gathering source as contender so that no other worker can set it
	 * as their closest free gathering source. This can only be done if the Unit
	 * is currently not mapped to an actual accessible gathering source.
	 */
	protected void markContenders() {
		Unit mineralField = this.findClosestFreeMineralField();
		Unit gasSource = this.findClosestFreeGasSource();

		// Create new entries if necessary.
		if (!mappedSourceContenders.containsKey(mineralField)) {
			mappedSourceContenders.put(mineralField, new ArrayList<Unit>());
		}
		if (!mappedSourceContenders.containsKey(gasSource)) {
			mappedSourceContenders.put(gasSource, new ArrayList<Unit>());
		}
		if (!mappedAccessibleGatheringSources.containsKey(mineralField)) {
			mappedAccessibleGatheringSources.put(mineralField, new ArrayList<Unit>());
		}
		if (!mappedAccessibleGatheringSources.containsKey(gasSource)) {
			mappedAccessibleGatheringSources.put(gasSource, new ArrayList<Unit>());
		}

		// If a space for gathering a resource is free, set this Unit as a
		// contender for the spot. Contended sources are assigned to a Unit
		// while the action is executed so that, if the found source has no
		// free spot, a new one will be eventually found in one of the next
		// iterations.
		if (mappedSourceContenders.get(mineralField).size()
				+ mappedAccessibleGatheringSources.get(mineralField).size() < MAX_NUMBER_MINING) {
			mappedSourceContenders.get(mineralField).add(this.unit);
			this.closestFreeMineralField = mineralField;
		}
		if (mappedSourceContenders.get(gasSource).size()
				+ mappedAccessibleGatheringSources.get(gasSource).size() < MAX_NUMBER_GATHERING_GAS) {
			mappedSourceContenders.get(gasSource).add(this.unit);
			this.closestFreeGasSource = gasSource;
		}
	}

	/**
	 * Function for finding the closest free mineral field.
	 * 
	 * @return the closest free mineral field.
	 */
	protected Unit findClosestFreeMineralField() {
		Unit closestFreeMineralField = null;

		// Get all mineral fields
		for (Unit gatheringSource : this.getUnit().getUnitsInRadius(PIXEL_GATHER_SEARCH_RADIUS)) {
			if (gatheringSource.getType().isMineralField()) {
				closestFreeMineralField = this.checkAgainstMappedAccessibleSources(gatheringSource,
						closestFreeMineralField, MAX_NUMBER_MINING);
			}
		}
		return closestFreeMineralField;
	}

	/**
	 * Function for finding the closest free gas source.
	 * 
	 * @return the closest free gas source.
	 */
	protected Unit findClosestFreeGasSource() {
		Unit closestRefinery = null;

		// Get all vaspene geysers
		for (Unit gatheringSource : this.getUnit().getUnitsInRadius(PIXEL_GATHER_SEARCH_RADIUS)) {
			if (gatheringSource.getType().isRefinery()) {
				closestRefinery = this.checkAgainstMappedAccessibleSources(gatheringSource, closestRefinery,
						MAX_NUMBER_GATHERING_GAS);
			}
		}
		return closestRefinery;
	}

	/**
	 * Function for checking if a Unit can be mapped to a gathering source.
	 * Conditions are that the amount of Units already gathering there has to be
	 * less than a set threshold as well as the check against an already
	 * existing reference Unit, which is a gathering source of the same type
	 * with a set distance to the PlayerUnitWorker (Unit can be null => first
	 * other gathering source will be set). If no entry for the source is found,
	 * a new one is generated.
	 * 
	 * @param gatheringSource
	 *            the gathering source.
	 * @param referenceUnit
	 *            the currently chosen closest gathering source.
	 * @param workerThreshold
	 *            the threshold of workers, that can work at the specific
	 *            gathering source.
	 * @return the reference Unit if the distance to the new gathering source is
	 *         greater than the reference value or the threshold is reached. Or
	 *         the gathering source, if the threshold is not yet reached and the
	 *         distance is smaller than the distance towards the reference Unit.
	 */
	protected Unit checkAgainstMappedAccessibleSources(Unit gatheringSource, Unit referenceUnit, int workerThreshold) {
		// Create a new entry in the map if no other entry for the gathering
		// source is found.
		if (!mappedAccessibleGatheringSources.containsKey(gatheringSource)) {
			mappedAccessibleGatheringSources.put(gatheringSource, new ArrayList<Unit>());
		}

		ArrayList<Unit> mappedUnits = mappedAccessibleGatheringSources.get(gatheringSource);

		// If the threshold is not reached, the Unit can gather there.
		if (mappedUnits.size() < workerThreshold && (referenceUnit == null
				|| this.unit.getDistance(gatheringSource) < this.unit.getDistance(referenceUnit))) {
			return gatheringSource;
		}
		return referenceUnit;
	}

	// ------------------------------ Getter / Setter

	public Unit getClosestFreeMineralField() {
		return closestFreeMineralField;
	}

	public Unit getClosestFreeGasSource() {
		return closestFreeGasSource;
	}

	public UnitType getAssignedBuildingType() {
		return assignedBuildingType;
	}

	public void setConstructingFlag() {
		this.constructingFlag = true;
	}

	public int getPersonalReservedMinerals() {
		return personalReservedMinerals;
	}

	public int getPersonalReservedGas() {
		return personalReservedGas;
	}

	public ConstructionState getCurrentConstructionState() {
		return currentConstructionState;
	}
}

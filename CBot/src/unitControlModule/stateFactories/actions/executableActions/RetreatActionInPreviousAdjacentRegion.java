package unitControlModule.stateFactories.actions.executableActions;

import bwapi.Color;
import bwapi.Position;
import bwapiMath.Point;
import bwapiMath.Vector;
import bwta.BWTA;
import bwta.Chokepoint;
import bwta.Region;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

// TODO: UML ADD
/**
 * RetreatActionInPreviousAdjacentRegion.java --- A retreat Action with which a
 * Unit can easily move between two different Regions and retreat to the one
 * that leads towards the Player's starting location. All this is based on the
 * Unit's distance towards the ChokePoints on the map. The Action is only
 * enabled if the Unit gets close to (The right) one.
 * 
 * @author P H - 27.08.2017
 *
 */
public class RetreatActionInPreviousAdjacentRegion extends RetreatActionGeneralSuperclass {

	// The range to a ChokePoint leading towards the starting location at which
	// the Action can be performed.
	private int acceptableChokePointRange = 320;
	private Position prevRegionRetreatPosition = null;

	/**
	 * @param target
	 *            type: Unit
	 */
	public RetreatActionInPreviousAdjacentRegion(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected Position generateTempRetreatPosition(IGoapUnit goapUnit) {
		return this.prevRegionRetreatPosition;
	}

	@Override
	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
		boolean success = false;

		// The ranges were not checked in any previous iteration.
		if (this.prevRegionRetreatPosition == null) {
			// Find the current Region and the one that the Unit must retreat to
			// in order to get to the starting location.
			Region curRegion = BWTA.getRegion(((PlayerUnit) goapUnit).getUnit().getPosition());
			Region prevRegion = ((PlayerUnit) goapUnit).getInformationStorage().getMapInfo()
					.getReversedRegionAccessOrder().get(curRegion);

			// The Unit might be already in the starting Region (No previous
			// Region!).
			if (prevRegion != null) {
				// Check the distance to each ChokePoint. If the distance is in
				// the acceptable range, the Action can be performed.
				for (Chokepoint chokepoint : prevRegion.getChokepoints()) {
					if (((PlayerUnit) goapUnit).getUnit()
							.getDistance(chokepoint.getCenter()) <= this.acceptableChokePointRange) {
						this.prevRegionRetreatPosition = prevRegion.getCenter();
						success = true;

						break;
					}
				}
			}
		}
		// the previous Region's center was found.
		else {
			success = true;

			// TODO: WIP POSITION DEBUG INFO
			// Display the retreat Position as one big (Red) circle.
			(new Point(this.prevRegionRetreatPosition)).display(25, new Color(255, 0, 0), true);
			(new Vector(((PlayerUnit) goapUnit).getUnit().getPosition(), this.prevRegionRetreatPosition)).display();
		}

		return success;
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		// Arbitrary cost. Can be chosen as desired. Only condition is, that it
		// must be smaller than the Vector retreat Action's cost since it only
		// then gets used when the Unit is near a possible retreat ChokePoint.
		return 10;
	}

	// TODO: UML ADD
	@Override
	protected void resetSpecific() {
		super.resetSpecific();

		this.prevRegionRetreatPosition = null;
	}

	// ------------------------------ Getter / Setter

}
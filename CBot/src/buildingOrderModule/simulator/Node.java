package buildingOrderModule.simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bwapi.Pair;

/**
 * Node.java --- Class that is being used to represent a single Element in the
 * tree that is being generated by the Simulator.
 * 
 * @author P H - 05.07.2017
 *
 */

public class Node implements Comparable<Node> {

	private HashMap<TypeWrapper, Integer> typesFree = new HashMap<>();
	// TODO: UML CHANGE TYPE
	private HashMap<TypeWrapper, List<Pair<TypeWrapper, Integer>>> typesWorking = new HashMap<>();
	private HashMap<ActionType, Integer> actionTypeOccurrences = new HashMap<>();
	// TODO: UML CHANGE TYPE
	private List<ActionType> chosenActions = new ArrayList<>();
	private int currentMinerals = 0;
	private int currentGas = 0;
	private int frameTimeStamp = -1;
	private Node previousNode = null;
	private int score = 0;

	// Influence of minerals and gas on the score:
	private static double InfluenceMinerals = 0.9;
	private static double InfluenceGas = 0.9;

	public Node() {

	}

	// -------------------- Functions

	/**
	 * Function for summing up the score of all ActionTypes that this Node is
	 * holding.
	 * 
	 * @return the sum of all the scores of all ActionTypes the Node is
	 *         currently storing.
	 */
	public int generateScoreOfActions() {
		int totalScore = 0;

		for (ActionType actionType : this.chosenActions) {
			totalScore += actionType.defineScore();
		}
		return totalScore;
	}

	@Override
	public int compareTo(Node node) {
		int totalScoreNodeOne = generateSingleNodeScore(this);
		int totalScoreNodeTwo = generateSingleNodeScore(node);

		// Highest score = index 0.
		// => Sorted: Descending.
		return -1 * (Integer.compare(totalScoreNodeOne, totalScoreNodeTwo));
	}

	// TODO: UML ADD IF MISSING
	/**
	 * Function for generating the score of a {@link Node} instance based on the
	 * current gas and minerals.
	 * 
	 * @param node
	 *            the {@link Node} whose score is going to be generated.
	 * @return the score of the provided {@link Node} based on it's gas and
	 *         minerals.
	 */
	private static int generateSingleNodeScore(Node node) {
		// Take the stored influences and apply them to the mineral and gas
		// counts.
		return node.getScore()
				+ (int) (node.getCurrentMinerals() * InfluenceMinerals + node.getCurrentGas() * InfluenceGas);
	}

	/**
	 * Function for resetting all values stored in the instance.
	 */
	public void reset() {
		this.typesFree = new HashMap<>();
		this.typesWorking = new HashMap<>();
		this.actionTypeOccurrences = new HashMap<>();
		this.chosenActions = new ArrayList<>();
		this.currentMinerals = 0;
		this.currentGas = 0;
		this.frameTimeStamp = -1;
		this.previousNode = null;
		this.score = 0;
	}

	// ------------------------------ Getter / Setter

	public HashMap<TypeWrapper, Integer> getTypesFree() {
		return typesFree;
	}

	public void setTypesFree(HashMap<TypeWrapper, Integer> unitsFree) {
		this.typesFree = unitsFree;
	}

	// TODO: UML CHANGE TYPE
	public HashMap<TypeWrapper, List<Pair<TypeWrapper, Integer>>> getTypesWorking() {
		return typesWorking;
	}

	// TODO: UML CHANGE PARAMS
	public void setTypesWorking(HashMap<TypeWrapper, List<Pair<TypeWrapper, Integer>>> unitsWorking) {
		this.typesWorking = unitsWorking;
	}

	public HashMap<ActionType, Integer> getActionTypeOccurrences() {
		return actionTypeOccurrences;
	}

	public void setActionTypeOccurrences(HashMap<ActionType, Integer> actionTypeOccurrences) {
		this.actionTypeOccurrences = actionTypeOccurrences;
	}

	// TODO: UML CHANGE TYPE
	public List<ActionType> getChosenActions() {
		return chosenActions;
	}

	// TODO: UML CHANGE PARAMS
	public void setChosenActions(List<ActionType> chosenActions) {
		this.chosenActions = chosenActions;
	}

	public int getCurrentMinerals() {
		return currentMinerals;
	}

	public void setCurrentMinerals(int currentMinerals) {
		this.currentMinerals = currentMinerals;
	}

	public int getCurrentGas() {
		return currentGas;
	}

	public void setCurrentGas(int currentGas) {
		this.currentGas = currentGas;
	}

	public int getFrameTimeStamp() {
		return frameTimeStamp;
	}

	public void setFrameTimeStamp(int frameTimeStamp) {
		this.frameTimeStamp = frameTimeStamp;
	}

	public Node getPreviousNode() {
		return previousNode;
	}

	public void setPreviousNode(Node previousNode) {
		this.previousNode = previousNode;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public double getInfluenceMinerals() {
		return InfluenceMinerals;
	}

	public void setInfluenceMinerals(double influenceMinerals) {
		Node.InfluenceMinerals = influenceMinerals;
	}

	public double getInfluenceGas() {
		return InfluenceGas;
	}

	public void setInfluenceGas(double influenceGas) {
		Node.InfluenceGas = influenceGas;
	}
}

package bwapiMath;

import bwapi.Color;
import bwapi.Game;
import bwapi.Position;
import core.Core;

/**
 * Vector.java --- Class used for Vector operations.
 * 
 * @author P H - 25.02.2017
 *
 */
public class Vector extends Point {

	private static final int DEFAULT_ARROW_LENGTH = 5;
	private static final int DEFAULT_ARROW_ANGLE_DEG = 30;
	private static final Color DEFAULT_ARROW_COLOR = new Color(255, 255, 255);
	private static final boolean DEFAULT_ARROW_DIRECTION_SHOWN = true;

	private double dirX = 0., dirY = 0.;
	private double length = 0.;

	public Vector(Point start, Position end) {
		this(start, new Point(end));
	}

	public Vector(Position start, Point end) {
		this(new Point(start), end);
	}

	public Vector(Position start, Position end) {
		this(new Point(start), new Point(end));
	}

	public Vector(Point start, Point end) {
		this(start.getX(), start.getY(), end.getX() - start.getX(), end.getY() - start.getY());
	}

	public Vector(int x, int y, double dirX, double dirY) {
		super(x, y, Point.Type.NONE);

		this.dirX = dirX;
		this.dirY = dirY;
		this.updateLength();
	}

	// -------------------- Functions

	@Override
	public Vector clone() {
		return new Vector(this.x, this.y, this.dirX, this.dirY);
	}

	@Override
	public String toString() {
		return "[" + this.x + " + " + this.dirX + ", " + this.y + " + " + this.dirY + "]";
	}

	/**
	 * @see #rotateLeftRAD(double)
	 * @param alpha
	 *            the degree at which the Vector is rotated.
	 */
	public void rotateLeftDEG(double alpha) {
		this.rotateLeftRAD(Math.toRadians(alpha));
	}

	/**
	 * Function for rotating a Vector left.
	 * 
	 * @param alpha
	 *            the radiant at which the Vector is rotated.
	 */
	public void rotateLeftRAD(double alpha) {
		double newDirX = this.dirX * Math.cos(alpha) + this.dirY * Math.sin(alpha);
		double newDirY = -1. * this.dirX * Math.sin(alpha) + this.dirY * Math.cos(alpha);

		this.dirX = newDirX;
		this.dirY = newDirY;
		this.updateLength();
	}

	/**
	 * @see #rotateRightRAD(double)
	 * @param alpha
	 *            the degree at which the Vector is rotated.
	 */
	public void rotateRightDEG(double alpha) {
		this.rotateRightRAD(Math.toRadians(alpha));
	}

	/**
	 * Function for rotating a Vector right.
	 * 
	 * @param alpha
	 *            the radiant at which the Vector is rotated.
	 */
	public void rotateRightRAD(double alpha) {
		double newDirX = this.dirX * Math.cos(alpha) - this.dirY * Math.sin(alpha);
		double newDirY = this.dirX * Math.sin(alpha) + this.dirY * Math.cos(alpha);

		this.dirX = newDirX;
		this.dirY = newDirY;
		this.updateLength();
	}

	/**
	 * Function for retrieving the length of the Vector.
	 * 
	 * @return the length of the Vector.
	 */
	public double length() {
		return this.length;
	}

	/**
	 * Function for updating the stored length of the Vector. This is necessary
	 * since calculating it over and over again would create unnecessary cpu
	 * work.
	 */
	private void updateLength() {
		this.length = Math.sqrt(Math.pow(this.dirX, 2) + Math.pow(this.dirY, 2));
	}

	/**
	 * Function for generating the cross product between this vector and another
	 * given one.
	 * 
	 * @param vectorB
	 *            another Vector with which the cross product is calculated.
	 * @return the cross product between this and the given Vector.
	 */
	public double getCrossProduct(Vector vectorB) {
		return this.dirX * vectorB.dirY - vectorB.dirX * this.dirY;
	}

	/**
	 * Normalizes the Vector.
	 */
	public void normalize() {
		double newDirX = (1. / this.length()) * this.dirX;
		double newDirY = (1. / this.length()) * this.dirY;

		this.dirX = newDirX;
		this.dirY = newDirY;
		this.updateLength();
	}

	/**
	 * Sets the Vector to a specific length.
	 * 
	 * @param length
	 *            the length that the Vector is supposed to have.
	 */
	public void setToLength(double length) {
		this.normalize();

		this.dirX *= length;
		this.dirY *= length;
		this.updateLength();
	}

	/**
	 * Function for generating the scalar / dot product of the current and a
	 * given Vector.
	 * 
	 * @param vectorB
	 *            the Vector the scalar product is calculated with.
	 * @return the scalar / dot product of the current and the given Vector.
	 */
	public double getScalarProduct(Vector vectorB) {
		double xProduct = this.dirX * vectorB.dirX;
		double yProduct = this.dirY * vectorB.dirY;

		return (xProduct + yProduct);
	}

	/**
	 * Function for calculating the angle of this Vector to another given one in
	 * <b>degrees</b>.
	 * 
	 * @param vectorB
	 *            the Vector the angle is calculated to.
	 * @return the angle of this Vector to another given one in <b>degrees</b>.
	 */
	public double getAngleToVector(Vector vectorB) {
		return Math.toDegrees(Math.acos(this.getScalarProduct(vectorB) / (this.length * vectorB.length())));
	}

	/**
	 * Convenience function. The values being used are the default ones.
	 * 
	 * @see Vector#display(Color, boolean, int, int)
	 */
	public void display() {
		this.display(DEFAULT_ARROW_COLOR);
	}

	/**
	 * Convenience function.
	 * 
	 * @see Vector#display(Color, boolean, int, int)
	 * @param color
	 *            the color of the displayed Vector.
	 */
	public void display(Color color) {
		this.display(color, DEFAULT_ARROW_DIRECTION_SHOWN);
	}

	/**
	 * Convenience function. If the direction flag is set the values being used
	 * are the default ones.
	 * 
	 * @see Vector#display(Color, boolean, int, int)
	 * @param color
	 *            the color of the displayed Vector.
	 * @param showDirection
	 *            flag for determining if the direction of the Vector should be
	 *            shown with an arrow.
	 */
	public void display(Color color, boolean showDirection) {
		this.display(color, showDirection, DEFAULT_ARROW_LENGTH, DEFAULT_ARROW_ANGLE_DEG);
	}

	/**
	 * Function for displaying the Vector on the ingame map.
	 * 
	 * @param color
	 *            the color of the displayed Vector.
	 * @param showDirection
	 *            flag for determining if the direction of the Vector should be
	 *            shown with an arrow.
	 * @param arrowLength
	 *            the length of the arrow head sides. Can be ignored if the
	 *            direction flag is not set.
	 * @param arrowAngleDeg
	 *            the degree at which the arrow head sides are shown. Can be
	 *            ignored if the direction flag is not set.
	 */
	public void display(Color color, boolean showDirection, int arrowLength, int arrowAngleDeg) {
		Position start = new Position(this.x, this.y);
		Position end = new Position((int) (this.x + this.dirX), (int) (this.y + this.dirY));
		Game game = Core.getInstance().getGame();

		game.drawLineMap(start, end, color);

		// Display the direction of the Vector.
		if (showDirection) {
			Vector arrowLeft = new Vector(end.getX(), end.getY(), start.getX() - end.getX(), start.getY() - end.getY());
			Vector arrowRight = new Vector(end.getX(), end.getY(), start.getX() - end.getX(),
					start.getY() - end.getY());

			// Shorten the Vectors.
			arrowLeft.setToLength(arrowLength);
			arrowRight.setToLength(arrowLength);

			// Rotate them accordingly.
			arrowLeft.rotateLeftDEG(arrowAngleDeg);
			arrowRight.rotateRightDEG(arrowAngleDeg);

			// Display them on the map.
			game.drawLineMap(end, new Position((int) (arrowLeft.getX() + arrowLeft.getDirX()),
					(int) (arrowLeft.getY() + arrowLeft.getDirY())), color);
			game.drawLineMap(end, new Position((int) (arrowRight.getX() + arrowRight.getDirX()),
					(int) (arrowRight.getY() + arrowRight.getDirY())), color);
		}
	}

	// ------------------------------ Getter / Setter

	@Override
	public void setX(Integer x) {
		this.x = x;

		this.updateLength();
	}

	@Override
	public void setY(Integer y) {
		this.y = y;

		this.updateLength();
	}

	public double getDirX() {
		return dirX;
	}

	public void setDirX(double dirX) {
		this.dirX = dirX;

		this.updateLength();
	}

	public double getDirY() {
		return dirY;
	}

	public void setDirY(double dirY) {
		this.dirY = dirY;

		this.updateLength();
	}
}

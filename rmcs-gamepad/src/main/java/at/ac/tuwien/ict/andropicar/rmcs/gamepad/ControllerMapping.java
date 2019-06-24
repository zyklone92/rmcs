package at.ac.tuwien.ict.andropicar.rmcs.gamepad;


/**
 * An abstract class to provide some general means to access most of a controllers buttons and axes, regardless of the type or brand of controller.
 * 
 * @author Boeck
 *
 */
public abstract class ControllerMapping {
	
	/** @return the index for the button that is usually used to turn on the controller */
	public abstract int getMode();
	/** @return the index for the select-button */
	public abstract int getSelect();
	/** @return the index for the start-button */
	public abstract int getStart();
	/** @return the index for the button on the right hand side of the controller that is facing south. */
	public abstract int getFace1();
	/** @return the index for the button on the right hand side of the controller that is facing east. */
	public abstract int getFace2();
	/** @return the index for the button on the right hand side of the controller that is facing west. */
	public abstract int getFace3();
	/** @return the index for the button on the right hand side of the controller that is facing north. */
	public abstract int getFace4();
	/** @return the index for the left-thumb-button (left-stick-button). */
	public abstract int getLeftThumb();
	/** @return the index for the right-thumb-button (right-stick-button). */
	public abstract int getRightThumb();
	/** @return the index for the controllers x-axis (left-stick horizontal axis). */
	public abstract int getXAxis();
	/** @return the index for the controllers y-axis (left-stick vertical axis). */
	public abstract int getYAxis();
	/** @return the index for the controllers z-axis (right-stick horizontal axis). */
	public abstract int getZAxis();
	/** @return the index for the controllers rz-axis (right-stick vertical axis). */
	public abstract int getRzAxis();
	/** @return the index for the controllers left upper-trigger. */
	public abstract int getLeftUpperTrigger();
	/** @return the index for the controllers right upper-trigger. */
	public abstract int getRightUpperTrigger();
	/** @return the index for the controllers left lower-trigger. */
	public abstract int getLeftLowerTrigger();
	/** @return the index for the controllers right lower-trigger. */
	public abstract int getRightLowerTrigger();
	/** @return the total number of the controllers buttons and axes. */
	public abstract int getNumberOfElements();
	
}

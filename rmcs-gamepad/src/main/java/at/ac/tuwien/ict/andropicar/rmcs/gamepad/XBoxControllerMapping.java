package at.ac.tuwien.ict.andropicar.rmcs.gamepad;


/**
 * Contains the mappings for all buttons and axes of a XBOX-Controller and provides means to access them.
 * 
 * @author Boeck
 *
 */
public class XBoxControllerMapping extends ControllerMapping {
	
	public static int Mode = 8;
	public static int Select = 6;
	public static int Start = 7;
	public static int A = 0;
	public static int B = 1;
	public static int X = 2;
	public static int Y = 3;
	public static int LeftUpperTrigger = 4;
	public static int RightUpperTrigger = 5;
	public static int LeftLowerTrigger = 16;
	public static int RightLowerTrigger = 15;
	public static int LeftThumb = 9;
	public static int RightThumb = 10;
	public static int XAxis = 11;
	public static int YAxis = 12;
	public static int ZAxis = 13;
	public static int RzAxis = 14;
	public static int PoV = 17;
	
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public int getNumberOfElements(){
		return 18;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public int getMode() {
		return Mode;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public int getSelect() {
		return Select;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public int getStart() {
		return Start;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public int getFace1() {
		return A;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public int getFace2() {
		return X;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public int getFace3() {
		return B;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public int getFace4() {
		return Y;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public int getLeftThumb() {
		return LeftThumb;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public int getRightThumb() {
		return RightThumb;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public int getXAxis() {
		return XAxis;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public int getYAxis() {
		return YAxis;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public int getZAxis() {
		return ZAxis;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public int getRzAxis() {
		return RzAxis;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public int getLeftUpperTrigger() {
		return LeftUpperTrigger;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public int getRightUpperTrigger() {
		return RightUpperTrigger;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public int getLeftLowerTrigger() {
		return LeftLowerTrigger;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public int getRightLowerTrigger() {
		return RightLowerTrigger;
	}
	
	
	
}

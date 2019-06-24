package at.ac.tuwien.ict.andropicar.rmcs.gamepad;


/**
 * Contains the mappings for all buttons and axes of a PS3-Controller and provides means to access them.
 * 
 * @author Boeck
 *
 */
public class PS3ControllerMapping extends ControllerMapping {
	
	public static int Playstation = 16;
	public static int Select = 0;
	public static int Start = 3;
	public static int X = 14;
	public static int Circle = 13;
	public static int Square = 15;
	public static int Triangle = 12;
	public static int LeftUpperTrigger = 10;
	public static int RightUpperTrigger = 11;
	public static int LeftLowerTrigger = 8;
	public static int RightLowerTrigger = 9;
	public static int ArrowUp = 4;
	public static int ArrowRight = 5;
	public static int ArrowDown = 6;
	public static int ArrowLeft = 7;
	public static int LeftThumb = 1;
	public static int RightThumb = 2;
	public static int XAxis = 19;
	public static int YAxis = 20;
	public static int ZAxis = 21;
	public static int RzAxis = 22;
	public static int XAnalog = 37;
	public static int CircleAnalog = 36;
	public static int SquareAnalog = 38;
	public static int TriangleAnalog = 35;
	public static int XGyro = 42;
	public static int YGyro = 43;
	public static int ZGyro = 44;
	
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public int getNumberOfElements(){
		return 28;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public int getMode() {
		return Playstation;
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
		return X;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public int getFace2() {
		return Square;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public int getFace3() {
		return Circle;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public int getFace4() {
		return Triangle;
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

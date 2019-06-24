package at.ac.tuwien.ict.andropicar.rmcs.network;


/**
 * This class holds all the keywords that are to be used to communicate with other devices (via JSON-strings).<br>
 * The keywords should be accessed in a static manner (without instantiation of this class), which is why the constructor has been set private.
 * @author Boeck
 */
public class Keywords{
	
	public static final String id = "id";
	public static final String connect = "connect";
	public static final String state = "state";
	public static final String request = "request";
	public static final String properties = "props";
	public static final String phoneControl = "phnCtrl";
	public static final String steering = "steer";
	public static final String acceleration = "acc";
	public static final String stop = "stop";
	public static final String cameraYaw = "yaw";
	public static final String cameraPitch = "ptch";
	public static final String frontLights = "fLts";
	public static final String backLights = "bLts";
	public static final String dynamicLights = "dynLts";
	public static final String leftWinker = "lWnkr";
	public static final String rightWinker = "rWnkr";
	public static final String ultrasonicSensor = "uSSen";
	public static final String leftInfraredSensor = "lISen";
	public static final String rightInfraredSensor = "rISen";
	public static final String hallSensor = "hSen";
	public static final String[] forwardKeywords = {"phnCtrl", "steer", "acc", "stop",
			"yaw", "ptch", "fLts", "bLts", "lWnkr", "rWnkr", "uSSen", "lISen", "rISen", "hSen"};
	
	
	private Keywords(){
	}
}

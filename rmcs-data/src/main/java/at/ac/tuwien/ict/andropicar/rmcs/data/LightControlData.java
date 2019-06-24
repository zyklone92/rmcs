package at.ac.tuwien.ict.andropicar.rmcs.data;


/**
 * Contains one set of values for controlling the RMCSs lights (front / back, winkers).
 * 
 * @author Boeck
 */
public class LightControlData implements IControlData {
	
	/** true, if RMCSs lights should be controlled dynamically (through a lightsensor). */
	private boolean dynamicLights = false;
	/** true, if the front-lights should be on. */
	private boolean frontlights = false;
	/** true, if the back-lights should be on. */
	private boolean backlights = false;
	/** true, if the left-winkers should be active. */
	private boolean leftWinker = false;
	/** true, if the right-winkers should be active. */
	private boolean rightWinker = false;
	
	
	/**
	 * @param frontlights true, if the front-lights should be on.
	 * @param backlights true, if the back-lights should be on. 
	 * @param dynamicLights true, if RMCSs lights should be controlled dynamically (through a lightsensor).
	 * @param leftWinker true, if the left-winkers should be active.
	 * @param rightWinker true, if the right-winkers should be active.
	 */
	public LightControlData(boolean frontlights, boolean backlights, boolean dynamicLights, boolean leftWinker, boolean rightWinker){
		this.frontlights = frontlights;
		this.backlights = backlights;
		this.dynamicLights = dynamicLights;
		this.leftWinker = leftWinker;
		this.rightWinker = rightWinker;
	}
	
	/**
	 * @param lightData an array of booleans to initialize this data-set in the following order:
	 * front-lights, back-lights, dynamic-lights, left-winkers, right-winkers;
	 */
	public LightControlData(boolean[] lightData){
		this.frontlights = lightData[0];
		this.backlights = lightData[1];
		this.dynamicLights = lightData[2];
		this.leftWinker = lightData[3];
		this.rightWinker = lightData[4];
		
	}

	
	/**
	 * @return true, if RMCSs lights should be controlled dynamically (through a lightsensor).
	 */
	public boolean dynamicLightsOn() {
		return dynamicLights;
	}

	/**
	 * @return true, if the front-lights should be on.
	 */
	public boolean headlightsOn() {
		return frontlights;
	}

	/**
	 * @return  true, if the back-lights should be on.
	 */
	public boolean brakelightsOn() {
		return backlights;
	}

	/**
	 * @return true, if the left-winkers should be active.
	 */
	public boolean leftWinkerOn() {
		return leftWinker;
	}

	/**
	 * @return rightWinker true, if the right-winkers should be active.
	 */
	public boolean rightWinkerOn() {
		return rightWinker;
	}

}

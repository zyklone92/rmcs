package at.ac.tuwien.ict.andropicar.rmcs.data;


/**
 * Contains one set of values for controlling the RMCSs camera rotation.
 * 
 * @author Boeck
 */
public class CameraControlData implements IControlData {
	
	/** the yaw-angle (horizontal) for the camera */
	private int yawAngle;
	/** the pitch-angle (vertical) for the camera */
	private int pitchAngle;
	
	
	/**
	 * Constructor that ensures that the data is within its valid range of [-127;127].<br>
	 * If the passed arguments are outside the valid range, the corresponding attributes are set to their maximum / minimum allowed value.
	 * @param yawAngle the yaw-angle for the camera. Should be between -127 and +127.
	 * @param pitchAngle the pitch-angle for the camera. Should be between -127 and +127.
	 */
	public CameraControlData(int yawAngle, int pitchAngle){
		if(yawAngle < -127)
			this.yawAngle = -127;
		else if(yawAngle > 127)
			this.yawAngle = 127;
		else
			this.yawAngle = yawAngle;
		
		if(pitchAngle < -127)
			this.pitchAngle = -127;
		else if(yawAngle > 127)
			this.pitchAngle = 127;
		else
			this.pitchAngle = pitchAngle;
	}

	
	/**
	 * @return the yaw-angle (horizontal) for the camera
	 */
	public int getYawAngle() {
		return yawAngle;
	}

	/**
	 * @return the pitch-angle (vertical) for the camera
	 */
	public int getPitchAngle() {
		return pitchAngle;
	}

}

package at.ac.tuwien.ict.andropicar.rmcs.data;


/**
 * Contains one set of values for controlling the RMCSs drive (acceleration/velocity and steering).
 * 
 * @author Boeck
 */
public class DriveControlData implements IControlData{

	/** the acceleration/velocity for the car. Should be between -127 and +127. */
	private int acceleration = 0;
	/** the steering angle for the car. Should be between -127 and +127. */
	private int steeringAngle = 0;
	/** true, if the RMCS should stop immediately (probably due to connection-loss), otherwise false */
	private boolean stop = false;
	
	/**
	 * Constructor that ensures that the data is within its valid range of [-127;127].<br>
	 * If the passed arguments are outside the valid range, the corresponding attributes are set to their maximum / minimum allowed value.
	 * @param acceleration the acceleration/velocity for the car. Should be between -127 and +127.
	 * @param steeringAngle the steering angle for the car. Should be between -127 and +127.
	 * @param stop true, if the RMCS should stop immediately (probably due to connection-loss), otherwise false
	 */
	public DriveControlData(int acceleration, int steeringAngle, boolean stop){
		this.acceleration = acceleration;
		this.steeringAngle = steeringAngle;
		this.stop = stop;
	}
	
	/**
	 * Constructor that ensures that the data is within its valid range of [-127;127].<br>
	 * If the passed arguments are outside the valid range, the corresponding attributes are set to their maximum / minimum allowed value.
	 * @param acceleration the acceleration/velocity for the car. Should be between -127 and +127.
	 * @param steeringAngle the steering angle for the car. Should be between -127 and +127.
	 */
	public DriveControlData(int acceleration, int steeringAngle){
		if(acceleration < -127)
			this.acceleration = -127;
		else if(acceleration > 127)
			this.acceleration = 127;
		else
			this.acceleration = acceleration;
		
		if(steeringAngle < -127)
			this.steeringAngle = -127;
		else if(steeringAngle > 127)
			this.steeringAngle = 127;
		else
			this.steeringAngle = steeringAngle;
	}
	
	
	/**
	 * @return the acceleration/velocity for the car.
	 */
	public int getAcceleration() {
		return acceleration;
	}
	
	/**
	 * @return the steering angle for the car.
	 */
	public int getSteeringAngle() {
		return steeringAngle;
	}
	
	
	/**
	 * @return true, if the car should be stopped immediately, otherwise false.
	 */
	public boolean isStopped(){
		return this.stop;
	}
	
}

package at.ac.tuwien.ict.andropicar.rmcs.gamepad;


/**
 * A class to set and store the gamepads last inputs to control the car.
 * 
 * @author Boeck
 *
 */
public class GamepadInput {

	/** the acceleration/velocity for the car. Should be between -127 and +127. */
	private int acceleration = 0;
	/** the steering angle for the car. Should be between -127 and +127. */
	private int steeringAngle = 0;
	/** the (virtual) gear the car is currently in. Maximum gear is 5. This has been implemented to have a finer control of the cars acceleration via a gamepad. */
	private int gear = 1;
	/** used to know which mode of operation is currently active for accelerating the car. */
	private boolean throttleMode = false;
	/** used to know which mode of operation is currently active for steering the car. */
	private boolean steeringMode = false;
	// TODO reactivate the controlsChanged attribute
	/** used to know if the gamepad input for controlling the drive has changed since last time the data was accessed. */
	private volatile boolean driveControlsChanged = false;
	/** true, if RMCSs lights should be controlled dynamically (through a lightsensor) */
	private boolean dynamicLights = false;
	/** true, if the front-lights should be on. */
	private boolean frontlights = false;
	/** true, if the back-lights should be on. */
	private boolean backlights = false;
	/** true, if the car is currently braking or in reverse (acceleration has a value smaller than or equal to zero). */
	private boolean brake = false;
	/** true, if the left winker is set */
	private boolean leftWinker = false;
	/** true, if the right winker is set */
	private boolean rightWinker = false;
	/** used to know if the gamepad input for controlling the lights has changed since last time the data was accessed. */
	private volatile boolean lightsChanged = false;
	
	
	/**
	 * @return the (virtual) gear the car is currently in.
	 */
	public int getGear(){
		return this.gear;
	}
	
	/**
	 * Sets the (virtual) gear of the car, while ensuring that the range [1;5] is adhered to.
	 * 
	 * @param gear the gear the car should be in.
	 */
	public void setGear(int gear){
		if(gear < 1)
			gear = 1;
		if(gear > 5)
			gear = 5;
		this.gear = gear;
	}
	
	/**
	 * Raises the gear of the car by 1, while ensuring that the range [1;5] is adhered to.
	 */
	public void gearUp(){
		if(this.gear < 5)
			this.gear++;
	}
	
	/**
	 * Lowers the gear of the car by 1, while ensuring that the range [1;5] is adhered to.
	 */
	public void gearDown(){
		if(this.gear > 1)
			this.gear--;
	}
	
	/**
	 * @return true, if the car is currently braking or in reverse (acceleration has a value smaller than or equal to zero).
	 */
	public boolean brakeOn()
	{
		return this.brake;
	}
	
	/**
	 * @param brake sets the corresponding attribute of this class.
	 */
	public void setBrake(boolean brake)
	{
		this.brake = brake;
	}
	
	/**
	 * @return used to know which mode of operation is currently active for accelerating the car.
	 */
	public boolean getThrottleMode()
	{
		return this.throttleMode;
	}
	
	/**
	 * Changes between the two available modes of operation for accelerating the car.
	 */
	public void toggleThrottleMode()
	{
		this.throttleMode = !this.throttleMode;
	}
	
	/**
	 * @return used to know which mode of operation is currently active for accelerating the car.
	 */
	public boolean getSteeringMode()
	{
		return this.steeringMode;
	}
	
	/**
	 * Changes between the two available modes of operation for steering the car.
	 */
	public void toggleSteeringMode()
	{
		this.steeringMode = !this.steeringMode;
	}

	/**
	 * @return true, if the gamepad input for controlling the drive has changed since last time the data was accessed.
	 */
	public boolean driveControlsChanged() {
		return this.driveControlsChanged;
	}

	/**
	 * @return true, if RMCSs lights should be controlled dynamically (through a lightsensor)
	 */
	public boolean dynamicLightsOn() {
		this.lightsChanged = false;
		return dynamicLights;
	}

	/**
	 * @param dynamicLights sets the corresponding attribute of this class.
	 */
	public void setDynamicLights(boolean dynamicLights) {
		this.lightsChanged = true;
		this.dynamicLights = dynamicLights;
	}
	
	/**
	 * Toggles between the dynamic lights being active and inactive.
	 */
	public void toggleDynamicLights()
	{
		this.lightsChanged = true;
		this.dynamicLights = !this.dynamicLights;
	}

	/**
	 * @return used to know if the gamepad input for controlling the lights has changed since last time the data was accessed.
	 */
	public boolean lightsChanged() {
		return lightsChanged;
	}

	/**
	 * @return the acceleration/velocity for the car. Should be between -127 and +127.
	 */
	public synchronized int getAcceleration() {
		//this.controlsChanged = false;
		return acceleration;
	}

	/**
	 * @param acceleration the acceleration/velocity for the car. Should be between -127 and +127.
	 */
	public synchronized void setAcceleration(int acceleration) {
		if(acceleration < -100)
			acceleration = -100;
		if(acceleration > 100)
			acceleration = 100;
		this.acceleration = acceleration;
		this.driveControlsChanged = true;
	}

	/**
	 * @return the steering angle for the car. Should be between -127 and +127.
	 */
	public synchronized int getSteeringAngle() {
		//this.controlsChanged = false;
		return this.steeringAngle;
	}

	/**
	 * @param steeringAngle the steering angle for the car. Should be between -127 and +127.
	 */
	public synchronized void setSteeringAngle(int steeringAngle) {
		this.steeringAngle = steeringAngle;
		this.driveControlsChanged = true;
	}

	/**
	 * @return true, if the front-lights should be on.
	 */
	public boolean frontLightsOn() {
		this.lightsChanged = false;
		return frontlights;
	}

	/**
	 * @param headlights sets the corresponding attribute of this class.
	 */
	public void setFrontLights(boolean headlights) {
		this.frontlights = headlights;
		this.lightsChanged = true;
	}
	
	/**
	 * Toggles between the front-lights being on and off.
	 */
	public void toggleFrontLights()
	{
		this.frontlights = !this.frontlights;
		this.lightsChanged = true;
	}

	/**
	 * @return true, if the back-lights should be on.
	 */
	public boolean backLightsOn() {
		this.lightsChanged = false;
		return backlights;
	}

	/**
	 * @param brakelights sets the corresponding attribute of this class.
	 */
	public void setBackLights(boolean brakelights) {
		this.backlights = brakelights;
		this.lightsChanged = true;
	}
	
	/**
	 * Toggles between the back-lights being on and off.
	 */
	public void toggleBackLights()
	{
		this.backlights = !this.backlights;
		this.lightsChanged = true;
	}

	/**
	 * @return true, if the left winker should be on.
	 */
	public boolean leftWinkerOn() {
		this.lightsChanged = false;
		if(!leftWinker)
			return false;
		leftWinker = false;
		return true;
	}

	/**
	 * Sets the the left winker to true.
	 */
	public void setLeftWinker() {
		this.leftWinker = true;
		this.lightsChanged = true;
	}

	/**
	 * @return true, if the right winker should be on.
	 */
	public boolean rightWinkerOn() {
		this.lightsChanged = false;
		if(!rightWinker)
			return false;
		rightWinker = false;
		return true;
	}

	/**
	 * Sets the right winker to true.
	 */
	public void setRightWinker() {
		this.rightWinker = true;
		this.lightsChanged = true;
	}
	
}


package at.ac.tuwien.ict.andropicar.rmcs.gamepad;

import java.util.concurrent.LinkedBlockingQueue;

import at.ac.tuwien.ict.andropicar.rmcs.connection.Connection;
import at.ac.tuwien.ict.andropicar.rmcs.connection.ControlChangeQueueElement;
import at.ac.tuwien.ict.andropicar.rmcs.data.CameraControlData;
import at.ac.tuwien.ict.andropicar.rmcs.data.IControlData;
import at.ac.tuwien.ict.andropicar.rmcs.data.IData;
import at.ac.tuwien.ict.andropicar.rmcs.data.DriveControlData;
import at.ac.tuwien.ict.andropicar.rmcs.data.EDataType;
import at.ac.tuwien.ict.andropicar.rmcs.data.LightControlData;
import at.ac.tuwien.ict.andropicar.rmcs.ui.EIdentifier;
import at.ac.tuwien.ict.andropicar.rmcs.ui.IUI;
import net.java.games.input.Controller;


/**
 * Being a concrete implementation of the {@link Connection} class, this class basic function is to communicate with a remote device connected to the RMCS - 
 * a gamepad in this case.<br>
 * This implementation reads data from a gamepad, creates {@link IControlData}-sets accordingly (if in control) and puts them on the {@link Connection#controlDataQueue}.<br>
 * It is also able to send control-change-requests to the main-thread.<br>
 * If there is more than one gamepad connected to the RMCS, one gamepad cannot be in control of the {@link DriveControlData} and the {@link CameraControlData} at the same time.
 * One will be in control of the drive and one will be in control of the camera.<br>
 * These controls can be passed onto the next gamepad, however. This is done by pressing the start-button or select-button for at least 500ms to pass on the control of the drive or the camera, respectively.<br>
 * If the next gamepad happens to be in control of one of the control-sets already, that gamepad is skipped and the control is automatically passed on to the next gamepad in the list.<br>
 * If the mode-button is pressed at least 500ms, complete control is passed to the {@link at.ac.tuwien.ict.andropicar.rmcs.network.ServerConnection}.<br>
 * Control between phone and gamepad can also be toggled by pressing both thumb-buttons and both upper-triggers for at least 1000ms.<br>
 * 
 * @author Boeck
 *
 */
public class GamepadConnection extends Connection implements Runnable {

	/** the actual Controller-Object of the jinput-package that is used to communicate with the gamepad */
	private Controller gamepad = null;
	/** the gamepads current state */
	private GamepadInput gamepadData = null;
	/** the button-mapping for this gamepad - depending on the gamepad that is used */
	private ControllerMapping controllerMapping = null;
	/** true, if this is the only connected gamepad, otherwise false */
	private boolean solo = false;
	/** true, if this runnable should finish, otherwise false */
	private boolean killRunnable = false;
	
	/** array to check which buttons were just pushed down (or not) an instant ago */
	boolean[] componentsPressed = null;
	
	/** array to check if an axis was just used in a digital manner (all the way to one side) and how it was used (-1, 0, +1) */
	int[] digitalAxes = new int[4];
	
	/**
	 * 
	 * @param carId the ID that the RMCS registers itself with on the Server.
	 * @param controlDataQueue the queue that is used to send ControlData to their corresponding interfaces.
	 * @param controlChangeQueue the queue that is used to signal the main class to change the connection that is currently under control of a specific control-data-set.
	 * @param gamepad the actual Controller-Object of the jinput-package that is used to communicate with the gamepad.
	 */
	public GamepadConnection(long carId, LinkedBlockingQueue<IData> controlDataQueue, LinkedBlockingQueue<ControlChangeQueueElement> controlChangeQueue,
			Controller gamepad) {
		
		super(carId, controlDataQueue, controlChangeQueue);

		if(gamepad == null || controlDataQueue == null || controlChangeQueue == null) {
			this.killRunnable = true;
			return;
		}
		
		this.gamepad = gamepad;
		//this.gamepadData = new GamepadInput(driveControl, cameraControl);
		this.gamepadData = new GamepadInput();
		this.setControllerMapping(gamepad);
		
		this.componentsPressed = new boolean[this.controllerMapping.getNumberOfElements()];
		
		// initialize the array
		for(int i = 0; i < this.controllerMapping.getNumberOfElements(); i++)
			this.componentsPressed[i] = false;
	}
	
	
	/**
	 * 
	 * @param carId The ID that the RMCS registers itself with on the Server.
	 * @param controlDataQueue the queue that is used to send ControlData to their corresponding interfaces.
	 * @param controlChangeQueue the queue that is used to signal the main class to change the connection that is currently under control of a specific control-data-set.
	 * @param gamepad the actual Controller-Object of the jinput-package that is used to communicate with the gamepad.
	 * @param solo true, if this is the only connected gamepad, otherwise false.
	 */
	public GamepadConnection(long carId, LinkedBlockingQueue<IData> controlDataQueue, LinkedBlockingQueue<ControlChangeQueueElement> controlChangeQueue,
			Controller gamepad, boolean solo) {
		
		this(carId, controlDataQueue, controlChangeQueue, gamepad);
		this.solo = solo;
	}
	
	
	/**
	 * {@inheritDoc}
	 * <br>
	 * The control-update method for the GamepadConnection has the additional task of making sure that one GamepadConnection cannot be in control of the the drive and the camera at the same time.<br>
	 * This would normally not be necessary, but instead has been implemented to demonstrate the flexibility of the core code, even for the gamepad-related components.<br>
	 * Actually, this override causes some kind of deadlock in the current implementation, because the controlChangeQueue is permanently written to, if only one gamepad is connected.
	 */
	@Override
	public boolean updateControllingDevice(long connectionIdInControl, EDataType dataType) {
		System.out.println("Processing new incoming controlChangeRequest!");
		if(connectionIdInControl < 1)
			return false;
		
		switch(dataType){
		case DRIVECONTROL:
			super.connectionIdInDriveControl = connectionIdInControl;
			if(!this.solo && connectionIdInControl == super.getConnectionId() && connectionIdInControl == super.connectionIdInCameraControl)
				// this will eventually cause an overflow (due to finite number of gamepads), which will be handled by the CarApplication class
				super.changeControllingDevice(connectionIdInControl + 1, dataType);
			break;
		case LIGHTCONTROL:
			super.connectionIdInLightControl = connectionIdInControl;
			// this is no error, but purposely the same if-statement as for the drive-control case, because for gamepads, drive-control and light-control belong together 
			if(!this.solo && connectionIdInControl == super.getConnectionId() && connectionIdInControl == connectionIdInCameraControl)
				super.changeControllingDevice(connectionIdInControl + 1, dataType);
			break;
		case CAMERACONTROL:
			super.connectionIdInCameraControl = connectionIdInControl;
			if(!this.solo && connectionIdInControl == super.getConnectionId() && connectionIdInControl == connectionIdInDriveControl)
				super.changeControllingDevice(connectionIdInControl + 1, dataType);
			break;
		default:
			return false;
		}
		System.out.println("Gamepad processed controlChangeRequest. New ID in Control: " + connectionIdInControl);
		return true;
	}
	
	
	/**
	 * Determines the {@link ControllerMapping} for the passed gamepad.
	 * 
	 * @param gamepad the gamepad, that the {@link ControllerMapping} should be determined for.
	 */
	private void setControllerMapping(Controller gamepad) {
		
		if(gamepad.getType().equals(Controller.Type.STICK)){
			// PS3-Controller
			this.controllerMapping = new PS3ControllerMapping();
		}
		else{
			// XBOX-Controller
			this.controllerMapping = new XBoxControllerMapping();
		}
	}
	
	
	/**
	 * Convenience-method for waiting a specified amount of milliseconds.
	 * 
	 * @param ms amount of milliseconds to wait.
	 * @return true, if the specified amount of time has passed, otherwise false.
	 */
	private boolean waitms(long ms) {
		try {
			synchronized(this){
				this.wait(ms);
			}
			return true;
		} catch(InterruptedException ie) {
			ie.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * Sets the killRunnable-flag to true, which tells this Runnable to finish.
	 */
	public void killRunnable() {
		this.killRunnable = true;
	}
	
	
	/**
	 * Reads data from the gamepad and calculates a relative value for acceleration (based on full acceleration)
	 * 
	 * @return the relative acceleration
	 */
	private int getAcceleration() {
		
		if(this.controllerMapping instanceof PS3ControllerMapping){
			
			//PS3-Controller
			if(!this.gamepadData.getThrottleMode()) {
				float originalAcc = 0;
				originalAcc = this.gamepad.getComponents()[PS3ControllerMapping.RzAxis].getPollData() * (-1);
				
				if(originalAcc < 0.04f && originalAcc > -0.04f)
					return 0;
					//return (int) (Math.pow(originalAcc, 2) * (-1) * (this.gamepadData.getGear() * 40));
				else
					return (int) ((originalAcc - 0.03f) * (1.0309f) * (this.gamepadData.getGear() * 20));
			}
			else{
				float acceleration = 0;
				float deceleration = 0;
				acceleration = ( this.gamepad.getComponents()[PS3ControllerMapping.XAnalog].getPollData() + 1 ) / 2;
				deceleration = ( this.gamepad.getComponents()[PS3ControllerMapping.SquareAnalog].getPollData() + 1 ) / (-2);
				return (int) ( ((acceleration * 20) + (deceleration * 40)) * this.gamepadData.getGear() );
			}
		}
		else if(this.controllerMapping instanceof XBoxControllerMapping){
			
			//XBOX-Controller
			float acceleration = 0;
			float deceleration = 0;
			acceleration = ( this.gamepad.getComponents()[XBoxControllerMapping.RightLowerTrigger].getPollData() + 1 ) / 2;
			deceleration = ( this.gamepad.getComponents()[XBoxControllerMapping.LeftLowerTrigger].getPollData() + 1 ) / (-2);
			return (int) ( ((acceleration * 20) + (deceleration * 40)) * this.gamepadData.getGear() );
		}
		else{
			return 0;
		}
	}
	
	
	/**
	 * Reads data from the gamepad and calculates a relative value for the steering angle (based on full left/right lock (from left to right))
	 * 
	 * @return the relative steering angle
	 */
	private int getSteeringAngle() {
		
		float originalSteer = 0;
		if(this.controllerMapping instanceof PS3ControllerMapping){
			
			//PS3-Controller
			if(!this.gamepadData.getSteeringMode()) {
				originalSteer = this.gamepad.getComponents()[PS3ControllerMapping.XAxis].getPollData();
				if(originalSteer < 0.04f && originalSteer > -0.04f)
					return 0;
					//return (int) (Math.pow(originalAcc, 2) * (-1) * (this.gamepadData.getGear() * 40));
				else
					return (int) ((originalSteer - 0.03f) * 1.0309f * 100);
				//return (int) (Math.pow(originalSteer, 3) * 100);
			}
			else{
				originalSteer = this.gamepad.getComponents()[PS3ControllerMapping.XGyro].getPollData();
				return (int) (originalSteer * (-833));
			}
		}
		else if(this.controllerMapping instanceof XBoxControllerMapping){
			
			//XBOX-Controller
			originalSteer = this.gamepad.getComponents()[XBoxControllerMapping.XAxis].getPollData();
			if(originalSteer < 0.04f && originalSteer > -0.04f)
				return 0;
				//return (int) (Math.pow(originalAcc, 2) * (-1) * (this.gamepadData.getGear() * 40));
			else
				return (int) ((originalSteer - 0.03f) * 1.0309f * 100);
			//return (int) (Math.pow(originalSteer, 3) * 100);
		}
		else{
			return 0;
		}
	}
	
	
	/**
	 * Reads data from the gamepad and checks if a certain button has just started to get pressed down.
	 * 
	 * @param button the index for the button.
	 * @return true, if the button is currently being pressed, and was not pressed before.
	 */
	private boolean checkButtonPressed(int button) {
		
		if(!componentsPressed[button] && this.gamepad.getComponents()[button].getPollData() == 1){
			// button has just been pressed down
    		return true;
    	}
		return false;
	}
	
	
	/**
	 * Reads data from the gamepad and checks if a certain button has just been released.
	 * 
	 * @param button the index for the button.
	 * @return true, if the button has just been released.
	 */
	private boolean checkButtonReleased(int button) {
		
    	if(componentsPressed[button] && this.gamepad.getComponents()[button].getPollData() == 0){
    		// button has just been released
    		return true;
    	}
		return false;
	}
	
	
	/**
	 * 
	 * 
	 * @param startedPressingThrottleModeChange timestamp, since the button to change the throttle mode has started to get pressed down. -1 if it is not pressed down at the moment.
	 * @param startedPressingSteeringModeChange timestamp, since the button to change the steering mode has started to get pressed down. -1 if it is not pressed down at the moment.
	 * @param startedPressingControlChange timestamp, since the button to pass the control onto the {@link at.ac.tuwien.ict.andropicar.rmcs.network.ServerConnection} has started to get pressed down. -1 if it is not pressed down at the moment.
	 * @param startedPressingGamepadDriveControlChange timestamp, since the button to pass on the drive-control has started to get pressed down. -1 if it is not pressed down at the moment.
	 * @param startedPressingGamepadCameraControlChange timestamp, since the button to pass on the camera-control has started to get pressed down. -1 if it is not pressed down at the moment.
	 */
	private void detectAndProcessControlChanges(Long startedPressingThrottleModeChange, Long startedPressingSteeringModeChange, Long startedPressingControlChange,
			Long startedPressingGamepadDriveControlChange, Long startedPressingGamepadCameraControlChange){
		// be careful when using the Wrapper class Long, instead of long!!! (contains a reference instead of the number itself)
		
		// the following toggles between controlModes of the PS3-Controller
		if(this.controllerMapping instanceof PS3ControllerMapping) {
			//PS3-Controller
			if(this.checkButtonPressed(this.controllerMapping.getFace4()))
	    		startedPressingThrottleModeChange = System.currentTimeMillis();
	    	if(this.checkButtonReleased(this.controllerMapping.getFace4())) {
	    		if(!startedPressingThrottleModeChange.equals(-1L) && System.currentTimeMillis() - startedPressingThrottleModeChange.longValue() > 1000)
	    			this.gamepadData.toggleThrottleMode();
	    		startedPressingThrottleModeChange = -1L;
	    	}
	    	
	    	if(this.checkButtonPressed(this.controllerMapping.getFace3()))
	    		startedPressingSteeringModeChange = System.currentTimeMillis();
	    	if(this.checkButtonReleased(this.controllerMapping.getFace3())) {
	    		if(!startedPressingSteeringModeChange.equals(-1L) && System.currentTimeMillis() - startedPressingSteeringModeChange.longValue() > 1000)
	    			this.gamepadData.toggleSteeringMode();
	    		startedPressingSteeringModeChange = -1L;
	    	}
		}
		
		//toggle control between ServerConnection and this gamepad. This combination has priority over everything else.
		if(startedPressingControlChange.equals(-1L) &&
				this.componentsPressed[this.controllerMapping.getLeftThumb()] && this.componentsPressed[this.controllerMapping.getRightThumb()]
							&& this.componentsPressed[this.controllerMapping.getLeftUpperTrigger()] && this.componentsPressed[this.controllerMapping.getRightUpperTrigger()])
			startedPressingControlChange = System.currentTimeMillis();
		// press all 4 buttons for more than one second and then release at least one of the buttons to complete the combination
//		if((!startedPressingControlChange.equals(-1L) &&
//				!this.componentsPressed[this.controllerMapping.getLeftThumb()] && !this.componentsPressed[this.controllerMapping.getRightThumb()] &&
//    					this.componentsPressed[this.controllerMapping.getLeftUpperTrigger()] && this.componentsPressed[this.controllerMapping.getRightUpperTrigger()] ) ||
//								( this.componentsPressed[this.controllerMapping.getLeftThumb()] && this.componentsPressed[this.controllerMapping.getRightThumb()]
//										&& !this.componentsPressed[this.controllerMapping.getLeftUpperTrigger()] && !this.componentsPressed[this.controllerMapping.getRightUpperTrigger()] )){
		if(!startedPressingControlChange.equals(-1L) &&
				( this.checkButtonReleased(this.controllerMapping.getLeftThumb()) == true || this.checkButtonReleased(this.controllerMapping.getRightThumb()) == true || 
				this.checkButtonReleased(this.controllerMapping.getLeftUpperTrigger()) == true || this.checkButtonReleased(this.controllerMapping.getRightUpperTrigger()) == true )) {
    		if(System.currentTimeMillis() - startedPressingControlChange.longValue() > 1000) {
    			if(super.connectionIdInDriveControl != super.getConnectionId()){
    				if(super.changeControllingDevice(super.getConnectionId(), EDataType.DRIVECONTROL)) {
    					super.changeControllingDevice(super.getConnectionId(), EDataType.LIGHTCONTROL);
    					super.changeControllingDevice(super.getConnectionId() + 1, EDataType.CAMERACONTROL);
    				}
    			}
    			else{
					// set the ServerConnection as the controlling  Connection
    				if(super.changeControllingDevice(1, EDataType.DRIVECONTROL)) {
    					super.changeControllingDevice(1, EDataType.LIGHTCONTROL);
    					super.changeControllingDevice(1, EDataType.CAMERACONTROL);
    				}
    			}
    		}
    		startedPressingControlChange = -1L;
    	}
		
		// the following changes from the gamepad to the SeverConnection as the controlling unit, if the corresponding button (mode) has been pressed long enough (0.5s)
		// this control is only changed, if 
    	if(this.checkButtonPressed(this.controllerMapping.getMode()))
    		startedPressingControlChange = System.currentTimeMillis();
    	if(this.checkButtonReleased(this.controllerMapping.getMode()))
    	{
    		if(!startedPressingControlChange.equals(-1L) && System.currentTimeMillis() - startedPressingControlChange.longValue() > 500){
    			if(super.changeControllingDevice(1, EDataType.DRIVECONTROL)){
    				super.changeControllingDevice(1, EDataType.LIGHTCONTROL);
    				super.changeControllingDevice(1, EDataType.CAMERACONTROL);
    			}
    		startedPressingControlChange = -1L;
    		}
    	}
    	
    	// the following changes the drive-controls from the current gamepad to the next gamepad as the controlling unit, if the corresponding button (start) has been pressed long enough (0.5s)
    	if(super.connectionIdInDriveControl == super.getConnectionId()) {
	    	if(this.checkButtonPressed(this.controllerMapping.getStart()))
	    		startedPressingGamepadDriveControlChange = System.currentTimeMillis();
	    	if(this.checkButtonReleased(this.controllerMapping.getStart()))
	    	{
	    		if(!startedPressingGamepadDriveControlChange.equals(-1L) && System.currentTimeMillis() - startedPressingGamepadDriveControlChange.longValue() > 500){
	    			if(super.changeControllingDevice(super.getConnectionId() + 1, EDataType.DRIVECONTROL))
	    				super.changeControllingDevice(super.getConnectionId() + 1, EDataType.LIGHTCONTROL);
	    			startedPressingGamepadDriveControlChange = -1L;
	    		}
	    	}
    	}
    	
    	// the following changes the camera-controls from the current gamepad to the next gamepad as the controlling unit, if the corresponding button (select) has been pressed long enough (0.5s)
    	if(super.connectionIdInCameraControl == super.getConnectionId()) {
	    	if(this.checkButtonPressed(this.controllerMapping.getSelect()))
	    		startedPressingGamepadCameraControlChange = System.currentTimeMillis();
	    	if(this.checkButtonReleased(this.controllerMapping.getSelect()))
	    	{
	    		if(!startedPressingGamepadCameraControlChange.equals(-1L) && System.currentTimeMillis() - startedPressingGamepadCameraControlChange.longValue() > 500){
	    			super.changeControllingDevice(super.getConnectionId() + 1, EDataType.CAMERACONTROL);
	    			startedPressingGamepadCameraControlChange = -1L;
	    		}
	    	}
    	}
	}
	
	
	/**
	 * Checks {@link #componentsPressed} and {@link #digitalAxes} and updates {@link #gamepadData} accordingly.
	 */
	private void updateGamepadInputData() {
		this.gamepadData.setAcceleration(this.getAcceleration());
    	this.gamepadData.setSteeringAngle(this.getSteeringAngle());
		
    	if(this.gamepadData.getAcceleration() <= 0)
    		this.gamepadData.setBrake(true);
    	else
    		this.gamepadData.setBrake(false);
    	
    	if(this.checkButtonPressed(this.controllerMapping.getLeftUpperTrigger()))
    		this.gamepadData.setLeftWinker();
    	
    	if(this.checkButtonPressed(this.controllerMapping.getRightUpperTrigger()))
    		this.gamepadData.setRightWinker();
    	
    	if(gamepad.getType().equals(Controller.Type.STICK)){
			//PS3-Controller
	    	if(this.checkButtonPressed(PS3ControllerMapping.LeftLowerTrigger))
	    		this.gamepadData.gearDown();
	    	
	    	if(this.checkButtonPressed(PS3ControllerMapping.RightLowerTrigger))
	    		this.gamepadData.gearUp();
	    	
	    	if(this.checkButtonPressed(PS3ControllerMapping.ArrowUp))
	    		this.gamepadData.toggleFrontLights();
	    	
	    	if(this.checkButtonPressed(PS3ControllerMapping.ArrowDown))
	    		this.gamepadData.toggleBackLights();
	    	
	    	if(this.checkButtonPressed(PS3ControllerMapping.ArrowRight))
	    		this.gamepadData.toggleDynamicLights();
		}
    	else{
    		//XBOX-Controller
    		if(this.gamepad.getComponents()[XBoxControllerMapping.RzAxis].getPollData() < -0.8f && this.digitalAxes[3] != -1)
	    		this.gamepadData.gearUp();
	    	
	    	if(this.gamepad.getComponents()[XBoxControllerMapping.RzAxis].getPollData() > 0.8f && this.digitalAxes[3] != 1)
	    		this.gamepadData.gearDown();
	    	
	    	int pov = (int)(this.gamepad.getComponents()[XBoxControllerMapping.PoV].getPollData() * 100);
	    	
	    	switch(pov){
		    	case(25): this.gamepadData.toggleFrontLights();
		    	break;
		    	case(50): this.gamepadData.toggleDynamicLights();
		    	break;
		    	case(75): this.gamepadData.toggleBackLights();
		    	break;
	    	}
    	}
	}
	
	
	/**
	 * Retrieves latest gamepad-input and stores it in {@link #componentsPressed} and {@link #digitalAxes}.
	 */
	private void updateComponentRelatedArrays() {
		// refresh componentsPressed array
		for(int i = 0; i < this.controllerMapping.getNumberOfElements(); i++)
			this.componentsPressed[i] = (this.gamepad.getComponents()[i].getPollData() == 1);

		// refresh digitalAxes
		int digitalAxis = 0;
		for(int i = 0; i < this.digitalAxes.length; i++){
			switch(i){
				case(0): digitalAxis = this.controllerMapping.getXAxis();
				break;
				case(1): digitalAxis = this.controllerMapping.getYAxis();
				break;
				case(2): digitalAxis = this.controllerMapping.getZAxis();
				break;
				case(3): digitalAxis = this.controllerMapping.getRzAxis();
				break;
			}
			if(this.gamepad.getComponents()[digitalAxis].getPollData() < -0.8f)
				this.digitalAxes[i] = -1;
			else if(this.gamepad.getComponents()[digitalAxis].getPollData() > 0.8f)
				this.digitalAxes[i] = 1;
			else
				this.digitalAxes[i] = 0;
		}
	}
	
	
	@Override
	/**
	 * Updates all registered UIs with the latest gamepad status.
	 */
	protected void updateUIs(EIdentifier identifier, String message) {
		/*
		System.out.println("Throttle mode: " + (this.gamepadData.getThrottleMode() ? "buttons" : "thumbsticks"));
		System.out.println("Steering mode: " + (this.gamepadData.getSteeringMode() ? "gyro" : "thumbsticks"));
		System.out.println("Controlling device: " + (this.gamepadData.getGamepadControl() ? "gamepad" : "phone"));
		System.out.println("Steering: " + this.gamepadData.getSteeringAngle());
		System.out.println("Acceler.: " + this.gamepadData.getAcceleration());
		System.out.println("Gear: " + this.gamepadData.getGear());
		//System.out.println("controlsChanged is " + this.gamepadData.controlsChanged());
		System.out.println("\n");
		*/
		String text = new String();
		if(message != null)
			text = message;
		else {
			text = text.concat("Throttle mode: " + (this.gamepadData.getThrottleMode() ? "buttons\n" : "thumbsticks\n"));
			text = text.concat("Steering mode: " + (this.gamepadData.getSteeringMode() ? "gyro\n" : "thumbsticks\n"));
			text = text.concat("Controlling device: " + ((super.getConnectionId() == super.connectionIdInDriveControl) ? "gamepad\n" : "phone\n"));
			text = text.concat("Steering: " + this.gamepadData.getSteeringAngle() + "\n");
			text = text.concat("Acceler.: " + this.gamepadData.getAcceleration() + "\n");
			text = text.concat("Gear: " + this.gamepadData.getGear());
			//text.concat("controlsChanged is " + this.gamepadData.controlsChanged());
		}
		
		if(identifier == null) {
			for(IUI ui : super.getUIs())
				ui.update(EIdentifier.GAMEPADCONTROL, text);
		}
		else {
			for(IUI ui : super.getUIs())
				ui.update(identifier, text);
		}
	}
	
	
	/**
	 * Checks if the gamepad is in control of certain control-data-sets and publishes the corresponding control-data on the appropiate {@link LinkedBlockingQueue} if so.
	 */
	private void updateControls() {
		
		if(this.gamepadData.driveControlsChanged()) {
			if(super.connectionIdInDriveControl == super.getConnectionId())
				super.putControlData(new DriveControlData(this.gamepadData.getAcceleration(), this.gamepadData.getSteeringAngle()*(-1)));
			if(super.connectionIdInCameraControl == super.getConnectionId())
				super.putControlData(new CameraControlData(this.gamepadData.getAcceleration(), this.gamepadData.getSteeringAngle()));
			//super.putControlData(new CameraControlData(0, 0));
		}
		
		if(this.gamepadData.lightsChanged() && super.connectionIdInLightControl == super.getConnectionId()) {
			boolean[] lightData = new boolean[5];
			lightData[0] = this.gamepadData.frontLightsOn();
			lightData[2] = this.gamepadData.dynamicLightsOn();
			lightData[3] = this.gamepadData.leftWinkerOn();
			lightData[4] = this.gamepadData.rightWinkerOn();
			if(this.gamepadData.backLightsOn() || this.gamepadData.brakeOn())
				lightData[1] = true;
			else
				lightData[1] = false;
			
			super.putControlData(new LightControlData(lightData));
		}
	}
	
	
	@Override
	/**
	 * Starts the handling of the {@link #gamepad}s inputs:<br>
	 * First the gamepad is polled for new data.<br>
	 * Then that data is processed.<br>
	 * Last the UIs are updated and if applicable control-sets published on their corresponding {@link LinkedBlockingQueue}.
	 */
	public void run() {
		
		Long startedPressingThrottleModeChange = new Long(-1);
		Long startedPressingSteeringModeChange = new Long(-1);
		Long startedPressingControlChange = new Long(-1);
		Long startedPressingGamepadDriveControlChange = new Long(-1);
		Long startedPressingGamepadCameraControlChange = new Long(-1);
		long lastUIUpdate = 0;
		long lastControlUpdate = 0;
        
		while(!killRunnable) {
			
			this.gamepad.poll();
			
			// TODO this is not working as intented (copy by value) -> the values are not saved for the next iteration of the loop
			// trying it with Long -> should work, since it is a wrapper class and its value should therefore contain a reference and not the number itself
			detectAndProcessControlChanges(startedPressingThrottleModeChange, startedPressingSteeringModeChange, startedPressingControlChange,
					startedPressingGamepadDriveControlChange, startedPressingGamepadCameraControlChange);
			updateGamepadInputData();
	    	updateComponentRelatedArrays();
			
			if(System.currentTimeMillis() - lastControlUpdate > 40){
				updateControls();
				lastControlUpdate = System.currentTimeMillis();
			}
			
			if(System.currentTimeMillis() - lastUIUpdate > 100){
				updateUIs(null, null);
				lastUIUpdate = System.currentTimeMillis();
			}
	    	
			this.waitms(5);
		}
	}

}














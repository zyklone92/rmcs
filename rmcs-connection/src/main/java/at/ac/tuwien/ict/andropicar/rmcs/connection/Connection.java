package at.ac.tuwien.ict.andropicar.rmcs.connection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

import at.ac.tuwien.ict.andropicar.rmcs.data.CameraControlData;
import at.ac.tuwien.ict.andropicar.rmcs.data.IControlData;
import at.ac.tuwien.ict.andropicar.rmcs.data.IData;
import at.ac.tuwien.ict.andropicar.rmcs.data.DriveControlData;
import at.ac.tuwien.ict.andropicar.rmcs.data.EDataType;
import at.ac.tuwien.ict.andropicar.rmcs.data.LightControlData;
import at.ac.tuwien.ict.andropicar.rmcs.ui.IUI;
import at.ac.tuwien.ict.andropicar.rmcs.ui.EIdentifier;

/**
 * The Connection-class is a base-class for each and every external device that communicates with the RMCS and therefore the Raspberry-Pi.<br>
 * It provides basic tools for forwarding ControlData and making requests to change the controlling device of a certain car-component.
 * 
 * @author Boeck
 */
public abstract class Connection{
	
	/** static variable to ensure every connection has a unique id. First device has an ID of 1. */
	private static long id = 1;
	/** the ID of this connection. Required to handle connections in the main class. First device has an ID of 1. */
	private long connectionId;
	/** holds the ID of the connection that is currently controlling the drive-control-data of the RMCS. */
	protected long connectionIdInDriveControl;
	/** holds the ID of the connection that is currently controlling the light-control-data of the RMCS. */
	protected long connectionIdInLightControl;
	/** holds the ID of the connection that is currently controlling the camera-control-data of the RMCS. */
	protected long connectionIdInCameraControl;
//	/** The ID that the RMCS registers itself with on the Server. */
//	// TODO possibly not needed?
//	private long carId;
	/** the list of {@link IUI}s that want to know about the gamepads status */
	private Collection<IUI> uis = new ArrayList<>();
	/** the queue that is used to send ControlData to their corresponding interfaces. */
	private LinkedBlockingQueue<IData> controlDataQueue;
	/** the queue that is used to signal the main thread to change the connection that is currently under control of a specific control-data-set. */
	private LinkedBlockingQueue<ControlChangeQueueElement> controlChangeQueue; 
	
	
	/**
	 * A simple constructor with the minimal set of parameters to instantiate a working subclass of this class.<br>
	 * Sets {@link #connectionId} to the current value of {@link #id} and increments {@link #id}.<br>
	 * The connections that have control over the different control-data-sets cannot be set here and need to be set after instantiation,
	 * via {@link #updateControllingDevice(ControlChangeQueueElement)} or {link {@link #updateControllingDevice(long, EDataType)}.
	 * 
	 * @param carId The ID that the RMCS registers itself with on the Server.
	 * @param controlDataQueue the queue that is used to send ControlData to their corresponding interfaces.
	 * @param controlChangeQueue the queue that is used to signal the main class to change the connection that is currently under control of a specific control-data-set.
	 */
	public Connection(long carId, LinkedBlockingQueue<IData> controlDataQueue, LinkedBlockingQueue<ControlChangeQueueElement> controlChangeQueue){
		this.connectionId = id++;
		this.connectionIdInDriveControl = 0;
		this.connectionIdInLightControl = 0;
		this.connectionIdInCameraControl = 0;
		//this.carId = carId;
		this.controlDataQueue = controlDataQueue;
		this.controlChangeQueue = controlChangeQueue;
	}
	
	/**
	 * A constructor with the maximum set of parameters to instantiate a working subclass of this class.<br>
	 * Sets {@link #connectionId} to the current value of {@link #id} and increments {@link #id}.<br>
	 * The connections that have control over the different control-data-sets are set via their corresponding parameters.
	 * 
	 * @param carId The ID that the RMCS registers itself with on the Server.
	 * @param controlDataQueue the queue that is used to send ControlData to their corresponding interfaces.
	 * @param controlChangeQueue the queue that is used to signal the main class to change the connection that is currently under control of a specific control-data-set.
	 * @param connectionIdInDriveControl the ID of the connection that is currently controlling the drive-control-data of the RMCS.
	 * @param connectionIdInLightControl the ID of the connection that is currently controlling the light-control-data of the RMCS.
	 * @param connectionIdInCameraControl the ID of the connection that is currently controlling the camera-control-data of the RMCS.
	 */
	public Connection(long carId, LinkedBlockingQueue<IData> controlDataQueue, LinkedBlockingQueue<ControlChangeQueueElement> controlChangeQueue, long connectionIdInDriveControl, long connectionIdInLightControl, long connectionIdInCameraControl){
		this.connectionId = id++;
		this.connectionIdInDriveControl = connectionIdInDriveControl;
		this.connectionIdInLightControl = connectionIdInLightControl;
		this.connectionIdInCameraControl = connectionIdInCameraControl;
		//this.carId = carId;
		this.controlDataQueue = controlDataQueue;
		this.controlChangeQueue = controlChangeQueue;
	}
	
	
	/**
	 * @return this connections ID.
	 */
	public long getConnectionId(){
		return this.connectionId;
	}
	
	/**
	 * @return the list of UIs that are registered with this object.
	 */
	protected Collection<IUI> getUIs(){
		return this.uis;
	}
	
	/**
	 * Updates all registered UIs with the latest status of this connection.
	 * @param identifier the topic of the message.
	 * @param message the message that the UIs should be updated with.
	 */
	protected abstract void updateUIs(EIdentifier identifier, String message);
	
	
	/**
	 * Registers a new UI.
	 * @param ui the UI to be registered.
	 */
	public void registerUI(IUI ui) {
		this.uis.add(ui);
	}
	
	
	/**
	 * Used to update {@link #connectionIdInDriveControl}, {@link #connectionIdInDriveControl} and {@link #connectionIdInDriveControl}, depending on the dataType that was passed.
	 * 
	 * @param connectionIdInControl the new ID that is currently in control of the specified control-data.
	 * @param dataType the control-data-type the specified ID is currently in control of.
	 * @return true, if the passed dataType is valid and the corresponding control-ID updated, otherwise false.
	 */
	public boolean updateControllingDevice(long connectionIdInControl, EDataType dataType){
		System.out.println("Processing new incoming controlchangeRequest!");
		if(connectionIdInControl < 1)
			return false;
		System.out.println("Connection processed controlChangeRequest. New ID in Control: " + connectionIdInControl);
		
		switch(dataType){
		case DRIVECONTROL:
			this.connectionIdInDriveControl = connectionIdInControl;
			break;
		case LIGHTCONTROL:
			this.connectionIdInLightControl = connectionIdInControl;
			break;
		case CAMERACONTROL:
			this.connectionIdInCameraControl = connectionIdInControl;
			break;
		default:
			return false;
		}
		return true;
	}
	
	
	/**
	 * Used to update {@link #connectionIdInDriveControl}, {@link #connectionIdInDriveControl} and {@link #connectionIdInDriveControl}, depending on the dataType that was passed.
	 * 
	 * @param queueElement contains information about device in control and type of control-data.
	 * @return true, if the passed dataType in the queueElement is valid and the corresponding control-ID updated, otherwise false.
	 */
	public boolean updateControllingDevice(ControlChangeQueueElement queueElement){
		if(queueElement == null)
			return false;
		return updateControllingDevice(queueElement.getId(), queueElement.getControlChangeType());
	}
	
	
	/**
	 * Informs the main-thread of a change in control of a certain type of control-data via the {@link #controlChangeQueue}.
	 * 
	 * @param connectionId the ID that should be in control of the specified control-data.
	 * @param dataType the control-data-type the specified ID should be in control of.
	 * @return true, if the {@link #controlChangeQueue} was successfully updated, otherwise false.
	 */
	protected boolean changeControllingDevice(long connectionId, EDataType dataType){
		if(connectionId <= 0)
			return false;
		
		try{
			controlChangeQueue.put(new ControlChangeQueueElement(connectionId, dataType));
			return true;
		} catch(InterruptedException ie){
			ie.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * Checks, if this object is in control of the passed control-data-type and if so, tries to update the {@link #controlDataQueue} with the passed {@link IControlData}.
	 * 
	 * @param data the element that the {@link #controlDataQueue} should be updated with.
	 * @return true, if this object is in control of the passed {@link IControlData} and the {@link #controlDataQueue} was successfully updated.
	 */
	protected boolean putControlData(IControlData data){
		if(data == null)
			return false;
		
		if((data instanceof DriveControlData && this.connectionIdInDriveControl == this.connectionId) || 
				(data instanceof LightControlData && this.connectionIdInLightControl == this.connectionId) || 
				(data instanceof CameraControlData && this.connectionIdInCameraControl == this.connectionId)) {
			try{
				controlDataQueue.put(data);
				return true;
			} catch(InterruptedException ie){
				ie.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * Helper-method to remove overhead from other places, where a specified amount of milliseconds should be waited.
	 * @param ms the amount of milliseconds that should be waited.
	 */
    protected void waitMs(int ms) {
        try{
            wait(ms);
        } catch(InterruptedException ie) {
            System.out.println("Could not sleep for the specified time of " + ms + " ms.");
        }
    }

}

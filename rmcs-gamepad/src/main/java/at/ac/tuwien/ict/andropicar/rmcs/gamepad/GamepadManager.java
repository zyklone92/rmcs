package at.ac.tuwien.ict.andropicar.rmcs.gamepad;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import at.ac.tuwien.ict.andropicar.rmcs.connection.ControlChangeQueueElement;
import at.ac.tuwien.ict.andropicar.rmcs.data.IData;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Rumbler;
import at.ac.tuwien.ict.andropicar.rmcs.ui.IUI;


/**
 * Searches for gamepads in the list of connected input-devides upon instantiation,
 * instantiates {@link GamepadConnection}s for each one and adds those to {@link #gamepadConnections}.<br>
 * It also provides methods to find Gamepads, print a list of all Gamepads and test a Gamepad.<br>
 * Listeners are able to register themselves with the ServerConnection to get updated about sending and receiving data as well as connection updates.
 * 
 * @author Boeck
 *
 */
public class GamepadManager {

	/** the list of all connected gamepads */
	private volatile LinkedList<Controller> gamepads = new LinkedList<>();
	/** the list of all gamepad-connections */
	private Collection<GamepadConnection> gamepadConnections = new ArrayList<>();
	/** the list of {@link IUI}s that want to be know about the gamepads status */
	private Collection<IUI> uis = new ArrayList<>();
	/** the ID that the RMCS registers itself with on the Server. */
	private long carId;
	/** the queue that is used to send ControlData to their corresponding interfaces. */
	private LinkedBlockingQueue<IData> controlDataQueue;
	/** the queue that is used to signal the main thread to change the connection that is currently under control of a specific control-data-set. */
	private LinkedBlockingQueue<ControlChangeQueueElement> controlChangeQueue;
	/** true, if this thread is finished initializing all gamepads, otherwise false */
	private boolean ready = false;
	
	
	/**
	 * @param carId the ID that the RMCS registers itself with on the Server.
	 * @param controlDataQueue the queue that is used to send ControlData to their corresponding interfaces.
	 * @param controlChangeQueue the queue that is used to signal the main class to change the connection that is currently under control of a specific control-data-set.
	 */
	public GamepadManager(long carId, LinkedBlockingQueue<IData> controlDataQueue, LinkedBlockingQueue<ControlChangeQueueElement> controlChangeQueue) {
		//this.firstConnectionId = firstConnectionId;
		this.carId = carId;
		this.controlDataQueue = controlDataQueue;
		this.controlChangeQueue = controlChangeQueue;
		initializeGamepads();
	}
	
	
	/**
	 * Registers a new UI.
	 * 
	 * @param ui the UI to be registered.
	 */
	public void registerUI(IUI ui) {
		this.uis.add(ui);
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
	 * @return true, if this thread is finished initializing all gamepads, otherwise false.
	 */
	public boolean isReady(){
		return this.ready;
	}
	
	
	/**
	 * @return the list of all gamepad-connections
	 */
	public Collection<GamepadConnection> getgamepadConnections() {
		return this.gamepadConnections;
	}

	
	/**
	 * Prints out a list of all connected gamepads and their components.
	 */
	public void printGamepads() {
		
		Controller[] ca = ControllerEnvironment.getDefaultEnvironment().getControllers();

        for(int i =0;i<ca.length;i++){
            if(ca[i].getType() == Controller.Type.GAMEPAD || ca[i].getType().equals(Controller.Type.STICK)){
                /* Get the name of the controller */
                System.out.println("\n" + ca[i].getName());
                System.out.println("Type: "+ca[i].getType().toString());
            	
            	Component[] components = ca[i].getComponents();
                for(int j=0;j<components.length;j++){
                    /* Get the components name */
                    System.out.println("  Component "+j+": "+components[j].getName());
                    System.out.println("    Identifier: "+ components[j].getIdentifier().getName());
                }
                
                Rumbler[] rumblers = ca[i].getRumblers();
                	for(Rumbler rum : rumblers){
	                	System.out.println(rum.toString());
	    				System.out.println(rum.getAxisName());
	    				System.out.println(rum.getAxisIdentifier());
	                }
                if (rumblers.length < 1)
                	System.out.println("No rumblers on this controller!");
            	System.out.println("\nThis is what we will be using, later on!");
            }
        }
	}
	
	
	/**
	 * Searches through the list of connected input-devices and returns the first one that matches the passed name.
	 * 
	 * @param controllerName the name of the device to be found.
	 * @return the device with the specified name.
	 */
	public Controller findController(String controllerName) {
		
		Controller[] ca = ControllerEnvironment.getDefaultEnvironment().getControllers();

        for(int i =0;i<ca.length;i++){
        	if(ca[i].getName().equals(controllerName))
        		return ca[i];
        }
        return null;
	}
	
	
	/**
	 * Searches through the list of connected input-devices and returns the first one that matches the passed type.
	 * 
	 * @param type the type of the device to be found.
	 * @return the device with the specified type.
	 */
	public Controller findController(Controller.Type type) {
		
		Controller[] ca = ControllerEnvironment.getDefaultEnvironment().getControllers();

        for(int i =0;i<ca.length;i++){
        	if(ca[i].getType().equals(type))
        		return ca[i];
        }
        return null;
	}
	
	
	/**
	 * Searches through the list of connected input-devices and returns the first one that identifies as a gamepad (Controller.Type.GAMEPAD or Controller.Type.STICK).
	 * 
	 * @return the first gamepad that was found.
	 */
	public Controller findController() {
		
		// TODO cleanup
		this.waitms(100);
		Controller[] ca = ControllerEnvironment.getDefaultEnvironment().getControllers();
		System.out.println(ca.length);
		this.waitms(100);
		ca = ControllerEnvironment.getDefaultEnvironment().getControllers();
		System.out.println(ca.length);
        for(int i =0;i<ca.length;i++){
            if(ca[i].getType() == Controller.Type.GAMEPAD || ca[i].getType().equals(Controller.Type.STICK)){
                /* Get the name of the controller */
                System.out.println("\n" + ca[i].getName());
                System.out.println("Type: "+ca[i].getType().toString());
            	
            	Component[] components = ca[i].getComponents();
                for(int j=0;j<components.length;j++){
                    /* Get the components name */
                    System.out.println("  Component "+j+": "+ components[j].getName());
                    System.out.println("    Identifier: "+ components[j].getIdentifier().getName());
                }
            	System.out.println("This is what we will be using, later on!");
            	return ca[i];
            }
        }
        return null;
	}
	
	
	 /** 
	 * @return the number of connected gamepads.
	 */
	public int getNumberOfAvailableGamepads() {
		int numberOfAvailableGamepads = 0;
		Controller[] inputDevices = ControllerEnvironment.getDefaultEnvironment().getControllers();
		for(int i = 0; i < inputDevices.length; i++){
			if(inputDevices[i].getType() == Controller.Type.GAMEPAD || inputDevices[i].getType().equals(Controller.Type.STICK))
				numberOfAvailableGamepads++;
		}
		
		return numberOfAvailableGamepads;
	}
	
	
	/**
	 * Searches through the list of connected input-devices and returns all that identify as a gamepad (Controller.Type.GAMEPAD or Controller.Type.STICK).
	 * 
	 * @return the list of available gamepads.
	 */
	public LinkedList<Controller> findGamepads() {
		
		// TODO cleanup
		this.waitms(100);
		Controller[] ca = ControllerEnvironment.getDefaultEnvironment().getControllers();
		System.out.println(ca.length);
		this.waitms(100);
		ca = ControllerEnvironment.getDefaultEnvironment().getControllers();
		System.out.println(ca.length);
		
		LinkedList<Controller> gamepads = new LinkedList<>();
        for(int i =0;i<ca.length;i++){
            if(ca[i].getType() == Controller.Type.GAMEPAD || ca[i].getType().equals(Controller.Type.STICK)){
                /* Get the name of the controller */
                System.out.println("\n" + ca[i].getName());
                System.out.println("Type: "+ca[i].getType().toString());
            	
            	Component[] components = ca[i].getComponents();
                for(int j=0;j<components.length;j++){
                    /* Get the components name */
                    System.out.println("  Component "+j+": "+ components[j].getName());
                    System.out.println("    Identifier: "+ components[j].getIdentifier().getName());
                }
            	System.out.println("Found a new gamepad!");
            	gamepads.add(ca[i]);
            }
        }
        return gamepads;
	}
	
	
	/**
	 * @return true, if at least one gamepad is connected to the PI.
	 */
	public boolean gamepadAvailable() {
		
		Controller[] ca = ControllerEnvironment.getDefaultEnvironment().getControllers();

        for(int i =0;i<ca.length;i++){
        	if(ca[i].getType().equals(Controller.Type.GAMEPAD) || ca[i].getType().equals(Controller.Type.STICK))
        		return true;
        }
        return false;
	}
	
	
	/**
	 * Searches for a gamepad in the list of connected input-devices and outputs the status of all its components every 200 milliseconds.
	 */
	public void testControls(){
		
		Controller gamepad = this.findController();
		long lastPrinted = System.currentTimeMillis();
		
		while(gamepads != null){
        	gamepad.poll();
			if(System.currentTimeMillis() - lastPrinted > 200)
			{
	    		for(int j=0;j<gamepad.getComponents().length;j++){    
	                System.out.println(""+j+"  "+gamepad.getComponents()[j].getName() + ": " + gamepad.getComponents()[j].getPollData());
	            }
				System.out.println("\n");
				lastPrinted = System.currentTimeMillis();
			}
        }
	}

	/**
	 * Searches for gamepads in the list of connected input-devides, instantiates {@link GamepadConnection}s for each one and adds those to {@link #gamepadConnections}.<br>
	 * Sets {@link #ready} to true, to signal that all GamepadConnections are setup and ready.
	 */
	private void initializeGamepads(){
		
		// TODO continuously search for a controller
		
		this.gamepads = this.findGamepads();
		
		if(this.gamepads.size() == 1){
			GamepadConnection gamepadConnection = new GamepadConnection(this.carId, this.controlDataQueue, this.controlChangeQueue, gamepads.get(0), true); 
			this.gamepadConnections.add(gamepadConnection);
			for(IUI ui : this.uis)
				gamepadConnection.registerUI(ui);
			(new Thread(gamepadConnection, "Gamepad-Connection " + 1)).start();
		}
		else{
			for(int i = 0; i < this.gamepads.size(); i++){
				GamepadConnection gamepadConnection = new GamepadConnection(this.carId, this.controlDataQueue, this.controlChangeQueue, gamepads.get(i)); 
				this.gamepadConnections.add(gamepadConnection);
				for(IUI ui : this.uis)
					gamepadConnection.registerUI(ui);
				(new Thread(gamepadConnection, "Gamepad-Connection " + i+1)).start();
			}
		}

		this.ready = true;
	}
	
	
	
	
}

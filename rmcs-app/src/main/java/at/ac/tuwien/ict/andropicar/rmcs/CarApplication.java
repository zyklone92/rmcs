package at.ac.tuwien.ict.andropicar.rmcs;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import at.ac.tuwien.ict.andropicar.rmcs.connection.Connection;
import at.ac.tuwien.ict.andropicar.rmcs.connection.ControlChangeQueueElement;
import at.ac.tuwien.ict.andropicar.rmcs.gamepad.GamepadConnection;
import at.ac.tuwien.ict.andropicar.rmcs.gamepad.GamepadManager;
import at.ac.tuwien.ict.andropicar.rmcs.network.Keywords;
import at.ac.tuwien.ict.andropicar.rmcs.network.ServerConnection;
import at.ac.tuwien.ict.andropicar.rmcs.stm32.RMCSComponentManager;
import at.ac.tuwien.ict.andropicar.rmcs.data.EDataType;
import at.ac.tuwien.ict.andropicar.rmcs.stm32.STM32SpiInterface;
import at.ac.tuwien.ict.andropicar.rmcs.stm32.STM32UartInterface;
import at.ac.tuwien.ict.andropicar.rmcs.data.IData;
import at.ac.tuwien.ict.andropicar.rmcs.ui.ConsoleUI;


/**
 * This is the RMCSs (Racecar-Mobility-Control-System) main class, where all needed classes are being instantiated and corresponding threads are started.<br>
 * It also processes all the control-change-events (via putting an element on the {@link CarApplication#controlChangeQueue}) and updates all connections in {@link CarApplication#connections}.<br>
 * A set of arguments can be entered upon start, which is processed by {@link CarApplication#processArguments}.
 * 
 * @author Boeck
 */
public class CarApplication{

	/** The servers default IP-address */
	private String serverIp = "192.168.1.100";
	
	/** The cars ID that is used to register on the Server. */
	private long carId = 1001;
	
	/** The list of this cars properties. */
	private ArrayList<String> properties = new ArrayList<>();
	
	/** The object that handles the connection with the TCP-Server. */
	private ServerConnection serverConnection;
	
	/** The object that handles all {@link GamepadConnection}s. */
	private GamepadManager gamepadManager;
	
	/** The list of all {@link Connection}s. */
	private Collection<Connection> connections = new ArrayList<>();
	
	/** The object that handles the SPI-connection to the STM32. */
	private STM32SpiInterface stmSpiConnection;
	
	/** The object that handles the UART-connection to the STM32. */
	private STM32UartInterface stmUartConnection;
	//private Collection<CarInterface> carInterfaces = new ArrayList<>();		// not really needed
	
	/** The {@link LinkedBlockingQueue} where all requests to change control are put. */
	private LinkedBlockingQueue<ControlChangeQueueElement> controlChangeQueue = new LinkedBlockingQueue<>();
	
	/** The object that handles all of the cars different hardware modules. */
	private RMCSComponentManager carInterfaceManager;
	
	/** The UI that displays all relevant information. */
	private ConsoleUI ui = new ConsoleUI();
	
	
	private CarApplication() {
		//add a Shutdown hook to be able to close the connection, when program is closed
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				if(serverConnection != null)
					serverConnection.closeConnection();
			}
		});
	}
	
	
	/**
	 * The method where all needed classes are being instantiated and corresponding threads are started.<br>
	 * If the RMCS does not have sensors, it will not try to establish a UART-connection with the STM32-board, 
	 * hence there will not be an instance of {@link STM32UartInterface}.<br>
	 * If the RMCS does have sensors, there will be an instance of {@link STM32UartInterface}
	 * which will lead to the inability to use the bluetooth module and the RMCS will not be able to be controlled by a PS3-gamepad.
	 * 
	 * @param args the list of arguments that were passed upon starting the RMCS.
	 */
	private void initialize(String[] args){
		
		// sensor-update-interval in milliseconds
		int sensorUpdateInterval = 500;
		LinkedBlockingQueue<IData> dataQueue = new LinkedBlockingQueue<>();
		
		// serverConnection must be instantiated first, so that it has an ID of 1 (important for the gamepadConnections)
		this.serverConnection = new ServerConnection(dataQueue, this.controlChangeQueue, this.serverIp, 6633, this.carId, this.properties);
		this.serverConnection.registerUI(this.ui);
		(new Thread(this.serverConnection, "Server-Connection")).start();
		this.connections.add(this.serverConnection);
		
		// gamepadManager must be instantiated last, so that the last registered gamepad has the highest id (important for controlChanges between gamepads)
		this.gamepadManager = new GamepadManager(this.carId, dataQueue, this.controlChangeQueue);
		this.gamepadManager.registerUI(this.ui);
		
		while(!this.gamepadManager.isReady()){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
		}
		System.out.println("Total number of GamepadConnection: " + this.gamepadManager.getNumberOfAvailableGamepads());
		System.out.println("Total number of GamepadConnections: " + this.gamepadManager.getgamepadConnections().size());
		for(Connection connection : this.gamepadManager.getgamepadConnections())
			this.connections.add(connection);
		
		boolean hasSensors = processArguments(args);
		carInterfaceManager = new RMCSComponentManager(dataQueue);
		
		Collection<EDataType> spiControlTypes = new ArrayList<>();
		spiControlTypes.add(EDataType.DRIVECONTROL);
		spiControlTypes.add(EDataType.CAMERACONTROL);
		spiControlTypes.add(EDataType.LIGHTCONTROL);
		this.stmSpiConnection = new STM32SpiInterface(spiControlTypes, sensorUpdateInterval);
		this.carInterfaceManager.addCarComponent(this.stmSpiConnection);
		(new Thread(this.stmSpiConnection, "STM32-SPI-Connection")).start();
		
		if(hasSensors){
			this.stmUartConnection = new STM32UartInterface(dataQueue, sensorUpdateInterval);
			//this.carInterfaceManager.addCarComponent(this.stmUartConnection);	// this doesn't make sense, wrong usage
		}
		
		// not yet needed
		//this.carInterfaces.add(stmSpiConnection);
		//this.carInterfaces.add(stmUartConnection);

		this.carInterfaceManager.addDataListener(this.serverConnection, EDataType.SENSOR);
		(new Thread(this.carInterfaceManager, "Car-Interface-Manager")).start();
	}
	
	/**
	 * A set of arguments can be entered upon start, which will be processed by this method.<br>
	 * A list of possible arguments can be shown by starting the application with the help-flag (-h or --help).
	 * @param args the list of Strings that are passed along upon application-start.
	 * @return true, if the RMCS has sensors, otherwise false.
	 */
	private boolean processArguments(String[] args){
		
		boolean hasSensors = false;
		
		// create the Options
		Options options = new Options();
		options.addOption("S", "server-ip", true, "the servers ip-adress. a range of 20 addresses will be tried repeatedly, continuously counting upwards upon failure. default value: 192.168.1.100");
		options.addOption("I", "id", true, "the id this car should have and use for identification at the server. default value: 1001");
		options.addOption("c", "camera", false, "tells the application to start the camera stream upon startup and add camera to the list of properties");
		options.addOption("l", "lights", false, "tells the application to add lihgts to the list of properties");
		options.addOption("w", "winkers", false, "tells the application to add winkers to the list of properties");
		options.addOption("s", "sensors", false, "tells the application to add all known sensors to the list of properties");
		options.addOption("", "frontDistanceSensor", false, "tells the application to add a front distance-sensor to the list of properties");
		options.addOption("", "leftDistanceSensor", false, "tells the application to add a left-side distance-sensor to the list of properties");
		options.addOption("", "rightDistanceSensor", false, "tells the application to add a right-side distance-sensor to the list of properties");
		options.addOption("", "velocitySensor", false, "tells the application to add a velocity-sensor to the list of properties");
		options.addOption("g", "gamepad", false, "tells the application to prioritize the gamepad for control upon startup");
		options.addOption("", "show-gamepads", false, "prints a list of all connected gamepads and their buttons / sticks / triggers. Application will exit afterwards");
		options.addOption("", "print-gamepads", false, "prints a list of all connected gamepads and their buttons / sticks / triggers. Application will exit afterwards");
		options.addOption("", "test-gamepad", false, "allows to test all the controls of the first gamepad, the application finds. this option only supports one gamepad");
		options.addOption("h", "help", false, "prints this message");
		
		try{
			// create the command line parser
			CommandLineParser clp = new DefaultParser();
			// parse the command line arguments
			CommandLine cl = clp.parse(options, args);
			
			// process command line arguments
			
			if(cl.hasOption("server-ip")){
				try {
					// check if the entered IP-address is valid
					new InetSocketAddress(cl.getOptionValue("server-ip"), 1);
					this.serverIp = cl.getOptionValue("server-ip");
				} catch(IllegalArgumentException iae){
					System.out.println("Invalid server-IP-address entered. Default IP will be used (192.168.1.100).");
				}
			}
			if(cl.hasOption("id")){
				try{
					// check if the entered ID is valid
					long id = Long.parseLong(cl.getOptionValue("id"));
					if(id > 0)
						this.carId = id;
				}catch(NumberFormatException nfe){
					System.out.println("Invalid car-ID entered. Default ID will be used (1001).");
				}
			}
			if(cl.hasOption("camera")){
				this.properties.add("camera");
				try{
					// TODO this is going to be changed to something different?
					// start the camera stream
					Process cameraProcess = Runtime.getRuntime().exec(new String[] 
							{"/bin/sh", "-c", "/usr/bin/raspivid -n -ih -t 0 -rot 180 -w 640 -h 480 -roi 0.25,0.25,0.5,0.5 -fps 60 -b 8000000 -o - | /bin/nc -lkv4 5001"});
					// pipe the streams of the above command to the corresponding standard-streams of this application
					new Thread(new SyncPipe(cameraProcess.getErrorStream(), System.err)).start();
				    new Thread(new SyncPipe(cameraProcess.getInputStream(), System.out)).start();
				} catch(IOException ioe){
					System.out.println("Error starting the camera stream!\n" + ioe.getMessage());
				}
			}
			if(cl.hasOption("lights"))
				this.properties.add("lights");
			if(cl.hasOption("winkers"))
				this.properties.add("winkers");
			if(cl.hasOption("sensors")) {
				this.properties.add(Keywords.ultrasonicSensor);
				this.properties.add(Keywords.leftInfraredSensor);
				this.properties.add(Keywords.rightInfraredSensor);
				this.properties.add(Keywords.hallSensor);
				hasSensors = true;
			}
			else {
				if(cl.hasOption("frontDistanceSensor")) {
					this.properties.add(Keywords.ultrasonicSensor);
					hasSensors = true;
				}
				if(cl.hasOption("leftDistanceSensor")) {
					this.properties.add(Keywords.leftInfraredSensor);
					hasSensors = true;
				}
				if(cl.hasOption("rightDistanceSensor")) {
					this.properties.add(Keywords.rightInfraredSensor);
					hasSensors = true;
				}
				if(cl.hasOption("velocitySensor")) {
					this.properties.add(Keywords.hallSensor);
					hasSensors = true;
				}
			}
			if(cl.hasOption("gamepad")){
				try{
					this.controlChangeQueue.put(new ControlChangeQueueElement(this.connections.size()-this.gamepadManager.getNumberOfAvailableGamepads()+1, EDataType.DRIVECONTROL));
					this.controlChangeQueue.put(new ControlChangeQueueElement(this.connections.size()-this.gamepadManager.getNumberOfAvailableGamepads()+1, EDataType.CAMERACONTROL));
					this.controlChangeQueue.put(new ControlChangeQueueElement(this.connections.size()-this.gamepadManager.getNumberOfAvailableGamepads()+1, EDataType.LIGHTCONTROL));
				} catch(InterruptedException ie){
					ie.printStackTrace();
				}
			}
			else{
				try{
					this.controlChangeQueue.put(new ControlChangeQueueElement(1, EDataType.DRIVECONTROL));
					this.controlChangeQueue.put(new ControlChangeQueueElement(1, EDataType.CAMERACONTROL));
					this.controlChangeQueue.put(new ControlChangeQueueElement(1, EDataType.LIGHTCONTROL));
				} catch(InterruptedException ie){
					ie.printStackTrace();
				}
			}
			if(cl.hasOption("show-gamepads") || cl.hasOption("print-gamepads")){
				this.gamepadManager.printGamepads();
				System.exit(0);
			}
			if(cl.hasOption("test-gamepad")){
				this.gamepadManager.testControls();
				System.exit(0);
			}
			if(cl.hasOption("help")){
				// generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "java -jar FilePath [OPTIONS]", options );
			}
			
		} catch(ParseException pe){
			pe.printStackTrace();
		}
		
		return hasSensors;
	}
	
	
	/**
	 * Processes all the control-change-events (via putting an element on the {@link CarApplication#controlChangeQueue}) and updates all connections in {@link CarApplication#connections}.<br>
	 * If the id of passed {@link ControlChangeQueueElement} exceeds the size of {@link #connections} or equals -1, the first {@link GamepadConnection} is set as the controlling device.
	 * If no gamepad is connected to the RMCS, the phone will be set as the controlling device.
	 * @param controlChangeQueueElement contains the next {@link ControlChangeQueueElement} to be processed
	 */
	private void processControlChangeQueueElement(ControlChangeQueueElement controlChangeQueueElement){
		if(controlChangeQueueElement.getId() > this.connections.size() || controlChangeQueueElement.getId() == -1){
			// new ID to control the car was set to an invalid value, phone wants to pass control to a gamepad, or
			// the gamepad with the highest ID wants to pass control on to the next gamepad (the one with the lowest ID in this case)
			// in either case the control is passed on to the gamepadConnection with the lowest ID, unless there is no gamepad connected,
			// in which case the phone is set as the controlling device.
			long firstGamepadConnectionId = 1;	// this is actually the phones ID
			if(this.gamepadManager.getNumberOfAvailableGamepads() != 0)
				firstGamepadConnectionId = this.connections.size()-this.gamepadManager.getNumberOfAvailableGamepads()+1; // now it is set to the first gamepads ID
			for(Connection connection : this.connections)
				connection.updateControllingDevice(firstGamepadConnectionId, controlChangeQueueElement.getControlChangeType());
		}
		else{
			if(controlChangeQueueElement.getId() < 1) {
				System.err.println("Invalid control-change-request-id!!!");
				return;
			}
			for(Connection connection : this.connections)
				connection.updateControllingDevice(controlChangeQueueElement);
		}
	}

	
	/**
	 * Starts the RMCS.
	 * @param args the list of Strings that are passed along upon application-start
	 */
	public void run(String[] args){

		initialize(args);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		
		while(this.controlChangeQueue != null){
			try{
				processControlChangeQueueElement(this.controlChangeQueue.take());
				System.out.println("Processed new ControlChangeEvent!!!\n");
			} catch(InterruptedException ie){
				ie.printStackTrace();
			}
		}
	}
	
	
	
	public static void main(String[] args){
		
		CarApplication carApp = new CarApplication();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {}
		carApp.run(args);
		
	}

}

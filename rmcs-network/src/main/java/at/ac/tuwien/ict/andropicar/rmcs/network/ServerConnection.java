package at.ac.tuwien.ict.andropicar.rmcs.network;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import at.ac.tuwien.ict.andropicar.rmcs.connection.ISensorDataListener;
import at.ac.tuwien.ict.andropicar.rmcs.data.CameraControlData;
import at.ac.tuwien.ict.andropicar.rmcs.data.IData;
import at.ac.tuwien.ict.andropicar.rmcs.data.DriveControlData;
import at.ac.tuwien.ict.andropicar.rmcs.data.EDataType;
import at.ac.tuwien.ict.andropicar.rmcs.data.LightControlData;
import at.ac.tuwien.ict.andropicar.rmcs.data.SensorData;
import at.ac.tuwien.ict.andropicar.rmcs.data.VelocitySensorData;
import at.ac.tuwien.ict.andropicar.rmcs.data.DistanceSensorData;
import at.ac.tuwien.ict.andropicar.json.JSONDecoder;
import at.ac.tuwien.ict.andropicar.rmcs.ui.EIdentifier;
import at.ac.tuwien.ict.andropicar.rmcs.ui.IUI;
import at.ac.tuwien.ict.andropicar.rmcs.connection.Connection;
import at.ac.tuwien.ict.andropicar.rmcs.connection.ControlChangeQueueElement;


/**
 * This class is used to communicate with a TCP-Server via a TCP-connection and
 * is able to send sensor-data to the server and receive control-data from the server to control the car.<br>
 * UI-listeners are able to register themselves with the ServerConnection to get updated about sending and receiving data as well as connection updates.
 * 
 * @author Boeck
 */
public class ServerConnection extends Connection implements ISensorDataListener, Runnable {
	
	/** The cars ID. */
	private long carId;
	
	/** Holds the connection itself. */
	private Socket connection;
	
	/** The inputStream to read from the connected device. */
	private InputStreamReader inputStream;
	
	/** The output-stream to write to the connected device. */
	private OutputStreamWriter outputStream;
	
	/** The servers address. */
	private InetSocketAddress address;
	
	/** The list of the cars properties (such as lights, winkers, sensors, etc.) */
	private Collection<String> properties;
	
	/** The DataQueue that is used to inform this Runnable about new {@link DistanceSensorData} */
	protected LinkedBlockingQueue<SensorData> sensorDataQueue;
	
	
	/**
	 * Instantiates a new object of this class with the given parameters, and tries to create a valid InetSocketAddress.
	 * If that fails, a default IP-address of 192.168.1.100 and port 6633 are used to connect to the TCP-Server.
	 * @param controlDataQueue the queue that is used to send ControlData to their corresponding interfaces.
	 * @param controlChangeQueue the queue that is used to signal the main class to change the connection that is currently under control of a specific control-data-set.
	 * @param serverAddress the servers IP-address.
	 * @param port the port on which to connect to the server.
	 * @param carId the ID that the RMCS registers itself with on the Server.
	 * @param properties the list of properties of this car.
	 */
	public ServerConnection(LinkedBlockingQueue<IData> controlDataQueue, LinkedBlockingQueue<ControlChangeQueueElement> controlChangeQueue, 
			String serverAddress, int port, long carId, Collection<String> properties) {
		super(carId, controlDataQueue, controlChangeQueue);
		this.properties = properties;
		this.carId = carId;
		this.sensorDataQueue = new LinkedBlockingQueue<>();
		
		try {
			this.address = new InetSocketAddress(serverAddress, port);
		} catch(Exception e) {}
		if(this.address == null)
			this.address = new InetSocketAddress("192.168.1.100", 6633);
	}
	
    
	@Override
	protected void updateUIs(EIdentifier identifier, String message) {
		for(IUI ui : this.getUIs())
			ui.update(identifier, message);
	}
	
	/**
	 * Tries to gracefully close the TCP-Connection.
	 */
	public void closeConnection() {
		if(this.outputStream!= null)
		{
			try {
				this.outputStream.close();
			} catch(IOException ioe) {}
		}
		if(this.inputStream!= null)
		{
			try {
				this.inputStream.close();
			} catch(IOException ioe) {}
		}
		if(this.connection != null)
		{
			try {
				this.connection.close();
			} catch(IOException ioe) {}
		}
	}
	
	/**
	 * Tries to establish a connection with the TCP-Server.
	 * If a connection on the specified IP-address cannot be established, the IP-address is incremented by one and a connection is tried to be established again.
	 * If the IP-address has been incremented 20 times, it is set back to its initial value and the process starts over until a connection can be established. 
	 */
	private synchronized void connectToServer() {
		// check if there are remnants of previous connections and clean up
		this.closeConnection();
		this.waitMs(500);
		
		InetSocketAddress currentAddress = this.address;
		
		while(true)
		{
			// try 20 consecutive IP-Addresses to connect to the server
			for(int i = 0; i < 20; i++)
			{
				this.waitMs(5);
				String text = new String();
				
				text = text.concat("Trying to connect on IP-Address " + currentAddress.getAddress().getHostAddress() + "\n");
				
				// if connectToServer does not return null, we are connected and done.
				InetSocketAddress tempAddress;
				if((tempAddress = this.connectToServer(currentAddress)) != null)
				{
					this.address = tempAddress;
					return;
				}
				
				// could not connect to Server on current IP-address -> increase IP-address
				try
				{
					byte[] incrementedAddress = currentAddress.getAddress().getAddress();
					incrementedAddress[3]++;
					currentAddress = new InetSocketAddress(InetAddress.getByAddress(incrementedAddress), currentAddress.getPort());
				}
				catch(IOException ioe)
				{
					text = text.concat("Error incrementing the IP-Address" + "\n");
				}
				
				updateUIs(EIdentifier.CONNECTION, text);
			}
			// start over
			currentAddress = this.address;
		}
	}
	
	/**
	 * Tries to connect to the server on the specified IP-address and port and also tries the next two ports.
	 * Tries to connect on each of the 3 ports 2 times. If an attempt is successful, the {@link Socket} is set up for further use.
	 * @param address the {@link InetSocketAddress} with the IP-address and port that the connection with the server should be established on.
	 * @return the {@link InetSocketAddress} with the IP-address an port that the connection to the server could be established, or null, if all attempts failed.
	 */
	private synchronized InetSocketAddress connectToServer(InetSocketAddress address) {
		if(address == null)
		{
			System.err.println("Server address cannot be null, what am I supposed to connect to?");
			return null;
		}
		
		InetSocketAddress currentAddress = address;
		int numberOfTries = 1;
		// try to connect on ports 6633-6635 several times
		while(currentAddress.getPort() <= (address.getPort()+2)) {
			try {
				// try to connect
				//if(numberOfTries%2 == 1)
				//System.out.println("Trying to connect on port " + currentAddress.getPort());
				//this.connection = new Socket(currentAddress.getAddress(), currentAddress.getPort());
				this.connection = new Socket();
				this.connection.connect(currentAddress, 120);
				this.waitMs(5);
				this.connection.setSoLinger(true, 1);          // set maximum allowed time in seconds for graceful shutdown
				this.connection.setSoTimeout(1);
                this.connection.setTcpNoDelay(true);           // deactivate nagle's algorithm, which collects data before it actually sends it to the network
                this.connection.setTrafficClass(112);          // set the ToS-Byte (called DSCP nowadays, ToS is deprecated) to prioritize data	
                this.inputStream = new InputStreamReader(this.connection.getInputStream(), StandardCharsets.UTF_8);
                this.outputStream = new OutputStreamWriter(this.connection.getOutputStream(), StandardCharsets.UTF_8);
                updateUIs(EIdentifier.CONNECTION, "Connection was successfully established!\n");
				return currentAddress;
			}
			catch(IOException ioe) {
				if(numberOfTries%1 == 0) {
					currentAddress = new InetSocketAddress(currentAddress.getAddress(), (currentAddress.getPort()+1));
				}
				numberOfTries++;
			}
		}
		return null;
	}
	
	/**
	 * Reads a maximum of 500 bytes from the {@link Socket}s InputStream, converts it into a String, and returns it.<br>
	 * If an IOException occurs, the connection is closed and a new connection to the TCP-Server is established, if possible.
	 * @return the String that was received, or null, if nothing was received before an IOException occurred (including a simple Timeout). 
	 */
	private String readData() {
		try {
			char[] inputBuffer = new char[500];
			if(this.inputStream.read(inputBuffer, 0, 500) != -1) {
				// get the String, remove whitespaces and cut it to size 
				String inputMessage = (new String(inputBuffer)).trim();
				//System.out.println("\nIncoming message:");
				//System.out.println(inputMessage);
				return inputMessage;
			}
		} catch(SocketTimeoutException ste) {
			return null;
		} catch(IOException ioe) {
			updateUIs(EIdentifier.CONNECTION, "There has been an IO Exception during a read operation.\nThis Connection cannot be used any longer.\nReconnecting...\n");
			this.waitMs(3000);
			connectToServer();
			this.waitMs(300);
			this.identify();
		}
		
		return null;
	}
	
	/**
	 * Writes a String to the {@link Socket}s OutputStream for the connected device to receive it.
	 * If an IOException occurs, the connection is closed and a new connection to the TCP-Server is established, if possible.
	 * @param outputMessage the String that should be sent to the TCP-Server.
	 * @return true, if the write to the outputStream was successful, otherwise false.
	 */
	protected boolean writeToStream(String outputMessage) {
		//System.out.println("Sending - " + outputMessage + " - to server.");
		try {
			this.outputStream.write(outputMessage, 0, outputMessage.length());
			this.outputStream.flush();
			return true;
		} catch (IOException ioe) {
			updateUIs(EIdentifier.CONNECTION, "There has been an IO Exception during a read operation.\nThis Connection cannot be used any longer.\nReconnecting...\n");
			connectToServer();
			this.waitMs(300);
			this.identify();
		}
		return false;
	}
	
	/**
	 * Converts a HashMap to a JSON-String and writes a String to the {@link Socket}s OutputStream for the connected device to receive it.
	 * @param messageMap the HashMap that should be converted to a JSON-String and sent to the TCP-Server.
	 * @return true, if the write to the outputStream was successful, otherwise false.
	 */
	protected boolean writeFromMapToStream(HashMap<String, Object> messageMap) {
		if(messageMap == null)
			return false;
		return writeToStream(JSONDecoder.encodeFromMap(messageMap));
	}
	
	/**
	 * Sends identification information to the TCP-Server.
	 */
	private void sendId() {
		//System.out.println("Sending ID to the server.");
		updateUIs(EIdentifier.CONNECTION, "Sending ID to the server.\n");
		HashMap<String, Object> outputMap = new HashMap<String, Object>();
		outputMap.put(Keywords.id, this.carId);
		String outputMessage = JSONDecoder.encodeFromMap(outputMap);
		this.writeToStream(outputMessage);
	}
	
	/**
	 * Sends a list of this cars properties to the TCP-Server.
	 */
	public void sendProperties() {
		//send list of properties to server
		HashMap<String, Object> outputMap = new HashMap<String, Object>();
		outputMap.put(Keywords.properties, this.properties);
		writeFromMapToStream(outputMap);
	}
	
	/**
	 * Sends identification information to the TCP-Server every 5 seconds and waits until the TCP-Server has sent back confirmation that the car has been
	 * successfully registered with it.<br> After a successful registration the car sends a list of its properties.
	 */
	private void identify() {
		boolean identified = false;
		long lastSentId = 0;
		while(!identified) {
			this.waitMs(50);
			if(System.currentTimeMillis() - lastSentId > 5000) {
				sendId();
				updateUIs(EIdentifier.CONNECTION, "Waiting for answer...\n");
			}
			String inputMessage = this.readData();
			if(inputMessage != null)
				continue;
			HashMap<String, Object> decodedDataset = JSONDecoder.decode(inputMessage);
			if(decodedDataset == null)
				continue;
			updateUIs(EIdentifier.CONNECTION, "Answer received!\n");
			if(decodedDataset.get(Keywords.state) instanceof Long) {
				if(((long)decodedDataset.get(Keywords.state)) == 1)
					identified = true;
				else if(((long)decodedDataset.get(Keywords.state)) == -1) {
					updateUIs(EIdentifier.CONNECTION, "Duplicate ID! Trying again in a minute.\n");
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {}
				}
			}
		}
		updateUIs(EIdentifier.CONNECTION, "Identification successful!\nSending the list of the cars capabilities...\n");
		sendProperties();
	}
	
	@Override
	/**
	 * Informs this runnable about new SensorData.
	 * @param data the new {@link SensorData}.
	 */
	public void updateSensorData(SensorData data) {
		if(data == null)
			return;
		
		try {
			this.sensorDataQueue.put(data);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks the incoming message for control-data and puts it on the controlDataQueue.
	 * Also changes the controlling device to a gamepad, if the phone wants to give up control of the car.
	 * @param decodedDataset the HashMap that contains the incoming data
	 */
	private void processMessage(HashMap<String, Object> decodedDataset) {
		if(decodedDataset == null)
			return;
		
		byte steeringData = 0;
		byte accelerationData = 0;
		boolean stop = false;
		boolean driveChanged = false;
		byte cameraYaw = 0;
		byte cameraPitch = 0;
		boolean cameraChanged = false;
		boolean[] lights = new boolean[5];
		boolean lightsChanged = false;
		Object cache;
		String text = "";
		
		// change controlling device to gamepad
		if((cache = decodedDataset.get(Keywords.phoneControl)) instanceof Long)
			try {
				if(((long) cache) == 0){
					super.changeControllingDevice(-1, EDataType.DRIVECONTROL);
					super.changeControllingDevice(-1, EDataType.LIGHTCONTROL);
					super.changeControllingDevice(-1, EDataType.CAMERACONTROL);
				}
			} catch(NumberFormatException nfe) {System.out.println(nfe.getMessage());}
		
		// steering data
		if((cache = decodedDataset.get(Keywords.steering)) instanceof Long)
			try {
				steeringData = (byte)((long)cache);
				driveChanged = true;
			} catch(NumberFormatException nfe) {System.out.println(nfe.getMessage());}

		// acceleration data
		if((cache = decodedDataset.get(Keywords.acceleration)) instanceof Long)
			try {
				accelerationData = (byte)((long)cache);
				driveChanged = true;
			} catch(NumberFormatException nfe) {System.out.println(nfe.getMessage());}
		
		// stop data, in case the phone is no longer connected
		if((cache = decodedDataset.get(Keywords.stop)) instanceof Long) {
			try {
				if(((long) cache) == 1){
					stop = true;
					driveChanged = true;
					text = text.concat("Stooop see caaaar!!!" + "\n");
				}
			} catch(NumberFormatException nfe){
				System.out.println(nfe.getMessage());
				}
		}
		
		if(driveChanged){
			super.putControlData(new DriveControlData(accelerationData, steeringData, stop));
			text = text.concat("Acceleration: " + accelerationData + "\n");
			text = text.concat("Steering: " + steeringData + "\n");
		}
		
		// cameras yaw position data
		if((cache = decodedDataset.get(Keywords.cameraYaw)) instanceof Long) {
			try{
				cameraYaw = (byte)((long)cache);
				cameraChanged = true;
			} catch(NumberFormatException nfe) {System.out.println(nfe.getMessage());}
		}
		
		// cameras pitch postition data
		if((cache = decodedDataset.get(Keywords.cameraPitch)) instanceof Long) {
			try{
				cameraPitch = (byte)((long)cache);
				cameraChanged = true;
			} catch(NumberFormatException nfe) {System.out.println(nfe.getMessage());}
		}
		
		if(cameraChanged) {
			super.putControlData(new CameraControlData(cameraYaw, cameraPitch));
			text.concat("Camera yaw angle: " + cameraYaw + "\n");
			text.concat("Camera pitch angle: " + cameraPitch + "\n");
		}
		
		// front light control data
		if((cache = decodedDataset.get(Keywords.frontLights)) instanceof Long) {
			try{
				if((long)cache == 1) {
					lights[0] = true;
					text = text.concat("Headlights are on" + "\n");
				}
				else {
					lights[0] = false;
					text = text.concat("Headlights are off" + "\n");
				}
				lightsChanged = true;
			} catch(NumberFormatException nfe) {System.out.println(nfe.getMessage());}
		}
		
		// back light control data
		if((cache = decodedDataset.get(Keywords.backLights)) instanceof Long) {
			try{
				if((long)cache == 1) {
					lights[1] = true;
					text = text.concat("Backlights are on" + "\n");
				}
				else {
					lights[1] = false;
					text = text.concat("Backlights are off" + "\n");
				}
				lightsChanged = true;
			} catch(NumberFormatException nfe) {System.out.println(nfe.getMessage());}
		}
		
		// dynamic light control data
		if((cache = decodedDataset.get(Keywords.dynamicLights)) instanceof Long) {
			try{
				if((long)cache == 1) {
					lights[2] = true;
					text = text.concat("Dynamiclights are enabled" + "\n");
				}
				else {
					lights[2] = false;
					text = text.concat("Dynamiclights are disabled" + "\n");
				}
				lightsChanged = true;
			} catch(NumberFormatException nfe) {System.out.println(nfe.getMessage());}
		}
		
		// left winker control data
		if((cache = decodedDataset.get(Keywords.leftWinker)) instanceof Long) {
			try{
				if((long)cache == 1)
					lights[3] = true;
				lightsChanged = true;
			} catch(NumberFormatException nfe) {System.out.println(nfe.getMessage());}
		}
		
		// right winker control data
		if((cache = decodedDataset.get(Keywords.rightWinker)) instanceof Long) {
			try{
				if((long)cache == 1)
					lights[4] = true;
				lightsChanged = true;
			} catch(NumberFormatException nfe) {System.out.println(nfe.getMessage());}
		}
		
		if(lightsChanged){
			super.putControlData(new LightControlData(lights));
			lightsChanged = false;
		}
		updateUIs(EIdentifier.PHONECONTROL, text);
	}
	
	/**
	 * Sends all immediately available sets of {@link DistanceSensorData} on the {@link #sensorDataQueue} to the TCP-Server.
	 */
	private void processSensorData() {
		while(this.sensorDataQueue.size() > 0) {
			
			SensorData data = this.sensorDataQueue.poll();
			
			if(data != null) {
				if(data instanceof DistanceSensorData){
					DistanceSensorData sensorData = (DistanceSensorData) data;
					HashMap<String, Object> outputMap = new HashMap<>();
					outputMap.put(Keywords.ultrasonicSensor, sensorData.getFrontDistanceSensor());
					outputMap.put(Keywords.leftInfraredSensor, sensorData.getLeftsideDistanceSensor());
					outputMap.put(Keywords.rightInfraredSensor, sensorData.getRightsideDistanceSensor());
					writeFromMapToStream(outputMap);
					
					String text = new String();
					text = text.concat("Retrieving Sensor-Data:\n");
					text = text.concat("Ultrasonic front: " + sensorData.getFrontDistanceSensor() + "\n");
					text = text.concat("Infraret left: " + sensorData.getLeftsideDistanceSensor() + "\n");
					text = text.concat("Infraret right: " + sensorData.getRightsideDistanceSensor() + "\n");
					updateUIs(EIdentifier.SENSOR, text);
				}
				else if(data instanceof VelocitySensorData) {
					VelocitySensorData sensorData = (VelocitySensorData) data;
					HashMap<String, Object> outputMap = new HashMap<>();
					outputMap.put(Keywords.hallSensor, sensorData.getVelocitySensor());
					writeFromMapToStream(outputMap);
					updateUIs(EIdentifier.SENSOR, ("Retrieving Velocity Sensor-data:\nSpeed: " + sensorData.getVelocitySensor() + "\n"));
				}
			}
		}
	}
	
	/**
	  * Tries to establish a connection with the TCP-Server until it is successful.<br>
	  * Registers this car on the TCP-Server and sends a list of the cars properties.<br>
	  * Continuously processes incoming data and sends the cars sensor-data to the TCP-Server.<br>
	  * If the connection to the TCP-Server is lost at some point, the method will immediately try to reestablish it.
	  */
	public synchronized void run() {
		
		this.connectToServer();
		
		//add the cars ip-address to the list of car information
		if(this.properties.contains("camera"))
			this.properties.add("ip=" + this.connection.getLocalAddress().getHostAddress());
		
		this.identify();
		
		while(true) {
			
			String inputMessage = this.readData();
			// if the phoneConnection is in control, process the incoming message
			if(inputMessage != null && super.getConnectionId() == super.connectionIdInDriveControl) {
				 processMessage(JSONDecoder.decode(inputMessage));
			}
			else if(inputMessage != null) {
				updateUIs(EIdentifier.PHONECONTROL, "Control-data is being received, but the gamepad is currently in control!");
			}
			
			if(this.properties.contains("sensors")) {
				processSensorData();
			}
		}
	}
	
}
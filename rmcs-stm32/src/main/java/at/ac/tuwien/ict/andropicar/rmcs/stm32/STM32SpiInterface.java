package at.ac.tuwien.ict.andropicar.rmcs.stm32;

import java.util.Collection;

import com.pi4j.wiringpi.Spi;

import at.ac.tuwien.ict.andropicar.rmcs.data.*;


/**
 * An Interface to control the data transmission to a STM32, that is connected through SPI.<br><br>
 * Used Pins for SPI communication: <br>	
 * - MOSI _ GPIO12 _ Pin 19 <br>	
 * - MISO _ GPIO13 _ Pin 21 <br>	
 * - SCK __ GPIO14 _ Pin 23 <br>	
 * - GND __ ______ _ Pin 25 <br>	
 * - GND __ ______ _ Pin 6 <br>
 * - TxD __ GPIO15 _ Pin 8 <br>
 * - RxD __ GPIO16 _ Pin 10 <br>
 *  <br>	
 * for pinout see: <a href="http://pi4j.com/images/j8header.png">Raspberry Pi Pinout</a>
 * 
 * DON'T FORGET TO ENABLE SPI INTERFACE IN RASPI-CONFIG!
 * 
 * @author Boeck, Valiko
 */
public class STM32SpiInterface extends RMCSControlComponent implements Runnable{
	
	/** The byte value precede the acceleration/velocity value. */ 
	private static final byte SendAcc_Start = -128;
	/** The byte value precede the steering-angle value. */ 
	private static final byte SendAngle_Start = -127;
	/** The byte value precede the lights values. */ 
	private static final byte SendLights_Start = -126;
	/** The byte value precede the camera-yaw-angle value. */ 
	private static final byte SendCamYaw = -125;
	/** The byte value precede the camera-pitch-angle value. */ 
	private static final byte SendCamPitch = -124;
	/** The byte value to send to request a sensor-update. */ 
	private static final byte SendUpdateSensorData = -123;
	
	/** The byte that represents enabling of the back-lights. */
	private static final byte brake_on = 1;
	/** The byte that represents disabling of the back-lights. */
	private static final byte brake_off = 2;
	/** The byte that represents enabling of front-lights. */
	private static final byte headlights_on = 3;
	/** The byte that represents disabling of front-lights. */
	private static final byte headlights_off = 4;
	/** The byte that represents setting the left winker. */
	private static final byte blink_left = 5;
	/** The byte that represents setting the left winker. */
	private static final byte blink_right = 6;
	
	/** true, if SPI-Communication should be enabled, otherwise false. */
	private static final boolean SPI_Activated = true;		// use false to be able to start the program on a pc
	
	/** The time in milliseconds that should be waited between sensor-data-requests.  */
	private int sensorUpdateInterval = 50;
	
	
	/**
	 * Initializes SPI Module 0 on the Raspberry Pi.
	 * @param controlTypes the list of {@link EDataType}s that the SPI interface can process.
	 * @param sensorUpdateInterval the time in milliseconds that should be waited between sensor-data-requests.
	 */
	public STM32SpiInterface(Collection<EDataType> controlTypes, int sensorUpdateInterval) {
		
		super(controlTypes);
		initialize(sensorUpdateInterval);
	}
	
	/**
	 * Initializes SPI Module 0 on the Raspberry Pi.
	 * @param controlType the {@link EDataType} that the SPI interface can process.
	 * @param sensorUpdateInterval the time in milliseconds that should be waited between sensor-data-requests.
	 */
	public STM32SpiInterface(EDataType controlType, int sensorUpdateInterval) {
		
		super(controlType);
		initialize(sensorUpdateInterval);
	}

	/**
	 * Initializes SPI Module 0 on the Raspberry Pi.
	 * @param controlType1 the first {@link EDataType} that the SPI interface can process.
	 * @param controlType2 the second {@link EDataType} that the SPI interface can process.
	 * @param sensorUpdateInterval the time in milliseconds that should be waited between sensor-data-requests.
	 */
	public STM32SpiInterface(EDataType controlType1, EDataType controlType2, int sensorUpdateInterval) {
	
		super(controlType1, controlType2);
		initialize(sensorUpdateInterval);
	}
	
	/**
	 * Initializes SPI Module 0 on the Raspberry Pi.
	 * @param controlType1 the first {@link EDataType} that the SPI interface can process.
	 * @param controlType2 the second {@link EDataType} that the SPI interface can process.
	 * @param controlType3 the third {@link EDataType} that the SPI interface can process.
	 * @param sensorUpdateInterval the time in milliseconds that should be waited between sensor-data-requests.
	 */
	public STM32SpiInterface(EDataType controlType1, EDataType controlType2,  EDataType controlType3, int sensorUpdateInterval) {
		
		super(controlType1, controlType2, controlType3);
		initialize(sensorUpdateInterval);
	}
	
	/**
	 * Initializes SPI Module 0 on the Raspberry Pi.
	 * @param sensorUpdateInterval the time in milliseconds that should be waited between sensor-data-requests.
	 */
	private void initialize(int sensorUpdateInterval){
		if(sensorUpdateInterval >= 10)
			this.sensorUpdateInterval = sensorUpdateInterval;
		
		if(SPI_Activated){
			System.out.println("");
			System.out.println("");
			System.out.println("Initialize SPI Interface.................");
			System.out.println("-----------------------------------------");
			System.out.println("");
		
			int fd = Spi.wiringPiSPISetupMode(Spi.CHANNEL_0, 1000000, Spi.MODE_0);
		
			if(fd == -1){
				System.out.println("****************SPI interface initialization returned with errors****************");
				System.out.println("");
			}else{
				System.out.println("SPI initialization completed!");
				System.out.println("");
			}
		}
		
		System.out.println("Initialize Serial Interface.................");
		System.out.println("--------------------------------------------");
		System.out.println("");
	}
	
	
	/**
	 * Is used to update the values for steering angle and velocity of the car
	 * @param acceleration is used to set the velocity of the car, has to be between -100 and 100.
	 * @param steeringAngle is used to set the steering angle of the car, has to be between -100 and 100.
	 */
	private void updateControls(byte acceleration, byte steeringAngle)
	{
		//System.out.println("Received new Control-data:\nAcceleration: " + acceleration + "\nSteering angle: " + steeringAngle);
		
		if(SPI_Activated){
			byte[] data = new byte[2];
			data[0] = SendAcc_Start;
			data[1] = acceleration;
	
			System.out.println("Send acc and angle data to uC");
			Spi.wiringPiSPIDataRW(Spi.CHANNEL_0, data, 2);
			
			data[0] = SendAngle_Start;
			data[1] = steeringAngle;
			
			Spi.wiringPiSPIDataRW(Spi.CHANNEL_0, data, 2);
		}
	}

	
	/**
	 * Is used to update the values for the camera angles
	 * @param yaw is used to set the yaw angle of the camera, has to be between -100 and 100.
	 * @param pitch  is used to set the pitch angle of the camera, has to be between -100 and 100.
	 */
	private void updateCameraAngle(byte yaw, byte pitch)
	{
		//System.out.println("Received new Camera-data:\nYaw: " + yaw + "\nPitch: " + pitch);
		
		if(SPI_Activated){
			byte[] data = new byte[2];
			data[0] = SendCamYaw;
			data[1] = yaw;

			Spi.wiringPiSPIDataRW(Spi.CHANNEL_0, data, 2);
			
			data[0] = SendCamPitch;
			data[1] = pitch;
			
			Spi.wiringPiSPIDataRW(Spi.CHANNEL_0, data, 2);
		}
	}
	
	
	/**
	 * Turns the back-lights on or off
	 * @param lightsOn true, if the back-lights should be turned on, otherwise false.
	 */
	private void setBackLights(boolean lightsOn){
		if(!SPI_Activated)
			return;
		
		if(lightsOn){
			byte[] data = new byte[2];
			data[0] = SendLights_Start;
			data[1] = brake_on;
			Spi.wiringPiSPIDataRW(Spi.CHANNEL_0, data, 2);
		} else{
			byte[] data = new byte[2];
			data[0] = SendLights_Start;
			data[1] = brake_off;
			Spi.wiringPiSPIDataRW(Spi.CHANNEL_0, data, 2);
		}
	}
	
	
	/**
	 * Turns the front-lights on or off
	 * @param lightsOn true, if the front-lights should be turned on, otherwise false.
	 */
	private void setFrontLights(boolean lightsOn){
		if(!SPI_Activated)
			return;
		
		if(lightsOn){
			byte[] data = new byte[2];
			data[0] = SendLights_Start;
			data[1] = headlights_on;
			Spi.wiringPiSPIDataRW(Spi.CHANNEL_0, data, 2);
		}else{
			byte[] data = new byte[2];
			data[0] = SendLights_Start;
			data[1] = headlights_off;
			Spi.wiringPiSPIDataRW(Spi.CHANNEL_0, data, 2);
		}
	}
	
	private void setDynamicLights(boolean lightsOn){
		if(!SPI_Activated)
			return;
		
		// not yet implemented on STM32
	}
	
	
	/**
	 * Starts blinking left sequence.
	 */	
	private void blink_left(){
		if(SPI_Activated){
			byte[] data = new byte[2];
			data[0] = SendLights_Start;
			data[1] = blink_left;

			Spi.wiringPiSPIDataRW(Spi.CHANNEL_0, data, 2);
		}
	}
	
	
	/**
	 * Starts blinking right sequence.
	 */
	private void blink_right(){
		if(SPI_Activated){
			byte[] data = new byte[2];
			data[0] = SendLights_Start;
			data[1] = blink_right;
			
			Spi.wiringPiSPIDataRW(Spi.CHANNEL_0, data, 2);
		}
	}
	
	
	/**
	 * Sends stop command to the STM32.
	 */
	private void stop() {
		// TODO Auto-generated method stub
		System.out.println("Stopping car!");
		
		if(SPI_Activated){
			byte[] data = new byte[2];
			data[0] = SendAcc_Start;
			data[1] = 0;
			
			System.out.println("Send stop-command to uC");
			Spi.wiringPiSPIDataRW(Spi.CHANNEL_0, data, 2);
		}
		
	}
	
	
	/**
	 * Requests new sensor data from the STM32.
	 */
	private void updateSensorData(){
		if(SPI_Activated){
			byte[] data = new byte[2];
			data[0] = SendUpdateSensorData;
			data[1] = 0;
			
			Spi.wiringPiSPIDataRW(Spi.CHANNEL_0, data, 2);
		}
	}
	
	private void processControlData(){
		
		IControlData controlData = null;
		try {
			controlData = getControlDataQueue().take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(controlData == null)
			return;
		
		if(controlData instanceof DriveControlData){
			DriveControlData newData = (DriveControlData) controlData;
			if(newData.isStopped())
				this.stop();
			else{
				//System.out.println("Sending DriveData to STM32!");
				System.out.println("Acceleration: " + newData.getAcceleration());
				System.out.println("Steering: " + newData.getSteeringAngle());
				this.updateControls((byte)newData.getAcceleration(), (byte)newData.getSteeringAngle());
			}
		}
		else if(controlData instanceof CameraControlData){
			//System.out.println("Sending CameraData to STM32!");
			CameraControlData newData = (CameraControlData) controlData;
			this.updateCameraAngle((byte)newData.getPitchAngle(), (byte)newData.getYawAngle());
		}
		else if(controlData instanceof LightControlData){
			//System.out.println("Sending LightData to STM32!");
			LightControlData lightData = (LightControlData) controlData;
			setFrontLights(lightData.headlightsOn());
			setBackLights(lightData.brakelightsOn());
			setDynamicLights(lightData.dynamicLightsOn());
			if(lightData.leftWinkerOn())
				blink_left();
			if(lightData.rightWinkerOn())
				blink_right();
		}
	}
	
	/**
	 * Continuously reads from its controlDataQueue and sends out all compatible {@link IControlData} over the SPI interface.
	 */
	public void run(){
		
		Long lastSensorUpdate = System.currentTimeMillis();
		//Long lastControlOutput = System.currentTimeMillis();
		while(super.getControlDataQueue() != null){
			
			if(System.currentTimeMillis() - lastSensorUpdate > this.sensorUpdateInterval){
				updateSensorData();
				lastSensorUpdate = System.currentTimeMillis();
			}
			
				processControlData();
		}
	}
	
	
	
}







package at.ac.tuwien.ict.andropicar.rmcs.data;

import java.sql.Timestamp;


/**
 * Contains one set of values of the RMCSs distance-sensors.
 * 
 * @author Boeck
 */
public class DistanceSensorData extends SensorData {
	

	/** the measurement (in cm) of the front distance-sensor (ultrasonic). */
	private int frontDistanceSensor;
	/** the measurement (in cm) of the left distance-sensor (infrared). */
	private int leftsideDistanceSensor;
	/** the measurement (in cm) of the right distance-sensor (infrared). */
	private int rightsideDistanceSensor;
	
	
	/**
	 * @param timestamp the time this data-set was created.
	 * @param frontDistanceSensor the measurement (in cm) for the front-ultrasonic-sensor/front-distance-sensor
	 * @param leftsideDistanceSensor the measurement (in cm) for the left-infrared-sensor/left-distance-sensor
	 * @param rightsideDistanceSensor the measurement (in cm) for the right-infrared-sensor/right-distance-sensor
	 */
	public DistanceSensorData(Timestamp timestamp, int frontDistanceSensor, int leftsideDistanceSensor, int rightsideDistanceSensor){
		super(timestamp);
		this.frontDistanceSensor = frontDistanceSensor;
		this.leftsideDistanceSensor = leftsideDistanceSensor;
		this.rightsideDistanceSensor = rightsideDistanceSensor;
	}
	
	/**
	 * Constructor where a long variable is used to set the timestamp.
	 * @param timestamp the time this data-set was created.
	 * @param frontDistanceSensor the measurement (in cm) for the front-ultrasonic-sensor/front-distance-sensor
	 * @param leftsideDistanceSensor the measurement (in cm) for the left-infrared-sensor/left-distance-sensor
	 * @param rightsideDistanceSensor the measurement (in cm) for the right-infrared-sensor/right-distance-sensor
	 */
	public DistanceSensorData(long timestamp, int frontDistanceSensor, int leftsideDistanceSensor, int rightsideDistanceSensor){
		super(new Timestamp(timestamp));
		this.frontDistanceSensor = frontDistanceSensor;
		this.leftsideDistanceSensor = leftsideDistanceSensor;
		this.rightsideDistanceSensor = rightsideDistanceSensor;
	}
	
	/**
	 * Constructor where the current System-time is used to set the timestamp.
	 * 
	 * @param frontDistanceSensor the measurement (in cm) for the front-ultrasonic-sensor/front-distance-sensor
	 * @param leftsideDistanceSensor the measurement (in cm) for the left-infrared-sensor/left-distance-sensor
	 * @param rightsideDistanceSensor the measurement (in cm) for the right-infrared-sensor/right-distance-sensor
	 */
	public DistanceSensorData(int frontDistanceSensor, int leftsideDistanceSensor, int rightsideDistanceSensor){
		super(new Timestamp(System.currentTimeMillis()));
		this.frontDistanceSensor = frontDistanceSensor;
		this.leftsideDistanceSensor = leftsideDistanceSensor;
		this.rightsideDistanceSensor = rightsideDistanceSensor;
	}
	
	/**
	 * @return the measurement (in cm) of the front distance-sensor.
	 */
	public int getFrontDistanceSensor() {
		return frontDistanceSensor;
	}

	/**
	 * @return the measurement (in cm) of the left-side distance-sensor.
	 */
	public int getLeftsideDistanceSensor() {
		return leftsideDistanceSensor;
	}

	/**
	 * @return the measurement (in cm) of the right-side distance-sensor.
	 */
	public int getRightsideDistanceSensor() {
		return rightsideDistanceSensor;
	}
	
}

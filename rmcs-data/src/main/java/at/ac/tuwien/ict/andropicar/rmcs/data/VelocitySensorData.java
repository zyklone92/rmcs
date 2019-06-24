package at.ac.tuwien.ict.andropicar.rmcs.data;

import java.sql.Timestamp;


/**
 * Contains one set of values of the RMCSs velocity-sensors.
 * If the RMCS is equipped with a 3-axis accelerometer and/or gyroscope, this class could be edited to accommodate that sensor-data.
 * 
 * @author Boeck
 */
public class VelocitySensorData extends SensorData {
	

	/** the measurement (in rpm) of the speed-sensor (hall). */
	private int velocitySensor;
	
	
	/**
	 * @param timestamp the time this data-set was created.
	 * @param velocitySensor the measurement (in rpm) for the hall-sensor/speed-sensor
	 */
	public VelocitySensorData(Timestamp timestamp, int velocitySensor){
		super(timestamp);
		this.velocitySensor = velocitySensor;
	}
	
	/**
	 * Constructor where a long variable is used to set the timestamp.
	 * @param timestamp the time this data-set was created.
	 * @param velocitySensor the measurement (in rpm) for the hall-sensor/speed-sensor
	 */
	public VelocitySensorData(long timestamp, int velocitySensor){
		super(new Timestamp(timestamp));
		this.velocitySensor = velocitySensor;
	}
	
	/**
	 * Constructor where the current System-time is used to set the timestamp.
	 * @param velocitySensor the measurement (in rpm) for the hall-sensor/speed-sensor
	 */
	public VelocitySensorData(int velocitySensor){
		super(new Timestamp(System.currentTimeMillis()));
		this.velocitySensor = velocitySensor;
	}
	

	/**
	 * @return the measurement (in rpm) of the speed-sensor.
	 */
	public int getVelocitySensor() {
		return velocitySensor;
	}
	
}

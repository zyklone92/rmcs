package at.ac.tuwien.ict.andropicar.rmcs.data;

import java.sql.Timestamp;

public abstract class SensorData implements IData {

	/** the time this set of sensor-data was captured */
	private Timestamp timestamp;
	
	
	public SensorData(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	
	public Timestamp getTimestamp() {
		return timestamp;
	}
	
}

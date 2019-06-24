package at.ac.tuwien.ict.andropicar.rmcs.connection;

import at.ac.tuwien.ict.andropicar.rmcs.data.SensorData;


/**
 * By implementing this interface the user indicates that it is capable of receiving updates on new {@link at.ac.tuwien.ict.andropicar.rmcs.data.DistanceSensorData} from the RMCS.
 * 
 * @author Boeck
 *
 */
public interface ISensorDataListener {
	
	/**
	 * Used to inform the user of new {@link at.ac.tuwien.ict.andropicar.rmcs.data.DistanceSensorData}.
	 * 
	 * @param data the new {@link at.ac.tuwien.ict.andropicar.rmcs.data.DistanceSensorData}.
	 */
	public void updateSensorData(SensorData data);
}

package at.ac.tuwien.ict.andropicar.rmcs.stm32;

import java.util.concurrent.LinkedBlockingQueue;

import at.ac.tuwien.ict.andropicar.rmcs.data.IData;
import at.ac.tuwien.ict.andropicar.rmcs.data.SensorData;

/**
 * The {@link RMCSSensorComponent}-class is a base-class for all components of the RMCS, that are able to publish {@link SensorData} on the RMCS.
 * Each object that is of the type {@link RMCSSensorComponent} can put one or more types of {@link SensorData} on the sensorDataQueue,
 * that is handled by the {@link RMCSComponentManager}.
 * 
 * @author Boeck
 */
public class RMCSSensorComponent {

	/** the sensorDataQueue, where the {@link SensorData} is posted. */
	private LinkedBlockingQueue<IData> sensorDataQueue;
	
	
	/**
	 * @param sensorDataQueue the sensorDataQueue, where the {@link SensorData} is posted.
	 */
	public RMCSSensorComponent(LinkedBlockingQueue<IData> sensorDataQueue) {
		this.sensorDataQueue = sensorDataQueue;
	}
	
	
	/**
	 * Puts new sensor-data on the {@link #sensorDataQueue}, so it can get distributed by the {@link RMCSComponentManager}.
	 * @param sensorData the {@link SensorData} that should be put on the {@link #sensorDataQueue}.
	 */
	protected void postSensorData(SensorData sensorData){
		if(sensorData == null)
			return;
		try{
			this.sensorDataQueue.put(sensorData);
		} catch(InterruptedException ie){
			ie.printStackTrace();
		}
	}

}

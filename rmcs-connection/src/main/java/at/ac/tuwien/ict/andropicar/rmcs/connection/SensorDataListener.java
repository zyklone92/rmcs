package at.ac.tuwien.ict.andropicar.rmcs.connection;

import java.util.ArrayList;
import java.util.Collection;

import at.ac.tuwien.ict.andropicar.rmcs.data.*;


/**
 * This class is being used by the RMCSs interfaces to know what {@link DistanceSensorData} should be forwarded to what {@link ISensorDataListener}.<br>
 * It is essentially a Tuple, with one attribute being the {@link ISensorDataListener} that wants to receive updates about sensor-events and the other
 * being a list of {@link EDataType}s that should be forwarded to the listener.<br>
 * Equality between two instances of this class, is determined by {@link #listener}.
 * 
 * @author Boeck
 *
 */
public class SensorDataListener {

	
	/** the {@link ISensorDataListener} that wants to receive updates about sensor-events. */
	private ISensorDataListener listener;
	/** the {@link EDataType} that should be forwarded to the listener. */
	private Collection<EDataType> listenerTypes = new ArrayList<>();;
	
	
	/**
	 * @param listener the {@link ISensorDataListener} that wants to receive updates about sensor-events.
	 * @param listenerTypes a list of {@link EDataType}s that should be forwarded to the listener.
	 */
	public SensorDataListener(ISensorDataListener listener, Collection<EDataType> listenerTypes){
		this.listener = listener;
		this.listenerTypes.addAll(listenerTypes);
	}
	
	/**
	 * @param listener the {@link ISensorDataListener} that wants to receive updates about sensor-events.
	 * @param listenerType the {@link EDataType} that should be forwarded to the listener.
	 */
	public SensorDataListener(ISensorDataListener listener, EDataType listenerType){
		this.listener = listener;
		this.listenerTypes.add(listenerType);
	}
	
	/**
	 * @param listener the {@link ISensorDataListener} that wants to receive updates about sensor-events.
	 * @param listenerType1 the first {@link EDataType} that should be forwarded to the listener.
	 * @param listenerType2 the second {@link EDataType} that should be forwarded to the listener.
	 */
	public SensorDataListener(ISensorDataListener listener, EDataType listenerType1, EDataType listenerType2){
		this.listener = listener;
		this.listenerTypes.add(listenerType1);
		this.listenerTypes.add(listenerType2);
	}
	
	/**
	 * @param listener the {@link ISensorDataListener} that wants to receive updates about sensor-events.
	 * @param listenerType1 the first {@link EDataType} that should be forwarded to the listener.
	 * @param listenerType2 the second {@link EDataType} that should be forwarded to the listener.
	 * @param listenerType3 the third {@link EDataType} that should be forwarded to the listener.
	 */
	public SensorDataListener(ISensorDataListener listener, EDataType listenerType1, EDataType listenerType2, EDataType listenerType3){
		this.listener = listener;
		this.listenerTypes.add(listenerType1);
		this.listenerTypes.add(listenerType2);
		this.listenerTypes.add(listenerType3);
	}
	

	/**
	 * @return the {@link ISensorDataListener} that wants to receive updates about sensor-events.
	 */
	public ISensorDataListener getListener() {
		return listener;
	}
	
	/**
	 * @return the {@link EDataType} that should be forwarded to the listener.
	 */
	public Collection<EDataType> getListenerTypes() {
		return listenerTypes;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		if(obj == this)
			return true;
		if(obj instanceof SensorDataListener) {
			SensorDataListener lis = (SensorDataListener) obj;
			if(lis.listener.equals(this.listener))
				return true;
		}
		return false;
	}
	
}

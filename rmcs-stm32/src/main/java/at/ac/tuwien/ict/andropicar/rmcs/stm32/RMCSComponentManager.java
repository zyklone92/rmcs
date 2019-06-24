package at.ac.tuwien.ict.andropicar.rmcs.stm32;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

import at.ac.tuwien.ict.andropicar.rmcs.data.*;
import at.ac.tuwien.ict.andropicar.rmcs.connection.*;

/**
 * The central manager that handles all incoming {@link IData} and,
 * depending on the type of data, forwards it to the corresponding {@link SensorDataListener}s and {@link RMCSControlComponent}s.<br>
 * All {@link RMCSControlComponent}s have to register themselves with the {@link RMCSComponentManager} in order to get updated about new {@link IControlData}.<br>
 * All {@link SensorDataListener}s have to register themselves with the {@link RMCSComponentManager} in order to get updated about new {@link SensorData}.<br>
 * 
 * @author Boeck
 */
public class RMCSComponentManager implements Runnable{
	
	/** The list of {@link RMCSControlComponent}s that the {@link RMCSComponentManager} updates about new {@link IControlData}. */
	private Collection<RMCSControlComponent> carComponents;
	
	/** The Queue, that {@link SensorDataListener}s and {@link RMCSControlComponent}s put new Data on, that should be distributed. */
	private LinkedBlockingQueue<IData> dataQueue;
	
	/** The list of {@link SensorDataListener}s that the {@link RMCSComponentManager} updates about new {@link SensorData}. */
	private Collection<SensorDataListener> dataListeners;
	
	
	/**
	 * @param dataQueue the Queue, that {@link SensorDataListener}s and {@link RMCSControlComponent}s put new Data on, that should be distributed.
	 */
	public RMCSComponentManager(LinkedBlockingQueue<IData> dataQueue){
		this.dataQueue = dataQueue;
		this.carComponents = new ArrayList<>();
		this.dataListeners = new ArrayList<>();
	}
	
	
	/**
	 * Adds the given {@link RMCSControlComponent} to {@link #carComponents}, if the passed object is not null.
	 * @param carComponent the {@link RMCSControlComponent} to be added to {@link #carComponents}.
	 * @return true, if {@link #carComponents} changed as a result of the call of this method, otherwise false.
	 */
	public boolean addCarComponent(RMCSControlComponent carComponent) {
		if(carComponent == null)
			return false;
		
		return carComponents.add(carComponent);
	}
	
	/**
	 * Removes the given {@link RMCSControlComponent} from {@link #carComponents}.
	 * @param carComponent the {@link RMCSControlComponent} to be removed from {@link #carComponents}.
	 * @return true, if {@link #carComponents} changed as a result of the call of this method, otherwise false.
	 */
	public boolean removeCarComponent(RMCSControlComponent carComponent){
		if(carComponent == null)
			return false;
		
		for(RMCSControlComponent tempCarComponent : this.carComponents)
			if(tempCarComponent.equals(carComponent))
				return this.carComponents.remove(carComponent);
		return false;
	}
	
	/**
	 * Adds the given {@link SensorDataListener} to {@link #dataListeners}, if the passed object is not null.
	 * @param listener the {@link ISensorDataListener} to be added to {@link #dataListeners}.
	 * @return true, if {@link #dataListeners} changed as a result of the call of this method, otherwise false.
	 */
	public boolean addDataListener(SensorDataListener listener) {
		if(listener == null)
			return false;
		
		return this.dataListeners.add(listener);
	}
	
	/**
	 * Creates a new {@link SensorDataListener} with the given parameters and adds it to {@link #dataListeners},
	 * unless a {@link SensorDataListener} with the given {@link ISensorDataListener} is already in {@link #dataListeners},
	 * in which case the passed {@link EDataType} is merely added to the list of {@link EDataType}s of the {@link SensorDataListener}
	 * that contains the passed {@link ISensorDataListener}.
	 * @param listener the {@link ISensorDataListener} that wants to be informed about new {@link SensorData}.
	 * @param listenerType the type of SensorData that the listener wants to be informed about.
	 * @return true, if {@link #dataListeners} or an object contained in {@link #dataListeners} changed as a result of the call of this method, otherwise false.
	 */
	public boolean addDataListener(ISensorDataListener listener, EDataType listenerType) {
		if(listener == null)
			return false;
		for(SensorDataListener senlis : this.dataListeners) {
			if(senlis.getListener().equals(listener))
				return senlis.getListenerTypes().add(listenerType);
		}
		return this.dataListeners.add(new SensorDataListener(listener, listenerType));
	}
	
	/**
	 * Creates a new {@link SensorDataListener} with the given parameters and adds it to {@link #dataListeners},
	 * unless a {@link SensorDataListener} with the given {@link ISensorDataListener} is already in {@link #dataListeners},
	 * in which case the passed {@link EDataType}s are merely added to the list of {@link EDataType}s of the {@link SensorDataListener}
	 * that contains the passed {@link ISensorDataListener}.
	 * @param listener the {@link ISensorDataListener} that wants to be informed about new {@link SensorData}.
	 * @param listenerTypes the list of types of SensorData that the listener wants to be informed about.
	 * @return true, if {@link #dataListeners} or an element thereof changed as a result of the call of this method, otherwise false.
	 */
	public boolean addDataListener(ISensorDataListener listener, Collection<EDataType> listenerTypes) {
		if(listener == null)
			return false;
		for(SensorDataListener senlis : this.dataListeners) {
			if(senlis.getListener().equals(listener))
				return senlis.getListenerTypes().addAll(listenerTypes);
		}
		return this.dataListeners.add(new SensorDataListener(listener, listenerTypes));
	}
	
	/**
	 * Removes the passed {@link SensorDataListener} from {@link #dataListeners}.
	 * @param listener the {@link SensorDataListener} to be removed.
	 * @return true, if {@link #dataListeners} changed as a result of the call of this method, otherwise false.
	 */
	public boolean removeDataListener(SensorDataListener listener) {
		return this.dataListeners.remove(listener);
	}
	
	/**
	 * Removes the passed {@link EDataType} from the {@link SensorDataListener} with the given {@link ISensorDataListener} from {@link #dataListeners}.
	 * @param listener the {@link ISensorDataListener} that wants to stop receiving updates about a certain type of {@link SensorData}.
	 * @param listenerType the type of {@link SensorData} that the listener doesn't want to be updated about anymore.
	 * @return true, if an element of {@link #dataListeners} changed as a result of the call of this method, otherwise false.
	 */
	public boolean removeDataListener(ISensorDataListener listener, EDataType listenerType){
		if(listener == null || listenerType == null)
			return false;
		
		for(SensorDataListener senlis : this.dataListeners) {
			if(senlis.getListener().equals(listener))
				return senlis.getListenerTypes().remove(listenerType);
		}
		return false;
	}
	
	/**
	 * Removes the passed {@link EDataType}s from the {@link SensorDataListener} with the given {@link ISensorDataListener} from {@link #dataListeners}.
	 * @param listener the {@link ISensorDataListener} that wants to stop receiving updates about a certain type of {@link SensorData}.
	 * @param listenerTypes the list of types of {@link SensorData} that the listener doesn't want to be updated about anymore.
	 * @return true, if an element of {@link #dataListeners} changed as a result of the call of this method, otherwise false.
	 */
	public boolean removeDataListener(ISensorDataListener listener, Collection<EDataType> listenerTypes){
		if(listener == null || listenerTypes == null)
			return false;
		
		for(SensorDataListener senlis : this.dataListeners) {
			if(senlis.getListener().equals(listener))
				return senlis.getListenerTypes().removeAll(listenerTypes);
		}
		return false;
	}
	
	/**
	 * Removes the {@link SensorDataListener} with the given {@link ISensorDataListener} from {@link #dataListeners}.
	 * @param listener the {@link SensorDataListener} to be removed.
	 * @return  true, if {@link #dataListeners} changed as a result of the call of this method, otherwise false.
	 */
	public boolean removeDataListener(ISensorDataListener listener) {
		if(listener == null)
			return false;
		return this.dataListeners.remove(new SensorDataListener(listener, (Collection<EDataType>)null));
	}
	
	/**
	 * Continuously waits for data on the dataQueue and distributes that data to the correct {@link RMCSControlComponent}s and {@link SensorDataListener}s,
	 * depending on what type of data they can process.
	 */
	@Override
	public void run(){
		
		IData data = null;
		while(carComponents != null && dataQueue != null){
			
			// wait for new Data to be processed
			try {
				data = dataQueue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// identify incoming data package and distribute it to the corresponding CarInterfaces or sensorDataListeners
			
			if(data instanceof IControlData){
				if(data instanceof DriveControlData){
					for(RMCSControlComponent carInterface : this.carComponents)
						if(carInterface.getTypes().contains(EDataType.DRIVECONTROL))
							carInterface.updateControlData((IControlData)data);
				}
				else if(data instanceof CameraControlData){
					for(RMCSControlComponent carInterface : this.carComponents)
						if(carInterface.getTypes().contains(EDataType.CAMERACONTROL))
							carInterface.updateControlData((IControlData)data);
				}
				else if(data instanceof LightControlData){
					for(RMCSControlComponent carInterface : this.carComponents)
						if(carInterface.getTypes().contains(EDataType.LIGHTCONTROL))
							carInterface.updateControlData((IControlData)data);
				}
			}
			else if(data instanceof SensorData) {
				if(data instanceof DistanceSensorData){
					for(SensorDataListener sensorDataListener : this.dataListeners){
						if(sensorDataListener.getListenerTypes().contains(EDataType.SENSOR) || 
								sensorDataListener.getListenerTypes().contains(EDataType.DISTANCE_SENSOR))
							sensorDataListener.getListener().updateSensorData((SensorData)data);
					}
				}
				else if(data instanceof VelocitySensorData){
					for(SensorDataListener sensorDataListener : this.dataListeners){
						if(sensorDataListener.getListenerTypes().contains(EDataType.SENSOR) || 
								sensorDataListener.getListenerTypes().contains(EDataType.VELOCITY_SENSOR))
							sensorDataListener.getListener().updateSensorData((SensorData)data);
					}
				}
			}
		}
	}
	
}






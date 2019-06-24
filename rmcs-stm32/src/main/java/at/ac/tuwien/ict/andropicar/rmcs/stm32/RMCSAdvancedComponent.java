package at.ac.tuwien.ict.andropicar.rmcs.stm32;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

import at.ac.tuwien.ict.andropicar.rmcs.data.EDataType;
import at.ac.tuwien.ict.andropicar.rmcs.data.IControlData;
import at.ac.tuwien.ict.andropicar.rmcs.data.IData;
import at.ac.tuwien.ict.andropicar.rmcs.data.SensorData;

/**
 * A composition of {@link RMCSControlComponent} and {@link RMCSSensorComponent}.
 * Sub-classes of {@link RMCSAdvancedComponent} are both able to process {@link IControlData} and post {@link SensorData}.
 * 
 * @author Boeck
 */
public abstract class RMCSAdvancedComponent {

	/** The {@link RMCSControlComponent}, which holds the Queue for new sets of {@link IControlData}. */
	private RMCSControlComponent controlComponent;
	/** The {@link RMCSSensorComponent}, which holds the Queue where new {@link SensorData} can be posted. */
	private RMCSSensorComponent sensorComponent;
	
	
	/**
	 * @param controlTypes the list of {@link EDataType}s that this component can process.
	 * @param sensorDataQueue the sensorDataQueue, where new {@link SensorData} is posted.
	 */
	public RMCSAdvancedComponent(Collection<EDataType> controlTypes, LinkedBlockingQueue<IData> sensorDataQueue) {
		this.controlComponent = new RMCSControlComponent(controlTypes);
		this.sensorComponent = new RMCSSensorComponent(sensorDataQueue);
	}
	
	/**
	 * @param controlType the {@link EDataType} that this component can process.
	 * @param sensorDataQueue the sensorDataQueue, where new {@link SensorData} is posted.
	 */
	public RMCSAdvancedComponent(EDataType controlType, LinkedBlockingQueue<IData> sensorDataQueue) {
		this.controlComponent = new RMCSControlComponent(controlType);
		this.sensorComponent = new RMCSSensorComponent(sensorDataQueue);
	}
	
	/**
	 * @param controlType1 the first {@link EDataType} that this component can process.
	 * @param controlType2 the second {@link EDataType} that this component can process.
	 * @param sensorDataQueue the sensorDataQueue, where new {@link SensorData} is posted.
	 */
	public RMCSAdvancedComponent(EDataType controlType1, EDataType controlType2, LinkedBlockingQueue<IData> sensorDataQueue) {
		this.controlComponent = new RMCSControlComponent(controlType1, controlType2);
		this.sensorComponent = new RMCSSensorComponent(sensorDataQueue);
	}
	
	/**
	 * @param controlType1 the first {@link EDataType} that this component can process.
	 * @param controlType2 the second {@link EDataType} that this component can process.
	 * @param controlType3 the third {@link EDataType} that this component can process.
	 * @param sensorDataQueue the sensorDataQueue, where new {@link SensorData} is posted.
	 */
	public RMCSAdvancedComponent(EDataType controlType1, EDataType controlType2, EDataType controlType3, LinkedBlockingQueue<IData> sensorDataQueue) {
		this.controlComponent = new RMCSControlComponent(controlType1, controlType2, controlType3);
		this.sensorComponent = new RMCSSensorComponent(sensorDataQueue);
	}

	
	/**
	 * @return the {@link RMCSControlComponent}, which holds the Queue for new sets of {@link IControlData}.
	 */
	public RMCSControlComponent getControlComponent() {
		return controlComponent;
	}

	/**
	 * @return the {@link RMCSSensorComponent}, which holds the Queue where new {@link SensorData} can be posted.
	 */
	public RMCSSensorComponent getSensorComponent() {
		return sensorComponent;
	}

}

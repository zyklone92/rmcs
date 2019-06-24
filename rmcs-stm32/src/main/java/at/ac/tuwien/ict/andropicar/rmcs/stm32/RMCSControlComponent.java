package at.ac.tuwien.ict.andropicar.rmcs.stm32;

import java.util.ArrayList;
import java.util.Collection;
import at.ac.tuwien.ict.andropicar.rmcs.data.*;

import at.ac.tuwien.ict.andropicar.rmcs.data.IControlData;

/**
 * The {@link RMCSControlComponent}-class is a base-class for all components of the RMCS, that directly or indirectly control parts of the RMCS (e.g. drive, lights, etc.).
 * Each object that is of the type {@link RMCSControlComponent} can process one or more types of {@link IControlData}.<br>
 * Each instance has its own {@link UniqueLinkedBlockingQueue}, which can be written to, to inform this component about new {@link IControlData},
 * which must be handled by sub-classes of {@link RMCSControlComponent}.
 * 
 * @author Boeck
 */
public class RMCSControlComponent {
	
	/** The types of {@link IControlData} this interface can process. */
	private Collection<EDataType> controlTypes;
	
	/** The Queue, that holds all control-data, that this component should process. */
	private UniqueLinkedBlockingQueue<IControlData> controlDataQueue = new UniqueLinkedBlockingQueue<>();
	
	
	/**
	 * @param controlTypes the list of {@link EDataType}s that this component can process.
	 */
	public RMCSControlComponent(Collection<EDataType> controlTypes) {
		this.controlTypes = controlTypes;
	}
	
	/**
	 * @param controlType the {@link EDataType} that this component can process.
	 */
	public RMCSControlComponent(EDataType controlType) {
		this.controlTypes = new ArrayList<>();
		this.controlTypes.add(controlType);
	}
	
	/**
	 * @param controlType1 the first {@link EDataType} that this component can process.
	 * @param controlType2 the second {@link EDataType} that this component can process.
	 */
	public RMCSControlComponent(EDataType controlType1, EDataType controlType2) {
		this.controlTypes = new ArrayList<>();
		this.controlTypes.add(controlType1);
		this.controlTypes.add(controlType2);
	}
	
	/**
	 * @param controlType1 the first {@link EDataType} that this component can process.
	 * @param controlType2 the second {@link EDataType} that this component can process.
	 * @param controlType3 the third {@link EDataType} that this component can process.
	 */
	public RMCSControlComponent(EDataType controlType1, EDataType controlType2, EDataType controlType3){
		this.controlTypes = new ArrayList<>();
		this.controlTypes.add(controlType1);
		this.controlTypes.add(controlType2);
		this.controlTypes.add(controlType3);
	}
	
	
	/**
	 * @return the Queue, that holds all control-data, that this component should process.
	 */
	protected UniqueLinkedBlockingQueue<IControlData> getControlDataQueue(){
		return this.controlDataQueue;
	}
	
	/**
	 * @return the list of {@link EDataType}s that this component can process.
	 */
	public Collection<EDataType> getTypes() {
		return controlTypes;
	}
	
	/**
	 * @return an element of EControlType, if the Collection controlTypes consists of only one element, otherwise null.
	 */
	public EDataType getSingleType(){
		if(controlTypesSize() == 1)
			for(EDataType controlType : this.controlTypes)
				return controlType;
		return null;
	}
	
	/**
	 * @return the number of {@link EDataType}s that this component can process.
	 */
	public long controlTypesSize(){
		return this.controlTypes.size();
	}
	
	/**
	 * Adds the passed {@link IControlData} to the {@link #controlDataQueue}.
	 * @param controlData the new {@link IControlData} that this component should process.
	 */
	public void updateControlData(IControlData controlData){
		if(controlData == null)
			return;
		
		try {
			this.controlDataQueue.put(controlData);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	

}

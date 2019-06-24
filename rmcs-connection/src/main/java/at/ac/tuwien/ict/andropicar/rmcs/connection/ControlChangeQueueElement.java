package at.ac.tuwien.ict.andropicar.rmcs.connection;

import java.util.concurrent.LinkedBlockingQueue;

import at.ac.tuwien.ict.andropicar.rmcs.data.EDataType;

/**
 * A simple Tuple for storing information on a {@link LinkedBlockingQueue} that is used to inform the main-thread about the change of connection in control of certain control-types.
 * 
 * @author Boeck
 *
 */
public class ControlChangeQueueElement {
	
	/** the ID of the connection that is supposed to be in control of the specified data-type. */
	private long id;
	/** the data-type the specified connection is supposed to be in control of. */
	private EDataType controlChangeType;
	
	
	/**
	 * @param id the ID of the connection that is supposed to be in control of the specified data-type.
	 * @param controlChangeType the data-type the specified connection is supposed to be in control of.
	 */
	public ControlChangeQueueElement(long id, EDataType controlChangeType){
		this.id = id;
		this.controlChangeType = controlChangeType;
	}

	
	/**
	 * @return the ID of the connection that is supposed to be in control of the specified data-type.
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the data-type the specified connection is supposed to be in control of.
	 */
	public EDataType getControlChangeType() {
		return controlChangeType;
	}

}

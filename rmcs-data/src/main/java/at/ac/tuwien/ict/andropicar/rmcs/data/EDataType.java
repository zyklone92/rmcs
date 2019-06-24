package at.ac.tuwien.ict.andropicar.rmcs.data;

/**
 * Holds possible types of data to indicate the type of control or sensor,
 * e.g. for registering listeners or forwarding specific data-sets to certain components / connections.
 * 
 * @author Boeck
 */
public enum EDataType {
	DRIVECONTROL, CAMERACONTROL, LIGHTCONTROL, SENSOR, DISTANCE_SENSOR, VELOCITY_SENSOR;
}

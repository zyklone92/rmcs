package at.ac.tuwien.ict.andropicar.rmcs.ui;

/**
 * The different topics that UI-text is divided into.
 * @author Boeck
 */
public enum EIdentifier {
	
	/** Messages that contain information about the status of the TCP-Connection with the server. */
	CONNECTION,
	/** Messages that contain information about the received messages from a linked phone (through the server). */
	PHONECONTROL,
	/** Messages that contain information about gamepad-input-data. */
	GAMEPADCONTROL,
	/** Messages that contain information about sensor-data. */
	SENSOR;
}

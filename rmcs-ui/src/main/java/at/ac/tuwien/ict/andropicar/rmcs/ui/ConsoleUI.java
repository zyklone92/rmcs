package at.ac.tuwien.ict.andropicar.rmcs.ui;

/**
 * A console based form of display, to check the current status of the different parts of the RMCS.
 * @author Boeck
 */
public class ConsoleUI implements IUI {
	
	/** The object containing information about the status of the TCP-Connection with the server. */
	private UIText connectionText = new UIText(ETextStyle.LOGSTYLE, 500, "No information available yet!");
	/** The object containing information about the received messages from a linked phone (through the server). */
	private UIText phoneControlText = new UIText(ETextStyle.FIXEDSTYLE, 500, "No information available yet!");
	/** The object containing information about gamepad-input-data. */
	private UIText gamepadControlText = new UIText(ETextStyle.FIXEDSTYLE, 500, "No information available yet!");
	/** The object containing information about sensor-data. */
	private UIText sensorText = new UIText(ETextStyle.FIXEDSTYLE, 500, "No information available yet!");
	
	/** The time at which the status of all topics was last printed. */
	private long lastPrinted = 0;

	
	public ConsoleUI() {
		this.connectionText = new UIText(ETextStyle.LOGSTYLE, 500, "No information available yet!");
		this.phoneControlText = new UIText(ETextStyle.FIXEDSTYLE, 500, "No information available yet!");
		this.gamepadControlText = new UIText(ETextStyle.FIXEDSTYLE, 500, "No information available yet!");
		this.sensorText = new UIText(ETextStyle.FIXEDSTYLE, 500, "No information available yet!");
	}

	/**
	 * {@inheritDoc}
	 * It also displays the new information after updating, if it has not been updated in the last 500ms.
	 */
	@Override
	public synchronized void update(EIdentifier identifier, String text) {
		if(identifier.equals(EIdentifier.CONNECTION))
			this.connectionText.setText(text);
		if(identifier.equals(EIdentifier.PHONECONTROL))
			this.phoneControlText.setText(text);
		if(identifier.equals(EIdentifier.GAMEPADCONTROL))
			this.gamepadControlText.setText(text);
		if(identifier.equals(EIdentifier.SENSOR))
			this.sensorText.setText(text);
		
		display();
	}
	
	/**
	 * Displays the status of all topics, if it has not been displayed in the last 500ms.
	 */
	private void display() {
		if((System.currentTimeMillis() - this.lastPrinted) > 500)
		{
			System.out.println("\n\n\n\n");
			System.out.println("Connection information:\n" + this.connectionText.getText());
			System.out.println("\nPhoneControl information:\n" + this.phoneControlText.getText());
			System.out.println("\nGamepadControl information:\n" + this.gamepadControlText.getText());
			System.out.println("\nSensor information:\n" + this.sensorText.getText());
			this.lastPrinted = System.currentTimeMillis();
		}
	}

}

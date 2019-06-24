package at.ac.tuwien.ict.andropicar.rmcs.ui;


/**
 * Provides the ability for any UI that implements this interface to get updated about new information to be displayed.
 * @author Boeck
 */
public interface IUI {
	
	/**
	 * Updates a specific topic({@link EIdentifier}) of the UI with new information.
	 * @param identifier the topic of the new information.
	 * @param text the new information to be displayed.
	 */
	public void update(EIdentifier identifier, String text);

}

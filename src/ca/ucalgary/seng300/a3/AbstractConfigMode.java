package ca.ucalgary.seng300.a3;


/**
 * This is an abstract class to guide the creation of new modes 
 *   that will be used in the configuration of the vending machine. 
 * @author ttdjo
 *
 */
public abstract class AbstractConfigMode {
	protected String message;
	
	/**
	 * A method to return a string that describes what the mode is meant to configure.
	 * @return a String that will be displayed with all other modeName's as options for the user to select.
	 */
	public abstract String modeName();
	
	/**
	 * a method to determine the action to take when the enter key on the panel has been pressed
	 * if a configuration has been completed, method returns true to indicate to the caller to reset the panel
	 * @return True if when a configuration is completed so the panel can be reset.
	 */
	public abstract boolean enterKeyPressed();
	
	/**
	 * A method to changed the state of the implemented mode based on what action is required when a button is pressed
	 * @param index - an integer indicating which button on the configuration panel was pressed.  
	 */
	public abstract void buttonPressed(int index);
}

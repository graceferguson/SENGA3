/**
 * SENG 300 Group Assignment 3
 * Group 
 * 
 * Class to handle text changes to the coin input field. Updates the value with
 * every key stroke so that values can be added when the user clicks the 
 * enter coins button.
 * Handles NumberFormat Exceptions by returning a value that is not valid
 * for the Canadian coins used here.
 */

package ca.ucalgary.seng300.a3;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class CoinInputGUIDocListener implements DocumentListener {

	private JTextField textbox;
	private String message = "";
	
	/**
	 * Creates a listener for the JTextField used to enter values
	 * of coins to insert.
	 * @param box the JTextField box
	 */
	public CoinInputGUIDocListener(JTextField box) {
		textbox = box;
	}
	
	@Override
	public void changedUpdate(DocumentEvent arg0) {
		update();
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		update();
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		update();
	}
	
	/**
	 * Method that gets called whenever the text in the box is changed.
	 * Stores the most recent entry to the box
	 */
	private void update() {
		message = textbox.getText();
		//System.out.println(message);
	}
	
	/**
	 * Returns the current value in the text box as an integer
	 * If the text is not a number (ie. NumberFormat Exception) the value
	 * 2 is returned (an invalid coin value for Canadian coins)
	 * @return the value in the textbox as an integer, or 2
	 */
	public int getValue() {
		int value = 0;
		
		try {
			value = Integer.parseInt(message);
		} catch (NumberFormatException e) {
			System.out.println("Not a number! I'm not putting anything into the vending machine");
		}
		
		return value;
	}

} // end class

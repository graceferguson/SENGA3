/**
 * SENG 300 Group Assignment 3
 * Group 
 * 
 * Class to handle mouse clicks of the GUI buttons that correspond
 * to pop selection buttons on the vending machine
 */

package ca.ucalgary.seng300.a3;

import java.awt.event.*;
import org.lsmr.vending.hardware.*;


public class PopButtonActionListener implements ActionListener {

	private int buttonIndex;
	private VendingMachine vend;
	private VendCommunicator vcom;
	private GUI graph;
	
	/**
	 * Creates an action listener for the JButtons that correspond
	 * to the pop selection buttons on the vending machine
	 * @param i the index of the button in the array of buttons
	 * @param vending the vending machine associated with the GUI
	 */
	public PopButtonActionListener(int i, VendingMachine vending, GUI gui, VendCommunicator vc) {
		buttonIndex = i;
		vend = vending;
		vcom = vc;
		graph = gui;
	}
	
	/**
	 * Calls the pressed method on the correct button on the vending machine
	 * when the button is pressed in the GUI
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		graph.setLastButtonPressed(buttonIndex);
		vend.getSelectionButton(buttonIndex).press();
		double myVal = vcom.getCredit()/100.0;
		graph.setDisplay("Credit: $" + myVal);
		
		

	}

}

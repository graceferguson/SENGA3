/**
 * SENG 300 Group Assignment 3
 * Group 
 * 
 * Class to handle unloading coins from the coin return
 * when the unload coins button is pressed
 */

package ca.ucalgary.seng300.a3;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.lsmr.vending.hardware.VendingMachine;

public class RetButtonActionListener implements ActionListener {

	private VendingMachine vend;
	
	/**
	 * Creates a listener for the unload coins GUI button 
	 * @param vending the vending machine linked to the GUI
	 */
	public RetButtonActionListener(VendingMachine vending) {
		vend = vending;
	}
	
	/**
	 * Unloads the coins from the vending machine when the 
	 * unload coins button is pressed
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		vend.getCoinReturn().unload();
	}

} //end class

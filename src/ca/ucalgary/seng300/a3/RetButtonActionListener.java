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
	private GUI gui;
	private GUICoinReturnListener l;
	
	/**
	 * Creates a listener for the unload coins GUI button 
	 * @param vending the vending machine linked to the GUI
	 */
	public RetButtonActionListener(VendingMachine vending, GUI mygui, GUICoinReturnListener listener) {
		vend = vending;
		gui = mygui;
		l = listener;
	}
	
	/**
	 * Unloads the coins from the vending machine when the 
	 * unload coins button is pressed
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		vend.getCoinReturn().unload();
		gui.setCoinReturnVal(0);
		l.clearTotal();
	}

} //end class

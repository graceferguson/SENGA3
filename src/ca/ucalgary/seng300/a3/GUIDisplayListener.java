/**
 * SENG 300 Group Assignment 3
 * Group 
 * 
 * Class to update the GUI display as the vending machine display changes
 */

package ca.ucalgary.seng300.a3;

import org.lsmr.vending.hardware.AbstractHardware;
import org.lsmr.vending.hardware.AbstractHardwareListener;
import org.lsmr.vending.hardware.Display;
import javax.swing.JLabel;
import org.lsmr.vending.hardware.*;

public class GUIDisplayListener implements DisplayListener {

	private VendingMachine vend;
	private GUI gui;
	
	/**
	 * Creates a listener for the vending machine display and registers it
	 * with the vending machine display hardware
	 * @param vending the vending machine to listen to
	 * @param mygui the GUI that this listener modifies
	 */
	public GUIDisplayListener(VendingMachine vending, GUI mygui) {
		vend = vending;
		vend.getDisplay().register(this);
		gui = mygui;
	}
	
	@Override
	public void enabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		// Do nothing		
	}

	@Override
	public void disabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		// Do nothing		
	}

	/**
	 * Updates the GUI JLabel whenever the vending machine display changes
	 * using the GUI method
	 */
	@Override
	public void messageChange(Display display, String oldMessage, String newMessage) {
		gui.setDisplay(newMessage);
	}

} //end class

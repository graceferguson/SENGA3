/**
 * SENG 300 Group Assignment 3
 * Group 
 * 
 * Class to handle the indicator light changes. Updates the GUI indicator
 * lights when the lights on the vending machine change.
 */

package ca.ucalgary.seng300.a3;

import org.lsmr.vending.hardware.*;

public class GUIIndicatorLightListener implements IndicatorLightListener {

	private GUI gui;
	private VendingMachine vend;
	
	/**
	 * Creates a listener for the indicator lights and updates the GUI JLabels
	 * @param mygui GUI being modified by this listener
	 * @param vending the vending machine to listen to
	 */
	public GUIIndicatorLightListener(GUI mygui, VendingMachine vending) {
		vend = vending;
		gui = mygui;
		vend.getOutOfOrderLight().register(this);
		vend.getExactChangeLight().register(this);
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
	 * Checks which light has been activated and turns the corresponding
	 * GUI light on using the GUI method
	 */
	@Override
	public void activated(IndicatorLight light) {
		if (vend.getOutOfOrderLight() == light) {
			gui.orderLightOn();
		} else if (vend.getExactChangeLight() == light) {
			gui.changeLightOn();
		}
	}

	/**
	 * Checks which light has been deactivated and turns the corresponding
	 * GUI light off using the GUI method
	 */
	@Override
	public void deactivated(IndicatorLight light) {
		if (vend.getOutOfOrderLight() == light) {
			gui.orderLightOff();
		} else if (vend.getExactChangeLight() == light) {
			gui.changeLightOff();
		}
	}

} // end class

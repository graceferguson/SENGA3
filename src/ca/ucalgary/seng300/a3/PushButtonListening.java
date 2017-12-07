/**
 * SENG 300 Group Assignment 3
 * 
 * Class for push button listener 
 */

package ca.ucalgary.seng300.a3;

import org.lsmr.vending.hardware.AbstractHardware;
import org.lsmr.vending.hardware.AbstractHardwareListener;
import org.lsmr.vending.hardware.PushButton;
import org.lsmr.vending.hardware.PushButtonListener;
import org.lsmr.vending.hardware.VendingMachine;

public class PushButtonListening implements PushButtonListener {
	
	private VendingMachine vm;
	private int index;
	
	public PushButtonListening(VendingMachine vm, int i) {
		this.vm = vm;
		this.index = i;
	}

	@Override
	public void enabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		// PushButton ignores the enabled/disabled state
	}

	@Override
	public void disabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		// PushButton ignores the enabled/disabled state
	}
	
    /**
     * An event that is announced to the listener when the indicated button has
     * been pressed.
     * 
     * @param button
     *            The device on which the event occurred.
     */
	@Override
	public void pressed(PushButton button) {
		vm.getConfigurationPanel().getButton(index);
		
	}
}

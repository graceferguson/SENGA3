package ca.ucalgary.seng300.a3;

import org.lsmr.vending.hardware.AbstractHardware;
import org.lsmr.vending.hardware.AbstractHardwareListener;
import org.lsmr.vending.hardware.PushButton;
import org.lsmr.vending.hardware.PushButtonListener;

public class PushButtonListening implements PushButtonListener {
	
	private VendCommunicator comm;
	
	public PushButtonListening(VendCommunicator comm) {
		this.comm = comm;
		
	}

	@Override
	public void enabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		
	}

	@Override
	public void disabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		
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
		comm.determineButtonAction(button);
		
	}
}

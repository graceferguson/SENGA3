package ca.ucalgary.seng300.a2;

import org.lsmr.vending.hardware.AbstractHardware;
import org.lsmr.vending.hardware.AbstractHardwareListener;
import org.lsmr.vending.hardware.Lock;

public class LockListener implements org.lsmr.vending.hardware.LockListener{

	@Override
	public void enabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		// TODO Auto-generated method stub
		
	}
	
	private void panelLocked() {
		
	}
	
	private void panelUnlocked() {
		
	}

	@Override
	public void locked(Lock lock) {
		// TODO Auto-generated method stub
		panelLocked();
	}

	@Override
	public void unlocked(Lock lock) {
		// TODO Auto-generated method stub
		panelUnlocked();
	}

}

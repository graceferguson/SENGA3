package ca.ucalgary.seng300.a3;

import org.lsmr.vending.hardware.AbstractHardware;
import org.lsmr.vending.hardware.AbstractHardwareListener;
import org.lsmr.vending.hardware.Lock;
import org.lsmr.vending.hardware.LockListener;
import org.lsmr.vending.hardware.VendingMachine;


public class GUILockPanelListener implements LockListener {
	
	private String text = "Lock";
	private GUI gui;
	private VendingMachine vend;
	private String prevState = "Lock";
	
	
	public GUILockPanelListener(GUI mygui, VendingMachine vending) {
		vend = vending;
		gui = mygui;
		vend.getLock().register(this);
		
	}

	@Override
	public void enabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void locked(Lock lock) {
		text = "Unlock"; 
		gui.changeTextLock(text);
		
	}

	@Override
	public void unlocked(Lock lock) {
//		if (prevState == "Unlock") {
//			prevState = text;
//			text = "Lock";
//			gui.changeTextLock(text);
//		}else {
//			prevState = text;
			text = "Lock";
			gui.changeTextLock(text);
	
			
//		}
		
		
	}

}

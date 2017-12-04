package ca.ucalgary.seng300.a3;

import java.io.IOException;

import org.lsmr.vending.hardware.AbstractHardware;
import org.lsmr.vending.hardware.AbstractHardwareListener;
import org.lsmr.vending.hardware.Lock;

public class LockPanelListener implements org.lsmr.vending.hardware.LockListener{

	
	private VendCommunicator communicator;
	
	public LockPanelListener(VendCommunicator vend) {
		this.communicator = vend;
	}
	
	@Override
	public void enabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		// TODO Auto-generated method stub
		
	}
	/**
	* Function that disables safety on the vending machine and logs it
	*/
	private void panelLocked() {
	// call event log here 
		
		String currentTime = LogFile.df.format(LogFile.dateobj);
		String classType  = this.getClass().getName();
		String newMessage = classType + "\t" + "control panel locked";
		try {
			LogFile.writeLog( currentTime +"\t" + newMessage);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	/**
	* Function that enables safety on the vending machine and logs it
	*/	
	private void panelUnlocked() {
		String currentTime = LogFile.df.format(LogFile.dateobj);
		String classType  = this.getClass().getName();
		String newMessage = classType + "\t" + "control panel unlocked";
		try {
			LogFile.writeLog( currentTime +"\t" + newMessage);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	@Override
	public void locked(Lock lock) {
		// TODO Auto-generated method stub
		communicator.disableSafety();
		panelLocked();
		
	}

	@Override
	public void unlocked(Lock lock) {
		// TODO Auto-generated method stub
		communicator.enableSafety();
		panelUnlocked();
		
	}

}

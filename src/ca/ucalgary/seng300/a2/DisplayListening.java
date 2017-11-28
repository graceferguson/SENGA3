/**
 * Function for delivery chute listener
 * Elodie Boudes 10171818, Grace Ferguson 30004869, 
 * Tae Chyung 10139101, Karndeep Dhami 10031989, 
 * Andrew Garcia-Corley 10015169 & Michael de Grood 10134884
 */

package  ca.ucalgary.seng300.a2;

import java.io.IOException;

import org.lsmr.vending.hardware.AbstractHardware;
import org.lsmr.vending.hardware.AbstractHardwareListener;
import org.lsmr.vending.hardware.Display;
import org.lsmr.vending.hardware.DisplayListener;

public class DisplayListening implements DisplayListener {
	private boolean isOn;
	private String prevMessage = "";
	private String currMessage = "";
	
	public DisplayListening() {
		isOn = true;
	
	}

	@Override
	public void enabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		isOn = true;
		
	}

	@Override
	public void disabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		isOn = false;
		
	}

	@Override
	public void messageChange(Display display, String oldMessage, String newMessage) {
		prevMessage = oldMessage;
		currMessage = newMessage;
		try {
			LogFile.writeLog(LogFile.df.format(LogFile.dateobj) + "\t" + this.getClass().getName() + "\t" + currMessage);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}
	
	/**
	 * Returns current status of display
	 * @return isOn
	 */ 
	public boolean isOn() {
		return isOn;
		
	}
	
	/**
	 * For testing purposes
	 * @return
	 */
	public String getPrevMessage() {
		return prevMessage;
		
	}
	
	/**
	 * For testing purposes
	 * @return
	 */
	public String getCurrMessage() {
		return currMessage;
		
	}
	
	/**
	 * For testing purposes 
	 * @param msg
	 */
	public void setCurrMessage(String msg) {
		currMessage = msg;
		
	}
	
	/**
	 * for testing purposes
	 * @param msg
	 */
	public void setPrevMessage(String msg) {
		prevMessage = msg;
		
	}



}

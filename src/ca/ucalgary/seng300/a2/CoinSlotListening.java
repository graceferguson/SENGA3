/**
 * Function for coin slot listener
 * Elodie Boudes 10171818, Grace Ferguson 30004869, 
 * Tae Chyung 10139101, Karndeep Dhami 10031989, 
 * Andrew Garcia-Corley 10015169 & Michael de Grood 10134884
 */
package ca.ucalgary.seng300.a2;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.lsmr.vending.*;
import org.lsmr.vending.hardware.*;

/**
 * Listener class for coin slot
 */
public class CoinSlotListening implements CoinSlotListener {
	private boolean isOn;
	
	static DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
    static Date dateobj = new Date();

	public CoinSlotListening() {
		isOn = true;
	}

	/**
	 * method for enabling listener
	 */
	public void enabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		isOn = true;
	}

	/**
	 * method for disabling listener
	 */
	public void disabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		isOn = false;
	}

	/**
	 * method for determining if the coin inserted is valid 
	 */
	public void validCoinInserted(CoinSlot slot, Coin coin) {
		try {
			LogFile.writeLog("\n"+df.format(dateobj) + "\t" + getClass().getName() + "\t" + "coin inserted");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * method for determining if the coin inserted is invalid 
	 */
	public void coinRejected(CoinSlot slot, Coin coin) {
	}

	/**
	 * method for checking state of listener
	 * 
	 * @return boolean state of listener
	 */
	public boolean isOn() {
		return isOn;
	}

}
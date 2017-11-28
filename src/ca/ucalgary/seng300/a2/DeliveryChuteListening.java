/**
 * Function for delivery chute listener
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

public class DeliveryChuteListening implements DeliveryChuteListener {

	boolean active;
	PopCan[] itemsReturned; // now Popcan instead of Deliverable since changes
							// hardware by client

	static DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
	static Date dateobj = new Date();

	/**
	 * method for enabling listener
	 */
	public void enabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		active = true;
	}

	/**
	 * method for disabling listener
	 */
	public void disabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		active = false;
	}

	/**
	 * When an item is sent to the delivery chute, empties the chute, and checks
	 * if the item was a popcan and outputs it
	 */
	public void itemDelivered(DeliveryChute chute) {
		itemsReturned = chute.removeItems();
		for (int i = 0; i < itemsReturned.length; i++) {
			if (itemsReturned[i] instanceof PopCan) {
				try {
					LogFile.writeLog("\n" + df.format(dateobj) + "\t" + getClass().getName() + "\t" + "pop dispensed");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * method for determining if the door is open
	 */
	public void doorOpened(DeliveryChute chute) {

	}

	/**
	 * method for determining if the door is closed
	 * 
	 */
	public void doorClosed(DeliveryChute chute) {

	}

	/**
	 * method for determining if the chute is full
	 * 
	 */
	public void chuteFull(DeliveryChute chute) {

	}

}
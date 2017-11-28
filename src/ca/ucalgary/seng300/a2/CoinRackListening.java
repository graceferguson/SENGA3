/**
 * Function for coin rack  listener
 * Elodie Boudes 10171818, Grace Ferguson 30004869, 
 * Tae Chyung 10139101, Karndeep Dhami 10031989, 
 * Andrew Garcia-Corley 10015169 & Michael de Grood 10134884
 */
package ca.ucalgary.seng300.a2;

import org.lsmr.vending.*;
import org.lsmr.vending.hardware.*;

/**
 * Listener for the coin rack. Take in messages about the state of the coin rack
 * and convey them to others
 */
public class CoinRackListening implements CoinRackListener {
	private boolean isOn;
	private int coinCount;
	private int value;

	/**
	 * constructor for coin rack listener object, stores value of coins in rack
	 * 
	 * @param value
	 *            value of coins in rack
	 */
	public CoinRackListening(int value) {
		this.value = value;
	}

	/**
	 * method for enabling coin rack
	 */
	public void enabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		isOn = true;
	}

	/**
	 * method for disabling coin rack
	 */
	public void disabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		isOn = false;
	}

	/**
	 * method to determine is the coin rack is full 
	 */
	public void coinsFull(CoinRack rack) {
	}

	/**
	 * method to determine is the coin rack is empty 
	 */
	public void coinsEmpty(CoinRack rack) {
	}

	/**
	 * method for adding coin
	 */
	public void coinAdded(CoinRack rack, Coin coin) {
		coinCount++;
	}

	/**
	 * method for removing all coins
	 */
	public void coinRemoved(CoinRack rack, Coin coin) {
		coinCount--;
	}

	/**
	 * method for loading coins in to rack
	 */
	public void coinsLoaded(CoinRack rack, Coin... coins) {
		coinCount += coins.length;
	}

	/**
	 * method for unloading coins
	 */
	public void coinsUnloaded(CoinRack rack, Coin... coins) {
		coinCount -= coins.length;
	}

	/**
	 * method to retrieve value of coin rack
	 * 
	 * @return value integer value of coins in rack
	 */
	public int getValue() {
		return value;
	}

	/**
	 * method for checking state of listener
	 * 
	 * @return boolean representing state of listener
	 */
	public boolean isOn() {
		return isOn;
	}

	/**
	 * method for getting amount of coins in rack
	 * 
	 * @return integer amount of coins in rack
	 */
	public int getCoins() {
		return coinCount;
	}

	/**
	 * 
	 * @return true if there are coins in the rack
	 */
	public boolean hasCoins() {
		return coinCount > 0;
	}

}

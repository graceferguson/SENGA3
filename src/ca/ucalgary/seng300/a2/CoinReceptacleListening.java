/**
 * Function for coin receptacle listener
 * Elodie Boudes 10171818, Grace Ferguson 30004869, 
 * Tae Chyung 10139101, Karndeep Dhami 10031989, 
 * Andrew Garcia-Corley 10015169 & Michael de Grood 10134884
 */

package ca.ucalgary.seng300.a2;

import org.lsmr.vending.*;
import org.lsmr.vending.hardware.*;

public class CoinReceptacleListening implements CoinReceptacleListener {
	private boolean isOn;
	private int value;
	private int coinCount;
	private String message;
	private VendCommunicator communicator;
	private emptyMsgLoop msgLoop;

	/**
	 * 
	 * @param reCap
	 */
	public CoinReceptacleListening(int reCap, VendCommunicator communicator, emptyMsgLoop msgLoop) {
		isOn = true;
		value = 0;
		coinCount = 0;
		message = "";
		this.communicator = communicator;
		this.msgLoop = msgLoop;
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
	 * method adds coin and value of the coin
	 */
	public void coinAdded(CoinReceptacle receptacle, Coin coin) {
		coinCount++;
		if(value == 0)
		{
			msgLoop.interruptThread();
		}
		value += coin.getValue();
		message = "Credit: "+ value;
		communicator.displayMsg(message);
	}

	/**
	 * method for removing all coins
	 */
	public void coinsRemoved(CoinReceptacle receptacle) {
		value = 0;
		coinCount = 0;
		msgLoop.reactivateMsg();
	}

	/**
	 * method to determine if the coin receptacle is full 
	 */
	public void coinsFull(CoinReceptacle receptacle) {
	}

	/**
	 * method for loading coins in to receptacle
	 */
	public void coinsLoaded(CoinReceptacle receptacle, Coin... coins) {
		for (Coin coin : coins) {
			value += coin.getValue();
			coinCount++;
		}
	}

	/**
	 * method for unloading coins
	 */
	public void coinsUnloaded(CoinReceptacle receptacle, Coin... coins) {
		for (Coin coin : coins) {
			value -= coin.getValue();
			coinCount--;
		}
	}

	/**
	 * subtracts amount spent when purchase is made
	 * 
	 * @param amount
	 *            amount of purchase
	 */
	public void Purchase(int amount) {
		value -= amount;
		if(value <= 0)
		{
			msgLoop.reactivateMsg();
		}
	}

	/**
	 * method to get value of coins in receptacle
	 * 
	 * @return value of coins
	 */
	public int getValue() {
		return value;
	}
	
	public void setValue(int value){
		this.value = value;
		if(value == 0 && msgLoop.reactivateCheck())
		{
			msgLoop.reactivateMsg();
		}
	}

	/**
	 * method to check state of listener
	 * 
	 * @return boolean state of listener
	 */
	public boolean isOn() {
		return isOn;
	}

	/**
	 * method to get amount of coins
	 * 
	 * @return integer coin amount
	 */
	public int coinCount() {
		return coinCount;
	}

}

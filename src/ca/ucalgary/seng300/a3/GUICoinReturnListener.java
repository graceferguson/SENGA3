/**
 * SENG 300 Group Assignment 3
 * Group 
 * 
 * Class to update GUI coin return value with the coins in the 
 * vending machine coin return
 */

package ca.ucalgary.seng300.a3;

import org.lsmr.vending.Coin;
import org.lsmr.vending.hardware.*;

public class GUICoinReturnListener implements CoinReturnListener {

	private int returnTotal = 0;
	private VendingMachine vend;
	private GUI gui;
	
	/**
	 * Creates a listener for the vending machine coin return
	 * @param vending
	 * @param mygui
	 * @param mylabel
	 */
	public GUICoinReturnListener(VendingMachine vending, GUI mygui) {
		vend = vending;
		gui = mygui;
		
		vend.getCoinReturn().register(this);
	}
	
	@Override
	public void enabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		// Do nothing		
	}

	@Override
	public void disabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		// Do nothing		
	}

	/**
	 * Tracks the value of the coins in the coin return and updates the
	 * GUI coin return value
	 */
	@Override
	public void coinsDelivered(CoinReturn coinReturn, Coin[] coins) {
		for (int i = 0; i < coins.length; i++) {
			returnTotal += coins[i].getValue();
		}

		gui.setCoinReturnVal(returnTotal);
	}

	@Override
	public void returnIsFull(CoinReturn coinReturn) {
		// TODO Auto-generated method stub
	}
	
	public void clearTotal() {
		returnTotal = 0;
	}

}// end class

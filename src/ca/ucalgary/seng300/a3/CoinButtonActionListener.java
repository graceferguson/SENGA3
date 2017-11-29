/**
 * SENG 300 Group Assignment 3
 * Group 
 * 
 * Class to handle mouse clicks of the GUI button that allows the user
 * to try to enter coins into the vending machine
 */

package ca.ucalgary.seng300.a3;

import java.awt.event.*;
import org.lsmr.vending.hardware.*;
import org.lsmr.vending.*;

public class CoinButtonActionListener implements ActionListener {

	private VendingMachine vend;
	private int coinValue;
	
	/**
	 * Creates an action listener for the JButton that allows the user of
	 * the GUI to enter coins into the vending machine
	 * @param i the value of the coin the user wishes to enter
	 * @param vending the vending machine associated with the GUI
	 */
	public CoinButtonActionListener(int i, VendingMachine vending) {
		vend = vending;
		coinValue = i;
	}
	
	/**
	 * Creates a coin with the value the user entered and inserts it into
	 * the vending machine's coin slot
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			vend.getCoinSlot().addCoin(new Coin(coinValue));
		} catch (DisabledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

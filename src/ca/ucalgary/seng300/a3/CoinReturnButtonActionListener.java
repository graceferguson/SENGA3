/**
 * SENG 300 Group Assignment 3
 * Group 
 * 
 * Class to handle mouse clicks of the GUI coin return button
 * 
 ************** Ready for future hardware addition of a coin return button
 */

package ca.ucalgary.seng300.a3;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.lsmr.vending.hardware.VendingMachine;

import ca.ucalgary.seng300.a3.VendCommunicator;

public class CoinReturnButtonActionListener implements ActionListener {
	VendingMachine vm;
	
	public CoinReturnButtonActionListener(VendingMachine vend) {
		vm = vend;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		//vm.getCoinReturnButton().press();
	}

}

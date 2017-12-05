/**
 * SENG 300 Group Assignment 3
 * Group 
 * 
 * Class to handle mouse clicks of the GUI button that allows the user
 * to try to enter coins into the vending machine
 */

package ca.ucalgary.seng300.a3;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.lsmr.vending.hardware.VendingMachine;

import ca.ucalgary.seng300.a2.VendCommunicator;

public class CoinReturnButtonActionListener implements ActionListener {
	VendingMachine vm;
	GUI gui;
	VendCommunicator vc;
	int ret = 0;

	public CoinReturnButtonActionListener(VendingMachine vend, GUI graph, VendCommunicator vcom) {
		vm = vend;
		gui = graph;
		vc = vcom;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		//dispense all the credit entered thus far as change
		vc.giveChange(vc.getReceptacle().getValue());
		//update the display right away
		gui.setDisplay("Credit: $0.00");
		//empty receptacle of coins
		vm.getCoinReceptacle().unload();
	}

}

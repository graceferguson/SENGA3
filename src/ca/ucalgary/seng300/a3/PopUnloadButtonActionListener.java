/**
 * SENG 300 Group Assignment 3
 * Group 
 * 
 * Class to update GUI coin return value with the coins in the 
 * vending machine coin return
 */

package ca.ucalgary.seng300.a3;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.lsmr.vending.hardware.VendingMachine;

public class PopUnloadButtonActionListener implements ActionListener {

	private VendingMachine vend;
	private GUI gui;
	private GUIPopReturnListener l;
	
	public PopUnloadButtonActionListener(VendingMachine vending, GUI mygui, GUIPopReturnListener listener) {
		vend = vending;
		gui = mygui;
		l = listener;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		vend.getDeliveryChute().removeItems();
		gui.setPopReturnVal("(None)");
		l.clearItemsInChute();
		
	}

}

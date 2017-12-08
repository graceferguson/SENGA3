package ca.ucalgary.seng300.a3;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;

import javax.swing.JButton;

import org.lsmr.vending.hardware.VendingMachine;

public class LockUnlockButtonListener implements ActionListener{

	
	private VendingMachine vend;
	private GUI gui;
	private GUILockPanelListener l;
	
	public LockUnlockButtonListener(VendingMachine vending, GUI mygui, GUILockPanelListener listener) {
		vend = vending;
		gui = mygui;
		l = listener;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String text;
		 if (arg0.getSource() instanceof JButton) {
            text = ((JButton) arg0.getSource()).getText();
            if (text.equals("Lock")) {
            		vend.getLock().lock();
            		gui.getGUIConfig().init();
            }
            else {
            	vend.getLock().unlock();
            	gui.getGUIConfig().dispose();
            }
         }
	
		
		
	}

}
	
	


/**
 * SENG 300 Group Assignment 3
 * 
 * Class to handle actions on push buttons of config panel GUI
 */

package ca.ucalgary.seng300.a3;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.lsmr.vending.hardware.ConfigurationPanel;


/**
 * Listens to the Configuration Panel actions
 *
 */
public class GUIConfigListening implements ActionListener {
		
	private int buttonIndex;
	private ConfigurationPanel configPanel;
	
	public GUIConfigListening(int i, ConfigurationPanel panel) {
		buttonIndex = i;
		configPanel = panel;
	}

	/**
	 * Signals that a button was pushed on the config panel 
	 * Press appropriate button depending on if it was the enter button or not
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		// For testing
		//System.out.println(event.getActionCommand());
		
		// Gets action from GUI and calls press on button
		if(event.getActionCommand().equals("Enter")) {
			configPanel.getEnterButton().press();
		} else {
			configPanel.getButton(buttonIndex).press();
		}
		
	}
	
}

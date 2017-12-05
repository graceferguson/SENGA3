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

	
	@Override
	public void actionPerformed(ActionEvent event) {
		
		System.out.println(event.getActionCommand());
		
		if(event.getActionCommand().equals("Enter")) {
			configPanel.getEnterButton().press();
		} else {
			configPanel.getButton(buttonIndex).press();
		}
		
	}
	
}

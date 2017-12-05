package ca.ucalgary.seng300.a3;

import org.lsmr.vending.hardware.AbstractHardware;
import org.lsmr.vending.hardware.AbstractHardwareListener;
import org.lsmr.vending.hardware.Display;
import org.lsmr.vending.hardware.DisplayListener;

public class ConfigPanelDisplayListening implements DisplayListener{
	private boolean isOn;
	private GUIConfigPanel configPanelGUI;
	
	public ConfigPanelDisplayListening(GUIConfigPanel config) {
		configPanelGUI = config;
		isOn = true;
	}

	@Override
	public void enabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		isOn = true;
		
	}

	@Override
	public void disabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		isOn = false;
		
	}

	@Override
	public void messageChange(Display display, String oldMessage, String newMessage) {
		configPanelGUI.updateDisplay(newMessage);
		
	}
	//FOR REGISTERING 
	//GUIConfigPanel configPanelGUI = new GUIConfigPanel();
	//ConfigPanelDisplayListening configPanelDisplayListening = new ConfigPanelDisplayListening(configPanelGUI);
	//machine.getDisplay().register(configPanelDisplayListening);

}

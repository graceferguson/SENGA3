package ca.ucalgary.seng300.a3;

import java.util.*;
import org.lsmr.vending.hardware.*;

public class ConfigPanelLogic {
	protected int mode;
	protected final int MAX_MODE = 1;   // the number of modes currently available
	protected int input;		// input value from the user
	protected VendingMachine vend;
	protected ConfigPopPrices configPopPriceLogic;
	protected ConfigPanelLogicListener listener;
	
	private ConfigPanelLogic() {
	}
	
	private static class ConfigPanelLogicHolder {
		private static final ConfigPanelLogic INSTANCE = new ConfigPanelLogic();
	}
	
	public static ConfigPanelLogic getInstance() {
		return ConfigPanelLogicHolder.INSTANCE;
	}
	
	public void initializeCP(VendingMachine vending) {
		vend = vending;
		mode = 0;
		input = -1;
		configPopPriceLogic = ConfigPopPrices.getInstance();
		configPopPriceLogic.initializeCPP(vend.getNumberOfSelectionButtons());
		this.displayConfigMessage();
	}
	
	/**
	 * A method to branch into the specific display message depending on the current mode
	 */
	private void displayConfigMessage() {
		String message = "";

		if (mode == 0) {	// all configuration options for the control panel
			message += this.displayPanelOptions();
		}
		else if (mode == 1) { // mode for configuring pop prices
			String[] names = new String[vend.getNumberOfSelectionButtons()];
			int[] prices = new int[vend.getNumberOfSelectionButtons()];
			for (int i = 0; i < vend.getNumberOfSelectionButtons(); i++) {
				names[i] = vend.getPopKindName(i);
				prices[i] = vend.getPopKindCost(i);
			}
			message = configPopPriceLogic.modeInstructions(names, prices);
		}
		else {
			//throw new SimulationException("Invalid Configuration panel mode in ConfigPanelLogic : mode = " + mode); 
		}
		
		vend.getConfigurationPanel().getDisplay().display(message);
	}
	
	/**
	 * a method to display the options available for configurations 
	 */
	private String displayPanelOptions() {
		String message = "Select which aspect to configure: \n ";
		message += "0 - " + configPopPriceLogic.modeName() + "\n ";
		//message += "1 - "; // TODO more messages to display options 
		if (input < 0)
			message += "Selection: ";
		else 
			message += "Selection: " + input;
		return message;
	}
	
	/**
	 * A method to determine what action should be taken when a button on the configuration panel is pressed
	 * @param button - the reference to the button that was pressed 
	 */
	public void configButtonAction(PushButton button) {
		if (vend.getLock().isLocked() == true) {
			if (button == vend.getConfigurationPanel().getEnterButton()) {
				if (mode == 0) {
					if (input + 1 <= MAX_MODE) {
						mode = input + 1; // since the selections start at 0 but the new modes start at 1.
					}
					input = -1;
				}
				else if (mode == 1) {
					boolean finished = configPopPriceLogic.enterKeyPressed();
					if (finished) {
						this.reconfigureVend();
						configPopPriceLogic.reset();
						mode = 0;
					}
				}
				else {
					//throw new SimulationException("Invalid Configuration panel mode in ConfigPanelLogic : mode = " + mode);
				}
			}
			
			else {
				for (int i = 0; i < 37; i++) {
					if (button == vend.getConfigurationPanel().getButton(i)) {  
						if (mode == 0) {
							input = i;
						}
						else if (mode == 1) {
							configPopPriceLogic.buttonPressed(i);
						}
						else {
							//throw new SimulationException("Invalid Configuration panel mode in ConfigPanelLogic : mode = " + mode);
						}
					}
				}
			}
			this.displayConfigMessage();		// re-display the current state of the panel
		}
	}
	
	/**
	 * The method that applies the inputs made by the user to the configuration of the vending machine.
	 */
	private void reconfigureVend() {
		List<String> popCanNames = new ArrayList<String>(vend.getNumberOfSelectionButtons());
		List<Integer> popCanCosts = new ArrayList<Integer>(vend.getNumberOfSelectionButtons());
		
		int[] changes = configPopPriceLogic.getChanges();
		
		for (int i = 0; i < vend.getNumberOfSelectionButtons(); i++) {
			popCanNames.add(i, vend.getPopKindName(i));
		}
		
		for (int i = 0; i < vend.getNumberOfSelectionButtons(); i++) {
			if (i == changes[0])
				popCanCosts.add(i, changes[1]);
			else 
				popCanCosts.add(i, vend.getPopKindCost(i));
		}
		vend.configure(popCanNames, popCanCosts);
		listener.priceUpdate(changes[0], changes[1]);
	}
	
	public void register(ConfigPanelLogicListener listen) {
		listener = listen;
	}
}

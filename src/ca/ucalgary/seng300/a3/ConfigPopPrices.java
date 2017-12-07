package ca.ucalgary.seng300.a3;

import org.lsmr.vending.hardware.SimulationException;

// 34th button is b

public class ConfigPopPrices extends AbstractConfigMode {
	private int subMode;
	private int enteredPrice;
	private int selectedPop;
	private int numberOfPopKinds;
	
	private ConfigPopPrices() {
	}
	
	private static class ConfigPopPricesHolder {
		private static final ConfigPopPrices INSTANCE = new ConfigPopPrices();
	}
	
	public static ConfigPopPrices getInstance() {
		return ConfigPopPricesHolder.INSTANCE;
	}
	
	public void initializeCPP(int numPopKinds) {
		subMode = 0;
		message = "";
		enteredPrice = 0;
		selectedPop = -1;
		numberOfPopKinds = numPopKinds;
	}
	
	@Override
	public String modeName() {
		message = "Set Pop Price";
		return message;
	}

	/**
	 * A method to create the message explaining what each button action does
	 *   subMode = 0 shows what pops are available for price adjustment 
	 *   subMode = 1 shows the new price that will be set for the selected pop
	 * @param names - an array of strings that have the names of all available pops in the machine.
	 * @param prices - an of integers that have the prices of all available pops in the machine.
	 * @return String of instructions.
	 */
	public String modeInstructions(String[] names, int[] prices) {
		if (subMode == 0) {
			message = "Choose a Pop type that you would like to change the price of: \n";
			for (int i = 0; i < names.length; i++) {
				message += i + " - " + names[i] + " : $" + String.format("%.2f", (double) ((double) prices[i] / 100)) + "\n";
			}
			if (selectedPop < 0)
				message += "Selection: ";
			else 
				message += "Selection: " + names[selectedPop];
		}
		else if (subMode == 1) {
			message = "Use 0 - 9 (b for backspace) to enter new price for " 
					+ names[selectedPop] + ": \n $";
			double dub = ((double) enteredPrice / (double) 100);
			message += String.format("%.2f", dub);
		}
		else {
			throw new SimulationException("Unknown mode in ConfigPopPrice");
		}
		return message;
	}
	
	/**
	 * A method to switch the subMode.  If it was in subMode 1, return that a configuration was completed (true)
	 * @return finished - a boolean value that is only true when a configuration has been completed.  
	 */
	@Override
	public boolean enterKeyPressed() {
		boolean finished = false;
		if (subMode == 0) {
			if (selectedPop != -1)
				subMode = 1;
		}
		else if (subMode == 1) {
			if ((enteredPrice % 5) != 0) {
				enteredPrice = 0;
			}
			else {
				subMode = 0;
				finished = true;
			}
		}
		else {
			throw new SimulationException("Unknown mode in ConfigPopPrice");
		}
		return finished;
	}

	/**
	 * Method to change the state of the class variables depending on what the current subMode is and 
	 *   which button was pressed.
	 */
	@Override
	public void buttonPressed(int index) {
		if (subMode == 0) {
			if (index >= numberOfPopKinds) {
				// do nothing. invalid pop selection
				}
			else {
				selectedPop = index;
			}
		}
		else if (subMode == 1) {
			if (index == 34) {	// button number 34 is 'b'.  used for backspace
				enteredPrice = (int) Math.floor((double) enteredPrice / 10); 
			}
			else if (index > 9) {
				// nothing. only use buttons 0 - 10
			}
			else {
				enteredPrice = (enteredPrice * 10) + index;
			}
		}
		else {
			throw new SimulationException("Unknown mode in ConfigPopPrice");
		}
	}

	/**
	 * returns the currently accumulated message
	 * @return changes - the selectedPop and EnteredPrice contained in an int[] of length 2.
	 */
	public int[] getChanges() {
		int[] changes = new int[2];
		changes[0] = selectedPop;
		changes[1] = enteredPrice;
		return changes;
	}
	
	/**
	 * A method to reset this class to its default values.  To be used after completing a price configuration
	 */
	public void reset() {
		subMode = 0;
		message = "";
		enteredPrice = 0;
		selectedPop = -1;
	}

	/**
	 * getters, useful for testing
	 */
	public int getSubMode() {
		return subMode;
	}
	
	public int getEnteredPrice() {
		return enteredPrice;
	}
	
	public int getSelectedPop() {
		return selectedPop;
	}
	
	public int getNumOfPopKinds() {
		return numberOfPopKinds;
	}
}

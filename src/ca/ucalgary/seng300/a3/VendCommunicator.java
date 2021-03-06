//SENG300 Group Assignment 3

package ca.ucalgary.seng300.a3;

import org.lsmr.vending.hardware.*;
import ca.ucalgary.seng300.a3.CoinRackListening;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Class that holds a reference to the parts of the machine necessary for
 * communication between parts of the vending machine.
 */
public class VendCommunicator {

	private CoinReceptacleListening receptacle;
	private PopCanRackListening[] pRacks;
	private VendingMachine machine;
	private HashMap<CoinRack, CoinRackListening> cRacks;
	private IndicatorLighListening changeLight;
	private OutOfOrderLightListening outOfOrderLight;
	private Boolean changeLightFlag = false;
	private LockPanelListener lockPanel;
	private boolean validCardFlag;
	private int credit;
	private ConfigPanelLogic configPanelLogic;
	private emptyMsgLoop emptyMsgL;
	private SwipeListening swipeListening;
	private double partialAmount = 0;
	private Timer timer1;
	private Timer timer2;

	//For use with writing to our log file
	static DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
    static Date dateobj = new Date();
	
	//0 = Cash, 1 = Debit, 2 = Credit
	private int paymentType;
	//The amount the user wants to pay
	private int amount;
	//The remaining cost after paying for a portion of pop
	private int costRemaining;

	public VendCommunicator() {
	}
	
	private static class VendCommunicatorHolder {
		private static final VendCommunicator INSTANCE = new VendCommunicator();
	}
	
	public static VendCommunicator getInstance()
	{
		return VendCommunicatorHolder.INSTANCE;
	}

	// Links the appropriate parts to their corresponding variables
	public void linkVending(CoinReceptacleListening receptacle,IndicatorLighListening indicator, OutOfOrderLightListening display, PopCanRackListening[] pRacks, VendingMachine machine,
			HashMap<CoinRack, CoinRackListening> cRacks, LockPanelListener lockPanel, int credit, SwipeListening swipe) {
		this.receptacle = receptacle;
		this.pRacks = pRacks;
		this.machine = machine;
		this.cRacks = cRacks;
		this.changeLight = indicator;
		this.outOfOrderLight = display;
		this.lockPanel = lockPanel;
		this.paymentType = 0; //Payment type defaults to cash
		this.amount = -1; //Payment amount defaults to -1
		this.validCardFlag = false; //Consider cards to be invalid by default
		this.credit = credit;
		configPanelLogic = ConfigPanelLogic.getInstance();
		configPanelLogic.initializeCP(machine);
		this.emptyMsgL = new emptyMsgLoop("Hi there!");
		//emptyMsgL.reactivateMsg();
		//emptyMsgL.startThread();
		this.swipeListening = swipe;
		welcomeMessageTimer();
	}

	/**
	 * Function that is called by SelectionButtonListening
	 * 
	 * @param index
	 * 				- index of the selectionButton calling the function
	 * @param amount
	 * 				- the amount the user wants to pay with the selected tender
	 * @param tenderType
	 * 				- the type of tender the user is making payment with
	 * 				- 0 = Cash
	 * 				- 1 = Debit
	 * 				- 2 = Credit
	 * 
	 * Checks if the requested pop is available. If it is, checks to see if the
	 * machine has enough credit to purchase the soda. If enough credit is
	 * available, deducts the price of the appropriate pop and calls for the machine
	 * to dispense said pop. Prints an appropriate message in each instance.
	 */
	public void purchasePop(int index) {
		//Variables to calculate remaining cost and change that was paid
		this.costRemaining = machine.getPopKindCost(index);
		int changePaid = 0;
		if (pRacks[index].isEmpty()) {
			displayMsg("Out of " + machine.getPopKindName(index));
		}
		//Take cash payments
		else if (paymentType == 0) {
			//If amount is -1, then no amount was specified. User pays for the full price in change.
			if (this.amount == -1) {
				//Verify there is enough change in the machine to pay for the full price
				if (credit >= machine.getPopKindCost(index)) {
					this.costRemaining = 0;
					changePaid = 0;
					updateCredit(-1 * machine.getPopKindCost(index));
				}
				else {
					displayMsg("Insufficient Funds. Please specify payment amount for partial cash payment.");
				}
			}
			//If a different amount to be paid is specified, only pay off that portion
			else {
				//Verify there is enough change in the machine to pay the specified amount
				if (credit >= this.amount) {
					//Adjust amount so you don't overpay for the price of a pop
					if (this.amount > this.costRemaining) {
						this.amount = this.costRemaining;
					}
					//Decrease the receptacle value by the amount paid
					/*//This happens on its own if dispensePopWithChange is called, don't want to double up
					else if (this.amount < this.costRemaining) {
						receptacle.Purchase(this.amount);
					}*/
					this.costRemaining -= this.amount;
					updateCredit(-1 * this.amount);
					changePaid += this.amount;
					displayMsg(this.amount + " paid by cash. Please pay off remaining " + this.costRemaining);
				}
				else {
					displayMsg("Insufficient Funds. Please specify a payment amount for which you have sufficient credit.");
				}
			}
			//Dispense a pop only if the remaining cost ever reaches 0
			if (this.costRemaining == 0) {
				dispensePopWithChange(index, changePaid);
			}
		}
		else if (this.paymentType == 1 || this.paymentType == 2){
			//If amount is -1, then no amount was specified. User pays for the full price using card.
			if (this.amount == -1) {
				//Make sure the card is valid before taking payment
				if (verifyCard(this.validCardFlag)) {
					this.costRemaining -= machine.getPopKindCost(index);
				}
			}
			//If an amount to be paid was specified, then the user only pays for that amount
			else {
				//Make sure the card is valid before taking payment
				if (verifyCard(this.validCardFlag)) {
					//Adjust amount so you don't overpay for the price of a pop
					if (this.amount > this.costRemaining) {
						this.amount = this.costRemaining;
					}
					this.costRemaining -= this.amount;
					displayMsg(this.amount + " paid by card. Please pay off remaining " + this.costRemaining);
				}
			}
			//Dispense a pop only if the remaining cost ever reaches 0
			if (this.costRemaining == 0) {
				dispensePopWithChange(index, changePaid);
			}
		}
		else {
			displayMsg("Payment type not supported at this time.");
			try {
				LogFile.writeLog("\n"+df.format(dateobj) + "\t" + getClass().getName() + "\t" + "payment type not yet supported");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			LogFile.writeLog("\n"+df.format(dateobj) + "\t" + getClass().getName() + "\t" + "button pressed");
		} catch (IOException e) {
			e.printStackTrace();
		}

		updateCredit(0);
	}
	
	/**
	 * A method to begin the timers for the welcome message
	 */
	public void welcomeMessageTimer() {
		timer1 = new Timer();
		timer1.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					welcomeMessage();
					}
		}, 0, 15000);
		
		timer2 = new Timer();
		timer2.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				clearDisplayMessage();
				}
		}, 5000, 15000);
		
	}
	
	/**
	 * A method to push a welcome message to the display
	 */
	public void welcomeMessage() {
		machine.getDisplay().display("Hi There!");
	}
	
	/**
	 * A method to clear the message to the display
	 */
	public void clearDisplayMessage() {
		machine.getDisplay().display("");
	}
	
	//Required setters and getters
	public boolean getChangeLightFlag() {
		return changeLightFlag;
	}
	public void setChangeLightFlag(boolean flag) {
		changeLightFlag = flag;
	}
	public void changeLight(boolean flag2) {
		if (flag2) {
			machine.getExactChangeLight().activate();
		}
		else {
			machine.getExactChangeLight().deactivate();
		}
	}
	public int getCredit() {
		return this.credit;
	}
	public void setValidCardFlag(boolean flag) {
		this.validCardFlag = flag;
	}
	//Setter for the payment type
	public void setPaymentType(int paymentType) {
		this.paymentType = paymentType;
	}	
	//Setter for the amount to be paid
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	/**
	*Function that enables safety on the vending machine		
	*/		
	public void enableSafety() {		
		machine.enableSafety();		
	}
	/**
	*Function that disables safety on the vending machine
	*/
	public void disableSafety() {
		machine.disableSafety();
	}
	
	/**
	* Function that tells us if a debit/credit card is valid.
	* A valid card means the payment processor has checked the
	* card number, balance, and PIN. If any of these are invalid,
	* return false. Otherwise, return true.
	*
	* @param isValid - The flag we would receive from the payment processor
	*/
	public boolean verifyCard(boolean isValid) {
		return isValid;
	}
	
	/**
	* Function that updates the value of credit, and displays it to the screen
	*
	* @param value - The value by which to increase or decrease credit in the machine
	*/
	public void updateCredit(int value) {
		credit += value;
		
		if (this.credit == 0) {
			welcomeMessageTimer();
		}
		else {
			this.displayMsg("Credit: $" + String.format("%.2f", (double) ((double) this.getCredit() / 100)));
		}
	}
    
	/**
	* Function that is called when enough cash payment is taken to dispense a pop
	*
	* @param index - the index of the pop can rack
	*/
	public void dispensePopWithChange(int index, int changePaid) {
		try {
			int change = credit - changePaid;
			machine.getCoinReceptacle().unload();
			machine.getPopCanRack(index).dispensePopCan();
			try {
				LogFile.writeLog("\n"+df.format(dateobj) + "\t" + getClass().getName() + "\t" + "pop dispensed\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
			int remainder = giveChange(change);
			credit = remainder;
			try {
				LogFile.writeLog("\n"+df.format(dateobj) + "\t" + getClass().getName() + "\t" + remainder + " cents in change given\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (!hasChange() && !changeLightFlag) {
				machine.getExactChangeLight().activate();
				changeLightFlag = true;
			}
			else if (hasChange() && changeLightFlag) {
				machine.getExactChangeLight().deactivate();
				changeLightFlag = false;
			}
		} catch (DisabledException e) {
		} catch (EmptyException e) {
		} catch (CapacityExceededException e) {
		}
	}

	/**
	* Function that is called when something needs to print to the display
	*
	* message - the message being outputted to the display
	*/
	public void displayMsg(String message) {
		if (timer1 != null) {
			timer1.cancel();
			timer2.cancel();
		}
		machine.getDisplay().display(message);
	}
	
	public void displayCreditMsg() {
		this.displayMsg("Credit: $" + String.format("%.2f", (double) ((double) credit / 100)));
	}

	/**
	 * Determines whether or not there are enough coins in the coin racks to give
	 * any arbitrary amount of change back after a transaction has occurred.
	 * 
	 * @return True if the machine can return exact change, false otherwise
	 */
	public boolean hasChange() {
		int[] coinKinds = new int[machine.getNumberOfCoinRacks()]; // get the coin kinds used in the machine
		for (int i = 0; i < machine.getNumberOfCoinRacks(); i++) {
			coinKinds[i] = machine.getCoinKindForCoinRack(i);
		}
		Arrays.sort(coinKinds); // sort in ascending order

		int[] coinsIn = new int[machine.getNumberOfCoinRacks()];

		for (int i = 0; i < machine.getNumberOfCoinRacks(); i++) {
			coinsIn[i] = cRacks.get(machine.getCoinRackForCoinKind(coinKinds[i])).getCoins();

		}
		int change = coinKinds[coinKinds.length - 1];
		int[][] bestChange = new int[change + 1][coinKinds.length];

		for (int i = 1; i <= change; i++) {
			int[] best = new int[coinKinds.length];
			int bestLength = change + 1;
			for (int j = 0; j < coinKinds.length; j++) {
				if (coinKinds[j] > i) { // coin denomination is more than change than we have to make
					continue;
				}
				if (coinsIn[j] == 0) { // there are no coins of this type
					continue;
				}

				int changeLeft = i - coinKinds[j];
				int[] changePossible = new int[best.length];

				for (int k = 0; k < best.length; k++) { //
					changePossible[k] = bestChange[changeLeft][k];
				}

				if (changeLeft == 0) { // returning only this coin will give the amount of change necessary
					best = new int[best.length];
					best[j] = 1;
					bestLength = 1;
					break;
				}
				if (bestChange[changeLeft][j] == coinsIn[j]) { // we've already used up every type of this coin
					continue;
				}
				if (sumArray(bestChange[changeLeft]) == 0) { // change could not be made for this particular
																// denomination
					continue;
				}
				changePossible[j] += 1;

				if (sumArray(changePossible) <= bestLength) {
					for (int k = 0; k < changePossible.length; k++) {
						best[k] = changePossible[k];
					}
					bestLength = sumArray(changePossible);
				}

			}

			for (int k = 0; k < best.length; k++) {
				if (sumArray(best) == 0) { // if we can't make change for some denomination up to and including the
											// greatest coin value, we can't guarantee change at all
					return false;
				}
				bestChange[i][k] = best[k];
			}
		}
		return true;
	}

	/**
	 * Removes a specified amount of money from the coin racks and delivers them to
	 * the coin racks' sink This method is intended for giving change. This method
	 * does not guarantee that exact change will be given, instead, as close to
	 * exact change as possible will be given
	 * 
	 * the correctness of this method for change (assuming the user pays with coins)
	 * is based on the assumption that all coins from the receptacle were added to
	 * the coin racks before change was given.
	 * 
	 * @param change
	 *            The amount of change that needs to be given
	 * @return the amount of change that was not given (returned 0 means all change
	 *         was given)
	 */
	public int giveChange(int change) {
		if (change == 0) {
			return 0;
		}
		// System.out.println(machine.getNumberOfCoinRacks());
		int[] coinKinds = new int[machine.getNumberOfCoinRacks()]; // get the coin kinds used in the machine
		for (int i = 0; i < machine.getNumberOfCoinRacks(); i++) {
			coinKinds[i] = machine.getCoinKindForCoinRack(i);
		}

		Arrays.sort(coinKinds); // sort in ascending order

		int[] coinsIn = new int[machine.getNumberOfCoinRacks()];

		for (int i = 0; i < machine.getNumberOfCoinRacks(); i++) {
			coinsIn[i] = cRacks.get(machine.getCoinRackForCoinKind(coinKinds[i])).getCoins();
		}


		HashMap<Integer, Integer> cha = makeChange(coinsIn, coinKinds, change);

		// gives change
		for (int coin : coinKinds) {
			try {
				for (int i = cha.get(coin); i > 0; i--) {
					machine.getCoinRackForCoinKind(coin).releaseCoin();
					change -= coin;
				}
			} catch (CapacityExceededException e) {
				e.printStackTrace();
			} catch (EmptyException e) {
				System.out.println("This shouldn't have happened.");
				e.printStackTrace();
			} catch (DisabledException e) {// do not dispense coins
				break;
			}
		}
		return change;

	}

	// internal function for giveChange
	private HashMap<Integer, Integer> makeChange(int[] coinsIn, int[] coinKinds, int change) {
		int[][] bestChange = new int[change + 1][coinKinds.length];

		for (int i = 1; i <= change; i++) {
			int[] best = new int[coinKinds.length];
			int bestLength = change + 1;
			for (int j = 0; j < coinKinds.length; j++) {
				if (coinKinds[j] > i) { // coin denomination is more than change than we have to make
					continue;
				}
				if (coinsIn[j] == 0) { // there are no coins of this type
					continue;
				}

				int changeLeft = i - coinKinds[j];
				int[] changePossible = new int[best.length];

				for (int k = 0; k < best.length; k++) { //
					changePossible[k] = bestChange[changeLeft][k];
				}

				if (changeLeft == 0) { // returning only this coin will give the amount of change necessary
					best = new int[best.length];
					best[j] = 1;
					bestLength = 1;
					break;
				}
				if (bestChange[changeLeft][j] == coinsIn[j]) { // we've already used up every type of this coin
					continue;
				}
				if (sumArray(bestChange[changeLeft]) == 0) { // change could not be made for this particular
																// denomination
					continue;
				}
				changePossible[j] += 1;

				if (sumArray(changePossible) <= bestLength) {
					for (int k = 0; k < changePossible.length; k++) {
						best[k] = changePossible[k];
					}
					bestLength = sumArray(changePossible);
				}

			}

			for (int k = 0; k < best.length; k++) {
				bestChange[i][k] = best[k];
			}
		}

		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int i = 0; i < coinKinds.length; i++) {
			map.put(coinKinds[i], 0);
		}

		for (int i = change; i >= 0; i--) {
			if (sumArray(bestChange[i]) > 0) {
				for (int j = 0; j < coinKinds.length; j++) {
					map.put(coinKinds[j], bestChange[i][j]);
				}
				break;
			}
		}
		return map;
	}

	private int sumArray(int[] a) {
		int out = 0;
		for (int i : a) {
			out += i;
		}
		return out;
	}
	
	/**
	*	A method that is used to determine what action the communicator should take when a button has been pressed.
	*		The referenced button will be compared to all buttons in the vending machine to figure out what type of, 
	*		and if nessasary what index, the button is.  This is then passed on to the appropriate method to complete the action.
	*
	*	@param button - the reference to the button that was pressed.
	*/
	public void determineButtonAction(PushButton button) {
		boolean found = false;
		// search through the selection buttons to see if the parameter button is a selection button
		for (int index = 0; (found == false) && (index < machine.getNumberOfSelectionButtons()); index++) {
			if (machine.getSelectionButton(index) == button) {
				found = true;
				if(machine.isSafetyEnabled() == false) {
					purchasePop(index);
				}
			}
		}
		
		
		// search through the configuration panel to see if the parameter button is part of these buttons
		// NOTE!!! the configuration panel has a hard coded list of 37 buttons.  If this changes it could cause an error here!
		for (int index = 0; (found == false) && (index < 37); index++) {
			if (machine.getConfigurationPanel().getButton(index) == button) {
				found = true;
				configPanelLogic.configButtonAction(button);
			}
		}
		
		// check to see if the button is the configuration panels enter button.
		if ((found == false) && (button == machine.getConfigurationPanel().getEnterButton())) {
			found = true;
			configPanelLogic.configButtonAction(button);
		}
	}
}

//SENG300 Group Assignment 2

package ca.ucalgary.seng300.a2;

import org.lsmr.vending.hardware.*;
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

	public VendCommunicator() {
	}
	

	// Links the appropriate parts to their corresponding variables
	public void linkVending(CoinReceptacleListening receptacle,IndicatorLighListening indicator, OutOfOrderLightListening display, PopCanRackListening[] pRacks, VendingMachine machine,
			HashMap<CoinRack, CoinRackListening> cRacks) {
		this.receptacle = receptacle;
		this.pRacks = pRacks;
		this.machine = machine;
		this.cRacks = cRacks;
		this.changeLight = indicator;
		this.outOfOrderLight = display;
	}

	/**
	 * Function that is called by SelectionButtonListening
	 * 
	 * index - index of the selectionButton calling the function
	 * 
	 * Checks if the requested pop is available. If it is, checks to see if the
	 * machine has enough credit to purchase the soda. If enough credit is
	 * available, deducts the price of the appropriate pop and calls for the machine
	 * to dispense said pop. Prints an appropriate message in each instance.
	 */
	public void purchasePop(int index) {
		if (pRacks[index].isEmpty()) {
			System.out.println("Out of " + machine.getPopKindName(index));
		} else if (receptacle.getValue() >= machine.getPopKindCost(index)) {
			try {
				int change = receptacle.getValue() - machine.getPopKindCost(index);
				machine.getCoinReceptacle().unload();
				//receptacle.Purchase(machine.getPopKindCost(index));
				machine.getPopCanRack(index).dispensePopCan();
				int remainder = giveChange(change);
				receptacle.setValue(remainder);
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
		} else {
			System.out.println("Insufficient Funds");
		}
	}
	
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

/**
	* Function that is called when something needs to print to the display
	*
	* message - the message being outputted to the display
	*/
	public void displayMsg(String message) {
		machine.getDisplay().display(message);
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

}

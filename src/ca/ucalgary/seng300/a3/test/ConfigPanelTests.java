package ca.ucalgary.seng300.a3.test;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import org.lsmr.vending.*;
import org.lsmr.vending.hardware.*;

import ca.ucalgary.seng300.a3.CoinRackListening;
import ca.ucalgary.seng300.a3.CoinReceptacleListening;
import ca.ucalgary.seng300.a3.CoinReturnListening;
import ca.ucalgary.seng300.a3.CoinSlotListening;
import ca.ucalgary.seng300.a3.ConfigPanelLogic;
import ca.ucalgary.seng300.a3.ConfigPopPrices;
import ca.ucalgary.seng300.a3.DeliveryChuteListening;
import ca.ucalgary.seng300.a3.DisplayListening;
import ca.ucalgary.seng300.a3.IndicatorLighListening;
import ca.ucalgary.seng300.a3.LockPanelListener;
import ca.ucalgary.seng300.a3.LogFile;
import ca.ucalgary.seng300.a3.OutOfOrderLightListening;
import ca.ucalgary.seng300.a3.PopCanRackListening;
import ca.ucalgary.seng300.a3.SelectionButtonListening;
import ca.ucalgary.seng300.a3.VendCommunicator;
import ca.ucalgary.seng300.a3.emptyMsgLoop;

public class ConfigPanelTests {
	private VendingMachine machine;
	private PopCanRackListening[] canRacks;
	private SelectionButtonListening[] buttons;
	private CoinReceptacleListening receptacle;
	private DeliveryChuteListening chute;
	private emptyMsgLoop msgLoop;
	private HashMap<CoinRack, CoinRackListening> rackMap;
	private CoinReturnListening coinReturn;
	private IndicatorLighListening changeLight;
	private OutOfOrderLightListening outOfOrderLight;
	private ConfigPopPrices cfp;
	private ConfigPanelLogic cpl;
	private LockPanelListener lockListening;
	private DisplayListening displayListener = new DisplayListening();
	
	/**
	 * setup to initialize vending machine and accompanying listeners
	 * @throws UnsupportedEncodingException 
	 * @throws FileNotFoundException 
	 */
	@Before
	public void setup() throws FileNotFoundException, UnsupportedEncodingException {
		
		changeLight = new IndicatorLighListening();
		outOfOrderLight  = new OutOfOrderLightListening();
		 
		LogFile.createLogFile();
		int[] coinTypes = {1, 5, 10, 25, 100, 200 };
		int numButtons = 6;
		int coinCap = 200;
		int popCap = 10;
		int reCap = 50;
		CoinSlotListening slot = new CoinSlotListening();
		CoinRackListening[] racks = new CoinRackListening[coinTypes.length];
		ArrayList<String> popNames = new ArrayList<String>(6);
		String[] names = { "Pop1", "Pop2", "Pop3", "Pop4", "Pop5", "Pop6" };
		for (String name : names) {
			popNames.add(name);
		}
		ArrayList<Integer> prices = new ArrayList<Integer>(6);
		int[] costs = { 250, 250, 250, 250, 250, 250 };
		for (int cost : costs) {
			prices.add(cost);
		}
		
		int[] coinKinds = new int[] {1,5,10,25,100,200};
		
		machine = new VendingMachine(coinKinds, 6, 200,10,200, 200, 200);
		VendCommunicator communicator = VendCommunicator.getInstance();
		msgLoop = new emptyMsgLoop("Hi there!");

		buttons = new SelectionButtonListening[numButtons];
		receptacle = new CoinReceptacleListening(reCap,msgLoop); //ESB 
		canRacks = new PopCanRackListening[6];
		chute = new DeliveryChuteListening();

		machine.configure(popNames, prices);
		machine.disableSafety();
		machine.getCoinSlot().register(slot);
		
		coinReturn = new CoinReturnListening();
		machine.getCoinReturn().register(coinReturn);
		
		machine.getCoinReceptacle().register(receptacle);
		machine.getDeliveryChute().register(chute);
		machine.getExactChangeLight().register(changeLight);
		machine.getOutOfOrderLight().register(outOfOrderLight);
		machine.getDisplay().register(displayListener);
		rackMap = new HashMap<CoinRack, CoinRackListening>();
		for (int i = 0; i < coinTypes.length; i++) {
			racks[i] = new CoinRackListening(coinTypes[i]);
			machine.getCoinRack(i).register(racks[i]);
			rackMap.put(machine.getCoinRack(i), racks[i]);
		}
		for (int i = 0; i < numButtons; i++) {
			buttons[i] = new SelectionButtonListening(i);
			machine.getSelectionButton(i).register(buttons[i]);
		}
		for (int i = 0; i < 6; i++) {
			canRacks[i] = new PopCanRackListening();
			machine.getPopCanRack(i).register(canRacks[i]);
			machine.getPopCanRack(i).load(new PopCan(machine.getPopKindName(i)));
		}
		
		
		cfp = ConfigPopPrices.getInstance();
		cfp.initializeCPP(2);
		
		lockListening = new LockPanelListener();
		
		communicator.linkVending(receptacle, changeLight, outOfOrderLight, canRacks, machine, rackMap, lockListening, 0, null);
		//msgLoop.startThread();
		
	}
	
	/**
	 * ConfigPopPrice tests
	 */
	@Test
	public void enterButtonNoModeChange() {
		boolean finished = cfp.enterKeyPressed();
		assertTrue(!finished && (cfp.getSubMode() == 0));
	}
	
	// should do nothing
	@Test
	public void invalidSelection() {
		cfp.buttonPressed(15);
		assertTrue(cfp.getSelectedPop() == -1);
	}
	
	
	@Test
	public void validSelection() {
		cfp.buttonPressed(1);
		assertTrue(cfp.getSelectedPop() == 1);
	}
	
	// assure mode changes when valid selection entered 
	@Test
	public void enterButtonWithModeChange() {
		cfp.buttonPressed(1);
		boolean finished = cfp.enterKeyPressed();
		assertTrue(!finished && (cfp.getSubMode() == 1) && (cfp.getSelectedPop() == 1));
	}
	
	// If enter button is pressed twice config should be finished for the indicated pop
	//  price entered = 0
	@Test
	public void enterButtonCompletedConfig$0() {
		cfp.buttonPressed(1);
		cfp.enterKeyPressed();
		boolean finished = cfp.enterKeyPressed();
		boolean correctMode = cfp.getSubMode() == 0;
		boolean correctPop = cfp.getSelectedPop() == 1;
		boolean correctPrice = cfp.getEnteredPrice() == 0;
		assertTrue(finished && correctMode && correctPop && correctPrice);
	}
	
	/** should have the enteredPrice to be kept at 7
	  */
	@Test
	public void priceEntered7cents() {
		cfp.buttonPressed(1);
		cfp.enterKeyPressed();
		cfp.buttonPressed(7);
		boolean correctPrice = cfp.getEnteredPrice() == 7;
		assertTrue(correctPrice);
	}
	
	/** should have the enteredPrice to be 75 cents
	 *   created from 2 button presses
	  */
	@Test
	public void priceEntered75cents() {
		cfp.buttonPressed(1);
		cfp.enterKeyPressed();
		cfp.buttonPressed(7);
		cfp.buttonPressed(5);
		boolean correctPrice = cfp.getEnteredPrice() == 75;
		assertTrue(correctPrice);
	}
	
	/** If enter button is pressed twice config but enteredPrice is not 
	 * a multiple of 5. entered price should reset to 0, and mode remains on 
	 * choosing a new price for the pop.
	  */
	@Test
	public void invalidPriceConfig7cents() {
		cfp.buttonPressed(1);
		cfp.enterKeyPressed();
		cfp.buttonPressed(7);
		boolean correctPrice = cfp.getEnteredPrice() == 7;
		boolean finished = cfp.enterKeyPressed();
		boolean correctMode = cfp.getSubMode() == 1;
		boolean correctPop = cfp.getSelectedPop() == 1;
		correctPrice = correctPrice && (cfp.getEnteredPrice() == 0);
		assertTrue(!finished && correctMode && correctPop && correctPrice);
	}
	
	/** If enter button is pressed twice config and enteredPrice is  
	 * a multiple of 5, config is completed and mode returns to 0
	 * selectedPop and enteredPrice remain as entered.
	  */
	@Test
	public void validPriceConfig75cents() {
		cfp.buttonPressed(1);
		cfp.enterKeyPressed();
		cfp.buttonPressed(7);
		boolean correctPrice = cfp.getEnteredPrice() == 7;
		cfp.buttonPressed(5);
		boolean finished = cfp.enterKeyPressed();
		boolean correctMode = cfp.getSubMode() == 0;
		boolean correctPop = cfp.getSelectedPop() == 1;
		correctPrice = correctPrice && (cfp.getEnteredPrice() == 75);
		assertTrue(finished && correctMode && correctPop && correctPrice);
	}
	
	/** If enter button is pressed twice config and enteredPrice is  
	 * a multiple of 5, config is completed and mode returns to 0
	 * selectedPop and enteredPrice remain as entered.
	  */
	@Test
	public void validPriceConfig50centsWithBackspace() {
		cfp.buttonPressed(1);
		cfp.enterKeyPressed();
		cfp.buttonPressed(5);
		cfp.buttonPressed(3);
		boolean correctPrice = (cfp.getEnteredPrice() == 53);
		cfp.buttonPressed(34);		// 'b'
		correctPrice = correctPrice && (cfp.getEnteredPrice() == 5);
		cfp.buttonPressed(0);
		boolean finished = cfp.enterKeyPressed();
		boolean correctMode = cfp.getSubMode() == 0;
		boolean correctPop = cfp.getSelectedPop() == 1;
		correctPrice = correctPrice && (cfp.getEnteredPrice() == 50);
		assertTrue(finished && correctMode && correctPop && correctPrice);
	}
	
	/**
	 * Ensure that after completing a configuration, the changes are not cleared immediately  
	 */
	@Test
	public void persistingChanges() {
		cfp.buttonPressed(1);
		cfp.enterKeyPressed();
		cfp.buttonPressed(5);
		cfp.enterKeyPressed();
		int[] changes = cfp.getChanges();
		assertTrue((changes[0] == 1) && (changes[1] == 5));
	}
	
	/**
	 * Ensure all changes are reset when reset() is called.
	 */
	@Test
	public void resetChanges() {
		cfp.buttonPressed(1);
		cfp.enterKeyPressed();
		cfp.buttonPressed(5);
		cfp.enterKeyPressed();
		cfp.reset();
		int[] changes = cfp.getChanges();
		assertTrue((changes[0] == -1) && (changes[1] == 0) && (cfp.getSubMode() == 0));
	}
	
	/**
	 * Ensure that the modeInstructions() method is generating the correct strings
	 *   depending on ConfigPopPrices current state. 
	 */
	@Test
	public void instructionsMode0NoSelection() {
		String[] names = new String[2];
		names[0] = "Coke";
		names[1] = "Sprite";
		int[] prices = new int[2];
		prices[0] = 100;
		prices[1] = 50;
		String message = cfp.modeInstructions(names, prices);
		String expected = "Choose a Pop type that you would like to change the price of: \n";
		expected += "0 - Coke : $1.00\n";
		expected += "1 - Sprite : $0.50\n";
		expected += "Selection: ";
		assertTrue(message.equals(expected));
	}
	
	@Test
	public void instructionsMode0Select1() {
		String[] names = new String[2];
		names[0] = "Coke";
		names[1] = "Sprite";
		int[] prices = new int[2];
		prices[0] = 100;
		prices[1] = 50;
		cfp.buttonPressed(1);
		String message = cfp.modeInstructions(names, prices);
		String expected = "Choose a Pop type that you would like to change the price of: \n";
		expected += "0 - Coke : $1.00\n";
		expected += "1 - Sprite : $0.50\n";
		expected += "Selection: Sprite";
		assertTrue(message.equals(expected));
	}
	
	@Test
	public void instructionsMode1NoSelection() {
		String[] names = new String[2];
		names[0] = "Coke";
		names[1] = "Sprite";
		int[] prices = new int[2];
		prices[0] = 100;
		prices[1] = 50;
		cfp.buttonPressed(1);
		cfp.enterKeyPressed();
		String message = cfp.modeInstructions(names, prices);
		String expected = "Use 0 - 9 (b for backspace) to enter new price for Sprite: \n $0.00";
		assertTrue(message.equals(expected));
	}
	
	@Test
	public void instructionsMode1_275cents() {
		String[] names = new String[2];
		names[0] = "Coke";
		names[1] = "Sprite";
		int[] prices = new int[2];
		prices[0] = 100;
		prices[1] = 50;
		cfp.buttonPressed(1);
		cfp.enterKeyPressed();
		cfp.buttonPressed(2);
		cfp.buttonPressed(7);
		cfp.buttonPressed(5);
		String message = cfp.modeInstructions(names, prices);
		String expected = "Use 0 - 9 (b for backspace) to enter new price for Sprite: \n $2.75";
		assertTrue(message.equals(expected));
	}
}

package ca.ucalgary.seng300.a3.test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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


public class ButtonTests {

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
	private DisplayListening cplDisplayListener;
	
	
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
		
		cplDisplayListener = new DisplayListening();
		machine.getConfigurationPanel().getDisplay().register(cplDisplayListener);
		
		for (int i=0; i<37; i++) {
			machine.getConfigurationPanel().getButton(i).register(new SelectionButtonListening(i));
		}
		
		machine.getConfigurationPanel().getEnterButton().register(new SelectionButtonListening(37));
		
		cfp = ConfigPopPrices.getInstance();
		cfp.initializeCPP(2);
		
		lockListening = new LockPanelListener();
		
		communicator.linkVending(receptacle, changeLight, outOfOrderLight, canRacks, machine, rackMap, lockListening, 0, null);
		//msgLoop.startThread();
		
	}
	
	/**
	 * A method to test the selection button
	 *   Only testing the button decision.  No credit added.
	 */
	@Test
	public void selectionButton() {
		machine.getSelectionButton(1).press();
		String result = displayListener.getCurrMessage();
		System.out.println(result);
		assertTrue(displayListener.getCurrMessage().equals("Insufficient Funds. Please specify payment amount for partial cash payment."));
	}
	

	/**
	 * A method to test the config panel button
	 */
	@Test
	public void configPanelButtonNoEntry() {
		System.out.println("\n configPanelButtonNoEntry " + cplDisplayListener.getCurrMessage());
		String expected = "Select which aspect to configure: \n";
		expected += " 0 - Set Pop Price\n";
		expected += " Selection: ";
		assertTrue(cplDisplayListener.getCurrMessage().equals(expected));
	}
	
	/**
	 * A method to test the config panel button
	 */
	@Test
	public void configPanelButtonPress0() {
		DisplayListening cplDisplayListener = new DisplayListening();
		machine.getConfigurationPanel().getDisplay().register(cplDisplayListener);
		machine.getConfigurationPanel().getButton(0).press();
		System.out.println("\n\n configPanelButtonPress0 \n" +cplDisplayListener.getCurrMessage());
		String expected = "Select which aspect to configure: \n";
		expected += " 0 - Set Pop Price\n";
		expected += " Selection: 0";
		assertTrue(cplDisplayListener.getCurrMessage().equals(expected));
	}
	
	
	/**
	 * A method to test the config panel button
	 */
	@Test
	public void configPanelButtonEnter() {
		DisplayListening cplDisplayListener = new DisplayListening();
		machine.getConfigurationPanel().getDisplay().register(cplDisplayListener);
		machine.getConfigurationPanel().getButton(0).press();
		machine.getConfigurationPanel().getEnterButton().press();
		
		System.out.println("\n\n configPanelButtonEnter \n" + cplDisplayListener.getCurrMessage());
		
		String expected = "Choose a Pop type that you would like to change the price of: \n";
		expected += "0 - Pop1 : $2.50\n";
		expected += "1 - Pop2 : $2.50\n";
		expected += "2 - Pop3 : $2.50\n";
		expected += "3 - Pop4 : $2.50\n";
		expected += "4 - Pop5 : $2.50\n";
		expected += "5 - Pop6 : $2.50\n";
		expected += "Selection: ";
		
		boolean result = cplDisplayListener.getCurrMessage().equals(expected); 

		assertTrue(result);
	}
	
	@After
	public void teardown() {
		machine = null;
		canRacks = null;
		buttons = null;
		receptacle = null;
		chute = null;
		msgLoop = null;
		rackMap = null;
		coinReturn = null;
		changeLight = null;
		outOfOrderLight = null;
		cfp = null;
		cpl = null;
		lockListening = null;
	}
}

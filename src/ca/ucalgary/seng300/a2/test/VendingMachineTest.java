/**
 * SENG300 Group Assignment 2
 * @author
 * 
 * A class that sets up and tests the vending machine
 */

package ca.ucalgary.seng300.a2.test;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.vending.*;
import org.lsmr.vending.hardware.*;

import ca.ucalgary.seng300.a2.CoinRackListening;
import ca.ucalgary.seng300.a2.CoinReceptacleListening;
import ca.ucalgary.seng300.a2.CoinReturnListening;
import ca.ucalgary.seng300.a2.CoinSlotListening;
import ca.ucalgary.seng300.a2.DeliveryChuteListening;
import ca.ucalgary.seng300.a2.IndicatorLighListening;
import ca.ucalgary.seng300.a2.LogFile;
import ca.ucalgary.seng300.a2.OutOfOrderLightListening;
import ca.ucalgary.seng300.a2.PopCanRackListening;
import ca.ucalgary.seng300.a2.SelectionButtonListening;
import ca.ucalgary.seng300.a2.VendCommunicator;
import ca.ucalgary.seng300.a2.emptyMsgLoop;

public class VendingMachineTest {

	private VendingMachine machine;
	private PopCanRackListening[] canRacks;
	private SelectionButtonListening[] buttons;
	private CoinReceptacleListening receptacle;
	private DeliveryChuteListening chute;
	private emptyMsgLoop msgLoop;
	private HashMap<CoinRack, CoinRackListening> rackMap;
	private CoinReturnListening coinReturn;
	private IndicatorLighListening changeLight = new IndicatorLighListening();
	private OutOfOrderLightListening outOfOrderLight  = new OutOfOrderLightListening();
	//private LogFile logfile; 
	

	/**
	 * setup to initialize vending machine and accompanying listeners
	 * @throws UnsupportedEncodingException 
	 * @throws FileNotFoundException 
	 */
	@Before
	public void setup() throws FileNotFoundException, UnsupportedEncodingException {
		//boolean createdfile; 
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
		VendCommunicator communicator = new VendCommunicator();
		msgLoop = new emptyMsgLoop("Hi there!", communicator);
		

		// communicator needs to be created before selection buttons, since
		// selection button takes in a reference to the communicator
//		VendCommunicator communicator = new VendCommunicator();

		buttons = new SelectionButtonListening[numButtons];
		receptacle = new CoinReceptacleListening(reCap,communicator,msgLoop); //ESB 
		canRacks = new PopCanRackListening[6];
		chute = new DeliveryChuteListening();

		machine.configure(popNames, prices);
		machine.disableSafety();
		machine.getCoinSlot().register(slot);
		
		coinReturn = new CoinReturnListening();
		machine.getCoinReturn().register(coinReturn);
		
		//CoinReturn cReturn = new CoinReturn(200);
		//HashMap<Integer, CoinChannel> coinRackChannels = new HashMap<Integer, CoinChannel>();
		
		//for(int i=0; i<coinKinds.length; i++) {
		//	machine.getCoinRackForCoinKind(coinKinds[i]).connect(new CoinChannel(cReturn));
		//	coinRackChannels.put(new Integer(coinKinds[i]), new CoinChannel(machine.getCoinRackForCoinKind(coinKinds[i])));
		//}
		rackMap = new HashMap<CoinRack, CoinRackListening>();
		//machine.getCoinSlot().connect(new CoinChannel(machine.getCoinReceptacle()), new CoinChannel(new CoinReturn(200)));
		//machine.getCoinReceptacle().connect(coinRackChannels, new CoinChannel(cReturn), new CoinChannel(null));
		machine.getCoinReceptacle().register(receptacle);
		machine.getDeliveryChute().register(chute);
		machine.getExactChangeLight().register(changeLight);
		machine.getOutOfOrderLight().register(outOfOrderLight);
		for (int i = 0; i < coinTypes.length; i++) {
			racks[i] = new CoinRackListening(coinTypes[i]);
			machine.getCoinRack(i).register(racks[i]);
			//machine.getCoinRack(i).connect(new CoinChannel(new CoinReturn(200)));
			rackMap.put(machine.getCoinRack(i), racks[i]);
		}
		for (int i = 0; i < numButtons; i++) {
			buttons[i] = new SelectionButtonListening(i, communicator);
			machine.getSelectionButton(i).register(buttons[i]);
		}
		for (int i = 0; i < 6; i++) {
			canRacks[i] = new PopCanRackListening();
			machine.getPopCanRack(i).register(canRacks[i]);
			machine.getPopCanRack(i).load(new PopCan(machine.getPopKindName(i)));
		}

		communicator.linkVending(receptacle, changeLight, outOfOrderLight, canRacks, machine, rackMap);
		msgLoop.startThread();
	}

	/**
	 * tests regular input into machine. Expected result: success, can rack is
	 * empty
	 * 
	 * @throws DisabledException
	 */
	@Test
	public void regularUseTest() throws DisabledException {
		for(int i = 0; i < 6; i++) {
			assertFalse(canRacks[i].isEmpty());
			machine.getCoinSlot().addCoin(new Coin(200));
			machine.getCoinSlot().addCoin(new Coin(25));
			machine.getCoinSlot().addCoin(new Coin(25));
			machine.getSelectionButton(i).press();
			assertTrue(canRacks[i].isEmpty());
		}
	
	}

	/**
	 * Tests an invalid coin input. Expected result: pop has not been dispensed
	 * 
	 * @throws DisabledException
	 */
	@Test
	public void invalidCoinTest() throws DisabledException {
		machine.getCoinSlot().addCoin(new Coin(300));
		machine.getSelectionButton(1).press();
		assertFalse(canRacks[1].isEmpty());
	}

	/**
	 * Insufficient funds are input into machine. Expected result: pop has not
	 * been dispensed
	 * 
	 * @throws DisabledException
	 */
	@Test
	public void insufficientFundsTest() throws DisabledException {
		machine.getCoinSlot().addCoin(new Coin(200));
		machine.getSelectionButton(2).press();
		assertFalse(canRacks[2].isEmpty());
	}

	/**
	 * rack is loaded with two pops, both are dispensed. Expected result: rack
	 * is empty
	 * 
	 * @throws DisabledException
	 */
	@Test
	public void multiplePopTest() throws DisabledException {
		for(int i = 0; i < 6; i++) {
			machine.getPopCanRack(i).load(new PopCan(machine.getPopKindName(i)));
			machine.getCoinSlot().addCoin(new Coin(200));
			machine.getCoinSlot().addCoin(new Coin(25));
			machine.getCoinSlot().addCoin(new Coin(25));
			machine.getSelectionButton(i).press();
			machine.getCoinSlot().addCoin(new Coin(200));
			machine.getCoinSlot().addCoin(new Coin(25));
			machine.getCoinSlot().addCoin(new Coin(25));
			machine.getSelectionButton(i).press();
			assertTrue(canRacks[i].isEmpty());
		}
	}

	/**
	 * rack is loaded with three pops, two are dispensed. Expected result: rack
	 * is not empty
	 * 
	 * @throws DisabledException
	 */
	@Test
	public void multiplePopTest2() throws DisabledException {
		for(int i = 0; i < 6; i++) {
			machine.getPopCanRack(i).load(new PopCan(machine.getPopKindName(i)));
			machine.getPopCanRack(i).load(new PopCan(machine.getPopKindName(i)));
			machine.getCoinSlot().addCoin(new Coin(200));
			machine.getCoinSlot().addCoin(new Coin(25));
			machine.getCoinSlot().addCoin(new Coin(25));
			machine.getSelectionButton(i).press();
			machine.getCoinSlot().addCoin(new Coin(200));
			machine.getCoinSlot().addCoin(new Coin(25));
			machine.getCoinSlot().addCoin(new Coin(25));
			machine.getSelectionButton(i).press();
			assertFalse(canRacks[i].isEmpty());
		}
	}

	/**
	 * dispense pop so that rack is empty and then try to purchase. Expected
	 * result: rack is empty, pop is not dispensed
	 * 
	 * @throws DisabledException
	 */
	@Test
	public void emptyRackTest() throws DisabledException {
		machine.getCoinSlot().addCoin(new Coin(200));
		machine.getCoinSlot().addCoin(new Coin(25));
		machine.getCoinSlot().addCoin(new Coin(25));
		machine.getSelectionButton(3).press();
		assertTrue(canRacks[3].isEmpty());

		machine.getCoinSlot().addCoin(new Coin(200));
		machine.getCoinSlot().addCoin(new Coin(25));
		machine.getCoinSlot().addCoin(new Coin(25));

		machine.getSelectionButton(3).press();

	}

	/**
	 * rack holds two pops, one pop is purchased, insufficient funds are added
	 * for second pop. Expected result: one pop is dispensed, other pop remains
	 * in rack
	 * 
	 * @throws DisabledException
	 */
	@Test
	public void multiplePopInsufficientFundTest() throws DisabledException {
		machine.getPopCanRack(4).load(new PopCan(machine.getPopKindName(4)));
		machine.getCoinSlot().addCoin(new Coin(200));
		machine.getCoinSlot().addCoin(new Coin(25));
		machine.getCoinSlot().addCoin(new Coin(25));
		machine.getSelectionButton(4).press();

		machine.getCoinSlot().addCoin(new Coin(200));
		machine.getSelectionButton(4).press();
		assertFalse(canRacks[4].isEmpty());
	}
	
	
	/** 
	 * Dispense change
	 * @throws DisabledException 
	 * @throws CapacityExceededException 
	 * 
	 */
	@Test 
	public void giveChangeTest() throws DisabledException, CapacityExceededException {
		machine.getCoinSlot().addCoin(new Coin(200));
		machine.getCoinSlot().addCoin(new Coin(200));
		machine.loadCoins(100,100,100,100,100,100);
		machine.getSelectionButton(1).press();
		int a = receptacle.getValue();
		// canRacks[1].isEmpty() = True 
		assertEquals(0, receptacle.getValue() )  ;
		assertEquals(150, coinReturn.getValue());
		//receptacle.setValue(remainder);
	}
	
}

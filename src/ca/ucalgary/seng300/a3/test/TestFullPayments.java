package ca.ucalgary.seng300.a3.test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.vending.Coin;
import org.lsmr.vending.PopCan;
import org.lsmr.vending.hardware.AbstractHardware;
import org.lsmr.vending.hardware.AbstractHardwareListener;
import org.lsmr.vending.hardware.CoinRack;
import org.lsmr.vending.hardware.DisabledException;
import org.lsmr.vending.hardware.Display;
import org.lsmr.vending.hardware.DisplayListener;
import org.lsmr.vending.hardware.VendingMachine;

import ca.ucalgary.seng300.a3.CoinRackListening;
import ca.ucalgary.seng300.a3.CoinReceptacleListening;
import ca.ucalgary.seng300.a3.CoinReturnListening;
import ca.ucalgary.seng300.a3.CoinSlotListening;
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

public class TestFullPayments {

	private emptyMsgLoop msgLoop;
	private displayListening myDisplay;
	private VendingMachine machine;
	private PopCanRackListening[] canRacks;
	private SelectionButtonListening[] buttons;
	private CoinReceptacleListening receptacle;
	private DeliveryChuteListening chute;
	private HashMap<CoinRack, CoinRackListening> rackMap;
	private CoinReturnListening coinReturn;
	private IndicatorLighListening changeLight = new IndicatorLighListening();
	private OutOfOrderLightListening outOfOrderLight  = new OutOfOrderLightListening();
	
	private VendCommunicator communicator;
	
	@Before
	public void setUp() throws FileNotFoundException, UnsupportedEncodingException {
		LogFile.createLogFile();
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
		communicator = VendCommunicator.getInstance();
		LockPanelListener lockListener = new LockPanelListener();
		msgLoop = new emptyMsgLoop("Hi there!");

		buttons = new SelectionButtonListening[numButtons];
		receptacle = new CoinReceptacleListening(reCap,msgLoop); //ESB 
		canRacks = new PopCanRackListening[6];
		chute = new DeliveryChuteListening();

		machine.configure(popNames, prices);
		machine.disableSafety();
		machine.getCoinSlot().register(slot);
		
		coinReturn = new CoinReturnListening();
		myDisplay = new displayListening();
		machine.getDisplay().register(myDisplay);
		machine.getCoinReturn().register(coinReturn);
		
		rackMap = new HashMap<CoinRack, CoinRackListening>();
		machine.getCoinReceptacle().register(receptacle);
		machine.getDeliveryChute().register(chute);
		machine.getExactChangeLight().register(changeLight);
		machine.getOutOfOrderLight().register(outOfOrderLight);
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

		communicator.linkVending(receptacle, changeLight, outOfOrderLight, canRacks, machine, rackMap, lockListener, 0, null);
	}
	
	class displayListening implements DisplayListener{
		private int numMessages = 0;
	
		@Override
		public void enabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		}
	
		@Override
		public void disabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		}
	
		@Override
		public void messageChange(Display display, String oldMessage, String newMessage) {
			numMessages++;		
		}
		
		public int getNum()
		{
			return numMessages;
		}
	}	

	@Test
	public void testValidCash() throws DisabledException {
		communicator.setPaymentType(0);
		Coin toonie = new Coin(200);
		machine.getCoinSlot().addCoin(toonie);
		machine.getCoinSlot().addCoin(toonie);
		communicator.purchasePop(0);
		assertEquals(0, machine.getPopCanRack(0).size());
		assertEquals(150, communicator.getCredit());
	}
	
	@Test
	public void testDebitValidCard() {
		communicator.setPaymentType(1);
		communicator.setValidCardFlag(true);
		communicator.purchasePop(0);
		assertEquals(0, machine.getPopCanRack(0).size());
	}
	
	@Test
	public void testCreditValidCard() {
		communicator.setPaymentType(2);
		communicator.setValidCardFlag(true);
		communicator.purchasePop(0);
		assertEquals(0, machine.getPopCanRack(0).size());
	}
	
	@Test
	public void testInvalidCash() throws DisabledException {
		communicator.setPaymentType(0);
		Coin toonie = new Coin(200);
		machine.getCoinSlot().addCoin(toonie);
		communicator.purchasePop(0);
		assertEquals(1, machine.getPopCanRack(0).size());
		assertEquals(200, communicator.getCredit());
		communicator.setAmount(250);
		communicator.purchasePop(0);
		assertEquals(1, machine.getPopCanRack(0).size());
	}
	
	@Test
	public void testDebitInvalidCard() {
		communicator.setPaymentType(1);
		communicator.setValidCardFlag(false);
		communicator.purchasePop(0);
		assertEquals(1, machine.getPopCanRack(0).size());
	}
	
	@Test
	public void testCreditInvalidCard() {
		communicator.setPaymentType(2);
		communicator.setValidCardFlag(false);
		communicator.purchasePop(0);
		assertEquals(1, machine.getPopCanRack(0).size());
	}
}

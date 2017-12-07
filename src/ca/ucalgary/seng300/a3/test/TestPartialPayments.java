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

public class TestPartialPayments {

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
		receptacle = new CoinReceptacleListening(reCap, msgLoop); //ESB 
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

	//Test partial cash payments with partial cash payments
	@Test
	public void testCashCash() throws DisabledException {
		communicator.setPaymentType(0);
		Coin toonie = new Coin(200);
		Coin loonie = new Coin(100);
		//Test valid cash purchase followed by another with remainder
		machine.getCoinSlot().addCoin(toonie);
		machine.getCoinSlot().addCoin(toonie);
		communicator.purchasePop(0);
		assertEquals(150, communicator.getCredit());
		machine.getCoinSlot().addCoin(toonie);
		communicator.purchasePop(0);
		assertEquals(100, communicator.getCredit());
		assertEquals(0, machine.getPopCanRack(0).size());
		//Test valid cash purchase followed by another with no remainder
		machine.getCoinSlot().addCoin(toonie);
		communicator.purchasePop(1);
		assertEquals(50, communicator.getCredit());
		machine.getCoinSlot().addCoin(toonie);
		communicator.purchasePop(1);
		assertEquals(0, communicator.getCredit());
		assertEquals(0, machine.getPopCanRack(1).size());
		//Test invalid cash purchase followed by valid with remainder
		communicator.purchasePop(2);
		assertEquals(0, communicator.getCredit());
		assertEquals(2, machine.getPopCanRack(2).size());
		machine.getCoinSlot().addCoin(toonie);
		machine.getCoinSlot().addCoin(toonie);
		communicator.purchasePop(2);
		assertEquals(150, communicator.getCredit());
		assertEquals(1, machine.getPopCanRack(2).size());
		//Test invalid cash purchase followed by valid with no remainder
		communicator.setAmount(200);
		communicator.purchasePop(3);
		assertEquals(150, communicator.getCredit());
		machine.getCoinSlot().addCoin(loonie);
		communicator.setAmount(-1);
		communicator.purchasePop(3);
		assertEquals(0, communicator.getCredit());
		assertEquals(1, machine.getPopCanRack(3).size());
		//Test partial cash payment for a pop, followed by another with remainder
		communicator.setAmount(200);
		machine.getCoinSlot().addCoin(toonie);
		communicator.purchasePop(4);
		assertEquals(0, communicator.getCredit());
		machine.getCoinSlot().addCoin(toonie);
		communicator.purchasePop(4);
		assertEquals(150, communicator.getCredit());
		assertEquals(1, machine.getPopCanRack(4).size());
		//Test partial cash payment for a pop, followed by another with no remainder
		communicator.setAmount(0);
		communicator.purchasePop(5);
		assertEquals(150, communicator.getCredit());
		machine.getCoinSlot().addCoin(loonie);
		communicator.setAmount(-1);
		communicator.purchasePop(5);
		assertEquals(0, communicator.getCredit());
		assertEquals(1, machine.getPopCanRack(5).size());
	}
	
	//Test partial debit payments with partial debit payments
	@Test
	public void testDebitDebit() {
		communicator.setPaymentType(1);
		communicator.setValidCardFlag(true);
		//Test valid partial debit purchase followed by valid with remainder
		communicator.setAmount(200);
		communicator.purchasePop(0);
		assertEquals(2, machine.getPopCanRack(0).size());
		communicator.setAmount(49);
		communicator.purchasePop(0);
		assertEquals(2, machine.getPopCanRack(0).size());
		//Test valid debit purchase followed by valid with no remainder
		communicator.setAmount(-1);
		communicator.purchasePop(0);
		assertEquals(1, machine.getPopCanRack(0).size());
		communicator.purchasePop(0);
		assertEquals(0, machine.getPopCanRack(0).size());
		//Test invalid debit purchase followed by partial valid with remainder
		communicator.setValidCardFlag(false);
		communicator.setAmount(50);
		communicator.purchasePop(1);
		assertEquals(2, machine.getPopCanRack(1).size());
		communicator.setValidCardFlag(true);
		communicator.purchasePop(1);
		assertEquals(2, machine.getPopCanRack(1).size());
		//Test valid debit purchase followed by invalid with no remainder
		communicator.setAmount(5000);
		communicator.purchasePop(1);
		assertEquals(1, machine.getPopCanRack(1).size());
		communicator.setValidCardFlag(false);
		communicator.setAmount(-1);
		communicator.purchasePop(1);
		assertEquals(1, machine.getPopCanRack(1).size());
	}
	
	//Test partial credit payments with partial credit payments
	@Test
	public void testCreditCredit() {
		communicator.setPaymentType(2);
		communicator.setValidCardFlag(true);
		//Test valid full credit purchase followed by valid partial purchase with remainder
		communicator.setAmount(5000);
		communicator.purchasePop(0);
		assertEquals(1, machine.getPopCanRack(0).size());
		communicator.setAmount(249);
		communicator.purchasePop(0);
		assertEquals(1, machine.getPopCanRack(0).size());
		//Test valid credit purchase followed by valid with no remainder
		communicator.setAmount(-1);
		communicator.purchasePop(0);
		assertEquals(0, machine.getPopCanRack(0).size());
		communicator.purchasePop(1);
		assertEquals(1, machine.getPopCanRack(1).size());
		//Test invalid credit purchase followed by partial valid with remainder
		communicator.setValidCardFlag(false);
		communicator.setAmount(50);
		communicator.purchasePop(1);
		assertEquals(1, machine.getPopCanRack(1).size());
		communicator.setValidCardFlag(true);
		communicator.purchasePop(1);
		assertEquals(1, machine.getPopCanRack(1).size());
		//Test valid credit purchase followed by invalid with no remainder
		communicator.setAmount(-1);
		communicator.purchasePop(1);
		assertEquals(0, machine.getPopCanRack(1).size());
		communicator.setValidCardFlag(false);
		communicator.purchasePop(2);
		assertEquals(2, machine.getPopCanRack(2).size());
	}
	
	//Test partial cash payments with partial debit payments
	@Test
	public void testCashDebit() throws DisabledException {
		Coin toonie = new Coin(200);
		communicator.setValidCardFlag(true);
		//Test valid partial cash purchase followed by valid debit with remainder
		communicator.setPaymentType(0);
		machine.getCoinSlot().addCoin(toonie);
		communicator.setAmount(50);
		communicator.purchasePop(0);
		assertEquals(150, communicator.getCredit());
		assertEquals(2, machine.getPopCanRack(0).size());
		communicator.setPaymentType(1);
		communicator.purchasePop(0);
		assertEquals(150, communicator.getCredit());
		assertEquals(2, machine.getPopCanRack(0).size());
		//Test valid partial cash purchase followed by valid debit with no remainder
		communicator.setPaymentType(0);
		communicator.setAmount(-1);
		communicator.purchasePop(0);
		assertEquals(0, communicator.getCredit());
		assertEquals(1, machine.getPopCanRack(0).size());
		communicator.setPaymentType(1);
		communicator.purchasePop(0);
		assertEquals(0, communicator.getCredit());
		assertEquals(0, machine.getPopCanRack(0).size());
		//Test valid partial debit purchase followed by valid cash with remainder
		communicator.setAmount(50);
		communicator.purchasePop(1);
		assertEquals(0, communicator.getCredit());
		assertEquals(2, machine.getPopCanRack(1).size());
		machine.getCoinSlot().addCoin(toonie);
		communicator.setPaymentType(0);
		communicator.setAmount(100);
		communicator.purchasePop(1);
		assertEquals(100, communicator.getCredit());
		assertEquals(2, machine.getPopCanRack(1).size());
		//Test valid partial debit purchase followed by valid cash with no remainder
		communicator.setPaymentType(1);
		communicator.setAmount(50);
		communicator.purchasePop(1);
		assertEquals(100, communicator.getCredit());
		assertEquals(2, machine.getPopCanRack(1).size());
		communicator.setPaymentType(0);
		communicator.setAmount(-1);
		communicator.purchasePop(1);
		assertEquals(50, communicator.getCredit());
		assertEquals(1, machine.getPopCanRack(1).size());
		//Test invalid partial cash purchase followed by valid partial debit with no remainder
		communicator.purchasePop(1);
		assertEquals(50, communicator.getCredit());
		assertEquals(1, machine.getPopCanRack(1).size());
		communicator.setPaymentType(1);
		communicator.purchasePop(1);
		assertEquals(50, communicator.getCredit());
		assertEquals(0, machine.getPopCanRack(1).size());
		//Test invalid partial cash purchase followed by valid partial debit with remainder
		communicator.setPaymentType(0);
		communicator.purchasePop(2);
		assertEquals(50, communicator.getCredit());
		assertEquals(2, machine.getPopCanRack(2).size());
		communicator.setPaymentType(1);
		communicator.setAmount(150);
		communicator.purchasePop(2);
		assertEquals(50, communicator.getCredit());
		assertEquals(2, machine.getPopCanRack(2).size());
		//Test invalid partial debit purchase followed by valid partial cash with remainder
		communicator.setValidCardFlag(false);
		communicator.setAmount(50);
		communicator.purchasePop(2);
		assertEquals(50, communicator.getCredit());
		assertEquals(2, machine.getPopCanRack(2).size());
		communicator.setPaymentType(0);
		communicator.purchasePop(2);
		assertEquals(0, communicator.getCredit());
		assertEquals(2, machine.getPopCanRack(2).size());
		//Test invalid partial debit purchase followed by valid cash with remainder
		communicator.setPaymentType(1);
		communicator.setAmount(-1);
		communicator.purchasePop(2);
		assertEquals(0, communicator.getCredit());
		assertEquals(2, machine.getPopCanRack(2).size());
		communicator.setPaymentType(0);
		machine.getCoinSlot().addCoin(toonie);
		communicator.purchasePop(2);
		assertEquals(150, communicator.getCredit());
		assertEquals(1, machine.getPopCanRack(2).size());
	}
	
	//Test partial cash payments with partial credit payments
	@Test
	public void testCashCredit() throws DisabledException {
		Coin toonie = new Coin(200);
		communicator.setValidCardFlag(true);
		//Test valid partial cash purchase followed by valid credit with remainder
		communicator.setPaymentType(0);
		machine.getCoinSlot().addCoin(toonie);
		communicator.setAmount(50);
		communicator.purchasePop(0);
		assertEquals(150, communicator.getCredit());
		assertEquals(2, machine.getPopCanRack(0).size());
		communicator.setPaymentType(2);
		communicator.purchasePop(0);
		assertEquals(150, communicator.getCredit());
		assertEquals(2, machine.getPopCanRack(0).size());
		//Test valid partial cash purchase followed by valid credit with no remainder
		communicator.setPaymentType(0);
		communicator.setAmount(-1);
		communicator.purchasePop(0);
		assertEquals(0, communicator.getCredit());
		assertEquals(1, machine.getPopCanRack(0).size());
		communicator.setPaymentType(2);
		communicator.purchasePop(0);
		assertEquals(0, communicator.getCredit());
		assertEquals(0, machine.getPopCanRack(0).size());
		//Test valid partial credit purchase followed by valid cash with remainder
		communicator.setAmount(50);
		communicator.purchasePop(1);
		assertEquals(0, communicator.getCredit());
		assertEquals(2, machine.getPopCanRack(1).size());
		machine.getCoinSlot().addCoin(toonie);
		communicator.setPaymentType(0);
		communicator.setAmount(100);
		communicator.purchasePop(1);
		assertEquals(100, communicator.getCredit());
		assertEquals(2, machine.getPopCanRack(1).size());
		//Test valid partial credit purchase followed by valid cash with no remainder
		communicator.setPaymentType(2);
		communicator.setAmount(50);
		communicator.purchasePop(1);
		assertEquals(100, communicator.getCredit());
		assertEquals(2, machine.getPopCanRack(1).size());
		communicator.setPaymentType(0);
		communicator.setAmount(-1);
		communicator.purchasePop(1);
		assertEquals(50, communicator.getCredit());
		assertEquals(1, machine.getPopCanRack(1).size());
		//Test invalid partial cash purchase followed by valid partial credit with no remainder
		communicator.purchasePop(1);
		assertEquals(50, communicator.getCredit());
		assertEquals(1, machine.getPopCanRack(1).size());
		communicator.setPaymentType(2);
		communicator.purchasePop(1);
		assertEquals(50, communicator.getCredit());
		assertEquals(0, machine.getPopCanRack(1).size());
		//Test invalid partial cash purchase followed by valid credit with remainder
		communicator.setPaymentType(0);
		communicator.purchasePop(2);
		assertEquals(50, communicator.getCredit());
		assertEquals(2, machine.getPopCanRack(2).size());
		communicator.setPaymentType(2);
		communicator.setAmount(150);
		communicator.purchasePop(2);
		assertEquals(50, communicator.getCredit());
		assertEquals(2, machine.getPopCanRack(2).size());
		//Test invalid partial credit purchase followed by valid partial cash with remainder
		communicator.setValidCardFlag(false);
		communicator.setAmount(50);
		communicator.purchasePop(2);
		assertEquals(50, communicator.getCredit());
		assertEquals(2, machine.getPopCanRack(2).size());
		communicator.setPaymentType(0);
		communicator.purchasePop(2);
		assertEquals(0, communicator.getCredit());
		assertEquals(2, machine.getPopCanRack(2).size());
		//Test invalid partial credit purchase followed by valid cash with  remainder
		communicator.setPaymentType(2);
		communicator.setAmount(-1);
		communicator.purchasePop(2);
		assertEquals(0, communicator.getCredit());
		assertEquals(2, machine.getPopCanRack(2).size());
		communicator.setPaymentType(0);
		machine.getCoinSlot().addCoin(toonie);
		communicator.purchasePop(2);
		assertEquals(150, communicator.getCredit());
		assertEquals(1, machine.getPopCanRack(2).size());
	}
}
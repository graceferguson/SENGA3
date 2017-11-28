package ca.ucalgary.seng300.a2.test;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

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

public class msgLoopTest {

	private VendingMachine machine;
	private PopCanRackListening[] canRacks;
	private SelectionButtonListening[] buttons;
	private CoinReceptacleListening receptacle;
	private DeliveryChuteListening chute;
	private emptyMsgLoop msgLoop;
	private HashMap<CoinRack, CoinRackListening> rackMap;
	private CoinReturnListening coinReturn;
	private myDisplayListening myDisplay;
	//private LogFile logfile; 
	
	class myDisplayListening implements DisplayListener
	{
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
		 IndicatorLighListening changeLight = new IndicatorLighListening();
		 OutOfOrderLightListening outOfOrderLight  = new OutOfOrderLightListening();
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
		myDisplay = new myDisplayListening();

		machine.configure(popNames, prices);
		machine.disableSafety();
		machine.getCoinSlot().register(slot);
		
		coinReturn = new CoinReturnListening();
		machine.getCoinReturn().register(coinReturn);
		
		machine.getDisplay().register(myDisplay);
		machine.getExactChangeLight().register(changeLight);
		machine.getOutOfOrderLight().register(outOfOrderLight);
		
		//CoinReturn cReturn = new CoinReturn(200);
		//HashMap<Integer, CoinChannel> coinRackChannels = new HashMap<Integer, CoinChannel>();
		
//		for(int i=0; i<coinKinds.length; i++) {
//			machine.getCoinRackForCoinKind(coinKinds[i]).connect(new CoinChannel(cReturn));
//			coinRackChannels.put(new Integer(coinKinds[i]), new CoinChannel(machine.getCoinRackForCoinKind(coinKinds[i])));
//		}
		//machine.loadCoins(100,100,100,100,100,100);
		rackMap = new HashMap<CoinRack, CoinRackListening>();
		//machine.getCoinSlot().connect(new CoinChannel(machine.getCoinReceptacle()), new CoinChannel(new CoinReturn(200)));
		//machine.getCoinReceptacle().connect(coinRackChannels, new CoinChannel(cReturn), new CoinChannel(null));
		machine.getCoinReceptacle().register(receptacle);
		machine.getDeliveryChute().register(chute);
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
	@Test
	public void test() throws DisabledException {

		try
		{
			TimeUnit.SECONDS.sleep(14);
		}
		catch(InterruptedException e){
			System.out.println("should not print");
		}
		assertEquals(myDisplay.getNum(), 2);
		
		machine.getCoinSlot().addCoin(new Coin(200));
		machine.getCoinSlot().addCoin(new Coin(100));
		machine.loadCoins(100,100,100,100,100,100);
		assertEquals(myDisplay.getNum(), 4);
		
		try
		{
			TimeUnit.SECONDS.sleep(14);
		}
		catch(InterruptedException e){
			System.out.println("should not print");
		}
		
		assertEquals(myDisplay.getNum(), 4);
		
		machine.getSelectionButton(2).press();
		
		try
		{
			TimeUnit.SECONDS.sleep(14);
		}
		catch(InterruptedException e){
			System.out.println("should not print");
		}
		
		assertEquals(myDisplay.getNum(), 6);
	}
}

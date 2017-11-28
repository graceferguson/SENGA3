package ca.ucalgary.seng300.a2.test;

import static org.junit.Assert.*;
import org.lsmr.vending.*;
import org.lsmr.vending.hardware.*;
import ca.ucalgary.seng300.a2.*;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

public class EmptyMsgLoopTest {

	//private VendingMachine machine;
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
	
	@Before 
	public void setup() throws FileNotFoundException, UnsupportedEncodingException {
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
		myDisplay = new displayListening();
		machine.getDisplay().register(myDisplay);
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
		
		
		}
	

	class displayListening implements DisplayListener
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

	@Test
	public void test() throws DisabledException {
		msgLoop.startThread();
		try
		{
			TimeUnit.SECONDS.sleep(14);
		}
		catch(InterruptedException e){
			System.out.println("should not print");
		}
		assertEquals(myDisplay.getNum(), 2);
		
		msgLoop.interruptThread();
		
		try
		{
			TimeUnit.SECONDS.sleep(14);
		}
		catch(InterruptedException e){
			System.out.println("should not print");
		}
		
		assertEquals(myDisplay.getNum(), 2);
		
		msgLoop.reactivateMsg();
		
		try
		{
			TimeUnit.SECONDS.sleep(14);
		}
		catch(InterruptedException e){
			System.out.println("should not print");
		}
		
		assertEquals(myDisplay.getNum(), 4);
	}
}

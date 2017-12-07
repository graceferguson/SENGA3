package ca.ucalgary.seng300.a3;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import javax.swing.*;

import org.lsmr.vending.Coin;
import org.lsmr.vending.PopCan;
import org.lsmr.vending.hardware.CoinRack;
import org.lsmr.vending.hardware.VendingMachine;

import ca.ucalgary.seng300.a3.CoinRackListening;
import ca.ucalgary.seng300.a3.CoinReceptacleListening;
import ca.ucalgary.seng300.a3.CoinReturnListening;
import ca.ucalgary.seng300.a3.CoinSlotListening;
import ca.ucalgary.seng300.a3.DeliveryChuteListening;
import ca.ucalgary.seng300.a3.IndicatorLighListening;
import ca.ucalgary.seng300.a3.LogFile;
import ca.ucalgary.seng300.a3.OutOfOrderLightListening;
import ca.ucalgary.seng300.a3.PopCanRackListening;
import ca.ucalgary.seng300.a3.SelectionButtonListening;
import ca.ucalgary.seng300.a3.VendCommunicator;
import ca.ucalgary.seng300.a3.emptyMsgLoop;
import ca.ucalgary.seng300.a3.GUILockPanelListener;
import ca.ucalgary.seng300.a3.GUI;

public class LoadUnloadGUI extends JFrame {
	private JPanel p = new JPanel(new GridBagLayout());
	private GridBagConstraints c = new GridBagConstraints();
	
	private JButton[] loadSoda;
	private JButton[] unloadSoda;
	private JButton[] loadCoinRack;
	private JButton[] unloadCoinRack;
	
	//Will likely need to make some of these non-static
	private VendingMachine vend;
	private static CoinRackListening[] racks;
	private static PopCanRackListening[] canRacks;
	private static VendingMachine vendingMachine;
	
	//For use with writing to our log file
	private	static DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
	private static Date dateobj = new Date();
	
	public LoadUnloadGUI(VendingMachine vend) {
		super("Loading and Unloading Panel");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.vend = vend;
		
		int numSoda = vend.getNumberOfPopCanRacks();
		int numCoin = vend.getNumberOfCoinRacks();
		int set = Math.max(numSoda, numCoin);
		
		setSize(700, 60*set);
		setResizable(true);
		
		
		loadSoda = new JButton[numSoda];
		unloadSoda = new JButton[numSoda];
		
		loadCoinRack = new JButton[numCoin];
		unloadCoinRack = new JButton[numCoin];
		
		//set all the soda buttons up
		for(int i = 0; i < numSoda;i++ ) {
			loadSoda[i] = new JButton("Load " + vend.getPopKindName(i) + " cans");
			unloadSoda[i] = new JButton("Unload " + vend.getPopKindName(i) + " cans");
		}
		//set all the coin rack buttons up
		for(int i = 0; i < numCoin;i++ ) {
			loadCoinRack[i] = new JButton("Load " + vend.getCoinKindForCoinRack(i).toString() + " coin");
			unloadCoinRack[i] = new JButton("Unload " + vend.getCoinKindForCoinRack(i).toString() + " coin");
		}
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,0,5,0);
		
		int counter = 0;
	
		//put buttons up.
		for(int i = 0; i < numSoda;i++) {
			c.gridx = 0; 
			c.gridy = i;
			p.add(loadSoda[i],c);
			c.gridx = 1;
			p.add(unloadSoda[i],c);
			counter++;
			
		}
	
		
		for(int i = 0; i <  numCoin;i++) {
			c.gridx = 2; 
			c.gridy = i;
			p.add(loadCoinRack[i],c);
			c.gridx = 3;
			p.add(unloadCoinRack[i],c);
			counter++;
		}
		
		add(p);
		setVisible(true);
		System.out.println("Construction complete");
		
	}
	
	/* Method to load a pop into the specified pop rack using the GUI
	 * 
	 * @param index - The index of the pop rack to be loaded
	 */
	public void loadPop(int index){
		if (canRacks[index].getAmount() == vendingMachine.getPopCanRack(index).getCapacity()) {
			VendCommunicator.getInstance().displayMsg("No more room for pop in rack at index " + index);
			try {
				LogFile.writeLog("\n"+df.format(dateobj) + "\t" + getClass().getName() + "\tCould not load pop into full rack for " + vendingMachine.getPopKindName(index) + ".\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			canRacks[index].popCanAdded(vendingMachine.getPopCanRack(index), new PopCan(vendingMachine.getPopKindName(index)));
			try {
				LogFile.writeLog("\n"+df.format(dateobj) + "\t" + getClass().getName() + "\tLoaded pop into rack for " + vendingMachine.getPopKindName(index) + ".\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/* Method to load a pop into the specified coin rack using the GUI
	 * 
	 * @param index - The index of the coin rack to be loaded
	 */
	public void loadCoin(int index){
		double value = racks[index].getValue() / 100;
		if (racks[index].getCoins() == vendingMachine.getCoinRack(index).getCapacity()) {
			VendCommunicator.getInstance().displayMsg("No more room for coins in rack for $" + value);
			try {
				LogFile.writeLog("\n"+df.format(dateobj) + "\t" + getClass().getName() + "\tCould not load coin into full rack for $" + value + " coins.\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			racks[index].coinAdded(vendingMachine.getCoinRack(index), new Coin(racks[index].getValue()));
			try {
				LogFile.writeLog("\n"+df.format(dateobj) + "\t" + getClass().getName() + "\tLoaded coin into rack for $" + value + " coins.\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/* Method to unload a pop from the specified pop rack using the GUI
	 * 
	 * @param index - The index of the pop rack to be unloaded
	 */
    public void unloadPop(int index){
    	if (canRacks[index].getAmount() > 0) {
			canRacks[index].popCanRemoved(vendingMachine.getPopCanRack(index), new PopCan(vendingMachine.getPopKindName(index)));
			try {
				LogFile.writeLog("\n"+df.format(dateobj) + "\t" + getClass().getName() + "\tUnloaded pop from rack for " + vendingMachine.getPopKindName(index) + ".\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			VendCommunicator.getInstance().displayMsg("No more pops left in rack for " + vendingMachine.getPopKindName(index));
			try {
				LogFile.writeLog("\n"+df.format(dateobj) + "\t" + getClass().getName() + "\tCould not unload pop from empty rack for " + vendingMachine.getPopKindName(index) + ".\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
    /* Method to unload a coin from the specified coin rack using the GUI
	 * 
	 * @param index - The index of the coin rack to be unloaded
     */
	public void unloadCoin(int index){
		double value = racks[index].getValue() / 100;
		if (racks[index].hasCoins()) {
			racks[index].coinRemoved(vendingMachine.getCoinRack(index), new Coin(racks[index].getValue()));
			try {
				LogFile.writeLog("\n"+df.format(dateobj) + "\t" + getClass().getName() + "\tUnloaded coin from rack for $" + value + " coins.\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			VendCommunicator.getInstance().displayMsg("No more coins left in rack for $" + value);
			try {
				LogFile.writeLog("\n"+df.format(dateobj) + "\t" + getClass().getName() + "\tCould not unload coin from empty rack for $" + value + " coins.\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//FOR TESTING PURPOSES-- REMOVE LATER********************************
    public static void main(String[] args) {
    	int[] canadianCoins = { 5, 10, 25, 100, 200 };


    	int coinRackCapacity = 15; 
    	int numPopTypes = 6;
		vendingMachine = new VendingMachine(canadianCoins, numPopTypes, coinRackCapacity, 15, 200, 200, 15);
		VendCommunicator comm = new VendCommunicator();
		
		// Do all the VendCommunicator things
		try {
			LogFile.createLogFile();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		CoinSlotListening slot = new CoinSlotListening();
		racks = new CoinRackListening[canadianCoins.length];
		SelectionButtonListening[] buttons = new SelectionButtonListening[numPopTypes];
		emptyMsgLoop msgLoop = new emptyMsgLoop("Hi there!");
		CoinReceptacleListening receptacle = new CoinReceptacleListening(50,msgLoop); //ESB 
		canRacks = new PopCanRackListening[6];
		DeliveryChuteListening chute = new DeliveryChuteListening();
		vendingMachine.getCoinSlot().register(slot);
		CoinReturnListening coinReturn = new CoinReturnListening();
		vendingMachine.getCoinReturn().register(coinReturn);
		IndicatorLighListening changeLight = new IndicatorLighListening();
		OutOfOrderLightListening outOfOrderLight  = new OutOfOrderLightListening();
		HashMap<CoinRack, CoinRackListening> rackMap = new HashMap<CoinRack, CoinRackListening>();
		vendingMachine.getCoinReceptacle().register(receptacle);
		vendingMachine.getDeliveryChute().register(chute);
		vendingMachine.getExactChangeLight().register(changeLight);
		vendingMachine.getOutOfOrderLight().register(outOfOrderLight);
		GUI newGUI = new GUI(vendingMachine, VendCommunicator.getInstance());
		GUILockPanelListener lockPanel = new GUILockPanelListener(newGUI, vendingMachine);
		for (int i = 0; i < canadianCoins.length; i++) {
			racks[i] = new CoinRackListening(canadianCoins[i]);
			vendingMachine.getCoinRack(i).register(racks[i]);
			//machine.getCoinRack(i).connect(new CoinChannel(new CoinReturn(200)));
			rackMap.put(vendingMachine.getCoinRack(i), racks[i]);
		}
		for (int i = 0; i < numPopTypes; i++) {
			buttons[i] = new SelectionButtonListening(i);
			vendingMachine.getSelectionButton(i).register(buttons[i]);
		}
		for (int i = 0; i < 6; i++) {
			canRacks[i] = new PopCanRackListening();
			vendingMachine.getPopCanRack(i).register(canRacks[i]);
			vendingMachine.getPopCanRack(i).load(new PopCan(vendingMachine.getPopKindName(i)));
		}
		comm.linkVending(receptacle, changeLight, outOfOrderLight, canRacks, vendingMachine, rackMap, null, comm.getCredit(), null);
		

		// Customize the pop kinds and pop costs in the vending machine
		java.util.List<String> popCanNames = Arrays.asList("Cola","Sprite","Fonda","Diet","Ginger Ale","Dr Pepper");
		java.util.List<Integer> popCanCosts = Arrays.asList(325,250,250,250,250,250);
		int[] popCanCounts = new int[vendingMachine.getNumberOfPopCanRacks()];
		for (int i = 0; i < popCanCounts.length; i++) {
			popCanCounts[i] = 1;
		}
	
		vendingMachine.configure(popCanNames, popCanCosts);		
		vendingMachine.loadPopCans(popCanCounts);
		
		int[] coinLoading = new int[vendingMachine.getNumberOfCoinRacks()];
		for (int i = 0; i < coinLoading.length; i++) {
			coinLoading[i] = coinRackCapacity - 5;
		}
		vendingMachine.loadCoins(coinLoading);
		
		new LoadUnloadGUI(vendingMachine);
		
		
		
    } // end main
}
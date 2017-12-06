package ca.ucalgary.seng300.a3;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.*;

import org.lsmr.vending.PopCan;
import org.lsmr.vending.hardware.CoinRack;
import org.lsmr.vending.hardware.VendingMachine;

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

public class LoadUnloadGUI extends JFrame {
	private JPanel p = new JPanel(new GridBagLayout());
	private GridBagConstraints c = new GridBagConstraints();
	
	private JButton[] loadSoda;
	private JButton[] unloadSoda;
	private JButton[] loadCoinRack;
	private JButton[] unloadCoinRack;
	
	private VendingMachine vend;
	
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
	
	//FOR TESTING PURPOSES-- REMOVE LATER********************************
    public static void main(String[] args) {
    	int[] canadianCoins = { 5, 10, 25, 100, 200 };


    	int coinRackCapacity = 15; 
    	int numPopTypes = 6;
		VendingMachine vendingMachine = new VendingMachine(canadianCoins, numPopTypes, coinRackCapacity, 15, 200, 200, 15);
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
		CoinRackListening[] racks = new CoinRackListening[canadianCoins.length];
		SelectionButtonListening[] buttons = new SelectionButtonListening[numPopTypes];
		emptyMsgLoop msgLoop = new emptyMsgLoop("Hi there!", comm);
		CoinReceptacleListening receptacle = new CoinReceptacleListening(50,comm,msgLoop); //ESB 
		PopCanRackListening[] canRacks = new PopCanRackListening[6];
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
		for (int i = 0; i < canadianCoins.length; i++) {
			racks[i] = new CoinRackListening(canadianCoins[i]);
			vendingMachine.getCoinRack(i).register(racks[i]);
			//machine.getCoinRack(i).connect(new CoinChannel(new CoinReturn(200)));
			rackMap.put(vendingMachine.getCoinRack(i), racks[i]);
		}
		for (int i = 0; i < numPopTypes; i++) {
			buttons[i] = new SelectionButtonListening(i, comm);
			vendingMachine.getSelectionButton(i).register(buttons[i]);
		}
		for (int i = 0; i < 6; i++) {
			canRacks[i] = new PopCanRackListening();
			vendingMachine.getPopCanRack(i).register(canRacks[i]);
			vendingMachine.getPopCanRack(i).load(new PopCan(vendingMachine.getPopKindName(i)));
		}
		comm.linkVending(receptacle, changeLight, outOfOrderLight, canRacks, vendingMachine, rackMap);
		

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

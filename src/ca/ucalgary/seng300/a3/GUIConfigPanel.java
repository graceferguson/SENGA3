package ca.ucalgary.seng300.a3;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.lsmr.vending.PopCan;
import org.lsmr.vending.hardware.CoinRack;
import org.lsmr.vending.hardware.ConfigurationPanel;
import org.lsmr.vending.hardware.VendingMachine;

/**
 * The configuration window of a simulated vending machine
 *
 */
public class GUIConfigPanel extends JFrame {
	
	private JTextField display;
	private GUIConfigButtons buttonPanel;
	private ConfigurationPanel configPanel;

	/**
	 * @param comm
	 *
	 */
	public GUIConfigPanel() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1100, 350);
		setTitle("Configuration Panel");
		configPanel = new ConfigurationPanel();
	}

	/**
	 * Initializes and displays configuration manager window.
	 */
	public void init() {
		setLayout(new BorderLayout());

		buttonPanel = new GUIConfigButtons(configPanel);

		createDisplay();
		buttonPanel.createButtons();

		getContentPane().add(buttonPanel, BorderLayout.CENTER);

		setVisible(true);
		setResizable(true);
	}

	
	public void createDisplay() {		

		display = new JTextField("");
		
		display.setPreferredSize(new Dimension(1000, 75));
		display.setEditable(false);
		add(display, BorderLayout.NORTH);
		setVisible(true);
	}

	public void updateDisplay(String message) {
		display.setText(message);
		setVisible(true);
	}
	
	
	
	/*
	 * Inner class to create buttons for Configuation Panel GUI
	 */
	private class GUIConfigButtons extends JPanel {
		
		private int buttonLength = 38;
		private JButton[] buttons;
		private String[] upperChar = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
				"A", "S", "D", "F", "G", "H", "J", "K", "L", "Shift", "Z", "X", "C", "V", "B", "N", "M", "Enter"};
		
		private ConfigurationPanel configPanel;
		
		public GUIConfigButtons(ConfigurationPanel configPanel) {
			this.configPanel = configPanel;
		}

		public void createButtons() {
			buttons = new JButton[buttonLength];
			
			for(int i = 0; i < buttonLength; i++) {
				buttons[i] = new JButton(upperChar[i]);
				buttons[i].setPreferredSize(new Dimension(100, 50));
				add(buttons[i]);
				buttons[i].addActionListener(new GUIConfigListening(i, configPanel));
			}

			setVisible(true);
		}
	}
	
	
//FOR TESTING
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
		emptyMsgLoop msgLoop = new emptyMsgLoop("Hi there!");
		CoinReceptacleListening receptacle = new CoinReceptacleListening(50,msgLoop); //ESB 
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
			buttons[i] = new SelectionButtonListening(i);
			vendingMachine.getSelectionButton(i).register(buttons[i]);
		}
		for (int i = 0; i < 6; i++) {
			canRacks[i] = new PopCanRackListening();
			vendingMachine.getPopCanRack(i).register(canRacks[i]);
			vendingMachine.getPopCanRack(i).load(new PopCan(vendingMachine.getPopKindName(i)));
		}
		comm.linkVending(receptacle, changeLight, outOfOrderLight, canRacks, vendingMachine, rackMap, null, numPopTypes);
		

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
		
		
		new GUIConfigPanel().init();

	}
}

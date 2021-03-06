package ca.ucalgary.seng300.a3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.awt.event.*;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.lsmr.vending.*;
import org.lsmr.vending.hardware.*;

import ca.ucalgary.seng300.a3.*;

import javax.swing.JTextArea;
import javax.swing.JLabel;

public class GUI extends JFrame{
    private JPanel p = new JPanel(new GridBagLayout());
    private GridBagConstraints c = new GridBagConstraints();
    //define colors to be used in the GUI
    private Color chartreuse = new Color(127,255,0);
    private Color clientBlue = new Color(0, 0, 255);
    
    //Indicator "lights", they are labels with a background that changes
    private JLabel outOfOrder = new JLabel("Out of Order", SwingConstants.CENTER);
    
    private JLabel exactOnly = new JLabel("Exact change only", SwingConstants.CENTER);
    
    //Display Box
    private JLabel display = new JLabel("Default Display", SwingConstants.CENTER);
    
    
    //input slots are actually 3 objects, a label, a input box, and a button, similar thing for card payments
    private JLabel coinLabel = new JLabel("Coin Slot: ", SwingConstants.RIGHT);
    private JTextField coinInput = new JTextField("Type a coin value");
    private JButton coinButton = new JButton("Enter coins");
    
    //Request to return coins button
    private JButton requestButton = new JButton("Return coins");
    
    //for credit card if you wish to extend the vending machine
    private JLabel cardLabel = new JLabel("Card Slot: ", SwingConstants.RIGHT);
    private JTextField cardInput = new JTextField("Type a value");
    private JButton cardButton = new JButton("Confirm payment");
   
    private JButton lockUnlock = new JButton("Lock");
    
    //Soda Selection Buttons
    //An array that doesn't have a set size to start with
    private JButton[] selection;
    
    //Coin return is basically like input again, but with a label instead of input
    private JLabel retLabel = new JLabel("Coin Return: ", SwingConstants.RIGHT);
    private JLabel retInput = new JLabel("0", SwingConstants.CENTER);
    private JButton retButton = new JButton("Unload coins");
    private int	retAmount = 0;
    
    private JLabel popRetLabel = new JLabel("Pops in delivery chute:",SwingConstants.RIGHT);
    private JTextArea popRetInput = new JTextArea("(None)");
    //private JLabel popRetInput = new JLabel(popRetDefaultText,SwingConstants.CENTER);
    private JButton popRetButton = new JButton("Unload pop");
    private int lastButtonPressed = -1; 
    private String popsReturned = "(None)";
    
    private VendingMachine vend;
    private VendCommunicator communicator;
    
    ConfigPanelDisplayListening configPanelDisplayListening;
    
    /**
     * 
     * @param vend the VendingMachine connected to the GUI
     * @param comm the VendCommunicator connected to the GUI
     */
    public GUI(VendingMachine vend, VendCommunicator comm) {
    		super("Vending Machine GUI");
    		//enable "X" to close the application and kill the process, without this line it continues to run but invisibly
    		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    		p.setBackground(chartreuse);
    		
	    	//pass objects to the class attributes
    		communicator = comm;
    		int numSelections = vend.getNumberOfPopCanRacks();
    		this.vend = vend;
        
	//size of the JFrame when opened 600 x 700 px
        setSize(600, 700);
        setResizable(true);
        selection = new JButton[numSelections];
        
        //Set the selection buttons up
        for(int i = 0; i < numSelections;i++) {
        	double theCost = vend.getPopKindCost(i)/100.0;
            selection[i] = new JButton(vend.getPopKindName(i) + "  $" + theCost);
        }
        
        //this makes all the buttons the same size in a cell
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 0, 5, 0);
        
        //Configure backgrounds/colors and the such
        display.setForeground(Color.blue);
        
        //place lock button on grid
        c.gridy =12 ;
        c.gridx =3 ;
        p.add(lockUnlock, c);
        
        //place out of order light on grid
        c.gridy =0 ;
        c.gridx =3 ;
        p.add(outOfOrder, c);

	//place exact change only light on grid
        c.gridy = 1;
        c.gridx = 3;
        p.add(exactOnly, c);
 
	//place that shows contents of the displayy
        c.gridy = 0;
        c.gridx = 1;
        c.gridwidth = 2;
        p.add(display, c);
        
	//label for the coin input
        c.gridwidth=1;
        c.gridy = 3;
        c.gridx = 0;
        p.add(coinLabel, c);
        
	//coin input text field
        c.weightx = 0.7;
        c.gridy = 3;
        c.gridx = 1;
        p.add(coinInput, c);
        
	//button used to insert coins after value typed
        c.weightx = 0.3;
        c.gridy = 3;
        c.gridx = 2;
        p.add(coinButton, c);
        
        c.weightx = 0.5;
        c.gridy = 3;
        c.gridx = 3;
        p.add(requestButton, c);
        
        //set constraints for pop buttons outside of loop since they are all the same
        
        c.gridwidth = 2;
        c.gridx = 1;
        
        //for loop for soda selection buttons, sets style
        for(int i = 0; i < numSelections; i++) {
            c.gridy = 5+i;
            p.add(selection[i], c);
            selection[i].setBackground(clientBlue);
            selection[i].setForeground(Color.WHITE);
            selection[i].setOpaque(true);
            selection[i].setBorderPainted(false);
        }    

        //reset c.gridwidth to 1 and restore other constraints
        c.gridwidth = 1;
        
        c.gridy = numSelections + 5;
        c.gridx = 0;
        p.add(retLabel, c);

        c.gridy = numSelections + 5;
        c.gridx = 1;
        p.add(retInput, c);
        
        c.gridy = numSelections + 5;
        c.gridx = 2;
        p.add(retButton, c);
        
        c.gridy = numSelections + 6;
        c.gridx = 0;
        p.add(popRetLabel, c);
        
        c.gridy = numSelections + 6;
        c.gridx = 1;
        p.add(popRetInput, c);
        
        c.gridy = numSelections + 6;
        c.gridx = 2;
        p.add(popRetButton, c);
           
        add(p);
        setVisible(true);
        
        //Event handling to connect the GUI to VendCommunicator
        
        //Connecting the pop buttons to the vending machine
        for (int i = 0; i < selection.length; i++) {
        	selection[i].addActionListener(new PopButtonActionListener(i, vend, this, communicator));
        }
        
        //Connecting the coin entering elements together
        coinInput.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                coinInput.setText("");
            }
        });
	//register action listener for the insert coin button
        CoinInputGUIDocListener cigl = new CoinInputGUIDocListener(coinInput);
        coinInput.getDocument().addDocumentListener(cigl);
        coinButton.addActionListener(new CoinButtonActionListener(cigl, vend));
        
        //Connecting GUI display to vending machine display
        GUIDisplayListener guidisplay = new GUIDisplayListener(vend, this);
        
        //Connecting indicator lights
        GUIIndicatorLightListener lights = new GUIIndicatorLightListener(this, vend);
        
        //Connecting coin return
        GUICoinReturnListener coinRet = new GUICoinReturnListener(vend, this);
        
	//adding action listener for the lock, unlock button
        GUILockPanelListener lock = new GUILockPanelListener(this, vend);
        lockUnlock.addActionListener(new LockUnlockButtonListener(vend,this,lock));        
       
        //Connecting unload coins button
        retButton.addActionListener(new RetButtonActionListener(vend, this, coinRet));
        
        //Connecting pop delivery chute elements
        GUIPopReturnListener popsInChute = new GUIPopReturnListener(vend, this);
        popRetButton.addActionListener(new PopUnloadButtonActionListener(vend, this, popsInChute));
        popRetInput.setEditable(false);
        
        //Connecting return coins button
        requestButton.addActionListener(new CoinReturnButtonActionListener(vend, this, communicator));
        
    }
    
    /**
     * Changes the text of the pop buttons when their values are updated
     * @param buttonIndex
     * @param newText
     */
    public void setPopButtonText(int buttonIndex, double newPrice) {
    	selection[buttonIndex].setText(vend.getPopKindName(buttonIndex) + "  $" + newPrice);
    }
    
    /**
     * Adds labels and buttons to allow for credit card payments
     */
    public void enableCreditCard() {
    	c.gridy = 4;
        c.gridx = 0;
        p.add(cardLabel, c);

        c.gridy = 4;
        c.gridx = 1;
        p.add(cardInput, c);

        c.gridy = 4;
        c.gridx = 2;
        p.add(cardButton, c);
        
        setVisible(true);
    }
    
    /**
     * Changes colour of Out of Order light to simulate it turning on
     */
    public void orderLightOn() {
        outOfOrder.setBackground(Color.MAGENTA);
        outOfOrder.setForeground(Color.white);
        outOfOrder.setOpaque(true);
        setVisible(true);
    	
    }
    
    /**
     * Changes colour of Out of Order light to simulate it turning off
     */
    public void orderLightOff() {
    	outOfOrder.setBackground(Color.white);
        outOfOrder.setForeground(Color.black);
        outOfOrder.setOpaque(false);
        setVisible(true);
    }
    
    /**
     * Changes colour of Exact Change Only light to simulate it turning on
     */
    public void changeLightOn() {
    	exactOnly.setBackground(Color.MAGENTA);
        exactOnly.setForeground(Color.white);
        exactOnly.setOpaque(true);
        setVisible(true);
    }
    
    /**
     * Changes colour of Out of Order light to simulate it turning off
     */
    public void changeLightOff() {
    	exactOnly.setBackground(Color.white);
        exactOnly.setForeground(Color.black);
        exactOnly.setOpaque(false);
        setVisible(true);
    }
    
    /**
     * Setter method for changing the display message
     * @param: message: String to be displayed
     */
    public void setDisplay(String message) {
    	display.setText(message);
    	setVisible(true);
    }
    // makes the vending machine display the current credit
    public void displayCredit() {
    	double val = communicator.getCredit()/100.0;
    	setDisplay("Credit: $" + val);
    }
    
    /**
     * Setter method for changing the value displayed in the coin return.
     * @params: value: the integer value to be set
     */
    public void setCoinReturnVal(int value) {
    	double decVal = value/100.0;
    	retInput.setText("$" + Double.toString(decVal));
    	retAmount = value;
    	setVisible(true);
    }
    //returns the amount of change in the coin return
    public int getCoinReturnVal() {
    	return retAmount;
    }

    
    /*
     * Setter method for changing the value displayed in the pop delivery chute.
     * @param: value: the integer value to be set
     */
    public void setPopReturnVal(String popsInChute) {
    	popRetInput.setText(popsInChute);
    	popsReturned = popsInChute;
    	setVisible(true);
    }
    
    public String getPopsReturned() {
    	return popsReturned;
    }    
    
    public void changeTextLock(String text) {
    	lockUnlock.setText(text);
    }
    
    public void setLastButtonPressed(int newPress) {
    	lastButtonPressed = newPress;
    }
    
    public int getLastButtonPressed() {
    	return lastButtonPressed;
    }
    
    //******************* MAIN METHOD **********************
    public static void main(String[] args) {
	 //valid coin amounts
    	int[] canadianCoins = { 5, 10, 25, 100, 200 };
	//vending machine parameters
    	int coinRackCapacity = 15; 
    	int numPopTypes = 6;
		VendingMachine vendingMachine = new VendingMachine(canadianCoins, numPopTypes, coinRackCapacity, 15, 200, 200, 15);
		VendCommunicator comm = VendCommunicator.getInstance();
		
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
	    	//-------------- Register Listeners for everything -----------------
		CoinSlotListening slot = new CoinSlotListening();
		CoinRackListening[] racks = new CoinRackListening[canadianCoins.length];
		SelectionButtonListening[] buttons = new SelectionButtonListening[numPopTypes];
	    	//start the display loop that flashes hi there when there is no credit
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
	    	//register listeners for each coin rack
		for (int i = 0; i < canadianCoins.length; i++) {
			racks[i] = new CoinRackListening(canadianCoins[i]);
			vendingMachine.getCoinRack(i).register(racks[i]);
			//machine.getCoinRack(i).connect(new CoinChannel(new CoinReturn(200)));
			rackMap.put(vendingMachine.getCoinRack(i), racks[i]);
		}
	    	//register a listener for each selection button
		for (int i = 0; i < numPopTypes; i++) {
			buttons[i] = new SelectionButtonListening(i);
			vendingMachine.getSelectionButton(i).register(buttons[i]);
		}
	    	// register listener for each pop can rack
		for (int i = 0; i < 6; i++) {
			canRacks[i] = new PopCanRackListening();
			vendingMachine.getPopCanRack(i).register(canRacks[i]);
			vendingMachine.getPopCanRack(i).load(new PopCan(vendingMachine.getPopKindName(i)));
		}
		//register listeners for the configuration panel buttons
		for (int i = 0; i < 37; i++) {
			vendingMachine.getConfigurationPanel().getButton(i).register(new SelectionButtonListening(i));
		}
		//special listener for the enter button
		vendingMachine.getConfigurationPanel().getEnterButton().register(new SelectionButtonListening(37));
		
		comm.linkVending(receptacle, changeLight, outOfOrderLight, canRacks, vendingMachine, rackMap, null, 0, null);
		

		// Customize the pop kinds and pop costs in the vending machine
		java.util.List<String> popCanNames = Arrays.asList("Cola","Sprite","Fonda","Diet","Ginger Ale","Dr Pepper");
		java.util.List<Integer> popCanCosts = Arrays.asList(325,250,250,250,250,250);
		int[] popCanCounts = new int[vendingMachine.getNumberOfPopCanRacks()];
		for (int i = 0; i < popCanCounts.length; i++) {
			popCanCounts[i] = 1;
		}
		/*configure the vending machine with the pop cans and prices above 
		* and load pop cans to their max capacity
		*/
		vendingMachine.configure(popCanNames, popCanCosts);		
		vendingMachine.loadPopCans(popCanCounts);
		
		int[] coinLoading = new int[vendingMachine.getNumberOfCoinRacks()];
		for (int i = 0; i < coinLoading.length; i++) {
			coinLoading[i] = coinRackCapacity - 5;
		}
		vendingMachine.loadCoins(coinLoading);
		
		GUI gui = new GUI(vendingMachine, comm);
		
		GUIConfigPanel configP = new GUIConfigPanel(vendingMachine);
		ConfigPanelDisplayListening configPanelDisplayListening = new ConfigPanelDisplayListening(configP);
		vendingMachine.getConfigurationPanel().getDisplay().register(configPanelDisplayListening);
		//configP.init();
		
		ConfigPanelLogicListener configListener = new ConfigPanelLogicListener(gui); // Where is the ConfigPanelLogic initialized????????????????
		
    } // end main
    
} // end class

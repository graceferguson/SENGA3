package ca.ucalgary.seng300.a3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.lsmr.vending.hardware.VendingMachine;

import ca.ucalgary.seng300.a2.*;

import javax.swing.JTextArea;
import javax.swing.JLabel;

public class GUI extends JFrame{
    private JPanel p = new JPanel(new GridBagLayout());
    private GridBagConstraints c = new GridBagConstraints();
    
    //Indicator "lights", they are labels with a background that changes
    private JLabel outOfOrder = new JLabel("Out of Order");
    
    private JLabel exactOnly = new JLabel("Exact change only");
    
    //Display Box
    private JLabel display = new JLabel("Default Display");
    
    
    //input slots are actually 3 objects, a label, a input box, and a button, similar thing for card payments
    private JLabel coinLabel = new JLabel("Coin Slot: ");
    private JTextField coinInput = new JTextField("Type a coin value");
    private JButton coinButton = new JButton("Enter coins");
    
    private JLabel cardLabel = new JLabel("Card Slot: ");
    private JTextField cardInput = new JTextField("Type a value");
    private JButton cardButton = new JButton("Confirm payment");
    
    //Soda Selection Buttons
    //An array that doesn't have a set size to start with
    private JButton[] selection;
    
    //Coin return is basically like input again, but with a label instead of input
    private JLabel retLabel = new JLabel("Coin Return: ");
    private JLabel retInput = new JLabel("0");
    private JButton retButton = new JButton("Unload coins");
    
    
  
    
    //FOR TESTING PURPOSES-- REMOVE LATER********************************
    public static void main(String[] args) {
    	int[] canadianCoins = { 5, 10, 25, 100, 200 };


    	int coinRackCapacity = 15; 
    	int numPopTypes = 6;
		VendingMachine vendingMachine = new VendingMachine(canadianCoins, numPopTypes, coinRackCapacity, 15, 200, 200, 15);
		

		// Customize the pop kinds and pop costs in the vending machine
				java.util.List<String> popCanNames = Arrays.asList("Cola","Sprite","Fonda","Diet","Ginger Ale","Dr Pepper");
		java.util.List<Integer> popCanCosts = Arrays.asList(250,250,250,250,250,250);
		int[] popCanCounts = new int[vendingMachine.getNumberOfPopCanRacks()];
		for (int i = 0; i < popCanCounts.length; i++) {
			popCanCounts[i] = 1;
		}
	
		vendingMachine.configure(popCanNames, popCanCosts);		
		vendingMachine.loadPopCans(popCanCounts);
        
		new GUI(vendingMachine);
		
		
		
    }
    
    public GUI(VendingMachine vend) {
    	super("Vending Machine GUI");
    	int numSelections = vend.getNumberOfPopCanRacks(); 
        
        
        setSize(500, 700);
        setResizable(true);
        selection = new JButton[numSelections];
        
        //Set the selection buttons up
        for(int i = 0; i < numSelections;i++) {
            selection[i] = new JButton(vend.getPopKindName(i));
        }
        
        //Configure backgrounds/colors and the such
        display.setForeground(Color.blue);
        

        c.gridy =0 ;
        c.gridx =3 ;
        p.add(outOfOrder, c);

        c.gridy = 1;
        c.gridx = 3;
        p.add(exactOnly, c);

        
        c.gridy = 0;
        c.gridx = 1;
        p.add(display, c);

        c.gridy = 3;
        c.gridx = 0;
        p.add(coinLabel, c);

        c.gridy = 3;
        c.gridx = 1;
        p.add(coinInput, c);

        c.gridy = 3;
        c.gridx = 2;
        p.add(coinButton, c);
        
        //for loop for remaining buttons
        for(int i = 0; i < numSelections; i++) {
            c.gridy = 5+i;
            c.gridx = 2;
            p.add(selection[i], c);
        }    

        c.gridy = numSelections + 5;
        c.gridx = 0;
        p.add(retLabel, c);

        c.gridy = numSelections + 5;
        c.gridx = 1;
        p.add(retInput, c);
        
        c.gridy = numSelections + 5;
        c.gridx = 2;
        p.add(retButton, c);
        
        
        
        add(p);
        setVisible(true);
        
    }
    
    /*
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
    
    
    /*
     * Changes colour of Out of Order light to simulate it turning on
     */
    public void orderLightOn() {
        outOfOrder.setBackground(Color.MAGENTA);
        outOfOrder.setForeground(Color.white);
        outOfOrder.setOpaque(true);
        setVisible(true);
    	
    }
    
    /*
     * Changes colour of Out of Order light to simulate it turning off
     */
    public void orderLightOff() {
    	outOfOrder.setBackground(Color.white);
        outOfOrder.setForeground(Color.black);
        outOfOrder.setOpaque(false);
        setVisible(true);
    }
    
    /*
     * Changes colour of Exact Change Only light to simulate it turning on
     */
    public void changeLightOn() {
    	exactOnly.setBackground(Color.MAGENTA);
        exactOnly.setForeground(Color.white);
        exactOnly.setOpaque(true);
        setVisible(true);
    }
    
    /*
     * Changes colour of Out of Order light to simulate it turning off
     */
    public void changeLightOff() {
    	exactOnly.setBackground(Color.white);
        exactOnly.setForeground(Color.black);
        exactOnly.setOpaque(false);
        setVisible(true);
    }
    
    /*
     * Setter method for changing the display message
     * @param: message: String to be displayed
     */
    public void setDisplay(String message) {
    	display.setText(message);
    	setVisible(true);

    }
    
    
    /*
     * Setter method for changing the value displayed in the coin return.
     * @params: value: the integer value to be set
     */
    public void setCoinReturnVal(int value) {
    	retInput.setText(Integer.toString(value));
    }
    
    
}

package ca.ucalgary.seng300.a3;

import java.util.ArrayList;

import org.lsmr.vending.hardware.VendingMachine;

public class VendingSetup {
//THE VENDING MACHINE
	private VendingMachine myVM;

//PARAMETERS THAT WILL BE PASSED TO INSTANTIATE VENDING MACHINE
	private int [] validCoins = {5,10,25,100,200}; //Array initializer must be up here
	private int ButtonCount;
	private int coinRackCapacity;
	private int popCanRackCapacity;
	private int receptacleCapacity;
	private int deliveryChuteCapacity;
	private int coinReturnCapacity;
	
	
	//pop names, pop costs, to configure vending machine
	private String [] pNames = {"Grape", "Root Beer", "Orange", "Cream Soda", "Coke", "Pepsi"};
	private Integer [] pCosts = {200, 250, 100, 150, 300, 300};
	public int [] capacEachRack = {10, 10, 10, 10, 10, 10};		//for pop
	private ArrayList<String> popNames;
	private ArrayList<Integer> popCosts;
	
	/**
	* Constructor creates the object, and sets the following as default values:
	*		ButtonCount = 6;
	*		coinRackCapacity = 100;
	*		popCanRackCapacity = 10;
	*		receptacleCapacity = 200;
	*		deliveryChuteCapacity = 1;
	*		coinReturnCapacity = 50; 
	*
	*/
	public VendingSetup() {
			ButtonCount = 6;
			coinRackCapacity = 100;
			popCanRackCapacity = 10;
			receptacleCapacity = 200;
			deliveryChuteCapacity = 1;
			coinReturnCapacity = 50; 
			
	//Popcan names
			popNames = new ArrayList<String>(ButtonCount);
			//convert to appropriate type for configuring VM
			for (int i = 0; i < pNames.length; i++) {
				popNames.add(pNames[i]);
			}		
	//Popcan costs
			popCosts = new ArrayList<Integer>(ButtonCount);
			//convert to appropriate type to configure VM
			for (int j = 0; j < pCosts.length; j++) {
				popCosts.add(pCosts[j]);	
			}
		//INSTANTIATE THE NEW VENDING MACHINE
		myVM = new VendingMachine(validCoins, ButtonCount, coinRackCapacity, popCanRackCapacity, receptacleCapacity, deliveryChuteCapacity, coinReturnCapacity);
		//Configure lists of pop names and popCosts 
		myVM.configure(popNames, popCosts);
		//load PopCans into the PopCanRacks up to their maximum capacity for the rack
		myVM.loadPopCans(capacEachRack);
	}
	
	
	/**
	* getter for the setup vending machine
	* @return Vending vm, the vending machine created.
	*/
	public VendingMachine getVendingMachine() {
		return myVM;
	}		
	
	/**
	* getter for the valid coin array
	* @return int[] validCoins, the coin kinds used
	*/
	public int[] getCoinKinds()
	{
		return validCoins;
	}
}

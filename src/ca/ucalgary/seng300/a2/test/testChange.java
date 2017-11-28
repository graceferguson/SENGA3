/**
 * Function for testing change
 * Elodie Boudes 10171818, Grace Ferguson 30004869, 
 * Tae Chyung 10139101, Karndeep Dhami 10031989, 
 * Andrew Garcia-Corley 10015169 & Michael de Grood 10134884
 */


package ca.ucalgary.seng300.a2.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;

import org.junit.Test;
import org.lsmr.vending.hardware.CoinChannel;
import org.lsmr.vending.hardware.CoinRack;
import org.lsmr.vending.hardware.CoinReturn;
import org.lsmr.vending.hardware.VendingMachine;

import ca.ucalgary.seng300.a2.CoinRackListening;
import ca.ucalgary.seng300.a2.CoinReceptacleListening;
import ca.ucalgary.seng300.a2.IndicatorLighListening;
import ca.ucalgary.seng300.a2.OutOfOrderLightListening;
import ca.ucalgary.seng300.a2.PopCanRackListening;
import ca.ucalgary.seng300.a2.VendCommunicator;

public class testChange {
		//Simple test wherein one specific coin is meant to be given as change
		@Test
		public void testSimple() {
			VendingMachine vm = new VendingMachine(new int[] {1,5,10,25,100,200}, 6, 200,10,200, 200, 200);
			vm.configure(Arrays.asList("popA", "popB", "popC", "popD", "popE", "popF"), Arrays.asList(100,100,100,100,150,200));
			CoinRackListening[] crl = new CoinRackListening[vm.getNumberOfCoinRacks()];
			HashMap<CoinRack, CoinRackListening> hm = new HashMap<CoinRack, CoinRackListening>();
			for(int i=0; i<vm.getNumberOfCoinRacks(); i++) {
				crl[i] = new CoinRackListening(vm.getCoinKindForCoinRack(i));
				vm.getCoinRack(i).register(crl[i]);
				vm.getCoinRack(i).connect(new CoinChannel(new CoinReturn(200)));
				hm.put(vm.getCoinRack(i), crl[i]);
			}
			vm.loadCoins(100,100,100,100,100,100);
			
			
			VendCommunicator vc = new VendCommunicator();
			vc.linkVending(new CoinReceptacleListening(100, vc, null),new IndicatorLighListening(), new OutOfOrderLightListening(), new PopCanRackListening[] {new PopCanRackListening()}, vm, hm);
			int a = vc.giveChange(1);
			assertEquals(a, 0);
			assertEquals(crl[0].getCoins(), 99);
			assertEquals(crl[1].getCoins(), 100);
			assertEquals(crl[2].getCoins(), 100);
			assertEquals(crl[3].getCoins(), 100);
			assertEquals(crl[4].getCoins(), 100);
			assertEquals(crl[5].getCoins(), 100);
			
		}
		
		//Tests that the least amount of coins will be given in a specific scenario
		@Test
		public void testBestConfiguration() {
			VendingMachine vm = new VendingMachine(new int[] {1,5,10,25,100,200}, 6, 200,10,200, 200, 200);
			vm.configure(Arrays.asList("popA", "popB", "popC", "popD", "popE", "popF"), Arrays.asList(100,100,100,100,150,200));
			CoinRackListening[] crl = new CoinRackListening[vm.getNumberOfCoinRacks()];
			HashMap<CoinRack, CoinRackListening> hm = new HashMap<CoinRack, CoinRackListening>();
			for(int i=0; i<vm.getNumberOfCoinRacks(); i++) {
				crl[i] = new CoinRackListening(vm.getCoinKindForCoinRack(i));
				vm.getCoinRack(i).register(crl[i]);
				vm.getCoinRack(i).connect(new CoinChannel(new CoinReturn(200)));
				hm.put(vm.getCoinRack(i), crl[i]);
			}
			vm.loadCoins(100,100,100,100,100,100);
			
			VendCommunicator vc = new VendCommunicator();
			vc.linkVending(new CoinReceptacleListening(0, vc, null),new IndicatorLighListening(), new OutOfOrderLightListening(), new PopCanRackListening[] {new PopCanRackListening()}, vm, hm);
			int a = vc.giveChange(30);
			assertEquals(a, 0);
			assertEquals(crl[2].getCoins(), 100);
			assertEquals(crl[1].getCoins(), 99);
			assertEquals(crl[3].getCoins(), 99);
			
		}
		
		//Tests that if there are less coins in the machine than can be given as change, that as much change as possible will be given
		//and no more than that
				@Test
				public void testChangeNotEnough() {
					VendingMachine vm = new VendingMachine(new int[] {1,5,10,25,100,200}, 6, 200,10,200, 200, 200);
					vm.configure(Arrays.asList("popA", "popB", "popC", "popD", "popE", "popF"), Arrays.asList(100,100,100,100,150,200));
					CoinRackListening[] crl = new CoinRackListening[vm.getNumberOfCoinRacks()];
					HashMap<CoinRack, CoinRackListening> hm = new HashMap<CoinRack, CoinRackListening>();
					for(int i=0; i<vm.getNumberOfCoinRacks(); i++) {
						crl[i] = new CoinRackListening(vm.getCoinKindForCoinRack(i));
						vm.getCoinRack(i).register(crl[i]);
						vm.getCoinRack(i).connect(new CoinChannel(new CoinReturn(200)));
						hm.put(vm.getCoinRack(i), crl[i]);
					}
					vm.loadCoins(4,0,1,1,100,100);
					
					VendCommunicator vc = new VendCommunicator();
					vc.linkVending(new CoinReceptacleListening(100, vc, null),new IndicatorLighListening(), new OutOfOrderLightListening(), new PopCanRackListening[] {new PopCanRackListening()}, vm, hm);
					int a = vc.giveChange(30);
					assertEquals(a, 1);
					assertEquals(crl[0].getCoins(), 0);
					assertEquals(crl[1].getCoins(), 0);
					assertEquals(crl[2].getCoins(), 1);
					assertEquals(crl[3].getCoins(), 0);
					assertEquals(crl[4].getCoins(), 100);
					assertEquals(crl[5].getCoins(), 100);
					
				}
				
				//Tests that if there are no coins in the machine, no change is given
				@Test
				public void testNone() {
					VendingMachine vm = new VendingMachine(new int[] {1,5,10,25,100,200}, 6, 200,10,200, 200, 200);
					vm.configure(Arrays.asList("popA", "popB", "popC", "popD", "popE", "popF"), Arrays.asList(100,100,100,100,150,200));
					CoinRackListening[] crl = new CoinRackListening[vm.getNumberOfCoinRacks()];
					HashMap<CoinRack, CoinRackListening> hm = new HashMap<CoinRack, CoinRackListening>();
					for(int i=0; i<vm.getNumberOfCoinRacks(); i++) {
						crl[i] = new CoinRackListening(vm.getCoinKindForCoinRack(i));
						vm.getCoinRack(i).register(crl[i]);
						vm.getCoinRack(i).connect(new CoinChannel(new CoinReturn(200)));
						hm.put(vm.getCoinRack(i), crl[i]);
					}
					vm.loadCoins(0,0,0,0,0,0);
					
					VendCommunicator vc = new VendCommunicator();
					vc.linkVending(new CoinReceptacleListening(100, vc, null),new IndicatorLighListening(), new OutOfOrderLightListening(), new PopCanRackListening[] {new PopCanRackListening()}, vm, hm);
					int a = vc.giveChange(400);
					assertEquals(a, 400);
					assertEquals(crl[0].getCoins(), 0);
					assertEquals(crl[1].getCoins(), 0);
					assertEquals(crl[2].getCoins(), 0);
					assertEquals(crl[3].getCoins(), 0);
					assertEquals(crl[4].getCoins(), 0);
					assertEquals(crl[5].getCoins(), 0);
					
				}
				
				//Tests that no change is given if 0 is an argument to giveChange
				@Test
				public void testNoChangeWanted() {
					VendingMachine vm = new VendingMachine(new int[] {1,5,10,25,100,200}, 6, 200,10,200, 200, 200);
					vm.configure(Arrays.asList("popA", "popB", "popC", "popD", "popE", "popF"), Arrays.asList(100,100,100,100,150,200));
					CoinRackListening[] crl = new CoinRackListening[vm.getNumberOfCoinRacks()];
					HashMap<CoinRack, CoinRackListening> hm = new HashMap<CoinRack, CoinRackListening>();
					for(int i=0; i<vm.getNumberOfCoinRacks(); i++) {
						crl[i] = new CoinRackListening(vm.getCoinKindForCoinRack(i));
						vm.getCoinRack(i).register(crl[i]);
						vm.getCoinRack(i).connect(new CoinChannel(new CoinReturn(200)));
						hm.put(vm.getCoinRack(i), crl[i]);
					}
					vm.loadCoins(100,100,100,100,100,100);
					
					VendCommunicator vc = new VendCommunicator();
					vc.linkVending(new CoinReceptacleListening(100, vc, null),new IndicatorLighListening(), new OutOfOrderLightListening(), new PopCanRackListening[] {new PopCanRackListening()}, vm, hm);
					int a = vc.giveChange(0);
					assertEquals(a, 0);
					assertEquals(crl[0].getCoins(), 100);
					assertEquals(crl[1].getCoins(), 100);
					assertEquals(crl[2].getCoins(), 100);
					assertEquals(crl[3].getCoins(), 100);
					assertEquals(crl[4].getCoins(), 100);
					assertEquals(crl[5].getCoins(), 100);
					
				}
				
				//Simple test wherein the machine can give change
				@Test
				public void testHasChange() {
					VendingMachine vm = new VendingMachine(new int[] {1,5,10,25,100,200}, 6, 200,10,200, 200, 200);
					vm.configure(Arrays.asList("popA", "popB", "popC", "popD", "popE", "popF"), Arrays.asList(100,100,100,100,150,200));
					CoinRackListening[] crl = new CoinRackListening[vm.getNumberOfCoinRacks()];
					HashMap<CoinRack, CoinRackListening> hm = new HashMap<CoinRack, CoinRackListening>();
					for(int i=0; i<vm.getNumberOfCoinRacks(); i++) {
						crl[i] = new CoinRackListening(vm.getCoinKindForCoinRack(i));
						vm.getCoinRack(i).register(crl[i]);
						vm.getCoinRack(i).connect(new CoinChannel(new CoinReturn(200)));
						hm.put(vm.getCoinRack(i), crl[i]);
					}
					vm.loadCoins(100,100,100,100,100,100);
					
					VendCommunicator vc = new VendCommunicator();
					vc.linkVending(new CoinReceptacleListening(100, vc, null),new IndicatorLighListening(), new OutOfOrderLightListening(), new PopCanRackListening[] {new PopCanRackListening()}, vm, hm);
					assertTrue(vc.hasChange());
					
				}
				
				//Simple test wherein the machine cannot be guaranteed to give change
				@Test
				public void testCantGuaranteeChange() {
					VendingMachine vm = new VendingMachine(new int[] {1,5,10,25,100,200}, 6, 200,10,200, 200, 200);
					vm.configure(Arrays.asList("popA", "popB", "popC", "popD", "popE", "popF"), Arrays.asList(100,100,100,100,150,200));
					CoinRackListening[] crl = new CoinRackListening[vm.getNumberOfCoinRacks()];
					HashMap<CoinRack, CoinRackListening> hm = new HashMap<CoinRack, CoinRackListening>();
					for(int i=0; i<vm.getNumberOfCoinRacks(); i++) {
						crl[i] = new CoinRackListening(vm.getCoinKindForCoinRack(i));
						vm.getCoinRack(i).register(crl[i]);
						vm.getCoinRack(i).connect(new CoinChannel(new CoinReturn(200)));
						hm.put(vm.getCoinRack(i), crl[i]);
					}
					vm.loadCoins(1,1,1,1,1,1);
					
					VendCommunicator vc = new VendCommunicator();
					vc.linkVending(new CoinReceptacleListening(100, vc, null),new IndicatorLighListening(), new OutOfOrderLightListening(), new PopCanRackListening[] {new PopCanRackListening()}, vm, hm);
					assertFalse(vc.hasChange());
					
				}
				
				//Ensures the machine cannot guarantee change if all coinracks are empty
				@Test
				public void testHasChangeNoCoins() {
					VendingMachine vm = new VendingMachine(new int[] {1,5,10,25,100,200}, 6, 200,10,200, 200, 200);
					vm.configure(Arrays.asList("popA", "popB", "popC", "popD", "popE", "popF"), Arrays.asList(100,100,100,100,150,200));
					CoinRackListening[] crl = new CoinRackListening[vm.getNumberOfCoinRacks()];
					HashMap<CoinRack, CoinRackListening> hm = new HashMap<CoinRack, CoinRackListening>();
					for(int i=0; i<vm.getNumberOfCoinRacks(); i++) {
						crl[i] = new CoinRackListening(vm.getCoinKindForCoinRack(i));
						vm.getCoinRack(i).register(crl[i]);
						vm.getCoinRack(i).connect(new CoinChannel(new CoinReturn(200)));
						hm.put(vm.getCoinRack(i), crl[i]);
					}
					vm.loadCoins(0,0,0,0,0,0);
					
					VendCommunicator vc = new VendCommunicator();
					vc.linkVending(new CoinReceptacleListening(100, vc, null),new IndicatorLighListening(), new OutOfOrderLightListening(), new PopCanRackListening[] {new PopCanRackListening()}, vm, hm);
					assertFalse(vc.hasChange());
					
				}
				
				//One minimum configuration that will result in change being guaranteed for canadian coins
				@Test
				public void testHasChangeMin() {
					VendingMachine vm = new VendingMachine(new int[] {1,5,10,25,100,200}, 6, 200,10,200, 200, 200);
					vm.configure(Arrays.asList("popA", "popB", "popC", "popD", "popE", "popF"), Arrays.asList(100,100,100,100,150,200));
					CoinRackListening[] crl = new CoinRackListening[vm.getNumberOfCoinRacks()];
					HashMap<CoinRack, CoinRackListening> hm = new HashMap<CoinRack, CoinRackListening>();
					for(int i=0; i<vm.getNumberOfCoinRacks(); i++) {
						crl[i] = new CoinRackListening(vm.getCoinKindForCoinRack(i));
						vm.getCoinRack(i).register(crl[i]);
						vm.getCoinRack(i).connect(new CoinChannel(new CoinReturn(200)));
						hm.put(vm.getCoinRack(i), crl[i]);
					}
					vm.loadCoins(4,1,2,3,1,0);
					
					VendCommunicator vc = new VendCommunicator();
					vc.linkVending(new CoinReceptacleListening(100, vc, null),new IndicatorLighListening(), new OutOfOrderLightListening(), new PopCanRackListening[] {new PopCanRackListening()}, vm, hm);
					assertTrue(vc.hasChange());
					
				}

}

/**
 * Function for coin return listener
 * Elodie Boudes 10171818, Grace Ferguson 30004869, 
 * Tae Chyung 10139101, Karndeep Dhami 10031989, 
 * Andrew Garcia-Corley 10015169 & Michael de Grood 10134884
 */
package ca.ucalgary.seng300.a2;

import org.lsmr.vending.Coin;
import org.lsmr.vending.hardware.AbstractHardware;
import org.lsmr.vending.hardware.AbstractHardwareListener;
import org.lsmr.vending.hardware.CoinReturn;
import org.lsmr.vending.hardware.CoinReturnListener;

public class CoinReturnListening implements CoinReturnListener{
	
	int value = 0;

	/**
	 *  enabled coinreturn
	 */
	@Override
	public void enabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		
	}

	/** 
	 * disabled coinreturn 
	 */
	@Override
	public void disabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		
	}

	/**
	 * coinsDelibered 
	 */
	@Override
	public void coinsDelivered(CoinReturn coinReturn, Coin[] coins) {
		for(Coin coin : coins) {
			value += coin.getValue();
		}
	}

	/**
	 * returnIsFull
	 */
	@Override
	public void returnIsFull(CoinReturn coinReturn) {
		
	}
	
	/**
	 * getValue of coinsreturn 
	 * @return value of the coin
	 */
	public int getValue() {
		return value;
	}

}
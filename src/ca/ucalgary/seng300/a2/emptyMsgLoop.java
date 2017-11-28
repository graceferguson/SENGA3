
/**
 * Function for empty messsage loop
 * Elodie Boudes 10171818, Grace Ferguson 30004869, 
 * Tae Chyung 10139101, Karndeep Dhami 10031989, 
 * Andrew Garcia-Corley 10015169 & Michael de Grood 10134884
 */


package ca.ucalgary.seng300.a2;
import java.lang.InterruptedException;

/**
* Class that handles the looping message that occurs when the number of credits in the machine is 0.
* In the form of a thread so that it can run while the machine itself is waiting for coin insertion/button presses
*/
public class emptyMsgLoop implements Runnable
{
	private String message;
	volatile private Boolean reactivate;
	private VendCommunicator communicator;
	private Thread msgLoopThread;
	private Boolean reactivateReady;
	
	public emptyMsgLoop(String message, VendCommunicator communicator)
	{
		this.message = message;
		reactivate = false;
		this.communicator = communicator;
		msgLoopThread = new Thread(this);
		reactivateReady = false;
	}
	
	public Thread startThread()
	{
		msgLoopThread.start();
		return msgLoopThread;
	}
	
	public void interruptThread()
	{
		msgLoopThread.interrupt();
	}
	
	// function that reactivates the looping message. Called by coinReceptacleListening when coin receptacle is empty.
	public void reactivateMsg()
	{
		reactivate = true;
	}
	
	public Boolean reactivateCheck()
	{
		return reactivateReady;
	}
	
	public void run()
	{
		// overall loops indefinitely until program stops
		while(true)
		{
			// while waiting for an interrupt, does the following
			try
			{
				while(true)
				{
					communicator.displayMsg(message);
					if (!communicator.hasChange() && !communicator.getChangeLightFlag()){
						communicator.setChangeLightFlag(true);
						communicator.changeLight(true);
					}
					else {
						communicator.setChangeLightFlag(false);
						communicator.changeLight(false);	
						
					}
					Thread.sleep(5000);
					communicator.displayMsg("");
					Thread.sleep(10000);
				}
			}
			// when an interrupt is received 
			catch(InterruptedException e)
			{
				reactivateReady = true;
				while(reactivate == false){}
				reactivate = false;
				reactivateReady = false;
			}
		}
	}
}
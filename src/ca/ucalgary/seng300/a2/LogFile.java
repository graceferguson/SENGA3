
/**
 * Function for delivery chute listener
 * Elodie Boudes 10171818, Grace Ferguson 30004869, 
 * Tae Chyung 10139101, Karndeep Dhami 10031989, 
 * Andrew Garcia-Corley 10015169 & Michael de Grood 10134884
 */

package ca.ucalgary.seng300.a2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;

public class LogFile {

	//Each action of the user and the actions of the machine that are visible to the user should be logged
	//in a text file (called the "event log").  You can determine the format of this text file.  
	//Be aware that the vending machine possesses an internal clock, as per a standard Java virtual machine.
	
	static boolean filecreated = false;
    static private PrintWriter pw = null;
    static private FileWriter fw = null;
  //  static File file = null; 
    
    static DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
    static Date dateobj = new Date();
    
// public static void main(String[] args) throws IOException
// {
//	 filecreated = createLogFile(); 
//	writeLog("test"); 
//	writeLog("test2");

// }

    
    public static void closeLogFile () throws IOException {
    	fw.close(); 
    }
    
    
	public static boolean createLogFile () throws FileNotFoundException, UnsupportedEncodingException {
		
		String fileName = "LogEvent.txt";
		String startingMessage = "Time of creation " + df.format(dateobj) + "\\n\\n";
		File f = new File(fileName);
		try
        {
			if(f.exists() ) {

				fw = new FileWriter(fileName,true);
				startingMessage = "\n--------------------This is the start of a new run for VM--------------------- " + startingMessage; 
			}
			else {
				fw = new FileWriter(fileName); // write in the package folder 
			
        }
            pw = new PrintWriter(fw);
            pw.println(startingMessage);
        }
        catch (IOException e)
        {
            System.out.println("Error while writting the file");
        }  
		
        filecreated = true; 
		return filecreated;
		
	}
	
	/**
	 * 
	 * @param input: String with the following format 
	 * 		dd/MM/yy HH:mm:ss \t class name \t message 
	 * @throws IOException
	 */
	public static void writeLog(String input) throws IOException{
		// input format: listener date/time + tab + listener name + tab + message 
		// df.format(dateobj) + "\t" + .getClass().getName() + “\t” + “message you want to store”
		// static DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
	    // static Date dateobj = new Date();
		// import java.util.Date; 
		pw.append("\n"+input);
		pw.flush();
	}
	
	



	
}

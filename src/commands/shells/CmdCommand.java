package commands.shells;

//get access to a cmd prompt
//imports
import terminal.*;
import remote.*;
import java.io.InputStream;
import java.io.OutputStream;

public class CmdCommand {
	//variables
	public static boolean hasInput = false;
	public static String line;
	//constructor
	public CmdCommand() {

	}

	//methods
	//set hasInput
	public static void setHasInput(boolean yes){
		hasInput = yes;
	}//end of setHasInput
	//set line
	public static void setLine(String inLine){
		line = inLine;
	}//end of setLine
	//spawn a cmd and redirect stdin/stdout/stderr
	public void execute(Connection conn, String command){
		//check if you are on a windows machine
		if (!Terminal.os.contains("Windows")){
			conn.writeDisln("\nyou are not on a windows machine\n");
			return;
		}

		//pause shell that is currently running
		//Terminal.pauseShell();

		//do stuff in this thread
		String cmd = "cmd.exe";
		//Console c = System.console();
		try{
			/*Console c = System.console();
			if (c == null){
				System.err.println("no console");
				return;
			}
			System.out.println("got a console");
			*/
			conn.writeDisln("dropping into the cmd prompt");
			ProcessBuilder pb = new ProcessBuilder(cmd).redirectErrorStream(true);
			//this will not work with a remote access tool
			//pb.inheritIO();

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
		} catch (Exception e){
			conn.writeDisln("process start IOException: " + e);
			e.printStackTrace();
		}

		//restart terminal shell
		//Terminal.startShell();
	}//end of execute with just command

	//print help if any arguments are provided
	public void execute(Connection conn, String command, String args){
		//check if -h is used
		if (args.equals("-h")) {
			help(conn);
		} else {
			//print some more descriptive help
			conn.writeDisln("\narguments will not be interpreted\n");
			execute(conn, command);
		}
	}//end of two arguments execute
	//help output
	public void help(Connection conn) {
		//display help information for this command
		//System.out.println("\ncmd - help information\n");
		//System.out.println("drops into a cmd prompt\n");
		//System.out.println("\t-h\tprints help information");
		conn.writeDisln("\ncmd - help information\n");
		conn.writeDisln("drops into a cmd prompt\n");
		conn.writeDisln("\t-h\tprints help information");
	}// end of help method
}//end of CmdCommand class

package commands.filesystem;

//view the contents of a file
//imports
import terminal.*;
import remote.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TouchCommand{
	//variables
	//constructors
	public TouchCommand(){
	}//end of CatCommand constructor

	//methods
	//execute method
	public void execute(Connection conn, String command){
		//shouldn't have this command withtout arguments
		conn.writeDisln("\ncommand needs arguments\n");
		help(conn);
	}//end of execute method
	
	//execute method with arguments
	public void execute(Connection conn, String command, String args){
		//declare file variable
		File toTouch;

		//convert args to file
		if (args.startsWith("~")){
			toTouch = new File(Terminal.help.getHomeDir().toString() + args.substring(1));
		} else {
			toTouch = Terminal.help.getCurPwd().toPath().resolve(Paths.get(args)).normalize().toFile();
		}

		//create the new file
		try {
			if(toTouch.createNewFile()){
				conn.writeDisln("\nfile successfully created\n");
			} else {
				conn.writeDisln("\nfile exists already\n");
			}
		} catch (IOException ioe){
			conn.writeDisln("\nIOException:\n\n" + ioe.getLocalizedMessage() + "\n");
		} catch (SecurityException se){
			conn.writeDisln("\nSecurityException:\n\n" + se.getLocalizedMessage() + "\n");
		}
	}//end of execute with args methods
	
	//help method
	public void help(Connection conn){
		//display help information for this command
		conn.writeDisln("\ntouch - help information\n");
		conn.writeDisln("creates and empty file in the current directory by default\n");
		conn.writeDisln("accepts relative and absolute paths\n");
		conn.writeDisln("\nExamples:\n");
		conn.writeDisln("\ttouch [file]\n");
		if (Terminal.os.contains("Windows")){
			//display windows help
			conn.writeDisln("\ttouch [C:\\path\\to\\new-file]\n");
		} else {
			//display linux help
			conn.writeDisln("\ttouch [/path/to/new-file]\n");
		}
	}//end of help method
}//end of TouchCommand class

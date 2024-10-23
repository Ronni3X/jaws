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

public class CatCommand{
	//variables
	//constructors
	public CatCommand(){
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
		File toCat;
		//convert args to file
		if (args.startsWith("~")){
			toCat = new File(Terminal.help.getHomeDir().toString() + args.substring(1));
		} else {
			toCat = Terminal.help.getCurPwd().toPath().resolve(Paths.get(args)).normalize().toFile();
		}
		//check if file exists
		if(!toCat.exists()){
			conn.writeDisln("\nfile doesn't exist\n");
			return;
		}

		//check if file is a directory
		if(toCat.isDirectory()){
			conn.writeDisln("\nprovided file is a directory\n");
			return;
		}

		//check if permissions are valid
		if(!toCat.canRead()){
			conn.writeDisln("\nPermission denied. Do not have read privileges.");
			conn.writeDisln("don't read so good\n");
			return;
		}

		try{
			//read in bytes of file and display them
			byte[] fileConts = Files.readAllBytes(toCat.toPath());

			//display those bytes as string
			conn.writeDisln("");
			conn.writeDisln(new String(fileConts, StandardCharsets.UTF_8));
		} catch (IOException ioe){
			conn.writeDisln("\n" + ioe.getLocalizedMessage() + "\n");
		}
	}//end of execute with args methods
	
	//help method
	public void help(Connection conn){
		//display help information for this command
		conn.writeDisln("\ncat - help information\n");
		conn.writeDisln("displays the contents of a file in the current directory by default\n");
		conn.writeDisln("accepts relative and absolute paths\n");
		conn.writeDisln("\nExamples:\n");
		conn.writeDisln("\tcat [file]\n");
		if (Terminal.os.contains("Windows")){
			//display windows help
			conn.writeDisln("\tcat [C:\\path\\to\\file]\n");
		} else {
			//display linux help
			conn.writeDisln("\tcat [/path/to/file]\n");
		}
	}//end of help method
}//end of CatCommand class

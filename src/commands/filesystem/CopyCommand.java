package commands.filesystem;

//view the contents of a file
//imports
import terminal.*;
import remote.*;
import commands.ArgParser;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.Arrays;

public class CopyCommand{
	//variables
	//constructors
	public CopyCommand(){
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
		//declare argpaser helper class
		ArgParser argParser = new ArgParser();

		//convert args to vals
		String[] toks = args.split(" ");
		String[] fixedArgs = new String[2];
		try {
			fixedArgs = argParser.fixQuotesInArgs(toks);
		} catch (Exception e){
			conn.writeDisln(e.toString());
			return;
		}
		//check if args is equal to two args
		if(fixedArgs.length != 2){
			conn.writeDisln("\ntwo arguments were not provided\n");
			return;
		}
		//assign vars
		File file2cp;
		//check permissions and tilde on file to copy
		try {
			file2cp = argParser.readCheckAndFix(fixedArgs[0]);
		} catch (Exception e){
			conn.writeDisln(e.toString());
			return;
		}
		//check if destination file has tilde 
		File dest4cp = argParser.checkAndFixTilde(fixedArgs[1]);
		//check write permissions
		if(!dest4cp.canWrite()){
			conn.writeDisln("Can't write to provided file/dir.\nno write so good");
			return;
		}
		//need to check if destination is folder or file
		if(dest4cp.isDirectory()){
			//add the provided filename to destination
			dest4cp = new File(dest4cp.toString() + File.separator + file2cp.getName());
			//conn.writeDisln(dest4cp.toString());
		}
		//check if overwriting
		if(dest4cp.exists()){
			//file exists ask for overwrite
			conn.writeDisln("\ndestination file exists\noverwrite file? (y/N)");
			String input = conn.readDisln();
			if (input.length() == 0 || input.equals("") || input == null || input.equalsIgnoreCase("n")){
				//do not overwrite
				conn.writeDisln("\nsounds good. will not overwrite\n");
				return;
			} else if (!input.equalsIgnoreCase("y")){
				//error
				conn.writeDisln("\nnot sure watcha put in there. but it wasn't y, Y, n, or N. Please try again\n");
				return;
			}
			conn.writeDisln("\n[***] will overwrite file\n");
		}
		//copy file to destination
		try{
			Files.copy(file2cp.toPath(), dest4cp.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
			conn.writeDisln("\ncopied file successfully\n");
		} catch (IOException ioe){
			conn.writeDisln("something went wrong with copy:\n" + ioe.toString());
			return;
		}
	}//end of execute with args methods
	
	//help method
	public void help(Connection conn){
		//display help information for this command
		conn.writeDisln("\ncopy - help information\n");
		conn.writeDisln("copies a file to a new destination\n");
		conn.writeDisln("accepts relative and absolute paths\n");
		conn.writeDisln("\nExamples:\n");
		conn.writeDisln("\tcp [source-file] [dest-file]\n");
		if (Terminal.os.contains("Windows")){
			//display windows help
			conn.writeDisln("\tcp cool-file.txt C:\\path\\to\\new-cool-file.txt\n");
		} else {
			//display linux help
			conn.writeDisln("\tcp cool-file.txt /path/to/new-cool-file.txt\n");
		}
	}//end of help method
}//end of CatCommand class

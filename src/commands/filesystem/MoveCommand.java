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

public class MoveCommand{
	//variables
	//constructors
	public MoveCommand(){
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
//		for (String arg : fixedArgs){
//			conn.writeDisln(arg);
//		}
		//check if args is equal to two args
		if(fixedArgs.length != 2){
			conn.writeDisln("\ntwo arguments were not provided\n");
			return;
		}
		//assign vars
		File file2mv;
		//check permissions and tilde on file to copy
		try {
			file2mv = argParser.readCheckAndFix(fixedArgs[0]);
		} catch (Exception e){
			conn.writeDisln(e.toString());
			return;
		}
		//check if destination file has tilde 
		File dest4mv = argParser.checkAndFixTilde(fixedArgs[1]);
		//check write permissions
		if(!dest4mv.canWrite()){
			conn.writeDisln("Can't write to provided file/dir.\nno write so good");
			return;
		}
		//need to check if destination is folder or file
		if(dest4mv.isDirectory()){
			//add the provided filename to destination
			dest4mv = new File(dest4mv.toString() + File.separator + file2mv.getName());
			//conn.writeDisln(dest4mv.toString());
		}
		//check if overwriting
		if(dest4mv.exists()){
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
			Files.move(file2mv.toPath(), dest4mv.toPath(), StandardCopyOption.REPLACE_EXISTING);
			conn.writeDisln("\nmoved file successfully\n");
		} catch (IOException ioe){
			conn.writeDisln("something went wrong with move:\n" + ioe.toString());
			return;
		}
	}//end of execute with args methods
	
	//help method
	public void help(Connection conn){
		//display help information for this command
		conn.writeDisln("\nmove - help information\n");
		conn.writeDisln("moves a file to a new destination\n");
		conn.writeDisln("accepts relative and absolute paths\n");
		conn.writeDisln("\nExamples:\n");
		conn.writeDisln("\tmv [source-file] [dest-file]\n");
		if (Terminal.os.contains("Windows")){
			//display windows help
			conn.writeDisln("\tmv cool-file.txt C:\\path\\to\\new-cool-file.txt\n");
		} else {
			//display linux help
			conn.writeDisln("\tmv cool-file.txt /path/to/new-cool-file.txt\n");
		}
	}//end of help method
}//end of CatCommand class

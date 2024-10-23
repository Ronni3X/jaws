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
import java.lang.SecurityException;

public class MkdirsCommand{
	//variables
	//constructors
	public MkdirsCommand(){
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
		String[] fixedArgs = new String[1];
		try {
			fixedArgs = argParser.fixQuotesInArgs(toks);
		} catch (Exception e){
			conn.writeDisln(e.toString());
			return;
		}
		//check if args is equal to two args
		if(fixedArgs.length != 1){
			conn.writeDisln("\none argument was not provided\n");
			return;
		}
		//attempt to create folder or folders
		try {
			if (argParser.checkAndFixTilde(fixedArgs[0]).mkdirs()){
				conn.writeDisln("\nsuccessfully created directory(s)\n");
			} else {
				conn.writeDisln("\nunable to create directory(s)\nprobs because of permissions\n");
			}
		} catch (SecurityException se){
			conn.writeDisln("not able to create folder(s): " + se.toString());
			return;
		}
	}//end of execute with args methods
	
	//help method
	public void help(Connection conn){
		//display help information for this command
		conn.writeDisln("\nmkdir - help information\n");
		conn.writeDisln("creates a new directory(ies)\n");
		conn.writeDisln("accepts relative and absolute paths\n");
		conn.writeDisln("\nExamples:\n");
		conn.writeDisln("\tmkdir [dir-to-create]\n");
		if (Terminal.os.contains("Windows")){
			//display windows help
			conn.writeDisln("\tmkdir C:\\path\\to\\dir\\or\\dirs\n");
		} else {
			//display linux help
			conn.writeDisln("\tmkdir /path/to/dir/or/dirs\n");
		}
	}//end of help method
}//end of CatCommand class

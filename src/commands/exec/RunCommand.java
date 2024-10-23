package commands.exec;

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
import java.util.ArrayList;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import static java.nio.file.FileVisitResult.*;
import java.nio.file.FileVisitOption.*;
import java.nio.file.PathMatcher;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.FileSystems;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class RunCommand{
	//variables
	//constructors
	public RunCommand(){
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
		String[] fixedArgs = new String[3];
		try {
			fixedArgs = argParser.fixQuotesInArgs(toks);
		} catch (Exception e){
			conn.writeDisln(e.toString());
			return;
		}
		//check if args is equal to two args
		if(fixedArgs.length < 1){
			conn.writeDisln("\nsomething wrong with your provided args\nmaybe you messed up some quotes\nreview the help\n");
			help(conn);
			return;
		}

		//build and run the provided args
		ProcessBuilder pb = new ProcessBuilder();

		//build command with provided args
		pb.command(fixedArgs);

		try {
			//run the process
			Process proc = pb.start();

			//get output of the process
			BufferedReader breader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

			//write lines to connection
			String line;
			while ((line = breader.readLine()) != null){
				conn.writeDisln(line);
			}

			//wait for process to exit
			int exitCode = proc.waitFor();
			conn.writeDisln("\nExited with error code: " + exitCode);
		} catch (IOException ioe){
			conn.writeDisln("\n" + ioe.getLocalizedMessage() + "\n");
		} catch (InterruptedException ie){
			conn.writeDisln("\n" + ie.getLocalizedMessage() + "\n");
		}
	}//end of execute with args methods
	
	//help method
	public void help(Connection conn){
		//display help information for this command
		conn.writeDisln("\nrun - help information\n");
		conn.writeDisln("runs provided strings with processbuilder\nexe/bin has to exist and be in path, unless an absolute path is provided");
		conn.writeDisln("\nExamples:\n");
		conn.writeDisln("\trun [process with args]\n");
		if (Terminal.os.contains("Windows")){
			//display windows help
			conn.writeDisln("\trun reg.exe save hklm\\sam C:\\sam\n");
		} else {
			//display linux help
			conn.writeDisln("\tcat /etc/passwd\n");
		}
	}//end of help method
}//end of CatCommand class

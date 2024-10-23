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
import java.nio.file.NoSuchFileException;
import java.nio.file.DirectoryNotEmptyException;

public class RemoveCommand{
	//variables
	//constructors
	public RemoveCommand(){
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
		if(fixedArgs.length != 1){
			conn.writeDisln("\none argument was not provided\n");
			return;
		}
//		//assign vars
//		File file2remove;
//		//check permissions and tilde on file to copy
//		try {
//			file2remove = argParser.readCheckAndFix(fixedArgs[0]);
//		} catch (Exception e){
//			conn.writeDisln(e.toString());
//			return;
//		}
		//attempt to remove file/dir
		try {
			//Files.delete(file2remove.toPath());
			Files.delete(argParser.checkAndFixTilde(fixedArgs[0]).toPath());
			conn.writeDisln("\nsuccessfully deleted file\n");
		} catch (NoSuchFileException x){
			conn.writeDisln("file doesn't exist: " + x.toString());
			return;
		} catch (DirectoryNotEmptyException x){
			conn.writeDisln("directory not empty: " + x.toString());
			return;
		} catch (IOException x){
			conn.writeDisln("don't have delete permissions: " + x.toString());
			return;
		}
	}//end of execute with args methods
	
	//help method
	public void help(Connection conn){
		//display help information for this command
		conn.writeDisln("\nremove - help information\n");
		conn.writeDisln("removes a file or empty directory\n");
		conn.writeDisln("accepts relative and absolute paths\n");
		conn.writeDisln("\nExamples:\n");
		conn.writeDisln("\trm [file-or-directory]\n");
		if (Terminal.os.contains("Windows")){
			//display windows help
			conn.writeDisln("\trm C:\\Temp\\cool-file.txt\n");
		} else {
			//display linux help
			conn.writeDisln("\trm /temp/cool-file.txt\n");
		}
	}//end of help method
}//end of CatCommand class

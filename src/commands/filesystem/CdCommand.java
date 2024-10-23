package commands.filesystem;

//changes the current directory
//imports
import terminal.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.filechooser.FileSystemView;
import java.io.IOException;
import remote.*;

public class CdCommand {
	//variables

	//constructors
	public CdCommand () {

	}//end of constructor

	//methods
	//test the relativize command with paths
	public void combineRelative(Path relPath){
		//get curPwd and turn into path
		Path curPwdPath = Terminal.help.getCurPwd().toPath();

		//try to resolve the path
		System.out.println(curPwdPath.resolve(relPath));
	}//end of combineRelative method
	//change directories
	public void changeDirs(Connection conn, File newDir){
		//check if directory exists
		if (!newDir.exists()){
			//System.out.println("\ndirectory does not exist\n");
			conn.writeDisln("\ndirectory does not exist\n");
			return;
		}

		//check if directory is a directory
		if (!newDir.isDirectory()){
			//System.out.println("\nprovided path does not end in a directory\n");
			conn.writeDisln("\nprovided path does not end in a directory\n");
			return;
		}

		//check if you have read privileges
		if (!newDir.canRead()){
			//System.out.println("\nPermission denied. Do not have read privileges.");
			//System.out.println("dont read so good\n");
			conn.writeDisln("\nPermission denied. Do not have read privileges.");
			conn.writeDisln("dont read so good\n");
			return;
		}
		//change directory to old directory
		//set interim storage of current pwd
		File newOld = Terminal.help.getCurPwd();

		//set current pwd to old pwd
		Terminal.help.setCurPwd(newDir);

		//set old pwd to interim storage
		Terminal.help.setOldPwd(newOld);

		//display the new directory
		//System.out.println("\nnew current directory is: " + Terminal.help.getCurPwd() + "\n");
		//System.out.println("");
		conn.writeDisln("");
	}//end of changeDirs
	//command line argument interpreter
	public void interpretArgs(Path args) {
		//interpret the arguments
		//check if provided input is an absolute directory
		//combineRelative(args.toPath());
		System.out.println("\nnormalize example:");
		System.out.println(args.normalize());
		System.out.println("\ntoRealPath() exampe:");
		try {
			System.out.println(args.toRealPath());
		} catch (IOException e){
			e.printStackTrace();
		}
	}//end of interpretArgs method

	//main execute method
	public void execute(Connection conn, String command) {
		//print out root directories
		//File curPwd = new File(Terminal.help.getCurPwd());
		
		//gives information about the drives
		try {
			FileSystemView fsv = Terminal.help.getFSV();
			for (File fl : Terminal.help.getRootDrives()) {
				//System.out.println(fl + "\t" + fsv.getSystemTypeDescription(fl));
				conn.writeDisln(fl + "\t" + fsv.getSystemTypeDescription(fl));
			}
		} catch (Exception e){
			System.out.println("can't get drives");
		}
		
		//default directory
		//System.out.println("\nDefault directory: " + Terminal.help.getDefaultDir());
		conn.writeDisln("\nDefault directory: " + Terminal.help.getDefaultDir());

		//home directory
		conn.writeDisln("Home directory: " + Terminal.help.getHomeDir());
		conn.writeDisln("\nList root directories: \n");
		conn.writeDisln("Drive\tDescription");
		conn.writeDisln("-----\t-----------");
		
		conn.writeDisln("");
	}//end of main execute method

	//execute method with arguments
	public void execute(Connection conn, String command, String args){
		//check for simple command arguments
		//if more complex send to interpreter
		if (args.length() == 0){
			execute(conn, "cd");
		} else if (args.equals("-h")){
			help(conn);
		} else if (args.equals("-")){
			changeDirs(conn, Terminal.help.getOldPwd());
		} else if (args.startsWith("~")) {
			changeDirs(conn, new File(Terminal.help.getHomeDir().toString() + args.substring(1)));
		} else {
			//using normalize
			changeDirs(conn, Terminal.help.getCurPwd().toPath().resolve(Paths.get(args)).normalize().toFile());
		}
	}

	//help method
	public void help(Connection conn) {
		//display help information for this command
		conn.writeDisln("\ncd - help information\n");
		conn.writeDisln("switches directories\n");
		conn.writeDisln("accepts relative and absolute paths\n");
		conn.writeDisln("Options:\n");
		conn.writeDisln("\t~\tis replaced with home directory");
		conn.writeDisln("\t-\tswitches to previous directory");
		conn.writeDisln("\t-h\tdisplays this help information");
		conn.writeDisln("\nExample:\n");
		conn.writeDisln("\tcd [relative or absolute directory]\n");
	}//end of help method
}//end of CdCommand class

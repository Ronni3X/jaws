package commands.filesystem;

//prints the contents of the current working directory
//imports
import terminal.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import remote.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

public class LsCommand {
	//variables
	public File curDir;
	//constructor
	public LsCommand () {
		curDir = Terminal.help.getCurPwd();
	}//end of constructor

	//methods
	//check file information and return a string
	public String checkFileInfo(File fl){
		//char array to edit
		char[] rwx = {' ', ' ', ' '};
		
		//check if you can read
		if (fl.canRead()){
			rwx[0] = 'r';
		}

		//check if you can write
		if (fl.canWrite()){
			rwx[1] = 'w';
		}

		//check if you can execute
		if (fl.canExecute()){
			rwx[2] = 'x';
		}

		//return the string
		return new String(rwx);
	}//end of checkFileInfo method
	//main execute method
	public void execute(Connection conn, String command) {
		//display directory listing of current directory
		getDirListing(conn, curDir);
		//System.out.println("\nDirectory listing for: \n\n" + curDir.toString() + "\n");
		//System.out.println("");
	}//argument-less execution
	
	//overloaded execute
	public void execute(Connection conn, String command, String args) {
		//execute ls with arguments
		//only supports -h currently
		if (args.equals("-h")) {
			help(conn);
		} else {
			//interpret other command line arguments
			File toLs = Terminal.help.getCurPwd().toPath().resolve(Paths.get(args)).normalize().toFile();
			//check if exists
			if(!toLs.exists()){
				conn.writeDisln("\nfolder doesn't exist\n");
				return;
			}
			//check if directory is directory
			if (!toLs.isDirectory()){
				conn.writeDisln("\npath does not end in a directory\n");
				return;
			}
			//check if you have read privileges
			if (!toLs.canRead()){
				conn.writeDisln("\npermission denied. Do not have read privileges.");
				conn.writeDisln("dont read to good\n");
				return;
			}
			//ls provided directory
			getDirListing(conn, toLs);
		}//end of if/else help check
	}//end of overloaded execute
	
	//output file listing
	public void getDirListing(Connection conn, File path){
		conn.writeDisln("\n" + path + "\n");
		//output permissions for current directory
		conn.writeDisln("d " + checkFileInfo(path) + " " + String.format("%1$12s", path.length()) + " .");
		try {
			//output permissions for parent directory
			conn.writeDisln("d " + checkFileInfo(new File(path.getParent())) + " " + String.format("%1$12s", path.length()) + " ..");
		} catch (NullPointerException npe){
			//npe.printStackTrace();
			conn.writeDisln("d " + checkFileInfo(path) + " " + String.format("%1$12s", path.length()) + " ..");
		}
		ArrayList<File> files = new ArrayList<File>();
		ArrayList<File> dirs = new ArrayList<File>();
		for (File listing : path.listFiles()) {
			//if a directory print
			//else store in another array for printing
			if (listing.isDirectory()){
				//check file info
				//print directory
				//System.out.println("d " + checkFileInfo(listing) + " " + listing.getName());
				//conn.writeDisln("d " + checkFileInfo(listing) + " " + String.format("%1$12s", listing.length()) + " " + listing.getName());
				dirs.add(listing);
			} else {
				//add to files array
				files.add(listing);
			}//end of if/else directory check
			//System.out.println(listing.getName());
		}//end of for loop through directory listing
		
		//check if dirs arraylist has any entries
		if (!dirs.isEmpty()) {
			//sort dirs
			Collections.sort(dirs);
			//print the files
			for (File listing : dirs) {
				//System.out.println("- " + checkFileInfo(listing) + " " + listing.getName());
				conn.writeDisln("d " + checkFileInfo(listing) + " " + String.format("%1$12s", listing.length()) + " " + listing.getName());
			}
		}
		//check if arraylist has any entries
		if (!files.isEmpty()) {
			//sort files
			Collections.sort(files);
			//print the files
			for (File listing : files) {
				//System.out.println("- " + checkFileInfo(listing) + " " + listing.getName());
				conn.writeDisln("- " + checkFileInfo(listing) + " " + String.format("%1$12s", listing.length()) + " " + listing.getName());
			}
		}
		//System.out.println("\n");
		conn.writeDisln("\n");
	}

	//help method
	public void help(Connection conn) {
		//display help information for this command
		//System.out.println("\nls - help information\n");
		conn.writeDisln("\nls - help information\n");
		//System.out.println("only displays directory listing for current directory\n");
		conn.writeDisln("only displays directory listing for current directory\n");
		//System.out.println("\t-h\tdisplays this help information\n");
		conn.writeDisln("\t-h\tdisplays this help information\n");
	}//end of help method
}//end of LsCommand class

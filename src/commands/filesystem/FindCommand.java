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
import java.util.ArrayList;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import static java.nio.file.FileVisitResult.*;
import java.nio.file.FileVisitOption.*;
import java.nio.file.PathMatcher;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.FileSystems;

public class FindCommand{
	//variables
	//constructors
	public FindCommand(){
	}//end of CatCommand constructor

	public static class Finder extends SimpleFileVisitor<Path> {
		private final PathMatcher matcher;
		private int numMatches = 0;
		private ArrayList<String> finds = new ArrayList<String>();

		Finder(String pattern) {
			matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
		}

		//compares the glob pattern against the file or directory name
		void find(Path file) {
			Path name = file.getFileName();
			if (name != null && matcher.matches(name)){
				numMatches++;
				finds.add(name.toAbsolutePath().toString());
			}
		}

		//returns the total number of finds
		int getNumMatches(){
			return numMatches;
		}

		//returns the arraylist of finds
		ArrayList getFinds(){
			return finds;
		}

		// Invoke the pattern matching method on each file
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs){
			find(file);
			return CONTINUE;
		}

		// Invoke the pattern matching method on each directory
		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs){
			find(dir);
			return CONTINUE;
		}

		//visitFileFailed override
		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc){
			finds.add(exc.toString());
			return CONTINUE;
		}
	}//end of Finder class

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
//		for (String arg : fixedArgs){
//			conn.writeDisln(arg);
//		}
		//check if args is equal to two args
		if(fixedArgs.length != 3 || !fixedArgs[1].equals("-name")){
			conn.writeDisln("\nsomething wrong with your provided args\nmaybe you messed up some quotes\nreview the help\n");
			help(conn);
			return;
		}

		//initialize finder
		Finder finder = new Finder(fixedArgs[2]);
		//start walking file system
		try {
			Files.walkFileTree(argParser.checkAndFixTilde(fixedArgs[0]).toPath(), finder);
		} catch (IOException ioe){
			conn.writeDisln("ran into an error: " + ioe.toString());
		}
		//return results
		ArrayList finds = finder.getFinds();
		conn.writeDisln("");
		for (int i=0; i < finds.size(); i++){
			//write the found file
			conn.writeDisln(finds.get(i).toString());
		}

		//print number of finds
		conn.writeDisln("\nnumber of matches: " + finder.getNumMatches() + "\n");
	}//end of execute with args methods
	
	//help method
	public void help(Connection conn){
		//display help information for this command
		conn.writeDisln("\nfind - help information\n");
		conn.writeDisln("searches for files based on regex value from provided start point\ntraditional linux find");
		conn.writeDisln("accepts relative and absolute paths\n");
		conn.writeDisln("\nExamples:\n");
		conn.writeDisln("\tfind [path] -name [regex]\n");
		if (Terminal.os.contains("Windows")){
			//display windows help
			conn.writeDisln("\tfind C:\\path\\to\\dir -name pass.txt\n");
		} else {
			//display linux help
			conn.writeDisln("\tfind /path/to/dir -name pass.txt\n");
		}
	}//end of help method
}//end of CatCommand class

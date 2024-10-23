package commands.filesystem;

//prints the present working directory
//imports
import terminal.*;
import remote.*;

public class PwdCommand {
	//variables
	//constructor
	public PwdCommand () {

	}

	//methods
	public void execute(Connection conn, String command){
		//print the present working directory
		//System.out.println("old call: " + System.getProperty("user.dir"));

		//access static helper class
		//System.out.println("\n" + Terminal.help.getCurPwd() + "\n");
		conn.writeDisln("\n" + Terminal.help.getCurPwd() + "\n");
	}//end of execute method

	public void execute(Connection conn, String command, String args) {
		//check if -h is used
		if (args.equals("-h")) {
			help(conn);
		} else {
			//System.out.println("\narguments arent supported\nbut this can fail successfully");
			conn.writeDisln("\narguments arent supported\nbut this can fail successfully");
			execute(conn, command);
		}
	}//end of overloading execute method

	//help output
	public void help(Connection conn){
		//print out a help for pwd
		//System.out.println("\nprints the current working directory\n");
		//System.out.println("\t-h or anything\t\tprints this help menu\n");
		//System.out.println("I will hopefully have a zshell type display of your pwd\n");
		conn.writeDisln("\nprints the current working directory\n");
		conn.writeDisln("\t-h or anything\t\tprints this help menu\n");
		conn.writeDisln("I will hopefully have a zshell type display of your pwd\n");
	}//end of help method
}//end of PwdCommand class

//imports
import java.util.Scanner;
import terminal.Terminal;
import java.lang.reflect.InvocationTargetException;

class Jaws {
	//start of Jaws
	//start of main
	public static void main(String[] args){
		//do stuff
		//String ip = "172.16.0.1";
		String ip = "192.168.0.74";
		int port = 8443;
		//parse arguments for ip and port values
		if (args.length == 1){
			//only ip is provided
			//no input validation
			ip = args[0];
		} else if (args.length == 2){
			//ip and port provided
			//no input validation
			ip = args[0];
			port = Integer.parseInt(args[1]);
		}
		//start terminal
		Terminal term = new Terminal(ip,port);
		term.initCommands();
		try {
			term.startShell();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}//end of main
}//end of Jaws class

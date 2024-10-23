package commands.filesystem;

//view the contents of a file
//imports
import terminal.*;
import remote.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.OutputStream;
import java.io.InputStream;

public class ToNCCommand{
	//variables
	//constructors
	public ToNCCommand(){
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
		//declare file variable
		File toSend;
		String ip;
		int port;

		//convert args to vals
		int space = args.indexOf(' ');
		String addr = args.substring(0, space);
		int colon = addr.indexOf(':');
		ip = addr.substring(0,colon);
		port = Integer.parseInt(addr.substring(colon+1));
		String fileToSend = args.substring(space+1);
		toSend = Terminal.help.getCurPwd().toPath().resolve(Paths.get(fileToSend)).normalize().toFile();

		//check if file exists
		if(!toSend.exists()){
			conn.writeDisln("\nfile doesn't exist\n");
			return;
		}

		//check if file is a directory
		if(toSend.isDirectory()){
			conn.writeDisln("\nprovided file is a directory\n");
			return;
		}

		//check if permissions are valid
		if(!toSend.canRead()){
			conn.writeDisln("\nPermission denied. Do not have read privileges.");
			conn.writeDisln("don't read so good\n");
			return;
		}

		//byte array to send to netcat
		byte[] fileConts;
		try{
			//read in bytes of file and display them
			fileConts = Files.readAllBytes(toSend.toPath());
		} catch (IOException ioe){
			conn.writeDisln("\n" + ioe.getLocalizedMessage() + "\n");
			return;
		}

		//attempt to reuse Connection class
		Connection ncConn = new Connection(ip, port);
		//create the connection
		ncConn.connectOut();
		//send bytes to netcat connection
		ncConn.writeba(fileConts);
		//close the connection
		ncConn.closeAll();
	}//end of execute with args methods
	
	//help method
	public void help(Connection conn){
		//display help information for this command
		conn.writeDisln("\ntonc - help information\n");
		conn.writeDisln("sends the bytes of a file to a listening netcat service from the current directory by default\n");
		conn.writeDisln("accepts relative and absolute paths\n");
		conn.writeDisln("\nExamples:\n");
		conn.writeDisln("\ttonc [ip:port] [file]\n");
		if (Terminal.os.contains("Windows")){
			//display windows help
			conn.writeDisln("\ttonc [192.168.0.1:8081] [C:\\path\\to\\file]\n");
		} else {
			//display linux help
			conn.writeDisln("\ttonc [192.168.0.1:8081] [/path/to/file]\n");
		}
	}//end of help method
}//end of CatCommand class

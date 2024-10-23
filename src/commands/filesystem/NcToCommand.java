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
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;

public class NcToCommand{
	//variables
	//constructors
	public NcToCommand(){
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
		File toWrite;
		String ip;
		int port;

		//convert args to vals
		int space = args.indexOf(' ');
		String addr = args.substring(0, space);
		int colon = addr.indexOf(':');
		ip = addr.substring(0,colon);
		port = Integer.parseInt(addr.substring(colon+1));
		String fileToWrite = args.substring(space+1);
		toWrite = Terminal.help.getCurPwd().toPath().resolve(Paths.get(fileToWrite)).normalize().toFile();
		//this one works
		//conn.writeDisln("parent path:\n" + toWrite.getParent());
		File pathToWriteTo = new File(toWrite.getParent());

		//check if file is a directory
		if(toWrite.isDirectory()){
			conn.writeDisln("\nprovided file is a directory. try naming it something else\n");
			return;
		}

		//check if file exists
		if(toWrite.exists()){
			//check file permissions
			if(!toWrite.canWrite()){
				conn.writeDisln("\npermission denied. Do not have write privileges for provided file.i");
				conn.writeDisln("don't write so good\n");
				return;
			}
			//able to write file. continue
			conn.writeDisln("\nfile exists\noverwrite file? (y/N)");
			String input = new String();
			input = conn.readDisln();
			byte[] inBytes = input.getBytes();
//			conn.writeDisln("writing the received input as hex");
//			printBites(conn, inBytes);
//			conn.writeDisln("writing y as hex");
//			printBites(conn, "y".getBytes());
			if (input.length() == 0 || input.equals("") || input == null || input.equals("n") || input.equals("N")){
				//do not overwrite
				conn.writeDisln("\nsounds good. will not overwrite\n");
				return;
			} else if (input.equals("y") || input.equals("Y")){
				conn.writeDisln("\n[***] will overwrite file\n");
			} else {
				conn.writeDisln("\nnot sure whatcha put in there. but it wasn't y, Y, n, or N. Please try again\n");
				return;
			}
		} else {
			//file doesn't exist check folder permissions
			//check if permissions are valid
			if(!pathToWriteTo.canWrite()){
				conn.writeDisln("\nPermission denied. Do not have write privileges in provided directory.");
				conn.writeDisln("don't write so good\n");
				return;
			}
		}

		//byte array from netcat
		byte[] tbytes = new byte[4096];
		//attempt to reuse Connection class
		Connection ncConn = new Connection(ip, port);
		//create the connection
		ncConn.connectOut();
		//get socket input stream
		try (InputStream sis = ncConn.getIn()){
			//decare bytearrayoutputstream
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			//prepare write for bytes written
			int bitesWritten = 0;
			//read bytes from socket and write to bytearrayoutputstream
			for (int i; (i=sis.read(tbytes)) != -1; ){
				baos.write(tbytes, 0, i);
				bitesWritten += i;
				conn.writeDisln("read bytes: " + bitesWritten);
			}
			FileOutputStream fos = new FileOutputStream(toWrite);
			baos.writeTo(fos);
			baos.close();
			fos.close();
		} catch (IOException ioe){
			ioe.printStackTrace();
		} catch (NullPointerException npe){
			npe.printStackTrace();
		}
		//write byte array to file

		//close the connection
		ncConn.closeAll();
	}//end of execute with args methods
	
	//help method
	public void help(Connection conn){
		//display help information for this command
		conn.writeDisln("\nncto - help information\n");
		conn.writeDisln("downloads the bytes of a file from a remote netcat service to the provided path/file\n");
		conn.writeDisln("accepts relative and absolute paths\n");
		conn.writeDisln("\nExamples:\n");
		conn.writeDisln("\tncto [ip:port] [file]\n");
		if (Terminal.os.contains("Windows")){
			//display windows help
			conn.writeDisln("\tncto [192.168.0.1:8081] [C:\\path\\to\\file]\n");
		} else {
			//display linux help
			conn.writeDisln("\tncto [192.168.0.1:8081] [/path/to/file]\n");
		}
	}//end of help method
}//end of CatCommand class

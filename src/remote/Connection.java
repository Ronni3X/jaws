package remote;
//imports
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Connection{
	//variables
	String dom;
	String ip;
	int port;
	Socket sock;
	OutputStream sos;
	InputStream sis;
	BufferedReader bri;
	//constructors
	//default
	public Connection(){

	}
	//with ip and port
	public Connection(String inIp, int inPort){
		ip = inIp;
		port = inPort;
	}
	//methods
	//write to socket output stream
	public void writeDis(String line){
		try {
			sos.write(line.getBytes(Charset.forName("UTF-8")));
		} catch (IOException io){
			io.printStackTrace();
		}
	}
	//write to socket output stream
	//append new line at end
	public void writeDisln(String line){
		try {
			sos.write(line.getBytes(Charset.forName("UTF-8")));
			sos.write("\n".getBytes(Charset.forName("UTF-8")));
		} catch (IOException io){
			io.printStackTrace();
		}
	}
	//write byte to socket stream
	public void writeb(byte bite){
		try {
			sos.write(bite);
		} catch (IOException io){
			io.printStackTrace();
		}
	}//end of writeb method
	//write byte array to socket stream
	public void writeba(byte[] b){
		try {
			sos.write(b);
		} catch (IOException io){
			io.printStackTrace();
		}
	}//end of writeba method
	//read byte from socket stream
	//end of readb method
	//read line from socket output stream
	public String readDisln(){
		String inRet = new String();
		try {
			inRet = bri.readLine();
		} catch (IOException ioe){
			ioe.printStackTrace();
		}
		return inRet;
	}
	//not sure if I should send these out or just declare the streams
	//and push data to them
	//get socket output stream (write to this)
	public OutputStream getOut() throws IOException{
		return sock.getOutputStream();
	}
	//get socket input stream (read from this)
	public InputStream getIn() throws IOException{
		return sock.getInputStream();
	}
	//get already opened output stream (write to this)
	public OutputStream getDefaultOut() throws IOException{
		return sos;
	}
	//get already opened input stream (read from this)
	public InputStream getDefaultIn() throws IOException{
		return sis;
	}
	//create connection
	public void connectOut(){
		try{
			sock = new Socket(ip, port);
			sos = sock.getOutputStream();
			sis = sock.getInputStream();
			bri = new BufferedReader(new InputStreamReader(sis));
		} catch (UnknownHostException e){
			e.printStackTrace();
		} catch (IOException io){
			io.printStackTrace();
		}
	}//end of connectOut method
	//get the socket
	public Socket getSock(){
		return sock;
	}//end of getSock method
	//close all
	public void closeAll(){
		//check if output stream is null
		if (sos != null){
			try {
				sos.close();
			} catch (IOException ioe){
				ioe.printStackTrace();
			}
		}
		//check if input stream is null
		if (sis != null){
			try {
				sis.close();
			} catch (IOException ioe){
				ioe.printStackTrace();
			}
		}
		//close socket
		try {
			sock.close();
		} catch (IOException ioe){
			ioe.printStackTrace();
		}
	}
}//end of Connection class

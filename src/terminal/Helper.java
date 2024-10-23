package terminal;
//this class stores useful information for the terminal
//imports
import java.io.File;
import javax.swing.filechooser.FileSystemView;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Helper {
	//variables
	static File orgPath;
	static File oldPwd;
	static File curPwd;
	static String userName;
	static String hostName;
	static File[] rootDrives;
	static FileSystemView fsv;
	static File defaultDir;
	static File homeDir;
	//constructor
	public Helper() {
		userName = System.getProperty("user.name");
		//System.out.println("username = " + userName);
		//if (userName.toLowerCase().equals("system") || userName.toLowerCase().contains("$")){
		//System.out.println("user is not normal user");	
		//set original present working directory
		orgPath = new File(System.getProperty("user.dir"));
		//System.out.println("system.getproperty(user.dir) = " + orgPath);
		//set old present working directory
		oldPwd = new File(System.getProperty("user.dir"));
		//set current present working directory
		curPwd = new File(System.getProperty("user.dir"));
		try {
			//get drives
			rootDrives = File.listRoots();
			//System.out.println("file.listroots() = " + rootDrives);
			fsv = FileSystemView.getFileSystemView();
			//System.out.println("fsv = filesystemview.getfilesystemview() = " + fsv.toString());
			defaultDir = fsv.getDefaultDirectory();
			//System.out.println("fsv.getdefaultdirectory() = " + defaultDir.toString());
			//get home dir from file system view
			homeDir = fsv.getHomeDirectory();
			//System.out.println("fsv.gethomedirectory() = " + homeDir.toString());
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("root drives can't be displayed");
			defaultDir = orgPath;
			homeDir = orgPath;
		}
		//}
		try {
			//set hostname
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}//end of constructor

	//methods
	//get userName
	public String getUserName() {
		return userName;
	}///end of getUserName
	//get hostName
	public String getHostName() {
		return hostName;
	}//end of getHostName
	//get homeDir
	public File getHomeDir() {
		return homeDir;
	}//end of getHomeDir
	//get defaultDir
	public File getDefaultDir() {
		return defaultDir;
	}//end of getDefaultDir
	//get FileSystemView
	public FileSystemView getFSV() {
		return fsv;
	}//end of getFSV
	//get rootDrives
	public File[] getRootDrives(){
		return rootDrives;
	}//end of getRootDrives
	//get curPwd
	public File getCurPwd() {
		return curPwd;
	}//end of getCurPwd method

	//get oldPwd
	public File getOldPwd() {
		return oldPwd;
	}//end of getOldPwd method

	//set curPwd
	public void setCurPwd(File cur){
		curPwd = cur;
		//update prompt with curPwd
		Terminal.updatePrompt();
	}//end of set curPwd

	//set oldPwd
	public void setOldPwd(File old){
		oldPwd = old;
	}//end of set oldPwd
}//end of Helper class

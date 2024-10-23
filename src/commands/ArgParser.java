package commands;

import terminal.*;
import remote.*;
import java.util.Arrays;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

public class ArgParser{
	//variables
	//constructors
	public ArgParser(){
	}

	//methods
	//check if tilde is used
	public File fixTilde(String pathToFix){
		//replace tilde with home dir
		return new File(Terminal.help.getHomeDir().toString() + pathToFix.substring(1));
	}
	//check and fix tilde
	public File checkAndFixTilde(String pathToFix){
		if (pathToFix.startsWith("~")){
			return fixTilde(pathToFix);
		} else {
			return Terminal.help.getCurPwd().toPath().resolve(Paths.get(pathToFix)).normalize().toFile();
		}
	}
	//check read permissions on provided file
	public File readCheckAndFix(String path) throws Exception{
		//declare file to return
		File toRead;
		//check if path starts with tilde
		if (path.startsWith("~")){
			toRead = fixTilde(path);
		} else {
			toRead = Terminal.help.getCurPwd().toPath().resolve(Paths.get(path)).normalize().toFile();
		}
		//check if file exists
		if(!toRead.exists()){
			throw new Exception("provided file doesn't exist");
		}
		//check if file is a directory
		if(toRead.isDirectory()){
			throw new Exception("provided file is a directory");
		}
		//check if permissions are valid to read
		if(!toRead.canRead()){
			throw new Exception("Permission denied. Do not have read privileges. no read so good");
		}
		//return fixed file
		return toRead;
	}
	//check read permissions on provided file dir
	public File dirReadCheckAndFix(String path) throws Exception{
		//declare file to return
		File toRead;
		//check if path starts with tilde
		if (path.startsWith("~")){
			toRead = fixTilde(path);
		} else {
			toRead = Terminal.help.getCurPwd().toPath().resolve(Paths.get(path)).normalize().toFile();
		}
		//check if file exists
		if(!toRead.exists()){
			throw new Exception("provided file doesn't exist");
		}
		//check if file is a directory
		if(!toRead.isDirectory()){
			throw new Exception("provided file is not a directory");
		}
		//check if permissions are valid to read
		if(!toRead.canRead()){
			throw new Exception("Permission denied. Do not have read privileges. no read so good");
		}
		//return fixed file
		return toRead;
	}
	//parse array for quotes to combine args
	public String[] fixQuotesInArgs(String[] args) throws Exception{
		String[] tempArgs = new String[args.length];
		int first = -1;
		String toMatch = new String();
		int newArgCnt = 0;
		for (int i=0; i < args.length; i++){
		       //process args
		       if (first < 0){
			       //haven't found a token that starts with a quote
			       if (args[i].startsWith("'")){
				       //arg starts with single quote
				       first = i;
				       toMatch = "'";
			       } else if(args[i].startsWith("\"")){
				       //arg starts with double quote
				       first = i;
				       toMatch = "\"";
			       } else {
				       //add arg to tempArgs
				       tempArgs[newArgCnt] = args[i];
				       newArgCnt++;
				       continue;
			       }
			       if(args[i].endsWith(toMatch)){
				       //arg also ends with quote
				       tempArgs[newArgCnt] = args[i].replaceAll("^\\" + toMatch + "|\\" + toMatch + "$", "");
				       newArgCnt++;
				       first = -1;
			       }
		       //} else if (first >= 0){
		       } else {
			       //look for matching quote
			       if (args[i].endsWith(toMatch)){
				       //found closing quote
				       //get subset of array and combine into string for tempArgs
				       tempArgs[newArgCnt] = String.join(" ", Arrays.copyOfRange(args, first, i+1)).replaceAll("^\\" + toMatch + "|\\" + toMatch + "$", "");
				       first = -1;
				       newArgCnt++;
			       }
		       }
		}

		if (first > -1){
			//a closing quote was not found
			throw new Exception("closing single or double quote was not found in the provided arguements");
		}
		//prepare fixed args to return
		String[] fixedArgs = Arrays.copyOfRange(tempArgs, 0, newArgCnt);
		return fixedArgs;
	}
}

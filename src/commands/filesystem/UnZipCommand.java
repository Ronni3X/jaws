package commands.filesystem;

//changes the current directory
//imports
import terminal.*;
import commands.ArgParser;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.io.PrintWriter;
import remote.*;
import java.util.ArrayList;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.stream.Stream;

public class UnZipCommand {
	//variables
	public ArgParser argParser = new ArgParser();

	//constructors
	public UnZipCommand () {
	}//end of constructor

	//methods
	//unzip to provided directory
	//unzip to current directory
	public void unzip(Connection conn, String[] args) throws IOException, FileNotFoundException {
		//check write permissions on first file
		File zipFile = argParser.checkAndFixTilde(args[0]);
		//check if file exists
		if (!zipFile.exists()){
			//file already exists
			conn.writeDisln("\nfile file does not exist: " + zipFile.toString() + "\n");
			return;
		}
		//check write permissions on parent directory
		File zipFileParent = zipFile.getParentFile();
		if (!zipFileParent.canWrite()){
			//file is not writable
			conn.writeDisln("\ncannot write to this path: " + zipFileParent.toString() + "\n");
			return;
		}
		//prep variables
		byte[] buffer = new byte[1024];
		ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
		ZipEntry zipEntry = zis.getNextEntry();
		while (zipEntry != null) {
			//create a  new file to write from zip
			File newFile = new File(zipFileParent, zipEntry.getName());
			conn.writeDisln("Unzipping: " + newFile.toString());
			String destDirPath = zipFileParent.getCanonicalPath();
			String destFilePath = newFile.getCanonicalPath();
			//check if file is writing outside of target folder (Zip Slip)
			if (!destFilePath.startsWith(destDirPath + File.separator)) {
//				conn.writeDisln("Entry is outside of the target dir (Zip Slip): " + zipEntry.getName());
//				return;
				throw new IOException("Entry is outside of the target dir (Zip Slip): " + zipEntry.getName());
			}
			if (zipEntry.isDirectory()) {
				if (!newFile.isDirectory() && !newFile.mkdirs()){
					throw new IOException("Failed to create a directory " + newFile.toString());
				}
			} else {
				// fix for windows created archives
				File parent = newFile.getParentFile();
				if (!parent.isDirectory() && !parent.mkdirs()) {
					//return with error
//					conn.writeDisln("Failed to create directory: " + newFile.toString());
//					return;
					throw new IOException("Failed to create a directory " + parent.toString());
				}

				//write file content
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0){
					fos.write(buffer, 0, len);
				}
				fos.close();
			}
			zipEntry = zis.getNextEntry();
		}

		zis.closeEntry();
		zis.close();
	}
	//main execute method
	public void execute(Connection conn, String command) {
		conn.writeDisln("\ncommand needs arguments\n");
		help(conn);
	}//end of main execute method

	//execute method with arguments
	public void execute(Connection conn, String command, String args){
		//split into string array
		String[] toks = args.split(" ");
		try {
			String[] fixedArgs = argParser.fixQuotesInArgs(toks);
			//args parsed properly
			if (fixedArgs[0].equals("-h")){
				help(conn);
//			} else if (fixedArgs[0].equals("-o")){
//				//pass to recursive zip method
//				unzipToFolder(conn,fixedArgs);
			} else {
				//pass to file(s) zip
				unzip(conn,fixedArgs);
			}
		} catch (Exception e){
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			conn.writeDisln(errors.toString());
		}
	}

	//help method
	public void help(Connection conn) {
		//display help information for this command
		conn.writeDisln("\nunzip - help information\n");
		conn.writeDisln("decompresses zip file into file(s)/folder(s)\n");
		conn.writeDisln("accepts relative and absolute paths\n");
		conn.writeDisln("Options:\n");
		conn.writeDisln("\t~\tis replaced with home directory");
		conn.writeDisln("\t-h\tdisplays this help information");
		conn.writeDisln("\t-o\tchange output directory from current working directory");
		conn.writeDisln("\nExample:\n");
		conn.writeDisln("\tzip [path-to-file.zip]\n");
		conn.writeDisln("\tzip -o [path-to-unzip-to] [path-to-file.zip]\n");
		conn.writeDisln("\tzip /temp/zipped.zip\n");
		conn.writeDisln("\tzip -o /temp /home/user/zipped.zip\n");
	}//end of help method
}//end of UnZipCommand class

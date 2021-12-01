#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

source ./xs_global.sh
MAINCP=".:$subjectloc/ODDInstrumented:/home/user/libs/soot-trunk.jar:/home/user/DUA1.jar:/home/user/DistODD.jar"
starttime=`date +%s%N | cut -b1-13`
java -cp ${MAINCP} XSocketServer
stoptime=`date +%s%N | cut -b1-13`
echo "StaticAnalysisTime for ${ver}${seed} elapsed: " `expr $stoptime - $starttime` milliseconds

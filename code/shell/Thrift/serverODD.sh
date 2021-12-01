#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

source ./th_global.sh
INDIR=$subjectloc/ODDInstrumented/
MAINCP=".:$ROOT/DistODD.jar:$ROOT/banderaCommons.jar:$ROOT/banderaToolFramework.jar:$ROOT/commons-cli-1.3.1.jar:$ROOT/commons-io-1.4.jar:$ROOT/commons-lang-2.1.jar:$ROOT/commons-logging-1.2.jar:$ROOT/commons-pool-1.2.jar:$ROOT/trove-2.1.0.jar:$ROOT/xmlenc-0.52.jar:/home/user/DUA1.jar:$ROOT/jibx-run-1.1.3.jar:$ROOT/libs/soot-trunk.jar:$subjectloc/config:$subjectloc/lib/log4j.jar:$INDIR"

suffix="thrift"

OUTDIR=$subjectloc
mkdir -p $OUTDIR


echo $MAINCP
for i in $subjectloc/0110/lib/java/build/lib/*.jar;
do
	MAINCP=$MAINCP:$i
done
echo $MAINCP
starttime=`date +%s%N | cut -b1-13`
java -cp ${MAINCP} CalculatorServer 
stoptime=`date +%s%N | cut -b1-13`
echo "StaticAnalysisTime for ${ver}${seed} elapsed: " `expr $stoptime - $starttime` milliseconds 




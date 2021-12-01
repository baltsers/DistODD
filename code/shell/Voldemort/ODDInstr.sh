#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi
source ./vd_global.sh
ROOT=/home/xqfu
#MAINCP="$ROOT/DistEA/DUAForensics.jar:$ROOT/DistTaint3.jar:$ROOT/libs/soot-trunk.jar"
MAINCP=".:$ROOT/DistODD.jar:$ROOT/banderaCommons.jar:$ROOT/banderaToolFramework.jar:$ROOT/commons-cli-1.3.1.jar:$ROOT/commons-io-1.4.jar:$ROOT/commons-lang-2.1.jar:$ROOT/commons-logging-1.2.jar:$ROOT/commons-pool-1.2.jar:$ROOT/trove-2.1.0.jar:$ROOT/xmlenc-0.52.jar:/home/user/DUA1.jar:$ROOT/jibx-run-1.1.3.jar:$ROOT/libs/soot-trunk.jar:$ROOT/log4j.jar"
SOOTCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:$subjectloc/dist2/classes:$ROOT/DistODD.jar"

suffix="voldemort"

LOGDIR=out-ODDInstr
mkdir -p $LOGDIR
logout=$LOGDIR/instr-$suffix.out
logerr=$LOGDIR/instr-$suffix.err

OUTDIR=$subjectloc/ODDInstrumented
mkdir -p $OUTDIR

starttime=`date +%s%N | cut -b1-13`

	#-allowphantom \
   	#-duaverbose \
	#-wrapTryCatch \
	#-dumpJimple \
	#-statUncaught \
	#-perthread \
	#-syncnio \
	#-main-class $DRIVERCLASS \
	#-entry:$DRIVERCLASS \
	#-syncnio \
	#-syncnio \
	#-main-class $DRIVERCLASS \
	#-entry:$DRIVERCLASS \
	
java -Xmx40g -ea -cp ${MAINCP} ODD.ODDInst \
	-w -cp $SOOTCP -p cg verbose:false,implicit-entry:false \
	-p cg.spark verbose:false,on-fly-cg:true,rta:false -f c \
	-d $OUTDIR \
	-brinstr:off -duainstr:off \
	-allowphantom \
	-wrapTryCatch \
            -interCD \
            -interCD \
            -exInterCD \
        -duaverbose   \
        -serializeVTG \
		-dumpFunctionList \
	-slicectxinsens \
	-main-class $DRIVERCLASS \
	-entry:$DRIVERCLASS \
          -process-dir $subjectloc/dist2/classes \
#	 1> $logout 2> $logerr
	 
cp $subjectloc/ODDInstrumented/functionList.out .

stoptime=`date +%s%N | cut -b1-13`
echo "StaticAnalysisTime for $suffix elapsed: " `expr $stoptime - $starttime` milliseconds

echo "Running finished."
exit 0


# hcai vim :set ts=4 tw=4 tws=4


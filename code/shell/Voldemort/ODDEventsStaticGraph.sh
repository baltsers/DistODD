#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi
source ./vd_global.sh
ROOT=/home/xqfu
#MAINCP="$ROOT/DistEA/DUAForensics.jar:$ROOT/DistTaint3.jar:$ROOT/libs/soot-trunk.jar"
MAINCP="$ROOT/DUA1.jar:$ROOT/DistODD.jar:$ROOT/libs/soot-trunk.jar"
SOOTCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:$subjectloc/dist/classes:$subjectloc/dist/testclasses:$ROOT/DistODD.jar"

suffix="voldemort"

LOGDIR=out-ODDSG
mkdir -p $LOGDIR
logout=$LOGDIR/instrSG-$suffix.out
logerr=$LOGDIR/instrSG-$suffix.err

OUTDIR=$subjectloc/ODDEventsStaticGraph
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
	
java -Xmx100g -ea -cp ${MAINCP} ODD.ODDEventsStaticGraph \
	-w -cp $SOOTCP -p cg verbose:false,implicit-entry:false \
	-p cg.spark verbose:false,on-fly-cg:true,rta:false -f c \
	-d $OUTDIR \
	-brinstr:off -duainstr:off \
	-serializeVTG \
          -process-dir $subjectloc/dist/classes \
		  -process-dir $subjectloc/dist/testclasses \
	 1>/dev/null   2>/dev/null  

cp $subjectloc/ODDEventsStaticGraph/staticVtg*.dat $subjectloc/.
cp $subjectloc/ODDEventsStaticGraph/staticVtg*.dat $subjectloc/ODDInstrumented/.

stoptime=`date +%s%N | cut -b1-13`
echo "StaticAnalysisTime for $suffix elapsed: " `expr $stoptime - $starttime` milliseconds

echo "Running finished."
exit 0


# hcai vim :set ts=4 tw=4 tws=4


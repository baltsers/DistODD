#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

source ./zk_global.sh
ROOT=/home/user/
#DRIVERCLASS=ChatServer.core.MainServer
#subjectloc=$subjectloc/


#MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:/home/user/libs/soot-trunk8.jar:/home/user/libs/DUA3.jar:/home/user/libs/mcia8.jar:$$subjectloc/dist/classes"
# MAINCP=".:/home/user/libs/soot-trunk.jar:/home/user/DUA1.jar:/home/user/DistODD.jar"
MAINCP=".:$ROOT/DistODD.jar:$ROOT/banderaCommons.jar:$ROOT/banderaToolFramework.jar:$ROOT/commons-cli-1.3.1.jar:$ROOT/commons-io-1.4.jar:$ROOT/commons-lang-2.1.jar:$ROOT/commons-logging-1.2.jar:$ROOT/commons-pool-1.2.jar:$ROOT/trove-2.1.0.jar:$ROOT/xmlenc-0.52.jar:/home/user/DUA1.jar:$ROOT/jibx-run-1.1.3.jar:$ROOT/libs/soot-trunk.jar"
echo $MAINCP
# rm -R ODDEventsStaticGraph -f
# mkdir -p ODDEventStaticGraph


SOOTCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:$subjectloc/build/classes:$subjectloc/build/test/classes:$ROOT/DistODD.jar"


echo $SOOTCP
OUTDIR=$subjectloc/ODDEventsStaticGraph
rm -R $OUTDIR -f
mkdir -p $OUTDIR

starttime=`date +%s%N | cut -b1-13`
	#-sclinit \
	#-wrapTryCatch \
	#-debug \
	#-dumpJimple \
	#-statUncaught \
	#-ignoreRTECD \
	#-exInterCD \
	#-main-class ScheduleClass -entry:ScheduleClass \
	
java -Xms100g -Xmx100g -ea -cp ${MAINCP} ODD.ODDEventsStaticGraph \
	-w -cp ${SOOTCP} \
	-p cg verbose:false,implicit-entry:false -p cg.spark verbose:false,on-fly-cg:true,rta:false \
	-f c -d "$OUTDIR" -brinstr:off -duainstr:off \
   	-brinstr:off -duainstr:off  \
	-process-dir $subjectloc/build/classes \
	-process-dir $subjectloc/build/test/classes \
    -serializeVTG \
    1>/dev/null 2>/dev/null

stoptime=`date +%s%N | cut -b1-13`
echo "StaticAnalysisTime for ${ver}${seed} elapsed: " `expr $stoptime - $starttime` milliseconds

echo "Running finished."
exit 0


# hcai vim :set ts=4 tw=4 tws=4


#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

source ./xs_global.sh
#ROOT=/home/user/
#DRIVERCLASS=ChatServer.core.MainServer
subjectloc=$subjectloc


#MAINCP=".:/opt/jdk1.8.0_101/jre/lib/rt.jar:/home/user/libs/soot-trunk8.jar:/home/user/libs/DUA3.jar:/home/user/libs/mcia8.jar:$$subjectloc/bin"
MAINCP=".:/home/user/libs/soot-trunk.jar:/home/user/DUA1.jar:/home/user/DistODD.jar"
echo $MAINCP
rm -R out-SGInstr -f
mkdir -p out-SGInstr


SOOTCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:$subjectloc/java/bin:$subjectloc/bin:/home/user/DistODD.jar"


echo $SOOTCP
OUTDIR=$subjectloc/ODDEventsStaticGraph
rm -R $OUTDIR
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
java -Xmx100g -ea -cp ${MAINCP} ODD.ODDEventsStaticGraph \
	-w -cp ${SOOTCP} \
	-p cg verbose:false,implicit-entry:false -p cg.spark verbose:false,on-fly-cg:true,rta:false \
	-f c -d "$OUTDIR" -brinstr:off -duainstr:off \
   	-brinstr:off -duainstr:off  \
	-process-dir $subjectloc/java/bin \
	-process-dir $subjectloc/bin \
    -allowphantom \
	-serializeVTG \
	 1>out-SGInstr/instr.out 2>out-SGInstr/instr.err

cp $OUTDIR/staticVtg.dat $subjectloc/ODDInstrumented/. -f
cp $OUTDIR/staticVtg.dat $subjectloc/. -f
stoptime=`date +%s%N | cut -b1-13`
echo "StaticAnalysisTime elapsed: " `expr $stoptime - $starttime` milliseconds

echo "Running finished."
exit 0


# hcai vim :set ts=4 tw=4 tws=4


#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

source ./vd_global.sh

INDIR=$subjectloc/ODDInstrumented


MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:$ROOT/DUA1.jar:$ROOT/DistODD.jar:$ROOT/libs/soot-trunk.jar:$INDIR:$subjectloc/dist/voldemort-contrib-1.10.26.jar"

#for i in /home/user/libs/*.jar;
#do
#	MAINCP=$MAINCP:$i
#done
#for file in /home/user/voldemort/dist/*.jar;
#do
#  MAINCP=$MAINCP:$file
#done

for file in /home/user/voldemort/lib/*.jar;
do
  MAINCP=$MAINCP:$file
done

for file in /home/user/voldemort/contrib/*/libs/*.jar;
do
  MAINCP=$MAINCP:$file
done

MAINCP=$MAINCP:/home/user/voldemort/dist/resources

#for i in $subjectloc/svlib/*.jar;
#do
#	MAINCP=$MAINCP:$i
#done

suffix="vd"

OUTDIR=$subjectloc
#mkdir -p $OUTDIR

VDMAIN="jline.ConsoleRunner"

starttime=`date +%s%N | cut -b1-13`

	#-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=false \
	#$subjectloc/config/test_config1/config/
	#tcp://cse-rsws-06.cse.nd.edu:6667
	#"tcp://localhost:6666"
	#-server -Dcom.sun.management.jmxremote 
#java -Xmx2G -ea -DltsDebug=true -DuseToken=false -Dlog4j.configuration=file://$subjectloc/src/java/log4j.properties \

java -Xms100G -Xmx100G -ea -DltsDebug=false -DuseToken=false -Dlog4j.configuration=file://$subjectloc/src/java/log4j.client.properties \
	-cp ${MAINCP} \
	${VDMAIN} \
	voldemort.VoldemortClientShell \
	"test" \
	tcp://localhost:6666

stoptime=`date +%s%N | cut -b1-13`

echo "RunTime for $suffix elapsed: " `expr $stoptime - $starttime` milliseconds
exit 0


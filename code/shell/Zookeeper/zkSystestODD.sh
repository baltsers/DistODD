#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

source ./zk_global.sh
zkHostPort=${1:-"10.99.1.191:2181"}

INDIR=$subjectloc/ODDInstrumented
#INDIR=$subjectloc/build.sv/test/classes/:$subjectloc/build.sv/classes
#INDIR=$subjectloc/build/test/classes/:$subjectloc/build/classes

MAINCP=".:$ROOT/DUA1.jar:$ROOT/DistODD.jar:$ROOT/libs/soot-trunk.jar:$subjectloc/conf:$INDIR"
echo $MAINCP
#MAINCP="$ROOT/workspace/mcia/bin:$INDIR:$subjectloc/conf"

for i in /home/user/libs/*.jar;
do
	MAINCP=$MAINCP:$i
done
#for i in $subjectloc/svlib/*.jar;
#do
#	MAINCP=$MAINCP:$i
#done
# MAINCP=$MAINCP:/home/user/Zookeeper/distEAInstrumented
suffix="zk"

echo $MAINCP

OUTDIR=DToutdyn
mkdir -p $OUTDIR

#java -DsysTest.zkHostPort=cse-rsws-06.cse.nd.edu:2181 -jar build/contrib/fatjar/zookeeper-dev-fatjar-instr.jar systest org.apache.zookeeper.test.system.SimpleSysTest
MAINCLS="org.apache.zookeeper.test.system.BaseSysTest"

starttime=`date +%s%N | cut -b1-13`

	#-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=false \
#java -Xmx40g -ea -Dzookeeper.log.dir=. -Dzookeeper.root.logger=INFO,CONSOLE \
#java -Xmx40g -ea -DltsDebug=true -DsysTest.zkHostPort=cse-rsws-06.cse.nd.edu:2181 \

java -Xmx40g -ea -DltsDebug=true -DsysTest.zkHostPort="$zkHostPort" \
        -Djute.maxbuffer=50000000 \
	-cp ${MAINCP} \
	${MAINCLS}  \
        org.apache.zookeeper.test.system.SimpleSysTest 

stoptime=`date +%s%N | cut -b1-13`

echo "RunTime for $suffix elapsed: " `expr $stoptime - $starttime` milliseconds
exit 0

# hcai vim :set ts=4 tw=4 tws=4

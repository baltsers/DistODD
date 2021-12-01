#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

source ./vd_global.sh

INDIR=$subjectloc/ODDInstrumented
#INDIR=$subjectloc/dist/testclasses/:$subjectloc/dist/classes

#MAINCP="/etc/alternatives/java_sdk/jre/lib/rt.jar:$ROOT/tools/j2sdk1.4.2_18/lib/tools.jar:$ROOT/tools/polyglot-1.3.5/lib/polyglot.jar:$ROOT/tools/soot-2.3.0/lib/sootclasses-2.5.0.jar:$ROOT/tools/jasmin-2.3.0/lib/jasminclasses-2.3.0.jar:$ROOT/tools/java_cup.jar:$ROOT/workspace/DUAForensics/bin:$ROOT/workspace/LocalsBox/bin:$ROOT/workspace/InstrReporters/bin:$ROOT/workspace/mcia/bin:$INDIR:$subjectloc/conf"
MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:/home/user/DUA1.jar:/home/user/libs/soot-trunk.jar:$ROOT/DistODD.jar:$ROOT/banderaCommons.jar:$ROOT/banderaToolFramework.jar:$ROOT/commons-cli-1.3.1.jar:$ROOT/commons-io-1.4.jar:$ROOT/commons-logging-1.2.jar:$ROOT/commons-pool-1.2.jar:$ROOT/trove-2.1.0.jar:$ROOT/xmlenc-0.52.jar:$ROOT/jibx-run-1.1.3.jar:$INDIR:$subjectloc/conf:$subjectloc/dist/voldemort-contrib-1.10.26.jar"
for i in /home/user/libs/*.jar;
do
	MAINCP=$MAINCP:$i
done
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

suffix="vd"

MAINCLS="voldemort.TestClientShutdown"

starttime=`date +%s%N | cut -b1-13`


	#-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=false \
	#$subjectloc/config/two_node_cluster/node_1/config/cluster.xml \
#jdb -Xmx40g -DltsDebug=true \
#	-classpath ${MAINCP} \

java -Xmx40g -ea -DltsDebug=true \
	-cp ${MAINCP} \
	${MAINCLS} \
	"test" \
	"tcp://localhost:6666"

stoptime=`date +%s%N | cut -b1-13`

echo "RunTime for $suffix elapsed: " `expr $stoptime - $starttime` milliseconds
exit 0

# hcai vim :set ts=4 tw=4 tws=4

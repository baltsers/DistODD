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

#MAINCP="/etc/alternatives/java_sdk/jre/lib/rt.jar:$ROOT/tools/j2sdk1.4.2_18/lib/tools.jar:$ROOT/tools/polyglot-1.3.5/lib/polyglot.jar:$ROOT/tools/soot-2.3.0/lib/sootclasses-2.5.0.jar:$ROOT/tools/jasmin-2.3.0/lib/jasminclasses-2.3.0.jar:$ROOT/tools/java_cup.jar:$ROOT/workspace/DUAForensics/bin:$ROOT/workspace/LocalsBox/bin:$ROOT/workspace/InstrReporters/bin:$ROOT/workspace/mcia/bin:$INDIR:$subjectloc/conf"
MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:$ROOT/DUA1.jar:$ROOT/DistODD.jar:$ROOT/libs/soot-trunk.jar:$subjectloc/conf:$INDIR"
#MAINCP="$ROOT/workspace/mcia/bin:$INDIR:$subjectloc/conf"

for i in $subjectloc/lib/*.jar;
do
	MAINCP=$MAINCP:$i
done
#for i in $subjectloc/svlib/*.jar;
#do
#	MAINCP=$MAINCP:$i
#done

suffix="zk"

OUTDIR=ODDoutdyn
mkdir -p $OUTDIR

MAINCLS="org.apache.zookeeper.test.system.GenerateLoad"
 #[--leaderOnly] [--leaderServes] zookeeper_host:port containerPrefix
 #ofServers #ofClients requestSize


starttime=`date +%s%N | cut -b1-13`

	#-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=false \
#java -Xmx40g -ea -Dzookeeper.log.dir=. -Dzookeeper.root.logger=INFO,CONSOLE \
#java -Xmx40g -ea -DltsDebug=true -DsysTest.zkHostPort=cse-rsws-06.cse.nd.edu:2181 \
java -Xmx40g -ea -DltsDebug=false \
	-cp ${MAINCP} \
	${MAINCLS} \
	localhost:2181 \
	"/sysTest" \
	2 \
	2 \
	10

stoptime=`date +%s%N | cut -b1-13`

echo "RunTime for $suffix elapsed: " `expr $stoptime - $starttime` milliseconds
exit 0

# hcai vim :set ts=4 tw=4 tws=4

#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

source ./zk_global.sh

INDIR=$subjectloc/ODDInstrumented
#INDIR=$subjectloc/build.sv/classes/
#INDIR=$subjectloc/build/classes/
cp $INDIR/staticVtg.dat .

MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:$ROOT/libs/soot-trunk.jar:$ROOT/DUA1.jar:$ROOT/DistODD.jar:$INDIR"

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

OUTDIR=$subjectloc #DT2outdyn
#rm -R $OUTDIR -f
#mkdir -p $OUTDIR

#java -jar build/contrib/fatjar/zookeeper-dev-fatjar-instr.jar server conf/zoo.cfg

ZOOMAIN="org.apache.zookeeper.server.quorum.QuorumPeerMain"

starttime=`date +%s%N | cut -b1-13`

	#-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=false \
	
java -Xmx40g -ea -DltsDebug=true -Dzookeeper.log.dir=. -Dzookeeper.root.logger=INFO,CONSOLE \
	-cp ${MAINCP} \
	${ZOOMAIN} \
	$subjectloc/conf/zoo.cfg

stoptime=`date +%s%N | cut -b1-13`

echo "RunTime for $suffix elapsed: " `expr $stoptime - $starttime` milliseconds
exit 0

# hcai vim :set ts=4 tw=4 tws=4

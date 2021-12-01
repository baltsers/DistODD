#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

source ./chord_global.sh

#INDIR=$subjectloc/distEAInstrumented
#INDIR=$subjectloc/build.sv/classes/
#INDIR=$subjectloc/build/classes/
# INDIR=$subjectloc/build/classes
INDIR=$subjectloc/ODDInstrumented/

MAINCP=".:$ROOT/DistODD.jar:$ROOT/banderaCommons.jar:$ROOT/banderaToolFramework.jar:$ROOT/commons-cli-1.3.1.jar:$ROOT/commons-io-1.4.jar:$ROOT/commons-lang-2.1.jar:$ROOT/commons-logging-1.2.jar:$ROOT/commons-pool-1.2.jar:$ROOT/trove-2.1.0.jar:$ROOT/xmlenc-0.52.jar:/home/user/DUA1.jar:$ROOT/jibx-run-1.1.3.jar:$ROOT/libs/soot-trunk.jar:$subjectloc/config:$subjectloc/lib/log4j.jar:$INDIR"

suffix="chord"

OUTDIR=$subjectloc
mkdir -p $OUTDIR

MAINCLS="de.uniba.wiai.lspi.chord.console.Main"

starttime=`date +%s%N | cut -b1-13`

	#-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=false \
java -Xmx200g -ea -DltsDebug=true -DuseToken=true \
	-cp ${MAINCP} \
	${MAINCLS} \

stoptime=`date +%s%N | cut -b1-13`

echo "RunTime for $suffix elapsed: " `expr $stoptime - $starttime` milliseconds
exit 0

# hcai vim :set ts=4 tw=4 tws=4

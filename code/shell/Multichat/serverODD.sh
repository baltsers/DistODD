#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi


source ./mc_global.sh

INDIR=$subjectloc/ODDInstrumented/
cp $INDIR/staticVtg.dat .

MAINCP=".:$ROOT/DistODD.jar:$ROOT/banderaCommons.jar:$ROOT/banderaToolFramework.jar:$ROOT/commons-cli-1.3.1.jar:$ROOT/commons-io-1.4.jar:$ROOT/commons-lang-2.1.jar:$ROOT/commons-logging-1.2.jar:$ROOT/commons-pool-1.2.jar:$ROOT/trove-2.1.0.jar:$ROOT/xmlenc-0.52.jar:/home/user/DUA1.jar:$ROOT/jibx-run-1.1.3.jar:$ROOT/libs/soot-trunk.jar:$INDIR"

    #"queries.lst"
starttime=`date +%s%N | cut -b1-13`

java -Xms100g -Xmx100g -ea -DltsDebug=false -DuseToken=false \
	-cp ${MAINCP} \
	ChatServer.core.MainServer \


stoptime=`date +%s%N | cut -b1-13`
echo "RunTime for ${ver}${seed} elapsed: " `expr $stoptime - $starttime` milliseconds

echo "Running finished."

exit 0


# hcai vim :set ts=4 tw=4 tws=4


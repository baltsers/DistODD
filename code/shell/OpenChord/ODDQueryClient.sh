#!/bin/bash


source ./chord_global.sh

INDIR=$subjectloc/ODDInstrumented/
cp $INDIR/functionList.out  .
MAINCP=".:$ROOT/DistODD.jar"
rm -R allResult.txt -f
    #"queries.lst"
starttime=`date +%s%N | cut -b1-13`
java -Xms100g -Xmx100g -ea -cp ${MAINCP} \
	ODD.ODDQueryClient \
	localhost \
 2000, \
	
stoptime=`date +%s%N | cut -b1-13`
echo "RunTime for ${ver}${seed} elapsed: " `expr $stoptime - $starttime` milliseconds

echo "Running finished."

exit 0


# hcai vim :set ts=4 tw=4 tws=4


#!/bin/bash

## VERSION=v0
#SEEDS="1 2 3 4 5 6 7"
ROOT=/home/user

DRIVERCLASS=org.apache.zookeeper.server.quorum.QuorumPeerMain
#DRIVERCLASS=org.apache.zookeeper.test.AllTestsSelect
#RUNALLCLASS=org.apache.zookeeper.test.AllTestsRun
#RUNPERCLASS=org.apache.zookeeper.test.TestSelector
subjectloc=$ROOT/z349
#C=(22034) # after insert the __link invocations


JAVA=/usr/lib/jvm/java-8-openjdk-amd64

JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
# CLASSPATH=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:/usr/lib/jvm/java-8-openjdk-amd64/lib/tools.jar:$R
#OOT/libs/DUA1.jar:$ROOT/libs/DistTaint.jar::$ROOT/libs/soot-trunk.jar::$subjectloc/bin"



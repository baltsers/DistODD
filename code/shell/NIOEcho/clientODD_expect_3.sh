#!/usr/bin/expect
set timeout 60
spawn ./clientODD.sh
expect "connecting to"
sleep 1
for {set i 0} {$i<9} {incr i 0}  {
    set sentence [exec sh -c {./RandomSentence.sh}]
    puts "sentence: $sentence"
    send "$sentence \r"

    set seconds [exec sh -c {./RANDOMNUM.sh}]
    puts "seconds: $seconds"
    expect "received data from "
    sleep $seconds
}
expect eof

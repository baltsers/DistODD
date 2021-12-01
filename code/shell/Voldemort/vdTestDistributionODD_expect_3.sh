#!/usr/bin/expect
set timeout 60
for {set i 0} {$i<9999} {incr i 0}  {
		spawn ./vdTestDistributionODD.sh
	
    set seconds [exec sh -c {./RANDOMNUM.sh}]
    puts "seconds: $seconds"
    sleep $seconds
}
expect eof

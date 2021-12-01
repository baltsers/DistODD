#!/usr/bin/expect
set timeout 60
spawn ./zkClientODD.sh
#expect "(CONNECTED) 0]"
sleep 11
send "create -e /zk-temp 123 \r"
#expect "(CONNECTED) 1]"
sleep 11
send "create -e /zk-temp2 456 \r"
#expect "(CONNECTED) 2]"
sleep 11
for {set i 0} {$i<9999} {incr i 0}  {

	send "ls /zk-temp \r"
	#expect "(CONNECTED)"
	sleep 11
	send "ls /zk-temp2 \r"	
	
	#expect "(CONNECTED)"
	sleep 11
	send "get /zk-temp \r"
	#expect "(CONNECTED)"
	sleep 11
	send "get /zk-temp2 \r"	
	
	#expect "(CONNECTED)"
	sleep 11
	send "set /zk-temp 789 \r"
	#expect "(CONNECTED)"
	sleep 11
	send "set /zk-temp2 901 \r"	
	
    set seconds [exec sh -c {./RANDOMNUM.sh}]
    #expect "(CONNECTED)"
    sleep $seconds
}
expect eof

send "delete /zk-temp \r"
#expect "(CONNECTED)"
sleep 11
send "delete /zk-temp \r"		
#expect "(CONNECTED)"
sleep 11
send "close \r"
#expect "(CONNECTED)"
sleep 11
send "quit \r"	

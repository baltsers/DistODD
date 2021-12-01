# DistODD: Self-Adaptive Dynamic Dependency Analysis for Distributed Systems
-----------
Dynamic dependence analysis underlies various software testing and validation techniques, 
yet traditional approaches have limited utilities for real-world distributed systems 
due to challenges in scalability and balancing precision and cost, especially under user budget constraints. 
To overcome these challenges, we present DistODD, a novel, self-adaptive dynamic dependence analysis 
for continuously running distributed systems. 
In DistODD, we have developed a control-theoretical method to predict optimal analysis configurations on demand. 
Exploiting the prior human knowledge on the precision of different configurations, 
DistODD learns subject-specific model parameters from subject execution characteristics. 
We evaluated DistODD against eight continuously running distributed systems in the real world 
and compared it with a state-of-the-art baseline, to demonstrate its superiority.

The complete artifact package of DistODD has been made available at https://figshare.com/s/328930f0c7d50dfff228,
including the code, experimental scripts, and datasets. 
The operations and test inputs of our subjects are documented in the file "Inputs.txt".	
										
-----------
### Install DistODD
-----------
      
- Download DistODD_Meterial zip file from https://figshare.com/s/328930f0c7d50dfff228

- Unzip the file.

- Copy all library files from the directory ”tool” of DistODD to a directory (e.g., "lib") defined by the user.


-----------
### Download and install subjects
-----------

- MultiChat https://code.google.com/p/multithread-chat-server/

- NIOEcho   http://rox-xmlrpc.sourceforge.net/niotut/index.html#The code

- OpenChord https://sourceforge.net/projects/open-chord/files/Open%20Chord%201.0/

- Thrift	http://archive.apache.org/dist/thrift/

- xSocket	https://mvnrepository.com/artifact/org.xsocket/xSocket

- Voldemort https://github.com/apache/zookeeper/releases

- ZooKeeper https://github.com/apache/zookeeper/releases

- Netty	  https://bintray.com/netty/downloads/netty/


-----------
### Compute dependencies
-----------

#### 1. Select one subject.

- NioEcho is a simple system whose server echoes all messages from the clients. 

- MultiChat is a chat program whose clients broadcast their messages to all other clients through the server. 

- OpenChord provides peer-to-peer network services using distributed hash tables. 

- Thrift is an application development framework with a code generation engine for developing scalable cross-language services. 

- xSocket is a framework based on non-blocking IO (NIO), for constructing high-performance, scalable software systems. 

- Voldemort is a distributed key-value storage system used by LinkedIn.

- ZooKeeper is a coordination system providing distributed synchronization and group services. 

- Netty is an asynchronous NIO framework used to rapidly develop server/client network applications. 
			 
#### 2. Use DistODD to compute dependencies.
      
- 2.1  Step 1 (Phase 1): Instrumentation:

  We execute code/shell/#subject#/ODDInstr.sh (e.g., code/shell/xSocket/ODDInstr.sh).  

- 2.2  Step 2 (Phase 2): Pre-training:				
			
  We use the existing testing data of the subject.
    For example, we copy the testing data files from the directory ”data/#subject#” (data/xSocket) of the package to the subject directory (e.g., xSocket) defined by the user.
	
  In particular, for library subjects(Thrift, xSocket, and Netty), we also compile corresponding applications developed by us.
    For example, we compile all java files in the directory ”data/#subject#/java” (data/xSocket/java).
			
- 2.3  Step 3 (Phases 3 and 4): Arbitration and Adjustment:		  
						
  First, we set milliseconds (e.g., 40000 for xSocket) for a user budget constraint in the file "budget.txt".
			
  Second, we start server and client instances of the instrumented program.
	For example, for a xSocket integration test, we separately execute "./serverODD.sh", "./clientODD.sh", and "./client2ODD.sh" to start a server and two clients of the instrumented program. 
	These two clients automatically send text messages to the server. 
	For continuously executing the xSocket integration test, we execute "./clientODDTimes.sh" and "./client2ODDTimes.sh" with parameters "(client execution) times" (e.g., 999).
			
  Finally, analysis configurations vary according to our control-based strategy.
		 
- 2.4  Step 4 (Phase 5): User Interaction:  

  First, we execute code/shell/#subject#/ODDQueryClient.sh (e.g., code/shell/xSocket/ODDQueryClient.sh) to start a querying client.

  Then, we input a dependence query (i.e., method name) such as <org.xsocket.connection.IoSocketDispatcher: void run()>.

  Eventually, we get corresponding dependencies as the outputs of DistODD.
	
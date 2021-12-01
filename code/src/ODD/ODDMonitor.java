package ODD;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

//import ODD.ODDMonitor.MyLoadStaticGraph;
//import ODD.ODDMonitor.MyCreateStaticGraph2;
//import ODD.ODDMonitor.MyLoadStaticGraph2;
//import ODD.ODDMonitor.MyProcessEvents2;
import QL.Qlearner;
import soot.SootClass;

class MySocketServer extends Thread {   
	//private static final Logger logger=Logger.getLogger(ODDMonitor.class); 	
    @Override
    public void run() {
    	int socketPort=getPortNum();
        try {
        	AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open();			
	        InetSocketAddress hostAddress = new InetSocketAddress("localhost", getPortNum());
	        serverChannel.bind(hostAddress);	         
	        System.out.println("NIO2Server channel bound to port: " + hostAddress.getPort());
       	    //logger.info("ODDMonitor NIO2Server channel bound to port: " + hostAddress.getPort());
            String query="";
            String impactSetStr="";
            while (true) {
            	String oldImpactSetStr="";
            	impactSetStr=ODDMonitor.allQuestResults;
            	if (!oldImpactSetStr.equals(impactSetStr) && impactSetStr.length()>1) {
                	ODDUtil.writeStringToFile(impactSetStr, "allQueryResult"+socketPort+".txt");
                	if (ODDMonitor.configurations.equals("111111")) {
                		ODDUtil.writeStringToFile(impactSetStr, "allQuery111111"+socketPort+".txt");
                	}
            		oldImpactSetStr=impactSetStr;
            	}
                Future acceptResult = serverChannel.accept();
                AsynchronousSocketChannel clientChannel = (AsynchronousSocketChannel) acceptResult.get();
                ByteBuffer buffer = ByteBuffer.allocate(1024*1024*1024);
                Future result = clientChannel.read(buffer);
                result.get();
                while (! result.isDone()) {
                    // do nothing
                } 
                buffer.flip();
                query = new String(buffer.array()).trim();
                System.out.println("Client say : " + query); 
                if (query!=null && query.length()>1)
                {	
                	 impactSetStr=ODDMonitor.getImpactSetStr(query,""+socketPort);
                	 System.out.println("impactSetStr = " + impactSetStr);
                	 //logger.info("ODDMonitor impactSetStr = " + impactSetStr);
          			buffer = ByteBuffer.wrap(impactSetStr.getBytes("UTF-8"));
         			clientChannel.write(buffer);
                }
                else if (query!=null && query.toUpperCase().equals("ALL"))  {
                	impactSetStr=ODDMonitor.allQuestResults;
               	    System.out.println("impactSetStr = " + impactSetStr);
               	 //logger.info("ODDMonitor impactSetStr = " + impactSetStr);
         			buffer = ByteBuffer.wrap(impactSetStr.getBytes("UTF-8"));
        			clientChannel.write(buffer);
                	ODDUtil.writeStringToFile(impactSetStr, "allQueryResult"+socketPort+".txt");
                	if (ODDMonitor.configurations.equals("111111")) {
                		ODDUtil.writeStringToFile(impactSetStr, "allQuery111111"+socketPort+".txt");
                	}
                	
                }
                buffer.clear();
                clientChannel.close();
            }
        }catch (Exception e) {
            System.out.println("Exception:" + e);
        }finally{
//          serverSocket.close();
        }
    }
	public static String getProcessID() {
		return ManagementFactory.getRuntimeMXBean().getName()+'\0';
	}
	
	public static int getPortNum() {
		int portNum=2000;
		String processStr=getProcessID();
		//System.out.println("getProcessID()="+processStr); 
		int processID=Integer.parseInt(processStr.split("@")[0]);
		if (portNum<=2000)
		{
			portNum=2000+processID%10;
		}
		else	
			portNum++;
		//System.out.println("getPortNum()="+portNum);
		return portNum;
//		return 2000+(int)(Math.random()*10+1);
	}
}
public class ODDMonitor {
	protected static long CN_LIMIT = 1*100;
	/* the global counter for time-stamping each method event */
	//protected static Integer g_counter = 0;	
	protected static long CN_LIMIT_QUEUE = 5*CN_LIMIT;
	/* the global counter for queueing time-stamping each method event */
	//protected static Integer g_counter_queue = 0;
	protected static long TIME_SPAN = 60000;
	protected static ODDImpactAllInOne icAgent = null;
	public static void setICAgent(ODDImpactAllInOne _agent) {icAgent = _agent;}	
	protected static Integer preMethod = null;	
	protected static int g_eventCnt = 0;	
	/* a flag ensuring the initialization and termination are both executed exactly once and they are paired*/
	protected static boolean bInitialized = false;
	private static boolean active = false;	
	private static boolean start = false;	
	/* buffering queue events */
	//protected static List<Integer> B_Queueing = new LinkedList<Integer>();			
	// a flag ensuring timeout of static graph create 
	static boolean isStaticCreateTimeOut = false;
	//static long budget=Long.MAX_VALUE/3;
	// timeOut time of static graph create 
	static long staticCreateTimeOutTime=Long.MAX_VALUE/3;            // 	
	// a flag ensuring timeout of dynamic processEvents  
	static boolean isDynamicTimeOut = false;
	// timeOut time of dynamic processEvents
	static long dynamicTimeOutTime=Long.MAX_VALUE/3;            //
	// a flag ensuring timeout of static graph load 
	static boolean isStaticLoadTimeOut = false;
	// timeOut time of static graph load 
	static long staticLoadTimeOutTime=Long.MAX_VALUE/3;            // 	
//	// a flag ensuring static statement coverage  
 	static boolean staticStatementCoverage = false;	
	// a flag ensuring dynamic statement coverage  
	static String timeFileName= "Times"+getProcessIDString()+".txt";
	static String timeCostFileName= "TimeCost"+getProcessIDString()+".txt";
	static String configurationFileName= "Configuration"+getProcessIDString()+".txt";
	static String mazeFileName= "Maze"+getProcessIDString()+".txt";
	static String allQueryResultFileName= "allQueryResult"+getProcessIDString()+".txt";
	static String firstEventFileName= "firstEvent"+getProcessIDString()+".txt";
	static String lastEventFileName= "lastevent"+getProcessIDString()+".txt";
	static String computationStatusFileName= "ComputationStatus"+getProcessIDString()+".txt";
	static String timeCostsFileName= "TimeCosts"+getProcessIDString()+".txt";
	static String distODD_QueueFileName= "DistODD_Queue"+getProcessIDString()+".txt";
	static String staticVtgFileName= "staticVtg"+getProcessIDString()+".dat";
	//static String firstAllFileName= "firstEvent"+getProcessIDString()+".txt";
	//static String lastAllFileName= "lastevent"+getProcessIDString()+".txt";
	//static String lastEventFileName= "firstEvent"+getProcessIDString()+".txt";
//	static String staticTimeFileName= "staticTimes"+getProcessIDString()+".txt";
//	static String staticConFigurationFileName= "staticConfiguration"+getProcessIDString()+".txt";//	
	/* buffering working events */
	protected static List<Integer> span_Queue = new LinkedList<Integer>();
	protected static List<Integer> old_span_Queue = new LinkedList<Integer>();
	protected static List<Integer> diff_Queue = new LinkedList<Integer>();
	protected static List<Integer> static_Queue = new LinkedList<Integer>();
	protected static List<Integer> dynamic_Queue = new LinkedList<Integer>();
	/* buffering all queue events */
	protected static List<Integer> All_Queue = new LinkedList<Integer>();	
	static String staticConfigurations="";
//	static String dynamicConfigurations="";
	static String configurations="";
	static boolean staticUpdated=true;
//	static ArrayList<Long> dynamicTimes=new ArrayList<Long>();
//	static ArrayList<Long> staticTimes=new ArrayList<Long>();
////	static boolean staticFlowSensity = true;	
////	static boolean staticContextSensity = false;	
	static boolean[] staticDynamicSettings=new boolean[6];
	static LinkedHashMap<Integer, Long> firstEventTimeStamps = new LinkedHashMap<Integer,Long>();
	static LinkedHashMap<Integer, Long> lastEventTimeStamps = new LinkedHashMap<Integer,Long>();
	static String firstEventsStr="";
	static String lastEventsStr="";
	static boolean isDynamicLongThanStatic=false;
	static Qlearner learner  = null;
	static long lastProcessTime=0;  //System.currentTimeMillis();
	
	static String optionStr="";
	static String DPPath="";
	static int global_eventCount = 0;	
	static HashSet<String> firstMethods= new HashSet<String>();
	static HashSet<String> lastMethods= new HashSet<String>();
	static String eventMethodCountFileName= "EventCount"+getProcessIDString()+".txt";
	static int receivedMsCount=0; 
	//static String receivedMsCountFileName= "ReceivedMsCount"+getProcessIDString()+".txt";
	
	/* two special events */
	public static final int PROGRAM_START = Integer.MIN_VALUE;
	public static final int PROGRAM_END = Integer.MAX_VALUE;
	
	/* debug flag: e.g. for dumping event sequence to human-readable format for debugging purposes, etc. */
	protected static boolean debugOut = false;
	protected static boolean usingToken = true; // use a token at the end of each clock message for identification and verification
	
	protected static boolean trackingSender = true; // send the identify of message sender with messages being sent
	
	public static void turnDebugOut(boolean b) { debugOut = b; }

	/* a flag ensuring the initialization and termination are both executed exactly once and they are paired*/
	//protected static boolean bInitialized = false;
	

	//public static String receivedMessages="";
	
	
	/* for DUAF/Soot to access this class */
	public static void __link() { }
	
	
	protected static SootClass clsBr;
	
	/* first message-receiving events */
	protected static HashMap<String, Integer> S = new HashMap<String, Integer>();
	
	/* last events */
	protected static HashMap<String, Integer> L = new HashMap<String, Integer>();
	// distEA events
	/* first events */
	protected static HashMap<String, Integer> F = new HashMap<String, Integer>();
	
	protected static Integer g_counter_distEA = 0;
	static boolean readThresholds=false;
	static long 	budget=1000;
	//private static final Logger logger=Logger.getLogger(ODDMonitor.class); 
	public static String allQuestResults="";
	public synchronized static void resetInternals() {
		preMethod = null;
		g_eventCnt = 0;
		start = false;
		//g_counter = 0;
		span_Queue.clear();
		//B_Queueing.clear();	
	}
	public synchronized static void limitQueues() {
		if (span_Queue.size()>2*CN_LIMIT_QUEUE)  {
			//System.out.println("limitQueues span_Queue.size()="+span_Queue.size());
			span_Queue=ODDUtil.returnEventsInNum(span_Queue, (int)CN_LIMIT_QUEUE);
			//span_Queue=ODDUtil.returnFirstLastEvents(span_Queue);
			//System.out.println("limitQueues After span_Queue.size()="+span_Queue.size());
		}	
		if (All_Queue.size()>2*CN_LIMIT_QUEUE)  {
			//System.out.println("limitQueues All_Queue.size()="+All_Queue.size());
			All_Queue=ODDUtil.returnEventsInNum(All_Queue, (int)CN_LIMIT_QUEUE);
			//System.out.println("limitQueues After All_Queue.size()="+All_Queue.size());
			//All_Queue=ODDUtil.returnFirstLastEvents(All_Queue);
		}	
	}
	
	public synchronized static void initialize() throws Exception{
		//System.out.println("Class14_05");
		final long startTime = System.currentTimeMillis();
		resetInternals();
		bInitialized = true;
		Thread queryServer = new MySocketServer(); 
		queryServer.start();  
		
		//ODDUtil.initialTimesFile("dynamicTimes.txt", 16 , " ");
		ODDUtil.createOrCopyFile("Configuration.txt","111111",configurationFileName);  //001000  110011  001001
		ODDUtil.createOrCopyFile("Times.txt","0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 ",timeFileName);

    	getConfigurations("Configuration.txt");
		getTimeoutsThresholds(); 	//getTimeouts(); 	
//		configurations=ODDUtil.readLastLine(configurationFileName);
//		staticConfigurations=configurations.substring(0, 2);
//		dynamicConfigurations=configurations.substring(2, 6);
    	ODDImpactAllInOne.initializeFunctionList();
    	
		System.out.println("**************ODDMonitor 16_03::initialize()  0th");		
		//	clsBr = Scene.v().getSootClass("profile.BranchReporter");
		//	clsBr.setApplicationClass();
		//System.out.println("dtUtil.getLineNum="+dtUtil.getLineNum("entitystmt.out.branch"));
		//covBranches=new int[dtUtil.getLineNum("entitystmt.out.branch")];
		
		g_lgclock.initClock(1);
		
		/** add hook to catch SIGKILL/SIGTERM */
		Runtime.getRuntime().addShutdownHook( new Thread()
        {
          public void run()
          {
            System.out.println( "Shutdown signal caught!" ) ;
        	//  System.out.println("**************dt2BranchMonitor::initialize() addShutdownHook run() 0th");	  
        	/** guarantee that the trace, if any collected, gets dumped */
        	if (debugOut) {
        		System.out.println("\nDumping method event trace of current process execution ...");
        	}
    		try {
    			bInitialized = true;
    			//System.out.println("**************dt2BranchMonitor::initialize() addShutdownHook run() 1th");	  
				ODDMonitor.terminate("Forced upon external termination");
				//System.out.println("**************dt2BranchMonitor::initialize() addShutdownHook run() 2th");	  
			} catch (Exception e) {
				e.printStackTrace();
			}
          }
        } ) ;
		
		String debugFlag = System.getProperty("ltsDebug");
		System.out.println("debugFlag="+debugFlag);
		if (null != debugFlag) {
			debugOut = debugFlag.equalsIgnoreCase("true");
		}

		
		//debugOut=true;
		System.out.println("debugOut="+debugOut);
		System.out.println("dt2BranchMonitor starts working ......");
		//ODDUtil.initialTimesFile("dynamicTimes.txt", 16 , " ");
		ODDUtil.createOrCopyFile("Configuration.txt","111111",configurationFileName);  //001000  110011  001001
		ODDUtil.createOrCopyFile("Times.txt","0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 ",timeFileName);

    	getConfigurations("Configuration.txt");
		getTimeoutsThresholds(); 	//getTimeouts(); 	
//		configurations=ODDUtil.readLastLine(configurationFileName);
//		staticConfigurations=configurations.substring(0, 2);
//		dynamicConfigurations=configurations.substring(2, 6);
    	ODDImpactAllInOne.initializeFunctionList();
		try {
			icAgent = new ODDImpactAllInOne();
			ODDMonitor.setICAgent(icAgent);
	   	    //icAgent.resetImpOPs();
			File file3 = new File("Option.txt");  
            if (file3.exists())             	
            { 
            	optionStr=ODDUtil.readToString("Option.txt").trim().replace("/n","").replace("  "," ").replace("   ", " ");  
            }  
            System.out.println("optionStr="+optionStr);
            if (optionStr.length()<1 || optionStr.toUpperCase().startsWith("Q")) {
	        	File file1 = new File("Maze.txt");  
	            if (!file1.exists())             	
	            { 
	            	learner  = new Qlearner();
	            }  
	            else {
	            	File file2 = new File("budget.txt");  
	                if (!file2.exists())             	
	                { 
	                	learner  = new Qlearner("Maze.txt");
	                }  
	                else {
	                	learner  = new Qlearner("Maze.txt","budget.txt");
	                }
	            }
	        	
				//learner.updateMAP(String mazeFile);
	            ODDUtil.createOrCopyFile("Maze.txt"," -99 -99 -99 -99 \n -99 -99 -99 -99 \n -99 -99 -99 -99 \n -99 -99 -99 -99  0, -99, -99, -99 \n 0, -99, -99, -99 \n -99, -99, -99, -99 \n -99, -99, -99, -99 \n  0, 0, 0, 0  \n -99, -99, -99, -99 \n  0, 0, 0, 0  \n -99, -99, -99, -99 \n  0, 0, 0, 0  \n  0, 0, 0, 0  \n  0, 0, 0, 0  \n  0, 0, 0, 0 ",mazeFileName);
				learner.gamma=0.9;
				learner.alpha=0.9;
				learner.epsilon=0.2;
            }
            else if (optionStr.toUpperCase().startsWith("D")) {            	
            	String[] optionStrs=optionStr.split(" ");
            	System.out.println("optionStrs="+optionStrs);
            	if (optionStrs.length>0)
            	   DPPath=optionStrs[optionStrs.length-1];
            	//ODDUtil.copyFile("Configuration.txt", DPPath+"/Configuration.txt");
                System.out.println("DPPath="+DPPath);
            	ODDUtil.copyInitialDPFiles(DPPath);
            	//ODDUtil.copyMidDPFiles(DPPath);
            }
			System.out.println("ODDMonitor initialization takes " + (System.currentTimeMillis() - startTime) + " ms");
			//logger.info("ODDMonitor initialization took " + (System.currentTimeMillis() - startTime) + " ms");
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		dtSocketInputStream.debugOut = debugOut;
		dtSocketInputStream.usingToken = usingToken;
		
		dtSocketInputStream.intercept = g_intercept;
		dtSocketOutputStream.intercept = g_intercept;
		S.put(getProcessID(), Integer.MAX_VALUE);
		
	}
	public static void getTimeouts(String configurationFile) throws Exception {
		long long0=ODDUtil.readTimeOutFromFile("StaticGraphCreate ", configurationFile);
		if (long0>0)
			staticCreateTimeOutTime=long0;
		long long1=ODDUtil.readTimeOutFromFile("StaticGraphLoad ", configurationFile);
		if (long1>0)
			staticLoadTimeOutTime=long1;
		//System.out.println("long1=" + long1);
		long long2=ODDUtil.readTimeOutFromFile("Dynamic ", configurationFile);
		if (long2>0)
			dynamicTimeOutTime=long2;
		//System.out.println("long2=" + long2);
		//System.out.println("staticLoadTimeOutTime=" + staticLoadTimeOutTime +" timeOutTimeDynamic=" + dynamicTimeOutTime+" staticLoadTimeOutTime=" + staticLoadTimeOutTime );
	}
	public static void getThresholds(String configurationFile) throws Exception {
		try
		{
			long long1=ODDUtil.readTimeOutFromFile("EventLimit ", configurationFile);
			if (long1>0)  {
				CN_LIMIT= long1;		
				CN_LIMIT_QUEUE = 5*long1;
			}	
			//System.out.println("long1=" + long1);
			long long2=ODDUtil.readTimeOutFromFile("TimeSpan ", configurationFile);
			if (long2>0)
				TIME_SPAN=long2;
			System.out.println("CN_LIMIT=" + CN_LIMIT + " CN_LIMIT_QUEUE=" + CN_LIMIT_QUEUE+ " TIME_SPAN=" + TIME_SPAN);
			readThresholds=true;
	    } catch (Exception e) { 
	    	readThresholds=false;
	    	
        }  
	}

	public static void getTimeoutsThresholds() throws Exception {
		getThresholds("timeoutsthresholds.txt"); 
		String budgetStr=ODDUtil.readToString("budget.txt").trim().replaceAll("[^\\d]", "");
		if (budgetStr.length()<1) 
			getTimeouts("timeoutsthresholds.txt"); 	
		//System.out.println("getTimeouts() budgetStr=" + budgetStr);
		try
		{			
			budget=Long.parseLong(budgetStr);											 
			if (staticDynamicSettings[2])  {
				if (isDynamicLongThanStatic)  {
					staticCreateTimeOutTime=(long)(budget*0.4);
					staticLoadTimeOutTime=(long)(budget*0.2);
					dynamicTimeOutTime=(long)(budget*0.4);					
				}
				else
				{	
					staticCreateTimeOutTime=(long)(budget*0.7);
					staticLoadTimeOutTime=(long)(budget*0.2);
					dynamicTimeOutTime=(long)(budget*0.1);
				}
			}
			else  {
				staticCreateTimeOutTime=0;
				staticLoadTimeOutTime=0;
				dynamicTimeOutTime=budget;
			}	
	    	//System.out.println("getTimeouts() timeStaticCreate=" + timeStaticCreate +" timeStaticLoad=" + timeStaticLoad+" timeDynamic=" + timeDynamic );
	    	if (budget<10 || staticCreateTimeOutTime<5 || staticLoadTimeOutTime<5 || dynamicTimeOutTime<5) {
	    		getTimeouts("timeoutsthresholds.txt");
	    	}
	    	
																						
																			
				
	    } catch (Exception e) {  
	    	getTimeouts("timeoutsthresholds.txt");	
        }  

    	System.out.println("getTimeouts() staticLoadTimeOutTime=" + staticLoadTimeOutTime +" staticCreateTimeOutTime=" + staticCreateTimeOutTime +" timeOutTimeDynamic=" + dynamicTimeOutTime);
    	//logger.info("ODDMonitor getTimeouts() staticLoadTimeOutTime=" + staticLoadTimeOutTime +" timeOutTimeDynamic=" + dynamicTimeOutTime+" staticCreateTimeOutTime=" + staticCreateTimeOutTime);
	}	
	
	static class MyProcessEvents implements Callable <Boolean>  {
		@Override
        public Boolean call() throws Exception {
//	public static void MyProcessEvents() { 
       	long startTime = System.currentTimeMillis();
    	System.out.println("start processing events in the buffer of " + span_Queue.size() + " events...... ");
        //logger.info("ODDMonitor start processing events in the buffer of " + span_Queue.size() + " events...... ");
    	//System.out.println("g_counter="+g_counter+" g_counter_queue="+g_counter_queue+" span_Queue.size()"+span_Queue.size()+" B_Queueing.size()"+B_Queueing.size());
		try {
			getTimeoutsThresholds(); 	 	
			getConfigurations(configurationFileName);
        	//System.out.println("g_counter="+g_counter+" g_counter_queue="+g_counter_queue+" span_Queue.size()"+span_Queue.size()+" B_Queueing.size()"+B_Queueing.size());
 	//[0] staticContextSensity  [1] staticFlowSensity  	[2] dynamicMethodInstanceLevel [3] dynamicStatementCoverage [4] staticGraph  [5]	dynamicMethodEvent	
            //staticDynamicSettings[2] staticGrap only for static graph existing
        	//[0]staticContextSensity  [1]staticFlowSensity  	[2]staticGraph     [3]dynamicMethodEvent	 [4]dynamicStatementCoverage  [5]dynamicMethodInstanceLevel	        	
        	//  Adaptation  
        	File file = new File("static.dat");  
            if (!file.exists()) {  
            	staticDynamicSettings[2]=false;
            }  
            
            System.out.println("staticDynamicSettings[5]="+staticDynamicSettings[5]+" before FirstLast span_Queue.size()=" + span_Queue.size());
            List<Integer> work_Queue = new LinkedList<Integer>();
            try {
            	System.out.println("Span_Queue.size()=" + span_Queue.size());
	            if (!staticDynamicSettings[5])                          //dynamicMethodInstanceLevel
	    		{
	            	work_Queue=ODDUtil.returnFirstLastEvents(span_Queue);
	            	System.out.println("After FirstLast span_Queue.size()=" + span_Queue.size());
	    		}
	            else
	            	work_Queue.addAll(span_Queue);
            } catch (Exception e) {

  		    }	    	
        	
        	if (staticDynamicSettings[4])                         //dynamicStatementCoverage
    			ODDImpactAllInOne.prunedByStmt("");
    		System.out.println("ODDMonitor MyProcessEvents after staticDynamicSettings[3]");
        	//logger.info("ODDMonitor MyProcessEvents after staticDynamicSettings[3]");
        	if (staticDynamicSettings[3])                         //dynamicMethodEvent
    		{	
            	for (int i=0; i<work_Queue.size(); i++) {
            		Integer _idx=work_Queue.get(i);                		
        			//System.out.println("i=" + i+" _idx=" + _idx);
        			//System.out.print(".");
        			Integer smidx = Math.abs(_idx);        			
        			if (null != preMethod && preMethod == smidx) {
        				continue;
        			}       			        			
        			if (!start) {
        				start = (ODDImpactAllInOne.getAllQueries()==null || ODDImpactAllInOne.getAllQueries().contains(smidx));
        				if (!start) {
        					continue;
        				}
        			}  
        			if (staticDynamicSettings[2])               //StaticGraph
        			{	// enter event
	        			if (_idx < 0) {
	        				// trivially each method, once executed, is treated as impacted by itself
	        				if (!icAgent.getAllImpactSets().containsKey(smidx)) {
	        					icAgent.add2ImpactSet(smidx, smidx);
	        				}
	        				icAgent.onMethodEntryEvent(smidx);
	        			}
	        			// return-into event
	        			else {
	        				icAgent.onMethodReturnedIntoEvent(smidx);
	        			}        			
	        			if (null != preMethod && preMethod != smidx) {
	        				// close some "open" source nodes
	        				icAgent.closeNodes(preMethod, smidx);
	        			}	        			
        			}
        			else
        			{        				
	        			if (_idx < 0) {
        				// trivially each method, once executed, is treated as impacted by itself
	        				if (!icAgent.getAllImpactSets().containsKey(smidx))
        					icAgent.add2ImpactSet(smidx, smidx);
        				}
        				for (int j=i+1; j<work_Queue.size(); j++) {	  
        					Integer valueJ=work_Queue.get(j);      	          		
                			//System.out.println("j=" + j+" valueJ=" + valueJ);
        					if (valueJ<0)
        					{
        						icAgent.add2ImpactSet(smidx, Math.abs(valueJ));
        					}
        					else
        						icAgent.add2ImpactSet(smidx, valueJ);   
        				}   // for
        				
        			}  //(dynamicStaticGraph)
        			preMethod = smidx;
        		}  //for 
    		}
        	else
        	{ 		  
        		if (staticDynamicSettings[2])                       //dynamicStaticGraph
    			{	
	            	for (int i=0; i<ODDImpactAllInOne.idx2method.size(); i++) {
	            		Integer _idx=i;  
	        			Integer smidx = Math.abs(_idx);        			
	        			if (null != preMethod && preMethod == smidx) {
	        				continue;
	        			}       			        			
	        			if (!start) {
	        				start = (ODDImpactAllInOne.getAllQueries()==null || ODDImpactAllInOne.getAllQueries().contains(smidx));
	        				if (!start) {
	        					continue;
	        				}
	        			} 
	    				icAgent.onMethodEntryEvent(smidx);			
	    				icAgent.onMethodReturnedIntoEvent(smidx);    				
	        			if (null != preMethod && preMethod != smidx) {
	        				// close some "open" source nodes
	        				icAgent.closeNodes(preMethod, smidx);
	        			}  // (null != preMethod && preMethod != smidx)
	        			preMethod = smidx;
	        		}   //for 
    			}   //if (dynamicStaticGraph)
        	}	
    		System.out.println("ODDMonitor MyProcessEvents after the computation");
        	//logger.info("ODDMonitor MyProcessEvents after the computation");
        	//String allQuestResults="";
    		if (staticDynamicSettings[2])  {    		        //dynamicStaticGraph
    			//icAgent.dumpAllImpactSets();
    			//System.out.println("ODDMonitor "+icAgent.getDumpAllImpactSetsSize());  
    			System.out.println("ODDMonitor icAgent.getDumpAllImpactSetsSize()");
    			//logger.info("ODDMonitor "+icAgent.getDumpAllImpactSetsSize());    
    			allQuestResults=icAgent.getDumpAllImpactSets();
    		}
    		else  {
    			//icAgent.dumpAllImpactSetsWithoutStatic();
    			//System.out.println("ODDMonitor "+icAgent.getDumpAllImpactSetsSizeWithoutStatic());      
    			System.out.println("ODDMonitor icAgent.getDumpAllImpactSetsSizeWithoutStatic()");
    			//logger.info("ODDMonitor "+icAgent.getDumpAllImpactSetsSizeWithoutStatic());        			  
    			allQuestResults=icAgent.getDumpAllImpactSetsWithoutStatic();
    		}
    		ODDUtil.writeStringToFile(allQuestResults, allQueryResultFileName);
    		if (configurations=="111111")
    			ODDUtil.writeStringToFile(allQuestResults, "allQuery111111"+getProcessIDString()+".txt");  			
    		//getPrecision(String result111, String result, ArrayList queries, String resultPath, String resultFile, String queriesFile)
    		
    		System.out.println();
    		old_span_Queue.clear();
    		old_span_Queue=ODDUtil.copyList(span_Queue);
    		span_Queue.clear();
    		if (staticDynamicSettings[2]) {
    			static_Queue.addAll(diff_Queue);  
    			dynamic_Queue.clear();
    		}
    		else
    		{
    			dynamic_Queue.addAll(diff_Queue);  
    			static_Queue.clear();    			
    		}
    		//firstEventTimeStamps.clear();
    		//lastEventTimeStamps.clear();
		  } catch (Exception e) {
			  e.printStackTrace();
			  System.out.println("MyProcessEvents is interrupted when calculating, will stop... Exception: "+e);
    			//logger.info("ODDMonitor MyProcessEvents is interrupted when calculating, will stop...");  
			 return false;
		  }	    		
		return true;
	}			
	}	

	public synchronized static void enter(String methodname){
//		if (0 == g_counter) {
//			//System.out.println("buffering events ......");
//		}
		//System.out.println("enter: "+methodname);
		if (active) return;
		active = true;
		//System.out.println("enter(String methodname): "+methodname);
		
		global_eventCount++;
		firstMethods.add(methodname);
		if (!readThresholds) {
			if (methodname.indexOf("chord.")>=0 || methodname.indexOf("thrift.")>=0 || methodname.indexOf("xsocket.")>=0 ) 
			{
				CN_LIMIT= 1000;			
				CN_LIMIT_QUEUE = 2*1000;
				TIME_SPAN = 900000;
			}
			else if (methodname.indexOf("zookeeper.")>=0 || methodname.indexOf("netty.")>=0) 
			{
				CN_LIMIT= 1000;			
				CN_LIMIT_QUEUE = 2*1000;
				TIME_SPAN = 900000;
			}		
			else if (methodname.indexOf("voldemort.")>=0) 
			{
				CN_LIMIT= 1000;			
				CN_LIMIT_QUEUE = 2*1000;
				TIME_SPAN = 900000;
			}	
			if (methodname.indexOf("thrift.")>=0)  {
				isDynamicLongThanStatic=true;
			}
		}	
		try {
			
			Integer smidx = ODDImpactAllInOne.getMethodIdx(methodname);
			//System.out.println("smidx: "+smidx);
			if (smidx==null) {
				return;
			}
			Integer minusSmidx=smidx*-1;
			if (span_Queue.size()< Integer.MAX_VALUE/2) 
				span_Queue.add(minusSmidx);
			//System.out.println("enter(smidx): "+smidx);
			if (!firstEventTimeStamps.containsKey(minusSmidx))  {
				//System.out.println("enter put: "+smidx);
				firstEventTimeStamps.put(minusSmidx,System.currentTimeMillis());
				//System.out.println("firstEventTimeStamps: "+firstEventTimeStamps);
			}	
//			g_counter ++;
//			g_counter_queue ++;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			active = false;
		}
		
		try {
			synchronized (g_counter_distEA) {
				Integer curTS = (Integer) F.get(methodname);
				if (null == curTS) {					
					curTS = 0;
					//System.out.println("\n enter_impl F.put(methodname, g_counter_distEA) methodname="+methodname+" g_counter_distEA="+g_counter_distEA);
					F.put(methodname, g_counter_distEA);
				}
				g_counter_distEA = g_lgclock.getLTS();
				//System.out.println("\n enter_impl L.put(methodname, g_counter_distEA) methodname="+methodname+" g_counter_distEA="+g_counter_distEA);
				L.put(methodname, g_counter_distEA);
				g_counter_distEA ++;
				g_lgclock.increment();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/* the callee could be either an actual method called or a trap */
	public synchronized static void returnInto(String methodname, String calleeName) throws IOException, InterruptedException, ExecutionException{
		//System.out.println("returnInto: "+methodname);
		if (active) return;
		active = true;
		global_eventCount++;
		lastMethods.add(methodname);
		//System.out.println("returnInto(String methodname, String calleeName): "+methodname+" "+calleeName);
		try {
			Integer smidx = ODDImpactAllInOne.getMethodIdx(methodname);
			//System.out.println("returnInto smidx, svtg: "+methodname+" "+smidx+" ODDImpactAllInOne.svtg.edgeSet().size()="+ODDImpactAllInOne.svtg.edgeSet().size()+" ODDImpactAllInOne.svtg.nodeSet().size()="+ODDImpactAllInOne.svtg.nodeSet().size());
			//if (smidx==null || !ODDImpactAllInOne.isMethodInSVTG(smidx)) {
			if (smidx==null) {	
				return;
			}		

			lastEventTimeStamps.put(smidx,System.currentTimeMillis());
			span_Queue.add(smidx);
			long timeSpan=System.currentTimeMillis()-lastProcessTime;
			//System.out.println("Before g_counter="+g_counter+" g_counter_queue="+g_counter_queue+" timeSpan "+timeSpan +" TIME_SPAN"+TIME_SPAN+" CN_LIMIT "+CN_LIMIT +" CN_LIMIT_QUEUE"+CN_LIMIT_QUEUE);
			boolean ifProcess=(timeSpan>TIME_SPAN) & (span_Queue.size() > CN_LIMIT);
			//boolean ifProcess=!(methodname.indexOf("voldemort.")>=0 || methodname.indexOf(".zookeeper.")>0 || methodname.indexOf(".netty.")>0) & (timeSpan>TIME_SPAN) & ((g_counter > CN_LIMIT && g_counter_queue > CN_LIMIT && B_Queueing.size()>=CN_LIMIT) || g_counter_queue > CN_LIMIT_QUEUE);
			//ifProcess= ifProcess || ((methodname.indexOf("voldemort.")>=0 || methodname.indexOf(".zookeeper.")>0 || methodname.indexOf(".netty.")>0) & timeSpan>TIME_SPAN &((g_counter > CN_LIMIT && g_counter_queue > CN_LIMIT && B_Queueing.size()>=CN_LIMIT) || g_counter_queue > CN_LIMIT_QUEUE));
			//System.out.println("Before g_counter="+g_counter+" g_counter_queue="+g_counter_queue+" CN_LIMIT "+CN_LIMIT +" CN_LIMIT_QUEUE"+CN_LIMIT_QUEUE);
			if (ifProcess) {
				//System.out.println("After g_counter="+g_counter+" g_counter_queue="+g_counter_queue+" span_Queue.size()"+span_Queue.size()+" B_Queueing.size()"+B_Queueing.size()+" timeSpan="+timeSpan+" lastProcessTime="+lastProcessTime);
				//logger.info("ODDMonitor After g_counter="+g_counter+" g_counter_queue="+g_counter_queue+" span_Queue.size()"+span_Queue.size()+" B_Queueing.size()"+B_Queueing.size());
				//System.out.println("ODDMonitor timeSpan="+timeSpan+" TIME_SPAN"+TIME_SPAN+" CN_LIMIT "+CN_LIMIT +" CN_LIMIT_QUEUE"+CN_LIMIT_QUEUE);
				if (ODDImpactAllInOne.idx2method.size()<1 || ODDImpactAllInOne.method2idx.size()<1 || ODDImpactAllInOne.idx2method.size()!=ODDImpactAllInOne.method2idx.size()) 
					ODDImpactAllInOne.initializeFunctionList();
				String computationStatus=ODDUtil.readToString(computationStatusFileName).replaceAll("[^a-zA-Z]", "").toLowerCase();
				if (!computationStatus.startsWith("start"))  {
					ODDUtil.writeStringToFile("start", computationStatusFileName);
					limitQueues();					
					processEvents((dynamicTimeOutTime+staticCreateTimeOutTime+staticLoadTimeOutTime));
					lastProcessTime=System.currentTimeMillis();
					timeSpan=0;
					ODDUtil.writeStringToFile("end", computationStatusFileName);
				}	
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			active = false;
		}		
		//System.out.println("returnInto methodname: " + methodname+" calleeName: " + calleeName);
        
		try {
			synchronized (g_counter_distEA) {
				Integer curTS = (Integer) L.get(methodname);
				if (null == curTS) {
					curTS = 0;
				}
				g_counter_distEA = g_lgclock.getLTS();
				
				L.put(methodname, g_counter_distEA);
				g_counter_distEA ++;
				g_lgclock.increment();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized static void terminate(String where) throws Exception {
		/** NOTE: we cannot call simply forward this call to super class even though we do the same thing as the parent, because
		 * we need take effect the overloaded SerializeEvents() here below 
		 */
		//Monitor.terminate(where);
//		if (bInitialized) {
//			bInitialized = false;
//		}
//		else {
//			return;
//		}
		//if (debugOut)
		System.out.println("terminate(String where)="+where);		
		int[] covArray = profile.BranchReporter.getBrCovArray();

        if (covArray !=null)  {
			System.out.println("covArray="+covArray);
			if (debugOut)
			{	
				System.out.println("covArray.length="+covArray.length);
				for (int i = 0; i < covArray.length; i++) {
					//System.out.print(covArray[i]+" ");
				}
				System.out.println();
			}
			profile.BranchReporter br=new profile.BranchReporter();
			br.report(covArray);
			br.writeReportMsg(covArray, "stmtCoverage1.out");
			br.writeReportMsg(covArray, System.getProperty("user.dir") + File.separator + "test1/branches"+System.currentTimeMillis()+".out");
		}
		if (bInitialized) {
			bInitialized = false;
		}
		else {
			return;
		}
	}
	public synchronized static String getImpactSetStr(String query) throws Exception {
		String resultStr="";
    	 ODDImpactAllInOne.setQuery(query);
    	 resultStr=firstEventsStr+lastEventsStr;
    	if (staticDynamicSettings[2]) 	 {
    		resultStr=resultStr+icAgent.getDumpAllImpactSetsNumber(); 
    	}	
    	else
    		resultStr=resultStr+icAgent.getDumpAllImpactSetsNumberWithoutStatic(); 
    	resultStr=resultStr+"\n"+icAgent.getDumpImpactSet(query);
    	 //System.out.println("impactSetStr=" + resultStr);
		return resultStr;
	}
	
	public synchronized static String getImpactSetStr(String query, String port) throws Exception {
		String resultStr="";
    	 ODDImpactAllInOne.setQuery(query);
  		// processEvent
     	final long startTime = System.currentTimeMillis();	
 		Integer smidx = ODDImpactAllInOne.getMethodIdx(query);
 		String computationStatus=ODDUtil.readToString(computationStatusFileName).replaceAll("[^a-zA-Z]", "").toLowerCase();
 		if (smidx!=null && !computationStatus.startsWith("start"))  {
			ODDUtil.writeStringToFile("start",computationStatusFileName);
			//ODDUtil.createOrCopyFile("Configuration.txt","111111",configurationFileName);    
	    	getConfigurations(configurationFileName);
			getTimeoutsThresholds(); 
			limitQueues();
			System.out.println("getImpactSetStr 7 span_Queue.size()="+span_Queue.size());
 			List<Integer> back_span_Queue = new LinkedList<Integer>();
 			back_span_Queue=ODDUtil.copyList(span_Queue);
			System.out.println("getImpactSetStr 8");
 			span_Queue=ODDUtil.returnEventsAfter(span_Queue,smidx);
 			if (span_Queue.size()<1)  {
 				 span_Queue=ODDUtil.copyList(back_span_Queue);
 			 }
 			getConfigurations(configurationFileName);
 			getTimeoutsThresholds(); 	
 			diff_Queue = ODDUtil.returnDiffEvents(old_span_Queue,span_Queue);
 			System.out.println("diff_Queue.size()="+diff_Queue.size()+" All_Queue.size()="+All_Queue.size()+" old_span_Queue..size()="+old_span_Queue.size()+" span_Queue.size()="+span_Queue.size());      
 			All_Queue.addAll(diff_Queue); 
 			isStaticCreateTimeOut=false;
 			//System.out.println("processEvents 0 staticDynamicSettings[2]="+staticDynamicSettings[2]);
 			if (staticDynamicSettings[2])  {
 	        	File file = new File("staticVtg.dat");  
 	            if (!file.exists())             	
 	            {  
 	            	if (All_Queue.size()>1) {
 	            		ODDUtil.writeUniqueMethodsFile(All_Queue, ODDImpactAllInOne.idx2method, distODD_QueueFileName);
 	            	}
 	            	else
 	            		ODDUtil.writeUniqueMethodsFile(diff_Queue, ODDImpactAllInOne.idx2method, distODD_QueueFileName);
 	            }
 	            else  {          	
 	            	if (diff_Queue.size()>1) {
 	            		ODDUtil.writeUniqueMethodsFile(diff_Queue, ODDImpactAllInOne.idx2method, distODD_QueueFileName);
 	            	}
 	            	else
 	            		ODDUtil.writeUniqueMethodsFile(All_Queue, ODDImpactAllInOne.idx2method, distODD_QueueFileName);
 	            }	
 	            //System.out.println("processEvents 1 staticDynamicSettings[2]="+staticDynamicSettings[2]);
 	            ODDUtil.writeStringToFile(staticVtgFileName, "DistODDEventsFile"+getProcessIDString()+".txt");
 	            createStaticGraph(staticCreateTimeOutTime);
 	            if (!isStaticCreateTimeOut)  {
 	            	LoadStaticGraph(staticLoadTimeOutTime);
 	            	if (isStaticLoadTimeOut)
 	            		staticDynamicSettings[2]=false; 
 	                //System.out.println("processEvents 3 staticDynamicSettings[2]="+staticDynamicSettings[2]);
 	            }
 	            else
 	            	staticDynamicSettings[2]=false;
 			}	
 	        //System.out.println("processEvents 4 staticDynamicSettings[2]="+staticDynamicSettings[2]);
 			long dynamicStartTime=System.currentTimeMillis();
 	//		if (!staticDynamicSettings[2]) 
 	//			timeOutTime= Long.MAX_VALUE/3;
 			//MyProcessEvents();
 	        MyProcessEvents task0 = new ODDMonitor.MyProcessEvents();
 	    	//logger.info("ODDMonitor ProcessEvents after staticUpdated");
 	    	ExecutorService executor = Executors.newCachedThreadPool();
 	    	//logger.info("ODDMonitor ProcessEvents after staticUpdated");
 	        Future<Boolean> future1=executor.submit(task0);
 	    	//logger.info("ODDMonitor ProcessEvents after staticUpdated");
 	
 	        try {
 	            if (future1.get(dynamicTimeOutTime, TimeUnit.MILLISECONDS)) {  
 	                System.out.println("ProcessEvents completes successfully");
 	    			//logger.info("ODDMonitor ProcessEvents completes successfully");   		
 	            }
 	        } catch (InterruptedException e) {
 	            System.out.println("ProcessEvents was interrupted during the sleeping");
 				//logger.info("ODDMonitor ProcessEvents was interrupted during the sleeping"); 
 	            executor.shutdownNow();
 	        } catch (ExecutionException e) {
 	            System.out.println("ProcessEvents has mistakes during getting the result");
 				//logger.info("ODDMonitor ProcessEvents has mistakes during getting the result");
 	            executor.shutdownNow();
 	        } catch (TimeoutException e) {
 	            System.out.println("ProcessEvents is timeoouts");
 				//logger.info("ODDMonitor ProcessEvents is timeoouts"); 
 	            future1.cancel(true);
 	             executor.shutdownNow();
 	             executor.shutdown();
 	        } finally {
 	            executor.shutdownNow();
 	        }  
 	        //System.out.println("processEvents 5 staticDynamicSettings[2]="+staticDynamicSettings[2]);
 	        System.out.println("The dynamic computation took " + (System.currentTimeMillis()-dynamicStartTime) + " ms");
 	        //System.out.println("ProcessEvents timeOutTime="+timeOutTime+" timeOutProcessEvents="+timeOutDynamic); 
 	        //System.out.println("ProcessEvents g_counter="+g_counter+" g_counter_queue="+g_counter_queue+" span_Queue.size()"+span_Queue.size()+" All_Queue.size()"+All_Queue.size());
 	        long resultL=System.currentTimeMillis()-startTime; 
 	        System.out.println("The dependence computation took " + resultL + " ms");
 	        ODDUtil.updateTimeFromConfigurationToFileForced(configurationFileName, timeFileName, resultL);        
 	        //ODDUtil.writeStringToFile(""+resultL, timeCostFileName);
 	        //System.out.println("ODDUtil.updateTimeFromConfigurationToFileForced(configurationFileName, timeFileName, resultL);");      
 			//logger.info("ODDMonitor updateTimeFromConfigurationToFileForced configurationFileName="+configurationFileName+" timeFileName="+timeFileName+" resultL"+resultL);
// 	        ODDUtil.writeStringToFileAppend(configurations+" Event computation time: "+ resultL + " ms\n", "TimeCosts.txt");
// 	        ODDUtil.writeStringToFileAppend(configurations+","+ resultL + "\n", timeCostsFileName);
 	        //ODDUtil.writeStringToFileAppend(configurations+" Event computation time: "+ resultL + " ms\n", "TimeCosts.txt");
 	        ODDUtil.writeStringToFile(""+ resultL, timeCostsFileName);
 	       String oldStaticConfigurations=ODDUtil.readToString(configurationFileName).substring(0,2);
 	     // With or without adaptation
 	       if (optionStr.toUpperCase().startsWith("Q")) 
 	        {
 	        	ODDQLUtil.saveRewardPenaltyfromFiles(configurations, mazeFileName, timeFileName, staticCreateTimeOutTime+staticLoadTimeOutTime, dynamicTimeOutTime, staticDynamicSettings[2], staticUpdated);
 	        	ODDController.setNextConfigurationInFile(learner, mazeFileName, configurationFileName,"budget.txt",resultL); 
 	        }
 	       else if (optionStr.toUpperCase().startsWith("D")) {
 	    	   ODDUtil.copyFile(configurationFileName, DPPath+"/Configuration.txt");
 	       }

 	        
 	        configurations=ODDUtil.readLastLine(configurationFileName);		
 	        staticConfigurations=configurations.substring(0, 2);
 			//dynamicConfigurations=configurations.substring(2, 6);
 			getConfigurations(configurationFileName);
 	        System.out.println("oldStaticConfigurations: " + oldStaticConfigurations + " newStaticConfigurations: " + staticConfigurations);
 	        //logger.info("ODDMonitor oldStaticConfigurations: " + oldStaticConfigurations + " newStaticConfigurations: " + staticConfigurations);
 	        if (oldStaticConfigurations.equals(staticConfigurations))  {        	
 	        	staticUpdated=false;
 	        }
 	        else
 	        {
 	        	staticUpdated=true;
 	        }					
 	       
 	    	 ODDImpactAllInOne.setQuery(query);
 			ODDUtil.writeStringToFile("end", computationStatusFileName);
 		}		
     	firstEventsStr=ODDUtil.getMapStr(firstEventTimeStamps);	
 		lastEventsStr=ODDUtil.getMapStr(lastEventTimeStamps);
    	 resultStr=ODDToolUtil.getOnlyQueryEventStr(ODDImpactAllInOne.method2idx, query, firstEventsStr)+lastEventsStr;
    	if (staticDynamicSettings[2]) 	 {
    		resultStr=resultStr+icAgent.getDumpAllImpactSetsNumber(); 
        	//resultStr=resultStr+"\n"+icAgent.getDumpAllImpactSets();
    	}	
    	else  {
    		resultStr=resultStr+icAgent.getDumpAllImpactSetsNumberWithoutStatic(); 
        	//resultStr=resultStr+"\n"+icAgent.getDumpAllImpactSetsWithoutStatic();
    	}	
    	resultStr=resultStr+"\n"+icAgent.getDumpImpactSet(query);
        //ODDUtil.writeStringToFileAppend(resultStr, "Message2Query"+ port+".txt");
    	 //System.out.println("impactSetStr=" + resultStr);
		return resultStr;
	}
    public synchronized static long processEvents(long timeOutTime) throws  Exception{
    	final long startTime = System.currentTimeMillis();
    	firstEventsStr=ODDUtil.getMapStr(firstEventTimeStamps);	
    	//System.out.println("firstEventsStr="+firstEventsStr);
		lastEventsStr=ODDUtil.getMapStr(lastEventTimeStamps);	
		diff_Queue = ODDUtil.returnDiffEvents(old_span_Queue,span_Queue);
		System.out.println("diff_Queue.size()="+diff_Queue.size()+" All_Queue.size()="+All_Queue.size()+" old_span_Queue..size()="+old_span_Queue.size()+" span_Queue.size()="+span_Queue.size());      
		All_Queue.addAll(diff_Queue); 
		isStaticCreateTimeOut=false;
		isStaticLoadTimeOut=false;
		//Map< Integer, String > idx2method = new LinkedHashMap<Integer, String>();
		System.out.println("Before ODDImpactAllInOne.size()="+ODDImpactAllInOne.idx2method.size());
		if (ODDImpactAllInOne.idx2method.size()<1) {
			ODDImpactAllInOne.initializeFunctionList();
			System.out.println("After ODDImpactAllInOne.size() ="+ODDImpactAllInOne.idx2method.size());
		}
	
		if (staticDynamicSettings[2])  {
        	File file = new File("staticVtg.dat");  
            if (!file.exists())             	
            {  
            	ODDUtil.writeMethodsFile(All_Queue, ODDImpactAllInOne.idx2method, "DistODD_Queue"+getProcessIDString()+".txt");
            }
            else            	
    			ODDUtil.writeMethodsFile(diff_Queue, ODDImpactAllInOne.idx2method, "DistODD_Queue"+getProcessIDString()+".txt");
            ODDUtil.writeStringToFile("staticVtg"+getProcessIDString()+".dat", "DistODDEventsFile"+getProcessIDString()+".txt");
            //System.out.println("processEvents 1 staticDynamicSettings[2]="+staticDynamicSettings[2]);
            createStaticGraph(timeOutTime);
            if (!isStaticCreateTimeOut)  {
            	LoadStaticGraph(timeOutTime);
            	if (isStaticLoadTimeOut)
            		staticDynamicSettings[2]=false; 
                //System.out.println("processEvents 3 staticDynamicSettings[2]="+staticDynamicSettings[2]);
            }
            else
            	staticDynamicSettings[2]=false;
		}	 
    	//MyProcessEvents();
        MyProcessEvents task0 = new ODDMonitor.MyProcessEvents();
    	//logger.info("ODDMonitor ProcessEvents after staticUpdated");
    	ExecutorService executor = Executors.newCachedThreadPool();
    	//logger.info("ODDMonitor ProcessEvents after staticUpdated");
        Future<Boolean> future1=executor.submit(task0);
    	//logger.info("ODDMonitor ProcessEvents after staticUpdated");

        try {
            if (future1.get(timeOutTime, TimeUnit.MILLISECONDS)) {  
                System.out.println("ProcessEvents completes successfully");
    			//logger.info("ODDMonitor ProcessEvents completes successfully");   		
            }
        } catch (InterruptedException e) {
            System.out.println("ProcessEvents was interrupted during the sleeping");
			//logger.info("ODDMonitor ProcessEvents was interrupted during the sleeping"); 
            executor.shutdownNow();
        } catch (ExecutionException e) {
            System.out.println("ProcessEvents has mistakes during getting the result");
			//logger.info("ODDMonitor ProcessEvents has mistakes during getting the result");
            executor.shutdownNow();
        } catch (TimeoutException e) {
            System.out.println("ProcessEvents is timeoouts");
			//logger.info("ODDMonitor ProcessEvents is timeoouts"); 
            future1.cancel(true);
             executor.shutdownNow();
             executor.shutdown();
        } finally {
            executor.shutdownNow();
        }   
        //System.out.println("ProcessEvents timeOutTime="+timeOutTime+" timeOutProcessEvents="+timeOutDynamic); 
        //System.out.println("ProcessEvents g_counter="+g_counter+" g_counter_queue="+g_counter_queue+" span_Queue.size()"+span_Queue.size()+" B_Queueing.size()"+B_Queueing.size());
        long resultL=System.currentTimeMillis()-startTime; 
        System.out.println("Event computation took " + resultL + " ms");
        
		//logger.info("ODDMonitor Event computation took " + resultL + " ms");
     
        //dynamicTimes=ODDUtil.updatedTimeFromConfigurationForced(dynamicConFigurationFileName, dynamicTimeFileName, resultL);
        ODDUtil.updateTimeFromConfigurationToFileForced(configurationFileName, timeFileName, resultL);       
        ODDUtil.writeStringToFile(""+resultL, timeCostFileName);
        ODDUtil.writeStringToFileAppend(""+resultL, "TimeCosts.txt");
        HashSet<String> coverMethods= new HashSet<String>();
        coverMethods.addAll(firstMethods);
        coverMethods.addAll(lastMethods);
        dtUtil.writeSet(coverMethods, "coveredMethods.txt");
        //public static float getAttackSurfaceFromFiles(HashSet<String> coveredMethods, String sourceSinkMethodFile, String portUntrustedDataFile)
        float attackSurface=ODDUtil.getAttackSurfaceFromFiles(coverMethods, "sourceSinkMethods.txt", "portUntrusted.txt");
        System.out.println("attackSurface= " + attackSurface);
        int[] covArray = profile.BranchReporter.getBrCovArray();
        if (covArray !=null)  {
	        System.out.println("covArray.length= " + covArray.length);
	        ODDUtil.writeStringToFile(""+global_eventCount+" "+coverMethods.size()+" "+attackSurface+" "+covArray.length, eventMethodCountFileName);
	        ODDUtil.writeStringToFile(""+global_eventCount+" "+coverMethods.size()+" "+attackSurface+" "+covArray.length, "EventCount.txt");	        
        }
        else {
        	ODDUtil.writeStringToFile(""+global_eventCount+" "+coverMethods.size()+" "+attackSurface+" 0", eventMethodCountFileName);
 	        ODDUtil.writeStringToFile(""+global_eventCount+" "+coverMethods.size()+" "+attackSurface+" 0", "EventCount.txt");
        }    
        
        System.out.println("After covArray.length  0");
        //ODDUtil.writeStringToFileAppend(""+receivedMsCount, receivedMsCountFileName);
        //System.out.println("ODDUtil.updateTimeFromConfigurationToFileForced(configurationFileName, timeFileName, resultL);");      
		//logger.info("ODDMonitor updateTimeFromConfigurationToFileForced configurationFileName="+configurationFileName+" timeFileName="+timeFileName+" resultL"+resultL);
        //ODDUtil.writeStringToFile(resultL + " ms\n", "TimeCosts.txt");
        ODDUtil.writeStringToFile(configurations+" Event computation time: "+ resultL + " ms\n", "TimeCosts.txt");
        ODDUtil.writeStringToFile(""+ resultL, timeCostsFileName);
        System.out.println("After covArray.length  1");
        System.out.println("optionStr.toUpperCase()= " + optionStr.toUpperCase());
        if (optionStr.toUpperCase().startsWith("D"))  {
//        	ODDUtil.copyFile("coveredMethods.txt", DPPath+"/coveredMethods.txt");
//        	ODDUtil.copyFile("stmtCoverage1.out", DPPath+"/stmtCoverage1.out");
            System.out.println("D DPPath= " + DPPath);
        	ODDUtil.copyMidDPFiles(DPPath);
        	ODDUtil.copyFile(eventMethodCountFileName, DPPath+"/EventCount.txt");
        	ODDUtil.copyFile(timeCostFileName, DPPath+"/"+timeCostFileName);
        	//ODDUtil.copyFile(receivedMsCountFileName, DPPath+"/ReceivedMsCount.txt");
        	
        	/*
        	//if (utilization>1.0)        		
        	Date date = new Date();
    		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		String dateS = " "+sdf.format(date);
        	double utilization=0.0;
        	if (budget>0)
        		utilization=(double)resultL/budget;
//    		if (utilization>1.0)
//    			utilization=1.0;
        	System.out.println("D resultL="+resultL+" budget="+ budget+" utilization= " + utilization);
        	double precision=0.0;
        	if (configurations=="111111") {
        		precision=1.0;
        	}
        	else {
        		//double getPrecision(String result111, String result, ArrayList queries, String resultPath, String resultFile, String queriesFile)
        		precision=ODDUtil.getPrecision();
        		if (precision>1.0)
        			precision=1.0;
        	}	
        	System.out.println("D precision= " + precision);
        		//double pre=getPrecision("", "", queries, "/Research/tp/", "allQueryResultOther.txt", ""); 
        	if (utilization>0.5 && precision>0.5)
        		ODDUtil.writeStringToFileAppend(""+System.currentTimeMillis()+dateS+" Utilization="+ utilization+" Precision="+ precision+ " \n", "DeepLearningLogs.txt");
        	*/
        }	

        System.out.println("After covArray.length  2");
        String oldStaticConfigurations=ODDUtil.readToString(configurationFileName).substring(0,2);
     // With or without adaptation
//    	File file1 = new File("budget.txt");  
//        if (file1.exists())   { 
//        	ODDUtil.saveRewardPenaltyfromFiles(mazeFileName, timeFileName, staticCreateTimeOutTime+staticLoadTimeOutTime, dynamicTimeOutTime, staticDynamicSettings[2], staticUpdated,"budget.txt");
//        	ODDController.setNextConfigurationInFile(learner, mazeFileName, configurationFileName,"budget.txt"); 
//        }else 
        if (optionStr.toUpperCase().startsWith("Q") || optionStr.toUpperCase().startsWith("C")) {
        	ODDQLUtil.saveRewardPenaltyfromFiles(configurations, mazeFileName, timeFileName, staticCreateTimeOutTime+staticLoadTimeOutTime, dynamicTimeOutTime, staticDynamicSettings[2], staticUpdated);
        	if (optionStr.toUpperCase().startsWith("C")) {
        		ODDController.setNextConfigurationInFileControl(mazeFileName, configurationFileName,"budget.txt", resultL); 
        	}
        	else
        		ODDController.setNextConfigurationInFile(learner, mazeFileName, configurationFileName,"budget.txt", resultL); 
        }
        else if (optionStr.toUpperCase().startsWith("D")) {
            System.out.println("D DPPath2= " + DPPath);        	
        	ODDUtil.copyFile(DPPath+"/Configuration.txt",configurationFileName);
        }
        //System.out.println("ODDUtil.saveRewardPenaltyfromFiles(");
        //logger.info("ODDUtil.saveRewardPenaltyfromFiles(");
        //dynamicConfigurations=ODDController.updatedConfigurationFromTimesFile(dynamicConFigurationFileName, dynamicTimeFileName, dynamicTimeOutTime,4);
        //System.out.println("oldStaticConfigurations: " + oldStaticConfigurations);
        //logger.info("ODDMonitor oldStaticConfigurations: " + oldStaticConfigurations);
        //ODDController.updateConfigurationFromTimesFile(configurationFileName, timeFileName, dynamicTimeOutTime,6);

        System.out.println("After covArray.length  4");
        
 
        
        //System.out.println("ODDController.setNextConfigurationInFile(learner, mazeFileName, configurationFileName);");
        //logger.info("ODDController.setNextConfigurationInFile(learner, mazeFileName, configurationFileName);");
       
        configurations=ODDUtil.readLastLine(configurationFileName);		
        staticConfigurations=configurations.substring(0, 2);
		//dynamicConfigurations=configurations.substring(2, 6);
		getConfigurations(configurationFileName);
        System.out.println("oldStaticConfigurations: " + oldStaticConfigurations + " newStaticConfigurations: " + staticConfigurations);
        //logger.info("ODDMonitor oldStaticConfigurations: " + oldStaticConfigurations + " newStaticConfigurations: " + staticConfigurations);
        if (oldStaticConfigurations.equals(staticConfigurations))  {        	
        	staticUpdated=false;
        }
        else
        {
        	staticUpdated=true;
        }

        System.out.println("After covArray.length  5");

		profile.BranchReporter br=new profile.BranchReporter();
		//br.report(covArray);
		br.writeReportMsg(covArray, "stmtCoverage1.out");
		br.writeReportMsg(covArray, System.getProperty("user.dir") + File.separator + "test1/branches"+System.currentTimeMillis()+".out");
		
        return (System.currentTimeMillis() - startTime);
    }
	public static void getConfigurations(String configurationFile) throws Exception {
//    	File file = new File(configurationFile);  
//        if (!file.exists()) {  
//        	configurationFile="Configuration.txt";
//        }  
		configurations=ODDUtil.readLastLine(configurationFile);
		System.out.println("ODDMonitor configurations="+configurations+" configurationFile"+configurationFile);
		staticConfigurations=configurations.substring(0, 2);
		//logger.info("ODDMonitor configurations="+configurations);
		for (int i=0; i<staticDynamicSettings.length; i++)
		{
			staticDynamicSettings[i]=true;
		}	
		String configurationFlag="";
		int flagSize=staticDynamicSettings.length;
		if	(configurations.length()<flagSize)
			flagSize=configurations.length();
		for (int i=0; i<configurations.length(); i++)
		{
			configurationFlag=configurations.substring(i,i+1);
			if (configurationFlag.equals("0") || configurationFlag.equals("f") ||configurationFlag.equals("F")) {
    			staticDynamicSettings[i]=false;
    		}
    		else {
    			staticDynamicSettings[i]=true;
    		}
			////logger.info("ODDMonitor staticDynamicSettings["+i+"]="+staticDynamicSettings[i]);
		}	
	}
	public static String getProcessIDString() {
		return ManagementFactory.getRuntimeMXBean().getName().replaceAll("[^a-zA-Z0-9]", "");
	}
	
	static class MyLoadStaticGraph implements Callable <Boolean> {
		@Override
        public Boolean call() throws Exception {
			try {
                Thread.sleep(1);     
                String mainClass=ODDUtil.findMainClass();
                int initializeReult=-1;
            	File file1 = new File(staticVtgFileName);  
                if (!file1.exists())             	
                { 
                	initializeReult=ODDImpactAllInOne.initializeClassGraph(mainClass,staticDynamicSettings[4]);
                }
                else
                	initializeReult=ODDImpactAllInOne.initializeClassGraph(mainClass,staticDynamicSettings[4],staticVtgFileName);   
				if (initializeReult !=0)  {
					//System.out.println("MyInitialStaticGraph ODDImpactAllInOne.svtg.edgeSet().size()="+ODDImpactAllInOne.svtg.edgeSet().size()+" ODDImpactAllInOne.svtg.nodeSet().size()="+ODDImpactAllInOne.svtg.nodeSet());
					System.out.println("Unable to load satic graph");
	    			//logger.info("ODDMonitor Unable to load satic graph");
				}
				else
					System.out.println("Loading static graph successes"); 
			  } catch (InterruptedException e) {
				  return false; // 
			  }
			return true;
		}
	}	
	
    public synchronized static void LoadStaticGraph(long timeOutTime) throws  Exception{
    	long startTime = System.currentTimeMillis();
        MyLoadStaticGraph task2 = new ODDMonitor.MyLoadStaticGraph();
        ExecutorService executor = Executors.newCachedThreadPool();
        Future<Boolean> f2 = executor.submit(task2);
        try {
            if (f2.get(staticCreateTimeOutTime, TimeUnit.MILLISECONDS)) { // 
                System.out.println("LoadStaticGraph complete successfully");        
                //logger.info("ODDMonitor Creating static graph successes");  
                isStaticCreateTimeOut=false;
            }
        } catch (InterruptedException e) {
            System.out.println("LoadStaticGraph was interrupted during the sleeping");        
            //logger.info("ODDMonitor Creating static graph successes");  
            executor.shutdownNow();
            isStaticCreateTimeOut=true;
        } catch (ExecutionException e) {
            System.out.println("LoadStaticGraph has mistakes during getting the result");        
            //logger.info("ODDMonitor Creating static graph successes");  
            executor.shutdownNow();
            isStaticCreateTimeOut=true;
        } catch (TimeoutException e) {
            System.out.println("LoadStaticGraph is timeoouts");        
            //logger.info("ODDMonitor Creating static graph successes");  
            f2.cancel(true);
            isStaticCreateTimeOut=true;
            // executor.shutdownNow();
            // executor.shutdown();
        } finally {
            executor.shutdownNow();
        }
        System.out.println("LoadStaticGraph took "+(System.currentTimeMillis() - startTime)+" ms ");
    }  
	
	static class MyCreateStaticGraph implements Callable <Boolean> {
		@Override
        public Boolean call() throws Exception {
			try {
				Thread.sleep(1); 
//				HashSet eventMethods=ODDUtil.getMethodNameSet(All_Queue, ODDImpactAllInOne.idx2method);
//				if (eventMethods.size()<1)
//					return false;
				System.out.println("Before ./ODDEventsStaticGraph.sh");
				Process process=Runtime.getRuntime().exec("./ODDEventsStaticGraph.sh "); 	
				process.waitFor();				
				System.out.println("After ./ODDEventsStaticGraph.sh");				
			  } catch (InterruptedException e) {
				  //e.printStackTrace();
				  System.out.println("MyCreateStaticGraph is interrupted when calculating, will stop... Exception: "+e);
	    			//logger.info("ODDMonitor MyInitialStaticGraph is interrupted when calculating, will stop...");
				  return false; // 
			  }
			return true;
		}
	}	
    public synchronized static void createStaticGraph(long timeOutTime) throws  Exception{
    	//System.out.println("createStaticGraph 1");   
    	long startTime = System.currentTimeMillis();
        MyCreateStaticGraph task1 = new ODDMonitor.MyCreateStaticGraph();
    	//System.out.println("createStaticGraph 2");   
        ExecutorService executor = Executors.newCachedThreadPool();
    	//System.out.println("createStaticGraph 3");   
        Future<Boolean> f1 = executor.submit(task1);
        try {
        	//System.out.println("createStaticGraph timeOutTime: "+timeOutTime); 
            if (f1.get(timeOutTime, TimeUnit.MILLISECONDS)) { // 
                System.out.println("createStaticGraph complete successfully");        
                //logger.info("ODDMonitor Creating static graph successes");  
            }
         	//ODDUtil.copyFile("staticVtg"+getProcessIDString()+".dat","staticVtg.dat"); 
            isStaticCreateTimeOut=false;
        } catch (InterruptedException e) {
            System.out.println("createStaticGraph was interrupted during the sleeping");        
            //logger.info("ODDMonitor Creating static graph successes");  
            executor.shutdownNow();
            isStaticCreateTimeOut=true;
        } catch (ExecutionException e) {
            System.out.println("createStaticGraph has mistakes during getting the result");        
            //logger.info("ODDMonitor Creating static graph successes");  
            executor.shutdownNow();
            isStaticCreateTimeOut=true;
        } catch (TimeoutException e) {
            System.out.println("createStaticGraph is timeoouts");        
            //logger.info("ODDMonitor Creating static graph successes");  
            f1.cancel(true);
            isStaticCreateTimeOut=true;
            // executor.shutdownNow();
            // executor.shutdown();
        } finally {
            executor.shutdownNow();
        }
        System.out.println("createStaticGraph took "+(System.currentTimeMillis() - startTime)+" ms ");
        //System.out.println("createStaticGraph 1 staticDynamicSettings[2]="+staticDynamicSettings[2]);
    }  
	////////////////////////////////////////////////////////////////
	// 2. communication Events
	////////////////////////////////////////////////////////////////
	private static boolean threadAsProcess = false;
	public static final int BUFLEN = 4;
	public static final int PIDLEN = 32;
	
	protected static final logicClock g_lgclock = new logicClock(new AtomicInteger(0), getMAC()+getProcessID());
	public synchronized static logicClock getlgclock() { return g_lgclock; }
	
	public static void setThreadAsProcess(boolean flag) {
		threadAsProcess = flag;
	}
	public static byte[] intToByteArray(int value, ByteOrder ord) {
		return ByteBuffer.allocate(BUFLEN).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array();
		//return ByteBuffer.allocate(BUFLEN).order(ByteOrder.BIG_ENDIAN).putInt(value).array();
		//return ByteBuffer.allocate(BUFLEN).order(ord).putInt(value).array();
	}
	public static int byteArrayToInt(byte[] b, ByteOrder ord) {
		return ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getInt();
		//return ByteBuffer.wrap(b).order(ByteOrder.BIG_ENDIAN).getInt();
		//return ByteBuffer.wrap(b).order(ord).getInt();
	}
	public static String getMAC() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i],
                        (i < mac.length - 1) ? "-" : ""));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
	public static String getProcessID() {
		return ManagementFactory.getRuntimeMXBean().getName()+'\0';
	}
	
	private static void onRecvSenderID(String senderid) {
		/** for each process, only record the first message receiving event for each unique sender */
		Integer curval = S.get(senderid);
		if (debugOut)
			System.out.println("******************** onRecvSenderID senderid"+senderid +" curval = " + curval +" getProcessID = " + getProcessID());
		//receivedMessages+="onRecvSenderID senderid"+senderid +" curval = " + curval +" getProcessID = " + getProcessID();
		receivedMsCount++;
		if (null == curval) {
			if (debugOut && senderid.compareToIgnoreCase(getProcessID())==0) {
				System.out.println("WARNING: receive a message from the local process being sent to itself !!");
			}
			S.put(senderid, g_lgclock.getLTS());
			return;
		}
	}
	
	/** the logic clock used for Lamport timestamping */
	public static class logicClock {
		
		private AtomicInteger lts; // lamport time stamp
		private String pid; // process id --- unique identifier of a process
		
	    //private AtomicInteger readyToRead = new AtomicInteger(1); /** forcing mirrored read and write */
	    //private AtomicInteger readyToReadSocket = new AtomicInteger(1); /** forcing mirrored read and write */
	    //private AtomicInteger readyToReadNio = new AtomicInteger(1); /** forcing mirrored read and write */
	    //private AtomicInteger readyToReadANio = new AtomicInteger(1); /** forcing mirrored read and write */
		 private boolean readyToRead = true; /** forcing mirrored read and write */
		    private boolean readyToReadSocket = true; /** forcing mirrored read and write */
		    private boolean readyToReadNio = true; /** forcing mirrored read and write */
		    private boolean readyToReadANio = true; /** forcing mirrored read and write */
		
		final static Integer TOKEN_FLAG = 0xABCDDCBA;
		    
		public logicClock(AtomicInteger _lts, String _pid) {
			this.lts = _lts;
			this.pid = _pid;
			
			/*
			readyToRead.set(1);
			readyToReadSocket.set(1);
			readyToReadNio.set(1);
			readyToReadANio.set(1);
			*/
		}
		public void initClock(int iv) {
			synchronized (lts) {
				lts.set(iv);
			}
		}
		@Override public String toString() {
			return hostId();
		}
		public String hostId() {
			if (threadAsProcess) {
				return pid + Thread.currentThread();
			}
			return pid;
		}
		protected boolean isClock(int _lts, boolean rev) {
			//if (rev) _lts = Integer.reverseBytes(_lts);
			//if (usingToken)
			//	return (_lts >> 28) == flag;
			return true;
		}
		private int pickClock(int _lts, boolean rev) {
			//if (rev) _lts = Integer.reverseBytes(_lts);
			//if (usingToken)
			//	return _lts & 0x0fffffff;
			return _lts;
		}
		public synchronized int getLTS() {
			return lts.get();
		}
		public synchronized int getTimestamp() {
			// we use the first byte to identify clock and three bytes after for storing time stamp itself
			//if (usingToken)
			// return lts.get() | (flag << 28);
			return lts.get();
		}
		public synchronized int setTimestamp(int _lts) {
			return lts.getAndSet(_lts);
		}
		public synchronized int increment() {
			return lts.getAndIncrement();
		}
		public synchronized int updateClock(int other_lts) {
			// update the local (process) clock with the remote (process) clock
			int val = Math.max(other_lts, this.getTimestamp());
			this.setTimestamp(val);
			return this.increment();
		}
		
		protected int bytesToReadANIO = 0, bytesToReadNIO = 0, bytesAvailableSocket = 0;
		
	    // Socket read: for now, just read the integer lts
	    public void retrieveClock(InputStream in) throws IOException {
    	
	    	
	        byte[] buf = new byte[BUFLEN];
	        //if (in.markSupported()) in.mark(Integer.MAX_VALUE);
	        int bytesRead = in.read(buf);
	        if (bytesRead == -1 || bytesRead == 0) {
	           //if (in.markSupported()) in.reset();
	           return;
	        }

				
	        if (usingToken) {

	        	
	        	/** 1: read the token, namely the data length the peer sent in recent write operation */ 
		        int token = byteArrayToInt(buf, ByteOrder.LITTLE_ENDIAN);
		        bytesAvailableSocket = token;
		        if (debugOut) {
		        	System.out.println("[To Read]<= " + "socket token received is " + token);
	        		System.out.println("[Read]<= " + BUFLEN + " bytes read for socket token");
	        	}
		        
		        assert bytesRead == BUFLEN;
		        bytesAvailableSocket -= BUFLEN;
		        
		        bytesRead = in.read(buf);
		        if (debugOut) {
	        		System.out.println("[Read]<= " + bytesRead + " bytes read for socket clock");
	        	}
		        if (bytesRead == -1 || bytesRead == 0) {
		           System.err.println("!!!!!Unexpected ERROR when retrieving socket clock after getting token!!!!!");
		           return;
		        }
		        
		        assert bytesRead == BUFLEN;
		        bytesAvailableSocket -= BUFLEN;
	        }
	        
	        /** 2: read the clock */
	        int lts = byteArrayToInt(buf, ByteOrder.LITTLE_ENDIAN);
	        //if (!isClock(lts, false)) {
	        	//if (in.markSupported()) in.reset();
	        //	return;
	        //}
	        //lts = pickClock(lts, false);
	        
	        //this.setTimestamp(lts);
	        this.updateClock(lts);
	        
	        /** 3: read the sender id if opted on for it */
	        if (usingToken)  {
	        	byte[] snlenarray = new byte[BUFLEN];
	        	int snlenlen = in.read(snlenarray);
	        	assert snlenlen == BUFLEN;
	        	 {
	        		bytesAvailableSocket -= BUFLEN;
	        	}
	        	int snlen = byteArrayToInt(snlenarray, ByteOrder.LITTLE_ENDIAN);
	        	
	        	//byte[] senderidArray = new byte[PIDLEN]; // 16 bytes to hold a unique process id should be enough
	        	byte[] senderidArray = new byte[snlen]; // 16 bytes to hold a unique process id should be enough
	        	int actuallen = in.read(senderidArray,0,snlen);
	        	if (debugOut) {
	        		System.out.println(actuallen + " bytes retrieved for sender name.");
	        	}
		        String sender = new String(senderidArray).trim();
		        onRecvSenderID(sender);
		        //receivedMessages+="[receive message ProcessID"+getProcessID();
		        System.out.println("******************** [receive message ProcessID"+getProcessID());
		        
		        if (debugOut) {
		        	System.out.println("[Socket I/O Stream @ " + this.hostId() + "]: receive message from sender: " + sender);
		        	//System.out.println("******************** [receive message ProcessID"+getProcessID()+ " from sender: " + getProcessID())
		        }
		         {
		        	bytesAvailableSocket -= actuallen;
		        }
	        }
	        
	        if (debugOut) {
	        	System.out.println("[Socket I/O Stream @ " + this.hostId() + "]: clock received = " + lts);
	        	if (lts > pickClock(this.getTimestamp(), false)) {
	        		System.out.println("\t ---> local clock updated to the remote one of " + lts);
	        	}
	        }
	    }

	    // Nio read: for now, just read the integer lts
	    public void retrieveClock(SocketChannel s) throws IOException {

	    	
	        ByteBuffer buf = ByteBuffer.allocate(BUFLEN);
	        //if (s.socket().getInputStream().markSupported()) s.socket().getInputStream().mark(Integer.MAX_VALUE);
	        
	        int bytesRead = s.read(buf);
	        //System.out.println(bytesRead + " bytes for clock read from socketChannel " +s);
	        if (bytesRead == -1 || bytesRead == 0) {
	        	//if (s.socket().getInputStream().markSupported()) s.socket().getInputStream().reset();
	            return;
	        }
	        /** 2: read the clock */
	        buf.rewind();
	        byte[] ltsArray = new byte[BUFLEN];
	        buf.get(ltsArray);
	        int lts = byteArrayToInt(ltsArray, buf.order());
	        
	        //if (!isClock(lts, buf.order()==ByteOrder.BIG_ENDIAN)) {
	        //	return;
	        //}
	        //lts = pickClock(lts, buf.order()==ByteOrder.BIG_ENDIAN);
	        
	        if (debugOut) {
	        	System.out.println("[NIO Channel/SocketChannel @ " + this.hostId() + "]: clock received = " + lts);
	        	if (lts > pickClock(this.getTimestamp(), false)) {
	        		System.out.println("\t ---> local clock updated to the remote one of " + lts);
	        	}
	        }
	        
	        //this.setTimestamp(lts);
	        this.updateClock(lts);
	        
	        /** 3: retrieve sender id if opted on for it */
	        if (usingToken)  {
	        	buf.clear();
	        	s.read(buf);
	        	buf.rewind();
	        	byte[] snlenarray = new byte[BUFLEN];
	        	buf.get(snlenarray);
	        	int snlen = byteArrayToInt(snlenarray, ByteOrder.LITTLE_ENDIAN);
	        	
	        	//byte[] senderidArray = new byte[PIDLEN]; // 16 bytes to hold a unique process id should be enough
	        	byte[] senderidArray = new byte[snlen];
	        	
	        	/*
	        	//byte[] senderidArray = new byte[PIDLEN]; // 16 bytes to hold a unique process id should be enough
		        buf.get(senderidArray);
		        */
	        	ByteBuffer buf2 = ByteBuffer.allocate(snlen);
	        	s.read(buf2);
	        	buf2.rewind();
	        	buf2.get(senderidArray);
		        
		        String sender = new String(senderidArray).trim();
		        onRecvSenderID(sender);
		        //receivedMessages+="retrieveClock receive message from sender: "+sender +"  getProcessID = " + getProcessID();
		        //System.out.println("******************** retrieveClock receive message from sender: "+sender +"  getProcessID = " + getProcessID());
		        if (debugOut) {
		        	System.out.println("[NIO Channel/SocketChannel @ " + this.hostId() + "]: receive message from sender: " + sender);
		        }
		        /*
		        if (usingToken) {
			        int actuallen = sender.getBytes().length;
			        bytesRead += actuallen;
			        bytesToReadNIO -= actuallen;
		        }
		        */
	        }
	    }
	    
	    // Nio Async read: for now, just read the integer lts
	    private int retrieveClockEx(SocketChannel s, ByteBuffer buf_recved) throws IOException {

	    	assert buf_recved.remaining() >= BUFLEN;
	    	//System.out.println("bytes remaining " + buf_recved.remaining());
	    	int bytesConsumed = 0;
	    	
	    	int nb = 0;
	        if (usingToken) {
	        	/** 1: retrieve token : bytes sent by peer recently */
	        	//buf_recved.mark();
		        //int pos = buf_recved.position();
	        	byte[] tokenarray = new byte[BUFLEN];
	        	buf_recved.get(tokenarray);
	        	int token = byteArrayToInt(tokenarray, buf_recved.order());
	        	bytesToReadANIO = token;
	        	if (debugOut) {
		        	//System.out.println("token received is " + Integer.toHexString(flag));
	        		System.out.println("<=[All to Read] " + "ANIO token received is " + token);
	        		System.out.println("<=[Read] " + BUFLEN + " bytes read for ANIO token");
	        	}
	        	bytesConsumed += BUFLEN;
	        	bytesToReadANIO -= BUFLEN;	        
	        
	        	/** 2: retrieve clock : recent LTS of peer process */
	        	assert bytesToReadANIO >= BUFLEN; 
		        if (buf_recved.remaining() < BUFLEN) {
		        	buf_recved.clear();
		        	nb = s.read(buf_recved);
		        	buf_recved.rewind();
		        	if (nb < BUFLEN) {
		        		System.err.println("!!!!!Unexpected ERROR when retrieving ANIO clock after getting token!!!!!");
		        		return bytesConsumed;
		        	}
		        }
	        }

	        byte[] ltsArray = new byte[BUFLEN];
	        buf_recved.get(ltsArray);
	        int lts = byteArrayToInt(ltsArray, buf_recved.order());
	        if (debugOut && usingToken) {
        		System.out.println("<=[Read] " + BUFLEN + " bytes read for ANIO clock");
        	}
	        
	        //lts = pickClock(lts,buf_recved.order()==ByteOrder.BIG_ENDIAN);
	        
	        if (debugOut) {
	        	System.out.println("[NIO Async Channel/SocketChannel @ " + this.hostId() + "]: clock received = " + lts);
	        	if (lts > pickClock(this.getTimestamp(), false)) {
	        		System.out.println("\t ---> local clock updated to the remote one of " + lts);
	        	}
	        }
	        
	        if (usingToken) {
	        	bytesConsumed += BUFLEN;
	        	bytesToReadANIO -= BUFLEN;
	        }
	        
	        //this.setTimestamp(lts);
	        this.updateClock(lts);
	        
	        /** 3: retrieve sender id if opted on for it */
	        if (trackingSender) {
	        	if (usingToken) {
		        	if (debugOut) {
		        		System.out.println(buf_recved.remaining() + " bytes remained before reading sender name length.");
		        	}
		        	if (bytesToReadANIO > 0 && buf_recved.remaining() == 0) {
		        		buf_recved.clear();
		    			nb = s.read(buf_recved);
		    			if (debugOut) {
		    				System.out.println(nb + " bytes fetched from the channel before reading sender name length.");
		    			}
		    			buf_recved.rewind();
		        	}
	        	}
	        	byte[] snlenarray = new byte[BUFLEN];
	        	buf_recved.get(snlenarray);
	        	if (usingToken) {
	        		bytesConsumed += BUFLEN;
		        	bytesToReadANIO -= BUFLEN;
	        	}
	        	
	        	int snlen = byteArrayToInt(snlenarray, buf_recved.order());
	        	byte[] senderidArray = new byte[snlen];
	        	
	        	/*
	        	//byte[] senderidArray = new byte[PIDLEN]; // 16 bytes to hold a unique process id should be enough
		        buf_recved.get(senderidArray);
		        */
	        	ByteBuffer buf = ByteBuffer.allocate(snlen);
	        	s.read(buf);
	        	buf.rewind();
	        	buf.get(senderidArray);
		        
		        String sender = new String(senderidArray).trim();
		        onRecvSenderID(sender);
		        if (debugOut) {
		        	System.out.println("[NIO Async Channel/SocketChannel @ " + this.hostId() + "]: receive message from sender: " + sender);
		        }
		        if (usingToken) {
			        int actuallen = snlen; //sender.getBytes().length;
			        bytesConsumed += actuallen;
		        	bytesToReadANIO -= actuallen;
		        }
	        }
	        
	        if (usingToken) {
	        	if (debugOut) {
	        		System.out.println("bytes remaining after retrieving ANIO clock: " + buf_recved.remaining());
	        	}
	        	/** 3: read the original message as intended by the instrumented I/O function call itself */
	        	if (bytesToReadANIO > 0 && buf_recved.remaining() == 0) {
	        		buf_recved.clear();
	    			nb = s.read(buf_recved);
	    			if (debugOut) {
	    				System.out.println("<=[Read] " + nb + " bytes read for original message with ANIO token+clock piggybacked");
	    			}
	    			bytesToReadANIO -= nb;
	    			bytesConsumed += nb;
	    			return nb;
		        }
	        	/** if the bytebuffer takes all data sent by peer in one read already; just compact the buffer now */
	        	buf_recved.compact();
	        	//return bytesConsumed;
	        	//return buf_recved.remaining();
	        	nb = bytesToReadANIO; // all data has been read through
	        	if (debugOut) {
    				System.out.println("<=[Read] " + nb + " bytes read for original message with ANIO token+clock piggybacked");
    			}
	        	bytesToReadANIO -= nb;
    			bytesConsumed += nb;
	        	return nb;
	        }
	        
	        return BUFLEN;
	    }
	    
	    public int retrieveClock (SocketChannel s, ByteBuffer dst) throws IOException {
	    	//assert dst.order() == ByteOrder.LITTLE_ENDIAN;
	    	//assert s.finishConnect() && s.isConnected() && s.isOpen();
	    	int nb = s.read(dst);
	    	//String name = new Object(){}.getClass().getEnclosingMethod().getName();
	    	//System.out.println(nb + " bytes read in " + name);
	    	//if (nb == 0) {
	    	if (nb == -1 || nb == 0) {
				//if (nb < BUFLEN) {
				//if (s.socket().getInputStream().markSupported()) s.socket().getInputStream().reset();
	    		//dst.rewind();
				return nb;
			}
	
			if (usingToken) {
				int ret = 0;
				if (debugOut) {
					System.out.println("<=[To Read] " + "ANIO bytes to read: " + bytesToReadANIO);
				}
				if (bytesToReadANIO > 0) {
					if (nb > 0) {
						bytesToReadANIO -= nb;
						if (bytesToReadANIO < 0) bytesToReadANIO = 0;
					}
					if (debugOut) {
						System.out.println("<=[Read] " + nb + " bytes read for original message without ANIO token+clock piggybacked");
					}
					return nb;
				}
				else {
					//System.out.println("FINISHED reading all data last sent by the peer: " + bytesToRead);
					
					dst.rewind();
					ret = this.retrieveClockEx(s,dst);
					//dst.compact();
					//System.out.println("bytes remaining after compacting: " + dst.remaining());
				}
				return ret;
			}
			
			//ByteBuffer ndst = ByteBuffer.allocate(dst.capacity()-BUFLEN);
			//ndst.put(dst);
			dst.rewind();
			int shift = this.retrieveClockEx(s,dst);
			dst.compact(); // this operation saves my life out of two-day hopeless debugging!
			assert nb - shift >= 0;
			return nb - shift; //BUFLEN;
	    }
	    public long retrieveClock (SocketChannel s, ByteBuffer[] dsts, int offset, int length) throws IOException {
	    	long nb = s.read(dsts, offset, length);
	    	if (nb == -1 || nb == 0) {
				//if (nb < BUFLEN) {
				//if (s.socket().getInputStream().markSupported()) s.socket().getInputStream().reset();
				return nb;
			}
	    	
	    	//if (s.socket().getInputStream().markSupported()) s.socket().getInputStream().mark(Integer.MAX_VALUE);
	    	
			//assert nb >= BUFLEN;
			/** use the first bytebuffer of the sequence for logic clock transmission */
	    	if (usingToken) {
				int ret = 0;
				System.out.println("<=[To Read] " + "ANIO bytes to read: " + bytesToReadANIO);
				if (bytesToReadANIO > 0) {
					if (nb > 0) {
						bytesToReadANIO -= nb;
						if (bytesToReadANIO < 0) bytesToReadANIO = 0;
					}
					if (debugOut) {
						System.out.println("<=[Read] " + nb + " bytes read for original message without ANIO token+clock piggybacked");
					}
					return nb;
				}
				else {
					//System.out.println("FINISHED reading all data last sent by the peer: " + bytesToRead);
					
					dsts[offset].rewind();
					ret = this.retrieveClockEx(s,dsts[offset]);
					//dst.compact();
					//System.out.println("bytes remaining after compacting: " + dst.remaining());
				}
				return ret;
			}
	    	
			dsts[offset].rewind();
			int shift = this.retrieveClockEx(s,dsts[offset]);
			dsts[offset].compact();
			return nb - shift; //BUFLEN;
	    }
	    
	    // Socket write: for now, just piggyback the original message with the integer lts
	    public void packClock(OutputStream out, int len) throws IOException {
	       	 int towrite = len + BUFLEN;
	    	 if (trackingSender) {
	    		 if (debugOut) {
	    			 System.out.println(getProcessID().getBytes().length + " bytes for sendername to pack.");
	    		 }
	    		 towrite += BUFLEN+getProcessID().getBytes().length;
	    	 }
	    	 
	    	 if (usingToken) {
	    		 towrite += BUFLEN;
	    		 byte[] tokenarray = intToByteArray(towrite,ByteOrder.LITTLE_ENDIAN);
	    		 if (debugOut) {
		        	System.out.println("[All to Write]=> " + "socket token to send " + towrite);
		         }
	    		 out.write(tokenarray);
	    	 }
	         
	         byte[] ltsarray = intToByteArray(getTimestamp(),ByteOrder.LITTLE_ENDIAN);
	         out.write(ltsarray);
	         if (trackingSender) {
	        	 byte[] snlenarray = intToByteArray(getProcessID().getBytes().length,ByteOrder.LITTLE_ENDIAN);
	        	 out.write(snlenarray);
	        	 out.write(getProcessID().getBytes());
	         }
	         
	         if (debugOut) {
	        	 System.out.println("[Socket I/O Stream @ " + this.hostId() + "]: clock sent = " + 
	        			 pickClock(this.getTimestamp(),false));
	        	 if (trackingSender) {
	        		 System.out.println("[Socket I/O Stream @ " + this.hostId() + "]: sender = " + getProcessID());
		         }
	         }
	         readyToReadSocket = true;
	         if (debugOut && usingToken) {
	        	 System.out.println("[Write]=> " + towrite + " socket bytes written");
		     }
	    }
	    
	    // Nio write: for now, just piggyback the original message with the integer lts 
	    public void packClock(SocketChannel s) throws IOException {
	    	int buflen = BUFLEN;

	    	if (trackingSender) {
	    		buflen += BUFLEN+getProcessID().getBytes().length;
		    }
	    	ByteBuffer buf = ByteBuffer.allocate(buflen);
	        byte[] ltsarray = intToByteArray(getTimestamp(), buf.order());

	        buf.put(ltsarray);
	        if (trackingSender) {
	        	byte[] snlenarray = intToByteArray(getProcessID().getBytes().length, buf.order());//ByteOrder.LITTLE_ENDIAN);
	        	buf.put(snlenarray);
	        	buf.put(getProcessID().getBytes());
	        }
	        buf.flip();
	        s.write(buf);
	        
	        if (debugOut) {
	        	 System.out.println("[NIO Channel/SocketChannel @ " + this.hostId() + "]: clock sent = " + 
	        			 pickClock(this.getTimestamp(), false));
	        	 if (trackingSender) {
	        		 System.out.println("[NIO Channel/SocketChannel @ " + this.hostId() + "]: sender = " + getProcessID());
		         }
	         }
	        readyToReadNio = true;
	    }
	    
	    // Nio Async write: for now, just piggyback the original message with the integer lts
	    // returned an augmented buffer which holds the logic clock appended with original buffer content
	    private ByteBuffer packClock(ByteBuffer buf_tosend) throws IOException {
	    	//byte[] lenarray = intToByteArray(buf_tosend.remaining());
	        byte[] ltsarray = intToByteArray(getTimestamp(), buf_tosend.order());
	        //ByteBuffer buf = ByteBuffer.allocate(BUFLEN*2 + buf_tosend.remaining());
	        int buflen = BUFLEN + buf_tosend.remaining();
	        if (usingToken) {
	        	buflen += BUFLEN;
	        }
	        if (trackingSender) {
	        	buflen += BUFLEN+getProcessID().getBytes().length;
	        }
	        ByteBuffer buf = ByteBuffer.allocate(buflen);
	        if (usingToken) {
	        	byte[] tokenarray = intToByteArray(buflen, buf_tosend.order());
	        	if (debugOut) {
	        		System.out.println("=>[All to Write] " + "ANIO token to send " + buflen);
	        	}
	        	buf.put(tokenarray);
	        }
	        //buf.put(lenarray);
	        buf.put(ltsarray);
	        //buf_tosend.rewind();
	        if (trackingSender) {
	        	byte[] snlenarray = intToByteArray(getProcessID().getBytes().length, buf_tosend.order()); //ByteOrder.LITTLE_ENDIAN);
	        	buf.put(snlenarray);
	        	buf.put(getProcessID().getBytes());
	        }
	        buf.put(buf_tosend);
	        
	        if (debugOut) {
	        	 System.out.println("[NIO Async Channel/SocketChannel @ " + this.hostId() + "]: clock sent = " + 
	        			 pickClock(this.getTimestamp(), false));
	        	 if (trackingSender) {
	        		 System.out.println("[NIO Async Channel/SocketChannel @ " + this.hostId() + "]: sender = " + getProcessID());
		         }
	        }
	        
	        return buf;
	    }
	    
	    public int packClock(SocketChannel s, ByteBuffer src) throws IOException {
	    	//System.out.println("src.remaining=" + src.remaining());
			ByteBuffer tosend = this.packClock(src);
			tosend.flip();
			int ret = s.write(tosend);
			readyToReadANio = true;
	        //src.position(src.position() + src.remaining());
	        if (debugOut && usingToken) {
	        	System.out.println("=>[Write] " + ret + " ANIO bytes written");
	        }
	        return ret;
		}
	    public long packClock(SocketChannel s, ByteBuffer[] srcs, int offset, int length) throws IOException {
			/** use the first bytebuffer of the sequence for logic clock transmission */
			ByteBuffer tosend = this.packClock(srcs[offset]);
			srcs[offset] = tosend;
			srcs[offset].flip();
			long ret = s.write(srcs, offset, length);
			readyToReadANio = true;
	        if (debugOut && usingToken) {
	        	System.out.println("=>[Write] " + ret + " ANIO bytes written");
	        }
			return ret;
		}
	}
	
	private static boolean g_intercept = true;
	public static void disable() { 
		g_intercept = false; 
		dtSocketInputStream.intercept = g_intercept;
		dtSocketOutputStream.intercept = g_intercept;
	}
	public static void enable() { 
		g_intercept = true;
		dtSocketInputStream.intercept = g_intercept;
		dtSocketOutputStream.intercept = g_intercept;
	}
	public static boolean isEnabled() { return g_intercept;}
	////----------------------------------- empirical tests show that the following, same as ShiVector did, can deal with NIO traffics at synchronous mode only 
	// probe for NIO reads
	public synchronized static void dist_nioread(SocketChannel s){
		if (debugOut)
			System.out.println("******************** [dist_nioread " + s + "]: getProcessID = " + getProcessID());
		//receivedMessages+="[dist_nioread " + s + "]: getProcessID = " + getProcessID()+"\n";
		receivedMsCount++;
		if (!isEnabled()) { return; }
		
		try {
			g_lgclock.retrieveClock(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	// probe for NIO writes	
	public synchronized static void dist_niowrite(SocketChannel s){
		if (!isEnabled()) { return; }
		try {
			g_lgclock.packClock(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	////----------------------------------- proprietary probes for IPC data transfers through ObjectIn/OutputStream:read/writeObject ---- 
	// probe for objectInputStream readObject
	public synchronized static void dist_objstreamread(InputStream is){
		if (debugOut)
			System.out.println("******************** [dist_objstreamread " + is + "]: getProcessID = " + getProcessID());
		//receivedMessages+="[dist_objstreamread " + is + "]: getProcessID = " + getProcessID()+"\n";
		receivedMsCount++;
		//System.out.println("did nothing.");
		if (!isEnabled()) { return; }
		
		try {
			g_lgclock.retrieveClock(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// probe for NIO writes	
	public synchronized static void dist_objstreamwrite(OutputStream os){
		//System.out.println("did nothing.");
		if (debugOut)
			System.out.println("******************** [dist_objstreamwrite " + os + "]: getProcessID = " + getProcessID());
		if (!isEnabled()) { return; }
		try {
			g_lgclock.packClock(os,0);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//// ----------------------------------- deal with NIOs traffics working at asynchronous mode
	public synchronized static int dist_async_nioread(SocketChannel s, ByteBuffer dst) throws IOException {
		if (debugOut)
		System.out.println("******************** [dist_async_nioread1 " + s +" dst=" + dst + "]: getProcessID = " + getProcessID());
		//receivedMessages+="[dist_async_nioread1 " + s +" dst=" + dst + "]: getProcessID = " + getProcessID()+"\n";
		receivedMsCount++;
		if (!isEnabled()) {
			//g_lgclock.retrieveClock(s);
			return s.read(dst);
		}
		
		int ret = 0;
		try {
			ret = g_lgclock.retrieveClock(s, dst);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	public synchronized static long dist_async_nioread(SocketChannel s, ByteBuffer[] dsts) throws IOException {
		if (debugOut)
		System.out.println("******************** [dist_async_nioread2 " + s +" dsts=" + dsts + "]: getProcessID = " + getProcessID());
		//receivedMessages+="[dist_async_nioread2 " + s +" dsts=" + dsts + "]: getProcessID = " + getProcessID()+"\n";
		receivedMsCount++;
		if (!isEnabled()) {
			//g_lgclock.retrieveClock(s);
			return s.read(dsts);
		}
		return dist_async_nioread(s, dsts, 0, dsts.length);
	}
	public synchronized static long dist_async_nioread(SocketChannel s, ByteBuffer[] dsts, int offset, int length) throws IOException {
		if (debugOut)
		System.out.println("******************** [dist_async_nioread3 " + s +" dsts=" + dsts +" offset=" + offset +" length=" + length + "]: getProcessID = " + getProcessID());
		//receivedMessages+="[dist_async_nioread3 " + s +" dsts=" + dsts +" offset=" + offset +" length=" + length + "]: getProcessID = " + getProcessID()+"\n";
		receivedMsCount++;
		if (!isEnabled()) {
			//g_lgclock.retrieveClock(s);
			return s.read(dsts, offset, length);
		}
		
		long ret = 0;
		try {			
			ret = g_lgclock.retrieveClock(s, dsts, offset, length);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public synchronized static int dist_async_niowrite(SocketChannel s, ByteBuffer src) throws IOException {
		//System.out.println("******************** [dist_async_niowrite1 " + s +" src=" + src + "]: getProcessID = " + getProcessID());
		if (!isEnabled()) {
			//g_lgclock.packClock(s);
			return s.write(src);
		}
		
		int ret = 0;
		try {
			ret = g_lgclock.packClock(s, src);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
		
	}
	public synchronized static long dist_async_niowrite(SocketChannel s, ByteBuffer[] srcs) throws IOException {
		//System.out.println("******************** [dist_async_niowrite2 " + s +" srcs=" + srcs + "]: getProcessID = " + getProcessID());
		if (!isEnabled()) {
			//g_lgclock.retrieveClock(s);
			return s.write(srcs);
		}
		
		return dist_async_niowrite(s, srcs, 0, srcs.length);
	}
	public synchronized static long dist_async_niowrite(SocketChannel s, ByteBuffer[] srcs, int offset, int length) throws IOException {
		//System.out.println("******************** [dist_async_niowrite3 " + s +" srcs=" + srcs + "]: getProcessID = " + getProcessID());
		if (!isEnabled()) {
			//g_lgclock.retrieveClock(s);
			return s.write(srcs, offset, length);
		}
		
		long ret = 0;
		try {
			ret = g_lgclock.packClock(s, srcs, offset, length);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

//	/** give the full EAS trace length */
//	public synchronized static int getFullTraceLength() {
//		synchronized (g_counter) {
//			return g_counter;
//		}
//	}
}

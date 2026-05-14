/**
 * File: src/Diver/ODDEAMonitorAllInOne.java
 * -------------------------------------------------------------------------------------------
 * Date			Author      	Changes
 * -------------------------------------------------------------------------------------------
 * 03/22/2019	Developer		created; for DistODD Monitor
*/
package ODD;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import ODD.TaskTest.Task;
import QL.Qlearner;
import edu.ksu.cis.indus.staticanalyses.dependency.DependencyXMLizerCLI;
import edu.ksu.cis.indus.staticanalyses.dependency.InterferenceDAv1;
import edu.ksu.cis.indus.staticanalyses.dependency.ReadyDAv1;
import edu.ksu.cis.indus.staticanalyses.dependency.SynchronizationDA;
import edu.ksu.cis.indus.staticanalyses.tokens.ITokens;
import logging.Logger;
import soot.Scene;
import soot.SootClass;

//import logging.Logger;
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
                Future acceptResult = serverChannel.accept();
                AsynchronousSocketChannel clientChannel = (AsynchronousSocketChannel) acceptResult.get();
                ByteBuffer buffer = ByteBuffer.allocate(10240);
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
                	 impactSetStr=ODDMonitor.getImpactSetStr(query);
                	 System.out.println("impactSetStr = " + impactSetStr);
                	 //logger.info("ODDMonitor impactSetStr = " + impactSetStr);
          			buffer = ByteBuffer.wrap(impactSetStr.getBytes("UTF-8"));
         			clientChannel.write(buffer);
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
	//protected static long CN_LIMIT_QUEUE = Long.MAX_VALUE;
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
	/* buffering working events */
	protected static List<Integer> span_Queue = new LinkedList<Integer>();
	protected static List<Integer> old_span_Queue = new LinkedList<Integer>();
	protected static List<Integer> diff_Queue = new LinkedList<Integer>();
	protected static List<Integer> static_Queue = new LinkedList<Integer>();
	protected static List<Integer> dynamic_Queue = new LinkedList<Integer>();
	/* buffering all queue events */
	protected static List<Integer> All_Queue = new LinkedList<Integer>();			
	// a flag ensuring timeout of static graph create 
	static boolean isStaticCreateTimeOut = false;
	// timeOut time of static graph create 
	//static long staticCreateTimeOutTime=Long.MAX_VALUE/3;            // 	
	// a flag ensuring timeout of dynamic processEvents  
	static boolean isDynamicTimeOut = false;
	// timeOut time of dynamic processEvents
	static long dynamicTimeOutTime=Long.MAX_VALUE/3;            //
	// a flag ensuring timeout of static graph load 
	static boolean isStaticLoadTimeOut = false;
	// timeOut time of static graph load 
	static long staticLoadTimeOutTime=Long.MAX_VALUE/3;            // 	
//	// a flag ensuring static statement coverage  
 	//static boolean staticStatementCoverage = false;	
	// a flag ensuring dynamic statement coverage  
	static String timeFileName= "Times"+getProcessIDString()+".txt";
	static String configurationFileName= "Configuration"+getProcessIDString()+".txt";
	static String mazeFileName= "Maze"+getProcessIDString()+".txt";
	static String allQueryResultFileName= "allQueryResult"+getProcessIDString()+".txt";
	static String computationStatusFileName= "ComputationStatus"+getProcessIDString()+".txt";
//	static String staticTimeFileName= "staticTimes"+getProcessIDString()+".txt";
//	static String staticConFigurationFileName= "staticConfiguration"+getProcessIDString()+".txt";//	
	static String staticConfigurations="";
//	static String dynamicConfigurations="";
	static String configurations="";
	static boolean staticUpdated=true;
//	static ArrayList<Long> dynamicTimes=new ArrayList<Long>();
//	static ArrayList<Long> staticTimes=new ArrayList<Long>();
////	static boolean staticFlowSensity = true;	
////	static boolean staticContextSensity = false;	
	static boolean[] staticDynamicSettings=new boolean[6];
//[0] staticContextSensity  [1] staticFlowSensity  	[2] dynamicMethodInstanceLevel [3] dynamicStatementCoverage [4] dynamicStaticGraph  [5]	dynamicMethodEvent	
	/* clean up internal data structures that should be done so for separate dumping of them, a typical such occasion is doing this per test case */
	
	static Qlearner learner  = new Qlearner();
	static long lastProcessTime=0;
	
	//private static final Logger logger=Logger.getLogger(ODDMonitor.class); 
	public synchronized static void resetInternals() {
		preMethod = null;
		g_eventCnt = 0;
		start = false;
		//g_counter = 0;
		span_Queue.clear();
		//All_Queue.clear();
	}
	
	/* initialize the two maps and the global counter upon the program start event */		
	public synchronized static void initialize() throws Exception{
		System.out.println("ODDMonitor initialization 181 ");
		final long startTime = System.currentTimeMillis();
		resetInternals();
		bInitialized = true;
		Thread queryServer1 = new MySocketServer(); 
		queryServer1.start();  
		
		//ODDUtil.initialTimesFile("dynamicTimes.txt", 16 , " ");
		ODDUtil.createOrCopyFile("Times.txt","0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 ",timeFileName);
		ODDUtil.createOrCopyFile("Configuration.txt","111111",configurationFileName);  //001000  110011  001001
		ODDUtil.createOrCopyFile("Maze.txt"," 0 0 0 0 \n 0 0 0 0 \n 0 0 0 0 \n 0 0 0 0 \n 0 0 0 0 \n 0 0 0 0 \n 0 0 0 0 \n 0 0 0 0 \n 0 0 0 0 \n 0 0 0 0 \n 0 0 0 0 \n 0 0 0 0 \n 0 0 0 0 \n 0 0 0 0 \n 0 0 0 0 \n 0 0 0 0 \n",mazeFileName);
		getTimeoutsThresholds(); 	//getTimeouts(); 	
//		configurations=ODDUtil.readLastLine(configurationFileName);
//		staticConfigurations=configurations.substring(0, 2);
//		dynamicConfigurations=configurations.substring(2, 6);
    	getConfigurations(configurationFileName);
		try {
    		if (staticDynamicSettings[2])     // staticGraph
    		{	
    			initialStaticGraph(staticLoadTimeOutTime);
    		}
    		else
    		{
    			initialFunctionList(staticLoadTimeOutTime);
    			ODDUtil.CopyStaticGraph("11");
    		}
   		//System.out.println("ODDImpactAllInOne operationFlag="+ODDImpactAllInOne.operationFlag);
			//System.out.println("initialize()2 dynamicStaticGraph="+dynamicStaticGraph+" dynamicMethodEvent="+dynamicMethodEvent); 
			icAgent = new ODDImpactAllInOne();
			ODDMonitor.setICAgent(icAgent);
	   	    //icAgent.resetImpOPs();

			
			learner.gamma=0.9;
			learner.alpha=0.9;
			learner.epsilon=0.2;
			System.out.println("ODDMonitor initialization takes " + (System.currentTimeMillis() - startTime) + " ms");
			//logger.info("ODDMonitor initialization took " + (System.currentTimeMillis() - startTime) + " ms");
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		if (methodname.indexOf("chord.")>=0 || methodname.indexOf("thrift.")>=0 ) 
		{
			CN_LIMIT= 1000;	
			TIME_SPAN = 900000;
		}
		else if (methodname.indexOf("zookeeper.")>=0 || methodname.indexOf("netty.")>=0) 
		{
			CN_LIMIT= 1000;		
			TIME_SPAN = 900000;
		}		
		else if (methodname.indexOf("voldemort.")>=0) 
		{
			CN_LIMIT= 1000;	
			TIME_SPAN = 900000;
		}		
		try {
			
			Integer smidx = ODDImpactAllInOne.getMethodIdx(methodname);
			if (smidx==null || !ODDImpactAllInOne.isMethodInSVTG(smidx)) {
				return;
			}
			
			span_Queue.add(smidx*-1);
			//g_counter ++;
			//g_counter_queue ++;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			active = false;
		}
	}
	/* the callee could be either an actual method called or a trap */
	public synchronized static void returnInto(String methodname, String calleeName) throws IOException, InterruptedException, ExecutionException{
		//System.out.println("returnInto: "+methodname);
		if (active) return;
		active = true;
		//System.out.println("returnInto(String methodname, String calleeName): "+methodname+" "+calleeName);
		try {
			Integer smidx = ODDImpactAllInOne.getMethodIdx(methodname);
			//System.out.println("returnInto smidx, svtg: "+methodname+" "+smidx+" ODDImpactAllInOne.svtg.edgeSet().size()="+ODDImpactAllInOne.svtg.edgeSet().size()+" ODDImpactAllInOne.svtg.nodeSet().size()="+ODDImpactAllInOne.svtg.nodeSet().size());
			//if (smidx==null || !ODDImpactAllInOne.isMethodInSVTG(smidx)) {
			if (smidx==null) {	
				return;
			}			
			span_Queue.add(smidx);
			//g_counter ++;
			//g_counter_queue++;
			long timeSpan=System.currentTimeMillis()-lastProcessTime;
			//System.out.println("Before g_counter="+g_counter+" g_counter_queue="+g_counter_queue+" timeSpan "+timeSpan +" TIME_SPAN"+TIME_SPAN+" CN_LIMIT "+CN_LIMIT +" CN_LIMIT_QUEUE"+CN_LIMIT_QUEUE);
			boolean ifProcess=(timeSpan>TIME_SPAN) & (span_Queue.size() > CN_LIMIT);
			//boolean ifProcess=!(methodname.indexOf("voldemort.")>=0 || methodname.indexOf(".zookeeper.")>0 || methodname.indexOf(".netty.")>0) & (timeSpan>TIME_SPAN) & ((g_counter > CN_LIMIT && g_counter_queue > CN_LIMIT && All_Queue.size()>=CN_LIMIT) || g_counter_queue > CN_LIMIT_QUEUE);
			//ifProcess= ifProcess || ((methodname.indexOf("voldemort.")>=0 || methodname.indexOf(".zookeeper.")>0 || methodname.indexOf(".netty.")>0) & timeSpan>TIME_SPAN &((g_counter > CN_LIMIT && g_counter_queue > CN_LIMIT && All_Queue.size()>=CN_LIMIT) || g_counter_queue > CN_LIMIT_QUEUE));
			//System.out.println("Before g_counter="+g_counter+" g_counter_queue="+g_counter_queue+" CN_LIMIT "+CN_LIMIT +" CN_LIMIT_QUEUE"+CN_LIMIT_QUEUE);
			if (ifProcess) {
				//System.out.println("After g_counter="+g_counter+" g_counter_queue="+g_counter_queue+" span_Queue.size()"+span_Queue.size()+" All_Queue.size()"+All_Queue.size()+" timeSpan="+timeSpan+" lastProcessTime="+lastProcessTime);
				//logger.info("ODDMonitor After g_counter="+g_counter+" g_counter_queue="+g_counter_queue+" span_Queue.size()"+span_Queue.size()+" All_Queue.size()"+All_Queue.size());
				System.out.println("ODDMonitor timeSpan="+timeSpan+" TIME_SPAN"+TIME_SPAN+" CN_LIMIT "+CN_LIMIT );
//				span_Queue.clear();
//				g_counter=0;
				//span_Queue.addAll(All_Queue);				
				//All_Queue.clear();
//				for (int i=0; i<CN_LIMIT; i++)
//				{
//					if (All_Queue.size()>0)
//					{	
//						span_Queue.add(All_Queue.get(0));
//						g_counter++;
//						All_Queue.remove(0);
//					}
//				}
				processEvents((dynamicTimeOutTime+staticLoadTimeOutTime));
				lastProcessTime=System.currentTimeMillis();
				timeSpan=0;
				
			}
//			if (g_counter_queue > CN_LIMIT_QUEUE) {
//				All_Queue.clear();
//				g_counter_queue=0;
//				span_Queue.clear();
//				g_counter=0;
//			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			active = false;
		}		
		//System.out.println("returnInto methodname: " + methodname+" calleeName: " + calleeName);
        
	}
	
	public synchronized static void terminate(String where) throws Exception {
		if (bInitialized) {
			bInitialized = false;
		}
		else {
			return;
		}
	}
    
	public synchronized static String getImpactSetStr(String query) throws Exception {
		String resultStr="";
		processEvents((dynamicTimeOutTime+staticLoadTimeOutTime));
    	 ODDImpactAllInOne.setQuery(query);
    	 resultStr=icAgent.getDumpImpactSet(query);
    	 //System.out.println("impactSetStr=" + resultStr);
		return resultStr;
	}
	public static void getTimeouts(String configurationFile) throws Exception {
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
		long long1=ODDUtil.readTimeOutFromFile("EventLimit ", configurationFile);
		if (long1>0)  {
			CN_LIMIT= long1;		
			//CN_LIMIT_QUEUE = 5*long1;
		}	
		//System.out.println("long1=" + long1);
		long long2=ODDUtil.readTimeOutFromFile("TimeSpan ", configurationFile);
		if (long2>0)
			TIME_SPAN=long2;
		System.out.println("CN_LIMIT=" + CN_LIMIT + " TIME_SPAN=" + TIME_SPAN);
	}

	public static void getTimeoutsThresholds() throws Exception {
		getThresholds("timeoutsthresholds.txt"); 
		String budgetStr=ODDUtil.readToString("budget.txt").trim().replaceAll("[^\\d]", "");
		if (budgetStr.length()<1) 
			getTimeouts("timeoutsthresholds.txt"); 	
		//System.out.println("getTimeouts() budgetStr=" + budgetStr);
		try
		{			
			long budget=Long.parseLong(budgetStr);											 
	    	long timeStaticLoad=(long)(budget*0.5);
	    	long timeDynamic=(long)(budget*0.5);
	    	//System.out.println("getTimeouts() timeStaticCreate=" + timeStaticCreate +" timeStaticLoad=" + timeStaticLoad+" timeDynamic=" + timeDynamic );
	    	if (budget<10 || timeStaticLoad<5 || timeDynamic<5) {
	    		getTimeouts("timeoutsthresholds.txt");
	    	}
	    	else  {
	    		staticLoadTimeOutTime=timeStaticLoad;
	    		dynamicTimeOutTime=timeDynamic;
	    	}	
	    } catch (Exception e) {  
	    	getTimeouts("timeoutsthresholds.txt");	
        }  

    	System.out.println("getTimeouts() staticLoadTimeOutTime=" + staticLoadTimeOutTime +" timeOutTimeDynamic=" + dynamicTimeOutTime);
    	//logger.info("ODDMonitor getTimeouts() staticLoadTimeOutTime=" + staticLoadTimeOutTime +" timeOutTimeDynamic=" + dynamicTimeOutTime+" staticCreateTimeOutTime=" + staticCreateTimeOutTime);
	}	
	public static void getConfigurations(String configurationFile) throws Exception {
		configurations=ODDUtil.readLastLine(configurationFile);
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
	
	static class MyInitialStaticGraph implements Callable <Boolean>  {
		@Override
        public Boolean call() throws Exception {
			try {
                Thread.sleep(1);      
				long startTime = System.currentTimeMillis();
//	    		File file1=new File("staticVtg.dat");
//	    		if (staticUpdated || !file1.exists()) {  
//	               	Runtime.getRuntime().exec("./ODDStaticGraph.sh>ODDStaticGraph.log");
//	    			//System.out.println("Creating static graph successes"); 
//	        		System.out.println("Creating static graph took "+(System.currentTimeMillis() - startTime)+" ms");
//	    			//logger.info("ODDMonitor Creating static graph took "+(System.currentTimeMillis() - startTime)+" ms");
//	        		staticUpdated=false;
//	            } 
				if (staticUpdated)  {
					ODDUtil.CopyStaticGraph(staticConfigurations);
				}		
	    		String mainClass=ODDUtil.findMainClass();
				if (ODDImpactAllInOne.initializeClassGraph(mainClass,staticDynamicSettings[4]) !=0)  {
					//System.out.println("MyInitialStaticGraph ODDImpactAllInOne.svtg.edgeSet().size()="+ODDImpactAllInOne.svtg.edgeSet().size()+" ODDImpactAllInOne.svtg.nodeSet().size()="+ODDImpactAllInOne.svtg.nodeSet());
					System.out.println("Unable to load satic graph");
	    			//logger.info("ODDMonitor Unable to load satic graph");
					return false;
				}
				else
					System.out.println("Loading static graph successes");      	
           	//return "" + (System.currentTimeMillis() - startTime);
			  } catch (InterruptedException e) {
				  System.out.println("MyInitialStaticGraph is interrupted when calculating, will stop...");
	    			//logger.info("ODDMonitor MyInitialStaticGraph is interrupted when calculating, will stop...");
				  return false; // 
			  }
			return true;
		}		
	}	
	static class MyInitialFunctionList implements Callable <Boolean>  {
		@Override
        public Boolean call() throws Exception {
			try {
                Thread.sleep(1);      
                if (ODDImpactAllInOne.initializeFunctionList(staticDynamicSettings[4]) !=0)  {

    				//System.out.println("MyInitialStaticGraph ODDImpactAllInOne.svtg.edgeSet().size()="+ODDImpactAllInOne.svtg.edgeSet().size()+" ODDImpactAllInOne.svtg.nodeSet().size()="+ODDImpactAllInOne.svtg.nodeSet());
    				System.out.println("Unable to initialize function list");
	    			//logger.info("ODDMonitor Unable to initialize function list");   				
    				return false;
    			}
    			else
    			{	
    				System.out.println("Initialization function list successes");
	    			//logger.info("ODDMonitor Initialization function list successes");    		
    				//System.out.println("initialFunctionList4 dynamicStaticGraph="+dynamicStaticGraph+" dynamicMethodEvent="+dynamicMethodEvent); 
    			}
           	//return "" + (System.currentTimeMillis() - startTime);
			  } catch (InterruptedException e) {
				  System.out.println("MyInitialStaticGraph is interrupted when calculating, will stop...");
	    			//logger.info("ODDMonitor MyInitialStaticGraph is interrupted when calculating, will stop...");    		
				  return false; // 
			  }
			return true;
		}			
	}	
    public synchronized static void initialStaticGraph(long timeOutTime) throws  Exception{
    	MyInitialStaticGraph task0 = new ODDMonitor.MyInitialStaticGraph();
    	ExecutorService executor = Executors.newCachedThreadPool();
        Future<Boolean> future1=executor.submit(task0);

        try {
            if (future1.get(timeOutTime, TimeUnit.MILLISECONDS)) { // 
                System.out.println("initialStaticGraph completes successfully");
    			//logger.info("ODDMonitor initialStaticGraph completes successfully");   		
            }
        } catch (InterruptedException e) {
            System.out.println("initialStaticGraph was interrupted during the sleeping");
			//logger.info("ODDMonitor initialStaticGraph was interrupted during the sleeping"); 
            executor.shutdownNow();
        } catch (ExecutionException e) {
            System.out.println("initialStaticGraph has mistakes during getting the result");
			//logger.info("ODDMonitor initialStaticGraph has mistakes during getting the result");
            executor.shutdownNow();
        } catch (TimeoutException e) {
            System.out.println("initialStaticGraph is timeoouts");
			//logger.info("ODDMonitor initialStaticGraph is timeoouts"); 
            future1.cancel(true);
            // executor.shutdownNow();
            // executor.shutdown();
        } finally {
            executor.shutdownNow();
        }        
    }  
    public synchronized static void initialFunctionList(long timeOutTime) throws  Exception{
    	MyInitialFunctionList task0 = new ODDMonitor.MyInitialFunctionList();
        ExecutorService executor= Executors.newSingleThreadExecutor();
        Future<Boolean> future1=executor.submit(task0);

        try {
            if (future1.get(timeOutTime, TimeUnit.MILLISECONDS)) { // 
                System.out.println("initialFunctionList completes successfully");
                //logger.info("ODDMonitor initialFunctionList completes successfully"); 
            }
        } catch (InterruptedException e) {
            System.out.println("initialFunctionList was interrupted during the sleeping");
            //logger.info("ODDMonitor initialFunctionList was interrupted during the sleeping"); 
            executor.shutdownNow();
        } catch (ExecutionException e) {
            System.out.println("initialFunctionList has mistakes during getting the result");
            //logger.info("ODDMonitor initialFunctionList has mistakes during getting the result"); 
            executor.shutdownNow();
        } catch (TimeoutException e) {
            System.out.println("initialFunctionList is timeoouts");
            //logger.info("ODDMonitor initialFunctionList is timeoouts"); 
            future1.cancel(true);
            // executor.shutdownNow();
            // executor.shutdown();
        } finally {
            executor.shutdownNow();
        }
        //System.out.println("InitialFunctionList timeOutTime="+timeOutTime+" timeOutProcessEvents="+timeOutDynamic); 
    }	
	static class MyProcessEvents implements Callable <Boolean>  {
		@Override
        public Boolean call() throws Exception {
       	long startTime = System.currentTimeMillis();
    	System.out.println("start processing events in the buffer of " + span_Queue.size() + " events...... ");
        //logger.info("ODDMonitor start processing events in the buffer of " + span_Queue.size() + " events...... ");
    	//System.out.println("g_counter="+g_counter+" g_counter_queue="+g_counter_queue+" span_Queue.size()"+span_Queue.size()+" All_Queue.size()"+All_Queue.size());
		try {
			getTimeoutsThresholds(); 	 	
			getConfigurations(configurationFileName);
        	//System.out.println("g_counter="+g_counter+" g_counter_queue="+g_counter_queue+" span_Queue.size()"+span_Queue.size()+" All_Queue.size()"+All_Queue.size());
 	//[0] staticContextSensity  [1] staticFlowSensity  	[2] dynamicMethodInstanceLevel [3] dynamicStatementCoverage [4] staticGraph  [5]	dynamicMethodEvent	
            //staticDynamicSettings[2] staticGrap only for static graph existing
        	//[0]staticContextSensity  [1]staticFlowSensity  	[2]staticGraph     [3]dynamicMethodEvent	 [4]dynamicStatementCoverage  [5]dynamicMethodInstanceLevel	        	
        	//  Adaptation  
        	File file = new File("static.dat");  
            if (!file.exists()) {  
            	staticDynamicSettings[2]=false;
            }  
            
            //System.out.println("staticDynamicSettings[5]="+staticDynamicSettings[5]+" before FirstLast span_Queue.size()=" + span_Queue.size());
            List<Integer> work_Queue = new LinkedList<Integer>();
            if (staticDynamicSettings[5])                          //dynamicMethodInstanceLevel
    		{
            	work_Queue=ODDUtil.returnFirstLastEvents(span_Queue);
            	//System.out.println("After FirstLast span_Queue.size()=" + span_Queue.size());
    		}
            else
            	work_Queue.addAll(span_Queue);
        	//System.out.println("After span_Queue.size()=" + span_Queue.size());
        	if (staticDynamicSettings[4])                         //dynamicStatementCoverage
    			ODDImpactAllInOne.prunedByStmt("");
    		//System.out.println("ODDMonitor MyProcessEvents after staticDynamicSettings[3]");
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
        	String allQuestResults="";
    		if (staticDynamicSettings[2])  {    		        //dynamicStaticGraph
    			//icAgent.dumpAllImpactSets();
    			System.out.println("ODDMonitor "+icAgent.getDumpAllImpactSetsSize());  
    			//logger.info("ODDMonitor "+icAgent.getDumpAllImpactSetsSize());    
    			allQuestResults=icAgent.getDumpAllImpactSets();
    		}
    		else  {
    			//icAgent.dumpAllImpactSetsWithoutStatic();
    			System.out.println("ODDMonitor "+icAgent.getDumpAllImpactSetsSizeWithoutStatic());      			
    			//logger.info("ODDMonitor "+icAgent.getDumpAllImpactSetsSizeWithoutStatic());        			  
    			allQuestResults=icAgent.getDumpAllImpactSetsWithoutStatic();
    		}
    		ODDUtil.writeStringToFile(allQuestResults, allQueryResultFileName);
    		System.out.println();
    		//g_counter = 0;
    		//System.out.println("ODDMonitor "+icAgent.getDumpAllImpactSetsSize());  
//    		List<Integer> newList = ODDUtil.returnDiffEvents(old_span_Queue,span_Queue);
//    		System.out.println("newList.size()="+newList.size()+" All_Queue.size()="+All_Queue.size()+" old_span_Queue..size()="+old_span_Queue.size()+" span_Queue.size()="+span_Queue.size());      
//    		All_Queue.addAll(newList);    		
    		old_span_Queue.clear();
    		old_span_Queue.addAll(span_Queue);
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
		  } catch (Exception e) {
			  System.out.println("MyProcessEvents is interrupted when calculating, will stop...");
    			//logger.info("ODDMonitor MyProcessEvents is interrupted when calculating, will stop...");  
			  return false;
		  }	    		
		return true;
	}			
}	

    public synchronized static long processEvents(long timeOutTime) throws  Exception{
    	final long startTime = System.currentTimeMillis();
  
		//configurations=ODDUtil.readLastLine(conFigurationFileName);
//		staticConfigurations=configurations.substring(0, 2);
//		dynamicConfigurations=configurations.substring(2, 6);
    	//getConfigurations(conFigurationFileName);
    	if	(staticUpdated)	
    	    {	
			if (staticDynamicSettings[2])
			{	
				initialStaticGraph(staticLoadTimeOutTime);
			}
			else
			{
				initialFunctionList(staticLoadTimeOutTime);
			}
	    }
    	//logger.info("ODDMonitor ProcessEvents after staticUpdated");
    	
//    	MyProcessEvents task1 = new ODDMonitor.MyProcessEvents();
//        ExecutorService executorService= Executors.newSingleThreadExecutor();
//        Future<String> future1=executorService.submit(task1);
//        long resultL=(long) 0;
//        try{
//            String result=future1.get(timeOutTime,TimeUnit.MILLISECONDS);
//            System.out.println("ProcessEvents took "+result+" ms");
//            isDynamicTimeOut = false;            
//            //dynamicStatementCoverage = false;  
//            resultL=Long.parseLong(result.replaceAll("[^0-9]",""));
//        }
//        catch (TimeoutException e){
//            System.out.println("Timeout!"); 
//            isDynamicTimeOut = true;            
//            //dynamicStatementCoverage = true;
//        }
//        finally  {
//        	future1.cancel(true);
//        }       
    	//MyProcessEvents();
		diff_Queue = ODDUtil.returnDiffEvents(old_span_Queue,span_Queue);
		System.out.println("diff_Queue.size()="+diff_Queue.size()+" All_Queue.size()="+All_Queue.size()+" old_span_Queue..size()="+old_span_Queue.size()+" span_Queue.size()="+span_Queue.size());      
		All_Queue.addAll(diff_Queue); 
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
        //System.out.println("ProcessEvents g_counter="+g_counter+" g_counter_queue="+g_counter_queue+" span_Queue.size()"+span_Queue.size()+" All_Queue.size()"+All_Queue.size());
        long resultL=System.currentTimeMillis()-startTime; 
        System.out.println("Event computation took " + resultL + " ms");
        
		//logger.info("ODDMonitor Event computation took " + resultL + " ms");
     
        //dynamicTimes=ODDUtil.updatedTimeFromConfigurationForced(dynamicConFigurationFileName, dynamicTimeFileName, resultL);
        ODDUtil.updateTimeFromConfigurationToFileForced(configurationFileName, timeFileName, resultL);        
        //System.out.println("ODDUtil.updateTimeFromConfigurationToFileForced(configurationFileName, timeFileName, resultL);");      
		//logger.info("ODDMonitor updateTimeFromConfigurationToFileForced configurationFileName="+configurationFileName+" timeFileName="+timeFileName+" resultL"+resultL);
        ODDUtil.writeStringToFileAppend(configurations+" Event computation time: "+ resultL + " ms\n", "TimeCosts.txt");
        
        
        ODDUtil.saveRewardPenaltyfromFiles(mazeFileName, timeFileName, staticLoadTimeOutTime, dynamicTimeOutTime, staticDynamicSettings[2], staticUpdated);
        //System.out.println("ODDUtil.saveRewardPenaltyfromFiles(");
        //logger.info("ODDUtil.saveRewardPenaltyfromFiles(");
        //dynamicConfigurations=ODDController.updatedConfigurationFromTimesFile(dynamicConFigurationFileName, dynamicTimeFileName, dynamicTimeOutTime,4);
        String oldStaticConfigurations=ODDUtil.readToString(configurationFileName).substring(0,2);
        //System.out.println("oldStaticConfigurations: " + oldStaticConfigurations);
        //logger.info("ODDMonitor oldStaticConfigurations: " + oldStaticConfigurations);
        //ODDController.updateConfigurationFromTimesFile(configurationFileName, timeFileName, dynamicTimeOutTime,6);
        
        // With or without adaptation
        ODDController.setNextConfigurationInFile(learner, mazeFileName, configurationFileName);        
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
        	//ODDUtil.writeStringToFile(staticConfigurations, "staticConfiguration.txt");        	//[0]staticContextSensity  [1]staticFlowSensity  	[2] dynamicMethodInstanceLevel [3] dynamicStatementCoverage [4] dynamicStaticGraph  [5]	dynamicMethodEvent       	
        	if (staticDynamicSettings[4])  {
//        		final long createStartTime = System.currentTimeMillis();
//        		createStaticGraph(staticCreateTimeOutTime);
//        		System.out.println("Creating Static Graph took "+ (System.currentTimeMillis() - createStartTime)+" ms");
                //logger.info("ODDMonitor Creating Static Graph took "+ (System.currentTimeMillis() - createStartTime)+" ms");
        	}	
        }
        
        //ODDUtil.saveRewardPenaltyfromFiles(mazeFileName, timeFileName, staticLoadTimeOutTime, dynamicTimeOutTime, staticDynamicSettings[4]);
        //System.out.println("ODDUtil.saveRewardPenaltyfromFiles(mazeFileName, timeFileName, staticLoadTimeOutTime, dynamicTimeOutTime, staticDynamicSettings[4]);");
     
        return (System.currentTimeMillis() - startTime);
    }

//	static class MyCreateStaticGraph implements Callable <Boolean> {
//		@Override
//        public Boolean call() throws Exception {
//			try {
//                Thread.sleep(1000);          
//	           	//long startTime = System.currentTimeMillis();
//	           	Runtime.getRuntime().exec("./ODDStaticGraph.sh");
//				System.out.println("Creating static graph successes");         
//                //logger.info("ODDMonitor Creating static graph successes");  	
//           	//return "" + (System.currentTimeMillis() - startTime);
//			  } catch (InterruptedException e) {
//				  return false; // 
//			  }
//			return true;
//		}
//	}	
//	
//    public synchronized static void createStaticGraph(long timeOutTime) throws  Exception{
//
//        ExecutorService executor = Executors.newCachedThreadPool();
//        MyCreateStaticGraph task1 = new ODDMonitor.MyCreateStaticGraph();
//        Future<Boolean> f1 = executor.submit(task1);
//        try {
//            if (f1.get(staticCreateTimeOutTime, TimeUnit.MILLISECONDS)) { // 
//                System.out.println("createStaticGraph complete successfully");        
//                //logger.info("ODDMonitor Creating static graph successes");  
//            }
//        } catch (InterruptedException e) {
//            System.out.println("createStaticGraph was interrupted during the sleeping");        
//            //logger.info("ODDMonitor Creating static graph successes");  
//            executor.shutdownNow();
//        } catch (ExecutionException e) {
//            System.out.println("createStaticGraph has mistakes during getting the result");        
//            //logger.info("ODDMonitor Creating static graph successes");  
//            executor.shutdownNow();
//        } catch (TimeoutException e) {
//            System.out.println("createStaticGraph is timeoouts");        
//            //logger.info("ODDMonitor Creating static graph successes");  
//            f1.cancel(true);
//            // executor.shutdownNow();
//            // executor.shutdown();
//        } finally {
//            executor.shutdownNow();
//        }
//        //System.out.println("InitialStaticGraph timeOutTime="+timeOutTime+" timeOutProcessEvents="+timeOutDynamic); 
//    }  
	public static String getProcessIDString() {
		return ManagementFactory.getRuntimeMXBean().getName().replaceAll("[^a-zA-Z0-9]", "");
	}
	
	static class MyCreateStaticGraph implements Callable <Boolean> {
		@Override
        public Boolean call() throws Exception {
			try {
				Thread.sleep(1); 
				HashSet eventMethods=ODDUtil.getMethodNameSet(All_Queue, ODDImpactAllInOne.idx2method);
				System.out.println("eventMethods="+eventMethods);
				StaticTransferGraph createdVtg=createVTGWithIndus(true, staticDynamicSettings[1], staticDynamicSettings[0], eventMethods);
				if (createdVtg ==null)  {
					System.out.println("Unable to create satic graph");
					return false;
				}
				System.out.println("createdVtg="+createdVtg);
				String mainClass=ODDUtil.findMainClass();
				if (ODDImpactAllInOne.initializeClassGraphCreated(createdVtg, mainClass, staticDynamicSettings[4] ) !=0)  { 
					//System.out.println("MyInitialStaticGraph ODDImpactAllInOne.svtg.edgeSet().size()="+ODDImpactAllInOne.svtg.edgeSet().size()+" ODDImpactAllInOne.svtg.nodeSet().size()="+ODDImpactAllInOne.svtg.nodeSet());
					System.out.println("Unable to load satic graph");
	    			//logger.info("ODDMonitor Unable to load satic graph");
					return false;
				}
				else
					System.out.println("Loading static graph successes");      	
				
			  } catch (InterruptedException e) {
				  System.out.println("MyInitialStaticGraph is interrupted when calculating, will stop...");
	    			//logger.info("ODDMonitor MyInitialStaticGraph is interrupted when calculating, will stop...");
				  return false; // 
			  }
			return true;
		}
	}	
	

    public synchronized static void createStaticGraph(long timeOutTime) throws  Exception{

        ExecutorService executor = Executors.newCachedThreadPool();
        MyCreateStaticGraph task1 = new ODDMonitor.MyCreateStaticGraph();
        Future<Boolean> f1 = executor.submit(task1);
        try {
            if (f1.get(staticCreateTimeOutTime, TimeUnit.MILLISECONDS)) { // 
                System.out.println("createStaticGraph complete successfully");        
                //logger.info("ODDMonitor Creating static graph successes");  
            }
        } catch (InterruptedException e) {
            System.out.println("createStaticGraph was interrupted during the sleeping");        
            //logger.info("ODDMonitor Creating static graph successes");  
            executor.shutdownNow();
        } catch (ExecutionException e) {
            System.out.println("createStaticGraph has mistakes during getting the result");        
            //logger.info("ODDMonitor Creating static graph successes");  
            executor.shutdownNow();
        } catch (TimeoutException e) {
            System.out.println("createStaticGraph is timeoouts");        
            //logger.info("ODDMonitor Creating static graph successes");  
            f1.cancel(true);
            // executor.shutdownNow();
            // executor.shutdown();
        } finally {
            executor.shutdownNow();
        }
        //System.out.println("InitialStaticGraph timeOutTime="+timeOutTime+" timeOutProcessEvents="+timeOutDynamic); 
    }  
    
	private static StaticTransferGraph createVTGWithIndus(boolean flagIndus, boolean flowS, boolean contextS, HashSet eventMethods) {
		StaticTransferGraph vtg = new StaticTransferGraph();
		StaticTransferGraph vtg2 = new StaticTransferGraph();
		try {
			final long startTime = System.currentTimeMillis();
			//if (0==vtg.buildGraph(opts.debugOut())) return 0;
//
//			vtg.setFlowSensitivity(flowS);	
//			vtg.setContextSensitivity(contextS);	
//			vtg.setHeFlowSens(flowS);	
			if (eventMethods.size()>1)
			{
				vtg.buildGraph(false,flowS, contextS, eventMethods);
			}
			else
				vtg.buildGraph(false, flowS, contextS);
			System.out.println("vtg before Indus");
			System.out.println(vtg);
			final long AddEdgesWithIndusstartTime = System.currentTimeMillis();
			System.out.println("	createVTGWithIndus_buildGraph  took " + (AddEdgesWithIndusstartTime - startTime) + " ms");
			if (flagIndus)
			{
				vtg2=vtg;				
			}
			else
			{	
				try {
					vtg2=AddEdgesWithIndus(vtg);
				} catch (Exception e)  
				{
					vtg2=vtg;
				}
				
	
//				System.out.println("vtg2 after Indus");
//				System.out.println(vtg2);
				//vtg.AddThreadEdge(opts.debugOut(),"Dependence.log");
				final long AddEdgesWithIndusstopTime = System.currentTimeMillis();
				System.out.println("	createVTGWithIndus_AddEdgesWithIndus took " + (AddEdgesWithIndusstopTime - AddEdgesWithIndusstartTime) + " ms");
			}

			System.out.println("vtg2 after Indus");
			System.out.println(vtg2);
			//vtg.AddThreadEdge(opts.debugOut(),"Dependence.log");
			final long AddEdgesWithIndusstopTime = System.currentTimeMillis();
			System.out.println("	createVTGWithIndus_AddEdgesWithIndus took " + (AddEdgesWithIndusstopTime - AddEdgesWithIndusstartTime) + " ms");
//			final long serializeVTGstartTime = System.currentTimeMillis();
//			System.out.println("vtg2 serialization");
//			System.out.println(vtg2);
//			// DEBUG: test serialization and deserialization
////			{
////				String fn = dua.util.Util.getCreateBaseOutPath() + "staticVtg.dat";
////				if ( 0 == vtg2.SerializeToFile(fn) ) {
////					//if (opts.debugOut()) 
////					{
////						System.out.println("======== VTG successfully serialized to " + fn + " ==========");
////					}
////				} // test serialization/deserialization
////			} // test static VTG construction
//			final long stopTime = System.currentTimeMillis();
//			System.out.println("	createVTGWithIndus_serializeVTG took " + (stopTime - serializeVTGstartTime) + " ms");
	

		}
		catch (Exception e) {
			System.out.println("Error occurred during the construction of VTG");
			e.printStackTrace();
		}
		
		System.out.println(vtg2);
		final long serializeVTGstartTime = System.currentTimeMillis();
		System.out.println("vtg2 serialization");
		System.out.println(vtg2);
		// DEBUG: test serialization and deserialization
		{
			String fn = dua.util.Util.getCreateBaseOutPath() + "staticVtg.dat";
			if ( 0 == vtg2.SerializeToFile(fn) ) {
				//if (opts.debugOut()) 
				{
					System.out.println("======== VTG successfully serialized to " + fn + " ==========");
//					StaticTransferGraph g = new StaticTransferGraph();
//					if (null != g.DeserializeFromFile (fn)) {
//						System.out.println("======== VTG loaded from disk file ==========");
//						//g.dumpGraphInternals(true);
//						System.out.println(g);
//					}
				}
			} // test serialization/deserialization
		} // test static VTG construction
		final long stopTime = System.currentTimeMillis();
		System.out.println("	createVTGWithIndus_serializeVTG took " + (stopTime - serializeVTGstartTime) + " ms");
		return vtg2;
	} // -- createVTG
	private static StaticTransferGraph AddEdgesWithIndus(StaticTransferGraph svtg) {
		Object[][] _dasOptions = {//				
				{"sda", "Synchronization dependence", new SynchronizationDA(svtg)},
				{"frda1", "Forward Ready dependence v1", ReadyDAv1.getForwardReadyDA(svtg)},
				{"ida1", "Interference dependence v1", new InterferenceDAv1(svtg)},
				};
		DependencyXMLizerCLI _xmlizerCLI = new DependencyXMLizerCLI();
		_xmlizerCLI.xmlizer.setXmlOutputDir("/tmp");
		_xmlizerCLI.dumpJimple = false;
		_xmlizerCLI.useAliasedUseDefv1 = false;
		_xmlizerCLI.useSafeLockAnalysis = false;
		_xmlizerCLI.exceptionalExits = false;
		_xmlizerCLI.commonUncheckedException = false;
		final List<String> _classNames = new ArrayList<String>();
		for (SootClass sClass:Scene.v().getApplicationClasses()) 
		{
			_classNames.add(sClass.toString());
		}	
		if (_classNames.isEmpty()) {
			System.out.println("Please specify at least one class.");
		}
		System.out.println("_classNames="+_classNames);
		_xmlizerCLI.setClassNames(_classNames);
		if (!_xmlizerCLI.parseForDependenceOptions(_dasOptions,_xmlizerCLI)) {
			System.out.println("At least one dependence analysis must be requested.");
		}
		System.out.println("_xmlizerCLI.das.size(): " + _xmlizerCLI.das.size());
		
		_xmlizerCLI.<ITokens> execute();
		System.out.println("svtg");
		System.out.println(svtg);
		return svtg;
	}
	
} 

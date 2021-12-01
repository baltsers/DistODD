package ODD;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MyQueriesThread extends Thread {
    
	String hostName="localhost";
	int portNum = 2000;
	String inputMsg="";
    String resultS="";
    boolean isValid=true;
    public static String allResult ="";
    public static HashMap<String,HashSet<String>> resultMap = new HashMap<String,HashSet<String>>();
    public MyQueriesThread(String hostName, int portNum, String inputMsg)
    {
    	this.hostName = hostName;
    	this.portNum = portNum;
        this.inputMsg = inputMsg;
    }
    
    @Override
    public void run() { 

    	Map< String, Integer > method2idx=new HashMap< String, Integer >();
    	Map< Integer, String > idx2method=new HashMap< Integer, String >();
	       try {
	   		FileReader reader = null;      
	        BufferedReader br = null;    
	        reader = new FileReader("functionList.out");   
	        br = new BufferedReader(reader);
	        String str = "";  
	        String mname="";
	        int index = 0;
	        while((str = br.readLine()) != null)
	        {  
	        	mname=str.trim();
				if (!method2idx.containsKey(mname)) {
					method2idx.put(mname, index);
					idx2method.put(index, mname);
					index ++;
				}
	        }        

	       } catch (Exception e) {  
	           e.printStackTrace(); 
	       } 
	    	System.out.println("The client is connecting port " + portNum); 
	    	// timeOut time of dynamic processEvents
	    	long timeOutTimeDynamic=Long.MAX_VALUE;            //
			//long long2=ODDUtil.readTimeOutFromFile("Dynamic TimeOut:", "dynamicConfigurations.txt");
//			if (long2>0)
//				timeOutTimeDynamic=long2;
			
	    	ArrayList querys=new ArrayList();
//	    	if (inputMsg.indexOf("<")>=0 && inputMsg.indexOf(">")>=0 && inputMsg.indexOf(": ")>0  && inputMsg.indexOf("(")>0 && inputMsg.indexOf(")")>0)
//	    	{
//	    		 querys.add(inputMsg);
//	    	}
//	    	else
	    	//querys = ODDUtil.getFindList(ODDUtil.getArrayList("queries.txt"), inputMsg);
	    	querys.add(inputMsg); //ODDUtil.getArrayList("queries.txt");
	    	System.out.println("querys.size()="+querys.size()+" querys="+querys+" inputMsg="+inputMsg);
	    	if (querys.size()<1)
	    	{	
	    		isValid=false;
	    		//System.out.println("In Run() isValid="+isValid);
	    		return;
	    	}	
	        //System.out.println("querys=" + querys);
	        String query="";
	    	try 
	    	{
	    		for(int i=0;i<querys.size();i++ ){
	    			allResult="";
	    			query=querys.get(i).toString();
	    			final long startTime = System.currentTimeMillis();		    			
		            AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
		            InetSocketAddress hostAddress = new InetSocketAddress(hostName, portNum);
		            Future future = client.connect(hostAddress);
		            future.get();
		            byte [] message = new String(query).getBytes();
		            ByteBuffer buffer = ByteBuffer.wrap(message);
		            Future result = client.write(buffer);
		            future.get();
		            long timeCount=0;
		            while (! result.isDone() && timeCount<=timeOutTimeDynamic) {
	        			try {
							Thread.sleep(1000);
							timeCount+=1000;
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
							break;
						}
	        			if (timeCount>timeOutTimeDynamic)
	        				break;
		            }
		            
		            buffer.flip();
		            System.out.println("Sent query: " + query + " To " + hostName+ " : " + portNum);
		            ByteBuffer buffer2 = ByteBuffer.allocate(1024*1024*1024);
		            Future result2 = client.read(buffer2);
		            result2.get();
		            timeCount=0;
		            while (! result2.isDone()) {
		            	try {
							Thread.sleep(1000);
							timeCount+=1000;
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
							break;
						}
	        			if (timeCount>timeOutTimeDynamic)
	        				break;
		            }
		            
		            String message2 = new String(buffer2.array()).trim();
		            //ODDUtil.writeStringToFile(message2, "Message2Query"+ portNum+".txt");
//		            String firstEventS=ODDToolUtil.getFirstEventStr(message2);
//		            String lastEventS=ODDToolUtil.getLastEventStr(message2);
//		            System.out.println("firstEventS: " + firstEventS);
//		            System.out.println("lastEventS: " + lastEventS);
		            resultS=ODDToolUtil.getQueryDependenceSetStr(message2, query);	
		            System.out.println("Query: " + query + " dependency set from " + hostName+ " : " + portNum+"\n");

		            //System.out.println(resultS);
		            //resultS=message2;	    		
			    	HashSet<String> onePortImpactSet = new HashSet<>();
			        onePortImpactSet=ODDToolUtil.getQuerySetFromDir(method2idx,idx2method, query);   //ODDUtil.getSetFromImpactsetStr(resultS, ";");		
	        		//System.out.println("onePortImpactSet="+onePortImpactSet);       
			        resultMap.put(query, onePortImpactSet);      	
			        //if (resultS!=null && resultS.length()>12)
			        allResult += ODDToolUtil.getQueryImpactSetSizes(onePortImpactSet, query); //resultS+"\n";		    		
	        		//System.out.println("Query="+query+" resultS: "+resultS+" onePortImpactSet "+onePortImpactSet+" allResult="+allResult+" resultMap="+resultMap);   
	        		System.out.println("Query="+query+" allResult="+allResult);        
			        ODDUtil.writeStringToFileAppend(allResult, "allResultSizes.txt");
			        long resultL=System.currentTimeMillis()-startTime; 
			        ODDUtil.writeStringToFileAppend(""+resultL, "allResultTimes.txt");
//					try {
//				    	String budgetStr=ODDUtil.readToString("budget.txt").trim().replaceAll("[^\\d]", "");
//				    	long budget=1000;
//						if (budgetStr.length()>1) 
//							budget=Long.parseLong(budgetStr);	
//						Thread.sleep((long) (Math.random() * 10000 + budget));
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						//e.printStackTrace();
//					}
	        	}	
	    		//System.out.println("Querys allResults:\n"+allResult+" resultMap="+resultMap); 
	    		
	    		isValid=true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//isValid=false;
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			//isValid=false;
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			//isValid=false;
			e.printStackTrace();
		}

    }
    
    public String getResultS() {
        return this.resultS;
    }
    public String getAllResult() {
        return this.allResult;
    }
    public HashMap<String,HashSet<String>> getResultMap() {
    	//System.out.println("getResultMap()="+this.resultMap);        
        return this.resultMap;
    }
    public boolean getIsValid() {
        return this.isValid;
    }
}

public class ODDQueries {	
static HashMap<String,HashSet<String>> resultMap = new HashMap<String,HashSet<String>>();
static String allResult="";
protected static Map< String, Integer > method2idx;
protected static Map< Integer, String > idx2method;
static float averageSize111111=(float) 0.0;

public static boolean isHostConnectable(String host, int port) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port));
        } catch (IOException e) {
            //e.printStackTrace();
        	System.out.println("The network address "+host+":"+port+" cannot be connected!" );
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
        return true;
    }
public static boolean isHostConnectable(String hostPort) {
    Socket socket = new Socket();
    
    try {
    	String[] hostPorts=hostPort.split(":");
    	if (hostPorts.length>1)
    	{	
        	String host=hostPorts[0];        	
        	int port=Integer.parseInt(hostPorts[1]);
            socket.connect(new InetSocketAddress(host, port));
    	}
    	else
    		return false;
    } catch (IOException e) {
        //e.printStackTrace();
    	System.out.println("The network address "+hostPort+" cannot be connected!" );
        return false;
    } finally {
        try {
            socket.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }
    return true;
}
public static void main(String[] args) throws NumberFormatException, IOException, InterruptedException {
	String hostNames="localhost,";
	//String hostName="localhost";
	String ports = "2000,";
	if (args.length > 0) {
		hostNames  = args[0];
	}
	if (args.length > 1) {
		ports  = args[1];
	}
	if (hostNames.length()<1 || hostNames.equals("localhost,") || hostNames.equals("localhost") || hostNames.equals("127.0.0.1,") || hostNames.equals("127.0.0.1") ) {
		hostNames="localhost,localhost,localhost,localhost,localhost,localhost,localhost,localhost,localhost,localhost";
	}
	if (ports.length()<1 || ports.equals("2000,")) {
		ports="2000,2001,2002,2003,2004,2005,2006,2007,2008,2009";
	}
	else if (ports.equals("3000,")) {
		ports="3000,3001,3002,3003,3004,3005,3006,3007,3008,3009";
	}
	//System.out.println("hostNames="+hostNames+" ports="+ports);
	String[] hostlist = hostNames.split(",");
	String[] portlist = ports.split(",");
//    String inputMsg ="";  
//    String returnMsg ="";
//    String allMsg ="";
//	for(int i=0;i<hostlist.length;i++ ){
//		System.out.println("hostlist["+i+"]="+hostlist[i]);
//	}	
//
//	for(int i=0;i<portlist.length;i++ ){
//		System.out.println("portlist["+i+"]="+portlist[i]);
//	}	
    

	initializeFunctionList();
//	long long2=ODDUtil.readTimeOutFromFile("Dynamic TimeOut:", "dynamicConfigurations.txt");
//	if (long2>0)
//		timeOutTimeDynamic=long2;
	ArrayList<String> validAddresses=ODDUtil.getConnectedAddresses(hostlist,portlist);
	Process process=Runtime.getRuntime().exec("./RMALL.sh");	
	process.waitFor();	
	handleAMsgsInFile("queries.txt", validAddresses);
}
public static void handleAMsgsInFile(String queryFile, ArrayList<String> validAddresses) {
    FileReader reader = null;  
    BufferedReader br = null;      
    Long totalSize=(long)0;
    Long totalTime=(long)0;
    int count=0;
    String configurations=ODDUtil.readLastLine(ODDUtil.getNewestFile ("", "Configuration"));		//? ODDUtil.getNewestFile(dirName, fileString)
    try {  

        reader = new FileReader(queryFile);  
        br = new BufferedReader(reader);              
        String str = null; 
        String resultLineStr="";
        while ((str = br.readLine()) != null) {  
        	//System.out.println("str="+str);
        	if (str.length()>1)
        	{
        		long[] midLs= {(long)0,(long)0};
        		midLs=handleAMsg(str, validAddresses);
        		if (midLs[0]>0  && midLs[1]>0) {
        			count++;
        			totalSize+=midLs[0];
        			totalTime+=midLs[1];
        			resultLineStr="Query "+str+" size="+midLs[0]+", took "+midLs[1]+" ms with the configuration"+configurations+"\n";
        			ODDUtil.writeStringToFileAppend(resultLineStr, "batchResults.txt");
        			ODDUtil.writeStringToFile(""+midLs[1], "TimeCost"+getProcessIDString()+".txt");
        		}
				try {
			    	String budgetStr=ODDUtil.readToString("budget.txt").trim().replaceAll("[^\\d]", "");
			    	long budget=1000;
					if (budgetStr.length()>1) 
						budget=Long.parseLong(budgetStr);	
					Thread.sleep((long) (Math.random() * 1000 + budget));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
        		
        	}
        }     
        br.close();  
        reader.close(); 
    } catch (Exception e) {  
        e.printStackTrace();                  
    }
    if (count>0)  {
    	float averageSize=(float)totalSize/count;
    	float averageTime=(float)totalTime/count;
    	String resultStr="For the configuration"+configurations+", query number is "+count+", total size is "+totalSize+", total time is "+totalTime+", average size is "+averageSize+", average time is "+averageTime+"\n";
    	System.out.println(resultStr);
    	ODDUtil.writeStringToFileAppend(resultStr, "batchResults.txt");	
    	if (configurations==("111111"))
    		averageSize111111=averageSize;
    	
    	//        	System.out.println("Query number is : "+count);
//    	System.out.println("Average size is : "+averageSize);
//    	System.out.println("Average time is : "+averageTime);
    	Date date = new Date();
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateS = " "+sdf.format(date);
    	double utilization=0.0;
    	
    	String budgetStr=ODDUtil.readToString("budget.txt").trim().replaceAll("[^\\d]", "");
    	long budget=1000;
		if (budgetStr.length()>1) 
			budget=Long.parseLong(budgetStr);	
    	if (budget>0)
    		utilization=(double)averageTime/budget;
//		if (utilization>1.0)
//			utilization=1.0;
    	System.out.println("D resultL="+averageTime+" budget="+ budget+" utilization= " + utilization);
    	
    	double precision=0.0;
    	if (configurations=="111111") {
    		precision=1.0;
    	}
    	else {
    		//double getPrecision(String result111, String result, ArrayList queries, String resultPath, String resultFile, String queriesFile)
    		
    		if (averageSize111111>=1 && averageSize>=1)  {
    			precision=(double)averageSize111111/averageSize;
    		}	
    		else {
    			precision=ODDUtil.getSetSize2FromBatchResult("batchResults.txt"); 
    			if  (precision==0.0)
    				precision=ODDUtil.getPrecision();
    		}    			
    		if (precision>1.0)
    			precision=1.0;
    	}	
    	System.out.println("D precision= " + precision);
    		//double pre=getPrecision("", "", queries, "/Research/tp/", "allQueryResultOther.txt", ""); 
    	if (utilization>0.5 && precision>0.5)
    		ODDUtil.writeStringToFileAppend(""+System.currentTimeMillis()+dateS+" Utilization="+ utilization+" Precision="+ precision+ " \n", "DeepLearningLogs.txt");
    	
    }
}
public static String getProcessIDString() {
	return ManagementFactory.getRuntimeMXBean().getName().replaceAll("[^a-zA-Z0-9]", "");
}
public static long[] handleAMsg(String inputMsg, ArrayList<String> validAddresses) throws IOException, InterruptedException  {
	long[] resultLs= {(long)0,(long)0};
//	Process process3=Runtime.getRuntime().exec("./RMMESSAGE2.sh");	
//	process3.waitFor();	
    long startTime = System.currentTimeMillis();
    Thread threads[]=new Thread[10];   
    boolean[] hasGetResult=new boolean[10];
    boolean[] valid=new boolean[10];
    String returnMsg ="";
    String allMsg ="";
    //boolean[] connected=new boolean[10];
    int validLength=validAddresses.size();               
	for (int i=0; i<validLength; i++) { 
		try
		{
	        String oneAddress ="";
	        String oneHost ="";
	        String onePort ="";    
			oneAddress=validAddresses.get(i).toString().trim();
			String[] oneAddresses=oneAddress.split(":");
	    	if (oneAddresses.length<2)
	    	{	
	    		continue;
	    	}
	    	oneHost=oneAddresses[0].trim();        	
	    	onePort=oneAddresses[1].trim();
            threads[i]  = (Thread) new MyQueriesThread(oneHost,Integer.parseInt(onePort),inputMsg);  
            threads[i].start();		                   
            //System.out.println("in for i="+i+" Before valid[i]"); 
            valid[i]=((MyQueriesThread) threads[i]).getIsValid();	  
            //connected[i]=true;        			
			 hasGetResult[i]=false;
            //System.out.println("in for i="+i+" valid[i]="+valid[i]);
        } catch (Exception e) {  
          //e.printStackTrace(); 
        	valid[i]=false;
        	continue;
        }          
		//System.out.println("i="+i+" valid[i]="+valid[i]);
	}    
	boolean isAllFalse=ODDUtil.isAllFalse(valid,validLength); // || ODDUtil.isAllFalse(connected,portlistlength);
	boolean isGetAllResult=ODDUtil.isAllTwoTrue(valid, hasGetResult, validLength);
	//System.out.println("isAllFalse="+isAllFalse+" isGetAllResult="+isGetAllResult);
	Runtime.getRuntime().exec("rm Message2Query*.txt -f");
	//while (!isAllFalse && !isGetAllResult)
	{
		for (int i=0; i<validLength; i++) { 
			if (!valid[i])
				break;
			try
			{
        		returnMsg=((MyQueriesThread) threads[i]).getResultS();
        		if (returnMsg.length()>1)  {
        			hasGetResult[i]=true;
        		}
        		else
        			continue;
        		//System.out.println("returnMsg: "+returnMsg);
        		allMsg=((MyQueriesThread) threads[i]).allResult;
        		//System.out.println("allMsg:"+allMsg);
        		//System.out.println("Old allResult="+allResult);
        		allResult+=allMsg;
        		//System.out.println("New allResult="+allResult);
        		HashMap<String,HashSet<String>> oneMap = ((MyQueriesThread) threads[i]).resultMap;
        		//System.out.println("Old resultMap="+resultMap);
        		//System.out.println("oneMap="+oneMap);
        		resultMap=ODDUtil.mergeResultMap(resultMap,oneMap);	     
        		//System.out.println("New resultMap="+resultMap);

	        } catch (Exception e) {  
              //e.printStackTrace();      
	        	continue;
            }          			

		}
//		try {
//			Thread.sleep((long) (Math.random() * 10000 + 10000));
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//		}
//		for (int i=0; i<validLength; i++) { 
//			try
//			{
//				valid[i]=((MyQueriesThread) threads[i]).getIsValid();
//	        } catch (Exception e) {  
//              //e.printStackTrace(); 
//	        	valid[i]=false;
//	        	continue;
//            }        
//		}   
//		isAllFalse=ODDUtil.isAllFalse(valid,validLength); // || ODDUtil.isAllFalse(connected,portlistlength);
//		if (isAllFalse)
//			break;
//		//System.out.println("isAllFalse="+isAllFalse);
//		isGetAllResult=ODDUtil.isAllTwoTrue(valid, hasGetResult, validLength);  
//		if (isGetAllResult)
//			break;        				
	}  
//	ODDUtil.clientOutputAll("allResult.txt");
//	String tmpStr=ODDUtil.readToString("allResult.txt");
//	String lastLineStr=ODDUtil.readLastLine("allResult.txt");
//	String sizeStr=ODDToolUtil.getSizeFromStr(lastLineStr);
//	try {
//		resultLs[0]=Long.parseLong(sizeStr);		
//	}
//	catch (Exception e) {
//	}
//	System.out.println(tmpStr);
	String queryTimeStr="The query took "+(System.currentTimeMillis() - startTime)+" ms";
	System.out.println(queryTimeStr);
	//ODDUtil.writeStringToFileAppend(queryTimeStr,"allResult.txt");   
	resultLs[1]=(System.currentTimeMillis() - startTime);
	return resultLs;
}
public static int initializeFunctionList() {
       try {
   		FileReader reader = null;      
        BufferedReader br = null;    
        reader = new FileReader("functionList.out");   
        br = new BufferedReader(reader);
        String str = "";  
        String mname="";
        int index = 0;
        method2idx=new HashMap< String, Integer >();
        idx2method=new HashMap< Integer, String >();
        while((str = br.readLine()) != null)
        {  
        	mname=str.trim();
			if (!method2idx.containsKey(mname)) {
				method2idx.put(mname, index);
				idx2method.put(index, mname);
				index ++;
			}
        }        

       } catch (Exception e) {  
           e.printStackTrace(); 
           return -1;
       } 
	return 0;
}

}
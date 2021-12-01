package ODD;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

class MyThreadAll extends Thread {
    
	String hostName="localhost";
	int portNum = 2000;
	String inputMsg="";
    String resultS="";
    boolean isValid=true;
    public static String allResult ="";
    public static HashMap<String,HashSet<String>> resultMap = new HashMap<String,HashSet<String>>();
    public MyThreadAll(String hostName, int portNum, String inputMsg)
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
	    	if (inputMsg.trim().toUpperCase().startsWith("ALL"))  {
	    		querys.add("ALL");
	    	}	    		
	    	else	
	    		querys = ODDUtil.getFindList(ODDUtil.getArrayList("functionList.out"), inputMsg);
	    	System.out.println("querys.size()="+querys.size()+" querys="+querys+" inputMsg="+inputMsg);
	    	if (querys.size()<1)
	    	{	
	    		isValid=false;
	    		//System.out.println("In Run() isValid="+isValid);
	    		return;
	    	}	
	        System.out.println("querys=" + querys);
	        String query="";
	    	try 
	    	{
	    		for(int i=0;i<querys.size();i++ ){
	    			allResult="";
	    			query=querys.get(i).toString();
	    	        System.out.println("query=" + query);
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
		            ODDUtil.writeStringToFile(message2, "Message2Query"+ portNum+".txt");
//		            String firstEventS=ODDToolUtil.getFirstEventStr(message2);
//		            String lastEventS=ODDToolUtil.getLastEventStr(message2);
//		            System.out.println("firstEventS: " + firstEventS);
//		            System.out.println("lastEventS: " + lastEventS);
		            resultS=ODDToolUtil.getQueryDependenceSetStr(message2, query);	
		            System.out.println("Query: " + query + " dependency set from " + hostName+ " : " + portNum+"\n");

		            System.out.println(resultS);
		            //resultS=message2;	    		
			    	HashSet<String> onePortImpactSet = new HashSet<>();
			        onePortImpactSet=ODDToolUtil.getQuerySetFromDir(method2idx,idx2method, query);   //ODDUtil.getSetFromImpactsetStr(resultS, ";");		
	        		//System.out.println("onePortImpactSet="+onePortImpactSet);       
			        resultMap.put(query, onePortImpactSet);      	
			        //if (resultS!=null && resultS.length()>12)
			        	allResult += ODDToolUtil.getQueryImpactSets(onePortImpactSet, query); //resultS+"\n";		    		
	        		//System.out.println("Query="+query+" resultS: "+resultS+" onePortImpactSet "+onePortImpactSet+" allResult="+allResult+" resultMap="+resultMap);    
	        		System.out.println("Query="+query+" allResult="+allResult);    
		    		ODDUtil.writeStringToFileAppend(allResult, "allResult.txt");
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
public class ODDQueryClientAll {
	static HashMap<String,HashSet<String>> resultMap = new HashMap<String,HashSet<String>>();
	static String allResult="";
	protected static Map< String, Integer > method2idx;
	protected static Map< Integer, String > idx2method;
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
        //String inputMsg ="ALL";  
        String returnMsg ="";
        String allMsg ="";
    	long timeOutTimeDynamic=Long.MAX_VALUE/3;            //
    	initializeFunctionList();
//		long long2=ODDUtil.readTimeOutFromFile("Dynamic TimeOut:", "dynamicConfigurations.txt");
//		if (long2>0)
//			timeOutTimeDynamic=long2;
    	
    	
    	
		ArrayList<String> validAddresses=ODDUtil.getConnectedAddresses(hostlist,portlist);
		
        long startTime = System.currentTimeMillis();
        Thread threads[]=new Thread[10];   
        boolean[] hasGetResult=new boolean[10];
        boolean[] valid=new boolean[10];
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
		    	
		    	
    			//query=querys.get(i).toString();
    	        //System.out.println("query=" + query);
		    	try
				{
	    		
		            AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
		            InetSocketAddress hostAddress = new InetSocketAddress(oneHost, Integer.valueOf(onePort));
		            Future future = client.connect(hostAddress);
		            future.get();
		            byte [] message = new String("ALL").getBytes();
		            ByteBuffer buffer = ByteBuffer.wrap(message);
		            Future result = client.write(buffer);
		            future.get();
		        } catch (Exception e) {  
		              //e.printStackTrace(); 
			        	valid[i]=false;
			        	continue;
		        }  
//                threads[i]  = (Thread) new MyThreadAll(oneHost,Integer.parseInt(onePort),"ALL");  
//                threads[i].start();		                   
//                //System.out.println("in for i="+i+" Before valid[i]"); 
//                valid[i]=((MyThread) threads[i]).getIsValid();	  
//                //connected[i]=true;        			
//    			 hasGetResult[i]=false;
		    	
                //System.out.println("in for i="+i+" valid[i]="+valid[i]);
	        } catch (Exception e) {  
              //e.printStackTrace(); 
	        	valid[i]=false;
	        	continue;
            }          
			//System.out.println("i="+i+" valid[i]="+valid[i]);
		}    
		int myTime=ODDUtil.getTimeFromFiles("", "TimeCost", validLength);
    	String budgetStr=ODDUtil.readToString("budget.txt").trim().replaceAll("[^\\d]", "");
    	
    	long budget=1000;
		if (budgetStr.length()>1) 
			budget=Long.parseLong(budgetStr);	
		double utilization=0.0;
    	if (budget>0)
    		utilization=(double)myTime/budget;
    	// compute precision
    	ArrayList querys=new ArrayList();
    	querys = ODDUtil.getArrayList("queries.txt");

	    	
//	    	if (inputMsg.indexOf("<")>=0 && inputMsg.indexOf(">")>=0 && inputMsg.indexOf(": ")>0  && inputMsg.indexOf("(")>0 && inputMsg.indexOf(")")>0)
//	    	{
//	    		 querys.add(inputMsg);
//	    	}
//	    	else
	    	
	    	System.out.println("querys.size()="+querys.size()+" querys="+querys);
	    	int total111111Size=0;
	    	int totalSize=0;
	    	
//    		boolean isAllFalse=ODDUtil.isAllFalse(valid,validLength); // || ODDUtil.isAllFalse(connected,portlistlength);
//    		boolean isGetAllResult=ODDUtil.isAllTwoTrue(valid, hasGetResult, validLength);
//    		//System.out.println("isAllFalse="+isAllFalse+" isGetAllResult="+isGetAllResult);
//			Runtime.getRuntime().exec("rm Message2Query*.txt -f");
//    		//while (!isAllFalse && !isGetAllResult)
//    		{
//    			for (int i=0; i<validLength; i++) { 
//    				if (!valid[i])
//    					break;
//        			try
//        			{
//	            		returnMsg=((MyThread) threads[i]).getResultS();
//	            		if (returnMsg.length()>1)  {
//	            			hasGetResult[i]=true;
//	            		}
//	            		else
//	            			continue;
//	            		//System.out.println("returnMsg: "+returnMsg);
//	            		allMsg=((MyThread) threads[i]).allResult;
//	            		//System.out.println("allMsg:"+allMsg);
//	            		//System.out.println("Old allResult="+allResult);
//	            		allResult+=allMsg;
//	            		//System.out.println("New allResult="+allResult);
//	            		HashMap<String,HashSet<String>> oneMap = ((MyThread) threads[i]).resultMap;
//	            		//System.out.println("Old resultMap="+resultMap);
//	            		//System.out.println("oneMap="+oneMap);
//	            		resultMap=ODDUtil.mergeResultMap(resultMap,oneMap);	     
//	            		//System.out.println("New resultMap="+resultMap);
//
//        	        } catch (Exception e) {  
//	                  e.printStackTrace();      
//        	        	continue;
//		            }          			
//
//    			}
//    			try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					//e.printStackTrace();
//				}
//    			for (int i=0; i<validLength; i++) { 
//        			try
//        			{
//        				valid[i]=((MyThread) threads[i]).getIsValid();
////        				if (connected[i])
////        				{	
////        					valid[i]=((MyThread) threads[i]).getIsValid();
////        				}
////        				else
////        					valid[i]=false;
//        	        } catch (Exception e) {  
//	                   e.printStackTrace(); 
//        	        	valid[i]=false;
//        	        	continue;
//		            }        
//        		}   
//    		
////   			isAllFalse=ODDUtil.isAllFalse(valid,validLength); // || ODDUtil.isAllFalse(connected,portlistlength);
////    			if (isAllFalse)
////    				break;
////    			//System.out.println("isAllFalse="+isAllFalse);
////    			isGetAllResult=ODDUtil.isAllTwoTrue(valid, hasGetResult, validLength);  
////    			if (isGetAllResult)
////    				break;
////    			//System.out.println("isGetAllResult="+isGetAllResult);
////    			
////    			if ((System.currentTimeMillis() - startTime)>timeOutTimeDynamic) {
////    				//System.out.println("Query took ="+(System.currentTimeMillis() - startTime)+" ms");
////    				break;
////    			}
//    				
//    		}  
	    	
	    	for(int j=0;j<querys.size();j++ ){
				String query=querys.get(j).toString();
				HashSet<String> baseSet= new HashSet<String>();
				HashSet<String> querySet= new HashSet<String>();
				for (int i=0; i<validLength; i++) { 
					try
					{
				        String oneAddress ="";
				        String onePort ="";    
						oneAddress=validAddresses.get(i).toString().trim();
						String[] oneAddresses=oneAddress.split(":");
				    	if (oneAddresses.length<2)
				    	{	
				    		continue;
				    	}
       	
				    	onePort=oneAddresses[1].trim();
				    	if (onePort.length()>0) {
				    		HashSet<String> oneBaseSet= ODDUtil.getSetofQueryFromFile(query,"allQuery111111"+onePort+".txt");
				    		HashSet<String> oneQuerySet= ODDUtil.getSetofQueryFromFile(query,"allQueryResult"+onePort+".txt");
					    	if  (oneBaseSet.size()>0 && oneQuerySet.size()>0 ) {
					    		baseSet.addAll(oneBaseSet);
					    		querySet.addAll(oneQuerySet);
					    	}
				    	}
		                //System.out.println("in for i="+i+" valid[i]="+valid[i]); allQuery1111112002.txt   allQueryResult2002.txt
			        } catch (Exception e) {  
		              //e.printStackTrace(); 
			        	valid[i]=false;
			        	continue;
		            }          
					//System.out.println("i="+i+" valid[i]="+valid[i]);
				}    
				int baseSetSize=baseSet.size();
				int querySetSize=querySet.size();

				//System.out.println(" query="+query+" baseSetSize="+baseSetSize+" querySetSize="+querySetSize);
				if (baseSetSize>0 && querySetSize>0) {
					total111111Size+=baseSetSize;
					totalSize+=querySetSize;
				}			
	    	}	
	    	double precision=0.0;
	    	System.out.println("total111111Size="+total111111Size+" totalSize="+totalSize);	    	
	    	if (totalSize>0)
	    		precision=(double) total111111Size/totalSize;
	    	if (precision>1.0)
	    		precision=(double) 1/precision;
	    	if (utilization>0.1 && precision>0.1) {
	        	Date date = new Date();
	    		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    		String dateS = " "+sdf.format(date);
	    		System.out.println("dateS="+dateS);
	    		ODDUtil.writeStringToFileAppend(""+System.currentTimeMillis()+dateS+" Utilization="+ utilization+" Precision="+ precision+ " \n", "DeepLearningLogs.txt");
	    	}				 

	}

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

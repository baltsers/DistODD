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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
class MySizeThread extends Thread {
    
	String hostName="localhost";
	int portNum = 2000;
	String inputMsg="";
    String resultS="";
    boolean isValid=true;
    public static String allResult ="";
    public static HashMap<String,HashSet<String>> resultMap = new HashMap<String,HashSet<String>>();
    public MySizeThread(String hostName, int portNum, String inputMsg)
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
	    		querys = ODDUtil.getFindList(ODDUtil.getArrayList("functionList.out"), inputMsg);
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
	    			query=querys.get(i).toString();
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
		    		
	        	}	
	    		//System.out.println("Querys allResults:\n"+allResult+" resultMap="+resultMap); 
	    		ODDUtil.writeStringToFile(allResult, "allResultSize"+getProcessIDString()+".txt");
	    		//if configurations
	    		ODDUtil.writeStringToFileAppend(allResult, "allResultSize.txt");
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
	public static String getProcessIDString() {
		return ManagementFactory.getRuntimeMXBean().getName().replaceAll("[^a-zA-Z0-9]", "");
	}
}
public class ODDQueryClientSize {

	static HashMap<String,HashSet<String>> resultMap = new HashMap<String,HashSet<String>>();
	static String allResult="";
	protected static Map< String, Integer > method2idx;
	protected static Map< Integer, String > idx2method;
//    public static void outputAll()
//    {
//    	try {
//	    	if (resultMap.size()<1)
//	    		resultMap= ODDUtil.getHashMapFromFile("allResult.txt");   	
//	    	if (resultMap!=null)
//	    		ODDUtil.printImpactSetHashMap(resultMap);
//			//if (allResult.length()>1)
//				//System.out.println("All Received Dependencey Sets: \n"+allResult);
//        } catch (Exception e) {  
//            e.printStackTrace();                   
//        }  
//    }

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
        String inputMsg ="";  
        String returnMsg ="";
        String allMsg ="";
//    	for(int i=0;i<hostlist.length;i++ ){
//			System.out.println("hostlist["+i+"]="+hostlist[i]);
//    	}	
//
//    	for(int i=0;i<portlist.length;i++ ){
//			System.out.println("portlist["+i+"]="+portlist[i]);
//    	}	
        
    	long timeOutTimeDynamic=Long.MAX_VALUE/3;            //
    	initializeFunctionList();
//		long long2=ODDUtil.readTimeOutFromFile("Dynamic TimeOut:", "dynamicConfigurations.txt");
//		if (long2>0)
//			timeOutTimeDynamic=long2;
		ArrayList<String> validAddresses=ODDUtil.getConnectedAddresses(hostlist,portlist);
		ODDBranchMonitor.resetInternals();
		Process process=Runtime.getRuntime().exec("./RMALL.sh");	
		process.waitFor();	
        //try //
        {     
            while(inputMsg.indexOf("bye") == -1){
                Scanner sc = new Scanner(System.in); 
                System.out.println("Please input method:"); 
                inputMsg = sc.nextLine();
                String inputMsg2=inputMsg.toLowerCase(); 
                if (inputMsg2.startsWith("bye") || inputMsg2.startsWith("exit") || inputMsg2.startsWith("quit") )
                {
                	break;
                }
                else if (inputMsg2.startsWith("print") ||  inputMsg2.startsWith("output") || inputMsg2.startsWith("all"))
                {
                	ODDUtil.clientOutputAll("allResult.txt");
                	continue;
                }
                else if (inputMsg2.indexOf("budget")>=0)
                {
                	ODDToolUtil.saveBudget(inputMsg2);
                	continue;
                }
                else if (inputMsg2.startsWith("reset"))
                {
                	//ODDMonitor.resetInternals();

    				Process process2=Runtime.getRuntime().exec("./RMALL.sh");	
    				process2.waitFor();				
                	continue;
                }
                if (inputMsg.indexOf(" ; ")>0)
                {
                	ODDToolUtil.saveBudget(inputMsg);
                	inputMsg=ODDToolUtil.getFirstStr(inputMsg, " ; ");
                }
				Process process3=Runtime.getRuntime().exec("./RMMESSAGE2.sh");	
				process3.waitFor();				
                //System.out.println("inputMsg="+inputMsg+" ports="+ports);
                //if (ODDUtil.getFindList(ODDUtil.getArrayList("functionList.out"), inputMsg).size()<1)
                //	continue;
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
	                    threads[i]  = (Thread) new MySizeThread(oneHost,Integer.parseInt(onePort),inputMsg);  
	                    threads[i].start();		                   
	                    //System.out.println("in for i="+i+" Before valid[i]"); 
	                    valid[i]=((MySizeThread) threads[i]).getIsValid();	  
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
		            		returnMsg=((MySizeThread) threads[i]).getResultS();
		            		if (returnMsg.length()>1)  {
		            			hasGetResult[i]=true;
		            		}
		            		else
		            			continue;
		            		//System.out.println("returnMsg: "+returnMsg);
		            		allMsg=((MySizeThread) threads[i]).allResult;
		            		//System.out.println("allMsg:"+allMsg);
		            		//System.out.println("Old allResult="+allResult);
		            		allResult+=allMsg;
		            		//System.out.println("New allResult="+allResult);
		            		HashMap<String,HashSet<String>> oneMap = ((MySizeThread) threads[i]).resultMap;
		            		//System.out.println("Old resultMap="+resultMap);
		            		//System.out.println("oneMap="+oneMap);
		            		resultMap=ODDUtil.mergeResultMap(resultMap,oneMap);	     
		            		//System.out.println("New resultMap="+resultMap);

            	        } catch (Exception e) {  
    	                  e.printStackTrace();      
            	        	continue;
    		            }          			

        			}
        			try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
        			for (int i=0; i<validLength; i++) { 
            			try
            			{
            				valid[i]=((MySizeThread) threads[i]).getIsValid();
//            				if (connected[i])
//            				{	
//            					valid[i]=((MyThread) threads[i]).getIsValid();
//            				}
//            				else
//            					valid[i]=false;
            	        } catch (Exception e) {  
    	                   e.printStackTrace(); 
            	        	valid[i]=false;
            	        	continue;
    		            }        
            		}   
//        			isAllFalse=ODDUtil.isAllFalse(valid,validLength); // || ODDUtil.isAllFalse(connected,portlistlength);
//        			if (isAllFalse)
//        				break;
//        			//System.out.println("isAllFalse="+isAllFalse);
//        			isGetAllResult=ODDUtil.isAllTwoTrue(valid, hasGetResult, validLength);  
//        			if (isGetAllResult)
//        				break;
//        			//System.out.println("isGetAllResult="+isGetAllResult);
//        			
//        			if ((System.currentTimeMillis() - startTime)>timeOutTimeDynamic) {
//        				//System.out.println("Query took ="+(System.currentTimeMillis() - startTime)+" ms");
//        				break;
//        			}
//        				
        		}  
//        		ODDUtil.clientOutputAll("allResult.txt");
//        		String tmpStr=ODDUtil.readToString("allResult.txt");
//        		System.out.println(tmpStr);
        		System.out.println("Query took "+(System.currentTimeMillis() - startTime)+" ms");
            }
            //System.out.println("resultMap:"+resultMap); 

        }
//        }catch (Exception e) {
//            System.out.println("Exception:" + e);
//        }

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

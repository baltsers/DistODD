package ODD;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
public class ODDMerge {
	static String getQueriesResult(String inputMsg)
	{
		String allResult ="";
		String resultS ="";
    	ArrayList querys=new ArrayList();
//    	if (inputMsg.indexOf("<")>=0 && inputMsg.indexOf(">")>=0 && inputMsg.indexOf(": ")>0  && inputMsg.indexOf("(")>0 && inputMsg.indexOf(")")>0)
//    	{
//    		 querys.add(inputMsg);
//    	}
//    	else
    	querys = ODDUtil.getFindList(ODDUtil.getArrayList("functionList.out"), inputMsg);
    	System.out.println("querys.size()="+querys.size()+" querys="+querys+" inputMsg="+inputMsg);
    	if (querys.size()<1)
    	{	
    		//System.out.println("In Run() isValid="+isValid);
    		return "";
    	}	
        //System.out.println("querys=" + querys);
        String query="";
        for(int i=0;i<querys.size();i++ ){
			query=querys.get(i).toString();
	        resultS="Query: "+query+" dependency set: ";
	    	HashSet<String> onePortImpactSet = ODDUtil.getQueryResultFromDir(query, "");
	    	resultS+="====Size: "+onePortImpactSet.size()+"====";
			for (String s:onePortImpactSet) {
				resultS+=s+", ";
			}
			allResult+=resultS;
        }
        return allResult;
	}
    public static void main(String[] args) throws NumberFormatException, IOException {
//    	String hostNames="localhost,";
//    	//String hostName="localhost";
//		String ports = "2000,";
//		if (args.length > 0) {
//			hostNames  = args[0];
//		}
//		if (args.length > 1) {
//			ports  = args[1];
//		}
//		if (hostNames.length()<1 || hostNames.equals("localhost,") || hostNames.equals("localhost") || hostNames.equals("127.0.0.1,") || hostNames.equals("127.0.0.1") ) {
//			hostNames="localhost,localhost,localhost,localhost,localhost,localhost,localhost,localhost,localhost,localhost";
//		}
//		if (ports.length()<1 || ports.equals("2000,")) {
//			ports="2000,2001,2002,2003,2004,2005,2006,2007,2008,2009";
//		}
//		String[] hostlist = hostNames.split(",");
//		String[] portlist = ports.split(",");
        String inputQuery ="";  
        String inputMsg ="";  
//        String returnMsg ="";
//        String allMsg ="";
//   
//        
//        
//    	long timeOutTimeDynamic=Long.MAX_VALUE;            //
//		long long2=ODDUtil.readTimeOutFromFile("Dynamic TimeOut:", "dynamicConfigurations.txt");
//		if (long2>0)
//			timeOutTimeDynamic=long2;
//		ArrayList<String> validAddresses=ODDUtil.getConnectedAddresses(hostlist,portlist);
//		
        //try //
        {     
            while(inputMsg.indexOf("bye") == -1){
                Scanner sc = new Scanner(System.in); 
                System.out.println("Please input method:"); 
                inputMsg = sc.nextLine();
                inputMsg=inputQuery.toLowerCase(); 
                if (inputMsg.startsWith("bye") || inputMsg.startsWith("exit") || inputMsg.startsWith("quit") )
                {
                	break;
                }
                else if (inputMsg.startsWith("print") ||  inputMsg.startsWith("output") || inputMsg.startsWith("all"))
                {
                	ODDUtil.clientOutputAll("allResult.txt");
                	continue;
                }
                else if (inputMsg.startsWith("reset"))
                {
                	ODDBranchMonitor.resetInternals();
                	continue;
                }
                //if (ODDUtil.getFindList(ODDUtil.getArrayList("functionList.out"), inputMsg).size()<1)
                //	continue;
                long startTime = System.currentTimeMillis();  
                System.out.println(getQueriesResult(inputQuery));
        		//ODDUtil.clientOutputAll("allResult.txt");
        		System.out.println("Query took ="+(System.currentTimeMillis() - startTime)+" ms");
            }
            //System.out.println("resultMap:"+resultMap); 

        }
//        }catch (Exception e) {
//            System.out.println("Exception:" + e);
//        }

    }
  
    
 
}
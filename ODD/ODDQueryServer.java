/**
 * File: src/ODD/ODDQueryServer.java
 * -------------------------------------------------------------------------------------------
 * Date			Author      Changes
 * -------------------------------------------------------------------------------------------
 * 04/03/2019	Developer		created; for DistODD Socket Query Server receiving the queries from Query Client
*/
package ODD;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
class MyQueryServer extends Thread {   
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
//                Socket socket = serverSocket.accept();           
//                BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(socket.getInputStream()));             
//                query = bufferedReader.readLine();
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
           	    //logger.info("ODDMonitor Client say : " + query);
                if (query!=null && query.length()>1)
                {	
                	//ODDMonitor.processEvents();
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
		int portNum=3000;
		String processStr=getProcessID();
		//System.out.println("getProcessID()="+processStr); 
		int processID=Integer.parseInt(processStr.split("@")[0]);
		if (portNum<=3000)
		{
			portNum=3000+processID%10;
		}
		else	
			portNum++;
		//System.out.println("getPortNum()="+portNum);
		return portNum;
//		return 2000+(int)(Math.random()*10+1);
	}
}
 
public class ODDQueryServer {
//	protected static final long CN_LIMIT = 1*1000*1000;
//	/* the global counter for time-stamping each method event */
//	protected static Integer g_counter = 0;
//	
//	protected static ODDImpactAllInOne icAgent = null;
//	public static void setICAgent(ODDImpactAllInOne _agent) {icAgent = _agent;}
//	
//	protected static Integer preMethod = null;
//	
//	protected static int g_eventCnt = 0;
//	
//	/* a flag ensuring the initialization and termination are both executed exactly once and they are paired*/
//	protected static boolean bInitialized = false;
//
//	private static boolean active = false;
//	
//	private static boolean start = false;
//	
//	/* buffering events */
//	protected static List<Integer> B = new LinkedList<Integer>();
   
 
    public static void main(String[] args)throws IOException, InterruptedException, ExecutionException {
		Thread queryServer1 = new MyQueryServer(); 
		queryServer1.start();  
    }
    

}
package ODD;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import soot.SootClass;

public class dt2BranchMonitor {

	
	
	/* two special events */
	public static final int PROGRAM_START = Integer.MIN_VALUE;
	public static final int PROGRAM_END = Integer.MAX_VALUE;
	
	/* debug flag: e.g. for dumping event sequence to human-readable format for debugging purposes, etc. */
	protected static boolean debugOut = true;
	public static void turnDebugOut(boolean b) { debugOut = b; }

	/* a flag ensuring the initialization and termination are both executed exactly once and they are paired*/
	protected static boolean bInitialized = false;
	

	//public static String receivedMessages="";
	
	
	/* for DUAF/Soot to access this class */
	public static void __link() { }
	
	
	protected static SootClass clsBr;
	

	/* The array will be used to record stmt branches */
	//public static int[] covBranches;
	
	/* initialize  */		
	public synchronized static void initialize() throws Exception{
		
		System.out.println("**************dt2BranchMonitor::initialize()  0th");		
		//	clsBr = Scene.v().getSootClass("profile.BranchReporter");
		//	clsBr.setApplicationClass();
		//System.out.println("dtUtil.getLineNum="+dtUtil.getLineNum("entitystmt.out.branch"));
		//covBranches=new int[dtUtil.getLineNum("entitystmt.out.branch")];
		
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
    			System.out.println("**************dt2BranchMonitor::initialize() addShutdownHook run() 1th");	  
				dt2BranchMonitor.terminate("Forced upon external termination");
				System.out.println("**************dt2BranchMonitor::initialize() addShutdownHook run() 2th");	  
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
		
		
	}
	
	public synchronized static void enter(String methodname){
		//System.out.println("enter(String methodname)="+methodname);
	}
	
	public synchronized static void returnInto(String methodname, String calleeName){
		//System.out.println("returnInto(String methodname)="+methodname+" calleeName="+calleeName);
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
//	private static void writeMessage(String fileName, String message) {
//		try {
//        FileWriter fw = new FileWriter(fileName, true);
//        BufferedWriter bw = new BufferedWriter(fw);
//        bw.append(message);
//        bw.close();
//        fw.close();
//		}
//        catch (Exception e) {
//        	System.out.println("Cannot write message to" + fileName );
//			e.printStackTrace();
//		}
//	}

}
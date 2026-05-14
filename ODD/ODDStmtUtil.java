/**
 * File: src/ODD/ODDUtil.java
 * -------------------------------------------------------------------------------------------
 * Date			Author      Changes
 * -------------------------------------------------------------------------------------------
 * 03/21/2019	Developer		created; for DistODD statement util tools
*/
package ODD;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dua.global.ProgramFlowGraph;
import dua.method.CFG;
import dua.method.CFG.CFGNode;
import dua.method.CFGDefUses.Variable;
import dua.unit.StmtTag;
import dua.util.Util;
import soot.SootMethod;
import soot.jimple.Stmt;
import soot.tagkit.LineNumberTag;
import soot.tagkit.Tag;
import fault.StmtMapper;
import dua.unit.StmtTag;

public class ODDStmtUtil {
	/*
	static final DynTransferGraph dvtg = new DynTransferGraph();
	static boolean debugOut = false;
	static HashSet sourceNodes = new HashSet();
	static HashSet sinkNodes = new HashSet();
	static HashSet sourceMethods = new HashSet();
	static HashSet sinkMethods = new HashSet();
	static HashSet<DVTNode> sendMsgNodes = new HashSet();
	static HashSet<DVTNode> receiveMsgNodes = new HashSet();
	static String sourceMessage="";
	static String sinkMessage="";
	*/
	//static HashSet allMethodStmt= new HashSet();
//	public static boolean applyStatementCoverage = false;
	//private static Map<Stmt, Integer> sToId = null;
	//private static Stmt[] idToS = null;
	
    public static void main(String []args) {
    	String str1="     <io.netty.util.internal.logging.Slf4JLogger: boolean isDebugEnabled()> - $z0 = interfaceinvoke $r1.<org.slf4j.Logger: boolean isDebugEnabled()>() --> <io.netty.buffer.PooledByteBufAllocator: void <clinit>()> - $z2 = interfaceinvoke $r2.<io.netty.util.internal.logging.InternalLogger: boolean isDebugEnabled()>()";
    	System.out.println("1="+getFirstFromPath(str1));
    	System.out.println("2="+getSecondFromPath(str1));
    	
    	System.out.println("getFirstMethod="+getFirstMethodFromPath(str1));
    	System.out.println("getSecondMethod="+getSecondMethodFromPath(str1));
    	System.out.println("getFirstStmt="+getFirstStmtFromPath(str1));
    	System.out.println("getSecondStmt="+getSecondStmtFromPath(str1));
    	
    
//    	HashMap stmtStrses=dtStmtUtil.getStmtStrsFromFile("C:/Research/multichat/DT2Instrumented/stmtids.out");
//    	ArrayList stmtStrs=(ArrayList) stmtStrses.get(1);
//    	ArrayList stmtAll=(ArrayList) stmtStrses.get(2);
//    	//System.out.println("getMethodStmtStr(int methodInt, ArrayList stmtStrs)="+getMethodStmtStr("<ChatServer.core.MainServer: void main(java.lang.String[])>", stmtStrs));
//    	System.out.println("stmtAll.size()="+stmtAll.size());
//    	for (int i=0; i<stmtAll.size(); i++)
//    		System.out.println("stmtAll.get["+i+"]="+stmtAll.get(i));
////    	ArrayList bStmts=getBranchStmtsFromFile(1,"C:/TEST/DiverInstrumented/entitystmt.out.branch");
////    	System.out.println("bStmts="+bStmts);
////    	ArrayList bStmtStrs=getBranchStmtStrFromFile(1,"C:/TEST/DiverInstrumented/entitystmt.out.branch",stmtStrs);
////    	System.out.println("M="+getBranchMessageFromFile(13,"C:/TEST/DiverInstrumented/entitystmt.out.branch",stmtStrs));
//    	List sci= 	readStmtCoverageInt("C:/Research/multichat/DT2Instrumented", 1);
//    	System.out.println("sci.size()="+sci.size());
//    	for (int i=0; i<sci.size(); i++)
//    		System.out.println("sci.get["+i+"]="+sci.get(i));
//    	
//    	List<String> scs= 	readStmtCoverageStr("C:/Research/multichat/DT2Instrumented", 1, stmtAll);
//    	System.out.println("scs.size()="+scs.size());
//    	for (int i=0; i<scs.size(); i++)
//    		System.out.println("scs.get["+i+"]="+scs.get(i));
////    	//    	showAllNodes();
//     	showAllNodes(stmtStrs);
//    	showAllEdges(stmtStrs);
//    	 	
//    	DVTNode dn1 = new DVTNode("$r4",2,39);
//    	DVTNode dn2 = new DVTNode("r2",2,41);
//    	HashSet ns=getNodes("OIjava.io.PrintStream","<A: void main(java.lang.String[])>","virtualinvoke $r1.<java.io.PrintStream: void println(java.lang.String)>(\"A\")", stmtStrs);
//    	System.out.println("ns="+ns);
    	//DVTNode dn2 = new DVTNode("\"A\"",0,5);
    	//computeNodes("<A: void main(java.lang.String[])>","$r1 = <java.lang.System: java.io.PrintStream out>", "<A: void main(java.lang.String[])>","virtualinvoke $r1.<java.io.PrintStream: void println(java.lang.String)>(\"A\")", stmtStrs);
    	//computeNodes("r2","<C: void main(java.lang.String[])>","r2 = $r4","\"B\"", "<B: void main(java.lang.String[])>","virtualinvoke $r1.<java.io.PrintStream: void println(java.lang.String)>(\"B\")", stmtStrs);
//    	computeNodes("<C: void main(java.lang.String[])>","r2 = $r4","<B: void printString(int,java.lang.String,java.lang.String)>","i0 := @parameter0: int", stmtStrs);
//    	System.out.println("sourceMessage="+sourceMessage);
//    	System.out.println("sinkMessage="+sinkMessage);

    }
//    public static void computeNodes(String method1, String stmt1, String method2, String stmt2, ArrayList stmtStrs)
//    {
//    	HashSet ds1=getNodes(method1,stmt1, stmtStrs);
//    	HashSet ds2=getNodes(method2,stmt2, stmtStrs);
////    	HashSet ns1=getSourceNodes(ds1, stmtStrs);
////    	HashSet ns2=getSinkNodes(ds2, stmtStrs);
//    	HashSet pathSet = getPathNodes(getSourceNodes(ds1, stmtStrs), getSinkNodes(ds2, stmtStrs));
//    	
//    	sourceNodes.clear();
//    	sinkNodes.clear();
////    	sourceMethods.clear();
////    	sinkMethods.clear();
//    	sendMsgNodes.clear();
//    	receiveMsgNodes.clear();
//    	if (pathSet.size()<1)
//    	{
//    		sourceMessage=getSuccessorMessageToWrite(ds1, stmtStrs, true);
//    		sinkMessage=getPredecessorMessageToRead(ds2, stmtStrs, true);
//    	}
//    	else
//    	{
//    		sourceMessage=getSuccessorMessage(ds1, pathSet, stmtStrs, true);
//    		sinkMessage="";
//    	}
//
//    }
//    public static void computeNodes(String var1, String method1, String stmt1, String var2, String method2, String stmt2, ArrayList stmtStrs)
//    {
//    	HashSet ds1=getNodes(var1,method1,stmt1, stmtStrs);
//    	HashSet ds2=getNodes(var2,method2,stmt2, stmtStrs);
////    	HashSet ns1=getSourceNodes(ds1, stmtStrs);
////    	HashSet ns2=getSinkNodes(ds2, stmtStrs);
//    	HashSet pathSet = getPathNodes(getSourceNodes(ds1, stmtStrs), getSinkNodes(ds2, stmtStrs));
//    	
////    	System.out.println("ds1="+ds1);
////    	System.out.println("ds2="+ds2);
//////    	System.out.println("ns1="+ns1);
//////    	System.out.println("ns2="+ns2);
////    	System.out.println("Path="+pathSet);
//    	sourceNodes.clear();
//    	sinkNodes.clear();
////    	sourceMethods.clear();
////    	sinkMethods.clear();
//    	sendMsgNodes.clear();
//    	receiveMsgNodes.clear();
//    	if (pathSet.size()<1)
//    	{
//    		sourceMessage=getSuccessorMessageToWrite(ds1, stmtStrs, true);
//    		sinkMessage=getPredecessorMessageToRead(ds2, stmtStrs, true);
//    	}
//    	else
//    	{
//    		sourceMessage=getSuccessorMessage(ds1, pathSet, stmtStrs, true);
//    		sinkMessage="";
//    	}
//
//    }
//    public static HashSet<DVTNode> getSourceNodes(DVTNode dn1, ArrayList stmtStrs)
//    {
//    	HashSet<DVTNode> sendMsgNodes = new HashSet();
//    	sendMsgNodes.clear();
//    	HashSet<DVTNode> sourceNodes = getSuccessorsToWrite(dn1,stmtStrs);
//    	//System.out.println("sourceNodes="+sourceNodes);
//		if (sendMsgNodes.size()<1)
//		{	
//			sendMsgNodes.addAll(sourceNodes);
//			sendMsgNodes.remove(dn1);
//		}
//		//System.out.println("sendMsgNodes="+sendMsgNodes);
//		HashSet sourceNodes2 = new HashSet();
//		for (DVTNode n: sendMsgNodes)
//		{
//			sourceNodes2.addAll(getPredecessorsToNode(n,dn1));
//		}
//		sourceNodes.retainAll(sourceNodes2);
//		if (sourceNodes.size()<1)
//		{	
//			sourceNodes.add(dn1);
//		}	
//		return sourceNodes;
//		
//    }	
//    public static HashSet<DVTNode> getSourceNodes(HashSet<DVTNode> ns, ArrayList stmtStrs)
//    {
//    	HashSet resultS = new HashSet();
//    	for (DVTNode d1: ns)    		
//    		resultS.addAll(getSourceNodes(d1,stmtStrs));		
//    	return resultS;    	
//    }	
//    public static HashSet<DVTNode> getSinkNodes(DVTNode dn2, ArrayList stmtStrs)
//    {
//    	HashSet<DVTNode> receiveMsgNodes = new HashSet();
//    	receiveMsgNodes.clear();
//    	HashSet<DVTNode> sinkNodes = getPredecessorsToRead(dn2,stmtStrs);
//		if (receiveMsgNodes.size()<1)
//		{	
//			receiveMsgNodes.addAll(sinkNodes);
//			receiveMsgNodes.remove(dn2);
//		}	
//		if (receiveMsgNodes.size()<1)
//		{	
//			receiveMsgNodes.add(dn2);
//		}	
//		//System.out.println("receiveMsgNodes="+receiveMsgNodes);
//		HashSet sinkNodes2 = new HashSet();
//		for (DVTNode n: receiveMsgNodes)
//		{
//			sinkNodes2.addAll(getSucessorsToNode(n,dn2));		
//		}
//		sinkNodes.retainAll(sinkNodes2);
//		if (sinkNodes.size()<1)
//		{	
//			sinkNodes.add(dn2);
//		}	
//		return sinkNodes;		
//    }	
//	
//    public static HashSet<DVTNode> getSinkNodes(HashSet<DVTNode> ns, ArrayList stmtStrs)
//    {
//    	HashSet resultS = new HashSet();
//    	for (DVTNode d1: ns)    		
//    		resultS.addAll(getSinkNodes(d1,stmtStrs));		
//    	return resultS;    	
//    }	
//    public static HashSet getPathNodes(DVTNode dn1, DVTNode dn2)
//    {
//    	HashSet Nodes1 = new HashSet();
//		Nodes1.add(dn1);
//    	if (compareNode(dn1,dn2)==0) 
//    	{
//    		return Nodes1;
//    	}
//    	Nodes1=getSucessorsToNode(dn1,dn2);
//    	HashSet Nodes2=getPredecessorsToNode(dn2,dn1);
//    	Nodes1.retainAll(Nodes2);
//    	return Nodes1;
//    }
//    
//    
//    public static HashSet getPathNodes(HashSet<DVTNode> ns1, HashSet<DVTNode> ns2)
//    {
//    	HashSet resultS = new HashSet();
//    	for (DVTNode d1: ns1)
//    		for (DVTNode d2: ns2)
//    			if (compareNode(d1,d2)!=0) 
//    				resultS.addAll(getPathNodes(d1,d2));		
//    	return resultS;
//    }
    
//	public static int initVTG(String binDir) {		
//		dvtg.setSVTG(binDir+File.separator+"staticVtg.dat");
//		if (0 != dvtg.initializeGraph(debugOut)) {
//			System.out.println("Unable to load the static value transfer graph, aborted now.");
//			return -1;
//		}
//		DynTransferGraph.reachingImpactPropagation = false;
//		return 0;
//	}
//	public static HashSet<DVTNode> getNodes(String method1,String stmt1, ArrayList stmtStrs) {		
//		HashSet<DVTNode> hs=new HashSet();
//		try {  
//			String midMethod="";
//			String midStmt="";
////			String str1="";
////			String str2="";
//			for (DVTNode dn: dvtg.nodeSet()) {
//				midMethod=dvtg.idx2method.get(dn.getMethod()).toString();
//				midStmt=stmtStrs.get(dn.getStmt()).toString();
//				if (method1.equals(midMethod) && stmt1.equals(midStmt))
//				{
//					hs.add(dn);
//				}
//			}
//		}
//		catch (Exception e) { 
//			System.err.println("Exception e="+e);
//		}
//		return hs;
//	}
//	
//	public static HashSet<DVTNode> getNodes(String variable1, String method1,String stmt1, ArrayList stmtStrs) {		
//		HashSet<DVTNode> hs=new HashSet();
//		try {  
//			String midMethod="";
//			String midStmt="";
////			String str1="";
////			String str2="";
//			for (DVTNode dn: dvtg.nodeSet()) {
//				midMethod=dvtg.idx2method.get(dn.getMethod()).toString();
//				midStmt=stmtStrs.get(dn.getStmt()).toString();
//				if (variable1.equals(dn.getVar()) && method1.equals(midMethod) && stmt1.equals(midStmt))
//				{
//					hs.add(dn);
//					break;
//				}
//			}
//		}
//		catch (Exception e) { 
//			System.err.println("Exception e="+e);
//		}
//		return hs;
//	}
	
//	public static void showAllNodes() {		
//		try {  //for (SVTNode sn : dvtg.svtg.nodeSet()) {
//			for (DVTNode dn : dvtg.nodeSet()) {
//				System.out.println("Node="+dn+" Method="+dn.getMethod()+" Stmt="+dn.getStmt()+" Timestamp="+dn.getTimestamp() );
//				
//			}
//		}
//		catch (Exception e) { 
//			System.err.println("Exception e="+e);
//		}
//	}
//	public static void showAllNodes(ArrayList stmtStrs) {		
//		try {  //for (SVTNode sn : dvtg.svtg.nodeSet()) {
//			for (DVTNode dn : dvtg.nodeSet()) {
//				//System.out.println("Node="+sn+" Method="+sn.getMethod()+" Stmt="+sn.getStmt()+" Timestamp="+sn.getTimestamp() );
//				System.out.println("Node="+dn+" Method="+dvtg.idx2method.get(dn.getMethod())+" Stmt="+stmtStrs.get(dn.getStmt())+" Timestamp="+dn.getTimestamp() );				
//			}
//		}
//		catch (Exception e) { 
//			System.err.println("Exception e="+e);
//		}
//	}
//	public static String oneNodeInfo(DVTNode dn, ArrayList stmtStrs) {		
//		return "("+dn.getVar()+","+dvtg.idx2method.get(dn.getMethod())+","+stmtStrs.get(dn.getStmt())+")";
//	}
//	public static void showAllNodeMethods() {		
//		try {  //for (SVTNode sn : dvtg.svtg.nodeSet()) {
//			for (DVTNode dn : dvtg.nodeSet()) {
//				System.out.println("sn.getMethod().getName()="+dn.getMethod());
//			}
//		}
//		catch (Exception e) { 
//			System.err.println("Exception e="+e);
//		}
//	}
//	public static void showAllEdges() {		
//		try {  //for (SVTEdge se : dvtg.svtg.edgeSet()) {
//			for (DVTEdge de : dvtg.edgeSet()) {
//				//System.out.println("Edge: "+se.getSource()+" ---> "+se.getTarget());
//				System.out.println("Edge: "+de+" "+de.getSource()+" ---> "+de.getTarget());
//			}
//		}
//		catch (Exception e) { 
//			System.err.println("Exception e="+e);
//		}
//	}
//	public static void showAllEdges(ArrayList stmtStrs) {		
//		try {  //for (SVTEdge se : dvtg.svtg.edgeSet()) {
//			for (DVTEdge de : dvtg.edgeSet()) {
//				//System.out.println("Edge: "+se.getSource()+" ---> "+se.getTarget());
//				System.out.println("Edge: "+de+" "+oneNodeInfo(de.getSource(),stmtStrs)+" ---> "+oneNodeInfo(de.getTarget(),stmtStrs));
//			}
//		}
//		catch (Exception e) { 
//			System.err.println("Exception e="+e);
//		}
//	}
//	public static HashSet getDynMethodStmts(ArrayList stmtStrs) {	
//		HashSet hs = new HashSet();
//		String midStr="";
//		try {
//			try {  
//				for (DVTNode dn : dvtg.nodeSet()) {
//						midStr=dvtg.idx2method.get(dn.getMethod())+" - "+stmtStrs.get(dn.getStmt())+"\n";
//						hs.add(midStr);
//				
//				}
//			}
//			catch (Exception e) { 
//				System.err.println("Exception e="+e);
//			}
//		}
//		catch (Exception e) { 
//			System.err.println("Exception e="+e);
//		}
//		return hs;
//	}
//	
//	public static void showStmtAfter(String theStmt, ArrayList stmtStrs) {		
//		try {
//			String midStr="";
//			int stmtId=-1;
//			String stmtStr="";
//			String methodStr="";
//			boolean showStmt=false;
//			for (SVTNode sn : dvtg.svtg.nodeSet()) {
//				methodStr=sn.getMethod().getName();
//				midStr=sn.getStmt().toString();
//				String strs[]=midStr.split(" ");
//				if (strs.length<2)
//				{
//					stmtStr=midStr;
//					stmtId=-1;
//				}
//				else
//				{
//					stmtId=Integer.parseInt(strs[1]);
//					stmtStr=getStrFromId(stmtId, stmtStrs);
//				}
//				
//				if (theStmt.equals(stmtStr))
//				{
//					showStmt=true;					
//				}
//				if (showStmt)
//					System.out.println(methodStr+" - "+stmtStr+"\t Id: "+stmtId);
//			}
//		}
//		catch (Exception e) { 
//			System.err.println("Exception e="+e);
//		}
//	}
//	public static void showStmtAfter(String theMethod, String theStmt, ArrayList lineStrs) {		
//		try {
//			String midStr="";
//			int stmtId=-1;
//			String stmtStr="";
//			String methodStr="";
//			boolean showStmt=false;
//			for (SVTNode sn : dvtg.svtg.nodeSet()) {
//				methodStr=sn.getMethod().getName();
//				midStr=sn.getStmt().toString();
//				String strs[]=midStr.split(" ");
//				if (strs.length<2)
//				{
//					stmtStr=midStr;
//					stmtId=-1;
//				}
//				else
//				{
//					stmtId=Integer.parseInt(strs[1]);
//					stmtStr=getStrFromId(stmtId, lineStrs);
//				}
//				
//				if (theStmt.equals(stmtStr) &&(methodStr.equals(theMethod) || methodStr.equals("<"+theMethod+">")))
//				{
//					showStmt=true;					
//				}
//				if (showStmt)
//					System.out.println(methodStr+" - "+stmtStr+"\t Id: "+stmtId);
//			}
//		}
//		catch (Exception e) { 
//			System.err.println("Exception e="+e);
//		}
//	}
//	
//	public static ArrayList getStmtAfter(String theMethod, String theStmt, ArrayList lineStrs) {		
//		ArrayList resultA= new ArrayList();
//		try {
//			String midStr="";
//			int stmtId=-1;
//			String stmtStr="";
//			String methodStr="";
//			int showStmt=-1;
//			for (SVTNode sn : dvtg.svtg.nodeSet()) {
//				methodStr=sn.getMethod().getName();
//				midStr=sn.getStmt().toString();
//				String strs[]=midStr.split(" ");
//				if (strs.length<2)
//				{
//					stmtStr=midStr;
//					stmtId=-1;
//				}
//				else
//				{
//					stmtId=Integer.parseInt(strs[1]);
//					stmtStr=getStrFromId(stmtId, lineStrs);
//				}
//				
//				if (theStmt.equals(stmtStr) &&(methodStr.equals(theMethod) || methodStr.equals("<"+theMethod+">")))
//				{
//					showStmt++;					
//				}
//				if (showStmt>=0)
//					resultA.add(methodStr+" - "+stmtStr+"\t Id: "+stmtId);
//					//System.out.println(methodStr+" - "+stmtStr+"\t Id: "+stmtId);
//			}
//		}
//		catch (Exception e) { 
//			System.err.println("Exception e="+e);
//		}
//		return resultA;
//	}
//	public static void showStmtAfterFromFile(String theMethod, String theStmt, String stmtFile) {		
//		try {
//	    	ArrayList stmtStrs=getStmtStrsFromFile(stmtFile);
//	    	showStmtAfter(theMethod,theStmt,stmtStrs);		
//		}
//		catch (Exception e) { 
//			System.err.println("Exception e="+e);
//		}
//	}
//	
//	public static ArrayList getStmtAfterFromFile(String theMethod, String theStmt, String stmtFile) {		
//		ArrayList resultLsit=new ArrayList();
//		try {
//	    	ArrayList stmtStrs=getStmtStrsFromFile(stmtFile);
//	    	resultLsit=getStmtAfter(theMethod,theStmt,stmtStrs);		
//		}
//		catch (Exception e) { 
//			System.err.println("Exception e="+e);
//		}
//		return resultLsit;
//	}
	public static String getBranchMessageFromFile(int stmtIdx,String branchFile, ArrayList stmtStrs) {
		String  resultM = ""; 
        //resultA.add(stmtStrs.get(Integer.parseInt(strs[j])));
        ArrayList  resultS=getBranchStmtStrFromFile(stmtIdx,branchFile,stmtStrs);
        //System.out.println("resultS="+resultS);
        for (int i=0; i<resultS.size()-1; i++)
		{
        	//System.out.println("resultS.get("+i+")="+resultS.get(i));
        	resultM+=resultS+" --> ";
		}
        resultM+=resultS.get(resultS.size()-1);
        System.out.println("resultM="+resultM);
        return resultM;
	}
	
	public static ArrayList getBranchStmtStrFromFile(int stmtIdx,String branchFile, ArrayList stmtStrs) {
		ArrayList  resultA = new ArrayList(); 
        //resultA.add(stmtStrs.get(Integer.parseInt(strs[j])));
        ArrayList  resultI=getBranchStmtsFromFile(stmtIdx,branchFile);
        //System.out.println("resultI="+resultI);
        for (int i=0; i<resultI.size(); i++)
		{
        	//System.out.println("resultI.get("+i+")="+resultI.get(i));
        	if (!resultA.contains((int)resultI.get(i)))
        		resultA.add(stmtStrs.get((int)resultI.get(i)));
		}
        //System.out.println("resultA="+resultA);
        return resultA;
	}
	
	public static ArrayList getBranchStmtsFromFile(int stmtIdx,String branchFile) {
		ArrayList  resultA = new ArrayList(); ;
        FileReader reader = null;  
        BufferedReader br = null;  
        boolean findLine=false;
        try {  
        	   
            reader = new FileReader(branchFile);  
            br = new BufferedReader(reader);              
            String str = null;  
            //int count=0;
            while ((str = br.readLine()) != null) {  
            	//System.out.println("str="+str);
            	if (findLine)
            		break;
            	if (str.length()>1)
            	{
            		String strs[]=str.split(" ");
            		if (strs.length<1)
            		{	
            			break;           		
            		}
            		else
            		{
            			for (int i=0; i<strs.length; i++)
            			{
            				if (strs[i].equals(""+stmtIdx))
            				{
            					for (int j=0; j<strs.length; j++)
            					{
            						if (!resultA.contains(resultA.add(Integer.parseInt(strs[j]))))
            								resultA.add(Integer.parseInt(strs[j]));
            					}
            					findLine=true;
            					break;
            				}
            			}
            		}
            	}
            }     
            br.close();  
            reader.close();  
        } catch (Exception e) {  
            e.printStackTrace(); 
        }  
        return resultA;
	}
	
	public static HashMap getStmtStrsFromFile(String stmtFile) {
		ArrayList  strToId = new ArrayList(); 
		ArrayList  strAll = new ArrayList(); 
		HashMap map = new HashMap();
        FileReader reader = null;  
        BufferedReader br = null;     
        try {  
   
            reader = new FileReader(stmtFile);  
            br = new BufferedReader(reader);              
            String str = null;  
            //int count=0;
            while ((str = br.readLine()) != null) {  
            	//System.out.println("str="+str);
            	if (str.length()>1)
            	{
            		//if (strAll.contains(str))
        				//continue;
            		strAll.add(str);
            		String strs[]=str.split("> - ");
                	//System.out.println("strs.length="+strs.length+" strs[1]="+strs[1]);
            		if (strs.length<2)
            		{	
            			strToId.add(str);
            		}
            		else
            		{	
            			strToId.add(strs[1].trim());
            		}

            	}
            }     
            br.close();  
            reader.close();    
            map.put(1,strToId);
            map.put(2,strAll);
            return map;   
        } catch (Exception e) {  
            e.printStackTrace();   
            return map;    
        }  
	}
	
	public static ArrayList getStrsFromFile(String stmtFile) {
		ArrayList  strToId = new ArrayList(); ;
        FileReader reader = null;  
        BufferedReader br = null;     
        try {  
   
            reader = new FileReader(stmtFile);  
            br = new BufferedReader(reader);              
            String str = null;  
            //int count=0;
            while ((str = br.readLine()) != null) {  
            	//System.out.println("str="+str);
            	if (str.length()>1)
            	{            		
            		strToId.add(str);
            		
            	}
            }     
            br.close();  
            reader.close();     
            return strToId;   
        } catch (Exception e) {  
            e.printStackTrace();   
            return strToId;    
        }  
	}
	
	public static int getStrId(String s, ArrayList stmtStrs) { 
		int resultI=-1;
	   	for (int i=0; i<stmtStrs.size(); i++)
	   	{	
			 //System.out.println("stmtStrs["+i+"]="+stmtStrs.get(i));
			 if (stmtStrs.get(i).toString().equals(s))
			 {
				 resultI=i;
				 break;
			 }
	   	}	 
		return resultI;
	}
	public static int getMethodStmtStrId(String methodStr,String stmtStr, ArrayList lineStrs) { 
		int resultI=-1;
		String midStr="";
	   	for (int i=0; i<lineStrs.size(); i++)
	   	{	
			 //System.out.println("lineStrs["+i+"]="+lineStrs.get(i));
	   		 midStr=lineStrs.get(i).toString().trim();
			 if (midStr.indexOf(methodStr)>=0 && midStr.indexOf(stmtStr)>1)
			 {
				 resultI=i;
				 break;
			 }
	   	}	 
		return resultI;
	}
	
	public static String getStrFromId(int sId, ArrayList stmtStrs) { return stmtStrs.get(sId).toString(); }
	
//	public static void showSrcEdges(DVTNode dn) {		
//		try {
//			for (DVTEdge de : dvtg.edgeSet()) {
//				//System.out.println("dn="+dn+" de.getTarget()="+de.getTarget());
//				if (compareNode(dn,de.getSource())==0 && compareNode(dn,de.getTarget())!=0)
//					System.out.println("show Src Edge: "+de+" "+dn+" ---> "+de.getTarget());
//			}
//		}
//		catch (Exception e) { 
//			System.err.println("Exception e="+e);
//		}
//	}
//	
//	public static void showDstEdges(DVTNode dn) {		
//		try {
//			for (DVTEdge de : dvtg.edgeSet()) {  //
//				//System.out.println(" de.getSource()="+de.getSource()+" dn="+dn);
//				if (compareNode(dn,de.getTarget())==0 && compareNode(dn,de.getSource())!=0)
//					System.out.println("show Dst Edge: "+de+" "+de.getSource()+" ---> "+dn);
//			}
//		}
//		catch (Exception e) { 
//			System.err.println("Exception e="+e);
//		}
//	}
//	
//	public static void showNextNodes(DVTNode dn) {		
//		try {
//			for (DVTEdge de : dvtg.edgeSet()) {
//				//System.out.println("dn="+dn+" de.getTarget()="+de.getTarget());
//				if (compareNode(dn,de.getSource())==0 && compareNode(dn,de.getTarget())!=0)
//					System.out.println(" Node "+dn+" Next: "+de.getTarget());
//			}
//		}
//		catch (Exception e) { 
//			System.err.println("Exception e="+e);
//		}
//	}
//	
//	public static void showPrevNodes(DVTNode dn) {		
//		try {
//			for (DVTEdge de : dvtg.edgeSet()) {  //
//				//System.out.println(" de.getSource()="+de.getSource()+" dn="+dn);
//				if (compareNode(dn,de.getTarget())==0 && compareNode(dn,de.getSource())!=0)
//					System.out.println(" Node "+dn+" Prev: "+de.getSource());
//			}
//		}
//		catch (Exception e) { 
//			System.err.println("Exception e="+e);
//		}
//	}
	
	
	
//	public static HashSet getNextNodes(DVTNode dn) {	
//		HashSet hs = new HashSet();
//		try {
//			for (DVTEdge de : dvtg.edgeSet()) {
//				//System.out.println("dn="+dn+" de.getTarget()="+de.getTarget());
//				if (compareNode(dn,de.getSource())==0 && compareNode(dn,de.getTarget())!=0)
//					hs.add(de.getTarget());
//			}
//		}
//		catch (Exception e) { 
//			System.err.println("Exception e="+e);
//		}
//		return hs;
//	}
	
//	public static HashSet getSuccessorNodes(DVTNode dn, boolean debugOut) {	
//		int oldSize=0;
//		HashSet<DVTNode> resultS=new HashSet();
//		String messages="";
//		boolean sizeIncremented=true;
//		
//		{
//			resultS.add(dn);
//			while (sizeIncremented)
//			{	
//				Object[] nodeS=resultS.toArray();
//				for (int i=0;i<nodeS.length;i++)
//				{	
//					DVTNode d0=(DVTNode)nodeS[i];
//					HashSet<DVTNode> tmpHs=new HashSet();
//					tmpHs=getNextNodes(d0);
//					Iterator iterator = tmpHs.iterator();  
//			        while (iterator.hasNext()) {  
//			            //System.out.println(d0+" --> "+iterator.next()); 
//			        	messages+=d0+" --> "+iterator.next()+"\n";
//			        }   					
//					resultS.addAll(tmpHs);
//				}	
//				messages+=" \n";
//				if (resultS.size()==oldSize)
//				{	
//					sizeIncremented=true;
//					break;
//				}	
//				oldSize=resultS.size();
//			}
//			
//		}
//		if (debugOut)
//			System.out.println(messages);
//		return resultS;
//	}
//	public static HashSet getPredecessorNodes(DVTNode dn, boolean debugOut) {	
//		int oldSize=0;
//		HashSet<DVTNode> resultS=new HashSet();
//		String messages="";
//		boolean sizeIncremented=true;
//		//try 
//		{
//			resultS.add(dn);
//			while (sizeIncremented)
//			{	
//				Object[] nodeS=resultS.toArray();
//				for (int i=0;i<nodeS.length;i++)
//				{	
//					DVTNode d0=(DVTNode)nodeS[i];
//					HashSet<DVTNode> tmpHs=new HashSet();
//					tmpHs=getPrevNodes(d0);
//					Iterator iterator = tmpHs.iterator();  
//			        while (iterator.hasNext()) {  
//			        	messages+=d0+" <-- "+iterator.next()+"\n";
//			        }   					
//					resultS.addAll(tmpHs);
//				}	
//				messages+=" \n";
//				if (resultS.size()==oldSize)
//				{	
//					sizeIncremented=true;
//					break;
//				}	
//				oldSize=resultS.size();
//			}
//			
//		}	
////		catch (Exception e) { 
////			System.err.println("Exception e="+e);
////		}
//		if (debugOut)
//			System.out.println(messages);
//		return resultS;
//	}	
	
//	public static HashSet getSuccessorsToWrite(DVTNode dn, ArrayList<String> stmtStrs) {	
//		int oldSize=0;
//		HashSet<DVTNode> resultS=new HashSet();
//		//HashSet<DVTNode> visited=new HashSet();
//		String stmtStr="";
//		//String messages="";
//		String midStr="";
//		String preStr="";
//		String resultMsg="";
//		DVTNode tmpNode=null;
//		//Set<String> messageSet=new HashSet();
//		boolean sizeIncremented=true;
//		//try 
//		{
//			//resultS.add(dn);
//			resultS.add(dn);
//			while (sizeIncremented)
//			{	
//				Object[] nodeS=resultS.toArray();
//				for (int i=0;i<nodeS.length;i++)
//				{						
//					DVTNode d0=(DVTNode)nodeS[i];
//					//if (visited.contains(d0))
//					//	continue;
//					stmtStr=stmtStrs.get(d0.getStmt()).toLowerCase();
//					if (stmtStr.indexOf("<java.net.Socket: java.io.OutputStream getOutputStream()>")<=-1 && stmtStr.indexOf("<java.io.ObjectOutputStream: void writeObject(java.lang.Object)>")<=-1 &&
//						stmtStr.indexOf("java.nio.channels.SocketChannel")<=-1) 
//					{						
//						HashSet<DVTNode> tmpHs=new HashSet();
//						tmpHs=getNextNodes(d0);
//						resultS.addAll(tmpHs);				
//					}
//					else
//					{
//						sendMsgNodes.add(d0);
//					}
//					resultS.add(d0);
//				}	
//				if (resultS.size()==oldSize)
//				{	
//					sizeIncremented=false;
//					break;
//				}	
//				oldSize=resultS.size();
//			}	
//			
//		}		
//		return resultS;
//	}
//
//	public static HashSet getPredecessorsToRead(DVTNode dn, ArrayList<String> stmtStrs) {
//		int oldSize=0;
//		HashSet<DVTNode> resultS=new HashSet();
//		//HashSet<DVTNode> visited=new HashSet();
//		String stmtStr="";
//		//String messages="";
//		String midStr="";
//		String preStr="";
//		String resultMsg="";
//		DVTNode tmpNode=null;
//		//Set<String> messageSet=new HashSet();
//		boolean sizeIncremented=true;
//		//try 
//		{
//			//resultS.add(dn);
//			resultS.add(dn);
//			while (sizeIncremented)
//			{	
//				Object[] nodeS=resultS.toArray();
//				for (int i=0;i<nodeS.length;i++)
//				{						
//					DVTNode d0=(DVTNode)nodeS[i];
//					//if (visited.contains(d0))
//					//	continue;
//					stmtStr=stmtStrs.get(d0.getStmt()).toLowerCase();
//					if (stmtStr.indexOf("<java.net.Socket: java.io.InputStream getInputStream()>")<=-1 && stmtStr.indexOf("<java.io.ObjectInputStream: java.lang.Object readObject()>")<=-1 &&
//						stmtStr.indexOf("java.nio.channels.SocketChannel")<=-1) 
//					{						
//						HashSet<DVTNode> tmpHs=new HashSet();
//						tmpHs=getPrevNodes(d0);
//						resultS.addAll(tmpHs);
////						Iterator iterator = tmpHs.iterator();  
////				        while (iterator.hasNext()) {  
////				        	tmpNode=(DVTNode)iterator.next();
////				        	resultS.add(tmpNode);
////				        }  						
//					}
//					else
//					{
//						receiveMsgNodes.add(d0);
//					}
//					resultS.add(d0);
//				}	
//				if (resultS.size()==oldSize)
//				{	
//					sizeIncremented=false;
//					break;
//				}	
//				oldSize=resultS.size();
//			}	
//			
//		}		
//		return resultS;
//	}
//	public static HashSet getSucessorsToNode(DVTNode dn, DVTNode dstN) {
//		int oldSize=0;
//		HashSet<DVTNode> resultS=new HashSet();
//		//HashSet<DVTNode> visited=new HashSet();
//		String stmtStr="";
//		//String messages="";
//		String midStr="";
//		String preStr="";
//		String resultMsg="";
//		DVTNode tmpNode=null;
//		boolean findDst=false;
//		//Set<String> messageSet=new HashSet();
//		boolean sizeIncremented=true;
//		//try 
//		{
//			//resultS.add(dn);
//			resultS.add(dn);
//			while (sizeIncremented)
//			{	
//				if (findDst)
//					break;
//				Object[] nodeS=resultS.toArray();
//				for (int i=0;i<nodeS.length;i++)
//				{
//					if (findDst)
//						break;						
//					DVTNode d0=(DVTNode)nodeS[i];
//					if (compareNode(dn,dstN)!=0)
//					{						
//						HashSet<DVTNode> tmpHs=new HashSet();
//						tmpHs=getNextNodes(d0);
//						Iterator iterator = tmpHs.iterator();  
//				        while (iterator.hasNext()) {  
//				        	tmpNode=(DVTNode)iterator.next();
//							if (compareNode(dn,tmpNode)!=0)
//							{	
//								resultS.add(tmpNode);
//							}	
//							else
//							{
//								findDst=true;
//								break;
//							}
//				        }  						
//					}
//					else
//					{
//						findDst=true;
//						break;
//					}
//					resultS.add(d0);
//				}	
//				if (resultS.size()==oldSize)
//				{	
//					sizeIncremented=false;
//					break;
//				}	
//				oldSize=resultS.size();
//			}	
//			
//		}		
//		return resultS;
//	}	
//	public static HashSet getPredecessorsToNode(DVTNode dn, DVTNode dstN) {
//		int oldSize=0;
//		HashSet<DVTNode> resultS=new HashSet();
//		//HashSet<DVTNode> visited=new HashSet();
//		String stmtStr="";
//		//String messages="";
//		String midStr="";
//		String preStr="";
//		String resultMsg="";
//		DVTNode tmpNode=null;
//		boolean findDst=false;
//		//Set<String> messageSet=new HashSet();
//		boolean sizeIncremented=true;
//		//try 
//		{
//			//resultS.add(dn);
//			resultS.add(dn);
//			while (sizeIncremented)
//			{	
//				if (findDst)
//					break;
//				Object[] nodeS=resultS.toArray();
//				for (int i=0;i<nodeS.length;i++)
//				{
//					if (findDst)
//						break;						
//					DVTNode d0=(DVTNode)nodeS[i];
//					if (compareNode(dn,dstN)!=0)
//					{						
//						HashSet<DVTNode> tmpHs=new HashSet();
//						tmpHs=getPrevNodes(d0);
//						Iterator iterator = tmpHs.iterator();  
//				        while (iterator.hasNext()) {  
//				        	tmpNode=(DVTNode)iterator.next();
//							if (compareNode(dn,tmpNode)!=0)
//							{	
//								resultS.add(tmpNode);
//							}	
//							else
//							{
//								findDst=true;
//								break;
//							}
//				        }  						
//					}
//					else
//					{
//						findDst=true;
//						break;
//					}
//					resultS.add(d0);
//				}	
//				if (resultS.size()==oldSize)
//				{	
//					sizeIncremented=false;
//					break;
//				}	
//				oldSize=resultS.size();
//			}	
//			
//		}		
//		return resultS;
//	}	
//	
//	public static HashSet getPrevNodes(DVTNode dn) {	
//		HashSet hs = new HashSet();
//		try {
//			for (DVTEdge de : dvtg.edgeSet()) {
//				//System.out.println("dn="+dn+" de.getTarget()="+de.getTarget());
//				if (compareNode(dn,de.getTarget())==0 && compareNode(dn,de.getSource())!=0)
//					hs.add(de.getSource());
//			}
//		}
//		catch (Exception e) { 
//			System.err.println("Exception e="+e);
//		}
//		return hs;
//	}
//
//	public static String getMethodStmtStr(String methodStr, ArrayList stmtStrs) {	
//		String messages="";
//		String midStr="";
//		Set messageSet=new HashSet();
//		if (methodStr.length()<1)
//			return "";
//		try {
//			try {  //for (SVTNode sn : dvtg.svtg.nodeSet()) {
//				for (DVTNode dn : dvtg.nodeSet()) {
//					if (methodStr.equals(dvtg.idx2method.get(dn.getMethod())))  {
//						midStr=methodStr+" - "+stmtStrs.get(dn.getStmt())+"\n";
//						if (!messageSet.contains(midStr))
//						{	
//							messages+=midStr;
//							messageSet.add(midStr);
//						}
//					}
//				}
//			}
//			catch (Exception e) { 
//				System.err.println("Exception e="+e);
//			}
//		}
//		catch (Exception e) { 
//			System.err.println("Exception e="+e);
//		}
//		return messages;
//	}
//	public static HashSet getSrcEdges(DVTNode dn) {	
//		HashSet hs = new HashSet();
//		try {
//			for (DVTEdge de : dvtg.edgeSet()) {
//				if (compareNode(dn,de.getSource())==0 && compareNode(dn,de.getTarget())!=0)
//					hs.add(de);
//			}
//		}
//		catch (Exception e) { 
//			System.err.println("Exception e="+e);
//		}
//		return hs;
//	}
//	public static HashSet getDstEdges(DVTNode dn) {	
//		HashSet hs = new HashSet();
//		try {
//			for (DVTEdge de : dvtg.edgeSet()) {
//				if (compareNode(dn,de.getTarget())==0 && compareNode(dn,de.getSource())!=0)
//					hs.add(de);
//			}
//		}
//		catch (Exception e) { 
//			System.err.println("Exception e="+e);
//		}
//		return hs;
//	}
	
	public static int compareNode(DVTNode n1, DVTNode n2) {
		final Integer mname1 = n1.m;
		final Integer mname2 = n2.m;

		final String vname1 = n1.v;
		final String vname2 = n2.v;

		int cmpmName = mname1 - mname2;
		int cmpvName = vname1.compareToIgnoreCase(vname2);
		if (null == n1.s || null == n2.s) {
			return (cmpmName != 0)?cmpmName : cmpvName; 
		}

		final int sid1 = n1.s;
		final int sid2 = n2.s;
		return (cmpmName != 0)?cmpmName : (cmpvName != 0)?cmpvName:
			(sid1 > sid2)?1:(sid1 < sid2)?-1:0;
	}
	
	public static void showSet(Set mySet) {
		if (mySet==null || mySet.isEmpty())
			return;
		Iterator<String> it = mySet.iterator();  
		while (it.hasNext()) {  
		  String str = it.next().toString();  
		  System.out.print(" "+str+" ");  
		}  
		System.out.println(" ");		
	}
	public static void showSets(Map<String, Set<String>> mis) {
		if (mis==null || mis.isEmpty())
			return;
		for (String m : mis.keySet()) {
			System.out.println("[Change Impact Set of " + m + "]: size= " + mis.get(m).size());
			for (String im : mis.get(m)) {
					System.out.println(""+im);
			}
		}
	}
	
    // get first item
    public static String getFirstFromPair(String str) {  
    	
        try {  

        	String[] strs=str.split(";"); 
        	if (strs.length<1)
        	{
        		return "";
        	}
        	else
        	{
        		return strs[0].trim();        	
        	}
        } catch (Exception e) {  
            e.printStackTrace();  
            return "";
        }  
    }   
    // get method
    public static String getMethodFromStr(String str) {  
    	
        try {  

        	String[] strs=str.split("> - "); 
        	if (strs.length<1)
        	{
        		return "";
        	}
        	else
        	{
        		return strs[0]+">";         	
        	}
        } catch (Exception e) {  
            e.printStackTrace();  
            return "";
        }  
    }     
    // get stmt
    public static String getStmtFromStr(String str) {  
    	
        try {  

        	String[] strs=str.split("> - "); 
        	if (strs.length<2)
        	{
        		return "";
        	}
        	else
        	{
        		return strs[1];         	
        	}
        } catch (Exception e) {  
            e.printStackTrace();  
            return "";
        }  
    }   
    // get second item
    public static String getSecondFromPair(String str) {  
    	
        try {  

        	String[] strs=str.split(";"); 
        	if (strs.length<2)
        	{
        		return "";
        	}
        	else
        	{
        		return strs[1].trim();         	
        	}
        } catch (Exception e) {  
            e.printStackTrace();  
            return "";
        }  
    }       
    public static String getFirstMethod(String str) { 
    	return getMethodFromStr(getFirstFromPair(str));
    }
    public static String getFirstStmt(String str) { 
    	return getStmtFromStr(getFirstFromPair(str));
    }   
    public static String getSecondMethod(String str) { 
    	return getMethodFromStr(getSecondFromPair(str));
    }
    public static String getSecondStmt(String str) { 
    	return getStmtFromStr(getSecondFromPair(str));
    }
    // get first from path
    public static String getFirstFromPath(String str) {  
    	
        try {  

        	String[] strs=str.split(" --> "); 
        	if (strs.length<1)
        	{
        		return "";
        	}
        	else
        	{
        		return strs[0].trim();         	
        	}
        } catch (Exception e) {  
            e.printStackTrace();  
            return "";
        }  
    }   
    // get second from path
    public static String getSecondFromPath(String str) {  
    	
        try {  

        	String[] strs=str.split(" --> "); 
        	if (strs.length<2)
        	{
        		return "";
        	}
        	else
        	{
        		return strs[1].trim();         	
        	}
        } catch (Exception e) {  
            e.printStackTrace();  
            return "";
        }  
    }           
    public static String getFirstMethodFromPath(String str) { 
    	return getMethodFromStr(getFirstFromPath(str));
    }
    public static String getFirstStmtFromPath(String str) { 
    	return getStmtFromStr(getFirstFromPath(str));
    }   
    public static String getSecondMethodFromPath(String str) { 
    	return getMethodFromStr(getSecondFromPath(str));
    }
    public static String getSecondStmtFromPath(String str) { 
    	return getStmtFromStr(getSecondFromPath(str));
    }
	public static boolean sameNodes(SVTNode n1, SVTNode n2) {
//		final SootMethod m1 = n1.getMethod();
//		final SootMethod m2 = n2.getMethod();
//
//		final Variable v1 = n1.getVar();
//		final Variable v2 = n2.getVar();
//
//		final Stmt s1=n1.getStmt();
//		final Stmt s2=n2.getStmt();
		final String m1 = n1.getMethod().toString();
		final String m2 = n2.getMethod().toString();
		final String v1 = n1.getVar().toString();
		final String v2 = n2.getVar().toString();
		final String s1 = n1.getStmt().toString();
		final String s2 = n2.getStmt().toString();
		if (m1.equals(m2) && v1.equals(v2) && s1.equals(s2) )  {
			return true;
		}
		else
			return false;
	}
//	public static String getSuccessorMessage(DVTNode dn, HashSet nodes, ArrayList<String> stmtStrs, boolean showDetail) {	
//		int oldSize=0;
//		HashSet<DVTNode> resultS=new HashSet();
//		//HashSet<DVTNode> visited=new HashSet();
//		String stmtStr="";
//		String messages="";
//		String midStr="";
//		String preStr="";
//		String resultMsg="";
//		DVTNode tmpNode=null;
//		Set<String> messageSet=new HashSet();
//		boolean sizeIncremented=true;
//		//try 
//		{
//			resultS.add(dn);
//			sourceNodes.add(dn);
//			int count=1;
//			while (sizeIncremented)
//			{	
//				Object[] nodeS=resultS.toArray();
//				for (int i=0;i<nodeS.length;i++)
//				{						
//					DVTNode d0=(DVTNode)nodeS[i];
//					//if (visited.contains(d0))
//					//	continue;
//					stmtStr=stmtStrs.get(d0.getStmt()).toLowerCase();
//					if (nodes.contains(d0)) 
//					{						
//						HashSet<DVTNode> tmpHs=new HashSet();
//						tmpHs=getNextNodes(d0);
//						Iterator iterator = tmpHs.iterator();  
//				        while (iterator.hasNext()) {  
//				        	tmpNode=(DVTNode)iterator.next();
//				        	if (resultS.contains(tmpNode))
//				        		continue;
//				        	if (showDetail)  {
//				        		midStr=oneNodeInfo(d0,stmtStrs)+" --> "+oneNodeInfo(tmpNode,stmtStrs)+"\n";
//				        	}
//				        	else
//				        		midStr=d0+" --> "+tmpNode+"\n";
//				        	if (!messageSet.contains(midStr))  {	
//				        		messageSet.add(midStr);
//				        		preStr=""; //+count+":";
//					        	for (int j=0;j<count; j++)
//					        	{
//					        		preStr+=" ";
//					        	}				        		
//				        		resultMsg+=preStr+midStr;
//				        		sourceNodes.add(tmpNode);
//				        	}
//				        	sourceMethods.add(tmpNode.getMethod());			        		
//				        }   	
//				        //visited.addAll(resultS);
//						resultS.addAll(tmpHs);
//						sourceNodes.add(d0);
//					}				
//				}				
//				count++;
//				if (resultS.size()==oldSize)
//				{	
//					sizeIncremented=false;
//					break;
//				}	
//				oldSize=resultS.size();
//			}	
//			
//		}		
//		return resultMsg;
//	}
//
//	public static String getPredecessorMessage(DVTNode dn, HashSet nodes, ArrayList<String> stmtStrs, boolean showDetail) {	
//		int oldSize=0;
//		HashSet<DVTNode> resultS=new HashSet();
//		String stmtStr="";
//		String messages="";
//		String midStr="";
//		String preStr="";
//		String resultMsg="";
//		DVTNode tmpNode=null;
//		Set<String> messageSet=new HashSet();
//		ArrayList messageA = new ArrayList();
//		boolean sizeIncremented=true;
//		//try 
//		{
//			resultS.add(dn);
//			sinkNodes.add(dn);
//			int count=1;
//			while (sizeIncremented)
//			{	
//				Object[] nodeS=resultS.toArray();
//				for (int i=0;i<nodeS.length;i++)
//				{	
//					DVTNode d0=(DVTNode)nodeS[i];
//					stmtStr=stmtStrs.get(d0.getStmt()).toLowerCase();
//					if (nodes.contains(d0)) 
//					{						
//						HashSet<DVTNode> tmpHs=new HashSet();
//						tmpHs=getPrevNodes(d0);
//						Iterator iterator = tmpHs.iterator();  
//				        while (iterator.hasNext()) {    
//				        	tmpNode=(DVTNode)iterator.next();
//				        	if (resultS.contains(tmpNode))
//				        		continue;
//				        	if (showDetail)  {
//				        		//midStr=oneNodeInfo(d0,stmtStrs)+" <-- "+oneNodeInfo(tmpNode,stmtStrs)+"\n";
//				        		midStr=oneNodeInfo(tmpNode,stmtStrs)+" --> "+oneNodeInfo(d0,stmtStrs)+"\n";
//				        	}
//				        	else
//				        		midStr=tmpNode+" --> "+d0+"\n";
//				        		//midStr=d0+" <-- "+tmpNode+"\n";
//				        	if (!messageSet.contains(midStr))  {	
//				        		messageSet.add(midStr);
//				        		preStr=""; //+count+":";
//					        	for (int j=0;j<count; j++)
//					        	{
//					        		preStr+=" ";
//					        	}				        		
//				        		//resultMsg+=preStr+midStr;
//					        	//System.out.println("LineStr="+preStr+midStr);
//					        	messageA.add(preStr+midStr);
//				        	}
//				        	sinkMethods.add(tmpNode.getMethod());
//				        }   					
//						resultS.addAll(tmpHs);
//					}					
//				}				
//				count++;
//				if (resultS.size()==oldSize)
//				{	
//					sizeIncremented=true;
//					break;
//				}	
//				oldSize=resultS.size();
//			}	
//			
//		}		
//		Collections.reverse(messageA);
//		for(int i =0;i<messageA.size(); i++){
//			resultMsg+=messageA.get(i);           
//        }
////		System.out.println("messageA="+messageA);
////		System.out.println("resultMsg="+resultMsg);
//		return resultMsg;
//	}
//	
//
//	public static String getSuccessorMessage(HashSet<DVTNode> dns, HashSet nodes, ArrayList<String> stmtStrs, boolean showDetail) {	
//		String resultMsg="";
//		for (DVTNode d1: dns)
//				resultMsg+=getSuccessorMessage(d1, nodes, stmtStrs, showDetail);
//		return resultMsg;
//	}
//	
//	public static String getPredecessorMessage(HashSet<DVTNode> dns, HashSet nodes, ArrayList<String> stmtStrs, boolean showDetail) {	
//		String resultMsg="";
//		for (DVTNode d1: dns)
//				resultMsg+=getPredecessorMessage(d1, nodes, stmtStrs, showDetail);
//		return resultMsg;
//	}
//	
//	public static String getSuccessorMessageToWrite(DVTNode dn, ArrayList<String> stmtStrs, boolean showDetail) {	
//		int oldSize=0;
//		HashSet<DVTNode> resultS=new HashSet();
//		//HashSet<DVTNode> visited=new HashSet();
//		String stmtStr="";
//		String messages="";
//		String midStr="";
//		String preStr="";
//		String resultMsg="";
//		DVTNode tmpNode=null;
//		Set<String> messageSet=new HashSet();
//		boolean sizeIncremented=true;
//		//try 
//		{
//			resultS.add(dn);
//			sourceNodes.add(dn);
//			//System.out.println("sourceNodes="+sourceNodes);
//			int count=1;
//			while (sizeIncremented)
//			{	
//				Object[] nodeS=resultS.toArray();
//				for (int i=0;i<nodeS.length;i++)
//				{						
//					DVTNode d0=(DVTNode)nodeS[i];
//					//if (visited.contains(d0))
//					//	continue;
//					stmtStr=stmtStrs.get(d0.getStmt()).toLowerCase();
//					if (stmtStr.indexOf("<java.net.Socket: java.io.OutputStream getOutputStream()>")<=-1 && stmtStr.indexOf("<java.io.ObjectOutputStream: void writeObject(java.lang.Object)>")<=-1 &&
//						stmtStr.indexOf("java.nio.channels.SocketChannel")<=-1) 
//					{						
//						HashSet<DVTNode> tmpHs=new HashSet();
//						tmpHs=getNextNodes(d0);
//						Iterator iterator = tmpHs.iterator();  
//				        while (iterator.hasNext()) {  
//				        	tmpNode=(DVTNode)iterator.next();
//				        	if (resultS.contains(tmpNode))
//				        		continue;
//				        	if (showDetail)  {
//				        		midStr=oneNodeInfo(d0,stmtStrs)+" --> "+oneNodeInfo(tmpNode,stmtStrs)+"\n";
//				        	}
//				        	else
//				        		midStr=d0+" --> "+tmpNode+"\n";
//				        	if (!messageSet.contains(midStr))  {	
//				        		messageSet.add(midStr);
//				        		preStr=""; //+count+":";
//					        	for (int j=0;j<count; j++)
//					        	{
//					        		preStr+=" ";
//					        	}				        		
//				        		resultMsg+=preStr+midStr;
//				        		sourceNodes.add(tmpNode);
//				        	}
//				        	sourceMethods.add(tmpNode.getMethod());			        		
//				        }   	
//				        //visited.addAll(resultS);
//						resultS.addAll(tmpHs);
//						sourceNodes.add(d0);
//						//System.out.println("sourceNodes="+sourceNodes);
//					}
//					else
//					{
//						sendMsgNodes.add(d0);
//						System.out.println("sendMsgNodes="+sendMsgNodes);
//					}
//				}				
//				count++;
//				if (resultS.size()==oldSize)
//				{	
//					sizeIncremented=false;
//					break;
//				}	
//				oldSize=resultS.size();
//			}	
//			
//		}		
//		return resultMsg;
//	}
//
//	public static String getPredecessorMessageToRead(DVTNode dn, ArrayList<String> stmtStrs, boolean showDetail) {	
//		int oldSize=0;
//		HashSet<DVTNode> resultS=new HashSet();
//		String stmtStr="";
//		String messages="";
//		String midStr="";
//		String preStr="";
//		String resultMsg="";
//		DVTNode tmpNode=null;
//		Set<String> messageSet=new HashSet();
//		ArrayList messageA = new ArrayList();
//		boolean sizeIncremented=true;
//		//try 
//		{
//			resultS.add(dn);
//			sinkNodes.add(dn);
//			int count=1;
//			while (sizeIncremented)
//			{	
//				Object[] nodeS=resultS.toArray();
//				for (int i=0;i<nodeS.length;i++)
//				{	
//					DVTNode d0=(DVTNode)nodeS[i];
//					stmtStr=stmtStrs.get(d0.getStmt()).toLowerCase();
//					if (stmtStr.indexOf("<java.net.Socket: java.io.InputStream getInputStream()>")<=-1 && stmtStr.indexOf("<java.io.ObjectInputStream: java.lang.Object readObject()>")<=-1 &&
//							stmtStr.indexOf("java.nio.channels.SocketChannel")<=-1) 
//					{						
//						HashSet<DVTNode> tmpHs=new HashSet();
//						tmpHs=getPrevNodes(d0);
//						Iterator iterator = tmpHs.iterator();  
//				        while (iterator.hasNext()) {    
//				        	tmpNode=(DVTNode)iterator.next();
//				        	if (resultS.contains(tmpNode))
//				        		continue;
//				        	if (showDetail)  {
//				        		//midStr=oneNodeInfo(d0,stmtStrs)+" <-- "+oneNodeInfo(tmpNode,stmtStrs)+"\n";
//				        		midStr=oneNodeInfo(tmpNode,stmtStrs)+" --> "+oneNodeInfo(d0,stmtStrs)+"\n";
//				        	}
//				        	else
//				        		midStr=tmpNode+" --> "+d0+"\n";
//				        		//midStr=d0+" <-- "+tmpNode+"\n";				        	
//				        	if (!messageSet.contains(midStr))  {	
//				        		messageSet.add(midStr);
//				        		preStr=""; //+count+":";
//					        	for (int j=0;j<count; j++)
//					        	{
//					        		preStr+=" ";
//					        	}				        		
//				        		//resultMsg+=preStr+midStr;
//					        	//System.out.println("LineStr="+preStr+midStr);
//					        	messageA.add(preStr+midStr);
//				        	}
//				        	sinkMethods.add(tmpNode.getMethod());
//				        }   					
//						resultS.addAll(tmpHs);
//						sinkNodes.add(d0);
//					}
//					else
//					{
//						receiveMsgNodes.add(d0);
//					}				
//				}				
//				count++;
//				if (resultS.size()==oldSize)
//				{	
//					sizeIncremented=true;
//					break;
//				}	
//				oldSize=resultS.size();
//			}	
//			
//		}		
//		Collections.reverse(messageA);
//		for(int i = 0;i < messageA.size(); i++){
//			resultMsg+=messageA.get(i);           
//        }
//
//		return resultMsg;
//	}
//	
//	public static String getSuccessorMessageToWrite(HashSet<DVTNode> dns, ArrayList<String> stmtStrs, boolean showDetail) {	
//		String resultMsg="";
//		for (DVTNode d1: dns)
//				resultMsg+=getSuccessorMessageToWrite(d1,stmtStrs, showDetail);
//		return resultMsg;
//	}
//	
//	public static String getPredecessorMessageToRead(HashSet<DVTNode> dns, ArrayList<String> stmtStrs, boolean showDetail) {	
//		String resultMsg="";
//		for (DVTNode d1: dns)
//				resultMsg+=getPredecessorMessageToRead(d1, stmtStrs, showDetail);
//		return resultMsg;
//	}	
	public static ArrayList getStmtStrsFromFiles(String stmtFile, HashSet usedMethods) {
		ArrayList  strToId = new ArrayList(); 
        FileReader reader = null;  
        BufferedReader br = null;     
        try {  
   
            reader = new FileReader(stmtFile);  
            br = new BufferedReader(reader);              
            String str = null;  
            //HashSet usedMethods=dtUtil.getListSet(methodFile);
            String methodStr="";
            while ((str = br.readLine()) != null) {  
            	//System.out.println("str="+str);
            	if (str.length()>1)
            	{
            		String strs[]=str.split("> - ");
                	//System.out.println("strs.length="+strs.length+" strs[1]="+strs[1]);
            		if (strs.length<2)
            		{	
            			strToId.add(str);
            		}
            		else
            		{
            			methodStr=strs[0]+">";
            			if (usedMethods != null && usedMethods.size()>1)
            			{	
	            			if (usedMethods.contains(methodStr))
	            				strToId.add(strs[1].trim());
            			}
            			else
            			{
            				strToId.add(strs[1].trim());
            			}
            		}
            	}
            }     
            br.close();  
            reader.close();     
            return strToId;   
        } catch (Exception e) {  
            e.printStackTrace();   
            return strToId;    
        }  
	}
	
	/** read per-test runtime statement coverage information */
	public static List readStmtCoverageInt(String traceDir, int tId) {
		String fnOut = traceDir  + File.separator + "stmtCoverage" + tId + ".out";
		String startMark = "Statements covered (based on branch coverage):";
		String startMark2 = "Total statements covered:";
		List coveredStmts = new ArrayList();
		//System.out.println("readStmtCoverage(String traceDir, int tId, List<Integer> coveredStmts fnOut=" + fnOut);
		try {
			File file1=new File(fnOut);
			if (!file1.exists())
				file1=new File("stmtCoverage" + tId + ".out");
			FileReader frdOut = new FileReader(file1);
			BufferedReader rin = new BufferedReader(frdOut);
			int tmpInt=-1;
			while (true) {
				String strLine = rin.readLine();
				//System.out.println("readStmtCoverage strLine ="+strLine);
				if (strLine == null) break;
				//if (strLine.startsWith(startMark)) {
				if (strLine.contains(startMark)) {
					String sub = strLine.substring(strLine.indexOf(startMark)+startMark.length()+1);
					//System.out.println("sub="+sub);
					List<String> stmtIds = dua.util.Util.parseStringList(sub,' ');
					//System.out.println("stmtIds="+stmtIds);
					//String[] stmtIds = sub.split(" ");
					for (String id : stmtIds) {
						tmpInt=Integer.valueOf(id);
						if (!coveredStmts.contains(tmpInt))
							coveredStmts.add(Integer.valueOf(id));
					}
				} 
				else if (strLine.contains(startMark2)) {
					String strs[]=strLine.split("covered: ");
					if (strs.length<2)  { continue; }
					String sub = strs[strs.length-1];
					//System.out.println("sub="+sub);
					List<String> stmtIds = dua.util.Util.parseStringList(sub,' ');
					//System.out.println("stmtIds="+stmtIds);
					//String[] stmtIds = sub.split(" ");
					for (String id : stmtIds) {
						tmpInt=Integer.valueOf(id);
						if (!coveredStmts.contains(tmpInt))
							coveredStmts.add(Integer.valueOf(id));
					}
				} 
			}
			
			rin.close();
			frdOut.close();			
		}
		catch (Exception e) { 
			e.printStackTrace();
			System.err.println("Error occurred when reading runtime coverage report from " + fnOut);			
		}
		//System.out.println("coveredStmts ="+coveredStmts);
		return coveredStmts;
	}
	
	public static List<String> readStmtCoverageStr(String traceDir, int tId, ArrayList stmtStrs) {
		String fnOut = traceDir  + File.separator + "stmtCoverage" + tId + ".out";
		String startMark = "Statements covered (based on branch coverage):";
		String startMark2 = "Total statements covered:";
		List<String> coveredStmtsStr = new ArrayList<String>();
		//System.out.println("readStmtCoverage(String traceDir, int tId, List<Integer> coveredStmts fnOut=" + fnOut);
		try {
			FileReader frdOut = new FileReader(new File(fnOut));
			BufferedReader rin = new BufferedReader(frdOut);
			//int tmpInt=-1;
			String tmpStr="";
			while (true) {
				String strLine = rin.readLine();
				//System.out.println("readStmtCoverage strLine ="+strLine);
				if (strLine == null) break;
				//if (strLine.startsWith(startMark)) {
				if (strLine.contains(startMark)) {
					String sub = strLine.substring(strLine.indexOf(startMark)+startMark.length()+1);
					//System.out.println("sub="+sub);
					List<String> stmtIds = dua.util.Util.parseStringList(sub,' ');
					//System.out.println("stmtIds="+stmtIds);
					//String[] stmtIds = sub.split(" ");
					for (String id : stmtIds) {
						tmpStr=(String)stmtStrs.get(Integer.valueOf(id));
						if (!coveredStmtsStr.contains(tmpStr))
							coveredStmtsStr.add(tmpStr);
					}
					//break;
				}
				else if (strLine.contains(startMark2)) {
					String strs[]=strLine.split("covered: ");
					if (strs.length<2)  { continue; }
					String sub = strs[strs.length-1].trim();
					//System.out.println("sub="+sub);
					List<String> stmtIds = dua.util.Util.parseStringList(sub,' ');
					//System.out.println("stmtIds="+stmtIds);
					//String[] stmtIds = sub.split(" ");
					for (String id : stmtIds) {
						tmpStr=(String)stmtStrs.get(Integer.valueOf(id));
						if (!coveredStmtsStr.contains(tmpStr))
							coveredStmtsStr.add(tmpStr);
					}
				} 
			}
			
			rin.close();
			frdOut.close();
		}
		catch (Exception e) { 
			e.printStackTrace();
			System.err.println("Error occurred when reading runtime coverage report from " + fnOut);			
		}
		//System.out.println("coveredStmts ="+coveredStmts);
		return coveredStmtsStr;
	}
	
}
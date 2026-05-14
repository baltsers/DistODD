/**
 * File: src/ODD/ODDStaticGraph.java
 * -------------------------------------------------------------------------------------------
 * Date			Author      Changes
 * -------------------------------------------------------------------------------------------
 * 03/22/2019	Developer		created; 
*/
package ODD;

import dua.Forensics;
import fault.StmtMapper;
import soot.*;
//import soot.util.dot.DotGraph;
//import EAS.*;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

//import profile.InstrumManager;
//import dua.global.ProgramFlowGraph;
//import dua.method.CFG;
//import dua.method.CFG.CFGNode;
//import dua.util.Util;
import soot.jimple.*;
import EAS.EAInst;
import EAS.EAOptions;
import EAS.EAStatic;
//import MciaUtil.utils;
//
//import edu.ksu.cis.indus.staticanalyses.dependency.DependencyXMLizer;
//import edu.ksu.cis.indus.staticanalyses.dependency.IDependencyAnalysis;
import edu.ksu.cis.indus.staticanalyses.dependency.InterferenceDAv1;
//import edu.ksu.cis.indus.staticanalyses.dependency.InterferenceDAv2;
//import edu.ksu.cis.indus.staticanalyses.dependency.InterferenceDAv3;
import edu.ksu.cis.indus.staticanalyses.dependency.ReadyDAv1;
//import edu.ksu.cis.indus.staticanalyses.dependency.ReadyDAv2;
//import edu.ksu.cis.indus.staticanalyses.dependency.ReadyDAv3;
import edu.ksu.cis.indus.staticanalyses.dependency.SynchronizationDA;
//import edu.ksu.cis.indus.staticanalyses.interfaces.IValueAnalyzer;
import edu.ksu.cis.indus.staticanalyses.tokens.ITokens;
import edu.ksu.cis.indus.staticanalyses.dependency.DependencyXMLizerCLI;

public class ODDStaticGraph extends EAInst {
	static boolean staticIndus = true;
	static boolean staticContextSensity = true;	
	static boolean staticFlowSensity = true;	
	protected static ODDOptions opts = new ODDOptions();
	static String staticConfigurations="";
	static String dynamicConfigurations="";
	static String configurations="";
	// a flag ensuring timeout of static  
	//static boolean timeOutStatic = false;
	// timeOut time of static 
	//static long timeOutTimeStatic=Long.MAX_VALUE;            // 
	//static String staticTimeFileName= "staticTimes"+getProcessIDString()+".txt";
	//static String staticConFigurationFileName= "staticConfiguration"+getProcessIDString()+".txt";
	public static void main(String args[]){
		args = preProcessArgs(opts, args);

		ODDStaticGraph osg = new ODDStaticGraph();
		// examine catch blocks
		dua.Options.ignoreCatchBlocks = false;
		//Scene.v().addBasicClass("ODD.ODDMonitor");
		Forensics.registerExtension(osg);
		Forensics.main(args);
	}

	@Override protected void init() {
//		clsMonitor = Scene.v().getSootClass("ODD.ODDMonitor");
//		clsMonitor.setApplicationClass();
//		mInitialize = clsMonitor.getMethodByName("initialize");
//		mEnter = clsMonitor.getMethodByName("enter");
//		mReturnInto = clsMonitor.getMethodByName("returnInto");
//		mTerminate = clsMonitor.getMethodByName("terminate");
//		
//		long long1=ODDUtil.readTimeOutFromFile("StaticGraphCreate ", "timeouts.txt");
//		if (long1>0)
//			timeOutTimeStatic=long1;
//		System.out.println("long1="+long1+" timeOutTimeStatic="+timeOutTimeStatic);
//    	getStaticConfigurations("staticConfiguration.txt");
//    	System.out.println("staticFlowSensity="+staticFlowSensity+" staticContextSensity="+staticContextSensity);
    	
	}
//	public static void getStaticConfigurations(String configurationFile)  {
//    	String staticConfigurations=ODDUtil.readToString(configurationFile);
//    	if (staticConfigurations.length()>=2)
//    	{
//    		String staticFlag="";
//    		//System.out.println("staticConfigurations.substring(0,1)=" + staticConfigurations.substring(0,1)); 
//    		staticFlag=staticConfigurations.substring(0,1);
//    		//System.out.println("staticFlag=" + staticFlag); 
//    		if (staticFlag.equals("0") || staticFlag.equals("f") ||staticFlag.equals("F")) {
//    			staticContextSensity=false;
//    		}
//    		else {
//    			staticContextSensity=true;
//    		}   
//
//    		staticFlag=staticConfigurations.substring(1,2);
//    		//System.out.println("staticFlag=" + staticFlag); 
//    		if (staticFlag.equals("0") || staticFlag.equals("f") ||staticFlag.equals("F")) {
//
//			staticFlowSensity=false;
//			}
//			else {
//				staticFlowSensity=true;
//			}
//    		System.out.println("staticFlowSensity="+staticFlowSensity+" staticContextSensity="+staticContextSensity); 
//    	}		
//	}
	@Override public void run() {
		final long startTime = System.currentTimeMillis();
		Stmt[] idToS= StmtMapper.getCreateInverseMap();		
		//ODDUtil.createOrCopyFile("staticTimes.txt","0 0 0 0",staticTimeFileName);
		//ODDUtil.createOrCopyFile("staticConfiguration.txt","11",staticConFigurationFileName);
	     if (opts.debugOut()) 
	     {
	    	 System.out.println("Running DistODD Static Graph");
	     }

    	long thisResult = 0;
		// 1. create the static value transfer graph	    
	     try {
	    	 
//			long long1=ODDUtil.readTimeOutFromFile("StaticGraphCreate", "timeouts.txt");
//			if (long1>0)
//				timeOutTimeStatic=long1;
	    	 
	 		configurations=ODDUtil.readLastLine(ODDUtil.getNewestFile ("", "Configuration"));
			staticConfigurations=configurations.substring(0, 2);
			if (staticConfigurations.substring(0, 1).equals("0") || staticConfigurations.substring(0, 1).equals("f")  || staticConfigurations.substring(0, 1).equals("F"))  {
				staticFlowSensity=false;
			}
			else
				staticFlowSensity=true;
			if (staticConfigurations.substring(1, 2).equals("0") || staticConfigurations.substring(1, 2).equals("f")  || staticConfigurations.substring(1, 2).equals("F"))  {
				staticContextSensity=false;
			}
			else
				staticContextSensity=true;
			System.out.println("staticConfigurations="+staticConfigurations+" staticFlowSensity="+staticFlowSensity+" staticContextSensity="+staticContextSensity);
	    	int ret = createVTGWithIndus(staticIndus,staticFlowSensity, staticContextSensity);
			System.out.println("createVTGWithIndus() took " + (System.currentTimeMillis() - startTime) + " ms");
			//long runResult=buildStaticGraph(timeOutTimeStatic);
			//ODDUtil.updateTimeFromConfigurationToFile("staticConfiguration.txt", "staticTimes.txt", runResult);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   System.out.println("createVTGWithIndus() System.exit(0);");
	   System.exit(0);
	}
	private static int createVTGWithIndus(boolean flagIndus, boolean flowS, boolean contextS) {
		StaticTransferGraph vtg = new StaticTransferGraph();
		StaticTransferGraph vtg2 = new StaticTransferGraph();
		try {
			final long startTime = System.currentTimeMillis();
			//if (0==vtg.buildGraph(opts.debugOut())) return 0;
//
//			vtg.setFlowSensitivity(flowS);	
//			vtg.setContextSensitivity(contextS);	
//			vtg.setHeFlowSens(flowS);	
			vtg.buildGraph(opts.debugOut(),flowS, contextS);
				
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


	

		}
		catch (Exception e) {
			System.out.println("Error occurred during the construction of VTG");
			e.printStackTrace();
			return -1;
		}
		
		if (opts.debugOut()) {
			vtg2.dumpGraphInternals(true);
		}
		else {
			System.out.println(vtg2);
		}
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
		
		return 0;
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
	
	public static String getProcessIDString() {
		return ManagementFactory.getRuntimeMXBean().getName().replaceAll("[^a-zA-Z0-9]", "");
	}
} 
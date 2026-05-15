/**
 * File: src/ODD/ODDInst.java
 * -------------------------------------------------------------------------------------------
 * Date			Author      Changes
 * -------------------------------------------------------------------------------------------
 * 03/22/2019	Developer		created; for DistODD instrument
*/
package ODD;

import dua.Forensics;
import fault.StmtMapper;
import soot.*;
//import soot.util.dot.DotGraph;
//import EAS.*;

import java.io.File;
import java.io.IOException;
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

public class ODDInst extends EAInst {
	static boolean staticIndus = true;
	static boolean staticContextSensity = true;	
	static boolean staticFlowSensity = true;	
	protected static ODDOptions opts = new ODDOptions();

	// a flag ensuring timeout of static  
	static boolean timeOutStatic = false;
	// timeOut time of static 
	static long timeOutTimeStatic=Long.MAX_VALUE;            // 
	
	public static void main(String args[]){
		args = preProcessArgs(opts, args);

		ODDInst inst = new ODDInst();
		// examine catch blocks
		dua.Options.ignoreCatchBlocks = false;
		Scene.v().addBasicClass("ODD.ODDMonitor");
		Forensics.registerExtension(inst);
		Forensics.main(args);
	}
	
	@Override protected void init() {
		clsMonitor = Scene.v().getSootClass("ODD.ODDMonitor");
		clsMonitor.setApplicationClass();
		mInitialize = clsMonitor.getMethodByName("initialize");
		mEnter = clsMonitor.getMethodByName("enter");
		mReturnInto = clsMonitor.getMethodByName("returnInto");
		mTerminate = clsMonitor.getMethodByName("terminate");

    	
	}


	@Override public void run() {
		final long startTime = System.currentTimeMillis();
		Stmt[] idToS= StmtMapper.getCreateInverseMap();		
	     if (opts.debugOut()) 
	     {
	    	 System.out.println("Running DistODD Instrumentation");
	     }

		// Instrument EAS events
	     final long instrumentStartTime = System.currentTimeMillis();
	     instrument(); 
	     System.out.println("instrument() took " + (System.currentTimeMillis() - instrumentStartTime) + " ms");
	     System.out.println("All run() took " + (System.currentTimeMillis() - startTime) + " ms");
	}

} 
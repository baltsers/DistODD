/**
 * File: src/EAS/EAInst.java
 * -------------------------------------------------------------------------------------------
 * Date			Author      Changes
 * -------------------------------------------------------------------------------------------
 * 05/17/13		hcai		created; for instrumenting EAS method events
 * 05/20/13		hcai		add all probes required;
 * 05/21/13		hcai		fix all bugs till it works for normal returns and catch blocks;
 *							add Jimple code output before and after instrumentation for debugging purposes
 * 05/22/13		hcai		identify finally blocks and insert probes in each one of them; tested and working 
 * 05/23/13		hcai		move program start event probe from entry method to <clinit> to fix the exception
 *							thrown in the call to the entry probe in <clinit> before the monitor initialization, 
 *							which executes before the program entry
 * 05/24/13		hcai		fix program end event probe insertions in all the three possible places, 
 *							normal returns+catch block + finally block so that the termination monitor 
 *							is executed exactly once - this bug is found when running the implementation with
 *							XML-security; 
 *							fix program start probe to ensure monitor initialization is executed before each run by
 *							moving the probe back to the entry method, reset global timestamp in the initialization 
 *							instead to fix the previous bug for which the probe was moved to <clinit>; a "-sclinit" option
 *							is added for strictly probing the REAL program start in <clinit> when the instrumented
 *							subject is expected to run independently rather than via EARun
 * 05/25/13		hcai		critical fix: in the entry method, END event probe can be misplaced in catch blocks in the 
 *							absence of any finally block; fixed by ensuring each such probe is inserted at the end of
 *							each catch block; in the presence of finally blocks, no such probe is inserted in the
 *							related catch blocks and the probe is placed at the end of the finally block after which
 *							there is no more call sites
 * 05/26/13		hcai		replace lengthy strings used as parameters in various probes with the hashCode of the probe's 
 *							contextual object such as traps or call sites, to reduce the size of instrumented code
 * 06/07/13		hcai		important change: simplified program END probes by always simply inserting probe before the 
 *							returnstmt
 * 06/10/13		hcai		fix the assertion for the number of returns in the entry method - there might be multiple
 *							returns 
 * 07/04/13		hcai		if pertinent option specified, wrap around the whole body of a given method the outermost
 *							try-catch blocks like try{ original body ...} catch(Exception e) { throw e }
 * 07/05/13		hcai		factor reusable code out as common utilities for the whole mcia project 
 * 07/17/13		hcai		add missing probes for call sites within catch blocks and finally blocks
 * 10/09/13		hcai		add callee signature in the returnedInto probe invocations as the second argument, instead
 *							just the method name alone, for dumping dynamic call maps
 * 07/17/14		hcai		fixed import inconsistency bug at line 552 and 572: change the string pattern to be matched to 
 * 							from "EAS.Monitor: void returnedInto" to "Monitor: void returnedInto" so that Diver's customized monitors
 * 							can be matched too! (this issue caused, for the first time with PDFBox and then with BCEL, different lines of 
 * 							instrumented code between EAInstr and DiverInstr and subsequently diverged method sequences between
 * 							EARun and DiverRun, for the same input bytecode and test suite. (debugging on this cost almost a day!)
 * 02/05/15		hcai		for entry methods having 'return' statements, instrument 'terminate' before the last unit of their cfgs 
 * 02/16/15		hcai		added instrumentation dealing with forcible program termination via "System.exit" invocation 
*/
package EAS;

//import java.io.File;
//import java.util.ArrayList;
//import java.util.LinkedHashMap;
//import java.util.LinkedHashSet;
//import java.util.List;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.Set;
//
//import profile.InstrumManager;
import dua.Extension;
//import dua.Forensics;
//import dua.global.ProgramFlowGraph;
//import dua.method.CFG;
//import dua.method.CFG.CFGNode;
//import dua.method.CallSite;
//import dua.util.Util;
//import soot.*;
//import soot.jimple.*;
////import soot.tagkit.Tag;
////import soot.tagkit.VisibilityAnnotationTag;
//import soot.toolkits.graph.Block;
//import soot.toolkits.graph.BlockGraph;
//import soot.toolkits.graph.ExceptionalBlockGraph;
//import soot.util.*;
//import MciaUtil.*;

public class EAStatic implements Extension {
	
//	protected SootClass clsMonitor;
//	
//	protected SootMethod mInitialize;
//	protected SootMethod mEnter;
//	protected SootMethod mReturnInto;
//	protected SootMethod mTerminate;
//	
//	protected File fJimpleOrig = null;
//	protected File fJimpleInsted = null;
	
	protected static boolean bProgramStartInClinit = false;
	protected static EAOptions opts = new EAOptions();
	
	public static void main(String args[]){
		args = preProcessArgs(opts, args);

		EAStatic es = new EAStatic();
		// examine catch blocks
		dua.Options.ignoreCatchBlocks = false;
		
		//Scene.v().addBasicClass("EAS.Monitor");
		
		EAForensics.registerExtension(es);
		EAForensics.main(args);
	}
	
	protected static String[] preProcessArgs(EAOptions _opts, String[] args) {
		opts = _opts;
		args = opts.process(args);
		
		String[] argsForDuaF;
		int offset = 0;

		argsForDuaF = new String[args.length + 2 - offset];
		System.arraycopy(args, offset, argsForDuaF, 0, args.length-offset);
		argsForDuaF[args.length+1 - offset] = "-paramdefuses";
		argsForDuaF[args.length+0 - offset] = "-keeprepbrs";
		
		return argsForDuaF;
	}
	
	/**
	 * Descendants may want to use customized event monitors
	 */
	protected void init() {
//		clsMonitor = Scene.v().getSootClass("EAS.Monitor");
//		clsMonitor.setApplicationClass();
//		
//		
//		mInitialize = clsMonitor.getMethodByName("initialize");
//		mEnter = clsMonitor.getMethodByName("enter");
//		mReturnInto = clsMonitor.getMethodByName("returnInto");
//		mTerminate = clsMonitor.getMethodByName("terminate");
	}
	
	public void run() {
		System.out.println("Running EAS extension of DUA-Forensics");
		//StmtMapper.getCreateInverseMap();

		//instrument();
	}
		

} // -- public class EAInst  

/* vim :set ts=4 tw=4 tws=4 */


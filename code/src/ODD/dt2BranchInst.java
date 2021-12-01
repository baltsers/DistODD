package ODD;

import dua.Forensics;
import fault.StmtMapper;
import soot.*;
import soot.util.dot.DotGraph;
import EAS.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import profile.BranchInstrumenter;
import EAS.EAInst;
import MciaUtil.RDFCDBranchEx;
import MciaUtil.utils;
import dua.Forensics;
import dua.global.ProgramFlowGraph;
import dua.method.CFGDefUses.Branch;
import dua.util.Util;
import fault.StmtMapper;
import soot.Scene;
import soot.SootClass;
import soot.jimple.Stmt;


public class dt2BranchInst extends EAInst {
	protected static ODDOptions opts = new ODDOptions();
	//distEA variables
//	protected SootMethod mReturnFrom;
//	
//	protected SootMethod mNioRead;
//	protected SootMethod mNioWrite;
//	protected SootClass cDistSockInStream;
//	protected SootClass cDistSockOutStream;
//	
//	protected SootMethod mObjStreamRead;
//	protected SootMethod mObjStreamWrite;
//	public static int methodNum=0;
	//private List<SootMethod> remoteMethods = new ArrayList<SootMethod>();
	//public static ArrayList remoteMethods = new ArrayList();
	
	// branch Instrument variables	
	/** the RDF/CD branch analyzer */
	private final static RDFCDBranchEx rdfCDBranchAnalyzer = RDFCDBranchEx.inst();
	
	/** if remove repeated branches in terms of same targets */
	public static boolean removeRepeatedBrs = true;
	protected SootClass clsMonitor;
	public static void main(String args[]){
		args = preProcessArgs(opts, args);

		dt2BranchInst d2r = new dt2BranchInst();
		// examine catch blocks
		dua.Options.ignoreCatchBlocks = false;
		Scene.v().addBasicClass("profile.BranchReporter");
		Scene.v().addBasicClass("ODD.dt2BranchMonitor");
		Forensics.registerExtension(d2r);
		Forensics.main(args);
	}
	
	@Override protected void init() {
		clsMonitor = Scene.v().getSootClass("profile.BranchReporter");
		clsMonitor.setApplicationClass();
		clsMonitor = Scene.v().getSootClass("ODD.dt2BranchMonitor");
		clsMonitor.setApplicationClass();
		
		mInitialize = clsMonitor.getMethodByName("initialize");
		mEnter = clsMonitor.getMethodByName("enter");
		mReturnInto = clsMonitor.getMethodByName("returnInto");
		mTerminate = clsMonitor.getMethodByName("terminate");
	}
	
	@Override public void run() {
System.out.println("Running Diver branch coverage instrumenter of DUA-Forensics");
		
		// 1. dump branch->CD stmts 
		dumpBranchCDStmts();
		
		// 2. instrument branch coverage monitors
		if (opts.dumpJimple()) {
			fJimpleOrig = new File(Util.getCreateBaseOutPath() + "JimpleOrig.out");
			utils.writeJimple(fJimpleOrig);
		}
		
		rdfCDBranchAnalyzer.removeAssistantNodes();
		instrumentAllBranches();
		instrumentTerminate();	   
		if (opts.dumpJimple()) {
			fJimpleInsted = new File(Util.getCreateBaseOutPath() + "JimpleInstrumented.out");
			utils.writeJimple(fJimpleInsted);
		}
	}
	protected void dumpBranchCDStmts() {
		// instantiate all intraprocedural CDGs, each per method
		/** don't use the standard algorithm for now: instead, follow the same algorithm of CD 
		 * computation as DuaF - RDF and "other (non-RDF)" CD branches, so the following is commented out*/
		//computeAllIntraCDs();
		
		// determine, for each branch, all stmts that are control dependent on it
		System.out.println("dt2BranchInst dumpBranchCDStmts() 0");
		Map<Stmt, Integer> stmtIds = StmtMapper.getWriteGlobalStmtIds();
		System.out.println("dt2BranchInst dumpBranchCDStmts() 0_1");
		List<Branch> allBranches = rdfCDBranchAnalyzer.getAllBranches();
		System.out.println("dt2BranchInst dumpBranchCDStmts() 1");
		if (removeRepeatedBrs) {
			allBranches = rdfCDBranchAnalyzer.getAllUniqueBranches();
		}
		Map<Branch, Set<Stmt>> br2cdstmts = rdfCDBranchAnalyzer.buildBranchToCDStmtsMap(allBranches, stmtIds);
		
		String suffix = "branch";
		File fBranchStmt = new File(Util.getCreateBaseOutPath() + "entitystmt.out." + suffix);
		try {
			// write always a new file, deleting previous contents (if any)
			BufferedWriter writer = new BufferedWriter(new FileWriter(fBranchStmt));
			
			// branches are assumed to be ordered by id
			for (Branch br : allBranches) {
				Set<Stmt> relStmts = br2cdstmts.get(br);
				for (Stmt s : relStmts) {
					writer.write(stmtIds.get(s) + " ");
				}
				writer.write("\n");
			}
			
			writer.flush();
			writer.close();
		}
		catch (FileNotFoundException e) { System.err.println("Couldn't write ENTITYSTMT '" + suffix + "' file: " + e); }
		catch (SecurityException e) { System.err.println("Couldn't write ENTITYSTMT '" + suffix + "' file: " + e); }
		catch (IOException e) { System.err.println("Couldn't write ENTITYSTMT '" + suffix + "' file: " + e); }
	}
	/** instrument branch coverage monitors for all branches */
	protected int instrumentAllBranches() {
		List<Branch> allBranches = rdfCDBranchAnalyzer.getAllBranches();
		if (removeRepeatedBrs) {
			allBranches = rdfCDBranchAnalyzer.getAllUniqueBranches();
		}
		// instrument using DuaF facilities
		BranchInstrumenter brInstrumenter = new BranchInstrumenter(true);
		ArrayList remoteMethods=ODDUtil.getArrayList(System.getProperty("user.dir") + File.separator + "methodList.out");
		if (remoteMethods.size()<1)  {
			brInstrumenter.instrumentDirect(allBranches, ProgramFlowGraph.inst().getEntryMethods());
		}
		else  {
			brInstrumenter.instrumentDirect(allBranches, ProgramFlowGraph.inst().getEntryMethods(),remoteMethods);
		}	
		return 0;
	} 

} 
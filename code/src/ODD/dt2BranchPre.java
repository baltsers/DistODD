package ODD;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import profile.BranchInstrumenter;
import EAS.EAInst;
import MciaUtil.RDFCDBranchEx;
import MciaUtil.utils;
import ODD.ODDOptions;
import dua.Forensics;
import dua.global.ProgramFlowGraph;
import dua.method.CFGDefUses.Branch;
import dua.util.Util;
import fault.StmtMapper;
import soot.Scene;
import soot.SootClass;
import soot.jimple.Stmt;

public class dt2BranchPre extends EAInst {
	/** the map from a method to its exceptional CDG (intraprocedural) */
	//private final Map<SootMethod, StaticCDGraphEx> me2CDG = new LinkedHashMap<SootMethod, StaticCDGraphEx>();
	
	protected static ODDOptions opts = new ODDOptions();
	
	/** the RDF/CD branch analyzer */
	private final static RDFCDBranchEx rdfCDBranchAnalyzer = RDFCDBranchEx.inst();
	
	/** if remove repeated branches in terms of same targets */
	public static boolean removeRepeatedBrs = true;
	protected SootClass clsMonitor;
	public static void main(String args[]){
		args = preProcessArgs(opts, args);

		dt2BranchPre d2p = new dt2BranchPre();
		// examine catch blocks
		dua.Options.ignoreCatchBlocks = false;
		Scene.v().addBasicClass("profile.BranchReporter");
		Scene.v().addBasicClass("disttaint.dt2BranchMonitor");
		Forensics.registerExtension(d2p);
		Forensics.main(args);
	}
	@Override public void init() {
		clsMonitor = Scene.v().getSootClass("profile.BranchReporter");
		clsMonitor.setApplicationClass();
		
		clsMonitor = Scene.v().getSootClass("disttaint.dt2BranchMonitor");
		clsMonitor.setApplicationClass();
//		clsBr = Scene.v().getSootClass("profile.BranchReporter");
//		clsBr.setApplicationClass();
		
		mInitialize = clsMonitor.getMethodByName("initialize");
		mEnter = clsMonitor.getMethodByName("enter");
		mReturnInto = clsMonitor.getMethodByName("returnInto");
		mTerminate = clsMonitor.getMethodByName("terminate");
	}
	@Override public void run() {
		System.out.println("Running dist branch coverage instrumenter of DUA-Forensics");
		
		// 1. dump branch->CD stmts 
		dumpBranchCDStmts();
		
		// 2. instrument branch coverage monitors
		if (opts.dumpJimple()) {
			fJimpleOrig = new File(Util.getCreateBaseOutPath() + "JimpleOrig.out");
			utils.writeJimple(fJimpleOrig);
		}
		System.out.println("rdfCDBranchAnalyzer.removeAssistantNodes()");
		rdfCDBranchAnalyzer.removeAssistantNodes();

		System.out.println("instrumentAllBranches()");
		instrumentAllBranches();
		
		System.out.println("instrumentTerminate()");
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
		Map<Stmt, Integer> stmtIds = StmtMapper.getWriteGlobalStmtIds();
		List<Branch> allBranches = rdfCDBranchAnalyzer.getAllBranches();
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
		//ArrayList remoteMethods=dtUtil.getArrayList(System.getProperty("user.dir") + File.separator + "methodList.out");

			brInstrumenter.instrumentDirect(allBranches, ProgramFlowGraph.inst().getEntryMethods());
		
		//brInstrumenter.instrumentDirect(allBranches, ProgramFlowGraph.inst().getEntryMethods());
		return 0;
	} // instrumentAllBranches
}
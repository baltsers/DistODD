package EAS;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import profile.BranchInstrumenter;
import profile.CatchWrapInstrumenter;
import profile.DUAInstrumenter;
import profile.EPPInstrumenter;
import profile.InstrumManager;
import profile.TestLabelInstrumenter;
import soot.PackManager;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.Transform;
import dua.DUA;
import dua.DUAAnalysis;
import dua.DUAssocSet;
import dua.Extension;

import dua.Options;
import dua.global.ProgramFlowGraph;
import dua.global.ReachabilityAnalysis;
import dua.global.ReqBranchAnalysis;
import dua.global.ProgramFlowGraph.EntryNotFoundException;
import dua.global.p2.P2Analysis;
import dua.method.CFG;
import dua.method.DominatorRelation;
import dua.method.EPPAnalysis;
import dua.method.CFG.CFGNode;
import dua.method.CFGDefUses.Branch;
import dua.method.CFGDefUses.Def;
import dua.method.CFGDefUses.Use;
import fault.BranchStmtMapper;
import fault.DUAStmtMapper;
import fault.PathMapper;
import fault.StmtMapper;

public class EAForensics extends SceneTransformer
{
	private static Collection<Extension> extensions = new ArrayList<Extension>();
	public static void registerExtension(Extension ext) { extensions.add(ext); }
	
	public static void main(String[] args) {
		String[] sootArgs = Options.parseFilterArgs(args);
		
		System.out.print("EAForensics DUA-Forensics args to Soot: ");
		for (String s : sootArgs)
			System.out.print(s + " ");
		System.out.println();
		
		// add this transformation, if not deactivated, and call soot main
		if (Options.doDuas())
			PackManager.v().getPack("wjtp").add(new Transform("wjtp.mt", new EAForensics()));
		
		if (Options.allowPhantomClasses())
			soot.options.Options.v().set_allow_phantom_refs(true);
		soot.Main.main(sootArgs);
	}
	
	public void run() {
		internalTransform("", new HashMap());
	}
	
	@Override
	protected void internalTransform(String phaseName, Map options) {
//		final boolean verbose = false; //true;
		
		System.out.println("EAForensics duaf internal transform");
		
		try { ProgramFlowGraph.createInstance(DUAAnalysis.getCFGFactory()); }
		catch (EntryNotFoundException e) 
		{ //throw new RuntimeException(e.getMessage()); 			
		}
		System.out.println("EAForensics duaf");
		// make sure (single) entry exists
		List<SootMethod> entryMethods = ProgramFlowGraph.inst().getEntryMethods();
		
		// having already found call sites, compute reachability
		if (Options.reachability())
			ReachabilityAnalysis.computeReachability(entryMethods);
		
		// the first thing is to perform p2 analysis
		P2Analysis.inst().compute();
				
		if (!Options.skipDUAAnalysis) { /** added by hcai: to bypass unnecessary computations for Tracer/SimplyDep */
			// note that local dominance was computed when mtags where created
			DominatorRelation.createInstance(); // option dominance() used inside to decide btw interproc dom or just local dom
			
			DUAAnalysis.createDUAAnalysis();
			DUAAnalysis duaAnalysis = DUAAnalysis.inst();
			
			// do Required Branches Analysis for the set of all CFGs
			ReqBranchAnalysis.createInstance();
			ReqBranchAnalysis reqBrAnalysis = ReqBranchAnalysis.inst();
			reqBrAnalysis.print(); // DEBUG
	//		outputDUAReqBranches(duaAnalysis); // DEBUG
			
			// Retrieve set of DUAs, required branches, and acyclic paths
			DUAssocSet duaSet = duaAnalysis.getDUASet();
			List<DUA> duas = duaSet.getAllDUAs();
			if (Options.duaVerbose()) {
				System.out.println("DUAs:");
				for (DUA dua : duas)
					System.out.println(" " + dua);
			}
			List<Branch> brs = Options.instrAllBranches()? reqBrAnalysis.getAllBranches() : reqBrAnalysis.getInstrBranches(duas);
			Map<CFG, EPPAnalysis> cfgEPPAnalyses = Options.eppInstr()?
					EPPAnalysis.computeInterprocEPP(Options.eppDepth(), ProgramFlowGraph.inst().getCFGs()) : null;
			
			// DEBUG
	//		PathDUAAnalysis pathDuaAnalysis = new PathDUAAnalysis(duaAnalysis.getDUASet());
	//		pathDuaAnalysis.computeInferability();
			
			// Output stmt mapping files for debugging (coverage report files)
			// These files are generated BEFORE instrumenting, to avoid including statements inserted later

			
			////////////////////////
			// Instrument program

		}
		// run extensions
		for (Extension ext : extensions)
			ext.run();
		
		if (!Options.skipDUAAnalysis) { 
			// finally, instrument to print label at the beginning of each 'test*' method
			//   *** THIS GOES LAST to avoid other instrumentation at the beginning of 'test*' methods printing things *before* the test # is printed
			//if (InstrumManager.v().isThereAnyInstrumentation()) //Options.anyInstr())
			//	TestLabelInstrumenter.instrument(entryMethods);
		}
	}
	
	// DEBUG
//	private void outputDUAReqBranches(DUAAnalysis duaAnalysis) {
//		// DEBUG - output RDFs
//		ReqBranchAnalysis reqBrAnalysis = ReqBranchAnalysis.inst();
//		System.out.println("Required branches:");
//		int duaIdx = 0;
//		for (DUA dua : duaAnalysis.getDUASet().getAllDUAs()) {
//			CFGNode nDef = dua.getDef().getN();
//			
//			System.out.println(duaIdx++ + ":  " + dua + ": def " + reqBrAnalysis.getReqBranches(nDef) + 
//					", use " + reqBrAnalysis.getUseReqBranches(dua.getUse()));
//		}
//		
//		// also print same-BB DUs
//		System.out.println("Same-BB DUs:");
//		Map<Def, Set<Use>> sameBBDUs = duaAnalysis.getDUASet().getSameBBDUs();
//		for (Def def : sameBBDUs.keySet()) {
//			Set<Use> uses = sameBBDUs.get(def);
//			for (Use u : uses)
//				System.out.println("  " + def + ", " + u);
//		}
//	}
	
}
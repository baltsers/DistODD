package ODD;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import EAS.EAInst;
import dua.Forensics;
import edu.ksu.cis.indus.staticanalyses.dependency.DependencyXMLizerCLI;
import edu.ksu.cis.indus.staticanalyses.dependency.InterferenceDAv1;
import edu.ksu.cis.indus.staticanalyses.dependency.ReadyDAv1;
import edu.ksu.cis.indus.staticanalyses.dependency.SynchronizationDA;
import edu.ksu.cis.indus.staticanalyses.tokens.ITokens;
import fault.StmtMapper;
import soot.Scene;
import soot.SootClass;
import soot.jimple.Stmt;

public class ODDAllStaticGraphs extends EAInst {
//	static boolean staticIndus = true;
//	static boolean staticContextSensity = true;	
//	static boolean staticFlowSensity = true;	
	protected static ODDOptions opts = new ODDOptions();
//	static String staticConfigurations="";
//	static String dynamicConfigurations="";
//	static String configurations="";
	// a flag ensuring timeout of static  
	//static boolean timeOutStatic = false;
	// timeOut time of static 
	//static long timeOutTimeStatic=Long.MAX_VALUE;            // 
	//static String staticTimeFileName= "staticTimes"+getProcessIDString()+".txt";
	//static String staticConFigurationFileName= "staticConfiguration"+getProcessIDString()+".txt";
	public static void main(String args[]){
		args = preProcessArgs(opts, args);

		ODDAllStaticGraphs osg = new ODDAllStaticGraphs();
		// examine catch blocks
		dua.Options.ignoreCatchBlocks = false;
		//Scene.v().addBasicClass("ODD.ODDMonitor");
		Forensics.registerExtension(osg);
		Forensics.main(args);
	}

	@Override protected void init() {

	}

	@Override public void run() {
		final long startTime = System.currentTimeMillis();
		// create statement file stmtids.out
		//Stmt[] idToS= StmtMapper.getCreateInverseMap();		
		//ODDUtil.createOrCopyFile("staticTimes.txt","0 0 0 0",staticTimeFileName);
		//ODDUtil.createOrCopyFile("staticConfiguration.txt","11",staticConFigurationFileName);
	     if (opts.debugOut()) 
	     {
	    	 System.out.println("Running DistODD Static Graph");
	     }

    	long thisResult = 0;
		// 1. create the static value transfer graph	    
	     try {   	 
	    	createVTGWithIndus("00");
	    	long time00 = System.currentTimeMillis();
	    	System.out.println("createVTGWithIndus00 took " + (time00 - startTime) + " ms");
	    	createVTGWithIndus("01");
	    	long time01 = System.currentTimeMillis();
	    	System.out.println("createVTGWithIndus01 took " + (time01 - time00) + " ms");
	    	createVTGWithIndus("10");
	    	long time10 = System.currentTimeMillis();
	    	System.out.println("createVTGWithIndus10 took " + (time10 - time01) + " ms");
	    	createVTGWithIndus("11");
	    	System.out.println("createVTGWithIndus11 took " + (System.currentTimeMillis() - time10) + " ms");
			System.out.println("Four createVTGWithIndus() took " + (System.currentTimeMillis() - startTime) + " ms");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   System.out.println("createVTGWithIndus() System.exit(0);");
	   System.exit(0);
	}
	private static void createVTGWithIndus(String staticConfigurations) {
		StaticTransferGraph vtg = new StaticTransferGraph();
		StaticTransferGraph vtg2 = new StaticTransferGraph();
		boolean contextS=false;
		boolean flowS=false;
		if (staticConfigurations.substring(0, 1).equals("0") || staticConfigurations.substring(0, 1).equals("f")  || staticConfigurations.substring(0, 1).equals("F"))  {
			contextS=false;
		}
		else
			contextS=true;
			
		if (staticConfigurations.substring(1, 2).equals("0") || staticConfigurations.substring(1, 2).equals("f")  || staticConfigurations.substring(1, 2).equals("F"))  {
			flowS=false;
		}
		else
			flowS=true;
		
		try {
			final long startTime = System.currentTimeMillis();
			//if (0==vtg.buildGraph(opts.debugOut())) return 0;

			vtg.buildGraph(opts.debugOut(),flowS, contextS);
				
			System.out.println("vtg before Indus");
			System.out.println(vtg);
			final long AddEdgesWithIndusstartTime = System.currentTimeMillis();
			System.out.println("	createVTGWithIndus_"+staticConfigurations+"_buildGraph  took " + (AddEdgesWithIndusstartTime - startTime) + " ms");
			
				try {
					vtg2=AddEdgesWithIndus(vtg);
				} catch (Exception e)  
				{
					vtg2=vtg;
				}
				
				System.out.println("vtg2 after Indus");
				System.out.println(vtg2);
				//vtg.AddThreadEdge(opts.debugOut(),"Dependence.log");

			System.out.println("vtg2 after Indus");
			System.out.println(vtg2);
			//vtg.AddThreadEdge(opts.debugOut(),"Dependence.log");
			final long AddEdgesWithIndusstopTime = System.currentTimeMillis();
			System.out.println("	createVTGWithIndus_"+staticConfigurations+"_AddEdgesWithIndus took " + (AddEdgesWithIndusstopTime - AddEdgesWithIndusstartTime) + " ms");


	

		}
		catch (Exception e) {
			System.out.println("Error occurred during the construction of VTG");
			e.printStackTrace();
			//return -1;
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
			String fn = dua.util.Util.getCreateBaseOutPath() + "staticVtg_"+staticConfigurations+".dat";
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
		
		//return 0;
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
	

} 
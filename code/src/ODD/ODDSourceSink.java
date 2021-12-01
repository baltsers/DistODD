package ODD;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import soot.jimple.spark.SparkTransformer;
import soot.options.Options;
import soot.*;
import soot.jimple.*;

public class ODDSourceSink {
	public static String path = ""; //"C:/Research/nioecho/bin";  ///voldemort/rest/coordinator/admin
	static boolean debugOut = true;
	public static void main(String args[]) {
      if(args.length == 0)
      {
          System.out.println("Usage: dtSourceSink directory");
          System.exit(0);
      }            
      else
			System.out.println("[mainClass]"+args[0]);	
      path = args[0]; 
		
		HashSet<String> sources = new HashSet<>(); 
		HashSet<String> sinks = new HashSet<>(); 
		LinkedHashMap<String, String> sourceClassMethods=new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> sinkClassMethods=new LinkedHashMap<String, String>();
		HashSet<String> sourceMethods = new HashSet<>(); 
		HashSet<String> sinkMethods = new HashSet<>(); 
//		HashSet<String>  sourceMethods = new ArrayList();
//		ArrayList  sinkMethods = new ArrayList();
//		ArrayList  sourceStmts = new ArrayList();
//		ArrayList  sinkStmts = new ArrayList();
//		TreeSet<String> sourceMethods = new TreeSet<>(); 
//		TreeSet<String> sinkMethods = new TreeSet<>(); 		
//		String sourceMsg=""; 
//		String sinkMsg="";
		//String sourceFile=System.getProperty("user.dir") + File.separator + "source_1.txt";
		//String sinkFile=System.getProperty("user.dir") + File.separator + "sink_1.txt"; 
		String sourceSinkFile=System.getProperty("user.dir") + File.separator + "sourceSinkMethods.txt";
		// set soot options 1 and 2
		initial(path);
		enableSpark(path);
   	 	sources=dtConfig.getFirstHashSet("data/Sources.txt","\t"); 
//   	 	for (String s:sources) 
//   	 	{
//   	 		System.out.println("sources=" +s);
//   	 	}
   	 		
   	    Set<SootClass> sourceSootClses = new HashSet<SootClass>();
		for (String clsname : sources) {
			sourceSootClses.add( Scene.v().getSootClass(clsname) );
		}
		
		sinks=dtConfig.getFirstHashSet("data/Sinks.txt","\t"); 
     	Set<SootClass> sinkSootClses = new HashSet<SootClass>();
		for (String clsname : sinks) {
			sinkSootClses.add( Scene.v().getSootClass(clsname) );
		}
     	
   	    sourceClassMethods=dtConfig.getClassesMethods("data/Sources.txt", "\t");
   	    sinkClassMethods=dtConfig.getClassesMethods("data/Sinks.txt", "\t");
//        String sourceMsg=""; 
//        String sinkMsg="";
//        String allMsg="";
//        String methodPairMsg="";
//        String stmtPairMsg="";
		String midStr1="";
		String sourceItem="";
		String sinkItem="";
		//String methodSource1="";
		String methodSource2="";
		//String methodSink1="";
		String methodSink2="";
		String methodItem="";
//		boolean useSourceInterfaceFirstStmt=false;
//		boolean useSinkInterfaceFirstStmt=false;
		String sourceClassHierarchy="";
		String sinkClassHierarchy="";
		String sourceClassHierarchyMethod="";
		String sinkClassHierarchyMethod="";
		for (SootClass sClass:Scene.v().getApplicationClasses()) 
		{
			if ( sClass.isPhantom() ) {	continue; }
			if ( !sClass.isConcrete() ) {	continue; }
//			useSourceInterfaceFirstStmt=false;
//			useSinkInterfaceFirstStmt=false;
			sourceClassHierarchyMethod="";
			sourceClassHierarchy=getHierarchy(sClass, sourceSootClses);
			if (sourceClassHierarchy.length()>0)  //org.jboss.netty.channel.ChannelPipelineFactory
			{
				sourceClassHierarchyMethod=dtConfig.getMethods(sourceClassMethods, sourceClassHierarchy).replace("|", " "); 
				//System.out.println("The source hierarchy class for "+sourceClassHierarchy+" with method" +sourceClassHierarchyMethod +" is :" + sClass.getName());
				//useSourceInterface=true;
			}
			sinkClassHierarchyMethod="";
			sinkClassHierarchy=getHierarchy(sClass, sinkSootClses);
			if (sinkClassHierarchy.length()>0)
			{
				//System.out.println("The sink hierarchy class for "+sinkClassHierarchy+" is :" + sClass.getName());
				//useSinkInterface=true;
			}
		
			for(SootMethod m:sClass.getMethods())
			{
				if(!m.isConcrete()) {	continue; }
				methodSource2=m.toString();
				methodSink2=methodSource2;			
				//System.out.println("methodSource2="+methodSource2+"methodSource2.indexOf(sourceClassHierarchy)="+methodSource2.indexOf(sourceClassHierarchy));
				//Body b=m.retrieveActiveBody();
				//System.out.println("[body]"+b);
				//methodSource2=<voldemort.rest.coordinator.admin.CoordinatorAdminPipelineFactory: org.jboss.netty.channel.ChannelPipeline getPipeline()>midStr1=r0 := @this: voldemort.rest.coordinator.admin.CoordinatorAdminPipelineFactory
				Iterator<Unit> stmts=m.retrieveActiveBody().getUnits().snapshotIterator();
				while(stmts.hasNext())
				{
					Unit u=stmts.next();
					if(!(u instanceof IdentityStmt) && !(u instanceof AssignStmt) && !(u instanceof InvokeStmt) && !(u instanceof DefinitionStmt) && !(u instanceof RetStmt)) 
					{	continue; }
					midStr1=u.toString();

					//System.out.println("methodSource2="+methodSource2+"midStr1="+midStr1);
					if (midStr1.indexOf(" goto ")>=0 || midStr1.indexOf(" if ")>=0)  // || midStr1.indexOf("?")>=0 || midStr1.indexOf("(null)")>=0 || midStr1.indexOf("(\"")>=0 || midStr1.indexOf("\")")>=0)
					{
						continue;
					}

					if (sourceClassHierarchy.length()>0)
					{
						methodItem=dtConfig.getMethodItem(sourceClassMethods, sourceClassHierarchy,methodSource2, " | "); 
						if (methodItem.length()>1)  
						{
							System.out.println(methodSource2 +" - "+midStr1+" :("+sourceClassHierarchy+": "+methodItem+")");
							//sourceMsg+=methodSource2 +" - "+midStr1+" :("+sourceClassHierarchy+": "+methodItem+") \n";
	    					//if (!sourceMethods.contains(methodSource2))
	    						sourceMethods.add(methodSource2);
//	    					if (!sourceStmts.contains(methodSource2 +" - "+midStr1))
//	    						sourceStmts.add(methodSource2 +" - "+midStr1);
	    					break;
						}						
					}
					sourceItem= dtConfig.itemInList(midStr1,sources);			
					//System.out.println("midStr1="+midStr1+" sourceItem="+sourceItem+" methodSource2="+methodSource2);
					//System.out.println(" getMethodItem1="+dtConfig.getMethodItem(sourceClassMethods, sourceItem,midStr1, " | "));
					if (sourceItem.length()>1)  // && !methodSource1.equals(methodSource2))
					{
						
						methodItem=dtConfig.getMethodItem(sourceClassMethods, sourceItem,midStr1, " | ");
						//System.out.println(" getMethodItem1="+methodItem+" midStr1="+midStr1+ " indexOf="+midStr1.indexOf(methodItem));
						if (methodItem.length()>1 && midStr1.indexOf(methodItem)>=0)  
						{
							System.out.println(methodSource2 +" - "+midStr1+" :("+sourceItem+": "+methodItem+")");
							//sourceMsg+=methodSource2 +" - "+midStr1+" :("+sourceItem+": "+methodItem+") \n";
	    					//if (!sourceMethods.contains(methodSource2))
	    						sourceMethods.add(methodSource2);
//	    					if (!sourceStmts.contains(methodSource2 +" - "+midStr1))
//	    						sourceStmts.add(methodSource2 +" - "+midStr1);
	    					
						}
					}		
					if (sinkClassHierarchy.length()>0)
					{
						methodItem=dtConfig.getMethodItem(sinkClassMethods, sinkClassHierarchy,methodSink2, " | "); 
						if (methodItem.length()>1)  
						{
							System.out.println(methodSink2 +" - "+midStr1+" :("+sinkClassHierarchy+": "+methodItem+")");
							//sinkMsg+=methodSink2 +" - "+midStr1+" :("+sinkClassHierarchy+": "+methodItem+") \n";
	    					//if (!sinkMethods.contains(methodSink2))
	    						sinkMethods.add(methodSink2);
//	    					if (!sinkStmts.contains(methodSink2 +" - "+midStr1))
//	    						sinkStmts.add(methodSink2 +" - "+midStr1);
	    					break;
						}						
					}
					sinkItem= dtConfig.itemInList(midStr1,sinks);
					//System.out.println("sinkItem="+sinkItem+" methodSink1="+methodSink1+" methodSink2="+methodSink2);
					if (sinkItem.length()>1)  // && !methodSink1.equals(methodSink2))
					{
						methodItem=dtConfig.getMethodItem(sinkClassMethods, sinkItem,midStr1, " | ");
						//System.out.println(" getMethodItem2="+methodItem+" midStr1="+midStr1+ " indexOf="+midStr1.indexOf(methodItem));
						if (methodItem.length()>1 && midStr1.indexOf(methodItem)>=0)  
						{
							System.out.println(methodSink2 +" - "+midStr1+" :("+sinkItem+": "+methodItem+")");
							//sinkMsg+=methodSink2 +" - "+midStr1+" :("+sinkItem+": "+methodItem+") \n";
							//System.out.println("sinkMsg="+sinkMsg);
							//sinkMethods.add(methodSink2+" ("+sinkItem+") in "+midStr1);
							//methodSink1=methodSink2;
							//sinkMethods.add(methodSink2);				
							//sinkStmts.add(methodSink2 +" - "+midStr1);
	    					//if (!sinkMethods.contains(methodSink2))
	    						sinkMethods.add(methodSink2);
//	    					if (!sinkStmts.contains(methodSink2 +" - "+midStr1))
//	    						sinkStmts.add(methodSink2 +" - "+midStr1);

						}
					} 
					
					
				}

			}	
			
		}
		HashSet<String> sourceSinkMethods = new HashSet<>(); 
		sourceSinkMethods.addAll(sourceMethods);
		sourceSinkMethods.addAll(sinkMethods);
		dtUtil.writeSet(sourceSinkMethods, sourceSinkFile);
//		dtConfig.writeMessage(sourceSinkFile, sourceMsg);
//		dtConfig.writeMessage(sourceSinkFile, sinkMsg);
		
		
//		System.out.println("sourceFile="+sourceFile);
////		System.out.println("sourceMsg="+sourceMsg);
//		dtConfig.writeMessage(sourceFile, sourceMsg);
//		System.out.println("sinkFile="+sinkFile);
////		System.out.println("sinkMsg="+sinkMsg);
//		dtConfig.writeMessage(sinkFile, sinkMsg);

//    	String methodPairFile=System.getProperty("user.dir") + File.separator + "sourceSinkMethodPair.txt";
//    	//String stmtPairFile=System.getProperty("user.dir") + File.separator + "sourceSinkStmtPair.txt";
//    	String methodPairDiffFile=System.getProperty("user.dir") + File.separator + "sourceSinkMethodPairDiffClass.txt";
//    	//String stmtPairDiffFile=System.getProperty("user.dir") + File.separator + "sourceSinkStmtPairDiffClass.txt";
//    	System.out.println("Write pair file: "+methodPairFile);
//    	dtConfig.caetesianTwoArraysToFile(sourceMethods,sinkMethods,methodPairFile,"method");
//    	System.out.println("Write pair file: "+methodPairDiffFile);
//    	dtConfig.caetesianTwoArraysToFile(sourceMethods,sinkMethods,methodPairDiffFile,"methodDiff");
//    	System.out.println("Write pair file: "+stmtPairFile);
//    	dtConfig.caetesianTwoArraysToFile(sourceStmts,sinkStmts,stmtPairFile,"stmt");       
//    	System.out.println("Write pair file: "+stmtPairDiffFile);
//    	dtConfig.caetesianTwoArraysToFile(sourceStmts,sinkStmts,stmtPairDiffFile,"stmtDiff");     
	}
 
	// soot option 1
	private static void initial(String classPath) {
		soot.G.reset();
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_process_dir(Collections.singletonList(classPath));//
		Options.v().set_whole_program(true);
		Scene.v().loadNecessaryClasses();
		
	}
	
	// soot option 2
    private static void enableSpark(String path){
        HashMap opt = new HashMap();
        //opt.put("verbose","true");
        //opt.put("propagator","worklist");
        opt.put("simple-edges-bidirectional","false");
        //opt.put("on-fly-cg","true");
        opt.put("apponly", "true");
//        opt.put("set-impl","double");
//        opt.put("double-set-old","hybrid");
//        opt.put("double-set-new","hybrid");
//        opt.put("allow-phantom-refs", "true");
        opt.put("-process-dir",path);
        
        SparkTransformer.v().transform("",opt);
    }

	public static String getHierarchy(SootClass cls, Set<SootClass> sourceOrSinks) {
		Hierarchy har = Scene.v().getActiveHierarchy();	
		for (SootClass scls : sourceOrSinks) {
			//System.out.println("The sourceSink class is :" + scls.getName());
			if (!scls.isInterface())   { continue; }
			//List<SootClass> sclsSub=har.getSubinterfacesOf(scls);
			if (har.getSubinterfacesOf(scls).contains(cls)) {
				return scls.toString();
			}
			//List<SootClass> sclsImpl=har.getImplementersOf(scls);
			if (har.getImplementersOf(scls).contains(cls)) {
				return scls.toString();
			}
		}
		return "";
	}

}
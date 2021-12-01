package ODD;

public class ODDTEST {
	protected static ODDImpactAllInOne icAgent = null;
	public static void setICAgent(ODDImpactAllInOne _agent) {icAgent = _agent;}
	protected static StaticTransferGraph svtg = new StaticTransferGraph();
	/* the static VTG pruned from main class */
	protected static StaticTransferGraph svtgPruned = new StaticTransferGraph();
    public static void main(String[] args) {
    	
		ODDImpactAllInOne.setSVTG("C:/Research/nioecho/ODDInstrumented/staticVtg.dat");
		//ODDImpactAllInOne.initializeGraph(true);
		if (0 != ODDImpactAllInOne.initializeGraph(true)) {
			System.out.println("Unable to load the static value transfer graph, aborted now.");
			return;
		}
		//ODDImpactAllInOne.showNodesOfClass("NioServer");
		initializeClassGraph("NioServer");
		icAgent = new ODDImpactAllInOne();
		ODDBranchMonitor.setICAgent(icAgent);
		
    }
	public static int initializeClassGraph(String class1) {	
		System.out.println("initializeClassGraph 0");
		if ( null == svtg.DeserializeFromFile("C:/Research/nioecho/ODDInstrumented/staticVtg"+class1+".dat") ) {
			System.out.println("initializeClassGraph 1");
			ODDImpactAllInOne.setSVTG("C:/Research/nioecho/ODDInstrumented/staticVtg.dat");
			System.out.println("initializeClassGraph 2");
			ODDImpactAllInOne.initializeGraph(true);
			//System.out.println("svtg.nodeSet().size(): "+svtg.nodeSet().size());
			//System.out.println("svtg.edgeSet().size(): "+svtg.edgeSet().size());

			if (ODDImpactAllInOne.prunedByClass(svtgPruned, class1)>0)  {
				System.out.println("svtgPruned.nodeSet().size(): "+svtgPruned.nodeSet().size());
				System.out.println("svtgPruned.edgeSet().size(): "+svtgPruned.edgeSet().size());
				svtgPruned.SerializeToFile("C:/Research/nioecho/ODDInstrumented/staticVtg"+class1+".dat");
				svtg=svtgPruned;
				System.out.println("svtg.nodeSet().size(): "+svtg.nodeSet().size());
				System.out.println("svtg.edgeSet().size(): "+svtg.edgeSet().size());
			}
			else
			{
				//System.out.println("Unable to separate the static value transfer graph according to the entry class");
			}
		}
		else
		{
			ODDImpactAllInOne.setSVTG("C:/Research/nioecho/ODDInstrumented/staticVtg"+class1+".dat");
			System.out.println("initializeClassGraph 2");
			svtg.DeserializeFromFile("C:/Research/nioecho/ODDInstrumented/staticVtg"+class1+".dat");
			ODDImpactAllInOne.initializeGraphStep2("static_"+class1+".dat", true);
			// 3. classify internal graph structures
			ODDImpactAllInOne.classifyEdgeAndNodes();			
			// 4. create mapping from IPs to reachable OPs to save PDG reachability analysis afterwards
			ODDImpactAllInOne.summarize();
			
			System.out.println("svtgPruned.nodeSet().size() = "+svtgPruned.nodeSet().size());
			System.out.println("svtgPruned.edgeSet().size() = "+svtgPruned.edgeSet().size());
			System.out.println("svtg.nodeSet().size() = "+svtg.nodeSet().size());
			System.out.println("svtg.edgeSet().size() = "+svtg.edgeSet().size());
//			ODDImpactAllInOne.initializeGraph(true);
//			System.out.println("svtgPruned.nodeSet().size() = "+svtgPruned.nodeSet().size());
//			System.out.println("svtgPruned.edgeSet().size() = "+svtgPruned.edgeSet().size());
//			System.out.println("svtg.nodeSet().size() = "+svtg.nodeSet().size());
//			System.out.println("svtg.edgeSet().size() = "+svtg.edgeSet().size());
		}
		return 0;
	}
}

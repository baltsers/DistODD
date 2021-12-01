/**
 * File: src/ODD/ODDImpactAllInOne.java
 * -------------------------------------------------------------------------------------------
 * Date			Author      Changes
 * -------------------------------------------------------------------------------------------
 * 04/09/2019	Developer		created; for an online (and offline all-in-one) impact computation
*/
package ODD;

import java.io.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import MciaUtil.*;
import MciaUtil.VTEdge.*;

public class ODDImpactAllInOne {
	/* the static VTG as the source of the initial dynamic VTG */
	protected static StaticTransferGraph svtg = new StaticTransferGraph();
	/* the static VTG pruned from main class */
	protected static StaticTransferGraph svtgPruned = new StaticTransferGraph();
	
	/* the full sequence of EAS events */
	protected LinkedHashMap<Integer, Integer> EASeq = null;
	
	/* the map from index to method signature associated with the full sequence of method events */
	protected LinkedHashMap<Integer, String> EAMethodMap = null;
		
	/* the file holding the execution trace being used */
	protected String fnTrace = "";
	
	/* the file holding the static VTG binary */
	protected static String fnSVTG = "";
	
	// a map from a method to all transfer edges on the static graph that are associated with the method
	protected static Map<Integer, List<SVTNode>> method2nodes;
	// a map from an edge type to all transfer edges of the type of the static graph
	protected static Map<VTEdge.VTEType, List<SVTEdge>> type2edges;
	
	/** map from a node to all incoming interprocedural edges */
	protected static Map< SVTNode, Set<SVTEdge> > nodeToInEdges;
	/** map from a node to all outgoing interprocedural edges */
	protected static Map< SVTNode, Set<SVTEdge> > nodeToOutEdges;
	
	/** a map from method signature to index for the underlying static VTG */
	static Map< String, Integer > method2idx;
	/** a map from index to method signature for the underlying static VTG */
	static Map< Integer, String > idx2method;
	
	protected Map< Integer, BitSet > ImpactSets;
	public Map< Integer, BitSet > getAllImpactSets() { return ImpactSets; }
	protected Map< Integer, Map<VTEdge.VTEType, Set<SVTNode>> > ImpOPs;
	
	protected static Set<Integer> allQueries = null;
	public static Set<Integer> getAllQueries() { return allQueries; }
	protected static Set<String> inputQueries = null;
	
	//static List<Integer> coveredStmts = new ArrayList<Integer>();
	//static List<String> coveredStmtsStr = new ArrayList<String>();
	
	//static String operationFlag="";
	/** when specific queries are specified, only compute impact sets of these queries */
	public static void setQueries(Collection<String> queries) {
		inputQueries = new HashSet<String>();
		inputQueries.addAll(queries);
		allQueries = new HashSet<Integer>();
		for (String query : queries) {
			Integer midx = getMethodIdx(query);
			if (null == midx) continue;
			allQueries.add(midx);
		}
	}
	public static void setQuery(String query) {
		Set<String> queries = new HashSet<String>();
		queries.add(query);
		setQueries(queries);
	}
	
	public static boolean isMethodInSVTG(Integer midx) {
		return method2nodes.get(midx)!=null;
	}
	public static Integer getMethodIdx(String mename) {
		if (mename==null) return null;
		return method2idx.get(mename);
	}
	
	public Set<String> getImpactSet() {
		if (inputQueries==null) {
			return new HashSet<String>();
		}
		return getImpactSet(inputQueries);
	}
	
	public Set<String> getImpactSet(Set<String> queries) {
		Set<String> ret = new HashSet<String>();
		if (queries.isEmpty()) return ret;
		
		for (String q : queries) {
			// save computation by DistODD if the query is not even executed
			if (EAMethodMap!=null && !EAMethodMap.containsValue(q)) {
				continue;
			}
			Integer idx = method2idx.get(q);
			//System.out.println("idx = " + idx +" q = " + q + "EAMethodMap = " + EAMethodMap +" method2idx = " + method2idx);
			if (null == idx || !ImpactSets.containsKey(idx)) {
				continue;
			}
				
			for (int i = ImpactSets.get(idx).nextSetBit(0); i >= 0; i = ImpactSets.get(idx).nextSetBit(i+1)) {
			     // operate on index i here
			     if (i == Integer.MAX_VALUE) {
			         break; // or (i+1) would overflow
			     }
			     ret.add(idx2method.get(i));
			 }
		}
		
		return ret;
	}
	
	public Set<String> getImpactSet(String query) {
		Set<String> queries = new HashSet<String>();
		queries.add(query);
		return getImpactSet(queries);
	}
	public Set<Integer> getImpactSetNumber(Integer idx) {
		Set<Integer> ret = new HashSet<Integer>();

		if (null == idx || !ImpactSets.containsKey(idx)) {
			return ret;
		}
			
		for (int i = ImpactSets.get(idx).nextSetBit(0); i >= 0; i = ImpactSets.get(idx).nextSetBit(i+1)) {
		     // operate on index i here
		     if (i == Integer.MAX_VALUE) {
		         break; // or (i+1) would overflow
		     }
		     ret.add(i);
		 }		
		return ret;
	}	
	public static Set<Integer> getChangeSetNumber(int chg) {
		Set<Integer> chgSet = new LinkedHashSet<Integer>();
		for (Integer _m : method2nodes.keySet()) {
			String mn = idx2method.get(_m);
			if (mn.toLowerCase().contains(idx2method.get(chg).toLowerCase())) {
				chgSet.add(_m);
			}
		}
		return chgSet;
	}
	public static Set<String> getChangeSet(String chg) {
		Set<String> chgSet = new LinkedHashSet<String>();
		for (Integer _m : method2nodes.keySet()) {
			String mn = idx2method.get(_m);
			if (mn.toLowerCase().contains(chg.toLowerCase())) {
				chgSet.add(mn);
			}
		}
		return chgSet;
	}
	
	public ODDImpactAllInOne() {
		initInternals();
	}
	
	static {
		method2nodes = new LinkedHashMap<Integer, List<SVTNode>>();
		type2edges = new LinkedHashMap<VTEType, List<SVTEdge>>();
		nodeToInEdges = new LinkedHashMap< SVTNode, Set<SVTEdge> >();
		nodeToOutEdges = new LinkedHashMap< SVTNode, Set<SVTEdge> >();
		method2idx = new LinkedHashMap<String, Integer>();
		idx2method = new LinkedHashMap<Integer, String>();
	}
	
	protected void initInternals() {
		ImpactSets = new LinkedHashMap<Integer, BitSet>();
		ImpOPs = new LinkedHashMap<Integer, Map<VTEdge.VTEType, Set<SVTNode>>>();
	}
	
	public void setTrace(String _fnTrace) {
		if (_fnTrace.equalsIgnoreCase(fnTrace)) return;
		fnTrace = _fnTrace;
		ImpOPs.clear();
		ImpactSets.clear();
		computeAllIS(false);
	}
	public static void setSVTG(String _fnSVTG) {
		fnSVTG = _fnSVTG;
	}
	
	final static Set<SVTEdge> emptyEdgeSet = new HashSet<SVTEdge>(); 
	public static Set<SVTEdge> getInEdges(SVTNode _node) { 
		Set<SVTEdge> ret = nodeToInEdges.get(_node);
		if (ret == null) return emptyEdgeSet;
		return ret;
	}
	public static Set<SVTEdge> getOutEdges(SVTNode _node) { 
		Set<SVTEdge> ret = nodeToOutEdges.get(_node); //svtg.getOutEdges(_node);
		if (ret == null) return emptyEdgeSet;
		return ret;
	}
	public static Set<SVTEdge> getAnyOutEdges(SVTNode _node) { 
		Set<SVTEdge> ret = svtg.getOutEdges(_node);
		if (ret == null) return emptyEdgeSet;
		return ret;
	}
	
	public static boolean isEmpty() {
		return nodeToInEdges.isEmpty() || method2nodes.isEmpty() || type2edges.isEmpty();
	}
	public static void clear() {
		nodeToInEdges.clear();
		method2nodes.clear();
		type2edges.clear();
		
		method2idx.clear();
		idx2method.clear();
	}
	
	public void resetImpOPs() { ImpOPs.clear(); System.gc();}
	
	public static void classifyEdgeAndNodes() {
		// 1. build the method->VTG nodes map to facilitate edge activation and source-target matching later on
		/* list nodes by enclosing methods */
		//System.out.println("svtg.nodeSet().size()="+svtg.nodeSet().size());
		//System.out.println("svtg.edgeSet().size()="+svtg.edgeSet().size());
		//System.out.println("method2idx.size()="+method2idx.size());
		//System.out.println("method2idx="+method2idx);
		//System.out.println("method2nodes.size()="+method2nodes.size());
		for (SVTNode vn : svtg.nodeSet()) {
			//System.out.println("classifyEdgeAndNodes()  vn.getMethod().getName()="+vn.getMethod().getName());
			try
			{
				String name1=vn.getMethod().getName();
				String name2=ODDUtil.getInter(vn.getMethod().getName());
				int ime=-1;
				if (method2idx.containsKey(name1))  {
					ime=method2idx.get(name1);
				}
				else if (method2idx.containsKey(name2))  {
					ime=method2idx.get(name2);
				}
				else
					continue;			
				List<SVTNode> vns = method2nodes.get(ime);
				if (vns == null) {
					vns = new LinkedList<SVTNode>();
					method2nodes.put(ime, vns);
				}
				vns.add(vn);
			} 		 catch (Exception e) {
				//e.printStackTrace();
			}
			
		}
		// 2. build the EdgeType->VTG edges map 
		for (SVTEdge edge : svtg.edgeSet()) {
			List<SVTEdge> els = type2edges.get(edge.getEdgeType());
			if (els == null) {
				els = new LinkedList<SVTEdge>();
				type2edges.put(edge.getEdgeType(), els);
			}
			els.add(edge);
		}
	}
	
	/** map : {method idx -> map : {IPs -> reachable OPs}} */
	final static Map<Integer, Map<SVTNode, Set<SVTNode>>> ip2ops = new HashMap<Integer, Map<SVTNode, Set<SVTNode>>>();
	/** map : {method idx -> OPs} */
	final static Map<Integer, Set<SVTNode>> me2ops = new HashMap<Integer, Set<SVTNode>>();
	public static void summarize() {
		// 1. collect incoming and outgoing ports for each method
		Map<Integer, Set<SVTNode>> mToIPs = new HashMap<Integer, Set<SVTNode>>();
		Map<Integer, Set<SVTNode>> mToOPs = new HashMap<Integer, Set<SVTNode>>();
		for (SVTEdge e : svtg.edgeSet()) {
			//if (!e.isLocalEdge() && !e.isIntraControlEdge()) {
			if (e.isInterproceduralEdge()) {
				Integer mi = method2idx.get(e.getTarget().getMethod().getName());
				Integer mo = method2idx.get(e.getSource().getMethod().getName());
				
				//assert mi != mo;
				
				// gather IPs for the target method
				Set<SVTNode> ni = mToIPs.get(mi);
				if (ni == null) {
					ni = new HashSet<SVTNode>();
					mToIPs.put(mi, ni);
				}
				ni.add(e.getTarget());
				
				// gather OPs for the source method
				Set<SVTNode> no = mToOPs.get(mo);
				if (no == null) {
					no = new HashSet<SVTNode>();
					mToOPs.put(mo, no);
				}
				no.add(e.getSource());
			}
		}
		// 2. for each method, create mapping from IPs to OPs of the method reachable via intraprocedural edges
		for (Integer m : mToIPs.keySet()) {
			//if (!mToOPs.containsKey(m)) {continue;}
			
			Map<SVTNode, Set<SVTNode>> in2outs = ip2ops.get(m);
			if (in2outs==null) {
				in2outs = new HashMap<SVTNode, Set<SVTNode>>();
				ip2ops.put(m, in2outs);
			}
			
			
			// match each IP against each OP checking if there is a path between them via intraprocedural edges
			for (SVTNode in : mToIPs.get(m)) {
				Set<SVTNode> outs = in2outs.get(in);
				if (outs==null) {
					outs = new HashSet<SVTNode>();
					in2outs.put(in, outs);
				}
				
				if (!mToOPs.containsKey(m)) {continue;}
				for (SVTNode on : mToOPs.get(m)) {
					boolean isReachable = isReachable(in, on);
					if (isReachable) {
						outs.add(on);
					}
				}
			}
		}
		// 3. for each method, create mapping from its index to all its OPs, reachable from any IPs or not
		me2ops.putAll(mToOPs);
		
	}
	
	/**
	 * load the initializing static value transfer graph from a disk file previously dumped
	 * @param sfn the static graph file name
	 * @return 0 for success and others for failure
	 */
	public static int initializeGraph(String sfn, boolean debugOut) {
		// 1. deserialize the static transfer graph firstly
		if ( null == svtg.DeserializeFromFile(sfn) ) {
			return -1;
		}
		
		// 2. establish the bijectional indices and nodeToInEdges mapping
//		int index = 0;
//		for (SVTNode sn : svtg.nodeSet()) {
//			String mname = sn.getMethod().getName();
//			//System.out.println("mname = "+mname);
//			if (!method2idx.containsKey(mname)) {
//				method2idx.put(mname, index);
//				idx2method.put(index, mname);
//				index ++;
//			}
//		}
		
		for (SVTEdge se : svtg.edgeSet()) {
			if (!se.isInterproceduralEdge()) continue;
			Set<SVTEdge> inEdges = nodeToInEdges.get(se.getTarget());
			if (null == inEdges) {
				inEdges = new HashSet<SVTEdge>();
				nodeToInEdges.put(se.getTarget(), inEdges);
			}
			inEdges.add(se);
			
			Set<SVTEdge> outEdges = nodeToOutEdges.get(se.getSource());
			if (null == outEdges) {
				outEdges = new HashSet<SVTEdge>();
				nodeToOutEdges.put(se.getSource(), outEdges);
			}
			outEdges.add(se);
		}
		
		// 3. classify internal graph structures
		classifyEdgeAndNodes();
		
		// 4. create mapping from IPs to reachable OPs to save PDG reachability analysis afterwards
		summarize();
		
		return 0;
	}
	public static int initializeGraph(boolean debugOut) {
		if (null == fnSVTG || fnSVTG.length() < 1) {
			return -1;
		}
		return initializeGraph(fnSVTG, debugOut);
	}
		
	/**
	 * load execution trace to be used for exercising the static VTG into a dynamic one
	 * @param fnSource the source disk file holding an EA execution trace 
	 * @return 0 for success and others for failure, different values indicating different reasons
	 */
	protected int loadEASequence(String fnSource) {
		if (null == EASeq) {
			EASeq = new LinkedHashMap<Integer, Integer>();
			EAMethodMap = new LinkedHashMap<Integer, String>();
		}
		else {
			EASeq.clear();
			EAMethodMap.clear();
		}
		FileInputStream fis;
		try {
			fis = new FileInputStream(fnSource);
			
			GZIPInputStream gis = new GZIPInputStream(fis);
			int len = 1024;
			int ziplen = (int)new File(fnSource).length();
			byte[] bs = new byte[ziplen*20]; // Gzip won't have more than 20 compression ratio for the binary data
			int off = 0;
			while (gis.available()!=0) {
				off += gis.read(bs, off, len);
			}
			gis.close();
			fis.close();

			ByteArrayInputStream bis = new ByteArrayInputStream(bs);
			
			//ObjectInputStream ois = new ObjectInputStream(fis);
			ObjectInputStream ois = new ObjectInputStream(bis);
			
			Map<String,Integer> EAmethod2idx = new LinkedHashMap<String,Integer>();
			@SuppressWarnings("unchecked")
			LinkedHashMap<String,Integer> readObject1 = (LinkedHashMap<String,Integer>) ois.readObject();
			EAmethod2idx = readObject1;
			for (Map.Entry<String, Integer> en : EAmethod2idx.entrySet()) {
				// create an inverse map for facilitating quick retrieval later on
				EAMethodMap.put(en.getValue(), en.getKey());
			}
			
			@SuppressWarnings("unchecked")
			LinkedHashMap<Integer, Integer> readObject2 = (LinkedHashMap<Integer, Integer>) ois.readObject();
			EASeq = readObject2;
			
			ois.close();
			bis.close();
			// --
		}
		catch (FileNotFoundException e) { 
			System.err.println("Failed to locate the given input EAS trace file " + fnSource);
			return -1;
		}
		catch (ClassCastException e) {
			System.err.println("Failed to cast the object deserialized to LinkedHashMap<Integer, String>!");
			return -2;
		}
		catch (IOException e) {
			throw new RuntimeException(e); 
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
		
	// simply check the reachability in the SVTG from node a to node b : lazy (recursive) implementation
	public static boolean isReachable (SVTNode a, SVTNode b, Set<SVTNode> visited) {
		return isReachable(a, b, visited, true);
	}
	
	public static boolean isReachable (SVTNode a, SVTNode b, Set<SVTNode> visited, boolean viaLocalOnly) {
		if (!visited.add(a)) return false;
		
		if (a.equals(b)) return true;
		//if (getInEdges(b) == null || getInEdges(b).isEmpty()) return false;
		Set<SVTEdge> oes = getAnyOutEdges(a);
		if (oes.isEmpty()) return false;
		
		for (SVTEdge oe : oes) {
			// only look for reachability through edges in the PDG of the enclosing method of node a 
			if (viaLocalOnly) {
				//if (!(oe.isLocalEdge() || oe.isIntraControlEdge())) {
				if (oe.isInterproceduralEdge()) {	
					continue;
				}
			}
			if (isReachable (oe.getTarget(), b, visited, viaLocalOnly)) return true;
		}
		return false;
	}
	
	// non-recursive version
	public static boolean isReachable (SVTNode a, SVTNode b) {
		if (a.equals(b)) return true;
		//if (getInEdges(b) == null || getInEdges(b).isEmpty()) return false;
		Set<SVTEdge> oes = getAnyOutEdges(a);
		if (oes.isEmpty()) return false;

		Set<SVTNode> visited = new HashSet<SVTNode>();
		Stack<SVTNode> S = new Stack<SVTNode>();
		S.push(a);
		while (!S.isEmpty()) {
			SVTNode curn = S.pop();
			if (curn.equals(b)) {
				return true;
			}
			if (!visited.contains(curn)) {
				visited.add(curn);
				for (SVTEdge oe : getAnyOutEdges(curn)) {
					if (oe.isInterproceduralEdge()) {	
						continue;
					}
					S.push(oe.getTarget());
				}
			}
		}
		return false;
	}
	
	public void add2ImpOPs(int midx, VTEdge.VTEType et, SVTNode node) {
		if (allQueries!=null && !allQueries.contains(midx)) {
			return;
		}
		Map<VTEdge.VTEType, Set<SVTNode>> ty2imnodes = ImpOPs.get(midx);
		if (ty2imnodes == null) {
			ty2imnodes = new LinkedHashMap<VTEdge.VTEType, Set<SVTNode>>();
			ImpOPs.put(midx, ty2imnodes);
		}
		Set<SVTNode> imnodes = ty2imnodes.get(et);
		if (imnodes == null) {
			imnodes = new LinkedHashSet<SVTNode>();
			ty2imnodes.put(et, imnodes);
		}
		imnodes.add(node);
		//ty2imnodes.put(et, imnodes);
		//ImpOPs.put(midx, ty2imnodes);
	}
	
	public void add2ImpactSet(int idxquery, int idximpacted) {
		if (allQueries!=null && !allQueries.contains(idxquery)) {
			return;
		}
		BitSet allis = ImpactSets.get(idxquery);
		if (allis == null) {
			allis = new BitSet(method2idx.keySet().size());
			ImpactSets.put(idxquery, allis);
		}
		allis.set(idximpacted);
		//ImpactSets.put(idxquery, allis);
	}
	
	public void onMethodEntryEventWithoutSummary(int smidx) {
		// mark all nodes in method smidx as impacted
		if (allQueries == null || allQueries.contains(smidx)) {
			for (SVTNode _n : method2nodes.get(smidx)) {
				for (SVTEdge _e : getOutEdges(_n)) {
					add2ImpOPs(smidx, _e.getEdgeType(), _e.getSource());
				}
			}
		}
		
		// check all incoming edges, mark method smidx as impacted for the methods at the edge sources (as queries)
		for (SVTNode _n : method2nodes.get(smidx)) {
			for (SVTEdge _e : getInEdges(_n)) {
				//if (!_e.isInterproceduralEdge()) continue;
				for (Integer srcme : ImpOPs.keySet()) {
					if (allQueries!=null) {
						//assert allQueries.contains(srcme);
					}
					if (_e.isReturnEdge() || _e.isRefReturnParamEdge() || 
						!ImpOPs.get(srcme).containsKey(_e.getEdgeType()) || 
						!ImpOPs.get(srcme).get(_e.getEdgeType()).contains(_e.getSource())) {
						continue;
					}
					
					add2ImpactSet(srcme, smidx);
					
					// propagate through PDG of method smidx
					for (SVTNode _n2 : method2nodes.get(smidx)) {
						if (_n.equals(_n2)) continue;
						for (SVTEdge _oe : getOutEdges(_n2)) {
							//if (!_oe.isInterproceduralEdge()) continue;
							//if (isReachable(_e.getTarget(), _oe.getSource(), new LinkedHashSet<SVTNode>())) {
							if (isReachable(_e.getTarget(), _oe.getSource())) {	
								add2ImpOPs(srcme, _oe.getEdgeType(), _oe.getSource());
							}
						}
					}
				}
			}
		}
		
		// end
	}
	
	
	public void onMethodEntryEvent(int smidx) {
		// mark all OPs in method smidx as impacted when smidx is executed 
		//if (/*ImpOPs.get(smidx)==null &&*/ me2ops.containsKey(smidx)) {
		if ( (allQueries == null || allQueries.contains(smidx)) && me2ops.containsKey(smidx)) {
			for (SVTNode _n : me2ops.get(smidx)) {				
				for (SVTEdge _e : getOutEdges(_n)) {
					//if (!_e.isInterproceduralEdge()) continue;
					add2ImpOPs(smidx, _e.getEdgeType(), _e.getSource());
				}
			}
		}
		
		if (!ip2ops.containsKey(smidx)) return;
		Map<SVTNode, Set<SVTNode>> in2outs = ip2ops.get(smidx);
				
		// check all incoming edges, mark method smidx as impacted for the methods at the edge sources (as queries)
		for (SVTNode _n : in2outs.keySet()) {
			for (SVTEdge _e : getInEdges(_n)) {
				//if (!_e.isInterproceduralEdge()) continue;
				if (_e.isReturnEdge() || _e.isRefReturnParamEdge()) continue;
				for (Integer srcme : ImpOPs.keySet()) {
					if (allQueries != null) {
						//assert allQueries.contains(srcme);
					}
					if (!ImpOPs.get(srcme).containsKey(_e.getEdgeType()) || 
						!ImpOPs.get(srcme).get(_e.getEdgeType()).contains(_e.getSource())) {
						continue;
					}
					
					add2ImpactSet(srcme, smidx);
					
					//if (justdid) continue;
					// propagate through PDG of method smidx
					for (SVTNode _n2 : in2outs.get(_n)) {
						for (SVTEdge _oe : getOutEdges(_n2)) {
							//if (!_oe.isInterproceduralEdge()) continue;
							add2ImpOPs(srcme, _oe.getEdgeType(), _oe.getSource());
						}
					}
				}
			}
		}
		
		// end
	}
	
	public void onMethodReturnedIntoEventWithoutSummary(int smidx) {
		// check all incoming edges, mark method smidx as impacted for the methods at the edge sources (as queries)
		for (SVTNode _n : method2nodes.get(smidx)) {
			for (SVTEdge _e : getInEdges(_n)) {
				//if (!_e.isInterproceduralEdge()) continue;
				for (Integer srcme : ImpOPs.keySet()) {
					if (allQueries!=null) {
						//assert allQueries.contains(srcme);
					}
					if (_e.isParameterEdge() || 
						!ImpOPs.get(srcme).containsKey(_e.getEdgeType()) || 
						!ImpOPs.get(srcme).get(_e.getEdgeType()).contains(_e.getSource())) {
						continue;
					}
					
					add2ImpactSet(srcme, smidx);
					
					// propagate through PDG of method smidx
					for (SVTNode _n2 : method2nodes.get(smidx)) {
						if (_n.equals(_n2)) continue;
						for (SVTEdge _oe : getOutEdges(_n)) {
							//if (!_oe.isInterproceduralEdge()) continue;
							//if (isReachable(_e.getTarget(), _oe.getSource(), new LinkedHashSet<SVTNode>())) {
							if (isReachable(_e.getTarget(), _oe.getSource())) {	
								add2ImpOPs(srcme, _oe.getEdgeType(), _oe.getSource());
							}
						}
					}
				}
			}
		}
		// end
	}
	
	public void onMethodReturnedIntoEvent(int smidx) {
		if (!ip2ops.containsKey(smidx)) return;
		Map<SVTNode, Set<SVTNode>> in2outs = ip2ops.get(smidx);
		
		// check all incoming edges, mark method smidx as impacted for the methods at the edge sources (as queries)
		for (SVTNode _n : in2outs.keySet()) {
			for (SVTEdge _e : getInEdges(_n)) {
				//if (!_e.isInterproceduralEdge()) continue;
				if (_e.isParameterEdge()) continue;
				for (Integer srcme : ImpOPs.keySet()) {
					if (allQueries!=null) {
						//assert allQueries.contains(srcme);
					}
					if (!ImpOPs.get(srcme).containsKey(_e.getEdgeType()) || 
						!ImpOPs.get(srcme).get(_e.getEdgeType()).contains(_e.getSource())) {
						continue;
					}
					
					add2ImpactSet(srcme, smidx);
					
					// propagate through PDG of method smidx
					for (SVTNode _n2 : in2outs.get(_n)) {
						for (SVTEdge _oe : getOutEdges(_n2)) {
							//if (!_oe.isInterproceduralEdge()) continue;
							add2ImpOPs(srcme, _oe.getEdgeType(), _oe.getSource());
						}
					}
				}
			}
		}
		// end
	}
	
	public int computeAllIS(boolean debugOut) {
		// 1. make sure the dynamic graph has been initialized successfully
		if (svtg.isEmpty() || isEmpty()) {
			// initializeGraph must be invoked and return success in the first place
			return -1;
		}
		// 2. load the trace
		if (null == fnTrace || fnTrace.length() < 1) {
			// trace not associated yet
			return -2;
		}
		
		// to handle multiple-file trace for a single test case
		int g_traceCnt = 0;
		while (true) {
			String curfnTrace = fnTrace + (g_traceCnt>0?g_traceCnt:"");
			if (!new File(curfnTrace).exists()) {
				// all segments have been processed
				break;
			}
			g_traceCnt ++;
			
			if (0 != loadEASequence(curfnTrace)) {
				// trace not loaded successfully
				return -3;
			}
			
			// - For DEBUG only
			if (debugOut) {
				System.out.println("===== method indexing map =====");
				System.out.println(EAMethodMap);
				System.out.println("===== The current execution trace under use [loaded from the call sequence] =====");
				TreeMap<Integer, Integer> treeA = new TreeMap<Integer, Integer> ( EASeq );
				System.out.println(treeA);
			}
			
			// 3. scan the execution trace and activate transfer edges
			Integer preMethod = null;
			int k = 0;
			boolean start = false;
			for (Map.Entry<Integer, Integer> _event : EASeq.entrySet()) {
				System.out.println("at event " + (k++) + " of " + EASeq.size() + " events...");
				Integer va = _event.getValue();
				//if (va.equalsIgnoreCase("program start") || va.equalsIgnoreCase("program end")) {
				if (va == Integer.MIN_VALUE || va == Integer.MAX_VALUE) {	
					// these are just two special events marking start and termination of the run
					continue;
				}
				
				// method index with respect to the trace --- executed methods
				int meidx = Math.abs(va);
				
				// method index with respect to the SVTG --- all methods defined in the subject
				Integer smidx = method2idx.get(EAMethodMap.get(meidx));
				
				// check each of all nodes associated with the currently checked method
				if (smidx==null || method2nodes.get(smidx) == null) {
					//System.out.println("associated with no nodes: " + em);
					continue;
				}
				
				if (!start) {
					start = (allQueries==null || allQueries.contains(smidx));
					if (!start) continue;
				}
				
				// trivially each method, once executed, is treated as impacted by itself
				if ( (allQueries == null || allQueries.contains(smidx)) && !ImpactSets.containsKey(smidx)) {
					add2ImpactSet(smidx, smidx);
				}
				
				boolean isEnterEvent = va < 0;
				/*
				if (isEnterEvent) {
					onMethodEntryEventWithoutSummary(smidx);
				}
				else {
					onMethodReturnedIntoEventWithoutSummary(smidx);
				}
				*/
				if (isEnterEvent) {
					onMethodEntryEvent(smidx);
				}
				else {
					onMethodReturnedIntoEvent(smidx);
				}
				
				/** Return/RefParam/Param edges all follow the "matching by adjacency of the source and target method" rule; so we 
				 * 	 can close the "open" nodes for these types of edge that are associated with the predecessor event's method now
				 */
				if (null == preMethod || preMethod == smidx) {
					// nothing to do with the first event
					preMethod = smidx;
					continue;
				}
				
				// close some "open" source nodes
				closeNodes(preMethod, smidx);
				
				preMethod = smidx;
			} // for each method event in currently examined execution trace
		} // for each trace segment file
		
		return 0;
	}
	
	public void closeNodes(int preMethod, int smidx) {
		for (Integer midx : ImpOPs.keySet()) {
			if (smidx == midx) continue;
			if (allQueries!=null) {
				//assert allQueries.contains(midx);
			}
			for (Map.Entry< VTEType, Set<SVTNode> > _en : ImpOPs.get(midx).entrySet()) {
				/** Interprocedural CD edges can pass across any number of methods, like Heap object transfer edges */
				if ( !VTEdge.isAdjacentType(_en.getKey()) ) {
					// close nodes for "adjacent type" edges only
					continue;
				}
				Set<SVTNode> toRemove = new HashSet<SVTNode>();
				for (SVTNode _n : _en.getValue()) {
					// close nodes marked open by the previous event
					if (method2idx.get(_n.getMethod().getName()) == preMethod) {
						toRemove.add(_n);
					}
				}
				ImpOPs.get(midx).get(_en.getKey()).removeAll(toRemove);
			}
		}
	}
	
	public void dumpAllImpactSets() {
		// System.out.println("ImpactSets.size() = " + ImpactSets.size() +" ImpactSets = " + ImpactSets);
		for (Integer midx : ImpactSets.keySet()) {
			Set<String> changeSet = getChangeSet(idx2method.get(midx));
			Set<String> finalResult = this.getImpactSet(idx2method.get(midx));
			System.out.println("==== DistODD impact set of [" + changeSet +"]  size=" + finalResult.size() + " ====");
			for (String m : finalResult) {
				System.out.println(m);
			}
		}
	}	
	public void dumpAllImpactSetsWithoutStatic() {
		// System.out.println("ImpactSets.size() = " + ImpactSets.size() +" ImpactSets = " + ImpactSets);
		for (Integer midx : ImpactSets.keySet()) {
			String methodName = idx2method.get(midx);
			Set<String> finalResult = this.getImpactSet(idx2method.get(midx));
			System.out.println("==== DistODD impact set of [" + methodName +"]  size=" + finalResult.size() + " ====");
			for (String m : finalResult) {
				System.out.println(m);
			}
		}
	}	

	
	public void dumpAllImpactSetsSize() {
		System.out.println("ImpactSets.size() = " + ImpactSets.size()); // +" ImpactSets = " + ImpactSets);
		for (Integer midx : ImpactSets.keySet()) {
			Set<String> changeSet = getChangeSet(idx2method.get(midx));
			Set<String> finalResult = this.getImpactSet(idx2method.get(midx));
			System.out.print("==== DistODD impact set of [" + changeSet +"]  size=" + finalResult.size() + " ====");			
		}
	}	
	public void dumpAllImpactSetsSizeWithoutStatic() {
		System.out.println("ImpactSets.size() = " + ImpactSets.size()); // +" ImpactSets = " + ImpactSets);
		for (Integer midx : ImpactSets.keySet()) {
			String methodName = idx2method.get(midx);
			Set<String> finalResult = this.getImpactSet(idx2method.get(midx));
			System.out.print("==== DistODD impact set of [" + methodName +"]  size=" + finalResult.size() + " ====");			
		}
	}	
	public String getDumpAllImpactSets() {
		// System.out.println("ImpactSets.size() = " + ImpactSets.size() +" ImpactSets = " + ImpactSets);
		String resultS="";
		for (Integer midx : ImpactSets.keySet()) {
			Set<String> changeSet = getChangeSet(idx2method.get(midx));
			Set<String> finalResult = this.getImpactSet(idx2method.get(midx));
			resultS+="==== DistODD impact set of [" + changeSet +"]  size=" + finalResult.size() + " ====\n";
			for (String m : finalResult) {
				//System.out.println(m);
				resultS+=m+"\n";
			}
		}
		return resultS;
	}	

	public String getDumpAllImpactSetsNumber() {
		// System.out.println("ImpactSets.size() = " + ImpactSets.size() +" ImpactSets = " + ImpactSets);
		String resultS="";
		for (Integer midx : ImpactSets.keySet()) {
			Set<Integer> changeSet = getChangeSetNumber(midx);
			 //System.out.println("changeSet.size() = " + changeSet.size() +" changeSet = " + changeSet);
			if (changeSet.size()<1)
				continue;
			Set<Integer> finalResult = this.getImpactSetNumber(midx);
			// System.out.println("finalResult.size() = " + finalResult.size() +" finalResult = " + finalResult);
			resultS+="[" + changeSet +"]  size=" + finalResult.size() ;
			for (Integer m : finalResult) {
				//System.out.println(m);
				resultS+=" ; "+m;
			}
		}
		return resultS;
	}	
	public String getDumpAllImpactSetsWithoutStatic() {
		// System.out.println("ImpactSets.size() = " + ImpactSets.size() +" ImpactSets = " + ImpactSets);
		String resultS="";
		for (Integer midx : ImpactSets.keySet()) {
			String methodName = idx2method.get(midx);
			Set<String> finalResult = this.getImpactSet(idx2method.get(midx));
			resultS+="==== DistODD impact set of [" + methodName +"]  size=" + finalResult.size() + " ====\n";
			for (String m : finalResult) {
				resultS+=m+"\n";
			}
		}
		return resultS;
	}	

	public String getDumpAllImpactSetsNumberWithoutStatic() {
		// System.out.println("ImpactSets.size() = " + ImpactSets.size() +" ImpactSets = " + ImpactSets);
		String resultS="";
		for (Integer midx : ImpactSets.keySet()) {
			Set<Integer> finalResult = this.getImpactSetNumber(midx);
			resultS+="[" + midx +"]  size=" + finalResult.size() ;
			for (Integer m : finalResult) {
				//System.out.println(m);
				resultS+=" ; "+m;
			}
		}
		return resultS;
	}	
	public String getDumpAllImpactSetsSize() {
		// System.out.println("ImpactSets.size() = " + ImpactSets.size() +" ImpactSets = " + ImpactSets);
		String resultS="ImpactSets.size() = " + ImpactSets.size();
		for (Integer midx : ImpactSets.keySet()) {
			Set<String> changeSet = getChangeSet(idx2method.get(midx));
			Set<String> finalResult = this.getImpactSet(idx2method.get(midx));
			resultS+="==== DistODD impact set of [" + changeSet +"]  size=" + finalResult.size() + " ====\n";
		}
		return resultS;
	}	
	public String getDumpAllImpactSetsSizeWithoutStatic() {
		// System.out.println("ImpactSets.size() = " + ImpactSets.size() +" ImpactSets = " + ImpactSets);
		String resultS="ImpactSets.size() = " + ImpactSets.size();
		for (Integer midx : ImpactSets.keySet()) {
			String methodName = idx2method.get(midx);
			Set<String> finalResult = this.getImpactSet(idx2method.get(midx));
			resultS+="==== DistODD impact set of [" + methodName +"]  size=" + finalResult.size() + " ====";			
		}
		return resultS;
	}	
	public void dumpImpactSet(String query) {
		Set<String> finalResult = this.getImpactSet(query);
		System.out.println("==== DistODD impact set of [" + query +"]  size=" + finalResult.size() + " ====");
		for (String m : finalResult) {
			System.out.println(m);
		}
	}
	
	public String getDumpImpactSet(String query) {
		String resultS="";
		Set<String> finalResult = this.getImpactSet(query);
		resultS="==== DistODD impact set of [" + query +"]  size=" + finalResult.size() + " ==== ; ";
		for (String m : finalResult) {
			resultS+=m+" ; ";
		}
		return resultS;
	}


	public void dumpImpactSet(Set<String> queries) {
		Set<String> finalResult = this.getImpactSet(queries);
		System.out.println("==== DistODD impact set of [" + queries +"]  size=" + finalResult.size() + " ====");
		for (String m : finalResult) {
			System.out.println(m);
		}

	}
	
	public static HashSet<SVTNode> getNodesOfClass(String class1) {		
		HashSet<SVTNode> hs=new HashSet();
		try {  
			String midMethod="";
//			String str1="";
//			String str2="";
			//System.out.println("svtg.nodeSet().size(): "+svtg.nodeSet().size());
			for (SVTNode sn: svtg.nodeSet()) {
				midMethod=sn.getMethod().toString();
				if (midMethod.startsWith("<"+class1+": "))
					hs.add(sn);
			}
		}
		catch (Exception e) { 
			System.err.println("Exception e="+e);
		}
		return hs;
	}
	
	@SuppressWarnings("unchecked")
	public static HashSet getNextNodes(SVTNode sn) {	
		HashSet hs = new HashSet();
		try {
			for (SVTEdge se : svtg.edgeSet()) {
				if (ODDStmtUtil.sameNodes(sn,se.getSource()) && !ODDStmtUtil.sameNodes(sn,se.getTarget()))
					hs.add(se.getTarget());
			}
		}
		catch (Exception e) { 
			System.err.println("Exception e="+e);
		}
		return hs;
	}

	public static HashSet getSuccessorNodes(String class1) {
		int oldSize=0;
		//System.out.println("getSuccessorNodes(String class1)");
		HashSet<SVTNode> resultS=new HashSet();
		HashSet<SVTNode> workS=new HashSet();
		//HashSet<SVTNode> visitedNodes=new HashSet();
		HashSet<SVTNode> nodesInClass1=getNodesOfClass(class1);
		//System.out.println("nodesInClass1.size(): "+nodesInClass1.size());
		boolean sizeIncremented=true;		
		{
			resultS.addAll(nodesInClass1);
			workS.addAll(nodesInClass1);
			while (sizeIncremented || workS.size()<1 )
			{	
				for (SVTEdge se : svtg.edgeSet()) {	
					SVTNode seSource=se.getSource();
					SVTNode seTarget=se.getTarget();
					if (workS.contains(seSource))
					{	
						resultS.add(seTarget);
						workS.add(seTarget);
						workS.remove(seSource);
					}	
					
					if (workS.contains(seTarget))
					{	
						resultS.add(seSource);
						workS.add(seSource);
						workS.remove(seTarget);
					}	
				}
				if (resultS.size()==oldSize)
				{	
					sizeIncremented=false;
					break;
				}	
				oldSize=resultS.size();
				//System.out.println("oldSize: "+oldSize);
			}			
		}
		//System.out.println("getSuccessorNodes(String class1) resultS.size(): "+resultS.size());
		return resultS;
	}

	public static Set<SVTEdge> getEdgesFromNodes(Set<SVTNode> sns) { 
		HashSet<SVTEdge> hs=new HashSet();
		try {  
			for (SVTNode sn: sns) {
				try
				{
					hs.addAll(nodeToInEdges.get(sn));
					hs.addAll(nodeToOutEdges.get(sn));
					hs.addAll(svtg.getOutEdges(sn));		
				}
				catch (Exception e) { 
					//System.err.println("Exception e="+e);
				}
			}
		}
		catch (Exception e) { 
			//System.err.println("Exception e="+e);
		}
		return hs;
	}
	
	public static Set<SVTNode> getNodesFromEdges(Set<SVTEdge> ses) { 
		HashSet<SVTNode> hs=new HashSet();
		try {  
			for (SVTEdge se : ses) {	
				hs.add(se.getSource());
				hs.add(se.getTarget());
			}
		}
		catch (Exception e) { 
			//System.err.println("Exception e="+e);
		}
		return hs;
	}
	
	public static Set<SVTEdge> getEdgesFromClass(String class1) { 
		//System.out.println("getEdgesFromClass(String class1)");
		HashSet<SVTNode> nodesForClass1=getSuccessorNodes(class1);
		//System.out.println("nodesInClass1.size(): "+nodesForClass1.size());
		return getEdgesFromNodes(nodesForClass1);
	}
	
	public static int prunedByClass(StaticTransferGraph svtg2, String class1) {
		int naddedEdges = 0;
		//System.out.println("prunedByClass(StaticTransferGraph svtg2, String class1)");
		Set<SVTEdge> allEdgesInClass=getEdgesFromClass(class1);

		for (SVTEdge de : allEdgesInClass) {
			svtg2.addEdge(de);
			naddedEdges++;
		}
		classifyEdgeAndNodes();
		//System.out.println("prunedByClass was added Edges: "+naddedEdges);
		return naddedEdges;
	}
	
	public static int prunedByClass(StaticTransferGraph svtg2, String class1, boolean statementPrune) {
		int naddedEdges = 0;
		//System.out.println("prunedByClass(StaticTransferGraph svtg2, String class1)");
		Set<SVTEdge> allEdgesInClass=getEdgesFromClass(class1);

		List<Integer> coveredStmts = new ArrayList<Integer>();
		if (statementPrune)
			coveredStmts = ODDStmtUtil.readStmtCoverageInt("", 1);		
		for (SVTEdge se : allEdgesInClass) {
			if (statementPrune && coveredStmts.size()>1)  {				
				if (coveredStmts.contains(utils.getFlexibleStmtId(se.getSource().getStmt())) || 
						 coveredStmts.contains(utils.getFlexibleStmtId(se.getTarget().getStmt()))) {
					svtg2.addEdge(se);
					naddedEdges++;
				}	
			}
			else
			{	
				svtg2.addEdge(se);
				naddedEdges++;
			}	
			naddedEdges++;
		}
//		for (SVTNode sn : svtg.nodeSet()) {
//			System.out.println("prunedByClass sn.getMethod().getName()="+sn.getMethod().getName());
//		}	
		classifyEdgeAndNodes();
		//System.out.println("prunedByClass was added Edges: "+naddedEdges);
		return naddedEdges;
	}
	
	public static int prunedByStmt(StaticTransferGraph svtg3, String binDir) {
		int naddedEdges = 0;		
		List<Integer> coveredStmts = ODDStmtUtil.readStmtCoverageInt(binDir, 1);
		if (coveredStmts.size()<=1)
		{
			svtg3=svtg;
			return 0;
		}
		
		//System.out.println("allEdgesInClass.size(): "+allEdgesInClass.size());
		for (SVTEdge se : svtg.edgeSet())  {
			if (coveredStmts.size()>1)  {				
				if (coveredStmts.contains(utils.getFlexibleStmtId(se.getSource().getStmt())) || 
						 coveredStmts.contains(utils.getFlexibleStmtId(se.getTarget().getStmt()))) {
					svtg3.addEdge(se);
					naddedEdges++;
				}	
			}
			else
			{	
				svtg3.addEdge(se);
				naddedEdges++;
			}	
			
		}
//		for (SVTNode sn : svtg.nodeSet()) {
//			System.out.println("prunedByClass sn.getMethod().getName()="+sn.getMethod().getName());
//		}	
		classifyEdgeAndNodes();
		System.out.println("prunedByStmt was added Edges: "+naddedEdges);
		return naddedEdges;
	}
	
	public static int initializeClassGraph(String class1) {
		setSVTG("staticVtg.dat");
		initializeGraph(true);
		if  (class1.length()>1)
			if (prunedByClass(svtgPruned, class1)>0) {
				svtg=svtgPruned;
			}
		return 0;
	}

	public static int prunedByStmt(String binDir) {
			
			if (prunedByStmt(svtgPruned, binDir)>0) {
				svtg=svtgPruned;
			}
		return 0;
	}
	
//	public static int initializeGraphSteps12(String sfn, boolean debugOut) {
//		// 1. deserialize the static transfer graph firstly
//		if ( null == svtg.DeserializeFromFile(sfn) ) {
//			return -1;
//		}
//		System.out.println("svtg.nodeSet().size() = "+svtg.nodeSet().size());
//		// 2. establish the bijectional indices and nodeToInEdges mapping
//		int index = 0;
//		for (SVTNode sn : svtg.nodeSet()) {
//			String mname = sn.getMethod().getName();
//			if (!method2idx.containsKey(mname)) {
//				method2idx.put(mname, index);
//				idx2method.put(index, mname);
//				index ++;
//			}
//		}		
//		System.out.println("method2idx.size() = "+method2idx.size());
//		System.out.println("idx2method.size() = "+idx2method.size());
//		System.out.println("method2idx = "+method2idx);
//		System.out.println("idx2method = "+idx2method);
//		return 0;
//	}
	

	public static int initializeGraphStep2(String sfn, boolean debugOut) {		
		System.out.println("svtg.edgeSet().size() = "+svtg.edgeSet().size());
		for (SVTEdge se : svtg.edgeSet()) {
			//if (!se.isInterproceduralEdge()) continue;
			Set<SVTEdge> inEdges = nodeToInEdges.get(se.getTarget());
			if (null == inEdges) {
				inEdges = new HashSet<SVTEdge>();
				nodeToInEdges.put(se.getTarget(), inEdges);
			}
			inEdges.add(se);
			
			Set<SVTEdge> outEdges = nodeToOutEdges.get(se.getSource());
			if (null == outEdges) {
				outEdges = new HashSet<SVTEdge>();
				nodeToOutEdges.put(se.getSource(), outEdges);
			}
			outEdges.add(se);
		}
		
//		// 3. classify internal graph structures
//		classifyEdgeAndNodes();
//		
//		// 4. create mapping from IPs to reachable OPs to save PDG reachability analysis afterwards
//		summarize();
		
		return 0;
	}
	
	public static int initializeClassGraph(String class1, boolean statementPrune) {
		setSVTG("staticVtg.dat");
		initializeGraph(true);
		//System.out.println("initializeGraph(true)="+ODDImpactAllInOne.svtg.edgeSet().size()+" ODDImpactAllInOne.svtg.nodeSet().size()="+ODDImpactAllInOne.svtg.nodeSet());
		if  (class1.length()>1)
			if (prunedByClass(svtgPruned, class1, statementPrune)>0) {
				//System.out.println("prunedByClass svtgPruned ="+svtgPruned.edgeSet().size()+" svtgPruned.nodeSet().size()="+svtgPruned.nodeSet());
				svtg=svtgPruned;
			}
		//System.out.println("prunedByClass svtg="+ODDImpactAllInOne.svtg.edgeSet().size()+" ODDImpactAllInOne.svtg.nodeSet().size()="+ODDImpactAllInOne.svtg.nodeSet());
		return 0;
	}

	public static int initializeClassGraph(String class1, boolean statementPrune, String staticVtgFile) {
		if (staticVtgFile.length()>0)  {
			setSVTG(staticVtgFile);
		}	
		else
			setSVTG("staticVtg.dat");
		initializeGraph(true);
		//System.out.println("initializeGraph(true)="+ODDImpactAllInOne.svtg.edgeSet().size()+" ODDImpactAllInOne.svtg.nodeSet().size()="+ODDImpactAllInOne.svtg.nodeSet());
		if  (class1.length()>1)
			if (prunedByClass(svtgPruned, class1, statementPrune)>0) {
				//System.out.println("prunedByClass svtgPruned ="+svtgPruned.edgeSet().size()+" svtgPruned.nodeSet().size()="+svtgPruned.nodeSet());
				svtg=svtgPruned;
			}
		//System.out.println("prunedByClass svtg="+ODDImpactAllInOne.svtg.edgeSet().size()+" ODDImpactAllInOne.svtg.nodeSet().size()="+ODDImpactAllInOne.svtg.nodeSet());
		return 0;
	}
	
	public static int initializeFunctionList(boolean statementPrune) {
	       try {
	   		FileReader reader = null;      
	        BufferedReader br = null;    
	        reader = new FileReader("functionList.out");   
	        br = new BufferedReader(reader);
	        String str = "";  
	        String mname="";
	        int index = 0;
	        method2idx=new HashMap< String, Integer >();
	        idx2method=new HashMap< Integer, String >();
	        while((str = br.readLine()) != null)
	        {  
	        	mname=str.trim();
				if (!method2idx.containsKey(mname)) {
					method2idx.put(mname, index);
					idx2method.put(index, mname);
					index ++;
				}
	        }        

	       } catch (Exception e) {  
	           e.printStackTrace(); 
	           return -1;
	       } 
		return 0;
	}
	public static int initializeFunctionList() {
	       try {
	   		FileReader reader = null;      
	        BufferedReader br = null;    
	        reader = new FileReader("functionList.out");   
	        br = new BufferedReader(reader);
	        String str = "";  
	        String mname="";
	        int index = 0;
	        method2idx=new HashMap< String, Integer >();
	        idx2method=new HashMap< Integer, String >();
	        while((str = br.readLine()) != null)
	        {  
	        	mname=str.trim();
				if (!method2idx.containsKey(mname)) {
					method2idx.put(mname, index);
					idx2method.put(index, mname);
					index ++;
				}
	        }        

	       } catch (Exception e) {  
	           e.printStackTrace(); 
	           return -1;
	       } 
		return 0;
	}
} // definition of ODDImpactAllInOne

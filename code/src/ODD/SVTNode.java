package ODD;


import java.util.Comparator;

import dua.method.CFGDefUses.Variable;
import fault.StmtMapper;

import soot.*;
import soot.jimple.*;

import MciaUtil.*;
import MciaUtil.CompleteUnitGraphEx.AugmentedUnit;

/** A static VTG node describes basic info about a variable w.r.t its service for value flow 
 * tracing of the variable 
 */
public class SVTNode implements IVTNode<Variable, SootMethod, Stmt>, Comparable<SVTNode> {
	/** variable underneath */
	protected Variable v;
	/** enclosing/hosting method of the variable */
	protected SootMethod m;
	/** statement location of the node */
	protected Stmt s;
	
	/** we may ignore stmt. loc. for some variables temporarily */
	public SVTNode(Variable _v, SootMethod _m) {
		v = _v;
		m = _m;
		s = null;
	}
	public SVTNode(Variable _v, SootMethod _m, Stmt _s) {
		v = _v;
		m = _m;
		s = _s;
	}
	
	/** accessors */
	void setStmt(Stmt _s) { this.s = _s; }
	public Variable getVar() { return v; }
	public SootMethod getMethod() { return m; }
	void setMethod(SootMethod _m) { this.m = _m; }
	public Stmt getStmt() { return s; }

	@Override public int hashCode() {
		return m.hashCode() + s.hashCode() + utils.getCanonicalFieldName(v).hashCode();
	}

	/** we do not distinguish two VTG nodes by statement location only */
	@Override public boolean equals(Object o) {
		//return v.mayEqualAndAlias(((SVTNode)o).v) && m == ((SVTNode)o).m;
		//return v == ((SVTNode)o).v && m == ((SVTNode)o).m;
		//return dua.util.Util.valuesEqual(v.getValue(), ((SVTNode)o).v.getValue(), true) && m == ((SVTNode)o).m;
		
		//return v.equals( ((SVTNode)o).v ) && m.equals( ((SVTNode)o).m );
		try {

			return m.equals( ((SVTNode)o).m ) && s.equals( ((SVTNode)o).s ) && 
						( utils.getCanonicalFieldName(v).equalsIgnoreCase(utils.getCanonicalFieldName (((SVTNode)o).v)) ||
								v.mayEqualAndAlias(((SVTNode)o).v) );
			
		}
		catch (Exception e) {
			/** this is for the makeshift during Serialization of the "SootMethod" field of SVTNode ONLY */

			return m.getSignature().equalsIgnoreCase( ((SVTNode)o).m.getSignature()) &&
						s.toString().equalsIgnoreCase( ((SVTNode)o).s.toString()) && 
						utils.getCanonicalFieldName(v).equalsIgnoreCase(utils.getCanonicalFieldName (((SVTNode)o).v));
					//utils.getFlexibleStmtId(s).equals(utils.getFlexibleStmtId(((SVTNode)o).s));
		}
	}
	/* exactly equal comparator */
	public boolean strictEquals(Object o) {
		//return this == o && s == ((SVTNode)o).s;
		return this.equals(o) && s.equals( ((SVTNode)o).s );
	}
	public String toStringNoStmt() {
		//return "("+utils.getCanonicalFieldName(v)+","+m.getName()+")";
		return "("+utils.getCanonicalFieldName(v)+","+m.getSignature()+")";
	}
	@Override public String toString() {
		if (null != s) {
			String sid = "";
			try {
				sid += StmtMapper.getGlobalStmtId(s);
			}
			catch(Exception e) {
				if (s instanceof ReturnStmt && ((ReturnStmt)s).getOp() instanceof IntConstant) {
					/** this is for the makeshift during Serialization of the "Stmt" field of SVTNode ONLY */
					sid += ( (IntConstant) ((ReturnStmt)s).getOp() ).toString();
				}
				else {
					sid = "unknown";
				}
			}
			//return "("+utils.getCanonicalFieldName(v)+","+m.getDeclaringClass().getName()+"::"+m.getName()+","+sid+")";
			return "("+utils.getCanonicalFieldName(v)+","+m.getSignature()+","+sid+")";
		}
		//return "("+utils.getCanonicalFieldName(v)+","+m.getDeclaringClass().getName()+"::"+m.getName()+")";
		return "("+utils.getCanonicalFieldName(v)+","+m.getSignature()+")";
	}
	
	public int compareTo(SVTNode other) {
		return SVTNodeComparator.inst.compare(this, other);
	}

	public static class SVTNodeComparator implements Comparator<SVTNode> {
		private SVTNodeComparator() {}
		public static final SVTNodeComparator inst = new SVTNodeComparator();

		public int compare(SVTNode n1, SVTNode n2) {
			final String mname1 = n1.m.getSignature();
			final String mname2 = n2.m.getSignature();

			final String vname1 = n1.v.isConstant()? ((Constant)n1.v.getValue()).toString() : n1.v.toString();
			final String vname2 = n2.v.isConstant()? ((Constant)n2.v.getValue()).toString() : n2.v.toString();

			int cmpmName = mname1.compareToIgnoreCase(mname2);
			int cmpvName = vname1.compareToIgnoreCase(vname2);
			if (null == n1.s || null == n2.s || 
					n1.s instanceof AugmentedUnit ||
					n2.s instanceof AugmentedUnit) {
				return (cmpmName != 0)?cmpmName : cmpvName; 
			}

			final int sid1 = StmtMapper.getGlobalStmtId(n1.s);
			final int sid2 = StmtMapper.getGlobalStmtId(n2.s);
			return (cmpmName != 0)?cmpmName : (cmpvName != 0)?cmpvName:
				(sid1 > sid2)?1:(sid1 < sid2)?-1:0;
		}
	}
}
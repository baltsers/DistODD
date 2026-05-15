/*******************************************************************************
 * Indus, a program analysis and transformation toolkit for Java.
 * Copyright (c) 2001, 2007 Venkatesh Prasad Ranganath
 * 
 * All rights reserved.  This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 which accompanies 
 * the distribution containing this program, and is available at 
 * http://www.opensource.org/licenses/eclipse-1.0.php.
 * 
 * For questions about the license, copyright, and software, contact 
 * 	Venkatesh Prasad Ranganath at venkateshprasad.ranganath@gmail.com
 *                                 
 * This software was developed by Venkatesh Prasad Ranganath in SAnToS Laboratory 
 * at Kansas State University.
 *******************************************************************************/

package edu.ksu.cis.indus.staticanalyses.dependency;

import edu.ksu.cis.indus.common.collections.IPredicate;
import edu.ksu.cis.indus.common.collections.InstanceOfPredicate;
import edu.ksu.cis.indus.common.collections.MapUtils;
import edu.ksu.cis.indus.common.datastructures.Pair;
import edu.ksu.cis.indus.common.datastructures.Triple;

import edu.ksu.cis.indus.interfaces.IMonitorInfo;

import edu.ksu.cis.indus.staticanalyses.InitializationException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ODD.StaticTransferGraph;
import MciaUtil.VTEdge.VTEType;
import dua.method.CFGDefUses.Variable;
import soot.FastHierarchy;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;
//import soot.jimple.InvokeStmt;
import soot.jimple.MonitorStmt;
import soot.jimple.Stmt;

/**
 * This class provides synchronization dependency information. This implementation refers to the technical report <a
 * href="http://www.cis.ksu.edu/santos/papers/technicalReports.html">A Formal Study of Slicing for Multi-threaded Program with
 * JVM Concurrency Primitives"</a>.
 * <p>
 * <i>Synchronization dependence</i>: All non-monitor statement in a method are synchronization dependent on the immediately
 * enclosing monitor statements in the same method.
 * </p>
 * <p>
 * In case of synchronized methods, the statements in the method not enclosed by monitor statements are dependent on the entry
 * and exit into the method which is tied to the call-sites. Hence, <code>getDependents()</code> and
 * <code>getDependees()</code> do not include this dependence as it is application specific and can be derived from the
 * control-flow. If the return points and entry point are assumed to comprise the monitor then there may be more than one
 * monitor pair as there are many return points, hence, not all statements in the method may be dependent on the same monitor
 * pair.
 * </p>
 * 
 * @author <a href="http://www.cis.ksu.edu/~rvprasad">Venkatesh Prasad Ranganath</a>
 * @author $Author: rvprasad $
 * @version $Revision: 1.67 $
 */
public final class SynchronizationDA
		extends
		AbstractDependencyAnalysis<Stmt, SootMethod, MonitorStmt, SootMethod, Map<Stmt, Collection<MonitorStmt>>, MonitorStmt, SootMethod, Stmt, SootMethod, Map<MonitorStmt, Collection<Stmt>>> {
	StaticTransferGraph vtg;
	/**
	 * This predicate can be used to check if an object of this class type.
	 */
	public static final IPredicate<IDependencyAnalysis<?, ?, ?, ?, ?, ?>> INSTANCEOF_PREDICATE = new InstanceOfPredicate<SynchronizationDA, IDependencyAnalysis<?, ?, ?, ?, ?, ?>>(
			SynchronizationDA.class);

	/**
	 * The logger used by instances of this class to log messages.
	 */
	static final Logger LOGGER = LoggerFactory.getLogger(SynchronizationDA.class);

	/**
	 * This provides monitor information.
	 */
	private IMonitorInfo<?> monitorInfo;

	/**
	 * Creates an instance of this class.
	 */
	public SynchronizationDA() {
		super(Direction.BI_DIRECTIONAL);
	}
	
	public SynchronizationDA(StaticTransferGraph vtg) {
		super(Direction.BI_DIRECTIONAL);
		this.vtg=vtg;
	}

//	protected void finalize(){ 
//		System.out.println("in Finalizing");  //
//		System.out.println("SynchronizationDA: "+toString());
//	}
	/**
	 * Calculates the synchronization dependency information for the methods provided during initialization.
	 * 
	 * @see edu.ksu.cis.indus.staticanalyses.dependency.AbstractDependencyAnalysis#analyze()
	 */
	@Override public void analyze() {
		unstable();
		stable();

		Map<SootMethod, Collection<Stmt>> myMethod2EEStmt = new HashMap<SootMethod, Collection<Stmt>>();
		Collection<Triple<EnterMonitorStmt, ExitMonitorStmt, SootMethod>> myEE2Methods = new HashSet<Triple<EnterMonitorStmt, ExitMonitorStmt, SootMethod>>();
		SootClass THREAD_CLASS = Scene.v().getSootClass("java.lang.Thread"); 
		FastHierarchy har = new FastHierarchy();
		for (SootClass sClass:Scene.v().getApplicationClasses()) 
		{
			if (!har.isSubclass(sClass, THREAD_CLASS) && !sClass.implementsInterface("java.lang.Runnable"))
				continue;
			if ( sClass.isPhantom() ) {
				// skip phantom classes
				continue;
			}
			for(SootMethod m:sClass.getMethods())
			{
//				Collection<Stmt> stmtInMethod= new HashSet<Stmt>();
//				Collection<InvokeStmt> waitStmtInMethod= new HashSet<InvokeStmt>();
//				Collection<InvokeStmt> notifyStmtInMethod= new HashSet<InvokeStmt>();
				if ( !m.isConcrete() ) {
					// skip abstract methods and phantom methods, and native methods as well
					continue; 
				}
				if ( m.toString().indexOf(": java.lang.Class class$") != -1 ) {
					// don't handle reflections now either
					continue;
				}
				if ( !m.hasActiveBody() ) {
					continue;
				}
				boolean hasEnter=false;
				//boolean hasEnterAfter=false;
//				boolean hasExit=false;
//				EnterMonitorStmt myEnter=null;
//				ExitMonitorStmt myExit=null;
//				Collection<Stmt> myStmtInEnterExit= new HashSet<Stmt>();
				try
				{
					Iterator<Unit> units=m.retrieveActiveBody().getUnits().iterator();		
					Stmt lastSt=null;
					while(units.hasNext())
					{
						try
						{					
							Stmt st = (Stmt) units.next();
							if (st instanceof EnterMonitorStmt)
							{
								hasEnter=true;
								//myEnter= (EnterMonitorStmt) st;						
							}
							
							if (hasEnter && !(st instanceof EnterMonitorStmt))
							{
				            	System.out.println("Synchronization() edge: "+m+" - "+lastSt+" --> "+m+" - "+st);			            	
		
								Variable _deV=vtg.makeVariable(soot.jimple.StringConstant.v("SYNC-EnterExitMonitor"),lastSt);
								Variable _dtV=vtg.makeVariable(soot.jimple.StringConstant.v("SYNC-EnterExitMonitor"),st);
								vtg.createTransferEdge(_deV,m, lastSt, _dtV, m, st, VTEType.VTE_CONTROL_INTRA, false);
							}
							if (st instanceof ExitMonitorStmt)
							{
								hasEnter=false;
								break;
							}
							lastSt=st;	
						 }
			            catch (Exception _e)
			            {
			            	//System.out.println("Exception: "+_e);
			            }
					}	
	            }
	            catch (Exception _e)
	            {
	            	//System.out.println("Exception: "+_e);
	            }
			}
		}	
	}

	/**
	 * Returns the enter and exit monitor statements on which the given statement is dependent on in the given method.
	 * 
	 * @param dependentStmt is a statement in the method.
	 * @param method in which <code>dependentStmt</code> occurs.
	 * @return a collection of enter and exit monitor statements.
	 * @see edu.ksu.cis.indus.staticanalyses.dependency.AbstractDependencyAnalysis#getDependees(java.lang.Object,
	 *      java.lang.Object)
	 */
	//create monitorInfo
	public Collection<MonitorStmt> getDependees(final Stmt dependentStmt, final SootMethod method) {
		final Map<Stmt, Collection<MonitorStmt>> _temp = MapUtils.getMapFromMap(dependent2dependee, method);
		Collection<MonitorStmt> _result = _temp.get(dependentStmt);

		if (_result == null) {
			//_result = new HashSet<MonitorStmt>(monitorInfo.getEnclosingMonitorStmts(dependentStmt, method, false));
			_result = new HashSet<MonitorStmt>();
			if (dependentStmt!=null && method!=null)      // whether in syn block
			if (monitorInfo!=null && monitorInfo.getEnclosingMonitorStmts(dependentStmt, method, false)!=null)  
			{	
				Collection<MonitorStmt> _ms1 = monitorInfo.getEnclosingMonitorStmts(dependentStmt, method, false);
				if (_ms1!=null)
				{
					Iterator<MonitorStmt> it =_ms1.iterator();
					while(it.hasNext()){
						_result.add(it.next());
					}		
				}
			}			
			//_result.add((MonitorStmt) monitorInfo.getEnclosingMonitorStmts(dependentStmt, method, false));
			if (dependentStmt instanceof MonitorStmt) {

				if (dependentStmt!=null && method!=null)  
				if (monitorInfo!=null &&  monitorInfo.getMonitorTriplesFor((MonitorStmt) dependentStmt, method)!=null)  
				{	
					final Collection<Triple<EnterMonitorStmt, ExitMonitorStmt, SootMethod>> _monitorTriples = monitorInfo
							.getMonitorTriplesFor((MonitorStmt) dependentStmt, method);
	
					for (final Iterator<Triple<EnterMonitorStmt, ExitMonitorStmt, SootMethod>> _i = _monitorTriples.iterator(); _i
							.hasNext();) {
						final Triple<EnterMonitorStmt, ExitMonitorStmt, SootMethod> _triple = _i.next();
						final EnterMonitorStmt _enter = _triple.getFirst();
						final ExitMonitorStmt _exit = _triple.getSecond();
	
						if (_enter.equals(dependentStmt) || _exit.equals(dependentStmt)) {
							_result.add(_enter);
							_result.add(_exit);
						}
					}
				}
			}
			_temp.put(dependentStmt, _result);
		}
		//one Edge
		return _result;
	}

	/**
	 * Returns the statements which depend on the given enter or exit monitor statement in the given method.
	 * 
	 * @param dependeeStmt is the enter or exit monitor statement.
	 * @param method in which<code>dependeeStmt</code> occurs.
	 * @return a collection of statements.
	 * @see edu.ksu.cis.indus.staticanalyses.dependency.AbstractDependencyAnalysis#getDependees(java.lang.Object,
	 *      java.lang.Object)
	 */
	public Collection<Stmt> getDependents(final MonitorStmt dependeeStmt, final SootMethod method) {
		final Map<MonitorStmt, Collection<Stmt>> _temp = MapUtils.getMapFromMap(dependee2dependent, method);  //?????????
		Collection<Stmt> _result = _temp.get(dependeeStmt);

		if (_result == null) {
			if (dependeeStmt!=null && method!=null)  
			if (monitorInfo!=null &&  monitorInfo.getMonitorTriplesFor((MonitorStmt) dependeeStmt, method)!=null)  
			{	
				final Collection<Triple<EnterMonitorStmt, ExitMonitorStmt, SootMethod>> _monitors = monitorInfo
						.getMonitorTriplesFor(dependeeStmt, method);
				_result = new HashSet<Stmt>();
	
				final Iterator<Triple<EnterMonitorStmt, ExitMonitorStmt, SootMethod>> _i = _monitors.iterator();
				final int _iEnd = _monitors.size();
	
				for (int _iIndex = 0; _iIndex < _iEnd; _iIndex++) {
					final Triple<EnterMonitorStmt, ExitMonitorStmt, SootMethod> _monitor = _i.next();
					final EnterMonitorStmt _enter = _monitor.getFirst();
					final ExitMonitorStmt _exit = _monitor.getSecond();
	
					if (dependeeStmt.equals(_enter) || dependeeStmt.equals(_exit)) {
						final Collection<Stmt> _enclosedStmts = monitorInfo.getEnclosedStmts(_monitor, false); //???????????????
						_result.addAll(_enclosedStmts);
						_result.add(_enter);
						_result.add(_exit);
					}
				}
				_temp.put(dependeeStmt, _result);
			}
		}
		return _result;
	}

	/**
	 * @see edu.ksu.cis.indus.staticanalyses.dependency.AbstractDependencyAnalysis#getIds()
	 */
	public Collection<IDependencyAnalysis.DependenceSort> getIds() {
		return Collections.singleton(IDependencyAnalysis.DependenceSort.SYNCHRONIZATION_DA);
	}

	/**
	 * @see edu.ksu.cis.indus.staticanalyses.interfaces.AbstractAnalysis#reset()
	 */
	@Override public void reset() {
		super.reset();
	}

	// /CLOVER:OFF

	/**
	 * Returns a stringized representation of this analysis. The representation includes the results of the analysis.
	 * 
	 * @return a stringized representation of this object.
	 */
	@Override public String toString() {
		return "The Statistics for Synchronization dependence is given by the monitor analysis used.\n"
				+ monitorInfo.toString();
	}

	// /CLOVER:ON

	/**
	 * @see edu.ksu.cis.indus.staticanalyses.dependency.AbstractDependencyAnalysis#getDependenceRetriever()
	 */
	@Override protected IDependenceRetriever<Stmt, SootMethod, MonitorStmt, MonitorStmt, SootMethod, Stmt> getDependenceRetriever() {
		return new AbstractDependenceRetriever<Stmt, SootMethod, MonitorStmt, MonitorStmt, SootMethod, Stmt>() {

			public Collection<Pair<MonitorStmt, SootMethod>> convertToConformantDependees(final Collection<Stmt> dependents,
					@SuppressWarnings("unused") final MonitorStmt base, final SootMethod context) {
				final Collection<Pair<MonitorStmt, SootMethod>> _result = new HashSet<Pair<MonitorStmt, SootMethod>>();
				for (final Stmt _t2 : dependents) {
					if (_t2 instanceof MonitorStmt) {
						_result.add(new Pair<MonitorStmt, SootMethod>((MonitorStmt) _t2, context));
					}
				}
				return _result;
			}

			public Collection<Pair<Stmt, SootMethod>> convertToConformantDependents(final Collection<MonitorStmt> dependees,
					@SuppressWarnings("unused") final Stmt base, final SootMethod context) {
				final Collection<Pair<Stmt, SootMethod>> _result = new HashSet<Pair<Stmt, SootMethod>>();
				for (final Stmt _e1 : dependees) {
					_result.add(new Pair<Stmt, SootMethod>(_e1, context));
				}
				return _result;
			}

		};
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws InitializationException when monitor analysis is not provided.
	 * @pre info.get(IMonitorInfo.ID) != null and info.get(IMonitorInfo.ID).oclIsTypeOf(IMonitorInfo)
	 * @see edu.ksu.cis.indus.staticanalyses.interfaces.AbstractAnalysis#setup()
	 */
	@Override protected void setup() throws InitializationException {
		super.setup();

		monitorInfo = (IMonitorInfo) info.get(IMonitorInfo.ID);
		
		if (monitorInfo == null) {
			throw new InitializationException(IMonitorInfo.ID + " was not provided in the info.");
		}
	}
}

// End of File

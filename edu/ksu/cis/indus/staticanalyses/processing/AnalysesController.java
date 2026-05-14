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

package edu.ksu.cis.indus.staticanalyses.processing;

import edu.ksu.cis.indus.annotations.Immutable;
import edu.ksu.cis.indus.annotations.NonNull;
import edu.ksu.cis.indus.common.collections.IFactory;
import edu.ksu.cis.indus.common.collections.MapUtils;
import edu.ksu.cis.indus.common.collections.SetUtils;
import edu.ksu.cis.indus.common.soot.BasicBlockGraphMgr;

import edu.ksu.cis.indus.interfaces.IActivePart;

import edu.ksu.cis.indus.processing.IProcessor;
import edu.ksu.cis.indus.processing.ProcessingController;

import edu.ksu.cis.indus.staticanalyses.InitializationException;
import edu.ksu.cis.indus.staticanalyses.interfaces.IAnalysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is provides the control class for the analyses suite. The analyses progress in phases. It may be so that some
 * application require a particular sequence in which each analysis should progress. Hence, the applications provide an
 * implementation of controller interface to drive the analyses in a particular sequence of phases.
 * <p>
 * This implementation will drive the given analyses such that each analysei is executed only when the anlaysis indicates that
 * all it's prerequesites have been fulfilled.
 * </p>
 *
 * @author <a href="http://www.cis.ksu.edu/~rvprasad">Venkatesh Prasad Ranganath</a>
 * @author $Author: rvprasad $
 * @version $Revision: 1.9 $
 */
public class AnalysesController {

	/**
	 * The logger used by instances of this class to log messages.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AnalysesController.class);

	/**
	 * The map of analysis being controlled by this object. It maps names of analysis to the analysis object.
	 *
	 * @invariant participatingAnalyses != null
	 */
	protected final Map<Comparable, Collection<? extends IAnalysis>> participatingAnalyses;

	/**
	 * This is the preprocessing controlling agent.
	 *
	 * @invariant preprocessController != null;
	 */
	protected final ProcessingController preprocessController;

	/**
	 * The object used to realize the "active" part of this object.
	 */
	private final IActivePart.ActivePart activePart = new IActivePart.ActivePart();

	/**
	 * This provides basic block graphs for the analyses.
	 */
	private BasicBlockGraphMgr basicBlockGraphMgr;

	/**
	 * This is a map of name to objects which provide information that maybe used by analyses, but is of no use to the
	 * controller.
	 */
	private Map<Comparable<?>, Object> info;

	/**
	 * Creates a new AbstractAnalysesController object.
	 *
	 * @param infoPrm is a map of name to objects which provide information that maybe used by analyses, but is of no use to
	 *            the controller.
	 * @param pc is the preprocessing controller.
	 * @param bbgMgr provides basic blocks graphs for methods. If this is non-null then the analyses are initialized with this
	 *            graph manager. If not, the graph managers of the analyses will not be initialized. Hence, it should be done
	 *            by the application.
	 * @pre pc != null
	 */
	public AnalysesController(final Map<Comparable<?>, Object> infoPrm, final ProcessingController pc,
			final BasicBlockGraphMgr bbgMgr) {
		participatingAnalyses = new HashMap<Comparable, Collection<? extends IAnalysis>>();
		info = infoPrm;
		preprocessController = pc;
		basicBlockGraphMgr = bbgMgr;
	}

	/**
	 * Adds the implementations to be used for analysis.
	 *
	 * @param id of the analysis.
	 * @param analyses are the implementations of the named analysis.
	 * @pre id != null and analyses != null and analysis->forall(o | o != null)
	 */
	/**
	 * Puts the given values into the collection mapped to the given key in the given map. If no mapping exists, then a new
	 * mapping is created from the key to a new collection.
	 * 
	 * @param <T1> is the type of the key in the map.
	 * @param <T3> is the type of the key object.
	 * @param <T2> is the type of the objects in the value collections in the map.
	 * @param <T4> is the type of objects to be injected.
	 * @param map to be altered.
	 * @param key is the key in the map that should be altered or populated.
	 * @param values to be added into the set mapped to <code>key</code>.
	 * @return <code>true</code> if the values were added; <code>false</code>, otherwise.
	 */
//	public final void addAnalyses(final Comparable<?> id, final Collection<? extends IAnalysis> analyses) {
//		MapUtils.putAllIntoCollectionInMap(participatingAnalyses, id, analyses);
//		//MapUtils.putAllIntoCollectionInMap(participatingAnalyses, id, analyses);
//	}
	
//	@NonNull @Immutable public static <T1, T3 extends T1, T2, T4 extends T2> boolean putAllIntoCollectionInMap(
//			@NonNull final Map<T1, Collection<T2>> map, @Immutable final T3 key,
//			@NonNull @Immutable final Collection<T4> values) {
//		return putAllIntoCollectionInMapUsingFactory(map, key, values, SetUtils.<T2> getFactory());
//	}
	/**
	 * Puts the given values into the collection mapped to the given key in the given map. If no mapping exists, then a new
	 * mapping is created from the key to a new collection.
	 * 
	 * @param <T1> is the type of the key in the map.
	 * @param <T3> is the type of the key object.
	 * @param <T2> is the type of the objects in the value collections in the map.
	 * @param <T4> is the type of objects to be injected.
	 * @param map to be altered.
	 * @param key is the key in the map that should be altered or populated.
	 * @param values to be added into the set mapped to <code>key</code>.
	 * @return <code>true</code> if the values were added; <code>false</code>, otherwise.
	 */
//	@NonNull @Immutable public static <T1, T3 extends T1, T2, T4 extends T2> boolean putAllIntoCollectionInMap(
//			@NonNull final Map<T1, Collection<T2>> map, @Immutable final T3 key,
//			@NonNull @Immutable final Collection<T4> values) {
//		return putAllIntoCollectionInMapUsingFactory(map, key, values, SetUtils.<T2> getFactory());
//	}
	//participatingAnalyses=map
	//id=T3 Key
	//T4: analyses value
//	@NonNull @Immutable public static <T1, T3 extends T1, T2, T4 extends T2, T5 extends Collection<T2>, T6 extends T5> boolean putAllIntoCollectionInMapUsingFactory(
//			@NonNull final Map<T1, T5> map, @Immutable final T3 key, @NonNull @Immutable final Collection<T4> values,
//			@NonNull @Immutable final IFactory<T6> factory) {
//		T5 _temp = map.get(key);
//
//		if (_temp == null) {
//			_temp = factory.create();
//			map.put(key, _temp);
//		}
//		return _temp.addAll(values);
//	}
//	return putAllIntoCollectionInMapUsingFactory(map, key, values, SetUtils.<T2> getFactory());
	
	public final void addAnalyses(Comparable<?> id, Collection<? extends IAnalysis> analyses) {
//		MapUtils.putAllIntoCollectionInMap(participatingAnalyses, id, analyses);
//		Map<participatingAnalyses, Collection<analyses>> map = new 
//		T5 _temp = map.get(key);
//
//		if (_temp == null) {
//			_temp = factory.create();
//			map.put(id, analyses);
//		}
//		return _temp.addAll(values);
		participatingAnalyses.put(id, analyses);
		//System.out.println(" Adding participatingAnalyses size()="+participatingAnalyses.size());
//		Comparable _temp=(Comparable) participatingAnalyses.get(id);
//		if (_temp==null)
//		{
//			_temp= (Comparable) SetUtils.getFactory().create();
//			participatingAnalyses.put(id, analyses);
//			System.out.println(" Adding participatingAnalyses size()="+participatingAnalyses.size());
//		}
	}
	/**
	 * Executes the analyses in the registered order.
	 */
	public void execute() {
		boolean _analyzing;
		final Collection<IAnalysis> _done = new ArrayList<IAnalysis>();
		System.out.println("**************** participatingAnalyses.size()="+participatingAnalyses.size());
        do {
			_analyzing = false;

			for (final Iterator<Comparable> _i = participatingAnalyses.keySet().iterator(); _i.hasNext()
					&& activePart.canProceed();) {
				final Comparable<?> _daName = _i.next();
				System.out.println("  participatingAnalyses Key: "+_daName);
				if (_daName.toString().indexOf("equivalence class based escape analysis")>=0)
					System.out.println("equivalence class based escape analysis");
				final Collection<? extends IAnalysis> _c = participatingAnalyses.get(_daName);

				for (final Iterator<? extends IAnalysis> _j = _c.iterator(); _j.hasNext();) {
					final IAnalysis _analysis = _j.next();

					if (_analysis != null && !_done.contains(_analysis)) {
						_analysis.analyze();

						final boolean _t = _analysis.isStable();

						if (_t) {
							_done.add(_analysis);
						}
						_analyzing |= _t;
					}
				}
			}
		} while (_analyzing);
	}

	/**
	 * Returns the active part of this object.
	 *
	 * @return the active part.
	 */
	public IActivePart getActivePart() {
		return activePart;
	}

	/**
	 * Provides the implementation registered for the given analysis purpose.
	 *
	 * @param id of the requested analyses. This has to be one of the names(XXX_DA) defined in this class.
	 * @return the implementation registered for the given purpose. Changes to this collection is visible to the controller.
	 * @post result != null
	 */
	public final Collection<? extends IAnalysis> getAnalyses(final Comparable<?> id) {
		final Collection<? extends IAnalysis> _result;

		if (participatingAnalyses != null) {
			_result = participatingAnalyses.get(id);
		} else {
			_result = null;
		}
		return _result;
	}

	/**
	 * Initializes the controller. Analyses are initialized and then driven to preprocess the system (in that order only).
	 */
	public void initialize() {
		final Collection<Comparable<?>> _failed = new ArrayList<Comparable<?>>();
		final Collection<IProcessor> _preprocessors = new HashSet<IProcessor>();

		for (final Iterator<Comparable> _k = participatingAnalyses.keySet().iterator(); _k.hasNext()
				&& activePart.canProceed();) {
			final Comparable<?> _key = _k.next();
			final Collection<? extends IAnalysis> _c = participatingAnalyses.get(_key);

			for (final Iterator<? extends IAnalysis> _j = _c.iterator(); _j.hasNext() && activePart.canProceed();) {
				final IAnalysis _analysis = _j.next();

				try {
					_analysis.initialize(info);

					if (_analysis.doesPreProcessing()) {
						final IProcessor _p = _analysis.getPreProcessor();
						_p.hookup(preprocessController);
						_preprocessors.add(_p);
					}

					if (basicBlockGraphMgr != null) {
						_analysis.setBasicBlockGraphManager(basicBlockGraphMgr);
					}
				} catch (final InitializationException _e) {
					if (LOGGER.isWarnEnabled()) {
						LOGGER.warn(_analysis.getClass() + " failed to initialize, hence, it will not executed.", _e);
					}
					_failed.add(_key);

					if (_analysis.doesPreProcessing()) {
						_preprocessors.remove(_analysis.getPreProcessor());
					}
				}
			}

			for (final Iterator<Comparable<?>> _i = _failed.iterator(); _i.hasNext();) {
				_c.remove(_i.next());
			}
		}

		if (activePart.canProceed()) {
			preprocessController.process();
		}

		for (final Iterator<IProcessor> _i = _preprocessors.iterator(); _i.hasNext();) {
			_i.next().unhook(preprocessController);
		}
	}

	/**
	 * Resets the internal data structures of the controller. This resets the participating analyses. This does not reset the
	 * Object Flow Analysis instance.
	 */
	public void reset() {
		for (final Iterator<Collection<? extends IAnalysis>> _i = participatingAnalyses.values().iterator(); _i.hasNext();) {
			final Collection<? extends IAnalysis> _c = _i.next();

			for (final Iterator<? extends IAnalysis> _j = _c.iterator(); _j.hasNext();) {
				final IAnalysis _analysis = _j.next();
				_analysis.reset();
			}
		}
		participatingAnalyses.clear();
		activePart.activate();
	}
}

// End of File

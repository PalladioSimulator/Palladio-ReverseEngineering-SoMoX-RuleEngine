package org.somox.metrics.helper;

import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClass;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.somox.configuration.SoMoXConfiguration;
import org.somox.filter.AccessedTargetBlacklistFilter;
import org.somox.filter.BaseFilter;
import org.somox.filter.EClassBasedFilter;
import org.somox.metrics.util.GraphPrinter;

import de.fzi.gast.accesses.Access;
import de.fzi.gast.accesses.accessesPackage;
import de.fzi.gast.types.GASTClass;

/**
 * A class used as filter to filter the available classes being potential component candidates 
 * based on a blacklist of component names
 * @author Michael Hauck, Klaus Krogmann, Steffen Becker
  */
public class Class2ClassAccessGraphHelper {
	
	private static final Logger logger = Logger.getLogger(Class2ClassAccessGraphHelper.class);
	
	private static final BaseFilter<GASTClass> primitiveTypeFilter = new BaseFilter<GASTClass>() {
		
		@Override
		public boolean passes(GASTClass object) {
			return !object.isPrimitive();
		}
	};

	private static final EdgeFactory<GASTClass, ClassAccessGraphEdge> edgeFactory = new EdgeFactory<GASTClass, ClassAccessGraphEdge>() {
		public ClassAccessGraphEdge createEdge(GASTClass source, GASTClass target) {
			return new ClassAccessGraphEdge(source, target);
		}
	};
	
	/**
	 * Computes a map which maps full qualified class names of source classes (FQN) on a set containing tuples (GASTClass, count). Each tuple contains a class which is directly 
	 * accessed by the source class and the number of (different) accesses from the source to each target class. Visually this is a graph containing the GAST classes as nodes and directed
	 * links between the nodes if there is an access from source to target and the link has a weight giving the number of "links" from source to target 
	 * @param filter A list of regular expression patterns used to filter the nodes of GAST classes contained in the graph. Matching classes are not added to the graph
	 * @param componentsImplementingClasses The list of classes used in the detected initial components. This list only contains classes filtered by the blacklist as the blacklist is
	 * also used in the initial component detection
	 * @return A "graph" giving the connections of GAST classes and their number of accesses
	 */
	public static DirectedGraph<GASTClass,ClassAccessGraphEdge> computeFilteredClass2ClassAccessGraph(
			SoMoXConfiguration somoxConfiguration, 
			Set<GASTClass> componentsImplementingClasses) {
		
		DirectedGraph<GASTClass,ClassAccessGraphEdge> accessGraph = new SimpleDirectedGraph<GASTClass, ClassAccessGraphEdge>(edgeFactory);
		for (GASTClass clazz : primitiveTypeFilter.filter(componentsImplementingClasses)) {
			accessGraph.addVertex(clazz);
		}

		assert noPrimitiveTypesAsVertexes(accessGraph);
		
		AccessedTargetBlacklistFilter filter = new AccessedTargetBlacklistFilter(somoxConfiguration.getBlacklistFilter());
		
		for (GASTClass clazz : componentsImplementingClasses) {
			addAccessesToGraph(accessGraph, filter, clazz);
		}
		
		assert noPrimitiveTypesAsVertexes(accessGraph);
		
		if (logger.isDebugEnabled()) {
			GraphPrinter.dumpGraph(
					new ComponentToImplementingClassesHelper(), 
					accessGraph, 
					somoxConfiguration.getFileLocations().getAnalyserInputFile(), 0, 0);
		}
		return accessGraph;
	}

	private static boolean noPrimitiveTypesAsVertexes(
			DirectedGraph<GASTClass, ClassAccessGraphEdge> accessGraph) {
		boolean result = true;
		for (GASTClass clazz : accessGraph.vertexSet()) {
			result &= !clazz.isPrimitive();
		}
		return result;
	}

	private static final EClassBasedFilter<Access> accessFilter = new EClassBasedFilter<Access>(
			new EClass[]{accessesPackage.eINSTANCE.getInheritanceTypeAccess()});
	
	/**
	 * Compute the outgoing links for the node containing class "clazz". Links pointing to classes which match the blacklist pattern are not created
	 * @param filter Blacklist match pattern. Used to remove potential targets
	 * @param clazz The class for which to compute the outgoing links
	 * @return A set of target classes and their link weights. Weights model the number of accesses between clazz and its respective target
	 */
	private static void addAccessesToGraph(
			Graph<GASTClass,ClassAccessGraphEdge> graph,
			AccessedTargetBlacklistFilter filter,
			GASTClass clazz) {
				
		for (Access singleAccess : accessFilter.filter(filter.filter(clazz.getAllAccesses()))) {
			GASTClass accessedClass = singleAccess.getAccessedClass();
			
			// Relations between the class itself are not interesting...
			if (clazz == accessedClass)
				continue;
			
			ClassAccessGraphEdge edge = graph.getEdge(clazz, accessedClass);
			if (edge == null) {
				if (graph.containsVertex(clazz) && graph.containsVertex(accessedClass)) {
					edge = graph.addEdge(clazz, accessedClass);
				} else {
					logger.trace("Do not add edge between "+clazz+" and "+accessedClass);
				}
			}
			if (edge != null) {
				edge.incrementCount();
			}
		}
	}
}

package org.somox.metrics.helper;

import java.util.Set;

import org.somox.filter.BaseFilter;

import de.fzi.gast.types.GASTClass;

public class SourceClassEdgeFilter extends BaseFilter<ClassAccessGraphEdge> { 
 
	private Set<GASTClass> filteredTarget;
	
	/**
	 * 
	 * @param filteredTarget positive list of non-filtered target class accesses
	 */
	public SourceClassEdgeFilter(Set<GASTClass> filteredTarget) {
		this.filteredTarget = filteredTarget;
	}
	
	@Override
	public boolean passes(ClassAccessGraphEdge object) {
		return filteredTarget.contains(object.getSourceClazz());
	}
		
}

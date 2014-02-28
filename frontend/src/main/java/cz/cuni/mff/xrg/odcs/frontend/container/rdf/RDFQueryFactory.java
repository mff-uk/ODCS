package cz.cuni.mff.xrg.odcs.frontend.container.rdf;

import org.vaadin.addons.lazyquerycontainer.Query;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;
import org.vaadin.addons.lazyquerycontainer.QueryFactory;

/**
 * Simple {@link QueryFactory} for constructing RDF queries.
 *
 * @author Bogo
 */
public class RDFQueryFactory implements QueryFactory {

	/**
	 * Construct query from definition.
	 *
	 * @param qd Query definition.
	 * @return Query.
	 */
	@Override
	public Query constructQuery(QueryDefinition qd) {
		if(qd.getClass() != RDFQueryDefinition.class) {
			throw new UnsupportedOperationException("Unsupported QueryDefinition class.");
		}
		return new RDFQuery((RDFQueryDefinition)qd);
	}
	
}

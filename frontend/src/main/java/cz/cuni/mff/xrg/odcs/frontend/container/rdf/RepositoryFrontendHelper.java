/**
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.cuni.mff.xrg.odcs.frontend.container.rdf;

import info.aduna.iteration.Iterations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.*;
import org.openrdf.query.resultio.TupleQueryResultWriter;
import org.openrdf.query.resultio.sparqljson.SPARQLResultsJSONWriter;
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.openrdf.query.resultio.text.csv.SPARQLResultsCSVWriter;
import org.openrdf.query.resultio.text.tsv.SPARQLResultsTSVWriter;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Container;
import com.vaadin.ui.UI;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.DataUnitInfo;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionInfo;
import cz.cuni.mff.xrg.odcs.frontend.AppEntry;
import cz.cuni.mff.xrg.odcs.frontend.dataunit.FrontendDataUnitManager;
import cz.cuni.mff.xrg.odcs.frontend.i18n.Messages;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.enums.SelectFormatType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.query.utils.QueryFilterManager;
import cz.cuni.mff.xrg.odcs.rdf.query.utils.RegexFilter;
import cz.cuni.mff.xrg.odcs.rdf.repositories.GraphUrl;
import cz.cuni.mff.xrg.odcs.rdf.repositories.MyRDFHandler;
import eu.unifiedviews.dataunit.rdf.impl.ManageableWritableRDFDataUnit;
import eu.unifiedviews.helpers.dataunit.dataset.DatasetBuilder;

public class RepositoryFrontendHelper {

    private static final Logger log = LoggerFactory.getLogger(RepositoryFrontendHelper.class);

    /**
     * Return repository for specified RDF DataUnit. Also load the repository.
     * 
     * @param executionInfo
     *            The pipelineExecution context.
     * @param dpuInstance
     *            Owner of DataUnit.
     * @param dataUnitInfo
     * @return Repository or null if there is no browser for given type.
     */
    public static ManageableWritableRDFDataUnit getRepository(ExecutionInfo executionInfo,
            DPUInstanceRecord dpuInstance, DataUnitInfo dataUnitInfo) {
        final FrontendDataUnitManager dataUnitManager = ((AppEntry) UI.getCurrent()).getBean(FrontendDataUnitManager.class);
        return dataUnitManager.getRDFDataUnit(executionInfo, dpuInstance, dataUnitInfo, FrontendDataUnitManager.CLOSE_TIME_SHORT);
    }

    /**
     * Longer time before close then getRepository.
     *
     * @param executionInfo
     * @param dpuInstance
     * @param dataUnitInfo
     * @return
     */
    public static ManageableWritableRDFDataUnit getRepositoryForDownload(ExecutionInfo executionInfo,
            DPUInstanceRecord dpuInstance, DataUnitInfo dataUnitInfo) {
        final FrontendDataUnitManager dataUnitManager = ((AppEntry) UI.getCurrent()).getBean(FrontendDataUnitManager.class);
        return dataUnitManager.getRDFDataUnit(executionInfo, dpuInstance, dataUnitInfo, FrontendDataUnitManager.CLOSE_TIME_LONG);
    }

    /**
     * Filter RDF query.
     * 
     * @param query
     *            Query to filter.
     * @param filters
     *            Filters to apply.
     * @return Filtered query.
     */
    public static String filterRDFQuery(String query, Collection<Container.Filter> filters) {
        if (filters == null) {
            return query;
        }

        QueryFilterManager filterManager = new QueryFilterManager(query);
        for (Container.Filter filter : filters) {
            if (filter.getClass() == RDFRegexFilter.class) {
                RDFRegexFilter rdfRegexFilter = (RDFRegexFilter) filter;
                RegexFilter rf = new RegexFilter(rdfRegexFilter.getColumnName(),
                        rdfRegexFilter.getRegex());
                filterManager.addFilter(rf);
            }
        }
        return filterManager.getFilteredQuery();
    }

    public static File executeSelectQuery(RepositoryConnection connection, String selectQuery, String filePath,
            SelectFormatType selectType, Set<URI> dataGraph) throws InvalidQueryException {
        try {

            TupleQuery tupleQuery = connection.prepareTupleQuery(
                    QueryLanguage.SPARQL, selectQuery);
            tupleQuery.setDataset(new DatasetBuilder().withDefaultGraphs(dataGraph).withNamedGraphs(dataGraph).build());

            log.debug("Query {} is valid.", selectQuery);

            File file = new File(filePath);
            try {
                file.createNewFile();
            } catch (IOException e) {
                log.debug(e.getMessage());
            }

            FileOutputStream os = new FileOutputStream(file);

            TupleQueryResultWriter tupleHandler;

            switch (selectType) {
                case XML:
                    tupleHandler = new SPARQLResultsXMLWriter(os);
                    break;
                case CSV:
                    tupleHandler = new SPARQLResultsCSVWriter(os);
                    break;
                case JSON:
                    tupleHandler = new SPARQLResultsJSONWriter(os);
                    break;
                case TSV:
                    tupleHandler = new SPARQLResultsTSVWriter(os);
                    break;
                default:
                    tupleHandler = new SPARQLResultsXMLWriter(os);

            }

            tupleQuery.evaluate(tupleHandler);
            return file;

        } catch (QueryEvaluationException | MalformedQueryException ex) {
            throw new InvalidQueryException(
                    Messages.getString("RepositoryFrontendHelper.select.invalidQuery") + ex.getMessage(),
                    ex);
        } catch (TupleQueryResultHandlerException ex) {
            log.error("Writing result to file fail. {}", ex.getMessage(),
                    ex);

        } catch (RepositoryException ex) {
            log.error("Connection to RDF repository failed. {}",
                    ex.getMessage(), ex);
        } catch (IOException ex) {
            log.error("Stream were not closed. {}", ex.getMessage(), ex);
        }

        throw new InvalidQueryException(
                Messages.getString("RepositoryFrontendHelper.select.dataFault"));

    }

    /**
     * Make select query over repository data and return tables as result.
     * 
     * @param selectQuery
     *            String representation of SPARQL select query.
     * @return <code>Map&lt;String,List&lt;String&gt;&gt;</code> as table, where
     *         map key is column name * * and <code>List&lt;String&gt;</code> are string values in this column. When query is invalid, return *
     *         * * * empty <code>Map</code>.
     * @throws InvalidQueryException
     *             when query is not valid.
     */
    public static Map<String, List<String>> executeSelectQuery(RepositoryConnection connection,
            String selectQuery, Set<URI> dataGraph)
            throws InvalidQueryException {

        Map<String, List<String>> map = new LinkedHashMap<>();

        List<BindingSet> listBindings = new ArrayList<>();
        TupleQueryResult result = null;
        try {
            result = executeSelectQueryAsTuples(connection, selectQuery, dataGraph);

            List<String> names = result.getBindingNames();

            for (String name : names) {
                map.put(name, new LinkedList<String>());
            }

            listBindings = Iterations.asList(result);
        } catch (QueryEvaluationException ex) {
            throw new InvalidQueryException(
                    Messages.getString("RepositoryFrontendHelper.evaluationException") + ex
                            .getMessage(),
                    ex);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (QueryEvaluationException ex) {
                    log.warn("Failed to close RDF tuple result. {}",
                            ex.getMessage(), ex);
                }
            }
        }

        for (BindingSet bindingNextSet : listBindings) {
            for (Binding next : bindingNextSet) {

                String name = next.getName();
                Value value = next.getValue();

                String stringValue;

                if (value != null) {
                    stringValue = value.stringValue();
                } else {
                    stringValue = "";
                }

                if (map.containsKey(name)) {
                    map.get(name).add(stringValue);
                }

            }
        }

        return map;
    }

    /**
     * Make select query over repository data and return MyTupleQueryResult
     * class as result.
     * 
     * @param selectQuery
     *            String representation of SPARQL select query.
     * @return MyTupleQueryResult representation of SPARQL select query.
     * @throws InvalidQueryException
     *             when query is not valid.
     */
    public static TupleQueryResult executeSelectQueryAsTuples(RepositoryConnection connection,
            String selectQuery, Set<URI> dataGraph)
            throws InvalidQueryException {

        try {

            TupleQuery tupleQuery = connection.prepareTupleQuery(
                    QueryLanguage.SPARQL, selectQuery);
            tupleQuery.setDataset(new DatasetBuilder().withDefaultGraphs(dataGraph).withNamedGraphs(dataGraph).build());

            log.debug("Query {} is valid.", selectQuery);

            try {
                TupleQueryResult tupleResult = tupleQuery.evaluate();
                log.debug("Query {} has not null result.", selectQuery);

                return tupleResult;

            } catch (QueryEvaluationException ex) {
                throw new InvalidQueryException(
                        Messages.getString("RepositoryFrontendHelper.selectAsTuples.evaluation") + ex
                                .getMessage(),
                        ex);
            }

        } catch (MalformedQueryException ex) {
            throw new InvalidQueryException(
                    Messages.getString("RepositoryFrontendHelper.selectAsTuples.malformed")
                            + ex.getMessage(), ex);
        } catch (RepositoryException ex) {
            log.error("Connection to RDF repository failed. {}",
                    ex.getMessage(), ex);
        }
        throw new InvalidQueryException(
                Messages.getString("RepositoryFrontendHelper.selectAsTuples.failed"));
    }

    /**
     * Returns graph contains all RDF triples as result of describe query for
     * given Resource URI. If graph is empty, there is are no triples for
     * describing.
     * 
     * @param uriResource
     *            Subject or object URI as resource use to describe it.
     * @return Graph contains all RDF triples as result of describe query for
     *         given Resource URI. If graph is empty, there is are no triples
     *         for describing.
     * @throws InvalidQueryException
     *             if resource is not URI type (e.g.
     *             BlankNode, some type of Literal (in object
     *             case))
     */
    public static Graph describeURI(RepositoryConnection connection, Set<URI> dataGraph, Resource uriResource) throws InvalidQueryException {

        if (uriResource instanceof URI) {
            String describeQuery = String.format("DESCRIBE <%s>", uriResource
                    .toString());

            return executeConstructQuery(connection, describeQuery, new DatasetBuilder().withDefaultGraphs(dataGraph).withNamedGraphs(dataGraph).build());
        } else {
            throw new InvalidQueryException(
                    Messages.getString("RepositoryFrontendHelper.describe.resource", uriResource.toString()));
        }

    }

    /**
     * Make construct query over graph URIs in dataSet and return interface
     * Graph as result contains iterator for statements (triples).
     * 
     * @param constructQuery
     *            String representation of SPARQL query.
     * @param dataSet
     *            Set of graph URIs used for construct query.
     * @return Interface Graph as result of construct SPARQL query.
     * @throws InvalidQueryException
     *             when query is not valid.
     */
    public static Graph executeConstructQuery(RepositoryConnection connection, String constructQuery, Dataset dataSet)
            throws InvalidQueryException {

        try {

            GraphQuery graphQuery = connection.prepareGraphQuery(
                    QueryLanguage.SPARQL,
                    constructQuery);

            graphQuery.setDataset(dataSet);

            log.debug("Query {} is valid.", constructQuery);

            try {
                GraphQueryResult result = graphQuery.evaluate();
                log.debug("Query {} has not null result.", constructQuery);
                return QueryResults.asModel(result);

            } catch (QueryEvaluationException ex) {
                throw new InvalidQueryException(
                        Messages.getString("RepositoryFrontendHelper.construct.evaluation") + ex
                                .getMessage(),
                        ex);
            }

        } catch (MalformedQueryException ex) {
            throw new InvalidQueryException(
                    Messages.getString("RepositoryFrontendHelper.construct.malformed")
                            + ex.getMessage(), ex);
        } catch (RepositoryException ex) {
            log.error("Connection to RDF repository failed. {}",
                    ex.getMessage(), ex);
        }

        throw new InvalidQueryException(
                Messages.getString("RepositoryFrontendHelper.construct.failed"));
    }

    /**
     * Make construct query over repository data and return file where RDF data
     * as result are saved.
     * 
     * @param constructQuery
     *            String representation of SPARQL query.
     * @param formatType
     *            Choosed type of format RDF data in result.
     * @param filePath
     *            String path to file where result with RDF data is
     *            stored.
     * @return File with RDF data in defined format as result of construct
     *         query.
     * @throws InvalidQueryException
     *             when query is not valid or creating file
     *             fail.
     */
    public static File executeConstructQuery(RepositoryConnection connection, Set<URI> dataGraph, String constructQuery,
            RDFFormatType formatType, String filePath) throws InvalidQueryException {

        try {

            GraphQuery graphQuery = connection.prepareGraphQuery(
                    QueryLanguage.SPARQL,
                    constructQuery);
            graphQuery.setDataset(new DatasetBuilder().withDefaultGraphs(dataGraph).withNamedGraphs(dataGraph).build());

            log.debug("Query {} is valid.", constructQuery);

            try {

                File file = new File(filePath);
                MyRDFHandler goal = getHandlerForConstructQuery(file, formatType);

                graphQuery.evaluate(goal);

                log.debug("Query {} has not null result.", constructQuery);

                return file;

            } catch (QueryEvaluationException ex) {
                throw new InvalidQueryException(
                        Messages.getString("RepositoryFrontendHelper.construct2.evaluation") + ex
                                .getMessage(),
                        ex);
            } catch (IOException ex) {
                log.error("Problems with file stream : {}", ex.getMessage(),
                        ex);
            }

        } catch (MalformedQueryException ex) {
            throw new InvalidQueryException(
                    Messages.getString("RepositoryFrontendHelper.construct.malformed")
                            + ex.getMessage(), ex);
        } catch (RepositoryException ex) {
            log.error("Connection to RDF repository failed. {}", ex
                    .getMessage(), ex);
        } catch (RDFHandlerException ex) {
            log.error("RDF handler failed. " + ex.getMessage(), ex);
        }

        throw new InvalidQueryException(
                Messages.getString("RepositoryFrontendHelper.construct2.failed"));
    }

    private static MyRDFHandler getHandlerForConstructQuery(File file,
            RDFFormatType formatType) throws IOException {

        try {
            file.createNewFile();
        } catch (IOException e) {
            log.debug(e.getMessage());
        }

        FileOutputStream os = new FileOutputStream(file);

        MyRDFHandler goal = new MyRDFHandler(os, formatType);

        return goal;

    }

    /**
     * Transform RDF in repository by SPARQL updateQuery.
     * 
     * @param updateQuery
     *            String value of update SPARQL query.
     * @param dataset
     * @throws cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException
     *             when transformation fault.
     */
    public static void executeSPARQLUpdateQuery(RepositoryConnection connection, String updateQuery, URI dataGraph, Dataset dataset)
            throws RDFException {

        try {

            String newUpdateQuery = AddGraphToUpdateQuery(updateQuery, dataGraph);
            Update myupdate = connection.prepareUpdate(QueryLanguage.SPARQL,
                    newUpdateQuery);
            myupdate.setDataset(dataset);

            log.debug("This SPARQL update query is valid and prepared for execution:");
            log.debug(newUpdateQuery);

            myupdate.execute();
            //connection.commit();

            log.debug("SPARQL update query for was executed successfully");

        } catch (MalformedQueryException e) {

            log.debug(e.getMessage());
            throw new RDFException(e.getMessage(), e);

        } catch (UpdateExecutionException ex) {

            final String message = Messages.getString("RepositoryFrontendHelper.update.not.executed");
            log.debug(message);
            log.debug(ex.getMessage());

            throw new RDFException(message + ex.getMessage(), ex);

        } catch (RepositoryException ex) {
            throw new RDFException(
                    Messages.getString("RepositoryFrontendHelper.update.exception")
                            + ex.getMessage(), ex);
        }

    }

    /**
     * @param updateQuery
     *            String value of SPARQL update query.
     * @return String extension of given update query works with set repository
     *         GRAPH.
     */
    public static String AddGraphToUpdateQuery(String updateQuery, URI dataGraph) {

        String regex = "(insert|delete)\\s\\{";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(updateQuery.toLowerCase());

        boolean hasResult = matcher.find();
        boolean hasWith = updateQuery.toLowerCase().contains("with");

        if (hasResult && !hasWith) {

            int index = matcher.start();

            String first = updateQuery.substring(0, index);
            String second = updateQuery.substring(index, updateQuery.length());

            String graphName = " WITH <" + dataGraph.stringValue() + "> ";

            String newQuery = first + graphName + second;
            return newQuery;

        } else {

            log.debug("WITH graph clause was not added, "
                    + "because the query was: {}", updateQuery);

            regex = "(insert|delete)\\sdata\\s\\{";
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(updateQuery.toLowerCase());

            hasResult = matcher.find();

            if (hasResult) {

                int start = matcher.start();
                int end = matcher.end();

                String first = updateQuery.substring(0, start);
                String second = updateQuery.substring(end, updateQuery.length());

                String myString = updateQuery.substring(start, end);
                String graphName = myString.replace("{",
                        "{ GRAPH <" + dataGraph.stringValue() + "> {");

                second = second.replaceFirst("}", "} }");
                String newQuery = first + graphName + second;

                return newQuery;

            }
        }
        return updateQuery;

    }

    /**
     * Delete all application graphs keeps in Virtuoso storage in case of
     * Virtuoso repository. When is used local repository as storage, this
     * method has no effect.
     * 
     * @return Info string message about removing application graphs.
     */
    public static String deleteApplicationGraphs(RepositoryConnection connection, Set<URI> dataGraph) {

        List<String> graphs = getApplicationGraphs(connection, dataGraph);

        String returnMessage;

        if (graphs.isEmpty()) {
            returnMessage = "NO APPLICATIONS GRAPHS to DELETE";
            log.info(returnMessage);
        } else {
            for (String nextGraph : graphs) {
                deleteNamedGraph(connection, nextGraph);
            }
            returnMessage = "TOTAL deleted: " + graphs.size() + " application graphs";
            log.info(returnMessage);

        }

        return returnMessage;
    }

    private static void deleteNamedGraph(RepositoryConnection connection, String graphName) {

        try {
            connection.clear(new URIImpl(graphName));

            log.info("Graph {} was sucessfully deleted", graphName);
        } catch (RepositoryException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }

    }

    /**
     * @return List of all application graphs keeps in Virtuoso storage in case
     *         of Virtuoso repository. When is used local repository as storage,
     *         this method return an empty list.
     */
    public static List<String> getApplicationGraphs(RepositoryConnection connection, Set<URI> dataGraph) {
        List<String> result = new ArrayList<>();

        try {
            String select = "select distinct ?g where {graph ?g {?s ?p ?o}}";
            TupleQueryResult tupleResult = executeSelectQueryAsTuples(connection, select, dataGraph);

            String prefix = GraphUrl.getGraphPrefix();

            for (BindingSet set : Iterations.asList(tupleResult)) {

                for (String name : set.getBindingNames()) {
                    String graphName = set.getValue(name).stringValue();

                    if (graphName.startsWith(prefix)) {
                        result.add(graphName);
                    }
                }
            }
            tupleResult.close();
        } catch (InvalidQueryException | QueryEvaluationException e) {
            log.debug(e.getMessage());
        }

        return result;
    }
}

package cz.cuni.mff.xrg.odcs.extractor.datanest;

/* Copyright (C) 2011 Peter Hanecak <hanecak@opendata.sk>
 *
 * This file is part of Open Data Node.
 *
 * Open Data Node is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Open Data Node is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Open Data Node.  If not, see <http://www.gnu.org/licenses/>.
 */

import cz.cuni.mff.xrg.odcs.extractor.data.AbstractRecord;
import cz.cuni.mff.xrg.odcs.extractor.serialization.AbstractSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Vector;

/**
 * Stuff common to all harvesters. Harvesters (along with serializers) perform
 * ETL (http://en.wikipedia.org/wiki/Extract,_transform,_load).
 * <p/>
 * What is a common harvesting work-flow:
 * 1) download the original document(s) from the main source of data
 * 2) store the copy of original(s) in primary repository
 * why: to have audit trail, our own copy, possibly to run next steps
 * from this own copy instead of downloading (possibly unchanged)
 * document repeatedly
 * 3) extract data
 * 4) enhance data: clean-up, correction, correlation, possibly production
 * of new data via math/logic, ...
 * 5) serialize data into format(s) suitable for ODN back-end repository(ies)
 * 6) store the data into ODN back-end repository(ies)
 * <p/>
 * Note in regards to "primary repository": After we harvest the data, we store
 * it into multiple repositories to serve for multiple purposes. In current
 * architecture, it means Jackrabbit as primary document store (with full data:
 * original record, harvested and enhanced record, ...) and secondary stores
 * SOLR (for full-text search) and Sesame (for RDF and SPARQL).
 *
 * @param <RecordType> type of individual record into which the harvested data are stored
 *                     into
 */
public abstract class AbstractHarvester<RecordType extends AbstractRecord> {

    public final static String ODN_HARVESTER_TMP_PREF = "odn-harvester-";
    public final static String ODN_HARVESTER_TMP_SUFF = ".tmp";
    private static Logger logger = LoggerFactory.getLogger(AbstractHarvester.class);
    private URL sourceUrl = null;
    private Vector<AbstractSerializer<RecordType, ?, ?>> serializers = null;
    private Object primaryRepository = null;

    /**
     * @throws java.io.IOException when initialization of primary repository fails
     */
    public AbstractHarvester() {

        // TODO remove this          primaryRepository
        this.primaryRepository = null;
        this.serializers = new Vector<AbstractSerializer<RecordType, ?, ?>>();
    }

    // TODO remove this       primaryRepository
    public Object getPrimaryRepository() {
        return primaryRepository;
    }

    public URL getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(URL sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public Vector<AbstractSerializer<RecordType, ?, ?>> getSerializers() {
        return serializers;
    }

    public void addSerializer(AbstractSerializer<RecordType, ?, ?> serializer) {
        serializers.add(serializer);
    }

    protected void storeOriginalData() {
        // TODO: get the data as downloaded and store then into dedicated node in Jackrabbit, say /raw/datanest/ppd/2012/08/22/hh/mm
    }

    /**
     * Extract, transform and load the data.
     *
     * @param sourceFile temporary file holding freshly obtained data to harvest from
     */
    abstract public Vector<RecordType> performEtl(File sourceFile) throws Exception;

    /**
     * Loop through all serializers and pass given records to them. Serializers
     * will serialize the records and store them.
     *
     * @param records list of records to serialize and store
     * @throws IllegalArgumentException if repository with given name does not exists
     */
    protected void store(List<RecordType> records) throws Exception {

        if (records.size() <= 0)
        // nothing to store so why bother?
        {
            return;
        }


        System.out.println(records);

        for (AbstractSerializer<RecordType, ?, ?> serializer : serializers)
            serializer.store(records);
    }
}

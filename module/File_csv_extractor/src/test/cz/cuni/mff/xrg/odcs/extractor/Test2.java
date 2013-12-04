package cuni.mff.xrg.odcs.extractor;

import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.extractor.file.FileCsvExtractor2;
import cz.cuni.mff.xrg.odcs.extractor.file.FileCsvExtractorConfig;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;

public class Test2 {

    @org.junit.Test
    public void test() throws ConfigException {
        FileCsvExtractor2 extractor = new FileCsvExtractor2();
        FileCsvExtractorConfig config = new FileCsvExtractorConfig();
        extractor.configureDirectly(config);

        TestEnvironment env = TestEnvironment.create();
        try {
            RDFDataUnit output = env.createRdfOutput("output", false);
            // run the execution
            String input = null;
            env.run(extractor);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            // release resources
            env.release();
        }
    }
}

package cz.cuni.mff.xrg.odcs.extractor.core;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;
import cz.cuni.mff.xrg.odcs.rdf.enums.FileExtractType;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;

/**
 * File extractor configuration.
 * 
 * @author Jan Marcek
 * 
 */
public class CsvOrganizationExtractorConfig extends DPUConfigObjectBase {

    public String TargetRDF = "e:/eea/comsode/rdf";
    public String Path = "file:/e:/eea/comsode/rdf/source/organisations-dump.csv";
    public RDFFormatType RDFFormatValue = RDFFormatType.AUTO;
    public String FileSuffix = "";

    public FileExtractType fileExtractType = FileExtractType.PATH_TO_FILE;
    public boolean OnlyThisSuffix = false;
    public boolean UseStatisticalHandler = true;
    public boolean failWhenErrors = false;
    public Integer DebugProcessOnlyNItems = new Integer(10000);
    public Integer BatchSize = new Integer(10000);

    @Override
    public boolean isValid() {
        return Path != null && TargetRDF != null && FileSuffix != null && RDFFormatValue != null && fileExtractType != null && DebugProcessOnlyNItems != null
                && BatchSize != null;
    }
}

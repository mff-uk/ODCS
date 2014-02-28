package cz.cuni.mff.xrg.odcs.extractor.file;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;
import cz.cuni.mff.xrg.odcs.rdf.enums.FileExtractType;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;

/**
 * File extractor configuration.
 *
 * @author Petyr
 * @author Jiri Tomes
 *
 */
public class FileExtractorConfig extends DPUConfigObjectBase {

	private String Path;

	private String FileSuffix;

	private RDFFormatType RDFFormatValue;

	private FileExtractType fileExtractType;

	private boolean OnlyThisSuffix;

	private boolean UseStatisticalHandler;

	private boolean failWhenErrors;

	public FileExtractorConfig() {
		this.Path = "";
		this.FileSuffix = "";
		this.RDFFormatValue = RDFFormatType.AUTO;
		this.fileExtractType = FileExtractType.PATH_TO_FILE;
		this.OnlyThisSuffix = false;
		this.UseStatisticalHandler = true;
		this.failWhenErrors = false;
	}

	public FileExtractorConfig(String Path, String FileSuffix,
			RDFFormatType RDFFormatValue, FileExtractType fileExtractType,
			boolean OnlyThisSuffix, boolean UseStatisticalHandler,
			boolean failWhenErrors) {

		this.Path = Path;
		this.FileSuffix = FileSuffix;
		this.RDFFormatValue = RDFFormatValue;
		this.fileExtractType = fileExtractType;
		this.OnlyThisSuffix = OnlyThisSuffix;
		this.UseStatisticalHandler = UseStatisticalHandler;
		this.failWhenErrors = failWhenErrors;
	}

	/**
	 * Returns the path to file as string value.
	 *
	 * @return the path to file as string value.
	 */
	public String getPath() {
		return Path;
	}

	/**
	 * Returns the value of file suffix, empty string if no suffix is defined.
	 *
	 * @return the file suffix, empty string if no suffix is defined.
	 */
	public String getFileSuffix() {
		return FileSuffix;
	}

	/**
	 * Returns selected {@link RDFFormatType} for extracted RDF data.
	 *
	 * @return selected {@link RDFFormatType} for extracted RDF data.
	 */
	public RDFFormatType getRDFFormatValue() {
		return RDFFormatValue;
	}

	/**
	 * Returns one of possibilities defined in {@link FileExtractType} how to
	 * data extract.
	 *
	 * @return one of possibilities defined in {@link FileExtractType} how to
	 *         data extract.
	 */
	public FileExtractType getFileExtractType() {
		return fileExtractType;
	}

	/**
	 * Returns true, if only files with defined suffix are used for data
	 * extraction, false otherwise.
	 *
	 * @return true, if only files with defined suffix are used for data
	 *         extraction, false otherwise.
	 */
	public boolean useOnlyThisSuffix() {
		return OnlyThisSuffix;
	}

	/**
	 * Returns true, if is used statistical handler for data extraction, false
	 * otherwise.
	 *
	 * @return true, if is used statistical handler for data extraction, false
	 *         otherwise.
	 */
	public boolean isUsedStatisticalHandler() {
		return UseStatisticalHandler;
	}

	/**
	 * Returns true, if execution should fail when some errors are detected,
	 * false otherwise.
	 *
	 * @return true, if execution should fail when some errors are detected,
	 *         false otherwise.
	 */
	public boolean isFailWhenErrors() {
		return failWhenErrors;
	}

	/**
	 * Returns true, if DPU configuration is valid, false otherwise.
	 *
	 * @return true, if DPU configuration is valid, false otherwise.
	 */
	@Override
	public boolean isValid() {
		return Path != null
				&& FileSuffix != null
				&& RDFFormatValue != null
				&& fileExtractType != null;
	}
}

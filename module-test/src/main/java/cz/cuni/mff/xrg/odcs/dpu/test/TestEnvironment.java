package cz.cuni.mff.xrg.odcs.dpu.test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.annotation.AnnotationContainer;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.annotation.AnnotationGetter;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPU;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.InputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnit;
import cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnitFactory;
import cz.cuni.mff.xrg.odcs.dataunit.file.ManageableFileDataUnit;
import cz.cuni.mff.xrg.odcs.dataunit.file.handlers.DirectoryHandler;
import cz.cuni.mff.xrg.odcs.dataunit.file.options.OptionsAdd;
import cz.cuni.mff.xrg.odcs.dpu.test.context.TestContext;
import cz.cuni.mff.xrg.odcs.dpu.test.data.DataUnitFactory;
import cz.cuni.mff.xrg.odcs.dpu.test.data.VirtuosoConfig;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.ManagableRdfDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;

/**
 * Hold environment used to test DPU.
 *
 * @author Petyr
 *
 */
public class TestEnvironment {

	private static final Logger LOG = LoggerFactory.getLogger(
			TestEnvironment.class);

	/**
	 * Configuration used to access virtuoso. If tests use Virtuoso then this
	 * must be set before first test. This value is shared by multiple tests.
	 */
	public static VirtuosoConfig virtuosoConfig = new VirtuosoConfig();

	/**
	 * Context used for testing.
	 */
	private final TestContext context;

	/**
	 * Working directory.
	 */
	private final File workingDirectory;

	/**
	 * Used {@link ManagableDataUnit}s
	 */
	private final LinkedList<ManagableDataUnit> dataUnits = new LinkedList<>();

	/**
	 * Directories for input {@link ManagableDataUnit}s.
	 */
	private final HashMap<String, ManagableDataUnit> inputDataUnits = new HashMap<>();

	/**
	 * Directories for output {@link ManagableDataUnit}s.
	 */
	private final HashMap<String, ManagableDataUnit> outputDataUnits = new HashMap<>();

	/**
	 * Factory for {@link DataUnit}s classes.
	 */
	private final DataUnitFactory dataUnitFactory;

	private TestEnvironment(File workingDirectory) {
		this.context = new TestContext();
		context.setWorkingDirectory(workingDirectory);

		this.workingDirectory = workingDirectory;
		this.dataUnitFactory = new DataUnitFactory(workingDirectory);
	}

	/**
	 * Create test environment. As working directory is used temp file.
	 *
	 * @return Test environment.
	 */
	public static TestEnvironment create() {
		// we use tmp path and time to create tmp directory
		return create(FileUtils.getTempDirectory());
	}

	/**
	 * Create test environment.
	 *
	 * @param directory Working directory.
	 * @return Test environment.
	 */
	public static TestEnvironment create(File directory) {
		final String testDirName = "odcs_test_"
				+ Long.toString((new Date()).getTime());

		return new TestEnvironment(new File(directory, testDirName));
	}

	// - - - - - - - - - methods for environment setup - - - - - - - - - //
	/**
	 * Set path that is used like jar-path during execution. This value will 
	 * be used during test execution if DPU asks for it.
	 *
	 * @param jarPath path to the jar file.
	 */
	public void setJarPath(String jarPath) {
		context.setJarPath(jarPath);
	}

	/**
	 * Set time for last execution. This value will  be used during test 
	 * execution if DPU asks for it.
	 *
	 * @param lastExecution Time of last execution.
	 */
	public void setLastExecution(Date lastExecution) {
		context.setLastExecution(lastExecution);
	}

	/**
	 * This value will be used during test execution if DPU asks for it.
	 * 
	 * @param workingDirectory the workingDirectory to set, use null to use
	 *                         subdirectory in {@link #workingDirectory}
	 */
	public void setWorkingDirectory(File workingDirectory) {
		context.setWorkingDirectory(workingDirectory);
	}

	/**
	 * This value will be used during test execution if DPU asks for it.
	 * 
	 * @param resultDirectory the resultDirectory to set, use null to use
	 *                        subdirectory in {@link #workingDirectory}
	 */
	public void setResultDirectory(File resultDirectory) {
		context.setResultDirectory(resultDirectory);
	}

	/**
	 * This value will be used during test execution if DPU asks for it.
	 * 
	 * @param globalDirectory the globalDirectory to set, use null to use
	 *                        subdirectory in {@link #workingDirectory}
	 */
	public void setGlobalDirectory(File globalDirectory) {
		context.setGlobalDirectory(globalDirectory);
	}

	/**
	 * This value will be used during test execution if DPU asks for it.
	 * 
	 * @param userDirectory the userDirectory to set, use null to use
	 *                      subdirectory in {@link #workingDirectory}
	 */
	public void setUserDirectory(File userDirectory) {
		context.setUserDirectory(userDirectory);
	}

	/**
	 * Set given {@link ManagableDataUnit} as an input. If there already is
	 * another value for given name it is overridden. The old
	 * {@link ManagableDataUnit} is not released.
	 *
	 * @param name     Name of dataUnit.
	 * @param dataUnit DataUnit to add as input.
	 */
	public void addInput(String name, ManagableDataUnit dataUnit) {
		inputDataUnits.put(name, dataUnit);
	}

	/**
	 * Set {@link ManagableDataUnit} where should the data, from output
	 * DataUnit, be stored. If there is other setting for given name then it is
	 * overwritten.
	 *
	 * The data in given {@link ManagableDataUnit} may not be accessible after
	 * call of {@link #release()}.
	 *
	 * If there already is another value for given name it is overridden. The
	 * old {@link ManagableDataUnit} is not released.
	 *
	 * @param name     Name of dataUnit.
	 * @param dataUnit DataUnit to add as output.
	 */
	public void addOutput(String name, ManagableDataUnit dataUnit) {
		outputDataUnits.put(name, dataUnit);
	}

	/**
	 * Create input {@link RDFDataUnit} that is used in test environment.
	 *
	 * @param name        Name of DataUnit.
	 * @param useVirtuoso If true then Virtuoso is used as a storage.
	 * @return Created input {@link RDFDataUnit}.
	 * @throws cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException
	 */
	public RDFDataUnit createRdfInput(String name, boolean useVirtuoso)
			throws RDFException {
		ManagableRdfDataUnit rdf = dataUnitFactory.createRDFDataUnit(name,
				useVirtuoso);
		addInput(name, rdf);
		return rdf;
	}

	/**
	 * Create input {@link RDFDataUnit} and populate it with data from given
	 * file. Created {@link RDFDataUnit} is used in test environment.
	 *
	 * The data are loaded from file in test\resources.
	 *
	 * @param name         Name of DataUnit.
	 * @param useVirtuoso  If true then Virtuoso is used as a storage.
	 * @param resourceName Name of resource file. The path to the resource file
	 *                     should be relative with respect to src/test/resources
	 *                     folder
	 * @param format       Format of input file.
	 * @return Created input {@link RDFDataUnit}.
	 * @throws cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException
	 */
	public RDFDataUnit createRdfInputFromResource(String name,
			boolean useVirtuoso,
			String resourceName,
			RDFFormat format) throws RDFException {
		ManagableRdfDataUnit rdf = dataUnitFactory.createRDFDataUnit(name,
				useVirtuoso);
		// construct path to the resource
		URL url = Thread.currentThread().getContextClassLoader()
				.getResource(resourceName);
		// check ..
		if (url == null) {
			throw new RDFException("Missing input file in resource for: "
					+ resourceName);
		}
		File inputFile = new File(url.getPath());
		// return file
		rdf.addFromFile(inputFile, format);
		addInput(name, rdf);
		return rdf;
	}

	/**
	 * Create output {@link RDFDataUnit}, add it to the test environment and
	 * return it.
	 *
	 * @param name        Name of DataUnit.
	 * @param useVirtuoso If true then Virtuoso is used as a storage.
	 * @return Created output {@link RDFDataUnit}.
	 * @throws cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException
	 */
	public RDFDataUnit createRdfOutput(String name, boolean useVirtuoso)
			throws RDFException {
		ManagableRdfDataUnit rdf = dataUnitFactory.createRDFDataUnit(name,
				useVirtuoso);
		addOutput(name, rdf);
		return rdf;
	}

	/**
	 * Create file data unit, add it as an input and return reference to it.
	 *
	 * @param name Name of DataUnit.
	 * @param dir  Root folder, where data unit can store data. Should be empty.
	 * @return Created input {@link FileDataUnit}.
	 */
	public FileDataUnit createFileInput(String name, File dir) {
		ManageableFileDataUnit file = FileDataUnitFactory.create(name, dir);
		addInput(name, file);
		return file;
	}

	/**
	 * Create file data unit, add it as an input and return reference to it. The
	 * file data unit is created in temp directory and data from given resource
	 * path are added to the root.
	 *
	 * @param name Name of DataUnit.
	 * @param resourceName Path to the resources.
	 * @return Created input {@link FileDataUnit}.
	 * @throws cz.cuni.mff.xrg.odcs.commons.data.DataUnitException
	 */
	public FileDataUnit createFileInputFromResource(String name,
			String resourceName)
			throws DataUnitException {
		File dir = new File(FileUtils.getTempDirectory(),
				"odcs-file-test-" + Long.toString(System.nanoTime()));
		dir.mkdirs();

		ManageableFileDataUnit file = FileDataUnitFactory.create(name, dir);
		// add from resources
		URL url = Thread.currentThread().getContextClassLoader()
				.getResource(resourceName);

		// check ..
		if (url == null) {
			throw new RDFException("Missing input file in resource for: "
					+ resourceName);
		}

		DirectoryHandler dh = file.getRootDir();
		File resourceRoot = new File(url.getPath());

		//if the resource is a directory: 
		if (resourceRoot.isDirectory()) {
			for (File toAdd : resourceRoot.listFiles()) {
				dh.addExistingDirectory(toAdd, new OptionsAdd(true));
			}
		} else {
			//add single resource (file)
			dh.addExistingFile(resourceRoot, new OptionsAdd(true));
		}

		addInput(name, file);
		return file;
	}

	/**
	 * Create file data unit, add it as an input and return reference to it.
	 *
	 * @param name Name of DataUnit.
	 * @param dir  Root folder, where data unit can store data. Should be empty.
	 * @return Created output {@link FileDataUnit}.
	 */
	public FileDataUnit createFileOutput(String name, File dir) {
		ManageableFileDataUnit file = FileDataUnitFactory.create(name, dir);
		addOutput(name, file);
		return file;
	}

	/**
	 * Create file data unit, add it as an input and return reference to it. As
	 * a directory use temp director.
	 *
	 * @param name Name of DataUnit.
	 * @return Created output {@link FileDataUnit}.
	 * @throws java.io.IOException
	 */
	public FileDataUnit createFileOutput(String name) throws IOException {
		File dir = new File(FileUtils.getTempDirectory(),
				"odcs-file-test-" + Long.toString(System.nanoTime()));
		dir.mkdirs();

		ManageableFileDataUnit file = FileDataUnitFactory.create(name, dir);
		addOutput(name, file);
		return file;
	}

	// - - - - - - - - - method for test execution - - - - - - - - - //
	/**
	 * Run given DPU in the test environment. The test environment is not reset
	 * before or after the test. If the test working directory should be deleted
	 * then is deleted at the end of this method same as all the
	 * {@link DataUnit}s
	 *
	 * Any thrown exception is passed. In every case the {@link #release()}
	 * method must be called in order to release test data.
	 *
	 * @param dpuInstance Instance of DPU to run.
	 * @return False if the execution failed by sending error message
	 * @throws java.lang.Exception
	 */
	public boolean run(DPU dpuInstance) throws Exception {
		// prepare dpu instance - set annotations
		connectDataUnits(dpuInstance);

		// execute
		dpuInstance.execute(context);

		return context.isPublishedError();
	}

	/**
	 * Delete testing data and release {@link ManagableDataUnit}s. Unused
	 * {@link ManagableDataUnit} are not deleted.
	 */
	public void release() {
		// release all DataUnits ..
		for (ManagableDataUnit item : dataUnits) {
			if (item != null) {
				item.delete();
			}
		}
		dataUnits.clear();
		// wait for some time .. so DataUnits can release their contexts
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		// delete working directory ..
		try {
			org.apache.commons.io.FileUtils.deleteDirectory(workingDirectory);
		} catch (IOException e) {
			LOG.error("Failed to delete working directory.", e);
		}
	}

	// - - - - - - - - - methods for examining the results - - - - - - - - - //
	/**
	 * Return context used during tests. Return null before call of
	 * {@link #run(cz.cuni.mff.xrg.odcs.commons.dpu.DPU)} method.
	 *
	 * @return Context used during testing.
	 */
	public TestContext getContext() {
		return context;
	}

	// - - - - - - - - - - - - methods for dpu setup - - - - - - - - - - - - //
	private ManagableDataUnit getInputDataUnit(Field field, String name) {
		if (inputDataUnits.containsKey(name)) {
			// check type
			Class<?> fc = field.getType();
			Class<?> rc = inputDataUnits.get(name).getClass();

			if (fc.isAssignableFrom(rc)) {
				// class match
				return inputDataUnits.get(name);
			} else {
				// miss match ..
				return null;
			}
		}
		return null;
	}

	private ManagableDataUnit getOutputDataUnit(Field field, String name) {
		if (outputDataUnits.containsKey(name)) {
			// check type
			Class<?> fc = field.getType();
			Class<?> rc = outputDataUnits.get(name).getClass();

			if (fc.isAssignableFrom(rc)) {
				// class match
				return outputDataUnits.get(name);
			} else {
				// miss match ..
				return null;
			}
		}
		return null;
	}

	/**
	 * Connect data units from {@link #inputDataUnits} and {@link #outputDataUnits}
	 * to the given DPU instance.
	 * 
	 * @param dpuInstance DPU instance object.
	 * @throws Exception 
	 */
	private void connectDataUnits(DPU dpuInstance) throws Exception {
		// add inputs
		List<AnnotationContainer<InputDataUnit>> inputAnnotations = AnnotationGetter
				.getAnnotations(dpuInstance, InputDataUnit.class);
		for (AnnotationContainer<InputDataUnit> item : inputAnnotations) {
			ManagableDataUnit dataUnit = getInputDataUnit(item.getField(),
					item.annotation.name());
			if (dataUnit == null && !item.getAnnotation().optional()) {
				// missing non option dataUnit
				throw new Exception(
						"Test failure missing import mandatory DataUnit: "
						+ item.getAnnotation().name());
			}

			item.getField().set(dpuInstance, dataUnit);
			// ...
			dataUnits.add(dataUnit);
		}

		// add outputs
		List<AnnotationContainer<OutputDataUnit>> outputAnnotations = AnnotationGetter
				.getAnnotations(dpuInstance, OutputDataUnit.class);
		for (AnnotationContainer<OutputDataUnit> item : outputAnnotations) {
			ManagableDataUnit dataUnit = getOutputDataUnit(item.getField(),
					item.annotation.name());
			item.getField().set(dpuInstance, dataUnit);
			if (dataUnit == null) {
				throw new Exception("Can not bind 'null' to output DataUnit: "
						+ item.getAnnotation().name());
			}
			// ...
			dataUnits.add(dataUnit);
		}
	}

}

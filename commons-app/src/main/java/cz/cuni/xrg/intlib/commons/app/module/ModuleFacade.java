package cz.cuni.xrg.intlib.commons.app.module;

import java.io.File;

import cz.cuni.xrg.intlib.commons.DPUExecutive;
import cz.cuni.xrg.intlib.commons.loader.Load;
import cz.cuni.xrg.intlib.commons.extractor.Extract;
import cz.cuni.xrg.intlib.commons.transformer.Transform;
import cz.cuni.xrg.intlib.commons.app.AppConfiguration;
import cz.cuni.xrg.intlib.commons.app.module.osgi.*;

/**
 * Facade providing actions with DPU module implementations.
 * 
 * @author Petyr
 *
 */
public class ModuleFacade {
	
	/**
	 * Used framework.
	 */
	private Framework framework;
	
	/**
	 * Application configuration.
	 */
	private AppConfiguration appConfiguration;
	
	/**
	 * Base ctor.
	 * @param appConfiguration application configuration
	 */
	public ModuleFacade(AppConfiguration appConfiguration) {
		this.framework = new Framework();
		this.appConfiguration = appConfiguration;
	}
	
	/**
	 * Start framework. Must be called as a first method after ctor.
	 * @param exportedPackages names of additional packages to export started and separated by comma
	 */
	public void start(String exportedPackages) throws ModuleException {
		// start
		this.framework.start(exportedPackages);
	}
	
	/**
	 * Stop framework. Should be called as last method after 
	 * releasing all used instances from ModuleFacade.
	 */
	public void stop() {
		this.framework.uninstallBundles();
		this.framework.stop();		
	}
	
	/**
	 * Try to load DPUExecutive from given path.
	 * @param path path to bundle (jar file) relative to dpu's directory
	 * @return loaded module
	 * @throws ModuleException
	 */
	public DPUExecutive getInstance(String path) throws ModuleException {
		return this.framework.loadDPU(appConfiguration.getDpuDirectory() + path);
	}
	
	/**
	 * Try to load DPUExecutive from given uri.
	 * @param uri uri to module file
	 * @return loaded loader
	 * @throws ModuleException
	 */
	public Load getInstanceLoader(String uri) throws ModuleException {
		return (Load) getInstance(uri);
	}
	
	/**
	 * Try to load DPUExecutive from given uri.
	 * @param uri uri to module file
	 * @return loaded extractor
	 * @throws ModuleException
	 */
	public Extract getInstanceExtract(String uri) throws ModuleException {
		return (Extract) getInstance(uri);
	}
	
	/**
	 * Try to load DPUExecutive from given uri.
	 * @param uri uri to module file
	 * @return loaded transformer
	 * @throws ModuleException
	 */
	public Transform getInstanceTransform(String uri) throws ModuleException {
		return (Transform) getInstance(uri);
	}
	
	/**
	 * List files in single directory (non-recursive). If the
	 * file is *.jar then load id as a bundle.
	 * @param directoryPath system path to directory. Not url.
	 */
	public void installDirectory(String directoryPath) {
		String message = "";
// TODO: Propagate exceptions to application ..		
		File directory = new File( directoryPath );
		File[] fList = directory.listFiles();
		for (File file : fList){
			if (file.isFile()){
				if (file.getName().contains("jar")) {
					// load as bundle
					// install bundle
					String path = "file:///" + file.getAbsolutePath().replace('\\', '/');
					message += "loading: " + path + "\n";
					try {
						framework.installBundle( path );
					} catch (OSGiException e) {
						message += e.getMessage() + " > " + e.getOriginal().getMessage() + "\n";
					} catch(Exception e) {
						message += "Exception: " + e.getMessage() + "\n";
					}							
				}
				
			}
		}
	}
	
	/**
	 * Return framework that is used to work with bundles.
	 * @return
	 */
	@Deprecated
	public cz.cuni.xrg.intlib.commons.app.module.osgi.Framework HACK_getFramework() {
		return this.framework;
	}
	
}

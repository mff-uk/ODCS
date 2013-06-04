package cz.cuni.xrg.intlib.commons.app.module;

import java.util.HashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;


/**
 * Represent a single bundle with some additional information.
 * 
 * @author Petyr
 *
 */
class BundleContainer {

	/**
	 * The OSGI bundle it self.
	 */
	private Bundle bundle;
	
	/**
	 * Bundle container's uri. From where
	 * the bundle has been loaded.
	 */
	private String uri;
	
	/**
	 * List of loaded class<?> from this bundle.
	 */
	private java.util.Map<String, Class<?>> loadedClassCtors;
		
	public BundleContainer(Bundle bundle, String uri) {
		this.bundle = bundle;
		this.uri = uri;
		this.loadedClassCtors = new HashMap<>();
		
	}
	
	/**
	 * Load class with given name from the bundle.
	 * @param className Class name prefixed with packages.
	 * @return Loaded class.
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public Object loadClass(String className) 
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> loaderClass = null;
		if (loadedClassCtors.containsKey(className)) {
			// class already been loaded 
			loaderClass = this.loadedClassCtors.get(className);
		} else {
			// try to load class -> throw ClassNotFoundException
            loaderClass = bundle.loadClass(className);
            // store loaded class
            this.loadedClassCtors.put(className, loaderClass);
		}
		// we have loader, create instance ..
		return loaderClass.newInstance(); // InstantiationException, IllegalAccessException
	}

	/**
	 * Uninstall bundle.
	 * @throws BundleException 
	 */
	public void uninstall() throws BundleException {
		// clear list
		loadedClassCtors.clear();
		// 
		bundle.uninstall();
		bundle = null;
	}

	public Bundle getBundle() {
		return bundle;
	}
	
	public String getUri() {
		return uri;
	}
}

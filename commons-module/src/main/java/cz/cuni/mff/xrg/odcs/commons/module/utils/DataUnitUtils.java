package cz.cuni.mff.xrg.odcs.commons.module.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.slf4j.LoggerFactory;

/**
 * Utility class with common functionality usable by data units
 * @author tomasknap
 */
public class DataUnitUtils {

	private static final org.slf4j.Logger log = LoggerFactory.getLogger(
			DataUnitUtils.class);

//        public static void printBytes(byte[] array, String name) {
//            for (int k = 0; k < array.length; k++) {
//                System.out.println(name + "[" + k + "]:" + array[k]);
//            }
//        }
//        
        /**
         * Stores string to a file
         * @param s String being stored
         * @param fileName Name of the file
         * @return Reference to the created file
         */
	public static File storeStringToTempFile(String s, String fileName) {
		return storeStringToTempFile(s, fileName, StandardCharsets.UTF_8);
	}

	/**
         * Stores string to a file
         * 
         * @param s String being stored
         * @param filePath  The path to the file
         * @param charset Encoding
         * @return Reference to the created file
         */
	public static File storeStringToTempFile(String s, String filePath, Charset charset) {

		if (s == null || s.isEmpty()) {
			log.warn("Nothing to be stored to a file");
			return null;
		}

		if (filePath == null || filePath.isEmpty()) {
			log.error("File name is missing");
			return null;
		}

        //log.debug("File content is: {}", s);
		//prepare temp file where the a is stored
		File configFile = new File(filePath);

		if (configFile == null) {
			log.error("Created file is null or empty, although the original string was non-empty .");
			return null;
		}

		try {
			log.debug("File path {}", configFile.getCanonicalPath());
		} catch (IOException ex) {
			log.error(ex.getLocalizedMessage());
		}

            
                BufferedWriter writer = null;
		try {
                    
                    writer = Files.newBufferedWriter(configFile.toPath(), charset);
                    writer.write(s, 0, s.length());
                    writer.close();
		} catch (IOException x) {
			log.error("IOException: %s%n", x);
		} finally {
                    
                }

		return configFile;

	}

        /**
         * Reads content of a file to string
         * @param path Path to a file
         * @return Content of a file
         */
	public static String readFile(String path) {
		return readFile(path, StandardCharsets.UTF_8);
	}

        /**
         * Reads content of a file to string
         * @param path Path to a file
         * @param encoding Encoding 
         * @return Content of a file
         */
	public static String readFile(String path, Charset encoding) {
		byte[] encoded;
		try {
			encoded = Files.readAllBytes(Paths.get(path));
		} catch (IOException ex) {
			log.warn("Cannot read the file {}", Paths.get(path));
			log.debug(ex.getLocalizedMessage());
			return null;
		}
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}

        /**
         * Checks existence of a directory
         * @param file Path to the directory
         */
	public static void checkExistanceOfDir(String file) {
		if (new File(file).mkdirs()) {
			log.debug("Dir {} created", file);
		} else {
			log.debug("Dir {} NOT created, could have already exist", file);
		}
	}

//        /**
//         * Encodes string with 
//         * @param literalValue
//         * @param escapedMappings
//         * @return 
//         */
//	public static String encode(String literalValue, String escapedMappings) {
//		String val = literalValue;
//		String[] split = escapedMappings.split("\\s*");
//		for (String s : split) {
//			String[] keyAndVal = s.split(":");
//			if (keyAndVal.length == 2) {
//				val = val.replaceAll(keyAndVal[0], keyAndVal[1]);
//
//			} else {
//				log.warn("Wrong format of escaped character mappings, skipping the mapping");
//
//			}
//		}
//		return val;
//	}
       
}

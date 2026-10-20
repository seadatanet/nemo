package fr.ifremer.nemo;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.IPath;

import fr.ifremer.nemo.config.ManageLists;
import fr.ifremer.nemo.config.NemoExternalResourcesManager;

/**
 * Nemo Resource Updater.
 * 
 * Used in build process, to update the external resources.
 * 
 * @author Sébastien Crouzille
 *
 */
public class ResourceUpdater {

	private final Logger myLogger = LogManager.getLogger("nemoJunits"); //$NON-NLS-1$

	// Directories
	private final String DEFAULT_RESOURCES_PATH_STRING = "../fr.ifremer.nemo/resources"; //$NON-NLS-1$
	private final String EXT_RESOURCES_DIR = "externalResources"; //$NON-NLS-1$
	private final String BODC_DIR = "BODCVocabularies"; //$NON-NLS-1$
	private final String CSR_DIR = "CSR"; //$NON-NLS-1$

	// Resource path String for runtime.
	private final String resourcesPathString;

	/**
	 * True if the base resources directory has to be retrieved from source
	 * (needed in test phase)
	 */
	private final Boolean resetConfiguration;

	public ResourceUpdater() {

		this.resourcesPathString = DEFAULT_RESOURCES_PATH_STRING;

		resetConfiguration = false;
	}

	public ResourceUpdater(String resourcesPathString) {

		this.resourcesPathString = resourcesPathString.trim();

		resetConfiguration = true;
	}

	/**
	 * Process resources update.
	 */
	public void updateResources() {

		printClassPath();

		myLogger.info("RESOURCES REINIT: START"); //$NON-NLS-1$

		// delete and copy resources directory from nemo project
		// only if not in nemo project!
		if (resetConfiguration) {

			try {
				myLogger.info("RESOURCES REINIT: delete directory"); //$NON-NLS-1$
				deleteDirectoryRecursion(Paths.get(resourcesPathString));
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				myLogger.info("RESOURCES REINIT: copy from Nemo project"); //$NON-NLS-1$
				FileUtils.copyDirectory(new File(DEFAULT_RESOURCES_PATH_STRING), new File(resourcesPathString));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		myLogger.info("RESOURCES REINIT: remove CSR and BODC vocabs"); //$NON-NLS-1$
		try {
			Path bodcDir = Paths.get(resourcesPathString, EXT_RESOURCES_DIR, BODC_DIR);
			deleteDirectoryRecursion(bodcDir);
			Files.createDirectories(bodcDir);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Path csrDir = Paths.get(resourcesPathString, EXT_RESOURCES_DIR, CSR_DIR);
			deleteDirectoryRecursion(csrDir);
			Files.createDirectories(csrDir);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Nemo.getInstance().setLogger(myLogger);

		myLogger.info("RESOURCES REINIT: update from the web"); //$NON-NLS-1$

		NemoExternalResourcesManager.init();
		ManageLists.initLists();

		try {
			Nemo.getInstance().callListsUpdates();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		myLogger.info("RESOURCES REINIT: ENDED"); //$NON-NLS-1$
	}

	/**
	 * Display the class path.
	 */
	private static void printClassPath() {
		String cp = System.getProperty("java.class.path"); //$NON-NLS-1$
		for (String s : cp.split(Character.toString(IPath.DEVICE_SEPARATOR))) {
			System.out.println(s);
		}
	}

	/**
	 * delete a directory and its contents.
	 * 
	 * @param path
	 * @throws IOException
	 */
	private void deleteDirectoryRecursion(Path path) throws IOException {
		if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
			try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
				for (Path entry : entries) {
					deleteDirectoryRecursion(entry);
				}
			}
		}
		Files.delete(path);
	}

	/**
	 * Main method, to perform resources update during prepare-package phase
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		ResourceUpdater updater = new ResourceUpdater();

		updater.updateResources();
	}
}

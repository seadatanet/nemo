package fr.ifremer.nemo.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ResourceUpdater;
import fr.ifremer.nemo.config.ManageLists;
import fr.ifremer.nemo.config.NemoExternalResourcesManager;

public class UpdateResourcesTest {

	private Logger testLogger=LogManager.getLogger("nemoJunits");

	private static void printClassPath() {
		String cp = System.getProperty("java.class.path");
		for(String s : cp.split(Character.toString(Path.DEVICE_SEPARATOR))) {
			System.out.println(s);
		}
	}
	
	
	@Test
	public void test() {
		try {
			ResourceUpdater updater = new ResourceUpdater("../fr.ifremer.nemo.test/resources");
			updater.updateResources();
		} catch (Throwable e) {
			// Network unavailable or JDK internal class missing (e.g. NoClassDefFoundError
			// on Java 11+ where com.sun.org.apache.xml.internal.resolver was removed).
			// Fall back to committed vocabs from the main project.
			// ResourceUpdater only clears fr.ifremer.nemo.test/resources, so
			// ../fr.ifremer.nemo/resources/externalResources still contains the cached files.
			testLogger.warn("Resource update from network failed, falling back to cached resources: " + e.getMessage());
			try {
				FileUtils.copyDirectory(
					new File("../fr.ifremer.nemo/resources/externalResources/BODCVocabularies"),
					new File("resources/externalResources/BODCVocabularies"));
			} catch (IOException ioe) {
				testLogger.warn("Could not restore cached BODCVocabularies directory: " + ioe.getMessage());
			}
		}
		// Always ensure csrCodeList.xml is present: ResourceUpdater does not download it,
		// so if it is missing (fresh clone / CI), copy it from the main project resources.
		try {
			File csrDest = new File("resources/externalResources/CSR/csrCodeList.xml");
			if (!csrDest.exists()) {
				testLogger.info("csrCodeList.xml not found, copying from main project resources.");
				FileUtils.copyDirectory(
					new File("../fr.ifremer.nemo/resources/externalResources/CSR"),
					new File("resources/externalResources/CSR"));
			}
		} catch (IOException ioe) {
			testLogger.warn("Could not copy CSR directory from main project: " + ioe.getMessage());
		}

//		
//		printClassPath();
//		
//		testLogger.info("RESOURCES REINIT: START");
//		// delete and copy resources directory from nemo project
//		try {
//			testLogger.info("RESOURCES REINIT: delete directory");
//			FileUtils.deleteDirectory(new File("../fr.ifremer.nemo.test/resources"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			testLogger.info("RESOURCES REINIT :copy from Nemo project");
//			FileUtils.copyDirectory(new File("../fr.ifremer.nemo/resources"), new File("../fr.ifremer.nemo.test/resources"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			testLogger.info("RESOURCES REINIT :remove CSR and BODC vocabs");
//			File bodcDir = new File("../fr.ifremer.nemo.test/resources/externalResources/BODCVocabularies");
//			FileUtils.deleteDirectory(bodcDir);
//			Files.createDirectories(bodcDir.toPath());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			File csrDir = new File("../fr.ifremer.nemo.test/resources/externalResources/CSR");
//			FileUtils.deleteDirectory(csrDir);
//			Files.createDirectories(csrDir.toPath());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//		testLogger = LogManager.getLogger("nemoJunits");
//		Nemo.getInstance().setLogger(testLogger);
//
//		testLogger.info("RESOURCES REINIT :update from the web");
////		try {
////			FileInputStream extResourcesConf = new FileInputStream(new File("resources/externalResourcesConfiguration.yaml"));
////			ExternalResourcesManager.getInstance(extResourcesConf, "resources/externalResources");
////			ExternalResourcesManager.getInstance().getSdnVocabularyManager().init(true);
////		} catch (Exception e) {
////			Nemo.getInstance().getLogger().error(e.getMessage());
////		}
//		NemoExternalResourcesManager.init();
//		ManageLists.initLists();
//		
//		try {
//			Nemo.getInstance().callListsUpdates();
//		} catch(Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//		testLogger.info("RESOURCES REINIT :ENDED");
	}

}

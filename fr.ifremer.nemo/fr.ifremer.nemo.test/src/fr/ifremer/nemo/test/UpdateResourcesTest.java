package fr.ifremer.nemo.test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

import fr.ifremer.nemo.Nemo;
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
		
		printClassPath();
		
		testLogger.info("RESOURCES REINIT :START");
		// delete and copy resources directory from nemo project
		try {
			testLogger.info("RESOURCES REINIT :delete directory");
			FileUtils.deleteDirectory(new File("../fr.ifremer.nemo.test/resources"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			testLogger.info("RESOURCES REINIT :copy from Nemo project");
			FileUtils.copyDirectory(new File("../fr.ifremer.nemo/resources"), new File("../fr.ifremer.nemo.test/resources"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			testLogger.info("RESOURCES REINIT :remove CSR and BODC vocabs");
			FileUtils.deleteDirectory(new File("../fr.ifremer.nemo.test/resources/Vocab"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			FileUtils.deleteDirectory(new File("../fr.ifremer.nemo.test/resources/CSR"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		testLogger = LogManager.getLogger("nemoJunits");
		Nemo.getInstance().setLogger(testLogger);

		testLogger.info("RESOURCES REINIT :update from the web");
//		try {
//			FileInputStream extResourcesConf = new FileInputStream(new File("resources/externalResourcesConfiguration.yaml"));
//			ExternalResourcesManager.getInstance(extResourcesConf, "resources/externalResources");
//			ExternalResourcesManager.getInstance().getSdnVocabularyManager().init(true);
//		} catch (Exception e) {
//			Nemo.getInstance().getLogger().error(e.getMessage());
//		}
		NemoExternalResourcesManager.init();
		ManageLists.initLists();
		
		try {
			Nemo.getInstance().callListsUpdates();
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		testLogger.info("RESOURCES REINIT :ENDED");
	}

}

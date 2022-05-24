package fr.ifremer.nemo.test;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.Configuration;
import fr.ifremer.nemo.config.Proxy;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.E_TypeConversion;
import fr.ifremer.nemo.util.Intern;

public class MiscTest  extends AbstractTest{
	private static Logger testLogger=LogManager.getLogger("nemoJunits");
	/**
	 * ================================================================= invalidated model =================================================================
	 */
	@Test
	public final void inValidated_model() {
		testLogger = LogManager.getLogger("nemoJunits");
		Nemo.getInstance().setLogger(testLogger);
		testLogger.info("\r\n\r\n\r\n\r\n_______________________________unValidated_model________________________________\r\n\r\n\r\n\r\n");
		File root = new File(dataRootPath);
		String inputAbsolutePath = root.getAbsolutePath() + File.separator + "_9656_collectionGroupByCruise_withSeparator_unvalidated_model" + File.separator + inputName;
		String outputAbsolutePath = root.getAbsolutePath() + File.separator + "_9656_collectionGroupByCruise_withSeparator_unvalidated_model" + File.separator + "P091" + File.separator + "ODV_MULTISTATIONS" + File.separator;
		Message message = new Message(testLogger);
		message = convertWithNemoBatch(message, Conversion.ODV, E_TypeConversion.ODV_MULTISTATIONS, inputAbsolutePath, outputAbsolutePath, true, false);
		Assert.assertFalse(message.batchCompRes);
	}
	/**
	 * ================================================================= emptyProxy =================================================================
	 */
	@Test
	public final void checkEmptyProxyTest() {
		testLogger.info("\r\n\r\n\r\n\r\n_______________________________emptyProxyTest________________________________\r\n\r\n\r\n\r\n");
		message = new Message(testLogger);
		message.testName = "empty Proxy test";

		Configuration config = new Configuration();
		Assert.assertTrue(checkEmptyProxy(config));
	}

	@Test
	public final void checkEmptyProxyTestInit() {
		testLogger.info("\r\n\r\n\r\n\r\n_______________________________emptyProxyTest________________________________\r\n\r\n\r\n\r\n");
		message = new Message(testLogger);
		message.testName = "empty Proxy test";

		Configuration config = new Configuration(true);
		Assert.assertTrue(checkEmptyProxy(config));
	}
	public boolean checkEmptyProxy(Configuration config) {

		Proxy proxy = config.getProxy();
		if (!proxy.isUse() && proxy.getAdress().isEmpty() && proxy.getLogin().isEmpty() && proxy.getPassword().isEmpty() && proxy.getPort().isEmpty()) {
			config.restoreDefault();
			proxy = config.getProxy();
			if (!proxy.isUse() && proxy.getAdress().isEmpty() && proxy.getLogin().isEmpty() && proxy.getPassword().isEmpty() && proxy.getPort().isEmpty())
				return true;
		}

		return false;
	}
	
	

	@Test
	public final void checkDefaultLanguageTest() {
		testLogger.info("\r\n\r\n\r\n\r\n_______________________________emptyProxyTest________________________________\r\n\r\n\r\n\r\n");
		message = new Message(testLogger);
		message.testName = "empty Proxy test";
		Configuration config = new Configuration(true);
		String lang = config.getLang();
		Assert.assertTrue(lang.equals("en_GB"));
	}

	@Test
	public final void checkDefaultLanguageInitTest() {
		testLogger.info("\r\n\r\n\r\n\r\n_______________________________emptyProxyTest________________________________\r\n\r\n\r\n\r\n");
		message = new Message(testLogger);
		message.testName = "empty Proxy test";
		Configuration config = new Configuration(true);
		String lang = config.getLang();
		Assert.assertTrue(lang.equals("en_GB"));
	}
	
	
	/**
	 * ================================================================= userParemetersLabels Empty =================================================================
	 */
	@Test
	public final void userParemetersLabelsEmpty() {
		boolean res = false;
		testLogger.info("\r\n\r\n\r\n\r\n__________________________userParemetersLabelsEmpty___________________________\r\n\r\n\r\n\r\n");
		message = new Message(testLogger);
		message.testName = " Empty userParemetersLabels";
		org.w3c.dom.Document document;
		String userParametersLabels = Intern.dir_resources + File.separator + "userParametersLabels" + Global.EXT_XML;
		// création d'une fabrique de documents
		DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
		// création d'un constructeur de documents
		DocumentBuilder constructeur;
		try {
			constructeur = fabrique.newDocumentBuilder();
			document = constructeur.parse(userParametersLabels);
			res = document.getFirstChild().getTextContent().trim().isEmpty();

		} catch (Exception e) {
			res = false;
		}

		Assert.assertTrue(res);
	}

	/**
	 * ================================================================= userParemetersLongNames Empty =================================================================
	 */
	@Test
	public final void userParametersLongNameEmpty() {
		boolean res = false;
		testLogger.info("\r\n\r\n\r\n\r\n__________________________userParemetersLongNamesEmpty___________________________\r\n\r\n\r\n\r\n");
		message = new Message(testLogger);
		message.testName = " Empty userParametersLongNames";
		org.w3c.dom.Document document;
		String userParametersLongNames = Intern.dir_resources + File.separator + "userParametersNetCDFLongNames" + Global.EXT_XML;
		// création d'une fabrique de documents
		DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
		// création d'un constructeur de documents
		DocumentBuilder constructeur;
		try {
			constructeur = fabrique.newDocumentBuilder();
			document = constructeur.parse(userParametersLongNames);
			res = document.getFirstChild().getTextContent().trim().isEmpty();

		} catch (Exception e) {
			res = false;
		}

		Assert.assertTrue(res);
	}

	/**
	 * ================================================================= userParemetersStandardNames Empty =================================================================
	 */
	@Test
	public final void userParametersStandardNameEmpty() {
		boolean res = false;
		testLogger.info("\r\n\r\n\r\n\r\n__________________________userParemetersStandardNamesEmpty___________________________\r\n\r\n\r\n\r\n");
		message = new Message(testLogger);
		message.testName = " Empty userParametersStandardNames";
		org.w3c.dom.Document document;
		String userParametersStandardNames = Intern.dir_resources + File.separator + "userParametersStandardNames" + Global.EXT_XML;
		// création d'une fabrique de documents
		DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
		// création d'un constructeur de documents
		DocumentBuilder constructeur;
		try {
			constructeur = fabrique.newDocumentBuilder();
			document = constructeur.parse(userParametersStandardNames);
			res = document.getFirstChild().getTextContent().trim().isEmpty();

		} catch (Exception e) {
			res = false;
		}

		Assert.assertTrue(res);
	}
}

/*
 * ============================================================================
 * Nom du fichier : NemoShellTest.java
 * Auteur : sbregent
 * Date : 4 févr. 11
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import compare.OdvToNetCDF;

import fr.ifremer.nemo.config.Configuration;
import fr.ifremer.nemo.config.Proxy;
import fr.ifremer.nemo.domain.NetCDFLongNameParameterManager;
import fr.ifremer.nemo.domain.StandardNameParameterManager;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;
import fr.ifremer.nemo.util.Global.E_TypeConversion;
import fr.ifremer.nemo.util.Intern;

/**
 * Description: classe de tests - generation auto en appelant le mode batch -
 * comparaison avec des fichiers de référence Structure des repertoires:
 * dataRootPath/xxxx/versionDirName/dataList/conversion/tyyy où - xxx est un nom
 * representant le type de données (ex: diapalis,...) - choix libre - yyy est un
 * nom quelconque, nb de char quelconque pour xxx: si le repertoire commence par
 * "_", il est ignoré pour tyyy: si ce répertoire commence par "t", il est
 * considéré comme un test. S'il commence par "_", il est considéré comme un
 * test désactivé. S'il commence par un autre caractère, il est ignoré. Pour ne
 * lancer les tests que sur un répertoire, utiliser testDirAlone ex: public
 * static String[] testDirAlone={"cruiseFile_trajectory_6959"}; ne lancera les
 * tests que sur cruiseFile_trajectory_6959 utiliser - time ODV = ISO Le EDMO
 * code est forcé automatiquement à 486 (code IFREMER utilisé dans les fichiers
 * reference)
 * 
 * @author sbregent
 */
public class NemoBashTest {

	// fichier de resultats
	private static FileWriter _writer;
	private static File _resFile;

	// racine du repertoire contenant les tests
	// public static String dataRootPath = "C:\\Documents" + Global.SPACE +
	// "and" + Global.SPACE + "Settings" + File.separator + "sbregent" +
	// File.separator + "Mes" + Global.SPACE + "documents" + File.separator +
	// "SOPHIE\\NEMO\\NEMO\\workspaceEclipse361\\ifremer_datatest";

	public static String dataRootPath = "";
	// générer les fichiers en batch et les comparer aux fichiers REFERENCE
	public static boolean testBatchs = true;
	// comparer les fichiers manuels (output_eclipse) aux fichiers REFERENCE
	public static boolean compareManualFiles = false;

	public static String[] testDirAlone = {};

	public String inputName = "input";
	public String outputName = "output_junit";
	public static String outputManualName = "output_junit_batch";
	public String modelName = "model.xml";
	public String outputReferenceName = "output_REFERENCE";
	private static String batchCommands;

	public NemoShell shell = new NemoShell();

	public boolean sortDatas = false;

	public String datasetRef = Global.EMPTY_STRING;
	public String cdiSummaryFile = Global.EMPTY_STRING;
	public boolean couplingTableUpdate = false;
	public String couplingTablePrefix = Global.EMPTY_STRING;
	public static Logger batchLogger;
	public static Logger testLogger;
	public static Logger netCDFLogger;

	public static int nbTotalTests = 0;
	public static int nbTotalFailed = 0;
	public static int nbTotalSuccess = 0;

	public static boolean isVocabV2Param = true;
	public static boolean isVocabV2 = true;

	public Message message;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DOMConfigurator.configure("resources/log4jBatch.xml"); //$NON-NLS-1$
		batchLogger = Logger.getLogger("nemoBatch");//$NON-NLS-1$
		batchLogger.setLevel(Level.DEBUG);
		// LOGGER
		DOMConfigurator.configure("resources/log4jBatch.xml"); //$NON-NLS-1$
		testLogger = Logger.getLogger("nemoJunits");//$NON-NLS-1$
		testLogger.setLevel(Level.DEBUG);

		// // LOGGER
		//		DOMConfigurator.configure("resources/log4j.xml"); //$NON-NLS-1$
		//		netCDFLogger = Logger.getLogger("nemoJunits");//$NON-NLS-1$
		// netCDFLogger.setLevel(Level.DEBUG);

		nbTotalTests = 0;
		nbTotalFailed = 0;
		nbTotalSuccess = 0;
		Date actuelle = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String dat = dateFormat.format(actuelle);
		setDataRootPath();

		testLogger.info("\r\n\r\n\r\n\r\n===================================== " + dat + " =========================\r\n\r\n\r\n\r\n");
		testLogger.info("test batchs : " + testBatchs);
		testLogger.info("comp manual files : " + compareManualFiles + "(" + outputManualName + ")");
		initResultWriter();
	}

	private static void initResultWriter() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy_mm_dd-hh-mm-ss"); //$NON-NLS-1$
		String resFileName = "results_" + df.format(new Date()) + ".csv";
		_resFile = new File(resFileName);
		try {
			_writer = new FileWriter(resFileName);
			_writer.append("testName;");
			_writer.append("test directory;");
			_writer.append("atLeastOneTestFound;");
			_writer.append("parameters list;");
			_writer.append("conversion;");
			// _writer.append("unit test;");
			_writer.append("reference found;");
			_writer.append("nemo convert ended;");
			_writer.append("comparison ok;");
			_writer.append('\n');

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void writeInResultsFile(Message message) {
		try {
			_writer.append(message.testName + ";");
			_writer.append(message.dirName + ";");
			_writer.append(message.atLeastOneTestFound + ";");
			_writer.append(message.parameterList + ";");
			_writer.append(message.convType + ";");
			_writer.append(message.isReferenceFound + ";");
			_writer.append(message.conversionRes + ";");
			_writer.append(message.batchCompRes + ";");
			_writer.append('\n');

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void setDataRootPath() {
		String pwdPath = System.getProperty("user.dir");
		// hudson: C:\Portail\data\jobs\Nemo\workspace\Nemo_product
		System.out.println("pwdPath = " + pwdPath + "--------------");

		int index = pwdPath.indexOf("NEMO_product");
		int index2 = pwdPath.indexOf("Nemo_product");
		int index3 = pwdPath.indexOf("fr.ifremer.nemo");

		if (index != -1) {
			String path = pwdPath.substring(0, index);
			dataRootPath = path + "ifremer_datatest";
		} else if (index2 != -1) {
			String path = pwdPath.substring(0, index2);
			dataRootPath = path + "ifremer_datatest";
		} else if (index3 != -1) {
			String path = pwdPath.substring(0, index3);
			dataRootPath = path + "ifremer_datatest";
		} else {
			dataRootPath = pwdPath + File.separator + "ifremer_datatest";
		}
		System.out.println("dataRootPath = " + dataRootPath + "--------------");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		_writer.close();

		testLogger.info("\r\n\r\n\r\n\r\n___________________________isVocabV2_____________________________\r\n\r\n\r\n\r\n");
		if (isVocabV2Param == true) {
			Assert.assertTrue(isVocabV2);
			testLogger.info("is vocab v2");
		} else {
			Assert.assertFalse(isVocabV2);
			testLogger.info("is vocab v1");
		}

		testLogger.info("\r\n\r\n" + "TOTAL =====================================================================================" + "\r\n" + nbTotalSuccess + " tests succeed on " + nbTotalTests + "\r\n" + nbTotalFailed + " tests failed");

		testLogger.info("batchCommands" + Global.RN);
		testLogger.info(batchCommands);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		sortDatas = false;
		datasetRef = Global.EMPTY_STRING;
		cdiSummaryFile = Global.EMPTY_STRING;
		couplingTableUpdate = false;
		couplingTablePrefix = Global.EMPTY_STRING;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {

	}

	private void writeInResultsFile(String theString) {
		try {
			System.out.println(theString);
			_writer.append(theString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * =================================================================
	 * MEDATLAS
	 * =================================================================
	 */
	@Test
	public final void test_P091_Medatlas() {
		testLogger.info("\r\n\r\n\r\n\r\n___________________________test_P091_Medatlas_____________________________\r\n\r\n\r\n\r\n");
		// writeInResultsFile("P091_Medatlas;");
		boolean res = testOneType_ListConv(DataListTypeEnum.P091, Conversion.MEDATLAS, E_TypeConversion.MEDATLAS);
		Assert.assertTrue(res);
	}

	@Test
	public final void test_P011viaP091_Medatlas() {
		testLogger.info("\r\n\r\n\r\n\r\n___________________________test_P011viaP091_Medatlas_____________________________\r\n\r\n\r\n\r\n");
		// writeInResultsFile("P011viaP091_Medatlas;");
		Assert.assertTrue(testOneType_ListConv(DataListTypeEnum.P011_VIA_P091, Conversion.MEDATLAS, E_TypeConversion.MEDATLAS));
	}

	/**
	 * ================================================================= ODV
	 * =================================================================
	 */
//	@Test
	public final void test_P091_ODVMULTI() {
		testLogger.info("\r\n\r\n\r\n\r\n____________________________test_P091_ODVMULTI____________________________\r\n\r\n\r\n\r\n");
		// writeInResultsFile("P091_ODVMULTI;");
		Assert.assertTrue(testOneType_ListConv(DataListTypeEnum.P091, Conversion.ODV, E_TypeConversion.ODV_MULTISTATIONS));
	}

//	@Test
	public final void test_P011viaP091_ODVMULTI() {
		testLogger.info("\r\n\r\n\r\n\r\n____________________________test_P011viaP091_ODVMULTI____________________________\r\n\r\n\r\n\r\n");
		// writeInResultsFile("P011viaP091_ODVMULTI;");
		Assert.assertTrue(testOneType_ListConv(DataListTypeEnum.P011_VIA_P091, Conversion.ODV, E_TypeConversion.ODV_MULTISTATIONS));
	}

//	@Test
	public final void test_P011viaP021_ODVMULTI() {
		testLogger.info("\r\n\r\n\r\n\r\n__________________________test_P011viaP021_ODVMULTI_____________________________\r\n\r\n\r\n\r\n");
		// writeInResultsFile("P011viaP021_ODVMULTI;");
		Assert.assertTrue(testOneType_ListConv(DataListTypeEnum.P011_VIA_P021, Conversion.ODV, E_TypeConversion.ODV_MULTISTATIONS));
	}

//	@Test
	public final void test_P011viaP091_NETCDFMULTI() {
		testLogger.info("\r\n\r\n\r\n\r\n____________________________test_P011viaP091_NETCDFMULTI____________________________\r\n\r\n\r\n\r\n");
		// writeInResultsFile("P011viaP091_NETCDFMULTI;");
		Assert.assertTrue(testOneType_ListConv(DataListTypeEnum.P011_VIA_P091, Conversion.NETCDF, E_TypeConversion.NETCDF_MULTISTATIONS));
	}

//	@Test
	public final void test_P011viaP021_NETCDFMULTI() {
		testLogger.info("\r\n\r\n\r\n\r\n__________________________test_P011viaP021_NETCDFMULTI_____________________________\r\n\r\n\r\n\r\n");
		// writeInResultsFile("P011viaP021_NETCDFMULTI;");
		Assert.assertTrue(testOneType_ListConv(DataListTypeEnum.P011_VIA_P021, Conversion.NETCDF, E_TypeConversion.NETCDF_MULTISTATIONS));
	}

	/**
	 * =================================================================
	 * invalidated model
	 * =================================================================
	 */
	@Test
	public final void inValidated_model() {
		testLogger.info("\r\n\r\n\r\n\r\n_______________________________unValidated_model________________________________\r\n\r\n\r\n\r\n");
		message = new Message();
		message.testName = "invalidated model in batch mod";
		File root = new File(dataRootPath);
		String inputAbsolutePath = root.getAbsolutePath() + File.separator + "_9656_collectionGroupByCruise_withSeparator_unvalidated_model" + File.separator + inputName;
		String outputAbsolutePath = root.getAbsolutePath() + File.separator + "_9656_collectionGroupByCruise_withSeparator_unvalidated_model" + File.separator + "P091" + File.separator + "ODV_MULTISTATIONS" + File.separator;
		Assert.assertFalse(convertWithNemoBatch(Conversion.ODV, E_TypeConversion.ODV_MULTISTATIONS, inputAbsolutePath, outputAbsolutePath, true));
	}

	/**
	 * =================================================================
	 * emptyProxy
	 * =================================================================
	 */
	@Test
	public final void checkEmptyProxyTest() {
		testLogger.info("\r\n\r\n\r\n\r\n_______________________________emptyProxyTest________________________________\r\n\r\n\r\n\r\n");
		message = new Message();
		message.testName = "empty Proxy test";

		Configuration config = new Configuration();
		Assert.assertTrue(checkEmptyProxy(config));
	}

	@Test
	public final void checkEmptyProxyTestInit() {
		testLogger.info("\r\n\r\n\r\n\r\n_______________________________emptyProxyTest________________________________\r\n\r\n\r\n\r\n");
		message = new Message();
		message.testName = "empty Proxy test";

		Configuration config = new Configuration(true);
		Assert.assertTrue(checkEmptyProxy(config));
	}

	@Test
	public final void checkDefaultLanguageTest() {
		testLogger.info("\r\n\r\n\r\n\r\n_______________________________emptyProxyTest________________________________\r\n\r\n\r\n\r\n");
		message = new Message();
		message.testName = "empty Proxy test";
		Configuration config = new Configuration();
		String lang = config.getLang();
		Assert.assertTrue(lang.equals("en_GB"));
	}

	@Test
	public final void checkDefaultLanguageInitTest() {
		testLogger.info("\r\n\r\n\r\n\r\n_______________________________emptyProxyTest________________________________\r\n\r\n\r\n\r\n");
		message = new Message();
		message.testName = "empty Proxy test";
		Configuration config = new Configuration(true);
		String lang = config.getLang();
		Assert.assertTrue(lang.equals("en_GB"));
	}

	/**
	 * =================================================================
	 * userParemetersLabels Empty
	 * =================================================================
	 */
	@Test
	public final void userParemetersLabelsEmpty() {
		boolean res = false;
		testLogger.info("\r\n\r\n\r\n\r\n__________________________userParemetersLabelsEmpty___________________________\r\n\r\n\r\n\r\n");
		message = new Message();
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
	 * =================================================================
	 * userParemetersLongNames Empty
	 * =================================================================
	 */
	@Test
	public final void userParemetersLongNameEmpty() {
		boolean res = false;
		testLogger.info("\r\n\r\n\r\n\r\n__________________________userParemetersLongNamesEmpty___________________________\r\n\r\n\r\n\r\n");
		message = new Message();
		message.testName = " Empty userParemetersLongNames";
		org.w3c.dom.Document document;
		String userParametersLongNames = Intern.dir_resources + File.separator + "userParametersNETCDFLongNames" + Global.EXT_XML;
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
	 * =================================================================
	 * userParemetersStandardNames Empty
	 * =================================================================
	 */
	@Test
	public final void userParemetersStandardNameEmpty() {
		boolean res = false;
		testLogger.info("\r\n\r\n\r\n\r\n__________________________userParemetersStandardNamesEmpty___________________________\r\n\r\n\r\n\r\n");
		message = new Message();
		message.testName = " Empty userParemetersStandardNames";
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

	/**
	 * =================================================================
	 * methodes
	 * =================================================================
	 */
	public boolean testOneType_ListConv(DataListTypeEnum dataList, Conversion conversion, E_TypeConversion convType) {

		boolean ok = true;
		boolean res;
		boolean comp = false;
		int cptTests = 0;
		int cptFail = 0;
		boolean testFound;
		System.out.println("dataRootPath" + dataRootPath + "--------");
		File root = new File(dataRootPath);
		for (File testDir : root.listFiles()) {
			// writeInResultsFile(testDir.getName() + ";");
			if (isToTest(testDir.getName())) {
				// writeInResultsFile("yes" + ";");
				System.out.println("testDir: " + testDir.toString());
				message = new Message();
				message.dirName = testDir.getName();
				testFound = false;

				for (File paramListDir : testDir.listFiles()) {

					if (paramListDir.getName().equals(dataList.toString())) {
						// writeInResultsFile(dataList.toString() + ";");

						for (File convDir : paramListDir.listFiles()) {
							// writeInResultsFile(convDir.getName() + ";");

							if (convDir.getName().equals(convType.toString())) {

								for (File unitTest : convDir.listFiles()) {
									ok = true;
									message = new Message();

									message.dirName = testDir.getName();
									message.convType = convType.toString();
									message.parameterList = paramListDir.getName();
									if (unitTest.getName().startsWith("t")) {
										// writeInResultsFile(unitTest.getName()
										// + ";");
										res = false;
										comp = false;
										message.testName = paramListDir.getName() + " " + convDir.getName() + " " + unitTest.getName();
										String inputAbsolutePath = testDir.getAbsolutePath() + File.separator + inputName;
										String outputAbsolutePath = unitTest.getAbsolutePath();
										cptTests++;
										nbTotalTests++;
										testFound = true;
										message.isTestFound = true;
										if (testBatchs) {
											message.isReferenceFound = fileExist(outputAbsolutePath, outputReferenceName);
											// writeInResultsFile(message.isReferenceFound
											// + ";");

											res = convertWithNemoBatch(conversion, convType, inputAbsolutePath, outputAbsolutePath, !unitTest.getName().startsWith("tj"));
											// writeInResultsFile(res + ";");
											ok = res && ok;
											if (res) {
												if (conversion == Conversion.NETCDF) {
													comp = compareWithODVReference(outputAbsolutePath, outputName);
												} else {
													comp = compareWithReference(outputAbsolutePath, outputName);
												}
												message.batchCompRes = comp;
												// writeInResultsFile(comp +
												// "\n");
												ok = comp && ok;
											}
											if (ok) {
												nbTotalSuccess++;
											}
											if (!ok) {
												cptFail++;
												nbTotalFailed++;
											}

										}

										if (compareManualFiles) {
											message.isManualFound = fileExist(outputAbsolutePath, outputManualName);
											if (message.isManualFound) {
												comp = compareWithReference(outputAbsolutePath, outputManualName);
												message.manualCompRes = comp;
												message.manualCompName = outputManualName;
												ok = comp && ok;
											}
										}

									} else if (unitTest.getName().startsWith("_t")) {
										message.testName = paramListDir.getName() + " " + convDir.getName() + " " + unitTest.getName();
										message.isActivated = false;
									}
									if (!testFound) {
										message.isTestFound = false;
									} else {

										message.print();
									}
									writeInResultsFile(message);
								}

							}
						}

					}

				}
				if (!testFound) {
					message.print();
				}

			} else {
				// writeInResultsFile("no" + ";");
			}
		}
		testLogger.info("\r\n=========================================" + cptFail + " failed /" + cptTests + " tested" + "=========================================" + "\r\n");

		return cptFail == 0;
	}

	public boolean isToTest(String dirName) {
		boolean res = true;
		if (dirName.startsWith("_") || dirName.startsWith(".")) {
			res = false;
		} else if (testDirAlone.length > 0) {
			res = false;
			for (String oneDir : testDirAlone) {
				if (dirName.equals(oneDir)) {
					res = true;
				}
			}
		}
		return res;
	}

	public boolean convertWithNemoBatch(Conversion conversion, E_TypeConversion odvConv, String inputAbsolutePath, String outputAbsolutePath, boolean isDateIso) {
		boolean res = false;
		String output = outputAbsolutePath + File.separator + outputName;
		String model = outputAbsolutePath + File.separator + modelName;
		couplingTablePrefix = outputAbsolutePath;
		couplingTableUpdate = false;
		cdiSummaryFile = outputAbsolutePath + File.separator + "cdiSummary_junit";
		File mod = new File(model);
		Nemo.getInstance().resetInstance();
		Nemo.getInstance().getConfig().setIdEdmoRespCdi("486");// 486 is ifremer
		Nemo.getInstance().getConfig().setDateISO(isDateIso);
		// add long Name for test
		NetCDFLongNameParameterManager.getInstance().getLongNamesMap().putAll(netCDFLongNamesMap());
		// add standard Name for test
		StandardNameParameterManager.getInstance().getStandardNamesMap().putAll(netCDFStandardNamesMap());
		// edmo code
		shell = new NemoShell();
		String batchCmd = "";
		if (!mod.exists()) {
			message.isModelFound = false;
		} else {
			message.isModelFound = true;
			Nemo.getInstance().setBatchMode(true);
			try {
				res = shell.main(inputAbsolutePath, output, model, conversion, sortDatas, odvConv, datasetRef, cdiSummaryFile, couplingTableUpdate, couplingTablePrefix, batchLogger);
			} catch (Exception e) {
				message.conversionRes = res;
				message.conversionError = e.getMessage();
			}

		}
		batchCmd = "nemo_batch";
		batchCmd += Global.SPACE + Global.MOINS + Global.optInputFile;
		batchCmd += Global.SPACE + inputAbsolutePath;

		batchCmd += Global.SPACE + Global.MOINS + Global.optModelFile;
		batchCmd += Global.SPACE + model;

		batchCmd += Global.SPACE + Global.MOINS + Global.optOutputFile;
		batchCmd += Global.SPACE + output + "_batch";

		batchCmd += Global.SPACE + Global.MOINS + Global.optConv;
		batchCmd += Global.SPACE + conversion;
		if (E_TypeConversion.ODV_MULTISTATIONS == odvConv) {
			batchCmd += Global.SPACE + Global.MOINS + Global.optOdvMulti;
		}
		batchCmd += Global.SPACE + Global.MOINS + Global.optCdiSummary;
		batchCmd += Global.SPACE + cdiSummaryFile + "_batch";

		if (couplingTableUpdate) {
			batchCmd += Global.SPACE + Global.MOINS + Global.optCouplingTableUpdate;
			batchCmd += Global.SPACE + Global.MOINS + Global.optCouplingTablePrefix;
			batchCmd += Global.SPACE + couplingTablePrefix;
		}
		batchCommands += Global.RN + batchCmd;

		message.conversionRes = res;
		return res;
	}

	// Long Names utilisé pour les tests de non regression
	// à completer si ajout de test
	private HashMap<String, String> netCDFLongNamesMap() {
		HashMap<String, String> longNames = new HashMap<String, String>();

		longNames.put("NTRAYYDZ", "Concentration of Nitrate"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("PRESPR01", "Pressure"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("ADEPZZ01", "Depth"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("PSSTZZ01", "Temperature"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("ODSDM021", "Salinity"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("DOXYZZ01", "Concentration of oxygen"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("FLUOZZZZ", "Fluorescence"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("TEMPET01", "Temperature"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("NTRIZZXX", "Concentration of nitrite"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("PSLTZZ01", "Salinity"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("COREDIST", "Depth"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("ASLVMSZZ", "Surface elevation"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("MAGNFLDX", "Magnetic Field of the Earth"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("MAGNFL02", "Magnetic Field of the Earth"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("MAGDCORR", "Diurnal Varaition"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("GRAVFLDX", "Gravity Field of the Earth"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("EOTVCORR", "Eotvoscorrection"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("FAGRVANM", "Gravity Field Anomaly"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("PBXXISPW", "Concentration of lead"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("MZNSP012", "Concentration of zinc"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("PRESCX01", "Pressure"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("NO2FLTIC", "Concentration of nitrite"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("NTRAAA04", "Concentration of nitrate"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("PHOSAAD3", "Concentration of phosphate"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("AMONHOTX", "Concentration of ammonium"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CPHLFLP2", "Concentration of chlorophll-a"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CHLBINTC", "Concentration of chlorophll-b"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CHLAEPIM", "Concentration of chlorophll-a"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("SPHAFLPD", "Concentration of phaeopigments"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CHLBMHP1", "Concentration of chlorophll-b"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CPHLHPP2", "Concentration of chlorophll-a"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("PHPICP01", "Concentration of phosphorus"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("ORGPMSZZ", "Concentration of phosphorus"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("TPHSXFP2", "Concentration of phosphorus"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("DEPHPM01", "Depth"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("NTRAZZXX", "Concentration of nitrite"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CNDCST01", "Electrical conductivity"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("TEMPSZ01", "Temperature"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("SSALSG01", "Salinity"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("TEMPPR01", "Temperature"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("PBCNXTXT", "Concentration of lead"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("TEMPZZXX", "Temperature"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("ATTNZZ01", "volume beam attenuation coefficient"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CPHLPM01", "mass concentration of chlorophyll a in sea water"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("AHSLZZ01", "Height above sea level"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("AHGTZZ01", "Height above sea level"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CJDY1101", "Julien Date"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CAPASS01", "Pressure"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CDTBSS01", "Temperature of the atmophere"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CDTSZZ01", "Temperature standard deviation of the atmosphere"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CHMSZZ01", "Specific humidity standard of the atmosphere"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CHUMSS01", "Specific humidity of the atmosphere"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("ESEWZZ01", "Eastward wind Velocity"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("ESNSZZ01", "Northward wind Velocity"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("EWEWSS01", "Eastward wind Velocity"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("EWNSSS01", "Northward wind Velocity"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CBTASS01", "Temperature"); //$NON-NLS-1$//$NON-NLS-2$

		return longNames;
	}

	// Standard Names utilisé pour les tests de non regression
	// à completer si ajout de test
	private HashMap<String, String> netCDFStandardNamesMap() {
		HashMap<String, String> standardNames = new HashMap<String, String>();

		standardNames.put("PRESPR01", "sea_water_pressure"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("NTRAYYDZ", "mole_concentration_of_nitrate_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("ADEPZZ01", "depth"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("PSSTZZ01", "sea_water_temperature"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("ODSDM021", "sea_water_salinity"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("DOXYZZ01", "mole_concentration_of_dissolved_molecular_oxygen_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("TEMPET01", "sea_water_temperature"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("NTRIZZXX", "moles_of_nitrite_per_unit_mass_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("PSLTZZ01", "sea_water_salinity"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("COREDIST", "depth"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("ASLVMSZZ", "sea_surface_height_above_sea_level"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("PRESCX01", "sea_water_pressure"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("NO2FLTIC", "moles_of_nitrite_per_unit_mass_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("NTRAAA04", "mole_concentration_of_nitrate_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("PHOSAAD3", "mole_concentration_of_phosphate_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("AMONHOTX", "mole_concentration_of_ammonium_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("CPHLFLP2", "mass_concentration_of_chlorophyll_a_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("CHLBINTC", "mass_concentration_of_chlorophyll_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("CHLAEPIM", "mass_concentration_of_chlorophyll_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("CHLBMHP1", "mass_concentration_of_chlorophyll_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("CPHLHPP2", "mass_concentration_of_chlorophyll_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("PHPICP01", "mole_concentration_of_particulate_organic_matter_expressed_as_phosphorus_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("TPHSXFP2", "mole_concentration_of_particulate_organic_matter_expressed_as_phosphorus_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("DEPHPM01", "depth"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("NTRAZZXX", "mole_concentration_of_nitrate_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("CNDCST01", "sea_water_electrical_conductivity"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("TEMPSZ01", "sea_water_temperature"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("SSALSG01", "sea_water_salinity"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("TEMPPR01", "sea_water_temperature"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("TEMPZZXX", "sea_water_temperature"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("ATTNZZ01", "volume_beam_attenuation_coefficient_of_radiative_flux_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("CPHLPM01", "mass_concentration_of_chlorophyll_a_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("AHSLZZ01", "height"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("AHGTZZ01", "height"); //$NON-NLS-1$//$NON-NLS-2$

		return standardNames;
	}

	public boolean fileExist(String outputAbsolutePath, String outputTestName) {
		File f = new File(outputAbsolutePath + File.separator + outputTestName);
		return f.exists();
	}

	public boolean compareWithReference(String outputAbsolutePath, String outputTestName) {
		boolean comp = false;
		comp = compare_____TwoFiles(outputAbsolutePath, outputTestName, outputReferenceName);
		return comp;
	}

	public boolean compareWithODVReference(String outputAbsolutePath, String outputTestName) {
		boolean comp = false;

		String outputTest = outputAbsolutePath + File.separator + outputTestName;
		String output_REFERENCE = outputAbsolutePath + File.separator + outputReferenceName;

		try {
			comp = OdvToNetCDF.compare(output_REFERENCE, outputTest);
		} catch (Exception e) {
			comp = false;
		}

		return comp;
	}

	public boolean compare_____TwoFiles(String outputAbsolutePath, String fileA, String fileB) {

		BufferedReader bufOut = null;
		BufferedReader bufRef = null;
		String outputLine = "";
		String refFileLine;
		String outputTest = outputAbsolutePath + File.separator + fileA;
		String output_REFERENCE = outputAbsolutePath + File.separator + fileB;
		boolean identical = true;

		try {
			bufOut = new BufferedReader(new FileReader(outputTest));
		} catch (FileNotFoundException e1) {
			// testLogger.error(fileA+" file not found");
			// message.filesMap.put(fileA, false);
			return false;
		}
		try {
			bufRef = new BufferedReader(new FileReader(output_REFERENCE));
		} catch (FileNotFoundException e1) {
			// testLogger.error(fileB+" file not found");
			// message.filesMap.put(fileB, false);
			return false;
		}

		// message.filesMap.put(fileA, true);
		// message.filesMap.put(fileB, true);
		try {
			String newRefLine = "";
			if (bufOut != null && bufRef != null) {
				while (bufOut.ready() && identical) {
					outputLine = bufOut.readLine();
					refFileLine = bufRef.readLine();
					String format = "";

					identical = outputLine.equals(refFileLine);
					// depuis le 17/01/2012, le mapping BODC a changé : PRESPS02
					// remplace par PRESPR01
					Pattern p;
					Matcher m;
					if (!identical) {
						p = Pattern.compile("PRESPS02");
						m = p.matcher(refFileLine);
						if (m.find()) {
							refFileLine = m.replaceAll("PRESPR01");
							identical = outputLine.equals(refFileLine);
						}
					}
					// depuisles vocab v2, le mapping BODC a changé : P011
					// remplace par P01
					if (!identical) {
						p = Pattern.compile("P011");
						m = p.matcher(refFileLine);
						if (m.find()) {
							refFileLine = m.replaceAll("P01");
							identical = outputLine.equals(refFileLine);
							isVocabV2 = true;
						}
					}
					// depuisles vocab v2, le mapping BODC a changé : P061
					// remplace par P06
					if (!identical) {
						p = Pattern.compile("P061");
						m = p.matcher(refFileLine);
						if (m.find()) {
							refFileLine = m.replaceAll("P06");
							identical = outputLine.equals(refFileLine);
							isVocabV2 = true;
						}
					}

				}
			}
			return identical;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			testLogger.error("IOException");
			return false;
		}
		catch (NullPointerException e) {
			// TODO Auto-generated catch block
			testLogger.error("NullPointerException");
			return false;
		}
	}

	private String completeTitle(String title) {
		String res = "";
		if (title.length() < 70) {
			for (int i = 0; i < 10; i++) {
				res += "_";
			}
			res += title;

			for (int i = 0; i < 60 - title.length(); i++) {
				res += "_";
			}
		} else {
			res = title;
		}
		return res;
	}

	public class Message {
		public String parameterList;
		public String convType;
		public boolean isActivated = true;
		public boolean isTestFound = true;
		public String dirName;
		public String testName = null;
		public boolean isModelFound;
		public boolean isReferenceFound = false;
		public boolean conversionRes;
		public String conversionError;
		public boolean batchCompRes;
		public boolean manualCompRes;
		public String manualCompName;
		public boolean isManualFound;
		public boolean atLeastOneTestFound = false;

		public Message() {

		}

		public void print() {
			String mes = "";
			mes += completeTitle(dirName);
			if (testName != null) {
				mes += "\r\n" + testName;
				if (!isActivated) {
					mes += ("- DESACTIVATED -");
					write(mes);
					return;
				}
				if (!isTestFound) {
					mes += ("- No test found -");
					write(mes);
					return;
				}
				if (!isModelFound) {
					mes += ("- KO No model found -");
					write(mes);
					return;
				}
				if (!conversionRes) {
					mes += "\r\nconversion: " + getString(conversionRes);
					mes += " " + conversionError;
				}
				if (testBatchs) {
					if (isReferenceFound) {
						if (batchCompRes) {
							mes += " " + getString(conversionRes);
						} else {
							mes += "\r\nbatch comparison: " + getString(batchCompRes);
						}
					} else {
						mes += "\r\nReference file not found";
					}
				}
				if (compareManualFiles && isManualFound) {
					if (isReferenceFound) {
						mes += "\r\nmanual comparison: " + getString(manualCompRes);
					} else {
						mes += "\r\nReference file not found";
					}

				}
				if (!(batchCompRes && conversionRes)) {
					write(mes);
				}
			} else {
				mes += " no test found";
				write(mes);
			}

		}

		private String getString(boolean res) {
			String str = "";
			if (res) {
				str = "OK";
			} else {
				str = "KO";
			}
			return str;
		}

		private void write(String mes) {
			testLogger.info(mes + "\r\n");
		}
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

}

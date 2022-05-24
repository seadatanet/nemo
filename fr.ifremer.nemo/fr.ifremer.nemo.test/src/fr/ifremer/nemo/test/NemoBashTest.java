package fr.ifremer.nemo.test;
/*
 * ============================================================================
 * Nom du fichier : NemoShellTest.java
 * Auteur : sbregent
 * Date : 4 févr. 11
 * Copyright : Altran
 * ============================================================================
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.ifremer.medatlas.exceptions.MedatlasReaderException;
import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;
import fr.ifremer.nemo.util.Global.E_TypeConversion;
import fr.ifremer.seadatanet.cfpoint.exceptions.CFPointException;
import fr.ifremer.seadatanet.odv.output.OdvException;
import ucar.ma2.Array;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.util.CompareNetcdf2;

/**
 * <pre>
 * Description: classe de tests - 
 * generation auto en appelant le mode batch - comparaison avec des fichiers de référence 
 * Structure des repertoires:
 * dataRootPath/xxxx/versionDirName/dataList/conversion/tyyy 
 * où 
 * - xxx est un nom representant le type de données (ex: diapalis,...) 
 * choix libre 
 * - yyy est un nom quelconque, nb de char quelconque pour xxx: si le repertoire commence par
 * "_", il est ignoré pour tyyy: si ce répertoire commence par "t", il est
 * considéré comme un test. S'il commence par "_", il est considéré comme un
 * test désactivé. S'il commence par un autre caractère, il est ignoré. 
 * Pour ne lancer les tests que sur un répertoire, utiliser testDirAlone 
 * ex: public static String[] testDirAlone={"cruiseFile_trajectory_6959"}; ne lancera les
 * tests que sur cruiseFile_trajectory_6959 
 * utiliser - time ODV = ISO (settings)
 * Le EDMO code est forcé automatiquement à 486 (code IFREMER utilisé dans les fichiers reference)
 * 
 * Les résultats sont synthétisés dans un fichier CSV results_YYYY_MM_DD-hh-ss.csv à la racine du projet
 * </pre>
 * 
 * @author sbregent
 */
public class NemoBashTest extends AbstractTest{

	// fichier de resultats
	private static FileWriter _writer;
	private static File _resFile;

	/**
	 * ******************************************************** *************** CONFIG ********************************* ********************************************************
	 */
	boolean REPLACE = false;// WARNING!!!
	boolean REMOVE_JUNIT = true;

	public static String[] testDirAlone = {};
//				 public static String[] testDirAlone = {"examples_PV_Pelgas2013"};

	public static String outputManualName = "output_junit_batch";
	public String outputReferenceName = "output_REFERENCE";
	public static Logger testLogger;
	public static Logger netCDFLogger;

	public static int nbTotalTests = 0;
	public static int nbTotalFailed = 0;
	public static int nbTotalSuccess = 0;
	public static boolean isVocabV2Param = true;
	public static boolean isVocabV2 = true;


	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		batchLogger = LogManager.getLogger("nemoBatch");
		testLogger = LogManager.getLogger("nemoJunits");

		nbTotalTests = 0;
		nbTotalFailed = 0;
		nbTotalSuccess = 0;
		Date actuelle = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String dat = dateFormat.format(actuelle);

		testLogger.info("\r\n\r\n\r\n\r\n===================================== " + dat + " =========================\r\n\r\n\r\n\r\n");
		initResultWriter();
		Nemo.getInstance().setLogger(testLogger);

	}
	private static void initResultWriter() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd-HH-mm-ss"); //$NON-NLS-1$
		String resFileName = "results_" + df.format(new Date()) + ".csv";
		_resFile = new File(resFileName);
		try {
			_writer = new FileWriter(resFileName);
			_writer.append("testName;");
			_writer.append("test directory;");
			_writer.append("parameters list;");
			_writer.append("conversion;");
			_writer.append("reference found;");
			_writer.append("nemo convert ended;");
			_writer.append("data comparison ok;");
			_writer.append("octopus check;");
			_writer.append("cdiSummary comparison ok;");
			_writer.append("diff;");
			_writer.append("processTime;");
			_writer.append('\n');

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void writeInResultsFile(Message message) {
		try {
			_writer.append(message.testName + ";");
			_writer.append(message.dirName + ";");
			_writer.append(message.parameterList + ";");
			_writer.append(message.convType + ";");
			_writer.append(message.isReferenceFound + ";");
			_writer.append(message.conversionRes + ";");
			_writer.append(message.batchCompRes + ";");
			_writer.append(message.octopusCheck + ";");
			_writer.append(message.batchCdiSummaryCompRes + ";");
			_writer.append(message.diff + ";");
			_writer.append(message.processTime + ";");
			_writer.append('\n');

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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


	/**
	 * ================================================================= MEDATLAS =================================================================
	 */
	@Test
	public final void test_P091_Medatlas() {

		testLogger.info("\r\n\r\n\r\n\r\n___________________________test_P091_Medatlas_____________________________\r\n\r\n\r\n\r\n");
		boolean res = testOneType_ListConv(DataListTypeEnum.P091, Conversion.MEDATLAS, E_TypeConversion.MEDATLAS);
		Assert.assertTrue(res);
	}

	@Test
	public final void test_P011viaP091_Medatlas() {
		testLogger.info("\r\n\r\n\r\n\r\n___________________________test_P011viaP091_Medatlas_____________________________\r\n\r\n\r\n\r\n");
		Assert.assertTrue(testOneType_ListConv(DataListTypeEnum.P011_VIA_P091, Conversion.MEDATLAS, E_TypeConversion.MEDATLAS));
	}

	/**
	 * ================================================================= ODV =================================================================
	 */
	@Test
	public final void test_P091_ODVMULTI() {
		testLogger.info("\r\n\r\n\r\n\r\n____________________________test_P091_ODVMULTI____________________________\r\n\r\n\r\n\r\n");
		Assert.assertTrue(testOneType_ListConv(DataListTypeEnum.P091, Conversion.ODV, E_TypeConversion.ODV_MULTISTATIONS));
	}

	@Test
	public final void test_P091_ODVMONO() {
		testLogger.info("\r\n\r\n\r\n\r\n____________________________test_P091_ODVMONO____________________________\r\n\r\n\r\n\r\n");
		Assert.assertTrue(testOneType_ListConv(DataListTypeEnum.P091, Conversion.ODV, E_TypeConversion.ODV_MONOSTATION));
	}

	@Test
	public final void test_P011viaP091_ODVMULTI() {
		testLogger.info("\r\n\r\n\r\n\r\n____________________________test_P011viaP091_ODVMULTI____________________________\r\n\r\n\r\n\r\n");
		Assert.assertTrue(testOneType_ListConv(DataListTypeEnum.P011_VIA_P091, Conversion.ODV, E_TypeConversion.ODV_MULTISTATIONS));
	}

	@Test
	public final void test_P011viaP091_ODVMONO() {
		testLogger.info("\r\n\r\n\r\n\r\n____________________________test_P011viaP091_ODVMONO____________________________\r\n\r\n\r\n\r\n");
		Assert.assertTrue(testOneType_ListConv(DataListTypeEnum.P011_VIA_P091, Conversion.ODV, E_TypeConversion.ODV_MONOSTATION));
	}

	@Test
	public final void test_P011viaP021_ODVMULTI() {
		testLogger.info("\r\n\r\n\r\n\r\n__________________________test_P011viaP021_ODVMULTI_____________________________\r\n\r\n\r\n\r\n");
		Assert.assertTrue(testOneType_ListConv(DataListTypeEnum.P011_VIA_P021, Conversion.ODV, E_TypeConversion.ODV_MULTISTATIONS));
	}

	@Test
	public final void test_P011viaP021_ODVMONO() {
		testLogger.info("\r\n\r\n\r\n\r\n__________________________test_P011viaP021_ODVMONO_____________________________\r\n\r\n\r\n\r\n");
		Assert.assertTrue(testOneType_ListConv(DataListTypeEnum.P011_VIA_P021, Conversion.ODV, E_TypeConversion.ODV_MONOSTATION));
	}

	/**
	 * ================================================================= CFPoint =================================================================
	 */
	@Test
	public final void test_P011viaP091_NETCDFMULTI() {
		testLogger.info("\r\n\r\n\r\n\r\n____________________________test_P011viaP091_NETCDFMULTI____________________________\r\n\r\n\r\n\r\n");
		Assert.assertTrue(testOneType_ListConv(DataListTypeEnum.P011_VIA_P091, Conversion.NETCDF, E_TypeConversion.NETCDF_MULTISTATIONS));
	}

	@Test
	public final void test_P011viaP091_NETCDFMONO() {
		testLogger.info("\r\n\r\n\r\n\r\n____________________________test_P011viaP091_NETCDFMONO____________________________\r\n\r\n\r\n\r\n");
		Assert.assertTrue(testOneType_ListConv(DataListTypeEnum.P011_VIA_P091, Conversion.NETCDF, E_TypeConversion.NETCDF_MONOSTATION));
	}

	@Test
	public final void test_P011viaP021_NETCDFMULTI() {
		testLogger.info("\r\n\r\n\r\n\r\n__________________________test_P011viaP021_NETCDFMULTI_____________________________\r\n\r\n\r\n\r\n");
		Assert.assertTrue(testOneType_ListConv(DataListTypeEnum.P011_VIA_P021, Conversion.NETCDF, E_TypeConversion.NETCDF_MULTISTATIONS));
	}

	@Test
	public final void test_P011viaP021_NETCDFMONO() {
		testLogger.info("\r\n\r\n\r\n\r\n__________________________test_P011viaP021_NETCDFMONO_____________________________\r\n\r\n\r\n\r\n");
		Assert.assertTrue(testOneType_ListConv(DataListTypeEnum.P011_VIA_P021, Conversion.NETCDF, E_TypeConversion.NETCDF_MONOSTATION));
	}







	/**
	 * ================================================================= methodes =================================================================
	 */
//	@Test
	public void cleanDirectories() {

		testLogger.info("dataRootPath " + dataRootPath + "--------");
		File root = new File(dataRootPath);
		for (File testDir : root.listFiles()) {
			System.out.println("testDir: " + testDir.toString());
			if (testDir.isDirectory()) {
				for (File paramListDir : testDir.listFiles()) {
					if (paramListDir.isFile() && paramListDir.getName().equalsIgnoreCase("PF_INPUT")) {
						paramListDir.delete();
					} else if (paramListDir.isDirectory()) {
						if (paramListDir.getName().equalsIgnoreCase("PF_INPUT")) {
							paramListDir.delete();
						} else {
							for (File convDir : paramListDir.listFiles()) {
								if (convDir.isDirectory() && 
										(convDir.getName().equals(E_TypeConversion.MEDATLAS.toString()) 
												|| convDir.getName().equals(E_TypeConversion.ODV_MONOSTATION.toString()) 
												|| convDir.getName().equals(E_TypeConversion.ODV_MULTISTATIONS.toString()) 
												|| convDir.getName().equals(E_TypeConversion.NETCDF_MONOSTATION.toString()) 
												|| convDir.getName().equals(E_TypeConversion.NETCDF_MULTISTATIONS.toString()))) {

									for (File unitTest : convDir.listFiles()) {
										if (unitTest.listFiles() == null) {
											testLogger.error("BAD DIRECTORY ORGANISATION!!!!!!!!! " + unitTest.getAbsolutePath());
										} else {
											for (File f : unitTest.listFiles()) {
												if (f.getName().equals(outputName)) {
													testLogger.info("delete file " + f);
													f.delete();
												} else if (f.getName().equals("cdiSummary_junit")) {
													testLogger.info("delete file " + f);
													f.delete();
												} else if (f.getName().equals("pf_input")) {
													testLogger.info("delete file " + f);
													f.delete();

												}
												else if (f.getName().equals("output_junit")) {
//													testLogger.info("delete file " + f);
//													f.delete();

												}
											}
										}

									}
								}
							}
						}
					}
				}
			}
		}
	}

	public boolean testOneType_ListConv(DataListTypeEnum dataList, Conversion conversion, E_TypeConversion convType) {

		boolean ok = true;
		boolean res;
		boolean comp = false;
		int cptTests = 0;
		int cptFail = 0;
		boolean testFound;
		if (testDirAlone.length > 0) {
			testLogger.warn("\n\nWARNING: TEST ONLY DIRECTORY set in testDirAlone\n\n");
		}
		if (REPLACE) {
			testLogger.warn("\n\nWARNING: REFERENCES WILL BE REPLACED!!!\n\n");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// testLogger.info("dataRootPath" + dataRootPath );
		File root = new File(dataRootPath);
		for (File testDir : root.listFiles()) {
			if (isToTest(testDir.getName())) {
				testLogger.info("___ testDir: " + testDir.toString());
				message = new Message(testLogger);
				message.dirName = testDir.getName();
				testFound = false;

				for (File paramListDir : testDir.listFiles()) {

					if (paramListDir.getName().equalsIgnoreCase(dataList.toString())) {

						for (File convDir : paramListDir.listFiles()) {

							if (convDir.getName().equalsIgnoreCase(convType.toString())) {

								for (File unitTest : convDir.listFiles()) {
									ok = true;
									boolean okCDI;
									message = new Message(testLogger);

									message.dirName = testDir.getName();
									message.convType = convType.toString();
									message.parameterList = paramListDir.getName();
									if (unitTest.getName().startsWith("t")) {
										res = false;
										comp = false;
										message.testName = unitTest.getName();
										String inputAbsolutePath = testDir.getAbsolutePath() + File.separator + inputName;
										String outputAbsolutePath = unitTest.getAbsolutePath();
										cleanCDISummary(outputAbsolutePath);
										cptTests++;
										nbTotalTests++;
										testFound = true;
										message.isTestFound = true;

										message.isReferenceFound = fileExist(outputAbsolutePath, outputReferenceName);
										testLogger.info(">>>>>>>>>>>>>>>>>> process " + message.dirName + " " + message.testName);
										try {
											long t0 = System.currentTimeMillis();
											message = convertWithNemoBatch(message, conversion, convType, inputAbsolutePath, outputAbsolutePath, !unitTest.getName().startsWith("tj"), false);
											res= message.conversionRes;
											long t1 = System.currentTimeMillis();
											testLogger.info("time: " + (t1 - t0));
											message.processTime = String.valueOf(((t1 - t0) / 1000));
										} catch (Exception e) {
											testLogger.error(e.getMessage());
										}
										ok = res && ok;
										if (res) {
											comp = compareWithReference(outputAbsolutePath, outputName, conversion);
											message.batchCompRes = comp;
											ok = comp && ok ;
										}
										if (ok) {
											okCDI = compareCdiSummary(outputAbsolutePath);
											if (!okCDI) {
												message.diff = "error in CDI summary";
											}
											ok = okCDI && ok;
										}
										if (ok) {
											nbTotalSuccess++;
										}
										if (!ok) {
											cptFail++;
											nbTotalFailed++;
										}


										cleanCDISummary(outputAbsolutePath);
										if (message.testName != null) {
											writeInResultsFile(message);
										}
									} else if (unitTest.getName().startsWith("_t")) {
										message.testName = paramListDir.getName() + " " + convDir.getName() + " " + unitTest.getName();
										message.isActivated = false;
									}

									message.print();

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

	private void cleanCDISummary(String outputAbsolutePath) {
		if (REMOVE_JUNIT) {
			File cdi = new File(outputAbsolutePath + File.separator + "cdiSummary_junit");
			try {
				cdi.delete();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

	}

	private boolean compareCdiSummary(String outputAbsolutePath) {
		BufferedReader bufOut = null;
		BufferedReader bufRef = null;
		String outputLine = "";
		String refFileLine;
		String outputTest = outputAbsolutePath + File.separator + "cdiSummary_junit";
		String output_REFERENCE = outputAbsolutePath + File.separator + "cdiSummary_REFERENCE";
		;
		// new File(output_REFERENCE).delete();
		// move junit to ref as nc
		// testLogger.warn("rename " +outputTest + " --> " +output_REFERENCE);
		// new File(outputTest).renameTo(new File(output_REFERENCE));
		boolean identical = true;

		try {
			bufOut = new BufferedReader(new FileReader(outputTest));
		} catch (FileNotFoundException e1) {
			testLogger.error("CDISummary junit file not found");
			return false;
		}
		try {
			bufRef = new BufferedReader(new FileReader(output_REFERENCE));
		} catch (FileNotFoundException e1) {
			testLogger.error("CDISummary reference file not found");
			return false;
		}

		try {
			String newRefLine = "";
			if (bufOut != null && bufRef != null) {
				while (bufRef.ready() && bufOut.ready() && identical) {
					outputLine = bufOut.readLine();
					refFileLine = bufRef.readLine();
					String format = "";

					identical = true;// outputLine.equals(refFileLine);
					String[] splitOutput = outputLine.split("\t");
					String[] splitRef = refFileLine.split("\t");
					for (int i = 0; i < splitRef.length; i++) {
						if (i != 5 && i != 20) {// col 5 = generation date -> changes
							boolean subidentical = splitOutput[i].equals(splitRef[i]);
							if (!subidentical) {
								testLogger.error("cdi diff col. " + i + 1 + ": out: " + splitOutput[i] + "!= ref: " + splitRef[i]);
								testLogger.error("CDI REF >>  " + refFileLine);
								testLogger.error("CDI OUT >>  " + outputLine);
								message.diff += "#REF# : " + refFileLine + "#OUT# : " + outputLine;
								identical = false;
							}
						}
					}


					if (!identical) {
						testLogger.error("CDI REF >>  " + refFileLine);
						testLogger.error("CDI OUT >>  " + outputLine);
						testLogger.error("meld " + outputTest + " " + output_REFERENCE);
						testLogger.error("code --diff " + outputTest + " " + output_REFERENCE);
						 
						message.diff += "#REF# : " + refFileLine + "#OUT# : " + outputLine;
					}

				}
				// normalement les 2 fichier doivent etre a la fin si identique.
				if (bufRef.ready() || bufOut.ready()) {
					identical = false;
				}
			}

			if (!identical && REPLACE) {
				// delete ref nc
				testLogger.warn("delete "+ output_REFERENCE);
				// move junit to ref as nc
				testLogger.warn("rename " + outputTest + " --> " + output_REFERENCE);
				new File(outputTest).renameTo(new File(output_REFERENCE));
				// delete junit result files
				new File(outputTest).delete();
			}
			if (REMOVE_JUNIT) {
				File ft=new File(outputTest); 
				if ( ft.isFile()) {
				// delete junit result files
				 ft.delete();
				}else {
					
					for (File f : ft.listFiles()) {
						if ( f.isFile()) {
							// delete junit result files
							 f.delete();
						}else {
							for (File fsub : f.listFiles()) {
								fsub.delete();
							}
						}
					}
				}
			}
			message.batchCdiSummaryCompRes=identical;
			return identical;
		} catch (Exception e) {
			testLogger.error("CDISummary comparison failed");
			message.batchCdiSummaryCompRes=false;
			return false;
		}

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


	public boolean fileExist(String outputAbsolutePath, String outputTestName) {
		File f = new File(outputAbsolutePath + File.separator + outputTestName);
		return f.exists();
	}


	private boolean compareWithReference( String outputAbsolutePath, String outputTestName, Conversion conv) {
		boolean currentComp=true;
		File ref = new File(outputAbsolutePath + "/" + outputReferenceName);
		File test = new File(outputAbsolutePath + "/" + outputTestName);

		if (conv == Conversion.NETCDF) {

			if (test.isDirectory()) {
				if (!ref.exists() || ref.listFiles().length == 0) {
					message.isReferenceFound = false;
					return false;
				}
				for (File f : test.listFiles()) {
					if (f.isDirectory()) {
						for (File ff : f.listFiles()) {
							if (ff.getName().endsWith(".nc")) { // ignore dumps
								currentComp &= compareCFPoint(ff.getAbsolutePath(), ff.getAbsolutePath().replace("junit", "REFERENCE"));
							}
						}
					} else {
						if (f.getName().endsWith(".nc")) { // ignore dumps
							currentComp &= compareCFPoint(f.getAbsolutePath(), outputAbsolutePath + File.separator + outputReferenceName + File.separator + f.getName());
						}
					}
				}
			} else {
				if (!outputTestName.endsWith(".dump")) { // ignore dumps
					currentComp &= compareCFPoint(outputAbsolutePath + File.separator + outputTestName, outputAbsolutePath + File.separator + outputReferenceName);
				}
			}
		} else {

			if (test.isDirectory()) {
				if (!ref.exists() || ref.listFiles().length == 0) {
					message.isReferenceFound = false;
					return false;
				}

				for (File f : test.listFiles()) {
					if (f.isDirectory()) {
						for (File ff : f.listFiles()) {
							currentComp &= compare_____TwoFiles(ff.getAbsolutePath(), ff.getAbsolutePath().replace("junit", "REFERENCE")

									);
						}
					} else {
						currentComp &= compare_____TwoFiles(f.getAbsolutePath(), outputAbsolutePath + File.separator + outputReferenceName + File.separator + f.getName()

								);
					}
				}
			} else {
				currentComp = compare_____TwoFiles(outputAbsolutePath + File.separator + outputTestName, outputAbsolutePath + File.separator + outputReferenceName);
			}
		}
		return currentComp;
	}

	private boolean compareCFPoint(String outputTestName, String outputNCReferenceName) {

		NetcdfFile ref = null;
		NetcdfFile junit = null;
		String refPath = outputNCReferenceName;
		String junitPath = outputTestName;

		Formatter formatter = new Formatter();

		message.batchCompRes = true;
		boolean specificReplace = false;

		if (new File(refPath).exists()) {
			message.isReferenceFound = true;
		} else {
			message.isReferenceFound = false;
			testLogger.error("reference file not found " + refPath);
			return false;
		}
		if (new File(junitPath).exists()) {

		} else {
			testLogger.error("junit file not found " + junitPath);
			return false;
		}

		try {
			message.octopusCheck= message.octopusCheck&&checkData(junitPath);
		} catch (CFPointException | OdvException | MedatlasReaderException e1) {
			message.octopusCheck=false;
		}


		try {
			ref = NetcdfFile.open(refPath);
			junit = NetcdfFile.open(junitPath);
		} catch (IOException e) {
			// e.printStackTrace();
			testLogger.error("NOT A VALID NETCDF FILE!");
			message.isReferenceFound = false;
			return false;
		}

		CompareNetcdf2 comparator = new CompareNetcdf2();
		comparator.compareFiles(ref, junit, formatter);

		String file1 = "", file2 = "";
		String expectedTitle = ".* title = \"SeaDataNet CFPoint .*";
		// String expectedTitle= "\\*\\* ucar.nc2.Attribute title = \"NetCDF .* - Generated by MedSDN2CFPOINT, version .*";
		for (String diffEntry : formatter.toString().split("\n")) {
			// testLogger.info(diffEntry);
			// get files paths
			if (diffEntry.trim().startsWith("First file =")) {
				file1 = diffEntry;
			}
			if (diffEntry.trim().startsWith("Second file =")) {
				file2 = diffEntry;
			}

			if (diffEntry.trim().startsWith("** ")) {
				//				if (Pattern.matches(expectedTitle, diffEntry.trim())) {
				if (diffEntry.contains("title = \"SeaDataNet CFPoint")){
					testLogger.info("title" + diffEntry);
				} else if (diffEntry.trim().startsWith("** global: :date_update = ")) {
					// normal: generation date = today
				
				} else {
					// System.out.println(diffEntry);
					testLogger.error(diffEntry);
					message.diff += diffEntry;
					message.batchCompRes = false;
					testLogger.error(diffEntry);

				}
			}
		}
		if (message.batchCompRes) {
			try {
				List<Variable> refVars = ref.getVariables();
				List<Variable> junitVars = junit.getVariables();
				HashMap<String, Variable> refVarsMap = new HashMap<String, Variable>();
				HashMap<String, Variable> junitVarsMap = new HashMap<String, Variable>();

				for (Variable var : refVars) {
					refVarsMap.put(var.getName(), var);
				}
				for (Variable var : junitVars) {
					junitVarsMap.put(var.getName(), var);
				}
				for (Variable var : refVars) {

					Array aVar = refVarsMap.get(var.getName()).read();
					Array bVar = junitVarsMap.get(var.getName()).read();
					if (!aVar.toString().equals(bVar.toString())) {
						if (var.getName() .equals("SDN_XLINK")) {
							testLogger.warn("VAR " + var.getName() + " data are different");
							specificReplace=true;
						}else {
						message.batchCompRes = false;
						testLogger.error("VAR " + var.getName() + " data are different");
						message.diff += System.lineSeparator() + "VAR " + var.getName() + " data are different";
						}

					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/**
		 * REPLACE REFERENCES and/or remove test files ================================================
		 */
		// set REPLACE to true to replace reference files by junit generated files if difference found
		// WARNING!!! be sure that the generated files are correct and that you want to replace REFERENCE with them!!!
		if (!message.batchCompRes) {
			String commande = "ncdump " + outputTestName + ">" + outputTestName + ".dump;" + "ncdump " + outputNCReferenceName + ">" + outputNCReferenceName + ".dump; " + "meld " + outputTestName + ".dump" + " " + outputNCReferenceName + ".dump";
			System.out.println(commande);
			commande = "ncdump " + outputTestName + " > " + outputTestName + ".dump;" + "ncdump " + outputNCReferenceName + " > " + outputNCReferenceName + ".dump; " + "code --diff " + outputTestName + ".dump" + " " + outputNCReferenceName + ".dump";
			System.out.println(commande);
			System.out.println("");
			System.out.println("cp " + outputTestName + " " + outputNCReferenceName);
			System.out.println("xcopy /Y "	+ outputTestName + " "+ outputNCReferenceName);
		}
		if (message.batchCompRes && specificReplace && REPLACE && message.octopusCheck) {
			// delete ref nc
			// testLogger.warn("delete "+ refPath);
			// new File(refPath).delete();
			// testLogger.warn("rename " +refPath + " --> " +refPath+".old");
			// new File(refPath).renameTo(new File(refPath+".old"));
			// move junit to ref as nc
			testLogger.warn("rename " + junitPath + " --> " + refPath);
			new File(junitPath).renameTo(new File(refPath));
			testLogger.warn("delete " + junitPath);
			// delete junit result files
			new File(junitPath).delete();
		}
		if (REMOVE_JUNIT) {
			// delete junit result files
			//			testLogger.warn("delete " + junitPath);
			new File(junitPath).delete();
		}
		return message.batchCompRes;

	}

	public boolean compare_____TwoFiles(String fileA, String fileB) {

		BufferedReader bufOut = null;
		BufferedReader bufRef = null;
		String outputLine = "";
		String refFileLine;
		String outputTest = fileA;
		String output_REFERENCE = fileB;


		if (!new File(fileB).exists()) {
			testLogger.error(fileA+" REFERENCE file not found");
			message.isReferenceFound = false;
			return false;
		}

		boolean identical = true;
		boolean specificReplace = false;

		try {
			bufOut = new BufferedReader(new FileReader(outputTest));
		} catch (FileNotFoundException e1) {
			testLogger.error(fileA+" REFERENCE file not found");
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
		try {
			message.octopusCheck= message.octopusCheck && checkData(outputTest);
		} catch (CFPointException | OdvException | MedatlasReaderException e1) {
			message.octopusCheck=false;
		}
		// message.filesMap.put(fileA, true);
		// message.filesMap.put(fileB, true);
		try {
			String newRefLine = "";
			if (bufOut != null && bufRef != null) {
				while (bufRef.ready() && bufOut.ready() && identical) {
					outputLine = bufOut.readLine();
					refFileLine = bufRef.readLine();
					String format = "";
					if (refFileLine.contains("Generated by Nemo") && outputLine.contains("Generated by Nemo")) {
						identical = true;
					} else {
						identical = outputLine.equals(refFileLine);
					}
					Pattern p;
					if (!identical) {
						testLogger.error("REF >>  " + refFileLine);
						testLogger.error("OUT >>  " + outputLine);
						message.diff += "#REF# : " + refFileLine + "#OUT# : " + outputLine;
					}

				}
				// normalement les 2 fichier doivent etre a la fin si identique.
				if (bufRef.ready() || bufOut.ready()) {
					identical = false;
				}
			}
			if (!identical) {
				System.out.println("meld " + fileA + " " + fileB);
				System.out.println("code --diff " + fileA + " " + fileB);
				System.out.println("cp " + fileA + " " + fileB);
				System.out.println("xcopy /Y "	+ fileA + " "+ fileB);
			}

			/**
			 * REPLACE REFERENCES and/or remove test files ================================================
			 */
			// set REPLACE to true to replace reference files by junit generated files if difference found
			// WARNING!!! be sure that the generated files are correct and that you want to replace REFERENCE with them!!!

			// String refPath = outputDir.getAbsolutePath()+File.separator+outputRefFileName;
			if (identical && message.octopusCheck &&REPLACE) {
				// delete ref nc
				testLogger.warn("delete " + output_REFERENCE);
				new File(output_REFERENCE).delete();
				// move junit to ref as nc
				testLogger.warn("rename " + outputTest + " --> " + output_REFERENCE);
				new File(outputTest).renameTo(new File(output_REFERENCE));

			}
			if (REMOVE_JUNIT) {
				// delete junit result files
				testLogger.warn("delete " + outputTest);
				new File(outputTest).delete();
			}

			return identical;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			testLogger.error("IOException");
			return false;
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			testLogger.error("NullPointerException");
			return false;
		}
	}





}

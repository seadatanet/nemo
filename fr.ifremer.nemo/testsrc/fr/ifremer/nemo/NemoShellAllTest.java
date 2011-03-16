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
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;
import fr.ifremer.nemo.util.Global.E_TypeConversion;

/**
 * Description: classe de tests
 * - generation auto en appelant le mode batch
 * - comparaison avec des fichiers de référence
 * 
 * 
 * Structure des repertoires:
 * dataRootPath/xxxx/versionDirName/dataList/conversion/tyyy
 * 
 * où
 * - xxx est un nom representant le type de données (ex: diapalis,...) - choix
 * libre
 * - yyy est un nom quelconque, nb de char quelconque
 * 
 * pour xxx: si le repertoire commence par "_", il est ignoré
 * pour tyyy: si ce répertoire commence par "t", il est considéré comme un test.
 * S'il commence par "_", il est considéré comme un test désactivé.
 * S'il commence par un autre caractère, il est ignoré.
 * 
 * Pour ne lancer les tests que sur un répertoire, utiliser testDirAlone
 * ex: public static String[] testDirAlone={"cruiseFile_trajectory_6959"}; ne
 * lancera les tests que sur cruiseFile_trajectory_6959
 * 
 * utiliser
 * - EDMO code = 486
 * - time ODV = ISO
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * @author sbregent
 * 
 */
public class NemoShellAllTest
{

	public String dataRootPath = "C:\\tests_nemo";

	// générer les fichiers en batch et les comparer aux fichiers REFERENCE
	public static boolean testBatchs = true;
	// comparer les fichiers manuels (output_eclipse) aux fichiers REFERENCE
	public static boolean compareManualFiles = true;

	// public static String[] testDirAlone =
	// {};
	public static String[] testDirAlone =
		{ "7019" };
	// public static String[] testDirAlone =
	// { "cruiseDir_timeseries_jade97" };

	public String inputName = "input";
	public String outputName = "output_junit";
	public static String outputManualName = "output_junit_batch";
	public String modelName = "model.xml";
	public String outputReferenceName = "output_REFERENCE";
	private static String batchCommands;

	public NemoShell shell = new NemoShell();

	// public String inputAbsolutePath;
	// public String outputAbsolutePath;

	public boolean sortDatas = false;

	// public Conversion conv=Conversion.MEDATLAS;
	// public E_TypeConversion odvConv= E_TypeConversion.MEDATLAS;

	public String datasetRef = Global.EMPTY_STRING;
	public String cdiSummaryFile = Global.EMPTY_STRING;
	public boolean couplingTableUpdate = true;
	public String couplingTablePrefix = Global.EMPTY_STRING;
	public static Logger batchLogger;
	public static Logger testLogger;

	public static int nbTotalTests = 0;
	public static int nbTotalFailed = 0;
	public static int nbTotalSuccess = 0;

	public Message message;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		DOMConfigurator.configure("resources/log4jBatch.xml"); //$NON-NLS-1$
		batchLogger = Logger.getLogger("nemoBatch");//$NON-NLS-1$
		batchLogger.setLevel(Level.DEBUG);
		// LOGGER
		DOMConfigurator.configure("resources/log4jBatch.xml"); //$NON-NLS-1$
		testLogger = Logger.getLogger("nemoJunits");//$NON-NLS-1$
		testLogger.setLevel(Level.DEBUG);
		nbTotalTests = 0;
		nbTotalFailed = 0;
		nbTotalSuccess = 0;
		Date actuelle = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String dat = dateFormat.format(actuelle);

		testLogger.info("\r\n\r\n\r\n\r\n===================================== " + dat + " =========================\r\n\r\n\r\n\r\n");
		testLogger.info("test batchs : " + testBatchs);
		testLogger.info("comp manual files : " + compareManualFiles + "(" + outputManualName + ")");

	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{

		testLogger.info("\r\n\r\n"
				+ "TOTAL ====================================================================================="
				+ "\r\n"
				+ nbTotalSuccess + " tests succeed on " + nbTotalTests
				+ "\r\n"
				+ nbTotalFailed + " tests failed");

		testLogger.info("batchCommands" + Global.RN);
		testLogger.info(batchCommands);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{

		sortDatas = false;
		datasetRef = Global.EMPTY_STRING;
		cdiSummaryFile = Global.EMPTY_STRING;
		couplingTableUpdate = true;
		couplingTablePrefix = Global.EMPTY_STRING;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{

	}

	/**
	 * =================================================================
	 * MEDATLAS
	 * =================================================================
	 */
	@Test
	public final void test_P091_Medatlas()
	{
		testLogger.info("\r\n\r\n\r\n\r\n___________________________test_P091_Medatlas_____________________________\r\n\r\n\r\n\r\n");
		boolean res = testOneType_ListConv(DataListTypeEnum.P091, Conversion.MEDATLAS, E_TypeConversion.MEDATLAS);
		Assert.assertTrue(res);
	}

	@Test
	public final void test_P011viaP091_Medatlas()
	{
		testLogger.info("\r\n\r\n\r\n\r\n___________________________test_P011viaP091_Medatlas_____________________________\r\n\r\n\r\n\r\n");
		Assert.assertTrue(testOneType_ListConv(DataListTypeEnum.P011_VIA_P091, Conversion.MEDATLAS, E_TypeConversion.MEDATLAS));
	}

	/**
	 * ================================================================= ODV
	 * =================================================================
	 */
	@Test
	public final void test_P091_ODVMULTI()
	{
		testLogger.info("\r\n\r\n\r\n\r\n____________________________test_P091_ODVMULTI____________________________\r\n\r\n\r\n\r\n");
		Assert.assertTrue(testOneType_ListConv(DataListTypeEnum.P091, Conversion.ODV, E_TypeConversion.ODV_MULTISTATIONS));
	}

	@Test
	public final void test_P011viaP091_ODVMULTI()
	{
		testLogger.info("\r\n\r\n\r\n\r\n____________________________test_P011viaP091_ODVMULTI____________________________\r\n\r\n\r\n\r\n");
		Assert.assertTrue(testOneType_ListConv(DataListTypeEnum.P011_VIA_P091, Conversion.ODV, E_TypeConversion.ODV_MULTISTATIONS));
	}

	@Test
	public final void test_P011viaP021_ODVMULTI()
	{
		testLogger.info("\r\n\r\n\r\n\r\n__________________________test_P011viaP021_ODVMULTI_____________________________\r\n\r\n\r\n\r\n");
		Assert.assertTrue(testOneType_ListConv(DataListTypeEnum.P011_VIA_P021, Conversion.ODV, E_TypeConversion.ODV_MULTISTATIONS));
	}

	/**
	 * =================================================================
	 * methodes
	 * =================================================================
	 */
	public boolean testOneType_ListConv(DataListTypeEnum dataList, Conversion conversion, E_TypeConversion odvConv)
	{

		boolean ok = true;
		boolean res;
		boolean comp = false;
		int cptTests = 0;
		int cptFail = 0;
		boolean testFound;

		File root = new File(dataRootPath);
		for (File testDir : root.listFiles())
		{
			if (isToTest(testDir.getName()))
			{
				message = new Message();
				message.dirName = testDir.getName();
				testFound = false;
				for (File paramListDir : testDir.listFiles())
				{
					if (paramListDir.getName().equals(dataList.toString()))
					{

						for (File convDir : paramListDir.listFiles())
						{
							if (convDir.getName().equals(odvConv.toString()))
							{
								for (File unitTest : convDir.listFiles())
								{
									ok = true;
									message = new Message();
									message.dirName = testDir.getName();
									if (unitTest.getName().startsWith("t"))
									{
										res = false;
										comp = false;
										message.testName = paramListDir.getName() + " " + convDir.getName() + " " + unitTest.getName();
										String inputAbsolutePath = testDir.getAbsolutePath() + "\\" + inputName;
										String outputAbsolutePath = unitTest.getAbsolutePath();
										cptTests++;
										nbTotalTests++;
										testFound = true;
										message.isTestFound = true;
										if (testBatchs)
										{
											message.isReferenceFound = fileExist(outputAbsolutePath, outputReferenceName);
											res = convertWithNemoBatch(conversion, odvConv, inputAbsolutePath, outputAbsolutePath);
											ok = res && ok;
											if (res)
											{

												comp = compareWithReference(outputAbsolutePath, outputName);
												message.batchCompRes = comp;
												ok = comp && ok;
											}
											if (ok)
											{
												nbTotalSuccess++;
											}
											if (!ok)
											{
												cptFail++;
												nbTotalFailed++;
											}

										}

										if (compareManualFiles)
										{
											message.isManualFound = fileExist(outputAbsolutePath, outputManualName);
											if (message.isManualFound)
											{
												comp = compareWithReference(outputAbsolutePath, outputManualName);
												message.manualCompRes = comp;
												message.manualCompName = outputManualName;
												if (!comp)
												{
													System.out.println("lllll");
												}
												ok = comp && ok;
											}
										}

									} else if (unitTest.getName().startsWith("_t"))
									{
										message.testName = paramListDir.getName() + " " + convDir.getName() + " " + unitTest.getName();
										message.isActivated = false;
									}
									if (!testFound)
									{
										message.isTestFound = false;
									} else
									{

										message.print();
									}
								}

							}
						}

					}

				}
				if (!testFound)
				{
					message.print();
				}

			}
		}
		testLogger.info("\r\n========================================="
				+ cptFail + " failed /" + cptTests + " tested"
				+ "========================================="
				+ "\r\n"
				);

		// if (!ok)
		// {
		// System.out.println("ici");
		// }
		return nbTotalFailed == 0;
	}

	public boolean isToTest(String dirName)
	{
		boolean res = true;
		if (dirName.startsWith("_") || dirName.startsWith("."))
		{
			res = false;
		} else if (testDirAlone.length > 0)
		{
			res = false;
			for (String oneDir : testDirAlone)
			{
				if (dirName.equals(oneDir))
				{
					res = true;
				}
			}
		}
		return res;
	}

	public boolean convertWithNemoBatch(Conversion conversion, E_TypeConversion odvConv, String inputAbsolutePath, String outputAbsolutePath)
	{
		boolean res = false;
		String output = outputAbsolutePath + "\\" + outputName;
		String model = outputAbsolutePath + "\\" + modelName;
		couplingTablePrefix = outputAbsolutePath;
		couplingTableUpdate = true;
		cdiSummaryFile = outputAbsolutePath + "\\" + "cdiSummary_junit";
		File mod = new File(model);
		Nemo.getInstance().resetInstance();
		shell = new NemoShell();
		String batchCmd = "";
		if (!mod.exists())
		{
			message.isModelFound = false;
		} else
		{
			message.isModelFound = true;
			Nemo.getInstance().setBatchMode(true);
			try
			{
				res = shell.main(inputAbsolutePath, output, model,
						conversion, sortDatas, odvConv, datasetRef,
						cdiSummaryFile,
						couplingTableUpdate, couplingTablePrefix,
						batchLogger);
			} catch (Exception e)
			{
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
		if (E_TypeConversion.ODV_MULTISTATIONS == odvConv)
		{
			batchCmd += Global.SPACE + Global.MOINS + Global.optOdvMulti;
		}
		batchCmd += Global.SPACE + Global.MOINS + Global.optCdiSummary;
		batchCmd += Global.SPACE + cdiSummaryFile + "_batch";

		if (couplingTableUpdate)
		{
			batchCmd += Global.SPACE + Global.MOINS + Global.optCouplingTableUpdate;
			batchCmd += Global.SPACE + Global.MOINS + Global.optCouplingTablePrefix;
			batchCmd += Global.SPACE + couplingTablePrefix;
		}
		batchCommands += Global.RN + batchCmd;

		message.conversionRes = res;
		return res;
	}

	public boolean fileExist(String outputAbsolutePath, String outputTestName)
	{
		File f = new File(outputAbsolutePath + "\\" + outputTestName);
		return f.exists();
	}

	public boolean compareWithReference(String outputAbsolutePath, String outputTestName)
	{
		boolean comp = false;
		comp = compare_____TwoFiles(outputAbsolutePath, outputTestName, outputReferenceName);
		return comp;
	}

	public boolean compare_____TwoFiles(String outputAbsolutePath, String fileA, String fileB)
	{

		BufferedReader bufOut = null;
		BufferedReader bufRef = null;
		String outputLine = "";
		String refFileLine;
		String outputTest = outputAbsolutePath + "\\" + fileA;
		String output_REFERENCE = outputAbsolutePath + "\\" + fileB;
		boolean identical = true;

		try
		{
			bufOut = new BufferedReader(new FileReader(outputTest));
		} catch (FileNotFoundException e1)
		{
			// testLogger.error(fileA+" file not found");
			// message.filesMap.put(fileA, false);
			return false;
		}
		try
		{
			bufRef = new BufferedReader(new FileReader(output_REFERENCE));
		} catch (FileNotFoundException e1)
		{
			// testLogger.error(fileB+" file not found");
			// message.filesMap.put(fileB, false);
			return false;
		}

		// message.filesMap.put(fileA, true);
		// message.filesMap.put(fileB, true);
		try
		{
			if (bufOut != null && bufRef != null)
			{
				while (bufOut.ready() && identical)
				{
					outputLine = bufOut.readLine();
					refFileLine = bufRef.readLine();
					String format = "";
					;
					String out = String.format(Locale.FRENCH, format, outputLine);
					String ref = String.format(Locale.FRENCH, format, refFileLine);
					identical = outputLine.equals(refFileLine);
				}
			}
			return identical;

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			testLogger.error("IOException");
			return false;
		}
	}

	private String completeTitle(String title)
	{
		String res = "";
		if (title.length() < 70)
		{
			for (int i = 0; i < 10; i++)
			{
				res += "_";
			}
			res += title;

			for (int i = 0; i < 60 - title.length(); i++)
			{
				res += "_";
			}
		} else
		{
			res = title;
		}
		return res;
	}

	public class Message
	{
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

		// public HashMap<String, Boolean> filesMap = new HashMap<String,
		// Boolean>();
		public Message()
		{

		}

		public void print()
		{
			String mes = "";
			mes += completeTitle(dirName);
			if (testName != null)
			{
				mes += "\r\n" + testName;
				if (!isActivated)
				{
					mes += ("- DESACTIVATED -");
					write(mes);
					return;
				}
				if (!isTestFound)
				{
					mes += ("- No test found -");
					write(mes);
					return;
				}
				if (!isModelFound)
				{
					mes += ("- KO No model found -");
					write(mes);
					return;
				}
				if (!conversionRes)
				{
					mes += "\r\nconversion: " + getString(conversionRes);
					mes += " " + conversionError;
				}
				if (testBatchs)
				{
					if (isReferenceFound)
					{
						if (batchCompRes)
						{
							mes += " " + getString(conversionRes);
						} else
						{
							mes += "\r\nbatch comparison: " + getString(batchCompRes);
						}
					} else
					{
						mes += "\r\nReference file not found";
					}
				}
				if (compareManualFiles && isManualFound)
				{
					if (isReferenceFound)
					{
						mes += "\r\nmanual comparison: " + getString(manualCompRes);
					} else
					{
						mes += "\r\nReference file not found";
					}

				}
				if (!(batchCompRes && conversionRes))
				{
					write(mes);
				}
			} else
			{
				// mes += " no test found";
				// write(mes);
			}

		}

		private String getString(boolean res)
		{
			String str = "";
			if (res)
			{
				str = "OK";
			} else
			{
				str = "KO";
			}
			return str;
		}

		private void write(String mes)
		{
			testLogger.info(mes + "\r\n");
		}
	}

}

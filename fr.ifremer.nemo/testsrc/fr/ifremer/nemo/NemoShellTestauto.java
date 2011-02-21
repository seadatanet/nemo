/* ============================================================================
 * Nom du fichier	: NemoShellTest.java
 * Auteur			: sbregent
 * Date				: 4 févr. 11
 * Copyright		: Altran
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
 * - xxx est un nom representant le type de données (ex: diapalis,...) - choix libre
 * - yyy est un nom quelconque, nb de char quelconque
 * 
 * pour xxx: si le repertoire commence par "_", il est ignoré
 * pour tyyy: si ce répertoire commence par "t", il est considéré comme un test. S'il commence par "_", il est considéré comme un test désactivé.
 * S'il commence par un autre caractère, il est ignoré.
 * 
 * Pour ne lancer les tests que sur un répertoire, utiliser testDirAlone
 * ex: public static String[] testDirAlone={"cruiseFile_trajectory_6959"}; ne lancera les tests que sur cruiseFile_trajectory_6959
 * 
 * utiliser 
 *  - EDMO code = 486
 *  - time ODV = ISO
 *  
 * 
 * @author sbregent
 *
 */
public class NemoShellTestauto
{
    
    public String dataRootPath="C:\\tests_nemo";

    
    // générer les fichiers en batch et les comparer aux fichiers REFERENCE
    boolean testBatchs=true;
    // comparer les fichiers manuels (output_eclipse) aux fichiers REFERENCE
    boolean compareManualFiles=true;
    
    
    public static String[] testDirAlone={};
//   public static String[] testDirAlone={"carotte_sed"};
//  public static String[] testDirAlone={"smatch_timeseries"}; 
//  public static String[] testDirAlone={"cruiseDir_profile_diap"}; 
//    public static String[] testDirAlone={"cruiseFile_profile_diap"};
//  public static String[] testDirAlone={"michele_tsg_trajectory"}; 
//  public static String[] testDirAlone={"cruiseDir_profile_diap_avec stHeaderDiffDansFichiersInput"}; 
//  public static String[] testDirAlone={"cruiseFile_trajectory_6959"}; 
//  public static String[] testDirAlone={"microdyn"}; 

    public String inputName="input";
    public String outputName="output_junit";
    public String outputManualName="output_michele";
    public String modelName="model.xml";
    public String outputReferenceName= "output_REFERENCE";

    public NemoShell shell = new NemoShell();

//    public String inputAbsolutePath;
//    public String outputAbsolutePath;


    public boolean sortDatas=false;

//    public Conversion conv=Conversion.MEDATLAS;
//    public E_TypeConversion odvConv= E_TypeConversion.MEDATLAS;

    public String datasetRef=Global.EMPTY_STRING;
    public String cdiSummaryFile=Global.EMPTY_STRING;
    public boolean couplingTableUpdate=false;
    public String couplingTablePrefix=Global.EMPTY_STRING;
    public static Logger batchLogger;
    public static Logger testLogger ;
    
    public static int nbTotalTests=0;
    public static int nbTotalFailed=0;
    public static int nbTotalSuccess=0;


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
        nbTotalTests=0;
        nbTotalFailed=0;
        nbTotalSuccess=0;
        Date actuelle = new Date();
         DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
         String dat = dateFormat.format(actuelle);
         
         testLogger.info(" ");
         testLogger.info(" ");
         testLogger.info(" ");
         testLogger.info(" ");
         testLogger.info(" ");
         testLogger.info("================================================================================");
         testLogger.info("================================================================================");
         testLogger.info("================================================================================");
         testLogger.info("================================================================================");
         testLogger.info("================================================================================");
         testLogger.info("================================================================================");
        testLogger.info("===================================== "+dat+" =========================");
        testLogger.info("================================================================================");
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
        
        testLogger.info("TOTAL =====================================================================================");
        testLogger.info(nbTotalSuccess+ " tests succeed on " + nbTotalTests);
        testLogger.info(nbTotalFailed + " tests failed");
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {

        sortDatas=false;
        datasetRef=Global.EMPTY_STRING;
        cdiSummaryFile=Global.EMPTY_STRING;
        couplingTableUpdate=false;
        couplingTablePrefix=Global.EMPTY_STRING;
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
        decoA_______________();
        testLogger.info("___________________________test_P091_Medatlas_____________________________");
        decoP_______________();
        boolean res = testOneType_ListConv(
            DataListTypeEnum.P091, 
            Conversion.MEDATLAS, 
            E_TypeConversion.MEDATLAS);
        Assert.assertTrue(res);
    }
    @Test
    public final void test_P011viaP091_Medatlas()
    {
        decoA_______________();
        testLogger.info("___________________________test_P011viaP091_Medatlas_____________________________");
        decoP_______________();
        Assert.assertTrue(testOneType_ListConv(
            DataListTypeEnum.P011_VIA_P091, 
            Conversion.MEDATLAS, 
            E_TypeConversion.MEDATLAS));
    }
    
    /**
     * =================================================================
     * ODV
     * =================================================================
     */
    @Test
    public final void test_P091_ODVMULTI()
    {
        decoA_______________();
        testLogger.info("____________________________test_P091_ODVMULTI____________________________");
        decoP_______________();
        Assert.assertTrue(testOneType_ListConv(
            DataListTypeEnum.P091, 
            Conversion.ODV, 
            E_TypeConversion.ODV_MULTISTATIONS));
    }
    @Test
    public final void test_P011viaP091_ODVMULTI()
    {
        decoA_______________();
        testLogger.info("____________________________test_P011viaP091_ODVMULTI____________________________");
        decoP_______________();
        Assert.assertTrue(testOneType_ListConv(
            DataListTypeEnum.P011_VIA_P091, 
            Conversion.ODV, 
            E_TypeConversion.ODV_MULTISTATIONS));
    }
    @Test
    public final void test_P011viaP021_ODVMULTI()
    {
        decoA_______________();
        testLogger.info("___________________________test_P011viaP021_ODVMULTI_____________________________");
        decoP_______________();
        Assert.assertTrue(testOneType_ListConv(
            DataListTypeEnum.P011_VIA_P021,
            Conversion.ODV,
            E_TypeConversion.ODV_MULTISTATIONS));
    }
    private void decoA_______________()
    {
        testLogger.info("");
        testLogger.info("");
        testLogger.info("*******************************************************");
        testLogger.info("");
        testLogger.info("");
    }
    private void decoP_______________()
    {
        testLogger.info("");
        testLogger.info("");
        testLogger.info("*******************************************************");
        testLogger.info("");
        testLogger.info("");
    }
    /**
     * =================================================================
     * methodes
     * =================================================================
     */
    public boolean testOneType_ListConv(DataListTypeEnum dataList, Conversion conversion, E_TypeConversion odvConv)
    {
        
        boolean ok=true;
        boolean res ;
        boolean comp=false;
        int cptTests=0;
        int cptFail=0;
        boolean testFound;
        File root =  new File(dataRootPath);
        for (File testDir : root.listFiles())
        {
            if (isToTest(testDir.getName()))
            {
                testLogger.info("");
                testLogger.info("[______________ "+testDir.getName()+"_____________]");
                testFound=false;

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
                                    if (unitTest.getName().startsWith("t") )
                                    {
                                        res=false;
                                        comp=false;
                                        testLogger.info("- "+paramListDir.getName()+" "+convDir.getName()+" "+unitTest.getName());
                                        String inputAbsolutePath = testDir.getAbsolutePath()+"\\"+inputName;
                                        String outputAbsolutePath=unitTest.getAbsolutePath();
                                        cptTests++;
                                        nbTotalTests++;
                                        testFound=true;

                                        if (testBatchs)
                                        {
                                            res=convertWithNemoBatch(conversion, odvConv, inputAbsolutePath, outputAbsolutePath);

                                            if (res)
                                            {
                                                nbTotalSuccess++;
                                                comp = compareWithReference(outputAbsolutePath, outputName);
                                            }
                                            if (!res)
                                            {
                                                cptFail++;
                                                nbTotalFailed++;
                                                testLogger.error("!!!!!!!!!!!!conversion: KO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                                            }
                                            else if (res && comp)
                                            {
                                                testLogger.info(" OK ");
                                            }
                                            else if (res && !comp)
                                            {
                                                testLogger.error("!!!!!!!!!!!!comparison: KO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                                            }

                                            ok= res && ok;
                                        }

                                        if (compareManualFiles)
                                        {
                                            testLogger.info("comp manual");
                                            comp= compareWithReference(outputAbsolutePath, outputManualName);
                                           ok= comp && ok;
                                        }

                                    }else if(unitTest.getName().startsWith("_t"))
                                    {
                                        testLogger.info("- "+paramListDir.getName()+" "+convDir.getName()+" "+unitTest.getName());
                                        testLogger.info("- DESACTIVATED - ");
                                    }
                                }

                            }
                        }



                    }






                }
                if (!testFound)
                {
                    testLogger.info("No test found");
                }
            }
        }
        testLogger.info("=========================================");
        testLogger.info(cptFail+ " failed /" +cptTests +" tested");
        testLogger.info("=========================================");
        testLogger.info("");
        return ok;
    }

    public boolean isToTest(String dirName)
    {
        boolean res=true;
        if (dirName.startsWith("_"))
        {
            res=false;
        }
        else if (testDirAlone.length>0)
        {
            res=false;
            if (dirName.equals(testDirAlone[0]))
            {
                res=true;
            }
        }
        return res;
    }
    public boolean convertWithNemoBatch(Conversion conversion, E_TypeConversion odvConv, String inputAbsolutePath,String outputAbsolutePath)
    {
        boolean res = false;
        String output = outputAbsolutePath+"\\"+outputName;
        String model = outputAbsolutePath+"\\"+modelName;

        File mod=new File(model);
        if (!mod.exists())
        {
            testLogger.error("!!!!!!!!!!!!model not found");
        }else{
            Nemo.getInstance().setBatchMode(true);
            try{
                res= shell.main(
                    inputAbsolutePath,
                    output,
                    model,
                    conversion, 
                    sortDatas, 
                    odvConv, 
                    datasetRef, cdiSummaryFile, couplingTableUpdate, couplingTablePrefix, batchLogger);
            }catch(Exception e)
            {
                testLogger.info("error main");
            }

        }
        return res ;
    }
    
    public boolean compareWithReference( String outputAbsolutePath, String outputTestName)
    {
        boolean comp = false;
        
        comp = compare_____TwoFiles(outputAbsolutePath, outputTestName, outputReferenceName);
            
         if (!comp)
        {
            testLogger.error("!!!!!!!!!!!!comparison: KO "+outputTestName+"<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        }
         return comp;
    }

    public boolean compare_____TwoFiles(String outputAbsolutePath, String fileA, String fileB){
        
        BufferedReader bufOut = null;
        BufferedReader bufRef = null;
        String outputLine="";
        String refFileLine;
        String outputTest = outputAbsolutePath+"\\"+fileA;
        String output_REFERENCE= outputAbsolutePath+"\\"+fileB;
        boolean identical=true;

        try
        {
            bufOut = new BufferedReader(new FileReader(outputTest));
        }
        catch (FileNotFoundException e1)
        {
            testLogger.error(fileA+" file not found");
        }
        try
        {
            bufRef = new BufferedReader(new FileReader(output_REFERENCE));
        }
        catch (FileNotFoundException e1)
        {
            testLogger.error(fileB+" file not found");
        }


        try
        {
            if (bufOut!=null && bufRef!=null){
                while( bufOut.ready() && identical)
                {
                    outputLine = bufOut.readLine();
                    refFileLine = bufRef.readLine();
                    identical= outputLine.equals(refFileLine);
                }
            }
            return identical;

        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            testLogger.error("IOException");
            return false;
        }
    }
}

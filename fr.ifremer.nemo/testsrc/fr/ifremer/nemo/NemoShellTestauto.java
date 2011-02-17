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
 * pur xxx: si le repertoire commence par "_", il est ignoré
 * pour tyyy: si ce répertoire commence par "t", il est considéré comme un test. S'il commence par "_", il est considéré comme un test désactivé.
 * S'il commence par un autre caractère, il est ignoré.
 * 
 * 
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
    public NemoShell shell = new NemoShell();


    public String dataRootPath="C:\\tests_nemo";


    public String inputName="input";
    public String outputName="output_junit";
    public String modelName="model.xml";
    public String outputReferenceName= "output_REFERENCE";



    public String inputAbsolutePath;
    public String outputAbsolutePath;

    String currentDataList;

    public boolean sortDatas=false;

    public Conversion conv=Conversion.MEDATLAS;
    public E_TypeConversion odvConv= E_TypeConversion.MEDATLAS;

    public String datasetRef=Global.EMPTY_STRING;
    public String cdiSummaryFile=Global.EMPTY_STRING;
    public boolean couplingTableUpdate=false;
    public String couplingTablePrefix=Global.EMPTY_STRING;
    public static Logger batchLogger;
    public static Logger testLogger ;
    
    public static int nbTotalTests=0;
    public static int nbTotalFailed=0;
    public static int nbTotalSuccess=0;

    // repertoires sous la racine a exclure des tests
//    public static String[] testDirExluded={"_structType", "_results","_michele"};
    // repertoire a tester (si vide, on teste tous sauf testDirExluded)
    public static String[] testDirAlone={};
//     public static String[] testDirAlone={"carotte_sed"};
//    public static String[] testDirAlone={"smatch_timeseries"}; 
//    public static String[] testDirAlone={"cruiseDir_profile_diap"}; 
//    public static String[] testDirAlone={"michele_tsg_trajectory"}; 
//    public static String[] testDirAlone={"cruiseDir_profile_diap_avec stHeaderDiffDansFichiersInput"}; 
//    public static String[] testDirAlone={"cruiseFile_trajectory_6959"}; 
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
         testLogger.info("================================================================");
        testLogger.info("====================================="+dat+"=====================================");
        testLogger.info("================================================================");
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
        
        testLogger.info("=====================================================================================");
        testLogger.info(nbTotalSuccess+ " tests succeed on " + nbTotalTests);
        testLogger.info(nbTotalFailed + " tests failed");
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {

        conv=Conversion.MEDATLAS;
        odvConv= E_TypeConversion.MEDATLAS;
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
        decoA();
        testLogger.info("________________________________________________________");
        decoP();
        currentDataList = DataListTypeEnum.P091.toString();
        conv = Conversion.MEDATLAS;
        odvConv= E_TypeConversion.MEDATLAS;
        boolean res = testOneType();
        Assert.assertTrue(res);
    }
    @Test
    public final void test_P011viaP091_Medatlas()
    {
        decoA();
        testLogger.info("________________________________________________________");
        decoP();
        currentDataList = DataListTypeEnum.P011_VIA_P091.toString();
        conv = Conversion.MEDATLAS;
        odvConv= E_TypeConversion.MEDATLAS;
        Assert.assertTrue(testOneType());
    }
    
    /**
     * =================================================================
     * ODV
     * =================================================================
     */
    @Test
    public final void test_P091_ODVMULTI()
    {
        decoA();
        testLogger.info("________________________________________________________");
        decoP();
        currentDataList = DataListTypeEnum.P091.toString();
        conv = Conversion.ODV;
        odvConv= E_TypeConversion.ODV_MULTISTATIONS;
        Assert.assertTrue(testOneType());
    }
    @Test
    public final void test_P011viaP091_ODVMULTI()
    {
        decoA();
        testLogger.info("________________________________________________________");
        decoP();
        currentDataList = DataListTypeEnum.P011_VIA_P091.toString();
        conv = Conversion.ODV;
        odvConv= E_TypeConversion.ODV_MULTISTATIONS;
        Assert.assertTrue(testOneType());
    }
    @Test
    public final void test_P011viaP021_ODVMULTI()
    {
        decoA();
        testLogger.info("________________________________________________________");
        decoP();

        currentDataList = DataListTypeEnum.P011_VIA_P021.toString();
        conv = Conversion.ODV;
        odvConv= E_TypeConversion.ODV_MULTISTATIONS;
        Assert.assertTrue(testOneType());
    }
    private void decoA()
    {
        testLogger.info("");
        testLogger.info("*******************************************************");
        testLogger.info("");
    }
    private void decoP()
    {
        testLogger.info("");
        testLogger.info("*******************************************************");
        testLogger.info("");
    }
    /**
     * =================================================================
     * methodes
     * =================================================================
     */
    public boolean testOneType()
    {
        boolean ok=true;
        boolean res ;
        int cptTests=0;
        int cptFail=0;
        boolean testFound;
        File root =  new File(dataRootPath);
        for (File testDir : root.listFiles())
        {
            if (isToTest(testDir.getName()))
            {
                testLogger.info("[______________ "+testDir.getName()+"_____________]");
                testFound=false;

                for (File paramListDir : testDir.listFiles())
                {
                    if (paramListDir.getName().equals(currentDataList))
                    {

                        for (File convDir : paramListDir.listFiles())
                        {
                            if (convDir.getName().equals(odvConv.toString()))
                            {
                                for (File unitTest : convDir.listFiles())
                                {
                                    if (unitTest.getName().startsWith("t") )
                                    {
                                        testLogger.info("- "+paramListDir.getName()+" "+convDir.getName()+" "+unitTest.getName());
                                        inputAbsolutePath = testDir.getAbsolutePath()+"\\"+inputName;
                                        outputAbsolutePath=unitTest.getAbsolutePath();
                                        cptTests++;
                                        nbTotalTests++;
                                        testFound=true;
                                        res=conv();
                                        if (!res)
                                        {
                                            cptFail++;
                                            nbTotalFailed++;
                                        }else{
                                            nbTotalSuccess++;
                                        }
                                        ok= res && ok;
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
    public boolean conv()
    {
        boolean res = false;
        boolean comp = false;
        String input = inputAbsolutePath;
        String output = outputAbsolutePath+"\\"+outputName;
        String output_REFERENCE= outputAbsolutePath+"\\"+outputReferenceName;
        String model = outputAbsolutePath+"\\"+modelName;
//      testLogger.info(input);
//      testLogger.info(output);
//      testLogger.info(model);
//      testLogger.info(conv);
//      testLogger.info(odvConv);
        Nemo.getInstance().setBatchMode(true);
        try{
            res= shell.main(
                input,
                output,
                model,
                conv, sortDatas, odvConv, 
                datasetRef, cdiSummaryFile, couplingTableUpdate, couplingTablePrefix, batchLogger);
        }catch(Exception e)
        {
            testLogger.info("error main");
        }
        
        if (res)
        {
            comp = compareFiles(output, output_REFERENCE);
        }
        if (!res)
        {
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
        return res && comp;
    }


    public boolean compareFiles(String output, String refFile){
        BufferedReader bufOut;
        BufferedReader bufRef;
        String outputLine="";
        String refFileLine;
        boolean identical=true;
        try
        {
            bufOut = new BufferedReader(new FileReader(output));
            bufRef = new BufferedReader(new FileReader(refFile));

            while( bufOut.ready() && identical)
            {
                outputLine = bufOut.readLine();
                refFileLine = bufRef.readLine();
                identical= outputLine.equals(refFileLine);
            }
            return identical;

        }
        catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
           testLogger.error("FileNotFoundException");
            return false;
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            testLogger.error("IOException");
            return false;
        }
    }
}

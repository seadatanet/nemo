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
import java.util.HashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.corba.se.impl.orb.ParserTable.TestBadServerIdHandler;

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
 *  
 *  
 *  
 *  
 
 * 
 * @author sbregent
 *
 */
public class NemoShellTestauto
{
    
    public String dataRootPath="C:\\tests_nemo";

    
    // générer les fichiers en batch et les comparer aux fichiers REFERENCE
    public static boolean testBatchs=true;
    // comparer les fichiers manuels (output_eclipse) aux fichiers REFERENCE
    public static boolean compareManualFiles=false;
    
    
    public static String[] testDirAlone={};
//  public static String[] testDirAlone={"cruiseDir_profile_mona"}; 
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
    public static String outputManualName="output_michele";
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
        nbTotalTests=0;
        nbTotalFailed=0;
        nbTotalSuccess=0;
        Date actuelle = new Date();
         DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
         String dat = dateFormat.format(actuelle);
         
        testLogger.info("\r\n\r\n\r\n\r\n===================================== "+dat+" =========================\r\n\r\n\r\n\r\n");
        testLogger.info("test batchs : "+testBatchs);
        testLogger.info("comp manual files : "+compareManualFiles+ "("+outputManualName+")");
        
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
        testLogger.info("\r\n\r\n\r\n\r\n___________________________test_P091_Medatlas_____________________________\r\n\r\n\r\n\r\n");
        boolean res = testOneType_ListConv(
            DataListTypeEnum.P091, 
            Conversion.MEDATLAS, 
            E_TypeConversion.MEDATLAS);
        Assert.assertTrue(res);
    }
    @Test
    public final void test_P011viaP091_Medatlas()
    {
        testLogger.info("\r\n\r\n\r\n\r\n___________________________test_P011viaP091_Medatlas_____________________________\r\n\r\n\r\n\r\n");
        Assert.assertTrue(testOneType_ListConv(
            DataListTypeEnum.P011_VIA_P091, 
            Conversion.MEDATLAS, 
            E_TypeConversion.MEDATLAS));
    }
    
    /**
     * =================================================================
     * ODV multi
     * =================================================================
     */
    @Test
    public final void test_P091_ODVMULTI()
    {
        testLogger.info("\r\n\r\n\r\n\r\n____________________________test_P091_ODVMULTI____________________________\r\n\r\n\r\n\r\n");
        Assert.assertTrue(testOneType_ListConv(
            DataListTypeEnum.P091, 
            Conversion.ODV, 
            E_TypeConversion.ODV_MULTISTATIONS));
    }
    @Test
    public final void test_P011viaP091_ODVMULTI()
    {
        testLogger.info("\r\n\r\n\r\n\r\n____________________________test_P011viaP091_ODVMULTI____________________________\r\n\r\n\r\n\r\n");
        Assert.assertTrue(testOneType_ListConv(
            DataListTypeEnum.P011_VIA_P091, 
            Conversion.ODV, 
            E_TypeConversion.ODV_MULTISTATIONS));
    }
    @Test
    public final void test_P011viaP021_ODVMULTI()
    {
        testLogger.info("\r\n\r\n\r\n\r\n__________________________test_P011viaP021_ODVMULTI_____________________________\r\n\r\n\r\n\r\n");
        Assert.assertTrue(testOneType_ListConv(
            DataListTypeEnum.P011_VIA_P021,
            Conversion.ODV,
            E_TypeConversion.ODV_MULTISTATIONS));
    }
    /**
     * =================================================================
     * ODV mono
     * =================================================================
     */
    @Test
    public final void test_P091_ODVMONO()
    {
        testLogger.info("\r\n\r\n\r\n\r\n____________________________test_P091_ODVMONO____________________________\r\n\r\n\r\n\r\n");
        Assert.assertTrue(testOneType_ListConv(
            DataListTypeEnum.P091, 
            Conversion.ODV, 
            E_TypeConversion.ODV_MONOSTATION));
    }
    @Test
    public final void test_P011viaP091_ODVMONO()
    {
        testLogger.info("\r\n\r\n\r\n\r\n____________________________test_P011viaP091_ODVMONO____________________________\r\n\r\n\r\n\r\n");
        Assert.assertTrue(testOneType_ListConv(
            DataListTypeEnum.P011_VIA_P091, 
            Conversion.ODV, 
            E_TypeConversion.ODV_MONOSTATION));
    }
    @Test
    public final void test_P011viaP021_ODVMONO()
    {
        testLogger.info("\r\n\r\n\r\n\r\n__________________________test_P011viaP021_ODVMONO_____________________________\r\n\r\n\r\n\r\n");
        Assert.assertTrue(testOneType_ListConv(
            DataListTypeEnum.P011_VIA_P021,
            Conversion.ODV,
            E_TypeConversion.ODV_MONOSTATION));
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
               message=new Message();
               message.dirName=testDir.getName();
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
                                    message=new Message();
                                    message.dirName=testDir.getName();
                                    if (unitTest.getName().startsWith("t") )
                                    {
                                        res=false;
                                        comp=false;
                                        message.testName=paramListDir.getName()+" "+convDir.getName()+" "+unitTest.getName();
//                                        System.out.println(message.testName);
                                        String inputAbsolutePath = testDir.getAbsolutePath()+"\\"+inputName;
                                        String outputAbsolutePath=unitTest.getAbsolutePath();
                                        cptTests++;
                                        testFound=true;
                                        message.isTestFound=true;
                                        if (testBatchs)
                                        {
                                            nbTotalTests++;
                                            message.isReferenceFound=fileExist(outputAbsolutePath, outputReferenceName);
                                            res=convertWithNemoBatch(conversion, odvConv, inputAbsolutePath, outputAbsolutePath);
                                            ok= res && ok;
                                            if (res)
                                            {
                                                comp = compareWithReference(outputAbsolutePath, outputName);
                                                message.batchCompRes=comp;
                                                if (comp)
                                                {
                                                    nbTotalSuccess++;
                                                }
                                                ok= comp && ok;
                                            }
                                            if (!res|| !comp)
                                            {
                                                cptFail++;
                                                nbTotalFailed++;
                                            }

                                        }

                                        if (compareManualFiles)
                                        {
                                           message.isManualFound = fileExist(outputAbsolutePath, outputManualName);
                                           if (message.isManualFound){
                                           comp= compareWithReference(outputAbsolutePath, outputManualName);
                                           message.manualCompRes=comp;
                                           message.manualCompName=outputManualName;
                                           if(!comp)
                                           {
                                              System.out
                                               .println("lllll");
                                           }
                                           ok= comp && ok;
                                           }
                                        }

                                    }else if(unitTest.getName().startsWith("_t"))
                                    {
                                        message.testName=paramListDir.getName()+" "+convDir.getName()+" "+unitTest.getName();
                                        message.isActivated=false;
                                    }
                                   
                                    message.print();
                                }

                            }
                        }



                    }






                }
                if (!testFound)
                {
                    message.isTestFound=false;
                }
//              message.print();
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
        if (dirName.startsWith("_") || dirName.startsWith("."))
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
          message.isModelFound=false;
        }else{
            message.isModelFound=true;
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
                message.conversionRes=res;
                message.conversionError=e.getMessage();
            }

        }
        message.conversionRes=res;
        return res ;
    }
    
    public boolean fileExist(String outputAbsolutePath, String outputTestName)
    {
        File f = new File(outputAbsolutePath+"\\"+outputTestName);
        return f.exists();
    }
    public boolean compareWithReference( String outputAbsolutePath, String outputTestName)
    {
        boolean comp = false;
        comp = compare_____TwoResults(outputAbsolutePath, outputTestName, outputReferenceName);
         return comp;
    }

    public boolean compare_____TwoResults(String outputAbsolutePath, String outA, String outB)
    {
        boolean identical=true;
        String outputTest = outputAbsolutePath+"\\"+outA;
        String output_REFERENCE= outputAbsolutePath+"\\"+outB;
        
        
        File a = new File(outputTest);
        File b = new File(output_REFERENCE);
        
        if (a.isDirectory()
                && a.listFiles()!=null
                && b.listFiles()!=null
                && a.listFiles().length>0
                && b.listFiles().length>0
                && a.listFiles().length==b.listFiles().length)
        {
            for (int i=0; i<a.listFiles().length; i++)
            {
                identical= compare_____TwoFiles(outputAbsolutePath,a.listFiles()[i].getAbsolutePath(), b.listFiles()[i].getAbsolutePath());
            }
        }else{
            identical= compare_____TwoFiles(outputAbsolutePath,outA, outB);
        }
        
        if (!identical)
        {
            System.out.println("ici");
        }
        return identical;
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
            return false;
        }
        try
        {
            bufRef = new BufferedReader(new FileReader(output_REFERENCE));
        }
        catch (FileNotFoundException e1)
        {
            return false;
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
    
    
    private String completeTitle(String title)
    {
        String res="";
        if (title.length()<70)
        {
            for (int i=0; i<10;i++)
            {
                res+="_";
            }
            res+=title;
            
            for (int i=0; i<60-title.length();i++)
            {
                res+="_";
            }
        }else{
            res=title;
        }
        return res;
    }
    
    
    public class Message{
        public boolean isActivated=true;
        public boolean isTestFound=true;
        public String dirName;
        public String testName=null;
        public boolean isModelFound;
        public boolean isReferenceFound=false;
        public boolean conversionRes;
        public String conversionError;
        public boolean batchCompRes;
        public boolean manualCompRes;
        public String manualCompName;
        public boolean isManualFound;
//        public HashMap<String, Boolean> filesMap = new HashMap<String, Boolean>();
        public Message()
        {
            
        }
        
        public void print()
        {
            String mes="";
            mes+=completeTitle(dirName);
            if (testName!=null){
            mes+="\r\n"+testName;
            }
            if (!isActivated)
            {
                mes+=("- DESACTIVATED -");
                write(mes);
                return;
            }
            if (!isTestFound){
                mes+=("- No test found -");
                write(mes);
                return;
            }
            if (!isModelFound)
            {
                mes+=("- KO No model found -");
                write(mes);
                return;
            }

//            for ( String file : filesMap.keySet())
//            {
//                if (!filesMap.get(file))
//                {
//                    if (file.equals(manualCompName))
//                    {
//                        message.isManualFound=false;
//                    }
//                    else if(file.equals(outputReferenceName))
//                    {
//                        message.isReferenceFound=false;
//                    }
//                }
//            }

            if (!conversionRes)
            {
                mes+= "\r\nconversion: "+getString(conversionRes);
                mes+= " "+conversionError;
            }
            if (testBatchs)
            {
                if (isReferenceFound){
                    if (batchCompRes){
                        mes+= " "+getString(conversionRes);
                    }else
                    {
                        mes+= "\r\nbatch comparison: "+getString(batchCompRes);
                    }
                }else{
                    mes+= "\r\nReference file not found";
                }
            }
            if (compareManualFiles && isManualFound)
            {
                if (isReferenceFound){
                mes+= "\r\nmanual comparison: "+getString(manualCompRes);
                }else{
                    mes+= "\r\nReference file not found";
                }
                    
            }
            
           
            write(mes);
        }
        private String getString(boolean res)
        {
            String str="";
            if (res){
                str= "OK";
            }
            else{
                str= "KO";
            }
            return str;
        }
        private void write(String mes)
        {
            testLogger.info(mes+"\r\n");
        }
    }
}

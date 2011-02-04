/* ============================================================================
 * Nom du fichier	: NemoShellTest.java
 * Auteur			: sbregent
 * Date				: 4 févr. 11
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.ifremer.nemo.ui.Application;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;
import fr.ifremer.nemo.util.Global.E_TypeConversion;

/**
 * Description: TODO
 * @author sbregent
 *
 */
public class NemoShellTest
{
    public NemoShell shell = new NemoShell();
    
    
    public String dataPath="C:\\test_1_2_32";
    String dataDir = "";
    String versionDir="";
    String dataList = "";
    String conversionDir = E_TypeConversion.UNKNOWN.toString();
    
    
    public String inputFile;
    public String outputFile;
    public String modelFile;
    public Conversion conv=Conversion.MEDATLAS;
    public boolean sortDatas=false;
    public E_TypeConversion odvConv= E_TypeConversion.MEDATLAS;
    public String datasetRef=Global.EMPTY_STRING;
    public String cdiSummaryFile=Global.EMPTY_STRING;
    public boolean couplingTableUpdate=false;
    public String couplingTablePrefix=Global.EMPTY_STRING;
    public static Logger batchLogger;
    public static Logger testLogger ;
    
    
    
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
        
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        inputFile="";
        outputFile="";
        modelFile="";
        conv=Conversion.MEDATLAS;
        sortDatas=false;
        odvConv= E_TypeConversion.MEDATLAS;
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
     * Test method for {@link fr.ifremer.nemo.NemoShell#main(java.lang.String, java.lang.String, java.lang.String, fr.ifremer.nemo.util.Conversion, boolean, fr.ifremer.nemo.util.Global.E_TypeConversion, java.lang.String, java.lang.String, boolean, java.lang.String, org.apache.log4j.Logger)}.
     */
    @Test
    public final void testMain_diapalis_1232_MEDATLAS_P091()
    {
        testLogger.info("");
        dataDir = "cruiseFile_profile_diap";
        versionDir="1_2_32_ok";
        dataList = DataListTypeEnum.P091.toString();
        conversionDir = E_TypeConversion.MEDATLAS.toString();
        inputFile = getInputPath()+"diapalis2_bot.prn";
        outputFile = getOutputPath()+"diap_out_junit";
        modelFile = getOutputPath()+getModelFileName("diap");//"diap_1232_MEDATLAS_P091.xml";
        conv =Conversion.MEDATLAS;
        
        // fichier de reference avec lequel comparer
        String refFile = getOutputPath()+"diap_out_REFERENCE";
        
     boolean  res= shell.main(inputFile, outputFile, modelFile, conv, sortDatas, odvConv, 
            datasetRef, cdiSummaryFile, couplingTableUpdate, couplingTablePrefix, batchLogger);
     
     Assert.assertTrue(res);
     Assert.assertTrue(compareFiles(outputFile, refFile));
     
    }
    @Test
    public final void testMain_diapalis_1232_ODVMULTI_P091()
    {
        testLogger.info("");
        dataDir = "cruiseFile_profile_diap";
        versionDir="1_2_32_ok";
        dataList = "P091";
        conversionDir = E_TypeConversion.ODV_MULTISTATIONS.toString();
        inputFile = getInputPath()+"diapalis2_bot.prn";
        outputFile = getOutputPath()+"diap_out_junit";
        modelFile = getOutputPath()+getModelFileName("diap");
        conv =Conversion.ODV;
        odvConv = E_TypeConversion.ODV_MULTISTATIONS;
        // fichier de reference avec lequel comparer
        String refFile = getOutputPath()+"diap_ODVMULTI_P091_REFERENCE";
        
     boolean  res= shell.main(inputFile, outputFile, modelFile, conv, sortDatas, odvConv, 
            datasetRef, cdiSummaryFile, couplingTableUpdate, couplingTablePrefix, batchLogger);
     
     Assert.assertTrue(res);
     Assert.assertTrue(compareFiles(outputFile, refFile));
    }
    
    @Test
    public final void testMain_diapalis_1232_MEDATLAS_P011viaP091()
    {
        testLogger.info("");
         dataDir = "cruiseFile_profile_diap";
         versionDir="1_2_32_ok";
         dataList = DataListTypeEnum.P011_VIA_P091.toString();
         conversionDir = E_TypeConversion.MEDATLAS.toString();
        inputFile = getInputPath()+"diapalis2_bot.prn";
        outputFile = getOutputPath()+"diap_out_junit";
        modelFile = getOutputPath()+getModelFileName("diap");//"diap_1232_MEDATLAS_P011viaP091.xml";
        conv =Conversion.MEDATLAS;
        
        // fichier de reference avec lequel comparer
        String refFile = getOutputPath()+"diap_out_REFERENCE";
        
     boolean  res= shell.main(inputFile, outputFile, modelFile, conv, sortDatas, odvConv, 
            datasetRef, cdiSummaryFile, couplingTableUpdate, couplingTablePrefix, batchLogger);
     
     Assert.assertTrue(res);
     Assert.assertTrue(compareFiles(outputFile, refFile));
     
    }
    @Test
    public final void testMain_diapalis_1232_ODVMULTI_P011viaP091()
    {
        testLogger.info("");
         dataDir = "cruiseFile_profile_diap";
         versionDir="1_2_32_ok";
         dataList = DataListTypeEnum.P011_VIA_P091.toString();
         conversionDir = E_TypeConversion.ODV_MULTISTATIONS.toString();
        inputFile = getInputPath()+"diapalis2_bot.prn";
        outputFile = getOutputPath()+"diap_out_junit";
        modelFile = getOutputPath()+getModelFileName("diap");//"diap_1232_MEDATLAS_P011viaP091.xml";
        conv =Conversion.ODV;
        odvConv = E_TypeConversion.ODV_MULTISTATIONS;
        
        // fichier de reference avec lequel comparer
        String refFile = getOutputPath()+"diap_out_REFERENCE";
        
     boolean  res= shell.main(inputFile, outputFile, modelFile, conv, sortDatas, odvConv, 
            datasetRef, cdiSummaryFile, couplingTableUpdate, couplingTablePrefix, batchLogger);
     
     Assert.assertTrue(res);
     Assert.assertTrue(compareFiles(outputFile, refFile));
     
    }
    @Test
    public final void testMain_diapalis_1232_ODVMULTI_P011viaP021()
    {
        testLogger.info("");
         dataDir = "cruiseFile_profile_diap";
         versionDir="1_2_32_ok";
         dataList = DataListTypeEnum.P011_VIA_P021.toString();
         conversionDir = E_TypeConversion.ODV_MULTISTATIONS.toString();
        inputFile = getInputPath()+"diapalis2_bot.prn";
        outputFile = getOutputPath()+"diap_out_junit";
        modelFile = getOutputPath()+getModelFileName("diap");//"diap_1232_MEDATLAS_P011viaP091.xml";
        conv =Conversion.ODV;
        odvConv = E_TypeConversion.ODV_MULTISTATIONS;
        
        // fichier de reference avec lequel comparer
        String refFile = getOutputPath()+"diap_out_REFERENCE";
        
     boolean  res= shell.main(inputFile, outputFile, modelFile, conv, sortDatas, odvConv, 
            datasetRef, cdiSummaryFile, couplingTableUpdate, couplingTablePrefix, batchLogger);
     
     Assert.assertTrue(res);
     Assert.assertTrue(compareFiles(outputFile, refFile));
     
    }
    @Test
    public final void testMain_6839_1232_ODVMULTI_P011viaP021()
    {
        testLogger.info("");
        dataDir = "6839";
        versionDir="1_2_32_ok";
        dataList = DataListTypeEnum.P011_VIA_P091.toString();
        conversionDir = E_TypeConversion.ODV_MULTISTATIONS.toString();
        inputFile = getInputPath()+"TSG_FNIN_20101230_000000.txt";
        outputFile = getOutputPath()+"out_junit";
        modelFile = getOutputPath()+getModelFileName("6839");
        conv =Conversion.ODV;
        odvConv = E_TypeConversion.ODV_MULTISTATIONS;
        datasetRef="ref";

        // fichier de reference avec lequel comparer
        String refFile = getOutputPath()+"6839_out_REFERENCE";

        boolean  res= shell.main(inputFile, outputFile, modelFile, conv, sortDatas, odvConv, 
            datasetRef, cdiSummaryFile, couplingTableUpdate, couplingTablePrefix, batchLogger);

        Assert.assertTrue(res);
        Assert.assertTrue(compareFiles(outputFile, refFile));

    }
    @Test
    public final void testMain_diapalis_6903_ODVMULTI_P011viaP091()
    {
        testLogger.info("");
         dataDir = "6903";
         versionDir="1_2_32_ok";
         dataList = DataListTypeEnum.P011_VIA_P091.toString();
         conversionDir = E_TypeConversion.ODV_MULTISTATIONS.toString();
        inputFile = getInputPath()+"diapalis2_bot.prn";
        outputFile = getOutputPath()+"diap_out_junit";
        modelFile = getOutputPath()+getModelFileName("6903");//"diap_1232_MEDATLAS_P011viaP091.xml";
        conv =Conversion.ODV;
        odvConv = E_TypeConversion.ODV_MULTISTATIONS;
        datasetRef="FI35200110014";
        // fichier de reference avec lequel comparer
        String refFile = getOutputPath()+"diap_out_REFERENCE";
        
     boolean  res= shell.main(inputFile, outputFile, modelFile, conv, sortDatas, odvConv, 
            datasetRef, cdiSummaryFile, couplingTableUpdate, couplingTablePrefix, batchLogger);
     
     Assert.assertTrue(res);
     Assert.assertTrue(compareFiles(outputFile, refFile));
     
    }
    @Test
    public final void testMain_diapalis_6903_ODVMULTI_P011viaP021()
    {
        testLogger.info("");
         dataDir = "6903";
         versionDir="1_2_32_ok";
         dataList = DataListTypeEnum.P011_VIA_P021.toString();
         conversionDir = E_TypeConversion.ODV_MULTISTATIONS.toString();
        inputFile = getInputPath()+"diapalis2_bot.prn";
        outputFile = getOutputPath()+"diap_out_junit";
        modelFile = getOutputPath()+getModelFileName("6903");//"diap_1232_MEDATLAS_P011viaP091.xml";
        conv =Conversion.ODV;
        odvConv = E_TypeConversion.ODV_MULTISTATIONS;
        datasetRef="FI35200110014";
        // fichier de reference avec lequel comparer
        String refFile = getOutputPath()+"diap_out_REFERENCE";
        
     boolean  res= shell.main(inputFile, outputFile, modelFile, conv, sortDatas, odvConv, 
            datasetRef, cdiSummaryFile, couplingTableUpdate, couplingTablePrefix, batchLogger);
     
     Assert.assertTrue(res);
     Assert.assertTrue(compareFiles(outputFile, refFile));
     
    }
    public String getModelFileName(String prefix)
    {
        return prefix+"_"+conversionDir+"_"+dataList+".xml";
    }
    public String getOutputPath()
    {
        return dataPath+"\\"+dataDir+"\\"+versionDir+"\\"+dataList+"\\"+conversionDir+"\\";
    }
    public String getInputPath()
    {
        return dataPath+"\\"+dataDir+"\\";
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
           e.printStackTrace();
           return false;
       }
       catch (IOException e)
       {
           // TODO Auto-generated catch block
           e.printStackTrace();
           return false;
       }
    }
}

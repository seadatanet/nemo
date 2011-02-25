/* ============================================================================
 * Nom du fichier	: NemoShellTest.java
 * Auteur			: sbregent
 * Date				: 4 févr. 11
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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
 * Description: TODO
 * @author sbregent
 *
 */
public class NemoShellTest
{
    public NemoShell shell = new NemoShell();


    public String dataPath="C:\\test_1_2_32";
    public String dataDir = "";
    public String versionDir="1_2_32_ok";
    public String dataList = "";
    public String conversionDir = E_TypeConversion.UNKNOWN.toString();


    public String input;
    public String output;
    public String output_REFERENCE;
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
        input="input";
        output="output_junit";
        modelFile="model.xml";
        output_REFERENCE = "output_REFERENCE";
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
        /**
         * nemo_batch 
         * -i "C:\test_1_2_32\cruiseFile_profile_diap\diapalis2_bot.prn" 
         * -m "C:\test_1_2_32\cruiseFile_profile_diap\1_2_32_ok\P091\MEDATLAS\diap_MEDATLAS_P091.xml"
         * -o "C:\test_1_2_32\cruiseFile_profile_diap\1_2_32_ok\P091\MEDATLAS\diap_out_eclipse" 
         * -c MEDATLAS
         */

        dataDir = "cruiseFile_profile_diap";
        dataList = DataListTypeEnum.P091.toString();
        conversionDir = E_TypeConversion.MEDATLAS.toString();
        conv =Conversion.MEDATLAS;
        cdiSummaryFile = getOutputPath()+"cdiSummary_junit.txt";
        // coupling table ne marche pas en junit -> voir s'il faut recopier un fichier dans le rep tests?
//      couplingTableUpdate=true;
//      couplingTablePrefix= getOutputPath();
        // fichier de reference avec lequel comparer

        boolean  res= shell.main(getInput(), getOutput(), getModel(), conv, sortDatas, odvConv, 
            datasetRef, cdiSummaryFile, couplingTableUpdate, couplingTablePrefix, batchLogger);

        Assert.assertTrue(res);
        Assert.assertTrue(compareFiles(output, getReference()));

    }
    @Test
    public final void testMain_diapalis_1232_ODVMULTI_P091()
    {
        testLogger.info("");
        dataDir = "cruiseFile_profile_diap";
        dataList = "P091";
        conversionDir = E_TypeConversion.ODV_MULTISTATIONS.toString();
        conv =Conversion.ODV;
        odvConv = E_TypeConversion.ODV_MULTISTATIONS;
        // fichier de reference avec lequel comparer

        boolean  res= shell.main(getInput(), getOutput(), getModel(), conv, sortDatas, odvConv, 
            datasetRef, cdiSummaryFile, couplingTableUpdate, couplingTablePrefix, batchLogger);

        Assert.assertTrue(res);
        Assert.assertTrue(compareFiles(output, getReference()));
    }

    @Test
    public final void testMain_diapalis_1232_MEDATLAS_P011viaP091()
    {
        testLogger.info("");
        dataDir = "cruiseFile_profile_diap";
        dataList = DataListTypeEnum.P011_VIA_P091.toString();
        conversionDir = E_TypeConversion.MEDATLAS.toString();
        conv =Conversion.MEDATLAS;


        boolean  res= shell.main(getInput(), getOutput(), getModel(), conv, sortDatas, odvConv, 
            datasetRef, cdiSummaryFile, couplingTableUpdate, couplingTablePrefix, batchLogger);

        Assert.assertTrue(res);
        Assert.assertTrue(compareFiles(output, getReference()));

    }
    @Test
    public final void testMain_diapalis_1232_ODVMULTI_P011viaP091()
    {
        testLogger.info("");
        dataDir = "cruiseFile_profile_diap";
        dataList = DataListTypeEnum.P011_VIA_P091.toString();
        conversionDir = E_TypeConversion.ODV_MULTISTATIONS.toString();
        conv =Conversion.ODV;
        odvConv = E_TypeConversion.ODV_MULTISTATIONS;


        boolean  res= shell.main(getInput(), getOutput(), getModel(), conv, sortDatas, odvConv, 
            datasetRef, cdiSummaryFile, couplingTableUpdate, couplingTablePrefix, batchLogger);

        Assert.assertTrue(res);
        Assert.assertTrue(compareFiles(output, getReference()));

    }
    @Test
    public final void testMain_diapalis_1232_ODVMULTI_P011viaP021()
    {
        testLogger.info("");
        dataDir = "cruiseFile_profile_diap";
        dataList = DataListTypeEnum.P011_VIA_P021.toString();
        conversionDir = E_TypeConversion.ODV_MULTISTATIONS.toString();
        conv =Conversion.ODV;
        odvConv = E_TypeConversion.ODV_MULTISTATIONS;


        boolean  res= shell.main(getInput(), getOutput(), getModel(), conv, sortDatas, odvConv, 
            datasetRef, cdiSummaryFile, couplingTableUpdate, couplingTablePrefix, batchLogger);

        Assert.assertTrue(res);
        Assert.assertTrue(compareFiles(output, getReference()));

    }

    @Test
    public final void testMain()
    {

    }

    @Test
    public final void testMain_6839_1232_ODVMULTI_P011viaP021()
    {
        testLogger.info("");
        dataDir = "6839";
        dataList = DataListTypeEnum.P011_VIA_P091.toString();
        conversionDir = E_TypeConversion.ODV_MULTISTATIONS.toString();
        conv =Conversion.ODV;
        odvConv = E_TypeConversion.ODV_MULTISTATIONS;
        datasetRef="ref";


        boolean  res= shell.main(getInput(), getOutput(), getModel(), conv, sortDatas, odvConv, 
            datasetRef, cdiSummaryFile, couplingTableUpdate, couplingTablePrefix, batchLogger);

        Assert.assertTrue(res);
        Assert.assertTrue(compareFiles(output, getReference()));

    }
    @Test
    public final void testMain_6903_ODVMULTI_P011viaP091()
    {
        testLogger.info("");
        dataDir = "6903";
        dataList = DataListTypeEnum.P011_VIA_P091.toString();
        conversionDir = E_TypeConversion.ODV_MULTISTATIONS.toString();
        conv =Conversion.ODV;
        odvConv = E_TypeConversion.ODV_MULTISTATIONS;
        datasetRef="FI35200110014";

        boolean  res= shell.main(getInput(), getOutput(), getModel(), conv, sortDatas, odvConv, 
            datasetRef, cdiSummaryFile, couplingTableUpdate, couplingTablePrefix, batchLogger);

        Assert.assertTrue(res);
        Assert.assertTrue(compareFiles(output, getReference()));

    }
    @Test
    public final void testMain_6903_ODVMULTI_P011viaP021()
    {
        testLogger.info("");
        dataDir = "6903";
        dataList = DataListTypeEnum.P011_VIA_P021.toString();
        conversionDir = E_TypeConversion.ODV_MULTISTATIONS.toString();
        conv =Conversion.ODV;
        odvConv = E_TypeConversion.ODV_MULTISTATIONS;
        datasetRef="FI35200110014";

        boolean  res= shell.main(getInput(), getOutput(), getModel(), conv, sortDatas, odvConv, 
            datasetRef, cdiSummaryFile, couplingTableUpdate, couplingTablePrefix, batchLogger);

        Assert.assertTrue(res);
        Assert.assertTrue(compareFiles(output, getReference()));

    }
//  public String getModelFileName(String prefix)
//  {
//  return prefix+"_"+conversionDir+"_"+dataList+".xml";
//  }
    private String getModel()
    {
        return getOutputPath()+modelFile;
    }

    private String getInput()
    {
        return  getInputPath()+input;
    }

    private String getOutput()
    {
        return getOutputPath()+output;
    }
    private String getReference()
    {
        return getOutputPath()+output_REFERENCE;
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

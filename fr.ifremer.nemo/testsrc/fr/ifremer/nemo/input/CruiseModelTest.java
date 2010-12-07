/* ============================================================================
 * Nom du fichier	: CruiseModelTest.java
 * Application      : NEMO version 1.2.20
 * Auteur			: dboulang
 * Date	de création : 7 déc. 09
 * Client			: Ifremer
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.input;


import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.ago.xml.XMLBootstrapParser;
import com.ibm.icu.text.SimpleDateFormat;


/**
 * Class CruiseModelTest.java
 * Description: TODO
 * @author dboulang 
 */
public class CruiseModelTest
{
    private InputModel model;
    private XMLBootstrapParser parser;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); //$NON-NLS-1$

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        MediaModel media = Mockito.mock(MediaModel.class);
        CruiseModel cruise = new CruiseModel();
        StationModel station = Mockito.mock(StationModel.class);
        DataModel data = Mockito.mock(DataModel.class);
        ConversionModel conversion = Mockito.mock(ConversionModel.class);
        
        model = new InputModel(media, cruise, station, data, conversion);
        parser = new XMLBootstrapParser(model);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
    }

    /**
     * @throws java.lang.Exception
     */
    @Test
    @SuppressWarnings("nls")
    public void parseCruiseModelXML() throws Exception{

        FileInputStream stream = new FileInputStream("test\\CampagneFICH_profile(diapalis2_bot)_NEW.xml");
        parser.parse(stream, false);
        
        checkValues_Profile(model);        
    }

    /**
     * @throws java.lang.Exception
     */
    @Test
    @SuppressWarnings("nls")
    public void parseCruiseModelCSR() throws Exception{
        
        CruiseModel cruise = new CruiseModel();
        cruise.importFromXML(new File("test\\CSR_sdn_V1.3.xml"));  
                
        Assert.assertEquals("Mauvais résultat sur _reference ! ", 
            "FI351994800010", cruise.getReference());

        Assert.assertEquals("Mauvais résultat sur _chiefScientist ! ", 
            "Laurent MEMERY", cruise.getChiefScientist());

        Assert.assertEquals("Mauvais résultat sur _startDate ! ", 
            "04/01/1994", dateFormat.format(new Date(cruise.getStartDate())));

        Assert.assertEquals("Mauvais résultat sur _endDate ! ", 
            "13/02/1994", dateFormat.format(new Date(cruise.getEndDate())));
        
        Assert.assertEquals("Mauvais résultat sur length of _dataTypes ! ", 
            2, cruise.getDataTypes().length);
        
        Assert.assertEquals("Mauvais résultat sur _dataTypes('H09')/_numberOfProfiles ! ", 
            40, cruise.getDataType("H09 - Water bottle stations").getNumberOfProfiles());      
    }

    /**
     * @throws java.lang.Exception
     */
    @Test
    @SuppressWarnings("nls")
    public void parseOldCruiseModelXML() throws Exception{

        StreamSource xmlSource = new StreamSource(new File("test\\CampagneFICH_profile(diapalis2_bot).xml"));
        StreamSource xslSource = new StreamSource(new File("test/cruise_model.xsl"));
        Result result = new SAXResult(parser);
        
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(xslSource);
        transformer.transform(xmlSource, result);
        
        checkValues_Profile(model);
    }

    @SuppressWarnings("nls")
    static protected void checkValues_Profile(InputModel inputModel){

        Assert.assertEquals("Mauvais résultat sur _reference ! ", 
            "FI35200110014", inputModel.getCruiseModel().getReference());

        Assert.assertEquals("Mauvais résultat sur _chiefScientist ! ", 
            "LE BOUTEILLER Aubert", inputModel.getCruiseModel().getChiefScientist());

        Assert.assertEquals("Mauvais résultat sur _startDate ! ", 
            "10/12/2001", dateFormat.format(new Date(inputModel.getCruiseModel().getStartDate())));

        Assert.assertEquals("Mauvais résultat sur _endDate ! ", 
            "21/12/2001", dateFormat.format(new Date(inputModel.getCruiseModel().getEndDate())));
        
        Assert.assertEquals("Mauvais résultat sur length of _dataTypes ! ", 
            2, inputModel.getCruiseModel().getDataTypes().length);
        
        Assert.assertEquals("Mauvais résultat sur _dataTypes('B71')/_numberOfProfiles ! ", 
            8, inputModel.getCruiseModel().getDataType("B71").getNumberOfProfiles());
    }

}

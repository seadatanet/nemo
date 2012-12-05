/* ============================================================================
 * Nom du fichier	: StationModelTest.java
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

import fr.ifremer.nemo.util.format.LatitudeFormat;

/**
 * Class StationModelTest.java
 * Description: TODO
 * @author dboulang 
 */
public class StationModelTest
{
    private InputModel model;
    private XMLBootstrapParser parser;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {

        MediaModel media = Mockito.mock(MediaModel.class);
        CruiseModel cruise = Mockito.mock(CruiseModel.class);
        StationModel station = new StationModel();
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
    public void parseStationModelXML() throws Exception{
        
        FileInputStream stream = new FileInputStream("test\\CampagneFICH_profile(diapalis2_bot)_NEW.xml");
        parser.parse(stream, false);

        checkValues_Profile(model);    
    }

    /**
     * @throws java.lang.Exception
     */
    @Test
    @SuppressWarnings("nls")
    public void parseOldStationModelXML() throws Exception{
        
        StreamSource xmlSource = new StreamSource(new File("test\\CampagneFICH_profile(diapalis2_bot).xml"));
        StreamSource xslSource = new StreamSource(new File("test/station_model.xsl"));
        Result result = new SAXResult(parser);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(xslSource);
        transformer.transform(xmlSource, result);
        
        checkValues_Profile(model);
    }

    @SuppressWarnings("nls")
    static protected void checkValues_Profile(InputModel inputModel){
        Assert.assertEquals("Mauvais résultat sur _referenceProperty/_fragment/_firstCharOffset ! ", 
            8, inputModel.getStationModel().getReferenceProperty().getFragment().getFirstCharOffset());
        
        Assert.assertEquals("Mauvais résultat sur length of _properties ! ", 
            11, inputModel.getStationModel().getProperties().length);
        
        Assert.assertEquals("Mauvais résultat sur _properties/_userValue ! ", 
            "test", inputModel.getStationModel().getProperty("dcHistory1").getUserValue());
        
        Assert.assertEquals("Mauvais résultat sur _properties/_format ! ", 
            LatitudeFormat.class, inputModel.getStationModel().getProperty("latitude").getFormat().getClass());
        
        Assert.assertEquals("Mauvais résultat sur _properties/_format ! ", 
            "NDD MM.mm", inputModel.getStationModel().getProperty("latitude").getFormat().toString());
        
        Assert.assertEquals("Mauvais résultat sur _dataType/_code ! ", 
            "H09", inputModel.getStationModel().getDataType().getCode());

        Assert.assertEquals("Mauvais résultat sur length of _flagTable ! ", 
            11, inputModel.getStationModel().getFlagTable().getFlags().size());
    }

}

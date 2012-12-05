/* ============================================================================
 * Nom du fichier	: DataModelTest.java
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


/**
 * Class DataModelTest.java
 * Description: TODO
 * @author dboulang 
 */
public class DataModelTest
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
        StationModel station = Mockito.mock(StationModel.class);
        DataModel data = new DataModel();
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
    public void parseDataModelXML() throws Exception{

        FileInputStream stream = new FileInputStream("test\\CampagneFICH_profile(diapalis2_bot)_NEW.xml");
        parser.parse(stream, false);
        
        checkValues_Profile(model);     
        
    }

    /**
     * @throws java.lang.Exception
     */
    @Test
    @SuppressWarnings("nls")
    public void parseOldDataModelXML() throws Exception{
   
        StreamSource xmlSource = new StreamSource(new File("test\\CampagneFICH_profile(diapalis2_bot).xml"));
        StreamSource xslSource = new StreamSource(new File("test/data_model.xsl"));
        Result result = new SAXResult(parser);
        
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(xslSource);
        transformer.transform(xmlSource, result);
        
        checkValues_Profile(model);     
    }

    @SuppressWarnings("nls")
    static protected void checkValues_Profile(InputModel inputModel){
        
        Assert.assertEquals("Mauvais résultat sur length of _parameterModels ! ", 
            6, inputModel.getDataModel().getParameterModels().length);
        
        Assert.assertEquals("Mauvais résultat sur _parameterModels('CPHL')/_parameter/_fullName ! ", 
            "CHLOROPHYLL-A TOTAL", inputModel.getDataModel().getParameterModel("CPHL").getParameter().getFullName());
        
        Assert.assertEquals("Mauvais résultat sur _parameterModels('TPHS')/_parameter/_unit ! ", 
            "millimole/m3", inputModel.getDataModel().getParameterModel("TPHS").getParameter().getUnit());
    }

}

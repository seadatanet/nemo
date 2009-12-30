/* ============================================================================
 * Nom du fichier	: InputModelTest.java
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
import java.io.FileOutputStream;

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

import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;

/**
 * Class InputModelTest.java
 * Description: TODO
 * @author dboulang 
 */
public class InputModelTest
{

    private InputModel model;
    private XMLBootstrapParser parser;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception{
        model = new InputModel();
        parser = new XMLBootstrapParser(model);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception{
    }

    /**
     * @throws java.lang.Exception
     */
    @Test
    @SuppressWarnings("nls")
    public void parseInputModelXML() throws Exception{
        
        MediaModel media = Mockito.mock(MediaModel.class);
        CruiseModel cruise = Mockito.mock(CruiseModel.class);
        StationModel station = Mockito.mock(StationModel.class);
        DataModel data = Mockito.mock(DataModel.class);
        ConversionModel conversion = Mockito.mock(ConversionModel.class);
        
        model = new InputModel(media, cruise, station, data, conversion);
        parser = new XMLBootstrapParser(model);
        
        FileInputStream stream = new FileInputStream("test\\CampagneFICH_profile(diapalis2_bot)_NEW.xml");
        parser.parse(stream, false);
        
        checkValues_Profile(model);       
    }

    /**
     * @throws java.lang.Exception
     */
    @Test
    @SuppressWarnings("nls")
    public void parseModelXML() throws Exception{
        parseModel("test\\CampagneFICH_profile(diapalis2_bot)_NEW.xml");;
    }

    /**
     * @throws java.lang.Exception
     */
    @Test
    @SuppressWarnings("nls")
    public void parseOldModelXML() throws Exception{
        parseModel("test\\CampagneFICH_profile(diapalis2_bot).xml");
    }

    /**
     * @throws java.lang.Exception
     */
    @Test
    @SuppressWarnings("nls")
    public void saveEmptyModelXML() throws Exception{
        writeModel("test\\Test_saveEmptyCompleteModel.xml");
    }

    /**
     * @throws java.lang.Exception
     */
    @Test
    @SuppressWarnings("nls")
    public void saveModelXML() throws Exception{        
        parseModel("test\\CampagneFICH_profile(diapalis2_bot)_NEW.xml");;
        writeModel("test\\Test_saveCompleteModel.xml");
    }

    /**
     * @throws java.lang.Exception
     */
    @Test
    @SuppressWarnings("nls")
    public void transformOldModel() throws Exception{        
        parseModel("test\\CampagneFICH_profile(diapalis2_bot).xml");
        writeModel("test\\Test_saveOldModel.xml");
    }

    /**
     * @throws java.lang.Exception
     */
    @SuppressWarnings("nls")
    private void parseModel(String fileName) throws Exception{
        
        FileInputStream stream = new FileInputStream(fileName);
        parser.parse(stream, false);
        
        if (Global.EMPTY_STRING.equals(model.getVersion())){
            model = new InputModel();
            parser = new XMLBootstrapParser(model);
            
            StreamSource xmlSource = new StreamSource(new File(fileName));
            StreamSource xslSource = new StreamSource(new File(Intern.dir_resources + File.separator + "model.xsl"));
            Result result = new SAXResult(parser);
            
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(xslSource);
            transformer.transform(xmlSource, result);
        }
        checkAllValues_Profile();
    }


    /**
     * @throws java.lang.Exception
     */
    @SuppressWarnings("nls")
    private void writeModel(String fileName) throws Exception{
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        model.writeXMLOn(fileOutputStream);
    }
    
    private void checkAllValues_Profile(){
        checkValues_Profile(model);
        MediaModelTest.checkValues_Profile(model);
        CruiseModelTest.checkValues_Profile(model);
        StationModelTest.checkValues_Profile(model);
        DataModelTest.checkValues_Profile(model);
        ConversionModelTest.checkValues_Profile(model);
    }
    
    @SuppressWarnings("nls")
    private void checkValues_Profile(InputModel inputModel){
        Assert.assertEquals("Mauvais résultat sur _version ! ", 
            "1.2.20", inputModel.getVersion()); 
    }

}

/* ============================================================================
 * Nom du fichier	: DataDomainTest.java
 * Application      : NEMO version 1.2.20
 * Auteur			: dboulang
 * Date	de création : 9 déc. 09
 * Client			: Ifremer
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.domain;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.ifremer.nemo.domain.AddedParameter.IdParameter;
import fr.ifremer.nemo.input.DataModel;
import fr.ifremer.nemo.input.InputModel;
import fr.ifremer.nemo.input.Parameter;
import fr.ifremer.nemo.input.ParameterModel;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;

/**
 * Class DataDomainTest.java
 * Description: TODO
 * @author dboulang 
 */
@SuppressWarnings("nls")
public class DataDomainTest
{
    DataDomain dDomain;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        Parameter pPRES = new Parameter();
        pPRES.setCode("PRES");
        pPRES.setDefault("-999.9");
        pPRES.setFormat("%6.1f");
        pPRES.setFullName("SEA PRESSURE sea surface=0");
        pPRES.setUnit("decibar=10000 pascals");
        ParameterModel pmPRES = mock(ParameterModel.class);
        when(pmPRES.getParameter()).thenReturn(pPRES);
        
        Parameter pPHOS = new Parameter();
        pPHOS.setCode("PHOS");
        pPHOS.setDefault("99.99");
        pPHOS.setFormat("%5.2f");
        pPHOS.setFullName("PHOSPHATE (PO4-P) CONTENT");
        pPHOS.setUnit("millimole/m3");
        ParameterModel pmPHOS = mock(ParameterModel.class);
        when(pmPHOS.getParameter()).thenReturn(pPHOS);
        
        Parameter pNTRI = new Parameter();
        pNTRI.setCode("NTRI");
        pNTRI.setDefault("99.999");
        pNTRI.setFormat("%6.3f");
        pNTRI.setFullName("NTRI NITRITE (NO2-N) CONTENT");
        pNTRI.setUnit("millimole/m3");
        ParameterModel pmNTRI = mock(ParameterModel.class);
        when(pmNTRI.getParameter()).thenReturn(pNTRI);
/*        
        AddedParameter pAddMED = new AddedParameter();
        pAddMED.setIdParam(IdParameter.YEAR);
        pAddMED.setCible(Conversion.MEDATLAS);
        pAddMED.setCode("YEAR");
        pAddMED.setDefault("9999");
        pAddMED.setFormat("%4d");
        pAddMED.setFullName("YEAR");
        pAddMED.setUnit("yyyy");
        ParameterModel pmAddMED = mock(ParameterModel.class);
        when(pmAddMED.getParameter()).thenReturn(pAddMED);
        
        AddedParameter pAddODV = new AddedParameter();
        pAddODV.setIdParam(IdParameter.DEPH);
        pAddODV.setCible(Conversion.ODV);
        pAddODV.setCode("DEPH");
        pAddODV.setDefault(Global.ZERO);
        pAddODV.setFormat(Global.EMPTY_STRING);
        pAddODV.setFullName("DEPTH BELOW SEA SURFACE");
        pAddODV.setUnit("m");
        ParameterModel pmAddODV = mock(ParameterModel.class);
        when(pmAddODV.getParameter()).thenReturn(pAddODV);
*/

        DataModel dModel = mock(DataModel.class);
        when(dModel.getParameterModels()).thenReturn(new ParameterModel[]{pmPRES, pmPHOS, pmNTRI});
        
        InputModel iModel = mock(InputModel.class);
        when(iModel.getDataModel()).thenReturn(dModel);
        
        dDomain = new DataDomain();
        dDomain.update(iModel);
//        dDomain.addMedParameterModel(pmAddMED);
//        dDomain.addOdvParameterModel(pmAddODV);
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
    public void ajouteDEPHsurODVsiPROFILEetNOTEXISTetPRESexist() throws Exception{
        Assert.fail("TODO");
    }

    /**
     * @throws java.lang.Exception
     */
    @Test
    public void ajouteDEPHsurODVsiTIMESERIEetNOTEXIST() throws Exception{
        Assert.fail("TODO");
    }

    /**
     * @throws java.lang.Exception
     */
    @Test
    public void ajouteDEPHsurODVsiTRAJECTORYetNOTEXIST() throws Exception{
        Assert.fail("TODO");
    }

    /**
     * @throws java.lang.Exception
     */
    @Test
    public void ajouteYEARsurMEDATLASsiTIMESERIE() throws Exception{
        Assert.fail("TODO");
    }

    /**
     * @throws java.lang.Exception
     */
    @Test
    public void ajouteYEARsurMEDATLASsiTRAJECTORY() throws Exception{
        Assert.fail("TODO");
    }

    /**
     * @throws java.lang.Exception
     */
    @Test
    public void ajouteDTUTsurODVsiTIMESERIEetDATEISO() throws Exception{
        Assert.fail("TODO");
    }

    /**
     * @throws java.lang.Exception
     */
    @Test
    public void ajouteCJDYsurODVsiTIMESERIEetPasDATEISO() throws Exception{
        Assert.fail("TODO");
    }

    /**
     * @throws java.lang.Exception
     */
    @Test
    public void ajouteLATXsurMEDATLASsiTRAJECTORY() throws Exception{
        Assert.fail("TODO");
    }

    /**
     * @throws java.lang.Exception
     */
    @Test
    public void ajouteBATHsurMEDATLASsiTRAJECTORYetDEPTHinData() throws Exception{
        Assert.fail("TODO");
    }

    /**
     * @throws java.lang.Exception
     */
    @Test
    public void correcteMiseEnFormeDuDataHeaderMEDATLAS() throws Exception{
        
        StringBuffer medDataHeader = dDomain.getDataHeader(Conversion.MEDATLAS);
        System.out.println(medDataHeader);
        Assert.assertEquals("Le Data Header MEDATLAS n'est pas conforme",
                            medDataHeader.toString(), 
                            "*YEAR PRES   PHOS  NTRI   \r\n");
    }

    /**
     * @throws java.lang.Exception
     */
    @Test
    public void correcteMiseEnFormeDeLaDerniereDataLineMEDATLAS() throws Exception{
        
        StringBuffer medData = dDomain.getMedatlasLastDataLine();
        System.out.println(medData);
        Assert.assertEquals("La dernière DataLine MEDATLAS n'est pas conforme",
                            medData.toString(), 
                            "9999 -999.9 99.99 99.999 \r\n");
    }

    /**
     * @throws java.lang.Exception
     */
    @Test
    public void correcteMiseEnFormeDuDataHeaderODV() throws Exception{
        
        StringBuffer odvDataHeader = dDomain.getDataHeader(Conversion.ODV);
        System.out.println(odvDataHeader);
        Assert.assertEquals("Le Data Header ODV n'est pas conforme",
                            odvDataHeader.toString(), 
                            "DEPH [m]\tQV:SEADATANET\tPRES [decibar=10000 pascals]\tQV:SEADATANET\tPHOS [millimole/m3]\tQV:SEADATANET\tNTRI [millimole/m3]\tQV:SEADATANET\t\r\n");
    }

}

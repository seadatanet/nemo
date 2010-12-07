/* ============================================================================
 * Nom du fichier	: CruiseDomain.java
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

import com.ibm.icu.text.SimpleDateFormat;

import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.input.CruiseModel;
import fr.ifremer.nemo.input.DataModel;
import fr.ifremer.nemo.input.DataType;
import fr.ifremer.nemo.input.Parameter;
import fr.ifremer.nemo.input.ParameterModel;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global.E_Availability;

/**
 * Class CruiseDomain.java
 * Description: TODO
 * @author dboulang 
 */
@SuppressWarnings("nls")
public class CruiseDomainTest
{
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); //$NON-NLS-1$

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
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
    public void correcteMiseEnFormeDuCruiseHeaderODV() throws Exception{
       
        NemoLists.class.newInstance();
        
        CruiseModel model = mock(CruiseModel.class);
        when(model.getComment()).thenReturn("campagne de mise a l'eau car donnees correspondantes aux dates\r\n" +
                                            "campagne de mise a l'eau car donnees correspondantes aux dates\r\n" +
                                            "campagne de mise a l'eau car donnees correspondantes aux dates\r\n");

        
        Parameter pPRES = mock(Parameter.class);
        when(pPRES.getCode()).thenReturn("PRES");
        ParameterModel pmPRES = mock(ParameterModel.class);
        when(pmPRES.getParameter()).thenReturn(pPRES);
        
        Parameter pPHOS = mock(Parameter.class);
        when(pPHOS.getCode()).thenReturn("PHOS");
        ParameterModel pmPHOS = mock(ParameterModel.class);
        when(pmPHOS.getParameter()).thenReturn(pPHOS);
        
        Parameter pNTRI = mock(Parameter.class);
        when(pNTRI.getCode()).thenReturn("NTRI");
        ParameterModel pmNTRI = mock(ParameterModel.class);
        when(pmNTRI.getParameter()).thenReturn(pNTRI);
        
        AddedParameter addpTEST = mock(AddedParameter.class);
        when(addpTEST.getCode()).thenReturn("DEPH");
        //when(addpTEST.getCible()).thenReturn(Conversion.ODV);
        ParameterModel addpmTEST = mock(ParameterModel.class);
        when(addpmTEST.getParameter()).thenReturn(addpTEST);
        
        
        DataModel dModel = mock(DataModel.class);
        when(dModel.getParameterModels()).thenReturn(new ParameterModel[]{pmPRES, pmPHOS, pmNTRI});
        
        DataDomain dDomain = mock(DataDomain.class);
        when(dDomain.getModel()).thenReturn(dModel);
        when(dDomain.getOdvParameterModels()).thenReturn(new ParameterModel[]{addpmTEST});
        
        StationDomain sDomain = mock(StationDomain.class);
        when(sDomain.getData()).thenReturn(dDomain);
        
        
        CruiseDomain cruise = new CruiseDomain();
        cruise.setModel(model);
        cruise.setStation(sDomain);
        
        StringBuffer cruiseHeader = cruise.getHeader(Conversion.ODV);
        System.out.println(cruiseHeader);
        
        Assert.assertEquals("Le Cruise Header n'est ODV pas conforme",
                            cruiseHeader.toString(), 
                            "//campagne de mise a l'eau car donnees correspondantes aux dates\r\n" +
                            "//campagne de mise a l'eau car donnees correspondantes aux dates\r\n" +
                            "//campagne de mise a l'eau car donnees correspondantes aux dates\r\n" +
                            "//\r\n" +
                            "//" +
                            "//SDN_parameter_mapping" +
                            "//<subject>SDN:LOCAL:DEPH</subject><object>SDN:P011::ADEPZZ01</object><units>SDN:P061::ULAA</units>\r\n" +
                            "//<subject>SDN:LOCAL:PRES</subject><object>SDN:P011::PRESPS02</object><units>SDN:P061::UPDB</units>\r\n" +
                            "//<subject>SDN:LOCAL:PHOS</subject><object>SDN:P011::PHOSZZXX</object><units>SDN:P061::UPOX</units>\r\n" +
                            "//<subject>SDN:LOCAL:NTRI</subject><object>SDN:P011::NTRIZZXX</object><units>SDN:P061::UPOX</units>\r\n" +
                            "//\r\n" +
                            "Cruise\tStation\tType\tyyyy-mm-ddThh:mm:ss.sss\tLongitude [degrees_east]\tLatitude [degrees_north]\tLOCAL_CDI_ID\tEDMO_CODE\tBot. Depth [m]\tDEPH [m]\tQV:SEADATANET\tPRES [decibar=10000 pascals]\tQV:SEADATANET\tPHOS [millimole/m3]\tQV:SEADATANET\tNTRI [millimole/m3]\tQV:SEADATANET\t\r\n");
    }

    /**
     * @throws java.lang.Exception
     */
    @Test
    public void correcteMiseEnFormeDuCruiseHeaderMEDATLAS() throws Exception{

        DataType dt1 = mock(DataType.class);
        when(dt1.getCode()).thenReturn("H10");
        when(dt1.getNumberOfProfiles()).thenReturn(44);
        when(dt1.isQc()).thenReturn(true);

        DataType dt2 = mock(DataType.class);
        when(dt2.getCode()).thenReturn("H09");
        when(dt2.getNumberOfProfiles()).thenReturn(2);
        when(dt2.isQc()).thenReturn(false);
        
        CruiseModel model = mock(CruiseModel.class);
        when(model.getAvailability()).thenReturn(E_Availability.publicly);
        when(model.getChiefScientist()).thenReturn("Laurent MEMERY");
        when(model.getComment()).thenReturn("campagne de mise a l'eau car donnees correspondantes aux dates\r\n" +
                                            "campagne de mise a l'eau car donnees correspondantes aux dates\r\n" +
                                            "campagne de mise a l'eau car donnees correspondantes aux dates\r\n");
        when(model.getCountryCode()).thenReturn("35");
        when(model.getDataCentre()).thenReturn("FI");
        when(model.getEndDate()).thenReturn(dateFormat.parse("13/02/1994").getTime());
        when(model.getLaboratories()).thenReturn("LABORATORY OF SCIENCES OF MARINE ENVIRONMENT (LEMAR) - LABORATORY of OCEANOGRAPHY");
        when(model.getName()).thenReturn("CITHER 2 LEG 1");
        when(model.getProjectName()).thenReturn("WOCE FRANCE - HYDROLOGY {acrWWWWWWWWWWWWWWWWWWW)");
        when(model.getReference()).thenReturn("FI35199480001");
        when(model.getRegionName()).thenReturn("South Atlantic Ocean");
        when(model.getShipCode()).thenReturn("3230");
        when(model.getShipName()).thenReturn("Maurice Ewing");
        when(model.getStartDate()).thenReturn(dateFormat.parse("04/01/1994").getTime());
        when(model.getDataTypes()).thenReturn(new DataType[]{dt1,dt2});
        
        CruiseDomain cruise = new CruiseDomain();
        cruise.setModel(model);
        
        StringBuffer cruiseHeader = cruise.getHeader(Conversion.MEDATLAS);
        System.out.println(cruiseHeader);
        
        Assert.assertEquals("Le Cruise Header MEDATLAS n'est pas conforme",
                            cruiseHeader.toString(), 
                            "*FI35199480001 CITHER 2 LEG 1                   3230 Maurice Ewing            \r\n" +
                            "04/01/1994-13/02/1994 South Atlantic Ocean                              \r\n" +
                            "35 LABORATORY OF SCIENCES OF MARINE ENVIRONMENT (LEMAR) - LABORATORY of OCEANO\r\n" +
                            "Laurent MEMERY                           Project=WOCE FRANCE - HYDROLOGY {acr\r\n" +
                            "Regional Archiving= FI                   Availability=P\r\n" +
                            "Data Type=H10 n=44   QC=Y\r\n" +
                            "Data Type=H09 n=2    QC=N\r\n" +
                            "COMMENT\r\n" +
                            "campagne de mise a l'eau car donnees correspondantes aux dates\r\n" +
                            "campagne de mise a l'eau car donnees correspondantes aux dates\r\n" +
                            "campagne de mise a l'eau car donnees correspondantes aux dates\r\n");
    }

}

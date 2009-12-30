/* ============================================================================
 * Nom du fichier	: ParameterTest.java
 * Application      : NEMO version 1.2.20
 * Auteur			: dboulang
 * Date	de création : 17 déc. 09
 * Client			: Ifremer
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.input;


import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.ifremer.nemo.util.Conversion;

/**
 * Class ParameterTest.java
 * Description: TODO
 * @author dboulang 
 */
@SuppressWarnings("nls")
public class ParameterTest
{
    Parameter pPRES;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        pPRES = new Parameter();
        pPRES.setCode("PRES");
        pPRES.setDefault("-999.9");
        pPRES.setFormat("%6.1f");
        pPRES.setFullName("SEA PRESSURE sea surface=0");
        pPRES.setUnit("decibar=10000 pascals");
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
    public void correcteMiseEnFormeDuCodePRESpourMEDATLAS() throws Exception{
        
        
        String code = pPRES.getOutputCode(Conversion.MEDATLAS);
        System.out.println(code);
        Assert.assertEquals("Le code Paramètre MEDATLAS n'est pas conforme",
                            code, 
                            "PRES   ");
    }

    /**
     * @throws java.lang.Exception
     */
    @Test
    public void correcteMiseEnFormeDuCodePRESpourODV() throws Exception{
        
        
        String code = pPRES.getOutputCode(Conversion.ODV);
        System.out.println(code);
        Assert.assertEquals("Le code Paramètre ODV n'est pas conforme",
                            code, 
                            "PRES [decibar=10000 pascals]\tQV:SEADATANET\t");
    }

}

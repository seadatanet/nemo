/* ============================================================================
 * Nom du fichier	: StationDomainTest.java
 * Application      : NEMO version 1.2.20
 * Auteur			: dboulang
 * Date	de création : 9 déc. 09
 * Client			: Ifremer
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.domain;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.ifremer.nemo.Nemo;

/**
 * Class StationDomainTest.java
 * Description: TODO
 * @author dboulang 
 */
@SuppressWarnings("nls")
public class StationDomainTest
{

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
    
    @Test
    public void calculCorrectTypeBCforProfile() throws Exception{
        
        
        // profile H09 -> B
        Nemo.getInstance().openModel("test\\CampagneFICH_profile(diapalis2_bot).xml");
        //Assert.assertEquals("B", Nemo.getInstance().getDomain().getCruise().getStation().getTypeBC(ref));
        
        // profile H10 -> C
        Nemo.getInstance().openModel("test\\CampagneFICH_profile(diapalis2_bot)_H10.xml");
        
        // profile H13 -> C
        Nemo.getInstance().openModel("test\\CampagneFICH_profile(diapalis2_bot)_H13.xml");
        
        // profile other
        // - de 250 lignes de données pour la station -> B
        Nemo.getInstance().openModel("test\\CampagneFICH_profile(diapalis2_bot)_O250.xml");
        
        // 250 lignes de données ou + pour la station -> C
        Nemo.getInstance().openModel("test\\CampagneFICH_profile(diapalis2_bot)_O.xml");
    }

}

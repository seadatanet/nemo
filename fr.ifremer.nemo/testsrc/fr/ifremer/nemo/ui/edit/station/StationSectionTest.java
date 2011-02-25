/* ============================================================================
 * Nom du fichier	: StationSectionTest.java
 * Auteur			: sbregent
 * Date				: 2 févr. 11
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.station;

import static org.junit.Assert.*;

import java.text.ParseException;

import junit.framework.Assert;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: TODO
 * @author sbregent
 *
 */
public class StationSectionTest
{
    public String res;
    public static boolean isDebug=true;
    public static StationSection latitudeSection;
    public static StationSection longitudeSection;
    public static Logger testLogger ;

    /**
     * formats longitude
     */
    public static String longitudeFormatString_A = "+DDD MM.mm"; 
    public static String longitudeFormatString_B = "DDD MM.mm N";
    public static String longitudeFormatString_C = "NDDD MM.mm"; //diap, mona
    public static String longitudeFormatString_D = "+DDD.ddd"; //trajectory
    public static String longitudeFormatString_DD = "+DDD.ddddd"; //6432 collec campagnes
    public static String longitudeFormatString_E = "+DD.dddd"; //2967

    /**
     * formats latitude
     */
    public static String latitudeFormatString_C = "NDD MM.mm"; //diap, mona
    public static String latitudeFormatString_D = "+DD.ddd"; //trajectory
    public static String latitudeFormatString_DD = "+DD.ddddd"; //6432 collec campagnes

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        // LOGGER
        DOMConfigurator.configure("resources/log4jBatch.xml"); //$NON-NLS-1$
        testLogger = Logger.getLogger("nemoJunits");//$NON-NLS-1$
        testLogger.setLevel(Level.DEBUG);
        
        latitudeSection =
            new StationSection(Intern.S_LAT, Messages.getString(Intern.S_LAT),
                Messages.edit_station_sectionLat_lib, false, true, 1, null);
        longitudeSection =
            new StationSection(Intern.S_LON, Messages.getString(Intern.S_LON),
                Messages.edit_station_sectionLon_lib, false, true, 1, null);
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

    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
    }
    
    /**
     * 
     * LONGITUDES
     */
    @Test
    public final void testCalcLongitudeTest_A1()
    {
        // formatString_A = "+DDD MM.mm"; 
        testLogger.info("");
        res = longitudeSection.calcLongitudeTest(longitudeFormatString_A,"+  1 22.33", false, isDebug );
        checkRes("E001 22.33", res);
    }
    @Test
    public final void testCalcLongitudeTest_A2()
    {
        // formatString_A = "+DDD MM.mm"; 
        testLogger.info("");
        res = longitudeSection.calcLongitudeTest(longitudeFormatString_A,"+ 11 22.33", false, isDebug );
        checkRes( "E011 22.33", res);
    }
    @Test
    public final void testCalcLongitudeTest_A3()
    {
        // formatString_A = "+DDD MM.mm"; 
        testLogger.info("");
        res = longitudeSection.calcLongitudeTest(longitudeFormatString_A,"+111 22.33", false, isDebug );
        checkRes("E111 22.33", res);
    }
    @Test
    public final void testCalcLongitudeTest_A4()
    {
        // formatString_A = "+DDD MM.mm";       
        testLogger.info(""); //$NON-NLS-1$
        res = longitudeSection.calcLongitudeTest(longitudeFormatString_A,"  +1 22.33", false , isDebug);
        checkRes( "E001 22.33", res);
    }
    @Test
    public final void testCalcLongitudeTest_A5()
    {
        // formatString_A = "+DDD MM.mm"; 
        testLogger.info("");
        res = longitudeSection.calcLongitudeTest(longitudeFormatString_A," +11 22.33", false, isDebug );
        checkRes( "E011 22.33", res);
    }


    @Test
    public final void testCalcLongitudeTest_B1()
    {
        // formatString_B = "DDD MM.mm N";
        testLogger.info("");
        res = longitudeSection.calcLongitudeTest(longitudeFormatString_B,"  1 22.33 E", false , isDebug);
        checkRes("E001 22.33", res);
    }
    @Test
    public final void testCalcLongitudeTest_B2()
    {
        // formatString_B = "DDD MM.mm N";
        testLogger.info("");
        res = longitudeSection.calcLongitudeTest(longitudeFormatString_B," 11 22.33 E", false , isDebug);
        checkRes( "E011 22.33", res);
    }
    @Test
    public final void testCalcLongitudeTest_B3()
    {
        // formatString_B = "DDD MM.mm N";
        testLogger.info("");
        res = longitudeSection.calcLongitudeTest(longitudeFormatString_B,"111 22.33 E", false , isDebug);
        checkRes( "E111 22.33", res);
    }       

    @Test
    public final void testCalcLongitudeTest_C1()
    {
        // formatString_C = "NDDD MM.mm";
        testLogger.info("");
        res = longitudeSection.calcLongitudeTest(longitudeFormatString_C,"E111 22.33", false , isDebug);
        checkRes( "E111 22.33", res);
    }
    @Test
    public final void testCalcLongitudeTest_C2()
    {
        // formatString_C = "NDDD MM.mm";
        testLogger.info("");
        res = longitudeSection.calcLongitudeTest(longitudeFormatString_C,"E 11 22.33", false , isDebug);
        checkRes( "E011 22.33", res);
    }
    @Test
    public final void testCalcLongitudeTest_C3()
    {
        // formatString_C = "NDDD MM.mm";
        testLogger.info("");
        res = longitudeSection.calcLongitudeTest(longitudeFormatString_C,"E  1 22.33", false, isDebug );
        checkRes("E001 22.33", res);
    }
    @Test
    public final void testCalcLongitudeTest_C4()
    {
        // formatString_C = "NDDD MM.mm";
        testLogger.info("");
        res = longitudeSection.calcLongitudeTest(longitudeFormatString_C,"E 1 22.33", false, isDebug );
        checkRes( "E001 22.33", res);
    } @Test
    public final void testCalcLongitudeTest_C4bis()
    {
        // formatString_C = "NDDD MM.mm";
        testLogger.info("");
        res = longitudeSection.calcLongitudeTest(longitudeFormatString_C," E 1 22.33", true, isDebug );
        checkRes( "E001 22.33", res);
    }
    @Test
    public final void testCalcLongitudeTest_C5()
    {
        // formatString_C = "NDDD MM.mm";
        testLogger.info("");
        res = longitudeSection.calcLongitudeTest(longitudeFormatString_C,"E 1 22.33", true, isDebug );
        checkRes( "E001 22.33", res);
    }
    
   @Test
    public final void testCalcLongitudeTest_D1()
    {
        //     ex: trajectory
       // longitudeFormatString_D = "+DDD.ddd"; 
       testLogger.info("");
        res = longitudeSection.calcLongitudeTest(longitudeFormatString_D," 7.749580", true , isDebug);
        checkRes( "E007 44.97", res);
    }
   @Test
   public final void testCalcLongitudeTest_D2()
   {
       //     ex: trajectory
      // longitudeFormatString_D = "+DDD.ddd"; 
       testLogger.info("");
       res = longitudeSection.calcLongitudeTest(longitudeFormatString_D,"-7.749580", true , isDebug);
       checkRes( "W007 44.97", res);
   }
   @Test
   public final void testCalcLongitudeTest_D3()
   {
       //     ex: trajectory
      // longitudeFormatString_D = "+DDD.ddd"; 
       testLogger.info("");
        res = longitudeSection.calcLongitudeTest(longitudeFormatString_D,"-77.749580", true , isDebug);
       checkRes( "W077 44.97", res);
   }
   @Test
   public final void testCalcLongitudeTest_D4()
   {
       //     ex: trajectory
      // longitudeFormatString_D = "+DDD.ddd"; 
       testLogger.info("");
        res = longitudeSection.calcLongitudeTest(longitudeFormatString_D,"-127.749580", true , isDebug);
       checkRes( "W127 44.97", res);
   }
   @Test
   public final void testCalcLongitudeTest_DD1()
   {
//     6432 collec campagnes
      // longitudeFormatString_DD = "+DDD.ddddd"; 
       testLogger.info("");
        res = longitudeSection.calcLongitudeTest(longitudeFormatString_DD,"-113.22480", false , isDebug);
       checkRes( "W113 13.49", res);
   }
   @Test
   public final void testCalcLongitudeTest_DD2()
   {
//     6432 collec campagnes
      // longitudeFormatString_DD = "+DDD.ddddd"; 
       testLogger.info("");
        res = longitudeSection.calcLongitudeTest(longitudeFormatString_DD,"   3.22480", false , isDebug);
       checkRes( "E003 13.49", res);
   }
   @Test
   public final void testCalcLongitudeTest_DD3()
   {
//     6432 collec campagnes
      // longitudeFormatString_DD = "+DDD.ddddd"; 
       testLogger.info("");
        res = longitudeSection.calcLongitudeTest(longitudeFormatString_DD,"  -3.22480", false , isDebug);
       checkRes( "W003 13.49", res);
   }
   @Test
   public final void testCalcLongitudeTest_DD4()
   {
//     6432 collec campagnes
      // longitudeFormatString_DD = "+DDD.ddddd"; 
       testLogger.info("");
        res = longitudeSection.calcLongitudeTest(longitudeFormatString_DD,"-  3.22480", false , isDebug);
       checkRes( "W003 13.49", res);
   }
   @Test
   public final void testCalcLongitudeTest_E1()
   {
//     2967
      // longitudeFormatString_E
       testLogger.info("");
        res = longitudeSection.calcLongitudeTest(longitudeFormatString_E," -29.5500", false , isDebug);
       checkRes( "W029 33.00", res);
   }
   @Test
   public final void testCalcLongitudeTest_E2()
   {
//     2967
      // longitudeFormatString_E 
       testLogger.info("");
        res = longitudeSection.calcLongitudeTest(longitudeFormatString_E,"  -2.5000", false , isDebug);
       checkRes( "W002 30.00", res);
   }
   
    @Test
    public final void testCalcLongitudeTest_X1()
    {
//      SO_202_31_1_down.cnv
//      ** Latitude: 49° 40.685' N
//      ** Longitude: 152° 32.778' W
        testLogger.info("");
          res = longitudeSection.calcLongitudeTest("DDD  MM.mmm  N","152° 32.778' W", false , isDebug);
        checkRes( "W152 32.77", res);
    }
   
    @Test
    public final void testCalcLongitudeTest_X2()
    {
//      SO_202_31_1_down.cnv
//      ** Latitude: 49° 40.685' N
//      ** Longitude: 152° 32.778' W
        testLogger.info("");
          res = longitudeSection.calcLongitudeTest("DDD° MM.mmm' N","152° 32.778' W", false , isDebug);
        checkRes( "W152 32.77", res);
    }
    @Test
    public final void testCalcLongitudeTest_edf1()
    {
// mail collection fichiers origine, extraits = 3603881_t5_00806.edf et 3624379_t5_00851.edf
//        48 58.885E
//        57 27.819E
        testLogger.info("");
          res = longitudeSection.calcLongitudeTest("DDD MM.mmN","48 58.885E", true , isDebug);
        checkRes( "E048 58.89", res);
    }
    @Test
    public final void testCalcLongitudeTest_edf2()
    {
// mail collection fichiers origine, extraits = 3603881_t5_00806.edf et 3624379_t5_00851.edf
//        48 58.885E
//        57 27.819E
        testLogger.info("");
          res = longitudeSection.calcLongitudeTest("DDD MM.mmN","148 58.885E", true , isDebug);
        checkRes( "E148 58.88", res);
    }
    @Test
    public final void testCalcLongitudeTest_canyon()
    {
// mail collection fichiers origine, extraits = 3603881_t5_00806.edf et 3624379_t5_00851.edf
//        48 58.885E
//        57 27.819E
        testLogger.info("");
          res = longitudeSection.calcLongitudeTest("DD.ddddd","3.22480", true , isDebug);
        checkRes( "E003 13.49", res);
    }
    @Test
    public final void testCalcLongitudeTest_canyon2()
    {
// mail collection fichiers origine, extraits = 3603881_t5_00806.edf et 3624379_t5_00851.edf
//        48 58.885E
//        57 27.819E
        testLogger.info("");
          res = longitudeSection.calcLongitudeTest("DD.ddddd"," 3.22480", false , isDebug);
        checkRes( "E003 13.49", res);
    }
    /**
     * LATITUDES
     *
     */
    @Test
    public final void testCalcLatitudeTest_C3()
    {
        //ex: mona, jade
//      latitudeFormatString_C = "NDD MM.mm";    
        
        testLogger.info("");
         res = latitudeSection.calcLatitudeTest(latitudeFormatString_C,"N 1 22.33", false , isDebug); //$NON-NLS-1$
        checkRes("N01 22.33", res);
    }
    @Test
    public final void testCalcLatitudeTest_C4()
    {
        //ex: mona, jade
//      latitudeFormatString_C = "NDD MM.mm";     
        testLogger.info("");
         res = latitudeSection.calcLatitudeTest(latitudeFormatString_C,"N1 22.33", false , isDebug); //$NON-NLS-1$
        checkRes("N01 22.33", res);
    }
    @Test
    public final void testCalcLatitudeTest_C5()
    {
        //ex: mona, jade, diap
//      latitudeFormatString_C = "NDD MM.mm";     
        testLogger.info("");
         res = latitudeSection.calcLatitudeTest(latitudeFormatString_C,"N11 22.33", false , isDebug); //$NON-NLS-1$
        checkRes( "N11 22.33", res);
    }
    @Test
    public final void testCalcLatitudeTest_D1()
    {
        //ex: trajectory   
        //latitudeFormatString_D = "+DD.ddd"; 
        testLogger.info("");
        res = latitudeSection.calcLatitudeTest(latitudeFormatString_D," 42.346631", true , isDebug); //$NON-NLS-1$
        checkRes("N42 20.80", res);
    }
    @Test
    public final void testCalcLatitudeTest_D2()
    {
        //ex: trajectory   
        //latitudeFormatString_D = "+DD.ddd"; 
        testLogger.info("");
          res = latitudeSection.calcLatitudeTest(latitudeFormatString_D,"-42.346631", true , isDebug); //$NON-NLS-1$
        checkRes("S42 20.80", res);
    }
    @Test
    public final void testCalcLatitudeTest_D3()
    {
        //ex: trajectory   
        //latitudeFormatString_D = "+DD.ddd"; 
        testLogger.info("");
          res = latitudeSection.calcLatitudeTest(latitudeFormatString_D,"-2.346631", true , isDebug); //$NON-NLS-1$
        checkRes("S02 20.80", res);
    }
    @Test
    public final void testCalcLatitudeTest_DD1()
    {
//      6432 collec campagnes
//        latitudeFormatString_DD = "+DD.ddddd"; 
        testLogger.info("");
          res = latitudeSection.calcLatitudeTest(latitudeFormatString_DD," 42.37190", false , isDebug); //$NON-NLS-1$
        checkRes("N42 22.31", res);
    }
    @Test
    public final void testCalcLatitudeTest_DD2()
    {
//      6432 collec campagnes
//        latitudeFormatString_DD = "+DD.ddddd"; 
        testLogger.info("");
          res = latitudeSection.calcLatitudeTest(latitudeFormatString_DD,"-42.37190", false , isDebug); //$NON-NLS-1$
        checkRes("S42 22.31", res);
    }
    @Test
    public final void testCalcLatitudeTest_DD3()
    {
//      6432 collec campagnes
//        latitudeFormatString_DD = "+DD.ddddd"; 
        testLogger.info("");
          res = latitudeSection.calcLatitudeTest(latitudeFormatString_DD,"- 2.37190", false , isDebug); //$NON-NLS-1$
        checkRes("S02 22.31", res);
    }
    @Test
    public final void testCalcLatitudeTest_DD4()
    {
//      6432 collec campagnes
//        latitudeFormatString_DD = "+DD.ddddd"; 
        testLogger.info("");
          res = latitudeSection.calcLatitudeTest(latitudeFormatString_DD," -2.37190", false , isDebug); //$NON-NLS-1$
        checkRes("S02 22.31", res);
    }
    
    @Test
    public final void testCalcLatitudeTest_X1()
    {
//      SO_202_31_1_down.cnv
//      ** Latitude: 49° 40.685' N
//      ** Longitude: 152° 32.778' W
        testLogger.info("");
          res = latitudeSection.calcLatitudeTest("DD  MM.mmm  N","49° 40.685' N", false , isDebug);
        checkRes( "N49 40.685", res);
    }
    @Test
    public final void testCalcLatitudeTest_edf1()
    {
//        Latitude      :  9 55.035S
//        Latitude      :  11 46.648N
        testLogger.info("");
          res = latitudeSection.calcLatitudeTest("DD MM.mmmmN","9 55.035S ", true , isDebug);
        checkRes( "S09 55.03", res);
    }
    @Test
    public final void testCalcLatitudeTest_edf2()
    {
//        Latitude      :  9 55.035S
//        Latitude      :  11 46.648N
        testLogger.info("");
          res = latitudeSection.calcLatitudeTest("DD MM.mmmmN","11 46.648N", true , isDebug);
        checkRes( "N11 46.65", res);
    }
    public void checkRes(String attendu, String res)
    {
        if (!attendu.equals(res))
        {
            System.out.println("!!!!!!!! ko"); //$NON-NLS-1$
        }
        Assert.assertEquals("ko", attendu, res); //$NON-NLS-1$
    }
}

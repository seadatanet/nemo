/* ============================================================================
 * Nom du fichier	: CalcPresTest.java
 * Auteur			: ycaudal
 * Date				: 13 janv. 10
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.util.presdepth;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Description: TODO
 * 
 * @author ycaudal
 * 
 */
@SuppressWarnings("nls")
public class CalcPresTest
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
    
    /**
     * @param args
     */
    @Test
    public void calculPressionTest()
    {

        try
        {
            String repertoire = "test/PRES_DEPTH";

            String[] fichiersTests =
                new String[] {"valeurs_test_1.txt", "valeurs_test_2.txt"};

            for (String fichier : fichiersTests)
            {
                
                System.out.println("Test file : " + fichier);
                
                CalcPres.init();
                
                BufferedReader br =
                    new BufferedReader(new FileReader(new File(repertoire + "/"
                            + fichier)));
                
                String ligne = br.readLine();
                
                double latitude =
                    Double.parseDouble(ligne.substring(0, 7).trim());
                
                ligne = br.readLine();                
                ligne = br.readLine();
                
                int numLine = 3;

                int numOk = 0;
                
                int numKO = 0;
                
                while (ligne != null)
                {
                    String valPres = ligne.substring(0, 6).trim().replace('.',',');
                    
                    double valDepth =
                        Double.parseDouble(ligne.substring(6, 13).trim());
                    double valTemp =
                        Double.parseDouble(ligne.substring(13, 20).trim());
                    double valPSal =
                        Double.parseDouble(ligne.substring(20, 27).trim());

                    double res =
                        CalcPres.calcPres(latitude, valPSal, valDepth, valTemp);
                    
                    String resString = String.format("%.1f", res);
                    
                    if (!valPres.trim().equals(resString))
                    {
                        System.out.println("Wrong value file " + fichier
                                + " line " + numLine + " value file : "
                                + valPres + " value computed " + resString);
                        numKO++;
                    }
                    else
                    {
                        numOk++;
                    }
                    
                    ligne = br.readLine();
                    numLine++;
                }
                System.out.println("OK " + numOk + " KO " + numKO);
                br.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

}

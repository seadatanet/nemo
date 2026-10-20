/* ============================================================================
 * Nom du fichier	: CalcPres.java
 * Auteur			: ycaudal
 * Date				: 13 janv. 10
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.util.presdepth;

/**
 * Description: Calcul de la pression en fonction de la profondeur
 * Nécessite également la salinité, la température et la latitude
 * @author ycaudal
 *
 */
public class CalcPres
{

    private static boolean prem = true;
    
    private static final double gamma = -0.2226e-05;
    
    private static double pression_avant;
    
    private static double depth_avant;
    
    private static double fi_avant;

    private static double g0;
    
    /**
     * Fonction d'initialisation à appeler avant le calcul de la première valeur du profil     *
     */
    public static void init()
    {
        prem = true;
        pression_avant = 0;
        depth_avant = 0;
        fi_avant = 0;
        g0 = 0;
    }
    
    /**
     * Calcul de la pression
     * 
     * @param latitude : nécessaire uniquement pour le premier appel après init
     * @param salinity : salinité de l'eau
     * @param depth : profondeur
     * @param temperature : température
     * @return
     */
    public static double calcPres(double latitude, double salinity, double depth, double temperature)
    {
       double teta = Tetai8.CP_tetai8(depth, temperature, salinity, 0.0);
       double alp = Vspe.CP_vspe08(0.0 , teta, salinity);
       double densite = (1.0)/alp ;
       
       double pression;
       
       //Première valeur du profil
       if (prem)
       {
           prem = false;
           g0 = Gravit.gravit(latitude);
           pression = depth*(densite)*(g0+0.5*gamma*(-depth))*1.0e-04;
           fi_avant = densite * (g0 + gamma * (- depth));
           pression_avant = pression;
           depth_avant = depth;
       }
       else
       {
           double fi = densite * (g0 + gamma * (-depth));
           fi_avant = fi;
           fi = ( fi_avant + fi )*0.5e-04;
           pression = pression_avant + fi*(depth - depth_avant);
           pression_avant = pression;
           depth_avant = depth;
       }
       
       return pression;
    }

    
}

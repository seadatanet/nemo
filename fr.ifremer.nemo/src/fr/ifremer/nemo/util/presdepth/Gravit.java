/* ============================================================================
 * Nom du fichier	: Gravit.java
 * Auteur			: ycaudal
 * Date				: 13 janv. 10
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.util.presdepth;

/**
 * Description: calcul de gravité en fonction de la latitude
 * @author ycaudal
 *
 */
public class Gravit
{
    protected static double gravit(double xlat)
    {
    double degrad;
    double pi = 3.141592653589793;
    double alat;
    double slat;
    double slat2;
    double ge;
    double b2;
    double b4;
    double g;

    degrad = pi/180.0;
    alat   = xlat*degrad;
    slat   = Math.sin(alat);
    slat2  = Math.sin(alat*2.00);
    ge     = 9.780318;
    b2     = 0.530244e-02;
    b4     = -0.585e-05;
    g = ge*(1.00 +b2*slat*slat+b4*slat2*slat2);
    return(g);
    }
}

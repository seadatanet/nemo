/* ============================================================================
 * Nom du fichier	: Tetai8.java
 * Auteur			: ycaudal
 * Date				: 13 janv. 10
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.util.presdepth;

/**
 * Description: TODO
 * 
 * @author ycaudal
 * 
 */
public class Tetai8
{
    protected static double CP_tetai8(double da_xp8, double da_xt8,
            double da_xs8, double da_xpr8)
    {
        /*
         * Declaration des parametres
         */
        double dl_p;
        double dl_t;
        double dl_s;
        double dl_pr;
        double dl_dp;
        double dl_t2;
        double dl_t3;
        double dl_p2;
        double dl_teta1 = 0;
        double dl_tetai8;
        double dl_g1 = 0;
        double dl_q2 = 0;
        double dl_q3;
        double dl_g;
        double dl_g2 = 0;
        double dl_g3 = 0;
        double dl_g4;
        double dl_teta2 = 0;
        double dl_teta3 = 0;
        double dl_teta4;
        double dl_a000;
        double dl_a001;
        double dl_a002;
        double dl_a003;
        double dl_a010;
        double dl_a011;
        double dl_a100;
        double dl_a101;
        double dl_a102;
        double dl_a103;
        double dl_a110;
        double dl_a111;
     //   double dl_a11;
        double dl_a200;
        double dl_a201;
        double dl_a202;
        int i;

        /*
         * Initialisation
         */
        dl_a000 =  .35803e-1;
        dl_a001 =  .85258e-2;
        dl_a002 =  -.68360e-4;
        dl_a003 =  .66228e-6;
        dl_a010 =  .18932e-2;
        dl_a011 =  -.42393e-4;
        dl_a100 =  .18741e-4;
        dl_a101 =  -.67795e-6;
        dl_a102 =  .87330e-8;
        dl_a103 =  -.54481e-10;
        dl_a110 =  -.11351e-6;
        dl_a111 =  .27759e-8;
        dl_a200 =  -.46206e-9;
        dl_a201 =  .18676e-10;
        dl_a202 =  -.21687e-12;
        /*
         * Debut du programme de calcul
         */
        dl_p = da_xp8;
        dl_t = da_xt8 *  1.00024;
        dl_s = da_xs8;
        dl_s = dl_s -  35.0;
        dl_pr = da_xpr8;
        dl_dp = dl_pr - dl_p;
        i = 0;

        /*
         * Boucle
         */
        while (true)
        {
            dl_t2 = dl_t * dl_t;
            dl_t3 = dl_t2 * dl_t;
            dl_p2 = dl_p * dl_p;
            dl_g =
                dl_a000 + dl_a001 * dl_t + dl_a002 * dl_t2 + dl_a003 * dl_t3
                        + dl_a010 * dl_s + dl_a011 * dl_s * dl_t + dl_a100
                        * dl_p + dl_a101 * dl_p * dl_t + dl_a102 * dl_p * dl_t2
                        + dl_a103 * dl_p * dl_t3 + dl_a110 * dl_p * dl_s
                        + dl_a111 * dl_p * dl_s * dl_t + dl_a200 * dl_p2
                        + dl_a201 * dl_p2 * dl_t + dl_a202 * dl_p2 * dl_t2;
            i = i + 1;

            /*
             * Differents Cas
             */
            if (i == 1)
            {
                dl_g1 = dl_dp * dl_g *  1.e-03;
                dl_teta1 = dl_t + dl_g1 *  .5;
                dl_p = dl_p + dl_dp *  .5;
                dl_t = dl_teta1;
            }
            else if (i == 2)
            {
                dl_g2 = dl_dp * dl_g *  1.e-3;
                dl_teta2 = dl_teta1 +  .292893218 * (dl_g2 - dl_g1);
                dl_t = dl_teta2;
            }
            else if (i == 3)
            {
                dl_g3 = dl_dp * dl_g *  1.e-03;
                dl_q2 =
                     .585786437 * dl_g2 +  .121320343 * dl_g1;
                dl_teta3 = dl_teta2 +  1.707106781 * (dl_g3 - dl_q2);
                dl_p = dl_pr;
                dl_t = dl_teta3;
            }
            else if (i == 4)
            {
                dl_g4 = dl_dp * dl_g *  1.e-03;
                dl_q3 =
                     3.414213562 * dl_g3 -  4.121320343 * dl_q2;
                dl_teta4 =
                    dl_teta3 + (dl_g4 - dl_q3 *  2.0) /  6.0;
                /* c.M2.deb */
                dl_tetai8 = dl_teta4 * 0.99976;
                /*
                 * Sortie de boucle
                 */
                break;
            }
        }

        /* c.M2.fin */
        /*
         * Retour de la fonction temperature potentielle
         */
        return (dl_tetai8);
    }
}

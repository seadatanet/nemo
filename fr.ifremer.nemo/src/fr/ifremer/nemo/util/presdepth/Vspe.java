/* ============================================================================
 * Nom du fichier	: Vspe.java
 * Auteur			: ycaudal
 * Date				: 13 janv. 10
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.util.presdepth;

/**
 * Description: TODO
 * @author ycaudal
 *
 */
public class Vspe
{
    protected static double CP_vspe08 (double da_xp8, double da_xt8, double da_xs8)
    {
    /*
     * Declaration des parametres
     */
        double dl_vspe08;
        double dl_s2;
        double dl_s32;
        double dl_a;
        double dl_b;
        double dl_c;
        double dl_d;
        double dl_sig;
        double dl_vzero;
        double dl_xkzw;
        double dl_aw;
        double dl_bw;
        double dl_aa;
        double dl_bb;
        double dl_cc;
        double dl_e;
        double dl_ax;
        double dl_bx;
        double dl_dd;
        double dl_xk;
        double dl_xkz;
        double dl_a0; 
        double dl_a1; 
        double dl_a2; 
        double dl_a3; 
        double dl_a4; 
        double dl_a5; 
        double dl_b0; 
        double dl_b1; 
        double dl_b2; 
        double dl_b3; 
        double dl_b4; 
        double dl_c0; 
        double dl_c1; 
        double dl_c2; 
        double dl_d0; 

    /*
     * Initialisation
     */ 
        dl_a0 = 999.842594;
        dl_a1 = 6.793952e-2;
        dl_a2 = -9.09529e-3;
        dl_a3 = 1.001685e-4;
        dl_a4 = -1.120083e-6;
        dl_a5 = 6.536332*1.0e-9;
        dl_b0 = .824493;
        dl_b1 = -4.0899e-3;
        dl_b2 = 7.6438e-5;
        dl_b3 = -8.2467*1.0e-7;
        dl_b4 = 5.3875*1.0e-9;
        dl_c0 = -5.72466e-3;
        dl_c1 = 1.0227e-4;
        dl_c2 = -1.6546e-6;
        dl_d0 = 4.8314e-4;
        

          dl_s2  = da_xs8*da_xs8;
          dl_s32 = Math.sqrt(dl_s2*da_xs8);

    /*
     * M1.deb
     */
          da_xt8 = da_xt8 * 1.00024;

    /*
     * M1.fin
     */
          dl_a = ((((dl_a5*da_xt8 + dl_a4)*da_xt8 + dl_a3)*da_xt8 + dl_a2)*da_xt8 + dl_a1)*da_xt8 + dl_a0;
          dl_b = (((dl_b4*da_xt8 + dl_b3)*da_xt8 + dl_b2)*da_xt8 + dl_b1)*da_xt8 + dl_b0;
          dl_c = (dl_c2*da_xt8 + dl_c1)*da_xt8 + dl_c0;
          dl_b = dl_b*da_xs8;
          dl_c = dl_c*dl_s32;
          dl_d = dl_d0*dl_s2;
          dl_sig = dl_a + dl_b + dl_c + dl_d;


          dl_vzero = 1.0/dl_sig;
          dl_xkzw = ((-5.155288e-5*da_xt8+1.360477e-2)*da_xt8-
                 2.327105)*da_xt8;
          dl_xkzw = (dl_xkzw + 148.4206)*da_xt8 + 19652.21;
          dl_aw = ((-5.77905e-8*da_xt8 + 1.16092e-5)*da_xt8 + 
               1.43713e-4)*da_xt8 + 3.239908e-1;
          dl_bw = (5.2787e-10*da_xt8 - 6.12293e-8)*da_xt8 + 
               8.50935e-7;
          dl_aa = ((-6.167e-5*da_xt8 + 1.09987e-2)*da_xt8 -
                0.603459)*da_xt8 + 54.6746;
          dl_bb = (-5.3009e-4*da_xt8 + 1.6483e-2)*da_xt8 + 
                7.944e-2;
          dl_cc = (-1.6078e-7*da_xt8 - 1.0981e-6)*da_xt8 + 
                2.2838e-4;
          dl_dd = 1.91075e-5;
          dl_e = (9.1697*1.0e-12*da_xt8 + 2.0816*1.0e-10)*da_xt8 - 9.9348*1.0e-9;
          dl_aa = dl_aa*da_xs8;
          dl_bb = dl_bb*dl_s32;
          dl_cc = dl_cc*da_xs8;
          dl_dd = dl_dd*dl_s32;
          dl_e = dl_e*da_xs8;
          dl_ax = dl_aw + dl_cc + dl_dd;
          dl_bx = dl_bw + dl_e;
          dl_xkz = dl_xkzw + dl_aa + dl_bb;
          dl_xk = dl_xkz + dl_ax*da_xp8 + dl_bx*da_xp8*da_xp8;
          dl_vspe08 = dl_vzero*(1.0 - .1*da_xp8/dl_xk);

    /*
     * Retour
     * Volume specifique calcule
     */
        return(dl_vspe08);
    }
}

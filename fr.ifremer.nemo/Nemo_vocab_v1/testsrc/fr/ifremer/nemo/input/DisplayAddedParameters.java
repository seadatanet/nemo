/* ============================================================================
 * Nom du fichier	: DisplayAddedParameters.java
 * Auteur			: sbregent
 * Date				: 28 janv. 11
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.input;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import fr.ifremer.nemo.input.parameters.addedParameters.AP_ADEPZZ01;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_BATH;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_CJDY;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_DAYX;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_DEPH;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_DEPHPR01;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_DTUT;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_LATX;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_LONX;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_MNTH;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_PRES;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_TIME;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_YEAR;
import fr.ifremer.nemo.input.parameters.addedParameters.AddedParam;


/**
 * Description: instanciation de tous les types de AddedParam et affichage de leurs valeurs
 * @author sbregent
 *
 */
public class DisplayAddedParameters
{
    public static Logger logger;
    
public static void main(String[] args)
{
    // LOGGER
    DOMConfigurator.configure("resources/log4jBatch.xml"); //$NON-NLS-1$
    logger = Logger.getLogger("nemotest");//$NON-NLS-1$
    logger.setLevel(Level.DEBUG);
    AddedParam p;

    ArrayList<AddedParam> addedParamList = new ArrayList<AddedParam>(); 
    p = new AP_ADEPZZ01(); addedParamList.add(p);
    p = new AP_BATH();  addedParamList.add(p);
    p = new AP_CJDY();  addedParamList.add(p);
    p = new AP_DAYX();  addedParamList.add(p);
    p = new AP_DEPH();  addedParamList.add(p);
    p = new AP_DEPHPR01();addedParamList.add(p);
    p = new AP_DTUT(); addedParamList.add(p);
    p = new AP_LATX(); addedParamList.add(p);
    p = new AP_LONX(); addedParamList.add(p);
    p = new AP_MNTH(); addedParamList.add(p);
    p = new AP_PRES(); addedParamList.add(p);
    p = new AP_TIME(); addedParamList.add(p);
    p = new AP_YEAR(); addedParamList.add(p);


    Iterator<AddedParam> iter = addedParamList.iterator();
    while (iter.hasNext())
    {
        p=iter.next();
        System.out.println("");
        System.out.println(p.getCode()+"____________________________________________________________");
        System.out.println("cible:                               "+p.getCible());
        System.out.println("code:                                "+p.getCode());
        System.out.println("code P011:                           "+p.getCodeP011());
        System.out.println("default value:                       "+p.getDefault());
        System.out.println("fullname:                            "+p.getFullName());
        System.out.println("localCode (mapping SDN et colonnes): "+p.getLocalCode());
        System.out.println("unit:                                "+p.getUnit());
        System.out.println("unit code:                           "+p.getUnitCode());
        try{
            System.out.println("format:                              "+p.getFormat());
        }catch(Exception e)
        {
            System.out.println("format:                              <context dependant>");
        }
        try{
            System.out.println("flag:                                 "+p.getFlag(null, null));
        }catch(Exception e)
        {
            System.out.println("flag:                                <station dependant>");
        }
    }
}
}

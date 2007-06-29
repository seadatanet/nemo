package fr.ifremer.nemo.ui;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class Regexp {
/*
 * Test des valeurs des champs de l'IHM
 * par utilisation de regexp
 *  
 */


public  static void initTab(){
	
		
	
}


public static void remplirArrayList(ArrayList<String> liste, String chaine, int day, int month, int year, String split){
	
	liste = new ArrayList<String>();
	
	liste.add(chaine);
	liste.add(""+day); //$NON-NLS-1$
	liste.add(""+month); //$NON-NLS-1$
	liste.add(""+year); //$NON-NLS-1$
	liste.add(split);
	
	}


public static Boolean checkPresenceValeurObligatoire(Text text, String nomSection){
	
	Boolean ok=true;
	
	if (text.getText().matches("")){ //$NON-NLS-1$
	Utils.messageErreurAppend(Messages.getString("Regexp.4")+nomSection+Messages.getString("Regexp.5")); //$NON-NLS-1$ //$NON-NLS-2$
	ok = false;
	}
	
	return ok;
}

public static Boolean checkLigneDebutfin(Text ligne, Text debut, Text fin, String nomSection){
	
	String lign, debu, fi, log;
	Pattern pattern;
	Matcher matcher;
	Boolean ok;
	
	//raz du message d'erreurs
	//Utils.messageErreur("");
	
	ok = true;
	//log = "\n===[Check section : " + nomSection + "]===\n";
	log="\nSection : "+nomSection+"\n"; //$NON-NLS-1$
	
	//System.out.println(nomSection);
	
	//valeurs des champs
	lign = ligne.getText();
	debu = debut.getText();
	fi = fin.getText();
	
	
	//format des différents champs
	//ligne
	if (Pattern.matches("^[1-9][0-9]*$", lign)){ //$NON-NLS-1$
		//System.out.println("ligne de la section " + nomSection + " ==> ok");
		ok = ok && true;
		//log = log + "Valeur de ligne ok\n";
		} 
	else { 
		ok = ok && false;
		log = log + "==>"+Messages.getString("Regexp.8"); //$NON-NLS-1$
		
		}

	//debut
	if (Pattern.matches("^[1-9][0-9]*$", debu)){ //$NON-NLS-1$
		//System.out.println("debut de la section " + nomSection + " ==> ok");
		ok = ok && true;
		//log = log + "Valeur de début ok\n";
	} else { 
		ok = ok && false;
		log = log + "==>"+Messages.getString("Regexp.10"); //$NON-NLS-1$
		
		}

	//fin
	if (Pattern.matches("^[1-9][0-9]*$", fi)){ //$NON-NLS-1$
		//System.out.println("fin de la section " + nomSection + " ==> ok");
		ok = ok && true;
		//log = log + "Valeur de fin ok\n";
	} else { 
		ok = ok && false;
		log = log + "==>"+Messages.getString("Regexp.12"); //$NON-NLS-1$
		
		}
		
	//tests des relations des champs
	//int i = Integer.parseInt(str);
	if (!debu.matches("")&&(!fi.matches(""))){ //$NON-NLS-1$ //$NON-NLS-2$
	if (Integer.parseInt(debu) > Integer.parseInt(fi)){
		ok = ok && false; 
		log = log + "==>"+Messages.getString("Regexp.15"); //$NON-NLS-1$
		}
	}
	
if (ok){
	//log = log+"...ok";
	}else{
	
Utils.messageErreurAppend(log);	}
	return ok;
	
}


public static Boolean checkTextValeur(Text text, String nomSection){
	Boolean ok;
	String valeur, log;
	
	ok = true;
	//log = "\n===[Check section : " + nomSection + "]===\n";
	log="\nSection : "+nomSection+"\n"; //$NON-NLS-1$
	valeur = text.getText();
	
	
	if (Pattern.matches("^[0-9][0-9]*$", valeur)){ //$NON-NLS-1$
		//System.out.println("valeur de la section " + nomSection + " ==> ok");
		ok = ok && true;
		//log = log + "Valeur ok\n";
	} else { 
		ok = ok && false;
		log = log + "==>"+Messages.getString("Regexp.18"); //$NON-NLS-1$
		
		}
	
	if (ok){
		//log = log + "...ok";
	}else{
	
Utils.messageErreurAppend(log);}
	return ok;
}


}




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

public static Vector vecteurValeurs, vecteurRegexp;
public static Dictionary dicoRegexp;
public static ArrayList <String> tabDDsMMsYY, tabMMsDDsYY, tabYYsDDsMM, tabYYsMMsDD, tabDDsYYsMM, tabMMsYYsDD;
public static ArrayList <String> tabDDpMMpYY, tabMMpDDpYY, tabYYpDDpMM, tabYYpMMpDD, tabDDpYYpMM, tabMMpYYpDD;
public static ArrayList <String> tabDDtMMtYY, tabMMtDDtYY, tabYYtDDtMM, tabYYtMMtDD, tabDDtYYtMM, tabMMtYYtDD;
public static ArrayList <String> tabDDdMMdYY, tabMMdDDdYY, tabYYdDDdMM, tabYYdMMdDD, tabDDdYYdMM, tabMMdYYdDD;
public static ArrayList <String> tabDDiMMiYY, tabMMiDDiYY, tabYYiDDiMM, tabYYiMMiDD, tabDDiYYiMM, tabMMiYYiDD;

//DDsMMsYY = 25/06/05
//DDpMMpYY = 25.06.05
//DDtMMtYY = 25-06-05
//DDdMMdYY = 25#12#05
//DDiMMiYY = 25|12|05
//DDMMYY  = 250605


public  static void initTab(){
	
	
	
	
}


public static void remplirArrayList(ArrayList<String> liste, String chaine, int day, int month, int year, String split){
	
	liste = new ArrayList<String>();
	
	liste.add(chaine);
	liste.add(""+day);
	liste.add(""+month);
	liste.add(""+year);
	liste.add(split);
	
	}

public static void initRegexp(){

dicoRegexp = new Hashtable();	

//tabs des valeurs regexp
//formats /
remplirArrayList(tabDDsMMsYY, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 1, 2, 3, "/");
remplirArrayList(tabMMsDDsYY, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 2, 1, 3, "/");
remplirArrayList(tabYYsDDsMM, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 3, 1, 2, "/");
remplirArrayList(tabDDsYYsMM, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 1, 3, 2, "/");
remplirArrayList(tabYYsMMsDD, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 3, 2, 1, "/");
remplirArrayList(tabMMsYYsDD, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 2, 3, 1, "/");

//formats .
remplirArrayList(tabDDsMMsYY, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 1, 2, 3, ".");
remplirArrayList(tabMMsDDsYY, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 2, 1, 3, ".");
remplirArrayList(tabYYsDDsMM, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 3, 1, 2, ".");
remplirArrayList(tabDDsYYsMM, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 1, 3, 2, ".");
remplirArrayList(tabYYsMMsDD, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 3, 2, 1, ".");
remplirArrayList(tabMMsYYsDD, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 2, 3, 1, ".");

//formats -
remplirArrayList(tabDDsMMsYY, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 1, 2, 3, "-");
remplirArrayList(tabMMsDDsYY, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 2, 1, 3, "-");
remplirArrayList(tabYYsDDsMM, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 3, 1, 2, "-");
remplirArrayList(tabDDsYYsMM, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 1, 3, 2, "-");
remplirArrayList(tabYYsMMsDD, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 3, 2, 1, "-");
remplirArrayList(tabMMsYYsDD, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 2, 3, 1, "-");

//formats #
remplirArrayList(tabDDsMMsYY, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 1, 2, 3, "#");
remplirArrayList(tabMMsDDsYY, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 2, 1, 3, "#");
remplirArrayList(tabYYsDDsMM, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 3, 1, 2, "#");
remplirArrayList(tabDDsYYsMM, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 1, 3, 2, "#");
remplirArrayList(tabYYsMMsDD, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 3, 2, 1, "#");
remplirArrayList(tabMMsYYsDD, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 2, 3, 1, "#");

//formats |
remplirArrayList(tabDDsMMsYY, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 1, 2, 3, "|");
remplirArrayList(tabMMsDDsYY, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 2, 1, 3, "|");
remplirArrayList(tabYYsDDsMM, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 3, 1, 2, "|");
remplirArrayList(tabDDsYYsMM, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 1, 3, 2, "|");
remplirArrayList(tabYYsMMsDD, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 3, 2, 1, "|");
remplirArrayList(tabMMsYYsDD, "^[0-9][0-9]/[0-9][0-9]/[0-9][0-9]$", 2, 3, 1, "|");

//
//dico des équivalences
//formats /
dicoRegexp.put("DD/MM/YY", tabDDsMMsYY);
dicoRegexp.put("MM/DD/YY", tabMMsDDsYY);
dicoRegexp.put("YY/DD/MM", tabYYsDDsMM);
dicoRegexp.put("DD/YY/MM", tabDDsYYsMM);
dicoRegexp.put("YY/MM/DD", tabYYsMMsDD);
dicoRegexp.put("MM/YY/DD", tabMMsYYsDD);

//formats .
dicoRegexp.put("DD/MM/YY", tabDDpMMpYY);
dicoRegexp.put("MM/DD/YY", tabMMpDDpYY);
dicoRegexp.put("YY/DD/MM", tabYYpDDpMM);
dicoRegexp.put("DD/YY/MM", tabDDpYYpMM);
dicoRegexp.put("YY/MM/DD", tabYYpMMpDD);
dicoRegexp.put("MM/YY/DD", tabMMpYYpDD);

//formats -
dicoRegexp.put("DD/MM/YY", tabDDtMMtYY);
dicoRegexp.put("MM/DD/YY", tabMMtDDtYY);
dicoRegexp.put("YY/DD/MM", tabYYtDDtMM);
dicoRegexp.put("DD/YY/MM", tabDDtYYtMM);
dicoRegexp.put("YY/MM/DD", tabYYtMMtDD);
dicoRegexp.put("MM/YY/DD", tabMMtYYtDD);

//formats #
dicoRegexp.put("DD/MM/YY", tabDDdMMdYY);
dicoRegexp.put("MM/DD/YY", tabMMdDDdYY);
dicoRegexp.put("YY/DD/MM", tabYYdDDdMM);
dicoRegexp.put("DD/YY/MM", tabDDdYYdMM);
dicoRegexp.put("YY/MM/DD", tabYYdMMdDD);
dicoRegexp.put("MM/YY/DD", tabMMdYYdDD);

//formats |
dicoRegexp.put("DD/MM/YY", tabDDiMMiYY);
dicoRegexp.put("MM/DD/YY", tabMMiDDiYY);
dicoRegexp.put("YY/DD/MM", tabYYiDDiMM);
dicoRegexp.put("DD/YY/MM", tabDDiYYiMM);
dicoRegexp.put("YY/MM/DD", tabYYiMMiDD);
dicoRegexp.put("MM/YY/DD", tabMMiYYiDD);
	
}	


public static Boolean checkPresenceValeurObligatoire(Text text, String nomSection){
	
	Boolean ok=true;
	
	if (text.getText().matches("")){
	Utils.messageErreurAppend("La valeur de la section "+nomSection+" est obligatoire !\n");
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
	log = "\n===[Check section : " + nomSection + "]===\n";
	
	System.out.println(nomSection);
	
	//valeurs des champs
	lign = ligne.getText();
	debu = debut.getText();
	fi = fin.getText();
	
	
	//TODO string to int
	//int i = Integer.parseInt(str);

	
	//format des différents champs
	//ligne
	if (Pattern.matches("^[1-9][0-9]*$", lign)){
		System.out.println("ligne de la section " + nomSection + " ==> ok");
		ok = ok && true;
		//log = log + "Valeur de ligne ok\n";
		} 
	else { 
		ok = ok && false;
		log = log + "   Erreur valeur/format de ligne\n";
		
		}

	//debut
	if (Pattern.matches("^[1-9][0-9]*$", debu)){
		System.out.println("debut de la section " + nomSection + " ==> ok");
		ok = ok && true;
		//log = log + "Valeur de début ok\n";
	} else { 
		ok = ok && false;
		log = log + "   Erreur valeur/format de début\n";
		
		}

	//fin
	if (Pattern.matches("^[1-9][0-9]*$", fi)){
		System.out.println("fin de la section " + nomSection + " ==> ok");
		ok = ok && true;
		//log = log + "Valeur de fin ok\n";
	} else { 
		ok = ok && false;
		log = log + "   Erreur valeur/format de fin";
		
		}
		
	//tests des relations des champs
	//int i = Integer.parseInt(str);
	if (!debu.matches("")&&(!fi.matches(""))){
	if (Integer.parseInt(debu) > Integer.parseInt(fi)){
		ok = ok && false; 
		log = log + "\n   La position de début doit être inférieure à celle de la fin";
		}
	}
	
if (ok){
	log = log+"...ok";
	}
	
Utils.messageErreurAppend(log);	
	return ok;
	
}


public static Boolean checkTextValeur(Text text, String nomSection){
	Boolean ok;
	String valeur, log;
	
	ok = true;
	log = "\n===[Check section : " + nomSection + "]===\n";
	valeur = text.getText();
	
	
	if (Pattern.matches("^[0-9][0-9]*$", valeur)){
		//System.out.println("valeur de la section " + nomSection + " ==> ok");
		ok = ok && true;
		//log = log + "Valeur ok\n";
	} else { 
		ok = ok && false;
		log = log + "   Erreur valeur/format";
		
		}
	
	if (ok){
		log = log + "...ok";
	}
	
Utils.messageErreurAppend(log);
	return ok;
}




public static Boolean checkRegexpDate(Text date, Combo listeFormats, String day, String month, String year){
	Boolean ok = true;
	String format, entree, split, tmpDay, tmpMonth, tmpYear;
	String tab[], chiffres[];
	int digits;
	
	//on récupère la regexp du format
	tab = new String[4];
	entree = date.getText();
	format = listeFormats.getItem(listeFormats.getSelectionIndex());
	tab = (String[])(dicoRegexp.get(format));
	split = tab[4];
	
	//on teste si l'entree correspond au format
	if (Pattern.matches(tab[0], entree)){
		
		//format ok donc on split en fonction du délimiteur en position 4 dans tab[]
		//-1 quand pas de séparation
		if ((Integer.valueOf(split) != -1)){
			//cas normal, on chope le spliteur
			chiffres = entree.split(split);
			
			day = chiffres[Integer.valueOf(tab[1])-1];
			month = chiffres[Integer.valueOf(tab[2])-1];
			year = chiffres[Integer.valueOf(tab[3])-1];
			
			digits = year.length();
			//en fonction du nombre de digits, le test sur l'année sera différent
			// -> désimbriquer les tests d'ailleurs !
			
			//vérif de l'année
			if (digits == 4){
				if ((Integer.valueOf(year) < 1900) || (Integer.valueOf(year) > 2008)){
					System.out.println("Valeur d'année éronnée");
					ok = ok && false;
				}
			}
			else {
				if ((Integer.valueOf(year) < 0) || (Integer.valueOf(year) > 8)){
					System.out.println("Valeur d'année éronnée");
					ok = ok && false;
				}
					
			}
			
			//vérif jour
			if ((Integer.valueOf(day) < 0) || ((Integer.valueOf(day) > 31))){
				System.out.println("Valeur de jour éronnée");
				ok = ok && false;
			}
			
			//vérif mois
			if ((Integer.valueOf(month) < 0) || ((Integer.valueOf(month) > 12))){
				System.out.println("Valeur de mois éronnée");
				ok = ok && false;
			}
		
	} 
	else {
		
		//pas de spliter défini donc on récupère les valeurs avec substring
		
		//test en fonction de la taille de l'année
		if (entree.length() == 8){
			//si DDMMYYYY
			
			if ((Integer.valueOf(tab[1])==1)&&(Integer.valueOf(tab[2])==2)){
				tmpDay = entree.substring(0, 1);
				tmpMonth = entree.substring(2, 3);
				tmpYear = entree.substring(4, 7);}
			if (((Integer.valueOf(tab[1])==2))&&((Integer.valueOf(tab[2])==1))){
				tmpDay = entree.substring(2, 3);
				tmpMonth = entree.substring(0, 1);
				tmpYear = entree.substring(4, 5);}
			if (((Integer.valueOf(tab[1])==3))&&((Integer.valueOf(tab[2])==1))){
				tmpDay = entree.substring(4, 5);
				tmpMonth = entree.substring(6, 7);
				tmpYear = entree.substring(0, 3);}
			if (((Integer.valueOf(tab[1])==2))&&((Integer.valueOf(tab[2])==3))){
				tmpDay = entree.substring(6, 7);
				tmpMonth = entree.substring(0, 1);
				tmpYear = entree.substring(2, 5);}
			if (((Integer.valueOf(tab[1])==3))&&((Integer.valueOf(tab[2])==2))){
				tmpDay = entree.substring(6, 7);
				tmpMonth = entree.substring(4, 5);
				tmpYear = entree.substring(0, 3);}	
			
			
			
		} else {
			//si DDMMYY
			
		if ((Integer.valueOf(tab[1])==1)&&(Integer.valueOf(tab[2])==2)){
			tmpDay = entree.substring(0, 1);
			tmpMonth = entree.substring(2, 3);
			tmpYear = entree.substring(4, 5);}
		if (((Integer.valueOf(tab[1])==2))&&((Integer.valueOf(tab[2])==1))){
			tmpDay = entree.substring(2, 3);
			tmpMonth = entree.substring(0, 1);
			tmpYear = entree.substring(4, 5);}
		if (((Integer.valueOf(tab[1])==3))&&((Integer.valueOf(tab[2])==1))){
			tmpDay = entree.substring(2, 3);
			tmpMonth = entree.substring(4, 5);
			tmpYear = entree.substring(0, 1);}
		if (((Integer.valueOf(tab[1])==2))&&((Integer.valueOf(tab[2])==3))){
			tmpDay = entree.substring(4, 5);
			tmpMonth = entree.substring(0, 1);
			tmpYear = entree.substring(2, 3);}
		if (((Integer.valueOf(tab[1])==3))&&((Integer.valueOf(tab[2])==2))){
			tmpDay = entree.substring(4, 5);
			tmpMonth = entree.substring(2, 3);
			tmpYear = entree.substring(0, 1);}
	}
	
	}	

	
} else {
	System.out.println("Format éronné");
}

	return ok; 

}

}




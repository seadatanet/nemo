/*
 * ============================================================================
 * Nom du fichier : Utils.java
 * Auteur : sbregent
 * Date : 4 févr. 11
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo.util;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: TODO
 * 
 * @author sbregent
 */
public class Utils
{
	public static String extraitCode(String valeur)
	{
		if (valeur.indexOf(Global.SPACE) > -1)
		{
			return valeur.substring(0, valeur.indexOf(Global.SPACE));
		} // else {
		return valeur;
		// }
	}

	public static String extraitCodeURN(String valeur)
	{
		if (valeur.lastIndexOf('/') > -1)
		{
			return valeur.substring(valeur.lastIndexOf('/') + 1);
		} // else {
		return valeur;
		// }

	}

	public static String extraitValeur(String valeur)
	{
		if (valeur.indexOf(Global.SEP) > -1)
		{
			return valeur.substring(valeur.indexOf(Global.SEP) + 3);
		} // else {
		return valeur;
		// }
	}

	public static String extraitFichierChemin(String chemin)
	{
		if (chemin.lastIndexOf(File.separator) > -1)
		{
			return chemin.substring(chemin.lastIndexOf(File.separator) + 1);
		} // else {
		return chemin;
		// }
	}

	public static String extraitCheminFichier(String chemin)
	{
		if (chemin.lastIndexOf(File.separator) > -1)
		{
			return chemin.substring(0, chemin.lastIndexOf(File.separator));
		} // else {
		return chemin;
		// }
	}

	public static String concat(String list1, String list2, String separator)
	{
		return list1 + separator + list2;
	}

	public static String validFileName(String value)
	{
		char newChar = '_';
		value = value.replace(' ', newChar);
		value = value.replace('/', newChar);
		value = value.replace('\\', newChar);
		value = value.replace(':', newChar);
		value = value.replace('*', newChar);
		value = value.replace('?', newChar);
		value = value.replace('\"', newChar);
		value = value.replace('<', newChar);
		value = value.replace('>', newChar);
		value = value.replace('|', newChar);
		return value;
	}

	public static boolean isNumeric(String inputString)
	{
		boolean numeric = false;
		try
		{
			Integer.parseInt(inputString);
			numeric = true;
		} catch (Exception e)
		{
			numeric = false;
		}
		return numeric;
	}

	public static String[] getFilesWithoutSVN(String[] listeFichiers)
	{
		if (listeFichiers != null && listeFichiers[0].toString().endsWith(Global.SVN_DIR))
		{
			String[] listeFichiersWithoutSVN = new String[listeFichiers.length - 1];
			int cpt = 0;
			for (int i = 0; i < listeFichiers.length; i++)
			{
				if (!listeFichiers[i].endsWith(Global.SVN_DIR))
				{
					listeFichiersWithoutSVN[cpt] = listeFichiers[i];
					cpt++;
				}
			}
			listeFichiers = listeFichiersWithoutSVN;
		}
		return listeFichiers;
	}

	public static File[] getFilesWithoutSVN(File[] subfiles)
	{
		// on enleve le repertoire .svn s'il existe
		if (subfiles != null && subfiles[0].toString().endsWith(Global.SVN_DIR))
		{
			File[] listeFichiersWithoutSVN = new File[subfiles.length - 1];
			int cpt = 0;
			for (int i = 0; i < subfiles.length; i++)
			{
				if (!subfiles[i].toString().endsWith(Global.SVN_DIR))
				{
					listeFichiersWithoutSVN[cpt] = subfiles[i];
					cpt++;
				}
			}
			subfiles = listeFichiersWithoutSVN;
		}
		return subfiles;
	}

	public static boolean isreadedLineMatchesRegexp(String readedLine,
			String regexp)
	{
		boolean res = false;
		// pattern
		Pattern p = Pattern.compile(regexp);
		// création d’un moteur de recherche
		Matcher m = p.matcher(readedLine);
		// lancement de la recherche de toutes les occurrences
		res = m.matches();
		return res;
	}
}

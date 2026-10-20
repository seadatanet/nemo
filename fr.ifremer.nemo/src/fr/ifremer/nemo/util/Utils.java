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

import org.apache.commons.lang.StringUtils;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.RulesManager;

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
	
	/**
	 * 
	 * @param version1
	 * @param version2
	 * @return 0 if versions are equal, -1 if version1<version2, & if version1>version2
	 */
		public static Integer compareVersion(String version1, String version2) {
			String[] vals1 = version1.split("\\.");
			String[] vals2 = version2.split("\\.");
			int i = 0;
			while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
				i++;
			}

			if (i < vals1.length && i < vals2.length) {
				int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
				return diff < 0 ? -1 : diff == 0 ? 0 : 1;
			}

			return vals1.length < vals2.length ? -1 : vals1.length == vals2.length ? 0 : 1;
		}
		

		public static boolean deleteDir(File dir) {
			if (dir.isDirectory()) {
				String[] children = dir.list();
				for (int i = 0; i < children.length; i++) {
					boolean success = deleteDir(new File(dir, children[i]));
					if (!success) {
						return false;
					}
				}
			}

			// The directory is now empty so delete it
			return dir.delete();
		}
		
		/**
		 * Extract a column value from a CSV-style line, indicated by a char position.
		 * 
		 * @param line a CSV-style line.
		 * @param separator the separator used in the CSV-style line.
		 * @param charPos The char position indicating the column to find.
		 * 
		 * @return the column value, or an empty string if not found.
		 */
		public static String getColumnFromCsvLine(String line, String separator, int charPos) {

			// rolling index
			int indexSearch = 1;
			
			boolean isColumnFound = false;
			
			String[] split = line.split(separator);
			String returnValue = Global.EMPTY_STRING;
			String foundData = Global.EMPTY_STRING;
			
			for (String colData : split) {

				foundData = colData;

				// indexSearch takes the index of the current column's last character
				indexSearch = indexSearch + colData.length();
				if (charPos < indexSearch) {
					
					// the column is found
					isColumnFound = true;
					break;
				}
				
				indexSearch++; // add separator (1 char)
			}
			
			if (isColumnFound) {
				returnValue = foundData;
			}

			// The value has been found AND the line is clearly with a separator.
			return returnValue;
		}
		
		/**
		 * Extract a column index from a CSV-style line, indicated by a char position.
		 * 
		 * @param line a CSV-style line.
		 * @param separator the separator used in the CSV-style line.
		 * @param charPos The char position indicating the column to find.
		 * 
		 * @return the column index, or an empty string if not found.
		 */
		public static int getColumnIndexFromCsvLine(String line, String separator, int charPos) {

			// rolling index
			int indexSearch = 1;
			int res=-1;
			
			boolean isColumnFound = false;
			
			String[] split = line.split(separator);
			int colIndex=0;
			for (String colData : split) {

				// indexSearch takes the index of the current column's last character
				indexSearch = indexSearch + colData.length();
				if (charPos < indexSearch) {
					
					// the column is found
					isColumnFound = true;
					break;
				}
				
				indexSearch++; // add separator (1 char)
				colIndex++;
			}
			
			if (isColumnFound) {
				res = colIndex;
			}

			// The value has been found AND the line is clearly with a separator.
			return res;
		}
		
		/**
		 * Return the column index (start at 1!) of a char in a CSV line
		 * @param csvLine
		 * @param separator
		 * @param charIndex (start at 1!)
		 * @return column number (start at 1!)
		 */
		public static int getColumnIndexInCsvLineFromCharIndex(String csvLine,  String separator, int charIndex) {
//			// Cut the line from start to desired character
//			String cuttedLine = csvLine.substring(0, charIndex);
//			// Count the number of seaparators found before the desired char
//			int columnIndex = StringUtils.countMatches(cuttedLine, separator);
			
			int columnIndex = getColumnIndexFromCsvLine(csvLine,separator,  charIndex);

			return columnIndex +1;
		}

		/**
		 * Return the start/end (start at 1!) index of a column in a CSV line
		 * @param csvLine
		 * @param separator
		 * @param column (start at 1!)
		 * @return
		 */
		public static int[] getStartEndIndexInCsvLineFromColumnIndex(String csvLine, String separator, int column) {
			int [] startEnd = new int[2];
			// Cut the line into columns
			String[] parts = csvLine.split(separator, -1);
			// if pipe separator, do not add escape
			String realSeparator = separator;
			if (realSeparator == "\\|") {
				realSeparator = "|";
			}
			// Compute the start index of the nth separator
			int start = StringUtils.ordinalIndexOf(csvLine, realSeparator,column-1) + realSeparator.length() + 1;		
//			int start = 1;
//			int columnCount = 0;
//			for (String part : parts) {
//				if (column - 1 == columnCount) {
//					break;
//				}
//				start = start + part.length() + separator.length();
//				columnCount++;
//			}
			// Compute the end index
			int end = start + parts[column-1].length() -1;
		    
		    startEnd[0] = start;
		    startEnd[1] = end;
			
			return startEnd;
		}
		
		public static boolean isInData(int lineIndex) {
			boolean inData = false;
			boolean inStationHeader = false;
			boolean inFileHeader = false;
			
			int lign = lineIndex;
			int fileHeader = 0;
			if (!RulesManager.getInstance().isCruiseInADirectory()
					|| Nemo.getInstance().getViewMain().getEditFileForm().isMultiStationFileChecked()) {
				// fichier campagne
				fileHeader = Nemo.getInstance().getDomain().getMedia().getModel().getFileHeader();
				// fileHeader = DataModel.singleSurvey.getHeaderLinesNumber();
			}
			int relativeLine = lign + fileHeader;
			int stationHeader = fileHeader + Nemo.getInstance().getDomain().getMedia().getStationHeaderNumberLines();

				inData = (relativeLine == (stationHeader + 1));
				inFileHeader = ((relativeLine <= (fileHeader)) && relativeLine != 0);
				inStationHeader = (relativeLine > (fileHeader)) && (relativeLine <= (stationHeader));

			return inData;
		}
		
	/**
	 * Transform a string to be compatible with MIKADO CSV (CDI Summary). If
	 * needed, adds double quotes around the string, and escapes existing double
	 * quotes.
	 * 
	 * @param data
	 * @return the transformed string.
	 */
	public static String escapeSpecialCharacters(String data) {
		String escapedData = data;
		if (data.contains("\t") || data.contains("\n") || data.contains("\"")) {
			escapedData = data.replace("\"", "\"\"");
			escapedData = "\"" + escapedData + "\"";
		}
		return escapedData;
	}

	/**
	 * Replaces tabs and new lines by spaces.
	 * 
	 * @param data an input String.
	 * @return the String after replacements.
	 */
	public static String replaceTabsAndNewLines(String data) {
		String output = data;
		return data.replaceAll("\\R|\\t", " ");
	}
}

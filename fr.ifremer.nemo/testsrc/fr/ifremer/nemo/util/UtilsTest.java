/*
 * ============================================================================
 * Nom du fichier : UtilsTest.java
 * Auteur : sbregent
 * Date : 28 févr. 2011
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * Description: TODO
 * 
 * @author sbregent
 */
public class UtilsTest
{
	public static Logger testLogger;
	public static String[] input = { "1100051103DANI90001004550N03312E690517120012200201000569", "11000512021200000015801846013165800932      013   055    ", "11000512031200000815501864013366240998      013   078     ", "1100551103DANI90002004552N03312E690517171517350200100569", "11005512021715000016201844013075970968      006   077    ", "11005512031715000816001842013095870947      013   077     ", "1101051103DANI90003004552N03315E690518080008200200100569", "11010512020800000016301857013145370873      010   033    ", "11010512030800000816101857013185720926      013   035   " };

	public static void main(String[] args)
	{

		isreadedLineMatchesRegexpTest();

		// boolean res =
		// isreadedLineMatchesRegexp("1100051103DANI900 pppp",
		// "110.051103DANI900.*");
		// System.out.println(res);
	}

	private static void isreadedLineMatchesRegexpTest()
	{
		boolean res;
		String regexp = ".{10}DANI900.*";

		for (int i = 0; i < input.length; i++)
		{
			res = isreadedLineMatchesRegexp(input[i], regexp);
			System.out.println(input[i].substring(0, 17));
			System.out.println(regexp);
			System.out.println(res);
		}
	}

	public static boolean isreadedLineMatchesRegexp(String readedLine, String regexp)
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

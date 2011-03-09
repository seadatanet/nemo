package fr.ifremer.nemo.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.Flag;
import fr.ifremer.nemo.config.FlagTable;
import fr.ifremer.nemo.input.Fragment;
import fr.ifremer.nemo.ui.edit.Cursor;
import fr.ifremer.nemo.ui.edit.conversion.EditConversionForm;
import fr.ifremer.nemo.ui.edit.cruise.EditCruiseForm;
import fr.ifremer.nemo.ui.edit.data.EditDataForm;
import fr.ifremer.nemo.ui.edit.file.EditFileForm;
import fr.ifremer.nemo.ui.edit.station.EditStationForm;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class Utils
{

	public static String texteBrut, texteFinal; // texte brut entier et texte

	public static StringBuffer stringBuffer;

	public static int progression;

	public static ArrayList<String> lignesBrutes; // lignes du texte

	public static int[] taillesCumLignes;

	// public static Configuration configuration;

	public static StyleRange[] stylesCouleurs;

	public static Font fontCourier = new Font(Application.display, "Courier", 10, SWT.NORMAL); //$NON-NLS-1$
	public static Font font08 = new Font(Application.display, Global.EMPTY_STRING, 8, SWT.NORMAL);
	public static Font font08bold = new Font(Application.display, Global.EMPTY_STRING, 8, SWT.BOLD);

	public static ProgressBar progressbar;

	// public static InputModel inputModel;

	private static FlagTable _blank;

	public static void insertTextFich(String fich, StyledText zone, boolean raz) throws IOException
	{

		int nbreLignes, i, cumul, indexMax;
		String ligne;

		texteBrut = Global.EMPTY_STRING;
		texteFinal = Global.EMPTY_STRING;
		nbreLignes = 0;

		lignesBrutes = new ArrayList<String>();
		stringBuffer = new StringBuffer();

		// lecture et concatenation
		BufferedReader entree = new BufferedReader(new InputStreamReader(new FileInputStream(fich), Global.NEMO_ENCODING));
		do
		{
			ligne = entree.readLine();

			if (ligne != null)
			{

				lignesBrutes.add(ligne);
				stringBuffer.append(ligne);
				stringBuffer.append("\n"); //$NON-NLS-1$

				nbreLignes = nbreLignes + 1;

			}

		} while (ligne != null);
		entree.close();

		// calcul des tailles cumulees
		taillesCumLignes = new int[nbreLignes];
		indexMax = nbreLignes - 1;
		cumul = 0;

		taillesCumLignes[0] = 0;
		for (i = 1; i <= indexMax; i++)
		{
			cumul = cumul + lignesBrutes.get(i - 1).length() + 1;
			taillesCumLignes[i] = cumul;

		}

		texteBrut = stringBuffer.toString();

		// affichage texte
		// changement de font
		zone.setFont(fontCourier);

		// debut d'infos du fichier
		ViewLog.styledTextLogInfos.setText(Messages.Utils_5 + fich);
		ViewLog.styledTextLogInfos.append(Messages.Utils_6 + nbreLignes);

		// reactivation de l'option serie temporelle onglet DESC
		Nemo.getInstance().getViewMain().getEditFileForm().enableSeparatorChoice(true);
		Nemo.getInstance().getViewMain().getEditFileForm().enableFileTypeChoice(true);

		// FAE 089
		// RAZ global si reouverture fichier en cours de processus
		Nemo.getInstance().refreshNomNemo();

		if (raz)
		{
			Utils.razGlobal();
			// invalidEtapes();
			Nemo.getInstance().resetModel();

			// reset de l'etape de conversion
			Nemo.getInstance().getViewMain().setEtape(0);
		}
	}

	public static void inject(Text ligne, Text debut, Text fin, StyledText styledtext)
	{
		int deb, fi, lign, i;

		// on recupere les coordonnees de la selection

		deb = styledtext.getSelection().x;
		fi = styledtext.getSelection().y;

		if (deb == fi)
		{
			Utils.messageErreurAppend(Messages.Utils_26);

		} else
		{

			lign = 1;
			// calcul de la ligne en fonction de la valeur de x
			// parcours du tableau des tailles de lignes cumulees

			for (i = 0; i < lignesBrutes.size(); i++)
			{

				if (deb == taillesCumLignes[i])
				{
					lign = i + 1;
					break;
				}
				if (i == (lignesBrutes.size() - 1))
				{
					lign = i + 1;
					break;
				}
				if ((deb >= taillesCumLignes[i]) && (deb < taillesCumLignes[i + 1]))
				{
					lign = i + 1;
					break;
				}

			}

			// remplissage ligne dans interface
			ligne.setText(String.valueOf(lign));

			deb = deb - taillesCumLignes[lign - 1] + 1; // debut reel sur ligne

			// nbre caracs par rapport
			// debut de ligne
			fi = fi - taillesCumLignes[lign - 1]; // fin reelle sur ligne

			// remplissage debut et fin dans interface
			debut.setText(String.valueOf(deb));
			fin.setText(String.valueOf(fi));

			debut.pack();
			ligne.pack();
			fin.pack();
		}
	}

	public static void test(Text test, StyledText styledtext, Text ligne, Text debut, Text fin)
	{
		int deb, fi, lign;

		if (ligne.getText().equals(Global.EMPTY_STRING))
		{
			// TODO verifier correspondance avec
			// Old_StationModel.getNumberOfLines()
			lign = Nemo.getInstance().getDomain().getMedia().getModel().getStationHeader() + 1;
		} else
		{
			lign = Integer.parseInt(ligne.getText());
		}

		deb = Integer.parseInt(debut.getText()) + taillesCumLignes[lign - 1];
		fi = Integer.parseInt(fin.getText()) + taillesCumLignes[lign - 1];

		styledtext.setSelection(deb - 1, fi);

		// remplissage test dans interface
		test.setText(styledtext.getText(deb - 1, fi - 1));
		test.pack();
	}

	public static void test(Text test, Text saisie)
	{
		test.setText(saisie.getText());

		test.pack();
	}

	public static void injectData(StyledText styledtext, int debut, int fin)
	{
		int deb, fi, lign, i, index;

		// on recupere les coordonnees de la selection
		deb = styledtext.getSelection().x;
		fi = styledtext.getSelection().y;
		index = Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().getMyTable().getSelectionIndex();

		if (deb == fi)
		{
			Utils.messageErreurAppend(Messages.Utils_26);
		} else
		{
			Utils.messageErreur("");

			lign = 1;
			// calcul de la ligne en fonction de la valeur de x
			// parcours du tableau des tailles de lignes cumulees

			for (i = 0; i < lignesBrutes.size(); i++)
			{

				if (deb == taillesCumLignes[i])
				{
					lign = i + 1;
					break;
				}
				if (i == (lignesBrutes.size() - 1))
				{
					lign = i + 1;
					break;
				}
				if ((deb >= taillesCumLignes[i]) && (deb < taillesCumLignes[i + 1]))
				{
					lign = i + 1;
					break;
				}

			}

			// remplissage ligne dans interface
			// ligne = lign;

			deb = deb - taillesCumLignes[lign - 1] + 1; // debut reel sur ligne
			// :
			// nbre caracs par rapport
			// debut de ligne
			fi = fi - taillesCumLignes[lign - 1]; // fin reelle sur ligne

			// remplissage debut et fin dans interface
			Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().getMyTable().getItems()[index].setText(debut, String.valueOf(deb));
			Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().getMyTable().getItems()[index].setText(fin, String.valueOf(fi));

		}
	}

	public static void injectFlag(StyledText styledtext, FlagTable flagTable, Table table, int indexLigne, int indexDebut, int indexFin, int indexTest1, int indexTest2)
	{
		int index = table.getSelectionIndex();
		if (index >= 0 && table.getItems() != null)
		{
			int deb, fi, lign, i;

			// on recupere les coordonnees de la selection
			deb = styledtext.getSelection().x;
			fi = styledtext.getSelection().y;

			if (deb == fi)
			{
				Utils.messageErreurAppend(Messages.Utils_26);
			} else
			{

				lign = 1;
				// calcul de la ligne en fonction de la valeur de x
				// parcours du tableau des tailles de lignes cumulees

				for (i = 0; i < lignesBrutes.size(); i++)
				{

					if (deb == taillesCumLignes[i])
					{
						lign = i + 1;
						break;
					}
					if (i == (lignesBrutes.size() - 1))
					{
						lign = i + 1;
						break;
					}
					if ((deb >= taillesCumLignes[i]) && (deb < taillesCumLignes[i + 1]))
					{
						lign = i + 1;
						break;
					}

				}

				deb = deb - taillesCumLignes[lign - 1] + 1; // debut reel sur
				// ligne :
				// nbre caracs par rapport debut de ligne
				fi = fi - taillesCumLignes[lign - 1]; // fin reelle sur ligne

				// remplissage ligne dans interface
				table.getItems()[index].setText(indexLigne, String.valueOf(lign));

				// remplissage debut et fin dans interface
				table.getItems()[index].setText(indexDebut, String.valueOf(deb));
				table.getItems()[index].setText(indexFin, String.valueOf(fi));

				// Test apres injection
				testFlag(styledtext, flagTable, table, indexLigne, indexDebut, indexFin, indexTest1, indexTest2);
			}
		}
	}

	public static void testFlag(StyledText styledtext, FlagTable flagTable, Table table, int indexLigne, int indexDebut, int indexFin, int indexTest1, int indexTest2)
	{
		int deb, fi, lign;

		for (int i = 0; i < table.getItemCount(); i++)
		{
			try
			{
				lign = Integer.parseInt(table.getItems()[i].getText(indexLigne));

				deb = Integer.parseInt(table.getItems()[i].getText(indexDebut)) + taillesCumLignes[lign - 1];
				fi = Integer.parseInt(table.getItems()[i].getText(indexFin)) + taillesCumLignes[lign - 1];

				// remplissage test entree dans interface
				table.getItems()[i].setText(indexTest1, styledtext.getText(deb - 1, fi - 1));

				// remplissage test sortie dans interface
				String flagSDN = Global.ZERO;
				int size = flagTable.getFlags().size();
				for (int j = 0; j < size; j++)
				{
					if (flagTable.getFlags().get(j).getLocal().equals(table.getItems()[i].getText(indexTest1)))
					{
						flagSDN = fr.ifremer.nemo.util.Utils.extraitCode(flagTable.getFlags().get(j).getSdn());
						break;
					}
				}
				table.getItems()[i].setText(indexTest2, flagSDN);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void injectNbreLignes(Text nbrelignes, StyledText styledtext)
	{
		int deb, fi, lignDepart, lignFin, i;

		// on recupere les coordonnees de la selection
		deb = styledtext.getSelection().x;
		fi = styledtext.getSelection().y;

		lignDepart = 1;
		lignFin = 1;
		// calcul de la ligne en fonction de la valeur de x
		// parcours du tableau des tailles de lignes cumulees

		if (deb == fi)
		{
			Utils.messageErreurAppend(Messages.Utils_26);

		} else
		{

			// calcul ligne depart
			for (i = 0; i < lignesBrutes.size(); i++)
			{

				if (deb == taillesCumLignes[i])
				{
					lignDepart = i + 1;
					break;
				}
				if (i == (lignesBrutes.size() - 1))
				{
					lignDepart = i + 1;
					break;
				}
				if ((deb >= taillesCumLignes[i]) && (deb < taillesCumLignes[i + 1]))
				{
					lignDepart = i + 1;
					break;
				}

			}

			// calcul ligne fin

			for (i = 0; i < lignesBrutes.size(); i++)
			{

				if (fi == taillesCumLignes[i])
				{
					lignFin = i + 1;
					break;
				}
				if (i == (lignesBrutes.size() - 1))
				{
					lignFin = i + 1;
					break;
				}
				if ((fi >= taillesCumLignes[i]) && (fi < taillesCumLignes[i + 1]))
				{
					lignFin = i + 1;
					break;
				}

			}

			// remplissage nbreLignes dans interface
			nbrelignes.setText("" + (lignFin - lignDepart + 1)); //$NON-NLS-1$
			nbrelignes.pack();
		}
	}

	public static void calculPosxPosy(Fragment frag, Cursor cursor)
	{
		// reconstitue la position x et y du surlignement souris e partir du
		// fragment

		int offsetDebut, offsetFin, ligneDebut, ligneFin;

		ligneDebut = frag.getFirstLine();
		ligneFin = frag.getLastLine();
		offsetDebut = frag.getFirstCharOffset();
		offsetFin = frag.getLastCharOffset();

		cursor.setX(taillesCumLignes[ligneDebut - 1] + offsetDebut - 1);
		cursor.setY(taillesCumLignes[ligneFin - 1] + offsetFin);

	}

	public static void calculOffsetDebOffsetFin(Fragment frag, Cursor cursor)
	{
		// calcule debut et fin e partir du surlignement souris
		int offsetDebut, offsetFin, ligneDebut, ligneFin;

		ligneDebut = frag.getFirstLine();
		ligneFin = frag.getLastLine();

		offsetDebut = cursor.getX() - taillesCumLignes[ligneDebut - 1] + 1;
		offsetFin = cursor.getY() - taillesCumLignes[ligneFin - 1];
		if (offsetDebut < 0)
		{
			frag.setFirstCharOffset(1);
		} else
		{
			frag.setFirstCharOffset(offsetDebut);
		}

		if (offsetFin > 0)
		{
			frag.setLastCharOffset(offsetFin);
		}
	}

	public static void calculRealOffsetFin(Fragment frag)
	{
		EditFileForm editFileForm = Nemo.getInstance().getViewMain().getEditFileForm();
		int offsetFin;
		int enteteFichier = Integer.parseInt(editFileForm.getSectionFileHeader().getValue());

		// calcul de l'offset de fin après rechargement du modèle
		if (enteteFichier > 0)
		{
			// prise en compte de l'entête de fichier
			offsetFin = frag.getLastCharOffset() + taillesCumLignes[frag.getLastLine() + Integer.parseInt(editFileForm.getSectionFileHeader().getValue())] - taillesCumLignes[frag.getFirstLine() + Integer.parseInt(editFileForm.getSectionFileHeader().getValue())];
		} else
		{
			// il n'y a pas d'entête de fichier
			offsetFin = frag.getLastCharOffset() + taillesCumLignes[frag.getLastLine() - 1] - taillesCumLignes[frag.getFirstLine() - 1];
		}

		if (offsetFin > 0)
		{
			frag.setLastCharOffset(offsetFin);
		}
	}

	public static void calculNbreLignesPuisRemplissageFrag(Fragment frag, int headerLines)
	{
		int fi, lignDepart, lignFin, i, nbrelignes, longueur;

		// on recupere les coordonnees de la selection
		fi = frag.getLastCharOffset();
		longueur = frag.getLastCharOffset();
		// System.out.println("deb clic = " + deb);
		// System.out.println("fi clic = " + fi);

		lignDepart = frag.getFirstLine() + headerLines;
		lignFin = lignDepart;
		// lignDepart = 1;
		// lignFin = 1;
		// calcul de la ligne en fonction de la valeur de x
		// parcours du tableau des tailles de lignes cumulees

		// // calcul ligne depart
		// for (i = 0; i < lignesBrutes.size(); i++) {
		//
		// if (deb == taillesCumLignes[i]) {
		// lignDepart = i + 1;
		// break;
		// }
		// if (i == (lignesBrutes.size() - 1)) {
		// lignDepart = i + 1;
		// break;
		// }
		// if ((deb >= taillesCumLignes[i]) && (deb < taillesCumLignes[i + 1]))
		// {
		// lignDepart = i + 1;
		// break;
		// }
		//
		// }

		// System.out.println("ligne depart = " + lignDepart);

		// calcul ligne fin

		for (i = lignDepart - 1; i < lignesBrutes.size(); i++)
		{

			if (longueur == taillesCumLignes[i] - taillesCumLignes[lignDepart - 1])
			{
				lignFin = i + 1;
				break;
			}
			if (i == (lignesBrutes.size() - 1))
			{
				lignFin = i + 1;
				break;
			}
			if ((longueur >= taillesCumLignes[i] - taillesCumLignes[lignDepart - 1]) && (longueur < taillesCumLignes[i + 1] - taillesCumLignes[lignDepart - 1]))
			{
				lignFin = i + 1;
				break;
			}

		}

		// System.out.println("ligne fin = " + lignFin);

		// nbre de lignes effectif
		nbrelignes = (lignFin - lignDepart + 1);

		if (nbrelignes == 1)
		{
			// frag.setSingleLine(true);
			frag.setLastLine(frag.getFirstLine());
		} else
		{
			frag.setSingleLine(false);
			frag.setLastLine(frag.getFirstLine() + nbrelignes - 1);

			// calcul de l'offset reel sur la derniere ligne
			int cpt = fi;
			for (int j = lignDepart - 1; j < lignFin - 1; j++)
			{
				cpt = cpt - Utils.lignesBrutes.get(j).length();
			}
			// en sortie de boucle, cpt a la valeur de l'offset de fin sur la
			// derniere ligne du commentaire
			// System.out.println("offset en ligne fin = " + cpt);
			// System.out.println("ligne fin = " + lignFin);
			// System.out.println("ligne debut = " + lignDepart);

			if (cpt > 0)
			{
				cpt = cpt - (nbrelignes - 1);
			}

			frag.setLastCharOffset(cpt);
		}

	}

	public static void calculNbreLignesPuisRemplissageFragClic(Fragment frag, Cursor cursor)
	{
		int deb, fi, lignDepart, lignFin, i, nbrelignes;

		// calcul nbre ligne/debut/fin en fonction de la zone de surlignement
		// souris

		// on recupere les coordonnees de la selection
		deb = cursor.getX();
		fi = cursor.getY();

		// System.out.println("deb clic = " + deb);
		// System.out.println("fi clic = " + fi);

		lignDepart = frag.getFirstLine();
		lignFin = frag.getFirstLine();
		// lignDepart = 1;
		// lignFin = 1;

		// calcul de la ligne en fonction de la valeur de x
		// parcours du tableau des tailles de lignes cumulees

		// calcul ligne depart
		for (i = 0; i < lignesBrutes.size(); i++)
		{
			if (deb == taillesCumLignes[i])
			{
				lignDepart = i + 1;
				break;
			}
			if (i == (lignesBrutes.size() - 1))
			{
				lignDepart = i + 1;
				break;
			}
			if ((deb >= taillesCumLignes[i]) && (deb < taillesCumLignes[i + 1]))
			{
				lignDepart = i + 1;
				break;
			}
			// System.out.println("cumul = " + taillesCumLignes[i] + "i= " + i);

		}

		// System.out.println("ligne depart = " + lignDepart);

		// calcul ligne fin

		for (i = 0; i < lignesBrutes.size(); i++)
		{

			if (fi == taillesCumLignes[i])
			{
				lignFin = i + 1;
				break;
			}
			if (i == (lignesBrutes.size() - 1))
			{
				lignFin = i + 1;
				break;
			}
			if ((fi >= taillesCumLignes[i]) && (fi < taillesCumLignes[i + 1]))
			{
				lignFin = i + 1;
				break;
			}

		}

		// System.out.println("ligne fin = " + lignFin);

		// nbre de lignes effectif
		nbrelignes = (lignFin - lignDepart + 1);

		if (nbrelignes == 1)
		{
			// frag.setSingleLine(true);
			frag.setLastLine(lignDepart);
		} else
		{
			frag.setSingleLine(false);
			frag.setLastLine(frag.getFirstLine() + nbrelignes - 1);
		}
		calculOffsetDebOffsetFin(frag, cursor);

	}

	/**
	 * @param onglet
	 *            : destination
	 */
	public static void validationEtape(int onglet)
	{
		// ViewMain.affiche => onglet courant
		// onglet => onglet destination
		EditFileForm editFileForm = Nemo.getInstance().getViewMain().getEditFileForm();
		EditCruiseForm editCruiseForm = Nemo.getInstance().getViewMain().getEditCruiseForm();
		EditStationForm editStationForm = Nemo.getInstance().getViewMain().getEditStationForm();
		EditDataForm editDataForm = Nemo.getInstance().getViewMain().getEditDataForm();
		EditConversionForm editConversionForm = Nemo.getInstance().getViewMain().getEditConversionForm();

		switch (onglet) {
		case 0:
			ViewLog.aideFile();
			if (editFileForm != null)
			{
				razEditionCurrent();
				if (editFileForm.isLoaded())
				{
					// EditDesc.DrawingBack();
					editFileForm.drawingBack();
				} else if (editFileForm.isFileLoaded())
				{
					// EditDesc.affichageEdition();
					editFileForm.afficheEdition();
				} else
				{
					editFileForm.reinitTextArea();
				}
			}
			Nemo.getInstance().getViewMain().setAffiche(0);
			break;
		case 1:
			razEditionCurrent();
			if (editCruiseForm.isLoaded())
			{
				editCruiseForm.drawingBack();
			} else if (editFileForm.isValidated())
			{
				editCruiseForm.afficheEdition();
			} else
			{
				ViewLog.aideCruise();
				Nemo.getInstance().getViewMain().getEditCruiseForm().getTextAreaCruise().setText(Global.EMPTY_STRING);
				Nemo.getInstance().getViewMain().setAffiche(1);
			}
			break;
		case 2:
			razEditionCurrent();
			if (editStationForm.isLoaded())
			{
				editStationForm.drawingBack();
			} else if ((!Nemo.getInstance().getViewMain().tabCruise.isDisposed() && editCruiseForm.isValidated()) || (Nemo.getInstance().getViewMain().tabCruise.isDisposed() && editFileForm.isValidated()))
			{
				editStationForm.afficheEdition();
			} else
			{
				ViewLog.aideStation();
				Nemo.getInstance().getViewMain().getEditStationForm().getTextAreaStation().setText(Global.EMPTY_STRING);
				Nemo.getInstance().getViewMain().setAffiche(2);
			}
			break;
		case 3:
			razEditionCurrent();
			if (editDataForm.isLoaded())
			{
				editDataForm.drawingBack();
			} else if (editStationForm.isValidated())
			{
				editDataForm.afficheEdition();
			} else
			{
				ViewLog.aideData();
				Nemo.getInstance().getViewMain().getEditDataForm().getTextAreaData().setText(Global.EMPTY_STRING);
				Nemo.getInstance().getViewMain().setAffiche(3);
			}
			break;
		case 4:
			razEditionCurrent();
			if (editConversionForm.isLoaded())
			{
				editConversionForm.drawingBack();
			} else if (editDataForm.isValidated())
			{
				editConversionForm.afficheEdition();
			} else
			{
				ViewLog.aideConversion();
				Nemo.getInstance().getViewMain().setAffiche(4);
			}
			break;
		}

	}

	private static void razEditionCurrent()
	{
		EditFileForm editFileForm = Nemo.getInstance().getViewMain().getEditFileForm();
		EditCruiseForm editCruiseForm = Nemo.getInstance().getViewMain().getEditCruiseForm();
		EditStationForm editStationForm = Nemo.getInstance().getViewMain().getEditStationForm();
		EditDataForm editDataForm = Nemo.getInstance().getViewMain().getEditDataForm();
		EditConversionForm editConversionForm = Nemo.getInstance().getViewMain().getEditConversionForm();

		switch (Nemo.getInstance().getViewMain().getAffiche()) {
		case 0:
			// from FILE
			if (editFileForm.isLoaded())
				editFileForm.razEdition();
			break;
		case 1:
			// from CRUISE
			if (editCruiseForm.isLoaded())
				editCruiseForm.razEdition();
			break;
		case 2:
			// from STATION
			if (editStationForm.isLoaded())
				editStationForm.razEdition();
			break;
		case 3:
			// from DATA
			if (editDataForm.isLoaded())
				editDataForm.razEdition();
			break;
		case 4:
			// from DATA
			if (editConversionForm.isLoaded())
				editConversionForm.razEdition();
			break;
		default:
			break;
		}
	}

	public static Boolean findFiles(String directoryPath, boolean raz)
	{
		File directory = new File(directoryPath);

		Boolean ok = true;

		File[] subfiles = directory.listFiles();

		Arrays.sort(subfiles);

		int nbre = subfiles.length;
		String nom = subfiles[0].getName();

		// verif sur l'extension de tous les fichiers du dossier
		Pattern p = Pattern.compile("[.]"); //$NON-NLS-1$
		String[] mots = p.split(nom);
		String ext;
		if (mots.length == 1)
		{
			ext = Global.EMPTY_STRING;
		} else
		{
			ext = mots[1];
		}

		for (int i = 0; i < nbre; i++)
		{

			String mots1[] = p.split(subfiles[i].getName());
			String ext1;
			if (mots1.length == 1)
			{
				ext1 = Global.EMPTY_STRING;
			} else
			{
				ext1 = mots1[1];
			}

			if (!ext.equals(ext1))
			{
				ok = false;
				Utils.messageErreurAppend(Messages.Utils_27);
				break;
			}

		}

		if (ok)
		{
			EditFileForm editFileForm = Nemo.getInstance().getViewMain().getEditFileForm();
			editFileForm.nomFichier = directoryPath + File.separator + subfiles[0].getName();
			editFileForm.nomRepertoire = directoryPath;

			try
			{
				Utils.insertTextFich(editFileForm.nomFichier, editFileForm.getTextAreaFile(), raz);
			} catch (IOException e)
			{
				e.printStackTrace();
				// gestion de l'erreur si le dossier ne contient aucun fichier -
				// affichage d'un message a l'utilisateur
				Utils.messageErreurAppend(Messages.error_file_selectDir);
				ok = false;
			}

		}

		return ok;

	}

	public static Boolean findFilesCollectCas1(String directoryPath, boolean raz)
	{
		File directory = new File(directoryPath);

		Boolean ok = true;

		File[] subfiles = directory.listFiles();

		Arrays.sort(subfiles);

		int nbre = subfiles.length;
		String nom = subfiles[0].getName();

		// verif sur l'extension de tous les fichiers du dossier
		Pattern p = Pattern.compile("[.]"); //$NON-NLS-1$
		String[] mots = p.split(nom);
		String ext;
		if (mots.length == 1)
		{
			ext = Global.EMPTY_STRING;
		} else
		{
			ext = mots[1];
		}

		for (int i = 0; i < nbre; i++)
		{

			String mots1[] = p.split(subfiles[i].getName());
			String ext1;
			if (mots1.length == 1)
			{
				ext1 = Global.EMPTY_STRING;
			} else
			{
				ext1 = mots1[1];
			}

			if (!ext.equals(ext1))
			{
				ok = false;
				Utils.messageErreurAppend(Messages.Utils_27);
				break;
			}

		}

		File[] subsubfiles = subfiles[0].listFiles();

		Arrays.sort(subsubfiles);

		String nom2 = subsubfiles[0].getName();

		// verif sur l'extension de tous les fichiers du dossier
		mots = p.split(nom2);

		if (mots.length == 1)
		{
			ext = Global.EMPTY_STRING;
		} else
		{
			ext = mots[1];
		}

		int nbre2 = subsubfiles.length;

		for (int i = 0; i < nbre2; i++)
		{

			String mots1[] = p.split(subsubfiles[i].getName());
			String ext1;
			if (mots1.length == 1)
			{
				ext1 = Global.EMPTY_STRING;
			} else
			{
				ext1 = mots1[1];
			}

			if (!ext.equals(ext1))
			{
				ok = false;
				Utils.messageErreurAppend(Messages.Utils_27);
				break;
			}

		}

		if (ok)
		{
			EditFileForm editFileForm = Nemo.getInstance().getViewMain().getEditFileForm();

			editFileForm.nomFichier = directoryPath + File.separator + subfiles[0].getName() + File.separator + subsubfiles[0].getName();
			editFileForm.nomRepertoire = directoryPath;

			try
			{
				Utils.insertTextFich(editFileForm.nomFichier, editFileForm.getTextAreaFile(), raz);
			} catch (IOException e)
			{

				e.printStackTrace();
			}

		}

		return ok;

	}

	public static void messageErreur(String message)
	{
		ViewLog.styledTextLogErreurs.setText(message);
		Color truc = new Color(Application.display, 255, 0, 0);

		StyleRange style = new StyleRange();
		style.start = 0;
		style.length = message.length();

		style.foreground = truc;

		ViewLog.styledTextLogErreurs.setStyleRange(style);
		ViewLog.tabFolderLog.setSelection(0);

		ViewLog.styledTextLogErreurs.update();
	}

	public static void messageErreurAppend(String message)
	{

		ViewLog.styledTextLogErreurs.append(Global.RN);

		ViewLog.styledTextLogErreurs.append(message);
		Color truc = new Color(Application.display, 255, 0, 0);

		StyleRange style = new StyleRange();
		style.start = 0;
		style.length = ViewLog.styledTextLogErreurs.getText().length();

		style.foreground = truc;

		ViewLog.styledTextLogErreurs.setStyleRange(style);
		ViewLog.tabFolderLog.setSelection(0);

		ViewLog.styledTextLogErreurs.update();

	}

	public static void messageOK(String message)
	{

		ViewLog.styledTextLogErreurs.setText(message);
		Color truc = new Color(Application.display, 0, 151, 0);

		StyleRange style = new StyleRange();
		style.start = 0;
		style.length = ViewLog.styledTextLogErreurs.getText().length();

		style.foreground = truc;

		ViewLog.styledTextLogErreurs.setStyleRange(style);
		ViewLog.tabFolderLog.setSelection(0);

		ViewLog.styledTextLogErreurs.update();
	}

	public static void messageOKAppend(String message)
	{

		ViewLog.styledTextLogErreurs.append(Global.RN);

		ViewLog.styledTextLogErreurs.append(message);
		Color truc = new Color(Application.display, 0, 151, 0);

		StyleRange style = new StyleRange();
		style.start = 0;
		style.length = ViewLog.styledTextLogErreurs.getText().length();

		style.foreground = truc;

		ViewLog.styledTextLogErreurs.setStyleRange(style);
		ViewLog.tabFolderLog.setSelection(0);

		ViewLog.styledTextLogErreurs.update();

	}

	public static void razGlobal()
	{
		EditFileForm editFileForm = Nemo.getInstance().getViewMain().getEditFileForm();
		EditCruiseForm editCruiseForm = Nemo.getInstance().getViewMain().getEditCruiseForm();
		EditStationForm editStationForm = Nemo.getInstance().getViewMain().getEditStationForm();
		EditDataForm editDataForm = Nemo.getInstance().getViewMain().getEditDataForm();

		// efface tous les onglets qui ont ete affiches jusque le en preparation
		// du chargement d'un nouveau fichier
		if (editFileForm.getSectionFileHeader() != null)
		{
			// //effacement boutons
			editFileForm.razEdition();

			// reinitialisation des formulaires
			editFileForm.razText();
			editCruiseForm.razText();
			editStationForm.razText();
			editDataForm.razText(false);
			editDataForm.getMyTableData().razAndRecreateTable();
			// --- l'onglet d'erreur
			Utils.messageErreur(Global.EMPTY_STRING);
		}

		invalidEtapes();
	}

	public static void messageInfo(String message)
	{
		ViewLog.styledTextLogInfos.setText(message);
	}

	public static void messageInfoAppend(String message)
	{
		ViewLog.styledTextLogInfos.append("\n" + message); //$NON-NLS-1$
	}

	public static void selectComboValeur(Combo combo, String valeur)
	{
		int i = 0;
		int size = combo.getItemCount();

		if (valeur.equals(Global.EMPTY_STRING))
		{
			combo.select(-1);
		} else
		{
			combo.select(i);

			while (i < size && !combo.getText().equalsIgnoreCase(valeur))
			{
				i++;
				combo.select(i);
			}

			if (!combo.getText().equalsIgnoreCase(valeur))
			{
				combo.add(valeur);
				combo.select(combo.getItemCount() - 1);
			}
		}
	}

	public static void selectComboCode(Combo combo, String code)
	{
		int i = 0;
		int length = code.length();
		int size = combo.getItemCount();

		if (code.equals(Global.EMPTY_STRING))
		{
			combo.deselectAll();
			combo.select(-1);
		} else
		{
			combo.select(i);

			while (i < size && !combo.getText().substring(0, Math.min(combo.getText().length(), length)).equals(code))
			{
				i++;
				combo.select(i);
			}

			if (!combo.getText().substring(0, Math.min(combo.getText().length(), length)).equals(code))
			{
				combo.add(code);
				combo.select(combo.getItemCount() - 1);
			}
		}
	}

	public static void selectComboLibelle(Combo combo, String libelle)
	{
		int i = 0;
		int length = libelle.length();
		int size = combo.getItemCount();
		boolean blnFound = false;

		if (libelle.equals(Global.EMPTY_STRING))
		{
			combo.select(-1);
		} else
		{

			while (i < size)
			{
				combo.select(i);
				int debutLibelle = combo.getText().length() - length;
				if (debutLibelle >= 0)
				{
					if (combo.getText().substring(debutLibelle).trim().equalsIgnoreCase(libelle))
					{
						blnFound = true;
						break;
					}
				}
				i++;
			}

			if (!blnFound)
			{
				combo.add(libelle);
				combo.select(combo.getItemCount() - 1);
			}
		}
	}

	public static void selectComboCodePlus(Combo combo, String code, String valeur)
	{
		int i = 0;
		int length = code.length();
		int size = combo.getItemCount();

		if (code.equals(Global.EMPTY_STRING))
		{
			combo.select(-1);
		} else
		{
			combo.select(i);

			while (i < size && !combo.getText().substring(0, Math.min(combo.getText().length(), length)).equals(code))
			{
				i++;
				combo.select(i);
			}

			if (!combo.getText().substring(0, Math.min(combo.getText().length(), length)).equals(code))
			{
				combo.add(code + Global.SEP + valeur);
				combo.select(combo.getItemCount() - 1);
			}
		}
	}

	public static void createSearchMenu(Combo combo, String table)
	{
		Menu menu = new Menu(combo.getShell(), SWT.POP_UP);

		MenuItem itemSearch = new MenuItem(menu, SWT.NONE);
		itemSearch.setText(Messages.menu_label_search);

		combo.setMenu(menu);

		final Combo comb = combo;
		final String tab = table;

		itemSearch.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				Nemo.getInstance().getViewMain().getMyFilteredTableFactory().go(comb.getShell(), tab, comb);
			}
		});
	}

	public static int calculNbLigne(File file)
	{
		int nbLignes = 0;

		try
		{
			FileReader reader = new FileReader(file);
			BufferedReader readerLigne = new BufferedReader(reader);

			while (readerLigne.readLine() != null)
			{
				nbLignes++;
			}
			readerLigne.close();
			return nbLignes;
		} catch (IOException e)
		{
			e.printStackTrace();
			return -1;
		}
	}

	public static FlagTable getBlankFlagTable()
	{
		if (_blank == null)
		{
			_blank = new FlagTable();
			String[] flags = Flag.flagSDN();
			for (int i = 0; i < flags.length; i++)
			{
				_blank.getFlags().add(new Flag(fr.ifremer.nemo.util.Utils.extraitCode(flags[i]), flags[i]));
			}
		}

		return _blank;
	}

	public static void forceMenu(Table table, Menu menu)
	{
		final Menu men = menu;
		table.addListener(SWT.MouseDown, new Listener()
		{
			public void handleEvent(Event event)
			{
				// Lors d'un clic droit sur le tableau, on force l'affichage du
				// menu contextuel
				if (event.button == 3)
				{
					men.setVisible(true);
				}
			}
		});
	}

	public static void invalidEtapes()
	{
		EditFileForm editFileForm = Nemo.getInstance().getViewMain().getEditFileForm();
		EditCruiseForm editCruiseForm = Nemo.getInstance().getViewMain().getEditCruiseForm();
		EditStationForm editStationForm = Nemo.getInstance().getViewMain().getEditStationForm();
		EditDataForm editDataForm = Nemo.getInstance().getViewMain().getEditDataForm();
		EditConversionForm editConversionForm = Nemo.getInstance().getViewMain().getEditConversionForm();
		// on modifie la valeur du champ texte
		// dès qu'on modifie une valeur, on invalide l'étape
		// remise du nom onglet à valeur par défaut

		// on invalide toutes les étapes suivantes avec le second
		// flag okApresReset à false
		if (editFileForm != null)
		{
			int currentTab = Nemo.getInstance().getViewMain().getAffiche();

			if (currentTab <= 0) // onglet FILE ou inférieur en cours
				editFileForm.invalidEtape();

			if (currentTab <= 1) // onglet CRUISE ou inférieur en cours
				editCruiseForm.invalidEtape();

			if (currentTab <= 2) // onglet STATION ou inférieur en cours
				editStationForm.invalidEtape();

			if (currentTab <= 3) // onglet DATA ou inférieur en cours
				editDataForm.invalidEtape();

			if (currentTab <= 4) // onglet CONVERSION ou inférieur en cours
				editConversionForm.invalidEtape();
		}
	}

	/**
     * 
     */
	public static Shell getMainShell()
	{
		return ApplicationWorkbenchWindowAdvisor.configurer.getWindow().getShell();
	}
}

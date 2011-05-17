package fr.ifremer.nemo.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.domain.MediaDomain;
import fr.ifremer.nemo.ui.Application;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.file.EditFileForm;
import fr.ifremer.nemo.util.Global.E_EndData;
import fr.ifremer.nemo.util.Global.E_ProcessType;

public class Preformat
{

	// /////////////
	// Attributs //
	// /////////////

	public static String PF = File.separator + "PF_"; //$NON-NLS-1$

	// repertoire en entree
	private static String _repEntree;

	// Nombre de lignes d'en-tete de fichier
	private static int _enTeteFichier;

	// Nombre de lignes d'en-tete de station
	private static boolean _isPattern;
	private static int _enTeteStation;
	private static String _enTeteStationPattern;
	private static int _lgBeforeData;

	// Indicateur de fin de station
	private static String _indicateur;

	// Nombre de colonnes
	private static int _nbColonne;

	// Separateur
	private static char _separateur;

	// Partie Entiere maximale
	private static int[] _maxEntier;

	// Nombre de decimales max
	private static int[] _maxDecimale;

	// Nombre d'entiers d'une colonne
	private static int[] _nbEntier;

	// Nombre de decimales d'une colonne
	private static int[] _nbDecimale;

	// Contenu des colonnes
	private static String[] _colonne;

	// Preformatage reussi
	private static boolean _preformatOK;

	// Ajout d'un espace en début de ligne de données
	private static boolean space;

	// ////////////
	// Methodes //
	// ////////////

	public static void refreshEtatAvancement(Double pourcentEtape1, Double pourcentEtape2)
	{
		StringBuffer messagePreformat = new StringBuffer();
		messagePreformat.append(Messages.progress_file_preformat);
		messagePreformat.append(Global.RN);
		if (pourcentEtape1 != 100d)
		{
			messagePreformat.append(Messages.progress_file_preformat_analyse);
			messagePreformat.append(pourcentEtape1.intValue());
			messagePreformat.append(Global.POURCENT);

			messagePreformat.append(Global.TAB);
			int i = 0;

			messagePreformat.append("["); //$NON-NLS-1$
			for (; i < ((Double) (pourcentEtape1 / 2d)).intValue(); i++)
				messagePreformat.append("|"); //$NON-NLS-1$
			for (; i < 50; i++)
				messagePreformat.append("-"); //$NON-NLS-1$
			messagePreformat.append("]"); //$NON-NLS-1$
		} else
		{
			messagePreformat.append(Messages.progress_file_preformat_analyse);
			messagePreformat.append(Global.SPACE);
			messagePreformat.append(Messages.success);
		}

		messagePreformat.append(Global.RN);

		if (pourcentEtape2 != 100d)
		{
			messagePreformat.append(Messages.progress_file_preformat_write);
			messagePreformat.append(pourcentEtape2.intValue());
			messagePreformat.append(Global.POURCENT);

			messagePreformat.append(Global.TAB);

			messagePreformat.append("["); //$NON-NLS-1$
			int i = 0;
			for (; i < ((Double) (pourcentEtape2 / 2d)).intValue(); i++)
				messagePreformat.append("|"); //$NON-NLS-1$
			for (; i < 50; i++)
				messagePreformat.append("-"); //$NON-NLS-1$

			messagePreformat.append("]"); //$NON-NLS-1$
		} else
		{
			messagePreformat.append(Messages.progress_file_preformat_write);
			messagePreformat.append(Global.SPACE);
			messagePreformat.append(Messages.success);
		}

		Utils.messageOK(messagePreformat.toString());

		Application.display.update();

	}

	public static void run() throws IOException
	{

		EditFileForm editFileForm = Nemo.getInstance().getViewMain().getEditFileForm();

		space = false;
		_repEntree = Global.EMPTY_STRING;
		File repSortie;

		Utils.messageOK(Messages.progress_file_preformat);

		_nbColonne = 1;
		_preformatOK = false;

		_enTeteFichier = Integer.parseInt(editFileForm.getSectionFileHeader().getValue());
		_isPattern = !editFileForm.getSectionStationHeader().isSelectNumber();
		_enTeteStation = Integer.parseInt(editFileForm.getSectionStationHeader().getValue());
		_enTeteStationPattern = editFileForm.getSectionStationHeader().getIndicatorText();
		_lgBeforeData = Integer.parseInt(editFileForm.getSectionStationHeader().getNbLinesBeforeData());

		String indic = editFileForm.getSectionDataTerminator().getIndicatorValue();
		if (indic != null)
		{
			_indicateur = indic;
		}

		if (editFileForm.isSeparatorChecked())
		{
			_separateur = editFileForm.getSeparatorAsChar();
		}

		if (editFileForm.isCruiseIsAFile())
		{

			File ficEntree = new File(editFileForm.nomFichier);

			processPreformat(new File[]
										{ ficEntree.getParentFile() }, new String[]
																				{ ficEntree.getName() }, ficEntree.getParentFile());

			editFileForm.nomFichier = ficEntree.getParent() + PF + ficEntree.getName();
			editFileForm.setFileLoaded(true);

			Utils.insertTextFich(editFileForm.nomFichier, Nemo.getInstance().getViewMain().getEditCruiseForm().getTextAreaCruise(), false);
			editFileForm.getTextAreaFile().setText(Utils.texteBrut);

		} else if (editFileForm.isGroupedByCruise())
		{

			File repCollection = new File(editFileForm.nomRepertoire);
			File[] listeRepertoires = repCollection.listFiles();

			repSortie = new File(repCollection.getParent() + PF + repCollection.getName());
			if (!repSortie.exists())
			{
				repSortie.mkdirs();
			}

			processPreformat(listeRepertoires, null, repSortie);

			editFileForm.nomRepertoire = repSortie.getAbsolutePath();
			editFileForm.setFileLoaded(true);

			if (Utils.findFilesCollectCas1(repCollection.getAbsolutePath(), false))
			{
				editFileForm.getTextAreaFile().setText(Utils.texteBrut);
			}
		} else if (editFileForm.isCruiseIsADir())
		{
			File repertoire = new File(editFileForm.nomRepertoire);
			repSortie = new File(repertoire.getParent() + PF + repertoire.getName());
			if (!repSortie.exists())
			{
				repSortie.mkdirs();
			}
			processPreformat(new File[]
										{ repertoire }, null, repSortie);

			editFileForm.nomRepertoire = repertoire.getParent() + PF + repertoire.getName();
			editFileForm.setFileLoaded(true);

			if (Utils.findFiles(editFileForm.nomRepertoire, false))
			{
				editFileForm.getTextAreaFile().setText(Utils.texteBrut);
			}
		} else
		{

			File repertoire = new File(editFileForm.nomRepertoire);

			processPreformat(new File[]
										{ repertoire }, null, repertoire.getParentFile());

			editFileForm.nomRepertoire = repertoire.getParent() + PF + repertoire.getName();
			editFileForm.setFileLoaded(true);

			if (Utils.findFiles(editFileForm.nomRepertoire, false))
			{
				editFileForm.getTextAreaFile().setText(Utils.texteBrut);
			}
		}
		setPreformat(true);
	}

	public static File runBatch(File inputFile) throws IOException
	{

		MediaDomain mediaDom = Nemo.getInstance().getDomain().getMedia();
		space = false;

		_nbColonne = 1;
		_preformatOK = false;

		_enTeteFichier = mediaDom.getModel().getFileHeader();

		_isPattern = mediaDom.getModel().getStationHeaderPattern().length() > 0;

		_enTeteStation = mediaDom.getModel().getStationHeader();

		_enTeteStationPattern = mediaDom.getModel().getStationHeaderPattern();

		_lgBeforeData = mediaDom.getModel().getStationHeader();

		_indicateur = mediaDom.getModel().getEndDataPattern();

		_separateur = mediaDom.getModel().getSeparator().toCharArray()[0];

		File ficSortie = null;
		if (inputFile.isFile())
		{

			processPreformat(new File[]
			{ inputFile.getParentFile() }, new String[]
			{ inputFile.getName() }, inputFile.getParentFile(), true);

			ficSortie = new File(inputFile.getParent() + PF + inputFile.getName());

		} else if (inputFile.listFiles()[0].isDirectory() &&
				(!inputFile.listFiles()[0].toString().endsWith(".svn")))

		{

			File[] listeRepertoires = inputFile.listFiles();

			ficSortie = new File(inputFile.getParent() + PF + inputFile.getName());
			if (!ficSortie.exists())
			{
				ficSortie.mkdirs();
			}

			processPreformat(listeRepertoires, null, ficSortie, true);

		} else
		{

			processPreformat(new File[]
			{ inputFile }, null, inputFile.getParentFile(), true);

			ficSortie = new File(inputFile.getParent() + PF + inputFile.getName());

		}

		return ficSortie;

	}

	private static void processPreformat(File[] lstRepertoires, String[] listeFichiers, File repSortie) throws IOException
	{
		processPreformat(lstRepertoires, listeFichiers, repSortie, false);
	}

	private static void processPreformat(File[] lstRepertoires, String[] listeFichiers, File repSortie, boolean batchMode) throws IOException
	{

		Double pourcentEtape1 = 0d;
		Double pourcentEtape2 = 0d;
		int nbFic = 0;

		// *****************
		// suppression des repertoires SVN
		boolean isSVNDirPresent = false;
		File[] lstRepertoiresWithoutSNV = new
				File[lstRepertoires.length];
		for (File rep1 : lstRepertoires)
		{
			if (rep1.toString().endsWith(".svn"))
			{
				isSVNDirPresent = true;
				break;
			}
		}
		if (isSVNDirPresent)
		{
			lstRepertoiresWithoutSNV = new File[lstRepertoires.length - 1];

			int cpt = 0;
			for (int i = 0; i < lstRepertoires.length; i++)
			{
				if (!lstRepertoires[i].toString().endsWith(".svn"))
				{
					lstRepertoiresWithoutSNV[cpt] = lstRepertoires[i];
					cpt++;
				}
			}
			lstRepertoires = lstRepertoiresWithoutSNV;
		}
		// *****************
		// /////////////////////////////////////////////////
		// Recuperation du nombres de colonnes //
		// /////////////////////////////////////////////////
		for (File repertoire : lstRepertoires)
		{
			_repEntree = repertoire.getAbsolutePath();
			if (listeFichiers == null)
			{
				listeFichiers = new File(_repEntree).list();
			}

			// on enleve le repertoire .svn s'il existe
			if (listeFichiers != null && listeFichiers[0].toString().endsWith(".svn"))
			{
				String[] listeFichiersWithoutSVN = new String[listeFichiers.length - 1];
				int cpt = 0;
				for (int i = 0; i < listeFichiers.length; i++)
				{
					if (!listeFichiers[i].endsWith(".svn"))
					{
						listeFichiersWithoutSVN[cpt] = listeFichiers[i];
						cpt++;
					}
				}
				listeFichiers = listeFichiersWithoutSVN;
			}

			nbFic += listeFichiers.length;

			for (String fichier : listeFichiers)
			{
				nombreColonnes(fichier);
			}
		}

		// /////////////////////////////////////////////////
		// Recuperation des entiers et decimales maximum //
		// /////////////////////////////////////////////////
		_maxEntier = new int[_nbColonne];
		_maxDecimale = new int[_nbColonne];
		_nbEntier = new int[_nbColonne];
		_nbDecimale = new int[_nbColonne];
		_colonne = new String[_nbColonne];
		for (File repertoire : lstRepertoires)
		{
			_repEntree = repertoire.getAbsolutePath();
			if (listeFichiers == null)
			{
				listeFichiers = new File(_repEntree).list();
			}

			for (String fichier : listeFichiers)
			{
				if (!batchMode)
				{
					pourcentEtape1 += 100d / nbFic;
					refreshEtatAvancement(pourcentEtape1, pourcentEtape2);
				}
				getMax(fichier, batchMode);
			}
		}
		pourcentEtape1 = 100d;

		// /////////////////////////////////////////////////
		// Ecriture des fichiers preformates //
		// /////////////////////////////////////////////////
		for (File repertoire : lstRepertoires)
		{
			_repEntree = repertoire.getAbsolutePath();
			if (listeFichiers == null)
			{
				listeFichiers = new File(_repEntree).list();
			}

			// Creation du repertoire pour les fichiers preformates
			String outputDir = repSortie.getAbsolutePath();

			if (Nemo.getInstance().getDomain().getMedia().getModel().getProcessType() != E_ProcessType.CRUISE_FILE)
			{
				outputDir += PF + repertoire.getName();
			}
			File repPreformat = new File(outputDir);
			if (!repPreformat.exists())
			{
				repPreformat.mkdirs();
			}

			for (String fichier : listeFichiers)
			{
				if (!batchMode)
				{
					pourcentEtape2 += 100d / nbFic;
					refreshEtatAvancement(pourcentEtape1, pourcentEtape2);
				}
				ecritureFichier(fichier, repPreformat.getAbsolutePath());
			}
		}
		pourcentEtape2 = 100d;
		if (!batchMode)
		{
			refreshEtatAvancement(pourcentEtape1, pourcentEtape2);
		}
	}

	private static void analyseLigne(String ligneLue)
	{
		int i;

		int[] indexSeparateur = new int[_nbColonne + 1];

		// Index des separateurs
		indexSeparateur[0] = -1;
		for (i = 1; i < _nbColonne + 1; i++)
		{
			indexSeparateur[i] = ligneLue.indexOf(_separateur, indexSeparateur[i - 1] + 1);

			// dernier separateur = fin de ligne
			if (indexSeparateur[i] < 0)
			{
				indexSeparateur[i] = ligneLue.length();
			}
		}

		// Decoupage des colonnes
		for (i = 0; i < _nbColonne; i++)
		{
			try
			{
				_colonne[i] = ligneLue.substring(indexSeparateur[i] + 1, indexSeparateur[i + 1]);
			} catch (Exception e)
			{
				System.out.println("error preformat"); //$NON-NLS-1$
			}
			// System.out.println("ligne " + ligneLue + "i " + i + "index sep "
			// +
			// indexSeparateur);

			if (_colonne[i].indexOf(Global.POINT) > -1)
			{
				_nbEntier[i] = _colonne[i].indexOf(Global.POINT);
				_nbDecimale[i] = _colonne[i].length() - _colonne[i].indexOf(Global.POINT) - 1;
			} else
			{
				_nbEntier[i] = _colonne[i].length();
				_nbDecimale[i] = 0;
			}
		}
	}

	private static void compareMax(int i)
	{
		if (_nbEntier[i] > _maxEntier[i])
		{
			_maxEntier[i] = _nbEntier[i];
		}

		if (_nbDecimale[i] > _maxDecimale[i])
		{
			_maxDecimale[i] = _nbDecimale[i];
		}
	}

	private static void nombreColonnes(String fichier) throws IOException
	{
		BufferedReader lecteurAvecBuffer = null;
		String ligneLue = Global.EMPTY_STRING;
		int i;

		try
		{
			lecteurAvecBuffer = new BufferedReader(new FileReader(_repEntree + File.separator + fichier));

			// Lecture des lignes d'en-tete
			consumeStationHeader(lecteurAvecBuffer);

			// Lecture de la 1e ligne de donnees
			if ((ligneLue = lecteurAvecBuffer.readLine()) != null)
			{
				if (ligneLue.indexOf(_separateur) < 0)
				{
					_nbColonne = 0;
				} else
				{
					_nbColonne = 1;
					for (i = 0; i < ligneLue.length(); i++)
					{
						if (ligneLue.charAt(i) == _separateur)
						{
							_nbColonne++;
						}
					}
				}
			}

		} catch (FileNotFoundException exc)
		{
			Utils.messageErreurAppend(Messages.error_file_preformat + Global.SPACE + fichier);
		} finally
		{
			if (lecteurAvecBuffer != null)
				lecteurAvecBuffer.close();
		}
	}

	private static void getMax(String fichier, boolean isBatch) throws IOException
	{
		BufferedReader lecteurAvecBuffer = null;
		String ligneLue = Global.EMPTY_STRING;
		boolean analyseOK;

		E_EndData endData = null;

		if (isBatch)
		{
			endData = Nemo.getInstance().getDomain().getMedia().getModel().getEndData();
		} else
		{
			EditFileForm editFileForm = Nemo.getInstance().getViewMain().getEditFileForm();
			endData = editFileForm.getSectionDataTerminator().getEndData();
		}

		try
		{
			lecteurAvecBuffer = new BufferedReader(new FileReader(_repEntree + File.separator + fichier));
			if (_nbColonne > 0)
			{

				// Lecture des lignes d'en-tete
				consumeStationHeader(lecteurAvecBuffer);

				while ((ligneLue = lecteurAvecBuffer.readLine()) != null)
				{
					analyseOK = false;

					if (!ligneLue.trim().equals(Global.EMPTY_STRING))
					{
						switch (endData)
						{
						case EOF:
						case EMPTY_LINE:
						case NEXT_START_WITH:
						case BEGIN_LINE_CHANGES:
							analyseOK = true;
							break;
						case END_WITH:
							analyseOK = !ligneLue.startsWith(_indicateur);
						}

						if (analyseOK)
						{
							analyseLigne(ligneLue);

							for (int i = 0; i < _nbColonne; i++)
							{
								compareMax(i);
							}
						}
					}
				}
			}

		} catch (FileNotFoundException exc)
		{
			Utils.messageErreurAppend(Messages.error_file_preformat + Global.SPACE + fichier);
		} finally
		{
			if (lecteurAvecBuffer != null)
				lecteurAvecBuffer.close();
		}
	}

	private static void ecritureFichier(String ficEntree, String repSortie) throws IOException
	{
		BufferedReader lecteurAvecBuffer = null;
		PrintWriter fichPreformat = null;
		String ligneLue = Global.EMPTY_STRING;
		int numLigne = 0;
		int i, j;

		try
		{
			numLigne = 0;
			File file = new File(_repEntree + File.separator + ficEntree);
			lecteurAvecBuffer = new BufferedReader(new FileReader(_repEntree + File.separator + ficEntree));
			// fichPreformat = new PrintWriter(new BufferedWriter(new
			// FileWriter(repSortie + PF + ficEntree)));
			fichPreformat = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(repSortie + PF + ficEntree), Global.NEMO_ENCODING)));

			lecteurAvecBuffer.mark((int) file.length());
			// Lecture des lignes d'en-tete
			int lgEnTeteStation = consumeStationHeader(lecteurAvecBuffer);
			lecteurAvecBuffer.reset();

			while ((ligneLue = lecteurAvecBuffer.readLine()) != null)
			{
				numLigne++;
				if (numLigne <= lgEnTeteStation)
				{
					// Traitement de l'en-tete
					fichPreformat.println(ligneLue);
				} else
				{
					// Traitement des donnees
					analyseLigne(ligneLue);

					for (i = 0; i < _nbColonne; i++)
					{
						// Ajout des zeros apres
						if (_maxDecimale[i] > 0)
						{
							if (_colonne[i].length() > 0)
							{
								if (_colonne[i].lastIndexOf(Global.POINT) < 0)
								{
									_colonne[i] += Global.POINT;
								}
								for (j = 0; j < _maxDecimale[i] - _nbDecimale[i]; j++)
								{
									_colonne[i] += Global.ZERO;
								}
							} else
							{
								// on ajoute des espaces pour eviter le decalage
								for (j = 0; j < _maxDecimale[i] + 1; j++)
								{
									_colonne[i] += Global.SPACE;
								}
							}
						}

						// Ajout des espaces avant
						while (_colonne[i].length() < _maxEntier[i] + _maxDecimale[i] + 1)
						{
							_colonne[i] = Global.SPACE + _colonne[i];
							if (i == 0)
								space = true;
						}

						fichPreformat.print(_colonne[i] + Global.SPACE);
					}

					fichPreformat.println(Global.EMPTY_STRING);
				}
			}

		} catch (FileNotFoundException exc)
		{
			Utils.messageErreurAppend(Messages.error_file_preformat + Global.SPACE + ficEntree);
		} finally
		{
			if (lecteurAvecBuffer != null)
				lecteurAvecBuffer.close();
			if (fichPreformat != null)
				fichPreformat.close();
		}
	}

	private static int consumeStationHeader(BufferedReader lecteurAvecBuffer) throws IOException
	{

		String ligneLue = Global.EMPTY_STRING;
		int lgEnTeteStation = 0;

		// Lecture des lignes d'en-tete
		int endStationHeader = 0;
		while ((ligneLue = lecteurAvecBuffer.readLine()) != null)
		{
			lgEnTeteStation++;
			if (_isPattern)
			{
				if (!(_enTeteStationPattern.equals(Global.EMPTY_STRING) && _lgBeforeData == 0))
				{
					if (!_enTeteStationPattern.equals(Global.EMPTY_STRING) && ligneLue.startsWith(_enTeteStationPattern) && lgEnTeteStation > _enTeteFichier)
					{
						endStationHeader = lgEnTeteStation + _lgBeforeData;
					}
					if (_enTeteStationPattern.equals(Global.EMPTY_STRING))
					{
						endStationHeader = _lgBeforeData + _enTeteFichier;
					}
					if (lgEnTeteStation > _enTeteFichier && endStationHeader > 0 && lgEnTeteStation == endStationHeader)
					{
						break;
					}
				} else if (lgEnTeteStation == _enTeteFichier)
				{
					break;
				}
			} else
			{
				if (lgEnTeteStation == _enTeteFichier + _enTeteStation)
				{
					break;
				}
			}
		}

		return lgEnTeteStation;
	}

	public static boolean isPreformat()
	{
		return _preformatOK;
	}

	public static void setPreformat(boolean blnPreformat)
	{
		_preformatOK = blnPreformat;
	}

	public static boolean isSpaceAdded()
	{
		return space;
	}

}

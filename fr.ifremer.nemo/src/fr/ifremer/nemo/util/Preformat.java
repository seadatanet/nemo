package fr.ifremer.nemo.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import fr.ifremer.nemo.ui.Application;
import fr.ifremer.nemo.ui.EditDesc;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.ViewFile;

public class Preformat {
	
	///////////////
	// Attributs //
	///////////////
	
	// liste des fichiers
	private static String[] _listeFichiers;
	
	// liste des repertoires
	private static String[] _listeRepertoires;
	
	// fichier en entree
	private static String _ficEntree;
	
	// repertoire en entree
	private static String _repEntree;
	
	// repertoire en sortie
	private static File _repSortie;
	
	// Nombre de lignes d'en-tete de fichier
	private static int _enTeteFichier;
	
	// Nombre de lignes d'en-tete de station
	private static int _enTeteStation;
	
	// Indicateur de fin de station
	private static String _indicateur;
	
//	// Nombre de lignes de fin de station
//	private static int _finStation;
	
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
	

	public static String messagePreformat;
	
	//////////////
	// Methodes //
	//////////////
	

	
	
	public static void refreshEtatAvancement(Double pourcentEtape1, Double pourcentEtape2)
	{
		messagePreformat = Messages.getString("Preformat.9");
		messagePreformat += "\r\n";
		if (pourcentEtape1 != 100d) {
			messagePreformat += Messages.getString("Preformat.10")
					+ pourcentEtape1.intValue() + "%";

			messagePreformat += "\t";
			int i = 0;

			messagePreformat += "[";
			for (; i < ((Double) (pourcentEtape1 / 2d)).intValue(); i++)
				messagePreformat += "|";
			for (; i < 50; i++)
				messagePreformat += "-";
			messagePreformat += "]";
		} else {
			messagePreformat += Messages.getString("Preformat.10") + "OK";
		}

		messagePreformat += "\r\n";

		if (pourcentEtape2 != 100d) {
			messagePreformat += Messages.getString("Preformat.11")
					+ pourcentEtape2.intValue() + "%";

			messagePreformat += "\t";

			messagePreformat += "[";
			int i = 0;
			for (; i < ((Double) (pourcentEtape2 / 2d)).intValue(); i++)
				messagePreformat += "|";
			for (; i < 50; i++)
				messagePreformat += "-";

			messagePreformat += "]";
		} else {
			messagePreformat += Messages.getString("Preformat.11") + "OK";
		}

		// System.out.println(messagePreformat);
		Utils.messageOK(messagePreformat);
		
		Application.display.update();
		
	}
	

	
	
	public static void run() throws IOException {
		Double pourcentEtape1 = 0d;
		Double pourcentEtape2 = 0d;
		int nbFic = 0;
		
		Utils.messageOK("Preformatage en cours");
				
		_nbColonne = 1;	
		_preformatOK = false;
		
		if (ViewFile.radioFichier.getSelection()) {
			_ficEntree = ViewFile.nomFichier.substring(ViewFile.nomFichier.lastIndexOf('\\')+1);
			_repEntree = ViewFile.nomFichier.substring(0,ViewFile.nomFichier.lastIndexOf('\\'));
		} else {
			_ficEntree = "";
			_repEntree = ViewFile.nomRepertoire;
		}
				
		_enTeteFichier = Integer.parseInt(EditDesc.lignesEnteteFichier.getText());
		_enTeteStation = Integer.parseInt(EditDesc.lignesEnteteStation.getText());
//		_finStation = Integer.parseInt(EditDesc.lignesFinStation.getText());
	
		if (EditDesc.radEOF.getSelection()) {
			
		} else if (EditDesc.radLigne.getSelection()) {
			
		} else if (EditDesc.radManuel.getSelection()) {
			_indicateur = EditDesc.txtManuel.getText();
		}
		
//		FAE 33
//		if (EditDesc.buttonIndicFinDonnees.getSelection()) {
//			_indicateur = EditDesc.comboIndicRuptureDonnees.getText();
//		} else {
//			_donnees = Integer.parseInt(EditDesc.nbreLignesMesures.getText());
//		}
				
		if (ViewFile.radioTabulation.getSelection()) {
			_separateur = '\t';
		} else if (ViewFile.radioPointVirgule.getSelection()) {
			_separateur = ';';
		} else if (ViewFile.radioVirgule.getSelection()) {
			_separateur = ',';
		} else if (ViewFile.radioEspace.getSelection()) {
			_separateur = ' ';
		} else if (ViewFile.radioSeparateurAutre.getSelection()) {
			_separateur = ViewFile.textSeparateur.getText().toCharArray()[0];
		}
		
//		Utils.messageErreur("");
		
		/////////////////////////////////////////
		// Recuperation du nombres de colonnes //
		/////////////////////////////////////////
		if (ViewFile.radioFichier.getSelection()) {
			nombreColonnes(_ficEntree);
		} else if (ViewFile.radioCollectCas1.getSelection()) {
			_listeRepertoires = new File(ViewFile.nomRepertoire).list();
			for (String repertoire : _listeRepertoires) {
				_repEntree = ViewFile.nomRepertoire+"\\"+repertoire;
				_listeFichiers = new File(_repEntree).list();
				
				
				for (String fichier : _listeFichiers) {
					nombreColonnes(fichier);
					// on se contente de recuperer le nombre de colonnes du 1er fichier
					break;
				}
				
				// on se contente du 1er repertoire
				break;
			}
		} else {
			_listeFichiers = new File(ViewFile.nomRepertoire).list();
			_repEntree = ViewFile.nomRepertoire;
			
			for (String fichier : _listeFichiers) {
				nombreColonnes(fichier);
				// on se contente de recuperer le nombre de colonnes du 1er fichier
				break;
			}
		}
		
		_maxEntier = new int[_nbColonne];
		_maxDecimale = new int[_nbColonne];
		_nbEntier = new int[_nbColonne];
		_nbDecimale = new int[_nbColonne];
		_colonne = new String[_nbColonne];
		
		///////////////////////////////////////////////////
		// Recuperation des entiers et decimales maximum //
		///////////////////////////////////////////////////
		if (ViewFile.radioFichier.getSelection()) {
			getMax(_ficEntree);
		} else if (ViewFile.radioCollectCas1.getSelection()) {
			
			for (String repertoire : _listeRepertoires) {
				_repEntree = ViewFile.nomRepertoire+"\\"+repertoire;
				_listeFichiers = new File(_repEntree).list();
				nbFic = _listeFichiers.length;
				
				for (String fichier : _listeFichiers) {
					pourcentEtape1 += 100d / nbFic;
					refreshEtatAvancement(pourcentEtape1, pourcentEtape2);
					getMax(fichier);
				}
			}
		} else {
			nbFic = _listeFichiers.length;
			for (String fichier : _listeFichiers) {
				pourcentEtape1 += 100d / nbFic;
				refreshEtatAvancement(pourcentEtape1, pourcentEtape2);
				
				getMax(fichier);
			}
		}
		
		pourcentEtape1 = 100d;
		
//		for (i = 0 ; i < _nbColonne ; i++) {
//		//	System.out.println("Colonne " + (i+1) + '\t' + ":" + " entier : " + _maxEntier[i] + " - decimales : " + _maxDecimale[i]);
//		}
		
		///////////////////////////////////////
		// Ecriture des fichiers preformates //
		///////////////////////////////////////
		if (ViewFile.radioFichier.getSelection()) {
			ecritureFichier(_ficEntree, _repEntree);
		} else if (ViewFile.radioCollectCas1.getSelection()) {
			_repEntree = ViewFile.nomRepertoire;
			_repSortie = new File(_repEntree.substring(0,_repEntree.lastIndexOf('\\'))
					+"\\PF_"+_repEntree.substring(_repEntree.lastIndexOf('\\')+1));
			if (!_repSortie.exists()) {
				_repSortie.mkdirs(); 
			}
		
			for (String repertoire : _listeRepertoires) {
				_repEntree = ViewFile.nomRepertoire+"\\"+repertoire;
				_listeFichiers = new File(_repEntree).list();
				
				// Creation du repertoire pour les fichiers preformates
				File repPreformat = new File(_repSortie.getAbsolutePath()+"\\PF_"+_repEntree.substring(_repEntree.lastIndexOf('\\')+1));
				if (!repPreformat.exists()) {
					repPreformat.mkdirs();
				}
				
				// Ecriture des fichiers
				for (String fichier : _listeFichiers) {
					ecritureFichier(fichier, repPreformat.getAbsolutePath());
				}
			}
		} else {
			// Creation du repertoire pour les fichiers preformates
			// au meme niveau que le repertoire d'entree
			// en recopiant le nom avec le prefixe "PF_"
			_repSortie = new File(ViewFile.nomRepertoire.substring(0,ViewFile.nomRepertoire.lastIndexOf('\\'))
					+"\\PF_"+ViewFile.nomRepertoire.substring(ViewFile.nomRepertoire.lastIndexOf('\\')+1));
			if (!_repSortie.exists()) {
				_repSortie.mkdirs(); 
			}
			
			// Ecriture des fichiers
			nbFic = _listeFichiers.length;
			
			for (String fichier : _listeFichiers) {
				
				pourcentEtape2 += 100d / nbFic;
				
				refreshEtatAvancement(pourcentEtape1, pourcentEtape2);
							
				ecritureFichier(fichier, _repSortie.getAbsolutePath());
			}
		}	
		
		pourcentEtape2 = 100d;		
		
		refreshEtatAvancement(pourcentEtape1, pourcentEtape2);
		
		/////////////////////////////////////
		// Affichage du fichier preformate //
		/////////////////////////////////////
		if (ViewFile.radioFichier.getSelection()) {
			ViewFile.nomFichier = _repEntree + "\\PF_" + _ficEntree;
		} else {
			ViewFile.nomRepertoire = _repSortie.getAbsolutePath();
		}
		
		ViewFile.fileLoaded = true;

		if (ViewFile.radioFichier.getSelection()) {
			Utils.insertTextFich(ViewFile.nomFichier, ViewFile.styledTextCampagne, false);
			ViewFile.textAreaDescription.setText(Utils.texteBrut);
		} else if (ViewFile.radioCollectCas1.getSelection()) {
			if (Utils.findFilesCollectCas1(ViewFile.nomRepertoire, true)) {
				ViewFile.textAreaDescription
						.setText(Utils.texteBrut);
			}
		} else {
			if (Utils.findFiles(ViewFile.nomRepertoire, false)) {
				ViewFile.textAreaDescription
						.setText(Utils.texteBrut);
			}
		}
		
		setPreformat(true);

		// affichage etape0 dans formulaire
		// d'edition de
		// parametres
//		Utils.razGlobal();
// 		EditDesc.affichageEdition();

//		// remplissage de l'IHM par le modele charge ?
//		if (DataModel.ouverture) {
//			DataModel.fillDesc();
//		}
	}
	
	private static void analyseLigne(String ligneLue) {
		int i;
		
		int[] indexSeparateur = new int[_nbColonne+1];
				
		// Index des separateurs
		indexSeparateur[0] = -1;
		for (i = 1 ; i < _nbColonne+1 ; i++) {
			indexSeparateur[i] = ligneLue.indexOf(_separateur, indexSeparateur[i-1] + 1);
			
			// dernier separateur = fin de ligne
			if (indexSeparateur[i] < 0) {
				indexSeparateur[i] = ligneLue.length();
			}
		}
	
		// Decoupage des colonnes
		for (i = 0 ; i < _nbColonne ; i++) {
			_colonne[i] = ligneLue.substring(indexSeparateur[i] + 1, indexSeparateur[i+1]);
			
//			System.out.println("ligne " + ligneLue + "i " + i + "index sep " + indexSeparateur);
			
			if (_colonne[i].indexOf(".") > -1) {
				_nbEntier[i] = _colonne[i].indexOf(".");
				_nbDecimale[i] = _colonne[i].length() - _colonne[i].indexOf(".") - 1;
			} else {
				_nbEntier[i] = _colonne[i].length();
				_nbDecimale[i] = 0;
			}
		}
	}
	
	private static void compareMax(int i) {
		if (_nbEntier[i] > _maxEntier[i]) {
			_maxEntier[i] = _nbEntier[i];
		}
		
		if (_nbDecimale[i] > _maxDecimale[i]) {
			_maxDecimale[i] = _nbDecimale[i];
		}
	}
	
	private static void nombreColonnes(String fichier) throws IOException {
		BufferedReader lecteurAvecBuffer = null;
		String ligneLue = "";
		int numLigne = 0;
		int i;

		try {
			lecteurAvecBuffer = new BufferedReader(new FileReader(
					_repEntree + "\\" + fichier));
			
			// Lecture des lignes d'en-tete
			while ((ligneLue = lecteurAvecBuffer.readLine()) != null) {
				numLigne++;
				if (numLigne == _enTeteFichier + _enTeteStation) {
					break;
				}
			}
			
			// Lecture de la 1e ligne de donnees
			if ((ligneLue = lecteurAvecBuffer.readLine()) != null) {
				if (ligneLue.indexOf(_separateur) < 0) {
					_nbColonne = 0;
				} else {
					_nbColonne = 1;
					for (i = 0 ; i < ligneLue.length() ; i++) {
						if (ligneLue.charAt(i) == _separateur) {
							_nbColonne++;
						}
					}
				}
			}

//			System.out.println("Nombre de colonnes : " + _nbColonne);			
			
		} catch (FileNotFoundException exc) {
			Utils
					.messageErreur(Messages.getString("Preformat.2")
							+ fichier);		
		} finally {
			lecteurAvecBuffer.close();	
		}
	}
	
	private static void getMax(String fichier) throws IOException {
		BufferedReader lecteurAvecBuffer = null;
		String ligneLue = "";
		int numLigne = 0;
		int i;
		boolean analyseOK;
		
		
		try {
			lecteurAvecBuffer = new BufferedReader(new FileReader(
					_repEntree + "\\" + fichier));
			if (_nbColonne > 0) {
				while ((ligneLue = lecteurAvecBuffer.readLine()) != null) {
					numLigne++;
					analyseOK = false;
					
					
					if (numLigne > _enTeteFichier + _enTeteStation) {
						if (!ligneLue.trim().equals("")) {
							if (EditDesc.radEOF.getSelection()) {
								analyseOK = true;
							} else if (EditDesc.radLigne.getSelection()) {
								analyseOK = true;
							} else if (EditDesc.radManuel.getSelection()) {
								analyseOK = !ligneLue.startsWith(_indicateur);
							}
							
//								FAE 33
//								if (EditDesc.buttonIndicFinDonnees.getSelection()) {
//									analyseOK = !ligneLue.startsWith(_indicateur);
//								} else {
//									
//								}
							
							if (analyseOK) {
								analyseLigne(ligneLue);
								
								for (i = 0 ; i < _nbColonne ; i++) {
									compareMax(i);
								}
							}
						}
						
					}
				}
			}
			
		} catch (FileNotFoundException exc) {
			Utils
					.messageErreur(Messages.getString("Preformat.2")
							+ fichier);
		} finally {
			lecteurAvecBuffer.close();
		}
	}
	
	private static void ecritureFichier(String ficEntree, String repSortie) throws IOException {
		BufferedReader lecteurAvecBuffer = null;
		String ligneLue = "";
		int numLigne = 0;
		int i, j;

		PrintWriter fichPreformat = new PrintWriter(new BufferedWriter(
				new FileWriter(repSortie + "\\PF_" + ficEntree)));
		numLigne = 0;
		lecteurAvecBuffer = null;
		lecteurAvecBuffer = new BufferedReader(new FileReader(_repEntree
				+ "\\" + ficEntree));

		try {
			lecteurAvecBuffer = new BufferedReader(new FileReader(_repEntree
					+ "\\" + ficEntree));

			while ((ligneLue = lecteurAvecBuffer.readLine()) != null) {
				numLigne++;
				if (numLigne <= _enTeteFichier + _enTeteStation) {
					// Traitement de l'en-tete

					if (ligneLue.startsWith("Latitude")) {
						// Verification du format de latitude
						fichPreformat.println(ligneLue);
					} else {
						if (ligneLue.startsWith("Longitude")) {
							// Verification du format de longitude
							fichPreformat.println(ligneLue);
						} else {
							// sinon on recopie la ligne
							fichPreformat.println(ligneLue);
						}
					}

				} else {
					// Traitement des donnees

					analyseLigne(ligneLue);

					for (i = 0; i < _nbColonne; i++) {
						
						// Ajout des zeros apres
						if (_maxDecimale[i] > 0) {
							if (_colonne[i].length() > 0){
								if (_colonne[i].lastIndexOf(".") < 0) {
									_colonne[i] += ".";
								}

								for (j = 0; j < _maxDecimale[i] - _nbDecimale[i]; j++) {
									_colonne[i] += "0";
								}
							}else{
								// on ajoute des espaces pour eviter le decalage
								for (j = 0; j < _maxDecimale[i] +1; j++) {
									_colonne[i] += " ";
								}
							}
						}

						// Ajout des espaces avant
						while (_colonne[i].length() < _maxEntier[i]
								+ _maxDecimale[i] + 1) {
							_colonne[i] = " " + _colonne[i];
						}

						fichPreformat.print(_colonne[i] + " ");
					}

					fichPreformat.println("");
				}
			}

		} catch (FileNotFoundException exc) {
			Utils.messageErreur(Messages.getString("Preformat.2") + ficEntree);
		} finally {
			lecteurAvecBuffer.close();
			fichPreformat.close();
		//	Utils.messageOK(Messages.getString("Preformat.1"));
		}
	}

	public static boolean isPreformat() {
		return _preformatOK;
	}
	
	public static void setPreformat(boolean blnPreformat) {
		_preformatOK = blnPreformat;
	}
	
}

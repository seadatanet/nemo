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
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.desc.EditDesc;
import fr.ifremer.nemo.ui.view.ViewFile;

public class Preformat {
	
	///////////////
	// Attributs //
	///////////////
    
    private static String PF = "\\PF_"; //$NON-NLS-1$
	
//	// liste des fichiers
//	private static String[] _listeFichiers;
//	
//	// liste des repertoires
//	private static String[] _listeRepertoires;
//	
//	// fichier en entree
//	private static String _ficEntree;
	
	// repertoire en entree
	private static String _repEntree;
	
//	// repertoire en sortie
//	private static File _repSortie;
	
	// Nombre de lignes d'en-tete de fichier
	private static int _enTeteFichier;
	
	// Nombre de lignes d'en-tete de station
	private static int _lgEnTeteStation;
    private static boolean _isPattern;
    private static int _enTeteStation;
    private static String _enTeteStationPattern;
    private static int _lgBeforeData;
	
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

	
	//////////////
	// Methodes //
	//////////////
	

	
	
	public static void refreshEtatAvancement(Double pourcentEtape1, Double pourcentEtape2)
	{
        StringBuffer messagePreformat = new StringBuffer();
        messagePreformat.append(Messages.progress_file_preformat);
        messagePreformat.append(Global.RN);
		if (pourcentEtape1 != 100d) {
            messagePreformat.append(Messages.progress_file_preformat_analyse);
            messagePreformat.append(pourcentEtape1.intValue());
            messagePreformat.append(Global.POURCENT);

            messagePreformat.append(Global.TAB);
			int i = 0;

            messagePreformat.append("["); //$NON-NLS-1$
			for (; i < ((Double) (pourcentEtape1 / 2d)).intValue(); i++)
                messagePreformat.append( "|"); //$NON-NLS-1$
			for (; i < 50; i++)
                messagePreformat.append("-"); //$NON-NLS-1$
            messagePreformat.append("]"); //$NON-NLS-1$
		} else {
            messagePreformat.append(Messages.progress_file_preformat_analyse);
            messagePreformat.append(Global.SPACE);
            messagePreformat.append(Messages.success);
		}

        messagePreformat.append(Global.RN);

		if (pourcentEtape2 != 100d) {
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
		} else {
            messagePreformat.append(Messages.progress_file_preformat_write);
            messagePreformat.append(Global.SPACE);
            messagePreformat.append(Messages.success);
		}

		Utils.messageOK(messagePreformat.toString());
		
		Application.display.update();
		
	}
	

	
	
	public static void run() throws IOException {
//		Double pourcentEtape1 = 0d;
//		Double pourcentEtape2 = 0d;
//		int nbFic = 0;

        _repEntree = Global.EMPTY_STRING;
        String[] listeFichiers;
        File repSortie;
        
		Utils.messageOK(Messages.progress_file_preformat);
				
		_nbColonne = 1;	
		_preformatOK = false;
		
//		if (ViewFile.radioFichier.getSelection()) {
//			_ficEntree = ViewFile.nomFichier.substring(ViewFile.nomFichier.lastIndexOf('\\')+1);
//			_repEntree = ViewFile.nomFichier.substring(0,ViewFile.nomFichier.lastIndexOf('\\'));
//		} else {
//			_ficEntree = Global.EMPTY_STRING;
//			_repEntree = ViewFile.nomRepertoire;
//		}
				
		_enTeteFichier = Integer.parseInt(EditDesc.sectionEnteteFichier.getValue());
        
        _isPattern = !EditDesc.sectionEnteteStation.isSelectNumber();
        _enTeteStation = Integer.parseInt(EditDesc.sectionEnteteStation.getValue());
        _enTeteStationPattern = EditDesc.sectionEnteteStation.getIndicatorText();
        _lgBeforeData = Integer.parseInt(EditDesc.sectionEnteteStation.getNbLinesBeforeData());
        
	
		if (EditDesc.sectionRuptureDonnees.radEOFselected()) {
			
		} else if (EditDesc.sectionRuptureDonnees.radLigneSelected()) {
			
		} else if (EditDesc.sectionRuptureDonnees.radNextStationSelected()) {
            
        } else if (EditDesc.sectionRuptureDonnees.radManuelSelected()) {
			_indicateur = EditDesc.sectionRuptureDonnees.getValue();
		}
        
				
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
		
		if (ViewFile.radioFichier.getSelection()) {

            String ficEntree = ViewFile.nomFichier.substring(ViewFile.nomFichier.lastIndexOf('\\')+1);
            listeFichiers = new String[]{ficEntree};
            _repEntree = ViewFile.nomFichier.substring(0,ViewFile.nomFichier.lastIndexOf('\\'));
            
            processPreformat(listeFichiers, _repEntree);
            
//            /////////////////////////////////////////
//            // Recuperation du nombres de colonnes //
//            /////////////////////////////////////////
//            nombreColonnes(_ficEntree);
//            _maxEntier = new int[_nbColonne];
//            _maxDecimale = new int[_nbColonne];
//            _nbEntier = new int[_nbColonne];
//            _nbDecimale = new int[_nbColonne];
//            _colonne = new String[_nbColonne];
//
//            ///////////////////////////////////////////////////
//            // Recuperation des entiers et decimales maximum //
//            ///////////////////////////////////////////////////
//			getMax(_ficEntree);
//            
//            pourcentEtape1 = 100d;
//            
//            ///////////////////////////////////////
//            // Ecriture des fichiers preformates //
//            ///////////////////////////////////////
//            ecritureFichier(_ficEntree, _repEntree);
//
//            pourcentEtape2 = 100d;      
//            refreshEtatAvancement(pourcentEtape1, pourcentEtape2);
            
            ViewFile.nomFichier = _repEntree + PF + ficEntree;
            ViewFile.fileLoaded = true;

            Utils.insertTextFich(ViewFile.nomFichier, ViewFile.styledTextCampagne, false);
            ViewFile.textAreaDescription.setText(Utils.texteBrut);
            
		} 
        else if (ViewFile.radioCollectCas1.getSelection()) {
            
            
            File repCollection = new File(ViewFile.nomRepertoire);            
            String[] listeRepertoires = repCollection.list();
			
			for (String repertoire : listeRepertoires) {
				_repEntree = repCollection.getAbsolutePath() + File.separator + repertoire;
				listeFichiers = new File(_repEntree).list();
                
                repSortie = new File(repCollection.getParent() + File.separator + PF + repCollection.getName());
                
                if (! repSortie.exists()) {
                     repSortie.mkdirs(); 
                }
                
                // Creation du repertoire pour les fichiers preformates
                File repPreformat = new File(repSortie.getAbsolutePath() + PF + repertoire);
                if (!repPreformat.exists()) {
                    repPreformat.mkdirs();
                }
                
                processPreformat(listeFichiers, repPreformat.getAbsolutePath());
				
//				for (String fichier : _listeFichiers) {
//                    /////////////////////////////////////////
//                    // Recuperation du nombres de colonnes //
//                    /////////////////////////////////////////
//                    nombreColonnes(fichier);
//                    _maxEntier = new int[_nbColonne];
//                    _maxDecimale = new int[_nbColonne];
//                    _nbEntier = new int[_nbColonne];
//                    _nbDecimale = new int[_nbColonne];
//                    _colonne = new String[_nbColonne];
//
//                    ///////////////////////////////////////////////////
//                    // Recuperation des entiers et decimales maximum //
//                    ///////////////////////////////////////////////////
//					pourcentEtape1 += 100d / nbFic;
//					refreshEtatAvancement(pourcentEtape1, pourcentEtape2);
//					getMax(fichier);
//                    
//                    pourcentEtape1 = 100d;
//                    
//                    ///////////////////////////////////////
//                    // Ecriture des fichiers preformates //
//                    ///////////////////////////////////////
//                    ecritureFichier(fichier, repPreformat.getAbsolutePath());
//				}
//                pourcentEtape2 = 100d;      
//                refreshEtatAvancement(pourcentEtape1, pourcentEtape2);
                ViewFile.nomRepertoire =  repSortie.getAbsolutePath();
                ViewFile.fileLoaded = true;
                
                if (Utils.findFilesCollectCas1(ViewFile.nomRepertoire, true)) {
                    ViewFile.textAreaDescription
                            .setText(Utils.texteBrut);
                }
			}
		} 
        else {
            _repEntree = ViewFile.nomRepertoire;
            listeFichiers = new File(_repEntree).list();
            
            // Creation du repertoire pour les fichiers preformates
            // au meme niveau que le repertoire d'entree
            // en recopiant le nom avec le prefixe "PF_"
            repSortie = new File(ViewFile.nomRepertoire.substring(0,ViewFile.nomRepertoire.lastIndexOf('\\'))
                    + PF + ViewFile.nomRepertoire.substring(ViewFile.nomRepertoire.lastIndexOf('\\')+1));
            if (!repSortie.exists()) {
               repSortie.mkdirs(); 
            }
            
            processPreformat(listeFichiers, repSortie.getAbsolutePath());
            
//			for (String fichier : _listeFichiers) {
//                /////////////////////////////////////////
//                // Recuperation du nombres de colonnes //
//                /////////////////////////////////////////
//                nombreColonnes(fichier);
//			}
//			_maxEntier = new int[_nbColonne];
//			_maxDecimale = new int[_nbColonne];
//			_nbEntier = new int[_nbColonne];
//			_nbDecimale = new int[_nbColonne];
//			_colonne = new String[_nbColonne];
//            
//            for (String fichier : _listeFichiers){
//                ///////////////////////////////////////////////////
//                // Recuperation des entiers et decimales maximum //
//                ///////////////////////////////////////////////////
//                pourcentEtape1 += 100d / nbFic;
//                refreshEtatAvancement(pourcentEtape1, pourcentEtape2);
//                getMax(fichier);
//            }
//            
//			pourcentEtape1 = 100d;
//            
//            for (String fichier : _listeFichiers){
//                ///////////////////////////////////////
//                // Ecriture des fichiers preformates //
//                ///////////////////////////////////////
//                pourcentEtape2 += 100d / nbFic;
//                refreshEtatAvancement(pourcentEtape1, pourcentEtape2);
//                ecritureFichier(fichier, _repSortie.getAbsolutePath());
//            }
//            
//            pourcentEtape2 = 100d;      
//            refreshEtatAvancement(pourcentEtape1, pourcentEtape2);
            
            ViewFile.nomRepertoire = repSortie.getAbsolutePath();
            ViewFile.fileLoaded = true;
            
            if (Utils.findFiles(ViewFile.nomRepertoire, false)) {
                ViewFile.textAreaDescription
                        .setText(Utils.texteBrut);
            }
		}
		setPreformat(true);
	}
    
    private static void processPreformat(String[] lstFichiers,
                                         String repSortie) throws IOException{
        
        Double pourcentEtape1 = 0d;
        Double pourcentEtape2 = 0d;
        
        int nbFic = lstFichiers.length;
        
        /////////////////////////////////////////
        // Recuperation du nombres de colonnes //
        /////////////////////////////////////////
        for (String fichier : lstFichiers) {
            nombreColonnes(fichier);
        }
        _maxEntier = new int[_nbColonne];
        _maxDecimale = new int[_nbColonne];
        _nbEntier = new int[_nbColonne];
        _nbDecimale = new int[_nbColonne];
        _colonne = new String[_nbColonne];
        
        ///////////////////////////////////////////////////
        // Recuperation des entiers et decimales maximum //
        ///////////////////////////////////////////////////
        for (String fichier : lstFichiers){
            pourcentEtape1 += 100d / nbFic;
            refreshEtatAvancement(pourcentEtape1, pourcentEtape2);
            getMax(fichier);
        }
        
        pourcentEtape1 = 100d;
        
        ///////////////////////////////////////
        // Ecriture des fichiers preformates //
        ///////////////////////////////////////
        for (String fichier : lstFichiers){
            pourcentEtape2 += 100d / nbFic;
            refreshEtatAvancement(pourcentEtape1, pourcentEtape2);
            ecritureFichier(fichier, repSortie);
        }
        
        pourcentEtape2 = 100d;      
        refreshEtatAvancement(pourcentEtape1, pourcentEtape2);
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
			
			if (_colonne[i].indexOf(Global.POINT) > -1) {
				_nbEntier[i] = _colonne[i].indexOf(Global.POINT);
				_nbDecimale[i] = _colonne[i].length() - _colonne[i].indexOf(Global.POINT) - 1;
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
		String ligneLue = Global.EMPTY_STRING;
 		_lgEnTeteStation = 0;
		int i;

		try {
			lecteurAvecBuffer = new BufferedReader(new FileReader(
					_repEntree + "\\" + fichier)); //$NON-NLS-1$
			
			// Lecture des lignes d'en-tete
            int endStationHeader = 0;
			while ((ligneLue = lecteurAvecBuffer.readLine()) != null) {
                _lgEnTeteStation++;
                if (_isPattern){
                    if (!(_enTeteStationPattern.equals(Global.EMPTY_STRING) &&  _lgBeforeData == 0)){
                        if (!_enTeteStationPattern.equals(Global.EMPTY_STRING) 
                                && ligneLue.startsWith(_enTeteStationPattern) 
                                && _lgEnTeteStation > _enTeteFichier){
                            endStationHeader = _lgEnTeteStation + _lgBeforeData;
                        }
                        if (_enTeteStationPattern.equals(Global.EMPTY_STRING)){
                            endStationHeader = _lgBeforeData + _enTeteFichier;
                        }
                        if (_lgEnTeteStation > _enTeteFichier
                                && endStationHeader > 0
                                && _lgEnTeteStation == endStationHeader){
                            break;
                        }
                    }
                    else if (_lgEnTeteStation == _enTeteFichier){
                        break;
                    }
                }
                else{
                    if (_lgEnTeteStation == _enTeteFichier + _enTeteStation) {
                        break;
                    }
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
			
		} catch (FileNotFoundException exc) {
			Utils.messageErreurAppend(Messages.error_file_preformat + Global.SPACE + fichier);		
		} finally {
			if (lecteurAvecBuffer != null) lecteurAvecBuffer.close();	
		}
	}
	
	private static void getMax(String fichier) throws IOException {
		BufferedReader lecteurAvecBuffer = null;
		String ligneLue = Global.EMPTY_STRING;
		int numLigne = 0;
		int i;
		boolean analyseOK;
		
		
		try {
			lecteurAvecBuffer = new BufferedReader(new FileReader(
					_repEntree + "\\" + fichier)); //$NON-NLS-1$
			if (_nbColonne > 0) {
				while ((ligneLue = lecteurAvecBuffer.readLine()) != null) {
					numLigne++;
					analyseOK = false;
					
					
					if (numLigne > _lgEnTeteStation) {
						if (!ligneLue.trim().equals(Global.EMPTY_STRING)) {
							if (EditDesc.sectionRuptureDonnees.radEOFselected()) {
								analyseOK = true;
							} else if (EditDesc.sectionRuptureDonnees.radLigneSelected()) {
								analyseOK = true;
							} else if (EditDesc.sectionRuptureDonnees.radNextStationSelected()) {
                                analyseOK = true;
                            } else if (EditDesc.sectionRuptureDonnees.radManuelSelected()) {
								analyseOK = !ligneLue.startsWith(_indicateur);
							}
							
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
					.messageErreurAppend(Messages.error_file_preformat + Global.SPACE
							+ fichier);
		} finally {
            if (lecteurAvecBuffer != null) lecteurAvecBuffer.close();
		}
	}
	
	private static void ecritureFichier(String ficEntree, String repSortie) throws IOException {
		BufferedReader lecteurAvecBuffer = null;
		String ligneLue = Global.EMPTY_STRING;
		int numLigne = 0;
		int i, j;

		PrintWriter fichPreformat = new PrintWriter(new BufferedWriter(
				new FileWriter(repSortie + PF + ficEntree)));
		numLigne = 0;
		lecteurAvecBuffer = new BufferedReader(new FileReader(_repEntree
				+ "\\" + ficEntree)); //$NON-NLS-1$

		try {
			lecteurAvecBuffer = new BufferedReader(new FileReader(_repEntree
					+ "\\" + ficEntree)); //$NON-NLS-1$

			while ((ligneLue = lecteurAvecBuffer.readLine()) != null) {
				numLigne++;
				if (numLigne <= _lgEnTeteStation) {
					// Traitement de l'en-tete

					if (ligneLue.startsWith("Latitude")) { //$NON-NLS-1$
						// Verification du format de latitude
						fichPreformat.println(ligneLue);
					} else {
						if (ligneLue.startsWith("Longitude")) { //$NON-NLS-1$
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
								if (_colonne[i].lastIndexOf(Global.POINT) < 0) {
									_colonne[i] += Global.POINT;
								}

								for (j = 0; j < _maxDecimale[i] - _nbDecimale[i]; j++) {
									_colonne[i] += Global.ZERO;
								}
							}else{
								// on ajoute des espaces pour eviter le decalage
								for (j = 0; j < _maxDecimale[i] +1; j++) {
									_colonne[i] += Global.SPACE;
								}
							}
						}

						// Ajout des espaces avant
						while (_colonne[i].length() < _maxEntier[i]
								+ _maxDecimale[i] + 1) {
							_colonne[i] = Global.SPACE + _colonne[i];
						}

						fichPreformat.print(_colonne[i] + Global.SPACE);
					}

					fichPreformat.println(Global.EMPTY_STRING);
				}
			}

		} catch (FileNotFoundException exc) {
			Utils.messageErreurAppend(Messages.error_file_preformat + Global.SPACE + ficEntree);
		} finally {
			lecteurAvecBuffer.close();
			fichPreformat.close();
		}
	}

	public static boolean isPreformat() {
		return _preformatOK;
	}
	
	public static void setPreformat(boolean blnPreformat) {
		_preformatOK = blnPreformat;
	}
	
}

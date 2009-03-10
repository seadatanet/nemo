package fr.ifremer.nemo.ui;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class EditDesc {
    
    // /////////////////////////////////////////

    public static Section sectionEnteteFichier, sectionEnteteStation,
            sectionFinStation, sectionRuptureDonnees = null;

    public static Text lignesEnteteFichier, lignesEnteteStation,
            lignesFinStation, txtManuel = null;

    public static Button buttonRAZ, buttonExpandDesc, 
            buttonValidateDesc, buttonLignesEnteteStation,
            btnInjectFin,
            radEOF, radLigne, radManuel;


    public static Group groupRuptureDonnees;

    public static ArrayList<Text> listeTextDesc;

    public static ArrayList<Section> listeSectionsDesc;

    public static Boolean okApresReset;

    public static boolean expand, validee, loaded;

    // expand : savoir si sections ouvertes ou non
    // validee : si etape validee
    // loaded : si l'etape est chargee et affichee
    public static ScrolledForm formEdition;

    // //////////////////////////////////////////

    private static Shell shell = new Shell();
    
    public static void affichageEdition() {
        // initialisation zone edition pour etape description de fichier
        ViewLog.aideDesc();
        if (ViewFile.fileLoaded) {

            formEdition = ViewEdit.formEdition;

            okApresReset = true;

            // raz message erreur
            Utils.messageErreur(Global.EMPTY_STRING);

            // de base les sections sont refermees
            expand = false;

            // flags de toutes les etapes e 0
            EditDesc.validee = false;
            EditSurvey.validee = false;
            EditStation.validee = false;
            EditData.validee = false;

            
            
            loaded = true;

            /*
             * recreer un formEdition sur le toolkit principal pour pouvoir
             * faire un dispose() dessus mais garder toutes les sections afin de
             * les reafficher par la suite dans DrawingBack.
             */

            GridLayout gridlayout = new GridLayout();
            gridlayout.numColumns = 1;
            formEdition.getBody().setLayout(gridlayout);

            formEdition.setText(Messages
                    .getString("EditDesc.descriptiondeFichier")); //$NON-NLS-1$

            // init de la arrayList des sections
            listeSectionsDesc = new ArrayList<Section>();

            // init de la arraylist des textes
            listeTextDesc = new ArrayList<Text>();

            // init composants IHM
            shell.close();
            shell = new Shell();
            disposeElements();
            
            initSections();
            initText();
            initBoutons();

            // bouton "valider etape"
            buttonValidateDesc.setParent(formEdition.getBody());
            buttonValidateDesc.setText(Messages
                    .getString("EditDesc.validerLetape")); //$NON-NLS-1$

            // bouton RAZ
            buttonRAZ.setParent(formEdition.getBody());
            buttonRAZ.setText("Reset"); //$NON-NLS-1$

            // boutons collapse all / expand all
            buttonExpandDesc.setParent(formEdition.getBody());
            buttonExpandDesc.setText("+"); //$NON-NLS-1$
            
            formEdition.redraw();
            formEdition.reflow(true);
            
            // initialisation liste des sections de la partie description
            // fichier

            // section entete fichier
            Drawing
                    .sectionLabelTextInjectNbreLignes(
                            formEdition,
                            Messages.getString("EditDesc.enteteFichier"), Messages.getString("EditDesc.surlignezLignes"), sectionEnteteFichier, lignesEnteteFichier); //$NON-NLS-1$ //$NON-NLS-2$
            lignesEnteteFichier.setText(Global.ZERO);


            Drawing
                    .sectionLabelTextInjectNbreLignes(
                            formEdition,
                            Messages.getString("EditDesc.enteteStation"), Messages.getString("EditDesc.nbreLignesEntete"), sectionEnteteStation, lignesEnteteStation); //$NON-NLS-1$ //$NON-NLS-2$
            lignesEnteteStation.setText(Global.ZERO);
            
            // section fin de station
            Drawing
                    .sectionLabelTextInjectNbreLignes(
                            formEdition,
                            Messages.getString("EditDesc.finStation"), Messages.getString("EditDesc.nbreLignesFinStation"), sectionFinStation, lignesFinStation); //$NON-NLS-1$ //$NON-NLS-2$
            lignesFinStation.setText(Global.ZERO); 

            
            Drawing
                    .sectionComboInjectNbreLignes(
                            formEdition,
                            Messages.getString("EditDesc.indicFinDonnees"), Messages.getString("EditDesc.indiquezIndicFinDonnees"), sectionRuptureDonnees, radEOF, radLigne, radManuel, txtManuel, btnInjectFin); //$NON-NLS-1$ //$NON-NLS-2$
            selectComboEnd(radEOF);
            
            // listeners
            buttonExpandDesc
                    .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                        @Override
                        public void widgetSelected(
                                org.eclipse.swt.events.SelectionEvent e) {
                            for (Section desc : EditDesc.listeSectionsDesc){
                                if (!expand){
                                    desc.setExpanded(true);
                                }else{
                                    desc.setExpanded(false);
                                }
                            }
                            if (!expand){
                                expand = true;
                                buttonExpandDesc.setText("-"); //$NON-NLS-1$
                            }else{
                                expand = false;
                                buttonExpandDesc.setText("+"); //$NON-NLS-1$
                            }

                            // refresh global
                            formEdition.reflow(true);

                        }
                    });

            buttonValidateDesc
                    .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                        @Override
                        public void widgetSelected(
                                org.eclipse.swt.events.SelectionEvent e) {
                            // on passe en ppar l'etape e laquelle on se trouve
                            // Etape.etape(0);
                            
                            Etape.validationDesc();

                        }
                    });

            buttonRAZ
                    .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                        @Override
                        public void widgetSelected(
                                org.eclipse.swt.events.SelectionEvent e) {

                            razText(listeTextDesc);

                        }
                    });

            // refresh du formulaire
            formEdition.reflow(true);

            // affichage courant
            ViewFile.affiche = 0;

            // remplissage de l'IHM par le modele charge ?
            if (DataModel.ouverture) {
                DataModel.fillDesc(false);
            }

            // nom de l'application
            nomNemo();

        }

    }

    public static void razEdition() {
        // reset de toute la zone d'edition apres etape descriptioon de fichier
        // on retire toutes les sections et les boutons expand et validation

        // effacement des boutons 
        buttonExpandDesc.setParent(shell);
        buttonRAZ.setParent(shell);
        buttonValidateDesc.setParent(shell);
        
        // effacement des sections
        for (Section desc : EditDesc.listeSectionsDesc){
            desc.setExpanded(true);
            desc.setParent(shell);
        }

        // effacement titre formulaire
        ViewEdit.formEdition.setText(Global.EMPTY_STRING);

        // refresh global
        ViewEdit.formEdition.reflow(true);

    }

    public static void DrawingBack() {
        // reset de toute la zone d'edition apres etape description de fichier
        // on retire toutes les sections et les boutons expand et validation

        ViewLog.aideDesc();
        nomNemo();

        // raz message erreur
        Utils.messageErreur(Global.EMPTY_STRING);

        ScrolledForm s;
        s = ViewEdit.formEdition;

        // affichage des boutons 
        buttonValidateDesc.setParent(s.getBody());
        buttonRAZ.setParent(s.getBody());
        buttonExpandDesc.setParent(s.getBody());

        // affichage des sections
        for (Section desc : EditDesc.listeSectionsDesc){
            desc.setExpanded(false);
            desc.setParent(s.getBody());
        }

        // affichage titre formulaire
        ViewEdit.formEdition.setText(Messages
                .getString("EditDesc.descriptiondeFichier")); //$NON-NLS-1$

        // refresh global
        ViewEdit.formEdition.reflow(true);

        // affichage courant
        ViewFile.affiche = 0;

    }

    public static void initSections() {
        
        sectionEnteteFichier = new Section(shell, SWT.NONE);
        sectionEnteteStation = new Section(shell, SWT.NONE);
        sectionFinStation = new Section(shell, SWT.NONE);
        sectionRuptureDonnees = new Section(shell, SWT.NONE);

    }

    public static void initText() {
        lignesEnteteFichier = new Text(shell, SWT.BORDER);
        listeTextDesc.add(lignesEnteteFichier);
        lignesEnteteStation = new Text(shell, SWT.BORDER);
        listeTextDesc.add(lignesEnteteStation);
        lignesFinStation = new Text(shell, SWT.BORDER);
        listeTextDesc.add(lignesFinStation);
        txtManuel = new Text(shell, SWT.BORDER);
        listeTextDesc.add(txtManuel);
        
        // remplissage de la liste des textes et ajout des listeners
        ajoutModifyListenerText(listeTextDesc);

    }

    public static void initBoutons() {
        
        buttonRAZ = new Button(shell, SWT.NONE);
        buttonLignesEnteteStation = new Button(shell, SWT.RADIO);
        buttonExpandDesc = new Button(shell, SWT.NONE);
        buttonValidateDesc = new Button(shell, SWT.NONE);
        radEOF = new Button(shell, SWT.RADIO);
        radLigne = new Button(shell, SWT.RADIO);
        radManuel = new Button(shell, SWT.RADIO);
        btnInjectFin = new Button(shell, SWT.NONE);
    }

    public static void ajoutModifyListenerText(ArrayList<Text> liste) {
        int taille = liste.size();
        for (int i = 0; i < taille; i++) {
            liste.get(i).addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent event) {
                    // on modifie la valeur du champ texte

                    // des qu'on modifie une valeur,
                    // invalidation de l'étape en cours et des suivantes
                    Utils.invalidEtapes();
                }
            });

        }
    }

    public static void razText(ArrayList<Text> liste) {

        for (Text text : liste){
            text.setText(Global.EMPTY_STRING);
        }
        lignesFinStation.setText(Global.ZERO);

        // invalidation de l'étape en cours et des suivantes
        Utils.invalidEtapes();
    }

    public static void nomNemo() {
        // nom de l'application
        if (ViewFile.dossierCampagne) {
            // dossier campagne

            File fich = new File(ViewFile.nomRepertoire);
            if (DataModel.ouverture) {
                ApplicationWorkbenchWindowAdvisor.configurer_titre
                        .setTitle(Messages.getString("EditDesc.nemoDossier") + fich.getName() + Messages.getString("EditDesc.modele") + OpenAction.nomFichierModele + " ]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

            } else {
                ApplicationWorkbenchWindowAdvisor.configurer_titre
                        .setTitle(Messages.getString("EditDesc.dossier") + fich.getName() + " ]"); //$NON-NLS-1$ //$NON-NLS-2$
            }

        } else {
            // fichier campagne
            File fich;
            fich = new File(ViewFile.nomFichier);

            if (DataModel.ouverture) {
                ApplicationWorkbenchWindowAdvisor.configurer_titre
                        .setTitle(Messages.getString("EditDesc.fichier") + fich.getName() + Messages.getString("EditDesc.modele") + OpenAction.nomFichierModele + " ]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            } else {
                ApplicationWorkbenchWindowAdvisor.configurer_titre
                        .setTitle(Messages.getString("EditDesc.fichier") + fich.getName() + " ]"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

    }
    
    public static void selectComboEnd(Button radio) {
        if (radio == EditDesc.radEOF) {
            EditDesc.radEOF.setSelection(true);
            EditDesc.radLigne.setSelection(false);
            EditDesc.radManuel.setSelection(false);

            EditDesc.txtManuel.setEnabled(false);
            EditDesc.btnInjectFin.setEnabled(false);
            
        } else if (radio == EditDesc.radLigne) {
            EditDesc.radEOF.setSelection(false);
            EditDesc.radLigne.setSelection(true);
            EditDesc.radManuel.setSelection(false);

            EditDesc.txtManuel.setEnabled(false);
            EditDesc.btnInjectFin.setEnabled(false);
            
        } else if (radio == EditDesc.radManuel) {
            EditDesc.radEOF.setSelection(false);
            EditDesc.radLigne.setSelection(false);
            EditDesc.radManuel.setSelection(true);
            
            EditDesc.txtManuel.setEnabled(true);
            EditDesc.btnInjectFin.setEnabled(true);
            
        } else {
            EditDesc.radEOF.setSelection(true);
            EditDesc.radLigne.setSelection(false);
            EditDesc.radManuel.setSelection(false);

            EditDesc.txtManuel.setEnabled(false);
            EditDesc.btnInjectFin.setEnabled(false);
        }
        
        // invalidation de l'étape en cours et des suivantes
        Utils.invalidEtapes();
    }

    public static void disposeElements()
    {              
        if (lignesEnteteFichier != null) {
            lignesEnteteFichier.dispose();
            lignesEnteteStation.dispose();
            lignesFinStation.dispose();
            txtManuel.dispose();
        }

        if (buttonRAZ != null) {
            buttonRAZ.dispose();
            buttonLignesEnteteStation.dispose();
            buttonExpandDesc.dispose();
            buttonValidateDesc.dispose();
            radEOF.dispose();
            radLigne.dispose();
            radManuel.dispose();
            btnInjectFin.dispose();
        }
        
        for (Section section : listeSectionsDesc){
            section.dispose();
        }
        
        
//        if (sectionEnteteFichier != null) {
//            sectionEnteteFichier.dispose();
//            sectionEnteteStation.dispose();
//            sectionFinStation.dispose();
//            sectionRuptureDonnees.dispose();
//        }
    }


    public static void invalidEtape(){
        okApresReset = false;
        validee = false;
        if (ViewFile.affiche != 0){
            loaded = false;
            disposeElements();
        }
        ViewFile.tabItem.setText(Messages.getString("EditDesc.descriptionFichier")); //$NON-NLS-1$
    }
    
}

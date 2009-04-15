package fr.ifremer.nemo.ui;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import fr.ifremer.nemo.ui.object.FileSection;
import fr.ifremer.nemo.ui.object.NemoSection;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class EditDesc {
    
    // /////////////////////////////////////////

    private static Shell shell = new Shell();

    public static Button buttonValidate, buttonRAZ, buttonExpand;
    public static Boolean okApresReset;
    public static boolean expand, validee, loaded;
    public static ScrolledForm formEdition;
    public static ArrayList<NemoSection> listeSectionsDesc;
    public static FileSection sectionEnteteFichier, sectionEnteteStation,
                              sectionFinStation, sectionRuptureDonnees;
    
    public static void affichageEdition() {
        // initialisation zone edition pour etape description de fichier
        ViewLog.aideDesc();
        if (ViewFile.fileLoaded) {

            formEdition = ViewEdit.formEdition;

            // raz message erreur
            Utils.messageErreur(Global.EMPTY_STRING);
            
            expand = false; // de base les sections sont refermees
            okApresReset = true;
            loaded = true;
            ViewFile.affiche = 0;

            // flags de toutes les etapes e 0
            EditDesc.validee = false;
            EditSurvey.validee = false;
            EditStation.validee = false;
            EditData.validee = false;
  
            /*
             * recreer un formEdition sur le toolkit principal pour pouvoir
             * faire un dispose() dessus mais garder toutes les sections afin de
             * les reafficher par la suite dans DrawingBack.
             */
            formEdition.getBody().setLayout(new GridLayout());
            formEdition.setText(Messages.edit_file_title); 

            // init composants IHM
            shell.close();
            shell = new Shell();
            disposeElements();
            
            // init des objets
            listeSectionsDesc = new ArrayList<NemoSection>();
            initBoutons();
            initSections();
            
            // remplissage de l'IHM par le modèle chargé
            if (DataModel.ouverture) {
                DataModel.fillDesc(false);
            } 
            
            formEdition.redraw();
            formEdition.reflow(true);

            // bouton "valider etape"
            buttonValidate.setParent(formEdition.getBody());
            buttonValidate.setText(Messages.button_validateStep);
            buttonValidate.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e) {
                    
                    Etape.validationDesc();
                }
            });

            // bouton RAZ
            buttonRAZ.setParent(formEdition.getBody());
            buttonRAZ.setText(Messages.button_reset);
            buttonRAZ.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e) {

                    razText();
                    // invalidation de l'étape en cours et des suivantes
                    Utils.invalidEtapes();

                }
            });

            // boutons collapse all / expand all
            buttonExpand.setParent(formEdition.getBody());
            buttonExpand.setText(Global.PLUS); 
            buttonExpand.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e) {
                    for (NemoSection desc : listeSectionsDesc){
                        if (!expand && desc.isEnabled()){
                            desc.setExpanded(true);
                        }else{
                            desc.setExpanded(false);
                        }
                    }
                    if (!expand){
                        expand = true;
                        buttonExpand.setText(Global.MOINS);
                    }else{
                        expand = false;
                        buttonExpand.setText(Global.PLUS); 
                    }

                    // refresh global
                    formEdition.reflow(true);

                }
            });
            

            // Draw Station Sections          
            for (NemoSection desc : listeSectionsDesc){
                desc.initSection(formEdition);
                desc.draw(formEdition.getBody());
            }
            
            actualiseEdition();

            ViewEdit.formEdition.reflow(true);

            // refresh du formulaire
            formEdition.reflow(true);
            
            // nom de l'application
            nomNemo();
        }

    }

    public static void razEdition() {
        // reset de toute la zone d'edition apres etape descriptioon de fichier
        // on retire toutes les sections et les boutons expand et validation

        // effacement des boutons 
        buttonExpand.setParent(shell);
        buttonRAZ.setParent(shell);
        buttonValidate.setParent(shell);
        
        // effacement des sections
        for (NemoSection desc : listeSectionsDesc){
            desc.setExpanded(true);
            desc.initSection(shell);
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
        buttonValidate.setParent(s.getBody());
        buttonRAZ.setParent(s.getBody());
        buttonExpand.setParent(s.getBody());

        // affichage des sections
        for (NemoSection desc : listeSectionsDesc){
            desc.setExpanded(false);
            desc.initSection(s.getBody());
        }

        // affichage titre formulaire
        ViewEdit.formEdition.setText(Messages.edit_file_title); 

        // refresh global
        ViewEdit.formEdition.reflow(true);

        // affichage courant
        ViewFile.affiche = 0;

    }

    public static void initSections() {
        sectionEnteteFichier = new FileSection(shell, NemoSection.F_FILEHEADER, Messages.getString(NemoSection.F_FILEHEADER), Messages.edit_file_enteteFichier, Global.ZERO, false);
        listeSectionsDesc.add(sectionEnteteFichier);
        
        sectionEnteteStation = new FileSection(shell, NemoSection.F_STATIONHEADER, Messages.getString(NemoSection.F_STATIONHEADER), Messages.edit_file_enteteStation, Global.EMPTY_STRING, true);
        listeSectionsDesc.add(sectionEnteteStation);
        
        sectionFinStation = new FileSection(shell, NemoSection.F_ENDSTATION, Messages.getString(NemoSection.F_ENDSTATION), Messages.edit_file_finStation, Global.ZERO, false);
        listeSectionsDesc.add(sectionFinStation);
        
        sectionRuptureDonnees = new FileSection(shell, NemoSection.F_ENDDATA, Messages.getString(NemoSection.F_ENDDATA), Messages.edit_file_indicFinDonnees, Global.EMPTY_STRING, true);
        listeSectionsDesc.add(sectionRuptureDonnees);
    }

    public static void initBoutons() {
        buttonRAZ = new Button(shell, SWT.NONE);
        buttonExpand = new Button(shell, SWT.NONE);
        buttonValidate = new Button(shell, SWT.NONE);
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

    public static void razText() {

        for (NemoSection desc : listeSectionsDesc){
            desc.razValues();
        }
    }

    public static void nomNemo() {
        // nom de l'application
        if (ViewFile.dossierCampagne) {
            // dossier campagne

            File fich = new File(ViewFile.nomRepertoire);
            if (DataModel.ouverture) {
                ApplicationWorkbenchWindowAdvisor.configurer_titre
                        .setTitle(Messages.application_directoryTitle + fich.getName() + Messages.application_modelTitle + OpenAction.nomFichierModele + " ]"); //$NON-NLS-1$

            } else {
                ApplicationWorkbenchWindowAdvisor.configurer_titre
                        .setTitle(Messages.application_directoryTitle + fich.getName() + " ]"); //$NON-NLS-1$ 
            }

        } else {
            // fichier campagne
            File fich;
            fich = new File(ViewFile.nomFichier);

            if (DataModel.ouverture) {
                ApplicationWorkbenchWindowAdvisor.configurer_titre
                        .setTitle(Messages.application_fileTitle + fich.getName() + Messages.application_modelTitle + OpenAction.nomFichierModele + " ]"); //$NON-NLS-1$
            } else {
                ApplicationWorkbenchWindowAdvisor.configurer_titre
                        .setTitle(Messages.application_fileTitle + fich.getName() + " ]"); //$NON-NLS-1$ 
            }
        }

    }

    public static void disposeElements()
    {                     
        if (listeSectionsDesc != null){
            for (NemoSection section : listeSectionsDesc){
                section.dispose();
            }
        }
        if (buttonRAZ != null) buttonRAZ.dispose();
        if (buttonExpand != null) buttonExpand.dispose();
        if (buttonValidate != null) buttonValidate.dispose();
    }
    
    public static void actualiseEdition(){
        // FAE 1810 - FileHeader accessible que pour un fichier campagne
        sectionEnteteFichier.setEnabled(ViewFile.radioFichier.getSelection());
    }


    public static void invalidEtape(){
        okApresReset = false;
        validee = false;
        if (ViewFile.affiche != 0 && loaded){
            loaded = false;
            disposeElements();
        }
        ViewFile.tabItem.setText(Messages.onglet_file);
    }
    
}

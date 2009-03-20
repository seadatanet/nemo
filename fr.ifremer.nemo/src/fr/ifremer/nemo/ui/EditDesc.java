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
            formEdition.setText(Messages.getString("EditDesc.descriptiondeFichier")); //$NON-NLS-1$

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
            buttonValidate.setText(Messages.getString("EditDesc.validerLetape")); //$NON-NLS-1$
            buttonValidate.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e) {
                    
                    Etape.validationDesc();
                }
            });

            // bouton RAZ
            buttonRAZ.setParent(formEdition.getBody());
            buttonRAZ.setText("Reset"); //$NON-NLS-1$
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
            buttonExpand.setText("+"); //$NON-NLS-1$
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
                        buttonExpand.setText("-"); //$NON-NLS-1$
                    }else{
                        expand = false;
                        buttonExpand.setText("+"); //$NON-NLS-1$
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
        ViewEdit.formEdition.setText(Messages
                .getString("EditDesc.descriptiondeFichier")); //$NON-NLS-1$

        // refresh global
        ViewEdit.formEdition.reflow(true);

        // affichage courant
        ViewFile.affiche = 0;

    }

    public static void initSections() {
        sectionEnteteFichier = new FileSection(shell, NemoSection.F_FILEHEADER, Messages.getString(NemoSection.F_FILEHEADER), Messages.getString("EditDesc.surlignezLignes"),Global.ZERO); //$NON-NLS-1$
        listeSectionsDesc.add(sectionEnteteFichier);
        
        sectionEnteteStation = new FileSection(shell, NemoSection.F_STATIONHEADER, Messages.getString(NemoSection.F_STATIONHEADER), Messages.getString("EditDesc.nbreLignesEntete"),Global.ZERO); //$NON-NLS-1$
        listeSectionsDesc.add(sectionEnteteStation);
        
        sectionFinStation = new FileSection(shell, NemoSection.F_ENDSTATION, Messages.getString(NemoSection.F_ENDSTATION), Messages.getString("EditDesc.nbreLignesFinStation"),Global.ZERO); //$NON-NLS-1$
        listeSectionsDesc.add(sectionFinStation);
        
        sectionRuptureDonnees = new FileSection(shell, NemoSection.F_ENDDATA, Messages.getString(NemoSection.F_ENDDATA), Messages.getString("EditDesc.indiquezIndicFinDonnees"),Global.EMPTY_STRING); //$NON-NLS-1$
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
        ViewFile.tabItem.setText(Messages.getString("EditDesc.descriptionFichier")); //$NON-NLS-1$
    }
    
}

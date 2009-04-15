package fr.ifremer.nemo.ui;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class EditData {

	// fonctions de remplissage de la zone d'edition pour les campagnes

	// /////////////////////////////////////////

	public static Button buttonRAZ, buttonValidate;

	public static boolean validee, loaded;

	public static Boolean okApresReset;

	public static ScrolledForm formEdition;

	private static Shell shell = new Shell();
	// //////////////////////////////////////////

	public static void affichageEdition() {
		// initialisation zone edition pour etape description de fichier

		formEdition = ViewEdit.formEdition;
		ViewLog.aideData();
		
		okApresReset = true;
        loaded = true;
        ViewFile.affiche = 3;

        // raz du message d'erreurs
        Utils.messageErreur(Global.EMPTY_STRING);

		// affichage du texte dans la zone de gauche
		ViewFile.styledTextData.setText(Utils.texteBrut);


		/*
		 * recreer un formEdition sur le toolkit principal pour pouvoir faire un
		 * dispose() dessus mais garder toutes les sections afin de les
		 * reafficher par la suite dans DrawingBack.
		 */

		ViewEdit.formEdition.getBody().setLayout(new GridLayout());
		ViewEdit.formEdition.setText(Messages.getString("EditData.8")); //$NON-NLS-1$

        shell.close();
        shell = new Shell();
		disposeElements();
		
		// init composants IHM
		initBoutons();

		// bouton validate
		buttonValidate.setParent(ViewEdit.formEdition.getBody());
		buttonValidate.setText(Messages.getString("EditData.3")); //$NON-NLS-1$

		// reset
		buttonRAZ.setParent(ViewEdit.formEdition.getBody());
		buttonRAZ.setText("Reset"); //$NON-NLS-1$

		formEdition.reflow(true);
		ViewEdit.formEdition.reflow(true);

		buttonValidate
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
                    public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						Etape.validationData();

						formEdition.redraw();
						formEdition.reflow(true);

					}
				});

		buttonRAZ
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
                    public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) 
                    {
                        TableData.fillTypes();
                        Utils.invalidEtapes();
					}
				});

		// refresh du formulaire
		formEdition.reflow(true);
		
		// remplissage de l'IHM par le modele charge
		if (TableData.types != null && !TableData.types.isEmpty()){
            TableData.razTableData();
            TableData.creationTable();
        }else if (DataModel.ouverture) {
			DataModel.fillData();
		}else{
            TableData.fillTypes();
        }
	}

	public static void razEdition() {
		// reset de toute la zone d'edition apres etape descriptioon de fichier
		// on retire toutes les sections et les boutons expand et validation

		// effacement des boutons valider/expand/init serveur
		EditData.buttonValidate.setParent(shell);
		EditData.buttonRAZ.setParent(shell);

		// effacement titre formulaire
		ViewEdit.formEdition.setText(""); //$NON-NLS-1$

		// refresh global
		ViewEdit.formEdition.reflow(true);
	}

	public static void ajoutModifyListenerText(ArrayList<Text> liste) {
		int taille = liste.size();
		for (int i = 0; i < taille; i++) {
			liste.get(i).addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent event) {
					// on modifie la valeur du champ texte

					// des qu'on modifie une valeur, on invalide l'etape
					Utils.invalidEtapes();

				}
			});

		}
	}

	public static void DrawingBack() {
		// reset de toute la zone d'edition apres etape descriptioon de fichier
		// on retire toutes les sections et les boutons expand et validation

		ViewLog.aideData();

		ScrolledForm s;
		s = ViewEdit.formEdition;

        // raz du message d'erreurs
        Utils.messageErreur(Global.EMPTY_STRING);

		buttonValidate.setParent(s.getBody());
		buttonRAZ.setParent(s.getBody());

		ViewEdit.formEdition.setText(Messages.getString("EditData.20")); //$NON-NLS-1$

		// refresh global
		ViewEdit.formEdition.reflow(true);

		// affichage courant
		ViewFile.affiche = 3;

	}
    
	public static void initBoutons() {
		buttonValidate = new Button(shell, SWT.NONE);
		buttonRAZ = new Button(shell, SWT.NONE);
	}

	private static void disposeElements()
	{
        if (buttonRAZ != null) buttonRAZ.dispose();
        if (buttonValidate != null) buttonValidate.dispose();
	}


    public static void invalidEtape(){
        okApresReset = false;
        validee = false;
        if (ViewFile.affiche != 3 && loaded){
            loaded = false;
            disposeElements();
        }
        ViewFile.tabItem3.setText(Messages.onglet_data); 
    }
}

package fr.ifremer.nemo.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;

public class SaveXMLAction extends Action implements ISelectionListener,
		ActionFactory.IWorkbenchAction { // implements ISelectionListener,
											// ActionFactory.IWorkbenchAction
	private final IWorkbenchWindow window;

	public final static String ID = "fr.ifremer.nemo.ui.SaveXMLAction";

	// vars nécessaires à la fenêtre d'options
	// public static
	public static Shell shelloptions;

	public static String nomFichierSortieXML;

	public static File fichierSortieXML;

	// private IStructuredSelection selection;

	public SaveXMLAction(IWorkbenchWindow window) {

		this.window = window;
		setId(ID);
		setText("Enregistrer Description XML");
		window.getSelectionService().addSelectionListener(this);

	}

	public void dispose() {
		window.getSelectionService().addSelectionListener(this);

	}

	public void run() {
		// sauvegarde du modèle
		// System.out.println("Menu save");

		FileOutputStream fileOutputStream = null;

		if (EditSurvey.validee == 0) {
			Utils
					.messageErreur("Veuillez valider au moins jusqu'à l'étape 2 avant d'enregistrer votre Description XML !");
		} else {

			choixFichierSortie();

			try {
				fileOutputStream = new FileOutputStream(fichierSortieXML);
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			}
			DataModel.inputModel.getMedia().getModel().writeXMLOn(
					fileOutputStream);
			Utils
					.messageErreur("==== Fin d'enregistrement Description XML ====");
		}

	}

	public static void choixFichierSortie() {

		Shell shell = new Shell();
		FileDialog dialog = new FileDialog(shell, SWT.SAVE
				| SWT.APPLICATION_MODAL);
		dialog.setText("Entrez un nom pour le fichier de sortie");
		nomFichierSortieXML = dialog.open();

		fichierSortieXML = new File(nomFichierSortieXML);
		if (fichierSortieXML.exists()) {
			shell.close();

			Shell shell2 = new Shell();

			MessageBox mb = new MessageBox(shell2, SWT.ICON_WARNING | SWT.YES
					| SWT.NO | SWT.APPLICATION_MODAL);
			mb
					.setMessage("Le fichier de sortie spécifié existe déjà ! Voulez-vous l'écraser ?");

			int reponse = mb.open();
			if (reponse == SWT.NO) {
				choixFichierSortie();
			}

		}

	}

	public void selectionChanged(IWorkbenchPart part, ISelection incoming) {
	}

}

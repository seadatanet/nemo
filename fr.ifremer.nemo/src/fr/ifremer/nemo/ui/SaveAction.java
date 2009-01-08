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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

import fr.ifremer.nemo.util.Messages;

public class SaveAction extends Action implements ISelectionListener,
		ActionFactory.IWorkbenchAction {
	private final IWorkbenchWindow window;

	public final static String ID = "fr.ifremer.nemo.ui.SaveAction"; //$NON-NLS-1$

	// vars necessaires e la fenetre d'options

	public static Shell shelloptions;

	public static String nomFichierSortieXML;

	public static File fichierSortieXML;

	public SaveAction(IWorkbenchWindow window) {

		this.window = window;
		setId(ID);
		setText(Messages.getString("SaveAction.1")); //$NON-NLS-1$
		window.getSelectionService().addSelectionListener(this);

	}

	public void dispose() {
		window.getSelectionService().addSelectionListener(this);

	}

	public void run() {
		// sauvegarde du modele

		FileOutputStream fileOutputStream = null;

		if (EditData.validee == 0) {
			Utils.messageErreur(Messages.getString("SaveAction.3")); //$NON-NLS-1$
		} else {
			if (EditDesc.okApresReset && EditSurvey.okApresReset
					&& EditStation.okApresReset && EditData.okApresReset) {
				choixFichierSortie();
				try {
					fileOutputStream = new FileOutputStream(fichierSortieXML);
				} catch (FileNotFoundException e) {
					//
					e.printStackTrace();
				}

				DataModel.inputModel.writeXMLOn(fileOutputStream);
				Utils.messageOK(Messages.getString("SaveAction.4")); //$NON-NLS-1$

			} else {
				Utils.messageErreur(Messages.getString("SaveAction.3"));
			}
		}
	}

	public static void choixFichierSortie() {

		Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		FileDialog dialog = new FileDialog(shell, SWT.SAVE
				| SWT.APPLICATION_MODAL);
		dialog.setText(Messages.getString("SaveAction.5")); //$NON-NLS-1$
		dialog.setFilterPath(Utils.configuration.getModelsDirectory() + "\\");
		String extensions[] = new String[1];
		extensions[0] = "*.xml"; //$NON-NLS-1$
		dialog.setFilterExtensions(extensions);

		nomFichierSortieXML = dialog.open();

		if (nomFichierSortieXML != null) {
			fichierSortieXML = new File(nomFichierSortieXML);
			if (fichierSortieXML.exists()) {
	//			shell.close();

				Shell shell2 = PlatformUI.getWorkbench().getDisplay().getActiveShell();

				MessageBox mb = new MessageBox(shell2, SWT.ICON_WARNING
						| SWT.YES | SWT.NO | SWT.APPLICATION_MODAL);
				mb.setMessage(Messages.getString("SaveAction.6")); //$NON-NLS-1$

				int reponse = mb.open();
				if (reponse == SWT.NO) {
					choixFichierSortie();
				}

			}

		}

	}

	public void selectionChanged(IWorkbenchPart part, ISelection incoming) {
	}

}

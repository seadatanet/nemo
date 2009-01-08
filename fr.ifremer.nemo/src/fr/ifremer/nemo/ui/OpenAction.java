package fr.ifremer.nemo.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

import fr.ifremer.nemo.input.InputModel;
import fr.ifremer.nemo.util.Messages;

public class OpenAction extends Action implements ISelectionListener,
		ActionFactory.IWorkbenchAction {
	private final IWorkbenchWindow window;

	public final static String ID = "fr.ifremer.nemo.ui.OpenAction"; //$NON-NLS-1$

	// vars necessaires e la fenetre d'options

	public static Shell shelloptions;

	public static String nomModele, nomFichierModele;

	public OpenAction(IWorkbenchWindow window) {

		this.window = window;
		setId(ID);
		setText(Messages.getString("OpenAction.1")); //$NON-NLS-1$
		window.getSelectionService().addSelectionListener(this);

	}

	public void dispose() {
		window.getSelectionService().addSelectionListener(this);

	}

	public void run() {
		FileInputStream stream = null;
//		System.out.println("Menu open"); //$NON-NLS-1$

		// verifier que un fichier est charge avant d'ouvrir un modele
		if (EditDesc.loaded == 0) {
			Utils.messageErreur(Messages.getString("OpenAction.3")); //$NON-NLS-1$
		} else {

			InputModel inputModel = new InputModel();

			Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
			FileDialog dialog = new FileDialog(shell, SWT.OPEN
					| SWT.APPLICATION_MODAL);
			dialog.setFilterPath(Utils.configuration.getModelsDirectory()
					+ "\\"); //$NON-NLS-1$
			dialog.setText(Messages.getString("OpenAction.5")); //$NON-NLS-1$
			String extensions[] = new String[1];
			extensions[0] = "*.xml"; //$NON-NLS-1$
			dialog.setFilterExtensions(extensions);
			nomModele = dialog.open();

			if (nomModele == null) {
			} else {

				// ouverture du fichier XML de modele

				com.ago.xml.XMLBootstrapParser parser = new com.ago.xml.XMLBootstrapParser(
						inputModel);

				try {
					stream = new java.io.FileInputStream(nomModele);
				} catch (FileNotFoundException e) {
					// 
					e.printStackTrace();
				}
//				System.out.println("Ouverture du modele : " + nomModele); //$NON-NLS-1$
				parser.parse(stream, false);

				File fich = new File(nomModele);
				nomFichierModele = fich.getName();

				DataModel.ouvertureModele(inputModel);
				DataModel.ouverture = true;

				ViewLog.styledTextLogInfos.append(Messages
						.getString("OpenAction.8") + nomModele); //$NON-NLS-1$

			}

		}

	}

	public void selectionChanged(IWorkbenchPart part, ISelection incoming) {
	}

}

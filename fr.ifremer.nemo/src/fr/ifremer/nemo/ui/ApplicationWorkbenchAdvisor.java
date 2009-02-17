package fr.ifremer.nemo.ui;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import fr.ifremer.nemo.util.Messages;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "fr.ifremer.nemo.ui.perspective"; //$NON-NLS-1$

	@Override
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	@Override
    public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}

	@Override
	public boolean preShutdown() {

	
		FileOutputStream fileOutputStream = null;
		int reponse = 0;

		MessageBox mb = new MessageBox(PlatformUI.getWorkbench().getDisplay().getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO
				| SWT.APPLICATION_MODAL);
		mb.setMessage(Messages.getString("ExitAction.2")); //$NON-NLS-1$
		reponse = mb.open();

		if (reponse == SWT.YES) {
			// demande pour sauver le modele
			if (EditData.validee) {
				if (EditDesc.okApresReset && EditSurvey.okApresReset
						&& EditStation.okApresReset && EditData.okApresReset) {

					Shell shell2 = PlatformUI.getWorkbench().getDisplay().getActiveShell();

					MessageBox mbox = new MessageBox(shell2, SWT.ICON_WARNING
							| SWT.YES | SWT.NO | SWT.APPLICATION_MODAL);
					mbox.setMessage(Messages
							.getString("ApplicationWorkbenchAdvisor.1")); //$NON-NLS-1$
					int reponse_mbox = mbox.open();
					if (reponse_mbox == SWT.YES) {

						SaveAction.choixFichierSortie();

						if (SaveAction.fichierSortieXML != null) {
							try {
								fileOutputStream = new FileOutputStream(
										SaveAction.fichierSortieXML);
							} catch (FileNotFoundException e) {
								//
								e.printStackTrace();
							}

							if (fileOutputStream != null) {
								DataModel.inputModel
										.writeXMLOn(fileOutputStream);
								Utils.messageOK(Messages
										.getString("SaveAction.4")); //$NON-NLS-1$
							}
						}
					}

				}

			}

			return true;
		}
		return false;

	}

}

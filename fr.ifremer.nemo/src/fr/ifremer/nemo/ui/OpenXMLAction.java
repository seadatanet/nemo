package fr.ifremer.nemo.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;

import fr.ifremer.nemo.util.Messages;

public class OpenXMLAction extends Action implements ISelectionListener,
		ActionFactory.IWorkbenchAction {
	private final IWorkbenchWindow window;

	public final static String ID = "fr.ifremer.nemo.ui.OpenXMLAction"; //$NON-NLS-1$

	// vars necessaires e la fenetre d'options

	public static Shell shelloptions;

	public static String nomXML, nomFichierXML;

	public OpenXMLAction(IWorkbenchWindow window) {

		this.window = window;
		setId(ID);
		setText(Messages.action_openSurveyDesc);
		window.getSelectionService().addSelectionListener(this);

	}

	public void dispose() {
		window.getSelectionService().addSelectionListener(this);

	}

	public void run() {

		EditSurvey.ouvertureDescriptionXML();

	}

	public void selectionChanged(IWorkbenchPart part, ISelection incoming) {
	}

}

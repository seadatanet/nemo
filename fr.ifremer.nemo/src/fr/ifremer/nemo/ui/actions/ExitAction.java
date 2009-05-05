package fr.ifremer.nemo.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

import fr.ifremer.nemo.ui.ApplicationActionBarAdvisor;
import fr.ifremer.nemo.util.Messages;

public class ExitAction extends Action implements ISelectionListener,
		ActionFactory.IWorkbenchAction { // implements ISelectionListener,
	// ActionFactory.IWorkbenchAction
	private final IWorkbenchWindow window;

	public final static String ID = "fr.ifremer.nemo.ui.ExitAction"; //$NON-NLS-1$

	// vars necessaires e la fenetre d'options
	// public static
	public static Shell shelloptions;

	public static String nomModele, nomFichierModele;

	// private IStructuredSelection selection;

	public ExitAction(IWorkbenchWindow window) {

		this.window = window;
		setId(ID);
		setText(Messages.menu_quit); 
		window.getSelectionService().addSelectionListener(this);

	}

	public void dispose() {
		window.getSelectionService().addSelectionListener(this);

	}

	public void run() {

		Shell s = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		MessageBox mb = new MessageBox(s, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		mb.setMessage(Messages.confirm_quit);
		ApplicationActionBarAdvisor.exitActionQuit.run();
	}

	public void selectionChanged(IWorkbenchPart part, ISelection incoming) {
	}

}

package fr.ifremer.nemo.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;

import fr.ifremer.nemo.util.Messages;

public class BatchBuilderAction extends Action implements ISelectionListener, ActionFactory.IWorkbenchAction {

	private final IWorkbenchWindow window;

	private final static String ID = "fr.ifremer.nemo.ui.BatchHelperAction"; //$NON-NLS-1$


	public BatchBuilderAction(IWorkbenchWindow window) {
		this.window = window;
		setId(ID);
		setText(Messages.batchBuilder_action);
		window.getSelectionService().addSelectionListener(this);
	}

	@Override
	public void dispose() {
		window.getSelectionService().addSelectionListener(this);
	}

	@Override
	public void run() {	
		BatchBuilderDialog batchHelperDialog = new BatchBuilderDialog(Display.getCurrent().getActiveShell());
		batchHelperDialog.init();
		batchHelperDialog.open();

	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// TODO Auto-generated method stub
		
	}
	
}

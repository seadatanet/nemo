package fr.ifremer.nemo.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;

public class ExitAction extends Action implements ISelectionListener, ActionFactory.IWorkbenchAction { //implements ISelectionListener, ActionFactory.IWorkbenchAction 
private final IWorkbenchWindow window;

public final static String ID = "fr.ifremer.nemo.ui.ExitAction"; //$NON-NLS-1$

//vars nécessaires à la fenêtre d'options
//public static 
public static Shell shelloptions;
public static String nomModele, nomFichierModele;

//private IStructuredSelection selection;

public ExitAction(IWorkbenchWindow window){
	
this.window = window;
setId(ID);
setText(Messages.getString("ExitAction.1")); //$NON-NLS-1$
window.getSelectionService().addSelectionListener(this);


}

public void dispose(){
	window.getSelectionService().addSelectionListener(this);
	
}



public void run(){

	Shell s = new Shell();
	int reponse = 0;
	
	MessageBox mb = new MessageBox(s, SWT.ICON_QUESTION |SWT.YES | SWT.NO);
	mb.setMessage(Messages.getString("ExitAction.2")); //$NON-NLS-1$
	//reponse = mb.open();
	
	//if (reponse == SWT.YES){
		ApplicationActionBarAdvisor.exitActionQuit.run();
		//Application.display.dispose();
		
//	}
	
}

public void selectionChanged(IWorkbenchPart part, ISelection incoming){}

}

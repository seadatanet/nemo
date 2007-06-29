package fr.ifremer.nemo.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "fr.ifremer.nemo.ui.perspective";

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}

	@Override
	public boolean preShutdown() {
		
		Shell s = new Shell();
		int reponse = 0;
		
		MessageBox mb = new MessageBox(s, SWT.ICON_QUESTION |SWT.YES | SWT.NO |SWT.APPLICATION_MODAL );
		mb.setMessage(Messages.getString("ExitAction.2")); //$NON-NLS-1$
		reponse = mb.open();
		
		if (reponse == SWT.YES){
			return true;
			
			
		}
		return false;
	}

	
	
	
	
	
	
}

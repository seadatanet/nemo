package fr.ifremer.nemo.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class SWTProgressWS extends ApplicationWindow {

	public SWTProgressWS() {
		super(null);
	}

	public void run(){

		final Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();

		try {
			// FAE 0001132 : enable the cancelation		
			// FIXME DBR : rendre fonctionnel ce bouton cancel !!
			new ProgressMonitorDialog(shell).run(true, true,
					new SWTProcessWS());
			
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}

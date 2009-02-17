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

	public boolean run(){

		final Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
        boolean interrompu = false;

		try {
			// FAE 0001132 : enable the cancelation		
            new ProgressMonitorDialog(shell).run(true, true, new SWTProcessWS());
			
		} catch (InvocationTargetException e) {
			e.printStackTrace();
            interrompu = true;
		} catch (InterruptedException e) {
            interrompu = true;
		}
        
        return interrompu;
	}

}

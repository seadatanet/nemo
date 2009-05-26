package fr.ifremer.nemo.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.window.ApplicationWindow;

import fr.ifremer.nemo.output.ProgressNotification;
import fr.ifremer.nemo.ui.view.ViewFile;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Messages;

public class SWTProgress extends ApplicationWindow {

	public static ProgressNotification notifier;

	public SWTProgress() {
		super(null);
	}

	public void run() throws InterruptedException
    {
        Conversion formatConvert = Conversion.MEDATLAS;
        if (ViewFile.radODV.getSelection()){
            formatConvert = Conversion.ODV;
        }
        SWTProcess.formatConversion = formatConvert;
        SWTProcess.monoStation = ViewFile.radMonoStation.getSelection();

		try {
			new MyProgressMonitorDialog(Application.display.getActiveShell(),
                                        Messages.update_shellTitle, 
                                        Messages.button_cancel)
					.run(true, true, new SWTProcess());
		} 
        catch (InvocationTargetException e) {

		} 
        catch (InterruptedException e) {
            System.out.println("Interrompu !\n------------"); //$NON-NLS-1$
			throw e;
		}

	}

}

package fr.ifremer.nemo.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.window.ApplicationWindow;

import fr.ifremer.nemo.output.ProgressNotification;
import fr.ifremer.nemo.util.Conversion;

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
			new ProgressMonitorDialog(Application.display.getActiveShell())
					.run(true, true, new SWTProcess());
		} 
        catch (InvocationTargetException e) {

		} 
        catch (InterruptedException e) {
            System.out.println("Interrompu !\n------------");
			throw e;
		}

	}

}

package fr.ifremer.nemo.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.widgets.Shell;

import fr.ifremer.nemo.util.Messages;

public class SWTProgressWS extends ApplicationWindow
{

    public SWTProgressWS()
    {
        super(null);
    }

    public String run()
    {

        final Shell shell = Utils.getMainShell();
        boolean interrompu = false;
        String error="";
        try
        {
            // FAE 0001132 : enable the cancelation
            new MyProgressMonitorDialog(shell, Messages.update_shellTitle,
                Messages.button_cancel).run(true, true, new SWTProcessWS());

        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
            error=e.getMessage();
        }
        catch (InterruptedException e)
        {
            interrompu = true;
            error=e.getMessage();
        }

        return error;
    }

}

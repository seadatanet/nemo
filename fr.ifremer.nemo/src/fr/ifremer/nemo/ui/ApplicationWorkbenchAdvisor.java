package fr.ifremer.nemo.ui;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import fr.ifremer.nemo.ui.actions.SaveAction;
import fr.ifremer.nemo.ui.edit.DataModel;
import fr.ifremer.nemo.ui.view.ViewMain;
import fr.ifremer.nemo.util.Messages;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor
{

    private static final String PERSPECTIVE_ID =
        "fr.ifremer.nemo.ui.perspective"; //$NON-NLS-1$

    @Override
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
            IWorkbenchWindowConfigurer configurer)
    {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

    @Override
    public String getInitialWindowPerspectiveId()
    {
        return PERSPECTIVE_ID;
    }

    @Override
    public boolean preShutdown()
    {

        FileOutputStream fileOutputStream = null;
        int reponse = 0;

        MessageBox mb =
            new MessageBox(PlatformUI.getWorkbench().getDisplay()
                .getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO
                    | SWT.APPLICATION_MODAL);
        mb.setMessage(Messages.confirm_quit);
        reponse = mb.open();

        if (reponse == SWT.YES)
        {
            // demande pour sauver le modele
            if (ViewMain.editDataForm.validee)
            {
                if (ViewMain.editFileForm.okApresReset
                        && ViewMain.editCruiseForm.okApresReset
                        && ViewMain.editStationForm.okApresReset
                        && ViewMain.editDataForm.okApresReset)
                {

                    Shell shell2 =
                        PlatformUI.getWorkbench().getDisplay().getActiveShell();

                    MessageBox mbox =
                        new MessageBox(shell2, SWT.ICON_WARNING | SWT.YES
                                | SWT.NO | SWT.APPLICATION_MODAL);
                    mbox.setMessage(Messages.confirm_saveModel);
                    int reponse_mbox = mbox.open();
                    if (reponse_mbox == SWT.YES)
                    {

                        SaveAction.choixFichierSortie();

                        if (SaveAction.fichierSortieXML != null)
                        {
                            try
                            {
                                fileOutputStream =
                                    new FileOutputStream(
                                        SaveAction.fichierSortieXML);
                            }
                            catch (FileNotFoundException e)
                            {
                                //
                                e.printStackTrace();
                            }

                            if (fileOutputStream != null)
                            {
                                DataModel.inputModel
                                    .writeXMLOn(fileOutputStream);
                                Utils.messageOK(Messages.SaveAction_4);
                            }
                        }
                    }

                }

            }

            return true;
        }
        return false;

    }

}

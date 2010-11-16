package fr.ifremer.nemo.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.actions.SaveAction;
import fr.ifremer.nemo.ui.edit.cruise.EditCruiseForm;
import fr.ifremer.nemo.ui.edit.data.EditDataForm;
import fr.ifremer.nemo.ui.edit.file.EditFileForm;
import fr.ifremer.nemo.ui.edit.station.EditStationForm;
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
            new MessageBox(Utils.getMainShell(), SWT.ICON_QUESTION | SWT.YES
                | SWT.NO | SWT.APPLICATION_MODAL);
        mb.setMessage(Messages.confirm_quit);
        reponse = mb.open();

        if (reponse == SWT.YES)
        {
            EditFileForm editFileForm = Nemo.getInstance().getViewMain().getEditFileForm();
            EditCruiseForm editCruiseForm = Nemo.getInstance().getViewMain().getEditCruiseForm();
            EditStationForm editStationForm = Nemo.getInstance().getViewMain().getEditStationForm();
            EditDataForm editDataForm = Nemo.getInstance().getViewMain().getEditDataForm();
            
            // demande pour sauver le modele
            if (editDataForm.validee )
            {
                if (editFileForm.isOkApresReset()
                        && editCruiseForm.isOkApresReset()
                        && editStationForm.isOkApresReset()
                        && editDataForm.isOkApresReset())
                {
                    //4063
                    File tmpBack =null;
                    boolean identical=true;
                    if (Nemo.getInstance().getCurrentOpenModelPath()==null){
                        identical=false;
                    }else{
                    // on le compare avec le fichier du modele courant si un fichier modele a été ouvert
                    File currentModel =new File(Nemo.getInstance().getCurrentOpenModelPath());
                    // on sauve le modele temporairement
                    try
                    {
                        tmpBack =new File("nemoBackupTmp.xml");
                        fileOutputStream =
                            new FileOutputStream(tmpBack);
                    }
                    catch (FileNotFoundException e)
                    {
                        //
                        e.printStackTrace();
                    }

                    if (fileOutputStream != null)
                    {
                        Nemo.getInstance().saveModel(fileOutputStream);
                    }


                    try
                    {
                        BufferedReader bufNew = new BufferedReader(new FileReader(tmpBack));
                        BufferedReader bufOld = new BufferedReader(new FileReader(currentModel));

                        int cpt=0;

                        while( bufNew.ready() && identical)
                        {
                            String newModel = bufNew.readLine();
                            String oldModel = bufOld.readLine();
                            if (cpt>2){

                                identical= newModel.equals(oldModel);
                            }
                            cpt++;
                        }

                        bufNew.close();
                        bufOld.close();
                        tmpBack.delete();
                    }
                    catch (FileNotFoundException e1)
                    {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    catch (IOException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    }
                    if(!identical)
                    {
                        //***********************
                        Shell shell2 = Utils.getMainShell();

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
                                    Nemo.getInstance().saveModel(fileOutputStream);
                                    Utils.messageOK(Messages.SaveAction_4);
                                }
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void initialize(IWorkbenchConfigurer configurer)
    {
        configurer.setSaveAndRestore(false);
    }

}
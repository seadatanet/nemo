package fr.ifremer.nemo.ui.actions;

import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;

import fr.ifremer.nemo.config.ManageLists;
import fr.ifremer.nemo.config.UpdateStatus;
import fr.ifremer.nemo.config.UpdateStatus.EtatUpdate;
import fr.ifremer.nemo.config.UpdateStatusList;
import fr.ifremer.nemo.ui.SWTProcessWS;
import fr.ifremer.nemo.ui.SWTProgressWS;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class UpdateAction extends Action implements ISelectionListener,
        ActionFactory.IWorkbenchAction
{
    private final IWorkbenchWindow window;

    public final static String ID = "fr.ifremer.nemo.ui.UpdateAction"; //$NON-NLS-1$

//    private static boolean interrompu = false;

    // vars necessaires e la fenetre d'options

    public static Shell shelloptions = new Shell();

    public static int options;

    public UpdateAction(IWorkbenchWindow window)
    {

        this.window = window;
        setId(ID);
        setText(Messages.update_barTitle);
        window.getSelectionService().addSelectionListener(this);
        shelloptions.setFont(Utils.font08);

    }

    @Override
	public void dispose()
    {
        window.getSelectionService().addSelectionListener(this);

    }

    /**
     * launch vocabulary update and show result with an information
     * MessageDialog
     */
    @Override
    public void run()
    {
        SWTProcessWS.fichiersConstantes = true;
        StringBuffer message = new StringBuffer();

       String interrompuError = new SWTProgressWS().run();

        if (!interrompuError.equals(Global.EMPTY_STRING))
        {
            message.append(Messages.update_info);
            message.append(Global.RN);
            message.append(interrompuError);
            message.append(Global.RN);
            message.append(Global.RN);
        }

        // affichage du resultats des mises a jour
        ArrayList<UpdateStatus> updatedLists = ManageLists.getUpdatedLists();
        for (UpdateStatus list : updatedLists)
        {
        	//si valide
            if (list.getEtat().compareTo(EtatUpdate.LOCAL) > 0)
            {
            	message.append(getMessageUpdate(list, false));
            	//si  UpdateStatus est en faite une liste
            	if(list instanceof UpdateStatusList)
            	{
	                ArrayList<UpdateStatus> subUpdatedLists = ((UpdateStatusList) list).getUpdateList();
	                for (UpdateStatus sublist : subUpdatedLists)
	                {
	                    message.append(getMessageUpdate(sublist, true));
	                }
            	}
            }
        }

        MessageDialog.openInformation(shelloptions, "WebServices Info", message.toString()); //$NON-NLS-1$
    }

    private String getMessageUpdate(UpdateStatus list, boolean sublist)
    {
        StringBuffer message = new StringBuffer();
        if (!sublist)
        {
            message.append(Global.RN);
            message.append(padRight(list.getName(), 35, '.'));
        }
        else
        {
            message.append(Global.SPACE);
            message.append(Global.SPACE);
            message.append(Global.SPACE);
            message.append(Global.SPACE);
            message.append(padRight(list.getName(), 33, ' '));
            if (list.getName().trim().length() < 15)
                message.append(Global.TAB);
        }

        message.append(Global.TAB);
        message.append(Global.SPACE);
        message.append(Global.SPACE);
        
        message.append(list.getStatusString());
        
        message.append(Global.RN);
        return message.toString();
    }

    @Override
	public void selectionChanged(IWorkbenchPart part, ISelection incoming)
    {
        // implement ISelectionListener...
    }

    /**
     * transforme la chaine d'entrée 
     * pour n'affichier qu un certain nombre de caractere
     * defini par max lenght
     * @param input string
     * @param maxlength max length use
     * @param pad
     * @return
     */
    private String padRight(String input, int maxlength, char pad)
    {
        StringBuffer output = new StringBuffer();
        if (input.length() > maxlength)
        {
            output.append(input.substring(0, maxlength));
            output.append('.');
            output.append('.');
            output.append('.');
        }
        else
        {
            output.append(input.trim());
            for (int i = input.trim().length(); i < maxlength; i++)
            {
                output.append(pad);
            }
            output.append(' ');
            output.append(' ');
            output.append(' ');
        }
        return output.toString();
    }
}

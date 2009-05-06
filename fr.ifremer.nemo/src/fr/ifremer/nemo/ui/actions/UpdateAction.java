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

import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.config.UpdateBean;
import fr.ifremer.nemo.config.UpdateBean.EtatUpdate;
import fr.ifremer.nemo.ui.SWTProcessWS;
import fr.ifremer.nemo.ui.SWTProgressWS;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;

public class UpdateAction extends Action implements ISelectionListener,
        ActionFactory.IWorkbenchAction
{
    private final IWorkbenchWindow window;

    public final static String ID = "fr.ifremer.nemo.ui.UpdateAction"; //$NON-NLS-1$
    
    private static boolean interrompu = false;

    // vars necessaires e la fenetre d'options

    public static Shell shelloptions = new Shell();;

    public static int options;

    public UpdateAction(IWorkbenchWindow window)
    {

        this.window = window;
        setId(ID);
        setText(Messages.update_barTitle); 
        window.getSelectionService().addSelectionListener(this);

    }

    public void dispose()
    {
        window.getSelectionService().addSelectionListener(this);

    }
    

    /** 
     * launch vocabulary update and show result with an information MessageDialog
     */
    @Override
    public void run()
    {
        SWTProcessWS.fichiersConstantes = true;
        StringBuffer message = new StringBuffer();
                  
        interrompu = new SWTProgressWS().run();
        

        if (interrompu){
            message.append(Messages.update_info);
            message.append(Global.RN);
            message.append(Global.RN);
        }
        
        // affichage du resultats des mises a jour
        ArrayList<UpdateBean> updatedLists = NemoLists.getUpdatedLists();
        for (UpdateBean list : updatedLists){
            if (list.getEtat().compareTo(EtatUpdate.LOCAL) > 0){
                message.append(getMessageUpdate(list, false));
                ArrayList<UpdateBean> subUpdatedLists = list.getSubUpdates();
                for(UpdateBean sublist : subUpdatedLists){
                    message.append(getMessageUpdate(sublist, true));
                }
            }
        }
        
        MessageDialog.openInformation(shelloptions, "WebServices Info", message.toString());  //$NON-NLS-1$
    }
    
    private String getMessageUpdate(UpdateBean list, boolean sublist)
    {
        StringBuffer message = new StringBuffer();
        if (!sublist){
            message.append(Global.RN);
            message.append(padRight(list.getName(), 35, '.'));
        }
        else{
            message.append(Global.TAB);
            message.append(padRight(list.getName(), 33, ' '));
            if (list.getName().trim().length() < 15)  message.append(Global.TAB);
        }
        
        message.append(Global.TAB);
        message.append(Global.SPACE);
        message.append(Global.SPACE);
        EtatUpdate etat = list.getEtat();
        message.append(Messages.getString(etat.toString()));
        switch (etat){
            case UPDATED :
                message.append(Global.SEP);
                message.append(list.getNbRetrieve());
                message.append(Global.SPACE);
                message.append(Messages.retrieved);
                break;
            case INTERRUPTED :
                if (!sublist && !list.getId().equals(Intern.id_nemo) && !list.getId().equals(Intern.id_edmo)){
                    message.append(Global.SEP);
                    message.append(list.getNbRetrieve());
                    message.append(Global.SPACE);
                    message.append(Messages.retrieved);
                    message.append(" / "); //$NON-NLS-1$
                    message.append(list.getNbTot());
                }
                break;
            default :
                break;
        }
        message.append(Global.RN);
        return message.toString();
    }

    public void selectionChanged(IWorkbenchPart part, ISelection incoming)
    {
        // implement ISelectionListener...
    }
    
    private String padRight(String input, int maxlength, char pad){
        StringBuffer output = new StringBuffer();
        if (input.length() > maxlength) {
            output.append(input.substring(0, maxlength));
            output.append('.');
            output.append('.');
            output.append('.');
        }else{
            output.append(input.trim());
            for (int i = input.trim().length(); i < maxlength; i++){
                output.append(pad);
            }
            output.append(' ');
            output.append(' ');
            output.append(' ');
        }
        return output.toString();
    }
}

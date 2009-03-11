package fr.ifremer.nemo.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;

import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class UpdateAction extends Action implements ISelectionListener,
        ActionFactory.IWorkbenchAction
{
    private final IWorkbenchWindow window;

    public final static String ID = "fr.ifremer.nemo.ui.UpdateAction"; //$NON-NLS-1$
    
    private static int nbListesOK = -1;

    // vars necessaires e la fenetre d'options

    public static Shell shelloptions = new Shell();;

    public static int options;

    public UpdateAction(IWorkbenchWindow window)
    {

        this.window = window;
        setId(ID);
        setText(Messages.getString("UpdateAction.1")); //$NON-NLS-1$
        window.getSelectionService().addSelectionListener(this);

    }

    public void dispose()
    {
        window.getSelectionService().addSelectionListener(this);

    }

    @Override
    public void run()
    {
        // System.out.println("Menu update WebServices"); //$NON-NLS-1$
        //EditSurvey.webServiceUpdate();
        SWTProcessWS.fichiersConstantes = true;

        StringBuffer message = new StringBuffer();
        
        String error = new SWTProgressWS().run();
        try{
            nbListesOK = Integer.parseInt(error);
        }catch (Exception e){
            // l'erreur de viens pas d'un cancel utilisateur
        }
        

        if (nbListesOK != -1){
            message.append(Messages.getString("SWTProcessWS.2")); //$NON-NLS-1$
            message.append(Global.RN);
            message.append(Global.RN);
        }
        
        // affichage du resultat des mises à jour des fichiers
        message.append(getMessageUpdate(0, Messages.getString("SWTProcessWS.3"), NemoLists.okDataTypes, NemoLists.nbDataTypes, NemoLists.getDataTypes().length)); //$NON-NLS-1$
        message.append(getMessageUpdate(1, Messages.getString("SWTProcessWS.7"), NemoLists.okParameters, NemoLists.nbParameters, NemoLists.getParameters().length)); //$NON-NLS-1$
        message.append(getMessageUpdate(2, Messages.getString("SWTProcessWS.8"), NemoLists.okSurveys, NemoLists.nbSurveys, NemoLists.getSurveys().length)); //$NON-NLS-1$
        message.append(getMessageUpdate(3, Messages.getString("SWTProcessWS.9"), NemoLists.okVocabLists, NemoLists.nbVocabLists, NemoLists.getVocabList().size())); //$NON-NLS-1$
        message.append(getMessageUpdate(4, Messages.getString("SWTProcessWS.11"), NemoLists.okVocabMaps, NemoLists.nbVocabMaps, NemoLists.getVocabMap().size())); //$NON-NLS-1$
        message.append(getMessageUpdate(5, Messages.getString("SWTProcessWS.10"), NemoLists.okEdmo, NemoLists.nbEdmo, NemoLists.getEdmo().size())); //$NON-NLS-1$
        
        MessageDialog.openInformation(shelloptions, "WebServices Info", message.toString()); //$NON-NLS-1$

    }

    public void selectionChanged(IWorkbenchPart part, ISelection incoming)
    {
        // implement ISelectionListener...
    }
    
    private String getMessageUpdate(int index, String listeUpdated, boolean ok, int nb, int nbTot){
        StringBuffer message = new StringBuffer();
        // n'alimenter le message que si 
        // - aucune interruption
        // - interruption mais nbListesOK > index
        if ((nbListesOK == -1) || (nbListesOK > index)){
            message.append(padRightPoint(listeUpdated));
            message.append(Global.SPACE);
            message.append(Global.SPACE);
            if (ok){
                message.append("OK - "); //$NON-NLS-1$
                message.append(nb);
                message.append(Global.SPACE);
                message.append(Messages.getString("SWTProcessWS.12")); //$NON-NLS-1$
            }else{
                if ((nbListesOK != -1) && nb > 0){
                    message.append(Messages.getString("SWTProcessWS.14")); //$NON-NLS-1$
                    message.append(nb);
                    message.append(Global.SPACE);
                    message.append(Messages.getString("SWTProcessWS.12")); //$NON-NLS-1$
                    message.append(" / "); //$NON-NLS-1$
                    message.append(nbTot);
                }else{
                    message.append(Messages.getString("SWTProcessWS.13")); //$NON-NLS-1$
                }
            }
            message.append(Global.RN);
            message.append(Global.RN);
        }
        return message.toString();
    }
    
    private String padRightPoint(String input){
        StringBuffer output = new StringBuffer();
        output.append(input.trim());
        // longueur fixe à 28 caractères
        int maxlength = 28;
        for (int i = input.trim().length(); i < maxlength; i++){
            output.append('.');
        }
        return output.toString();
    }
}

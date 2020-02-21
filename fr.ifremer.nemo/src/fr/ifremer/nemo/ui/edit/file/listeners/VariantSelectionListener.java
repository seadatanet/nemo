package fr.ifremer.nemo.ui.edit.file.listeners;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import fr.ifremer.nemo.ui.edit.file.EditFileForm;

/**
 * Description: listener sur la selection du type de variante (pour fichiers d'entree en ODV)
 * @author sbregent
 *
 */
public class VariantSelectionListener  implements SelectionListener
{
    // reference sur l'editeur
    protected final EditFileForm myEditFileForm;
    
    public VariantSelectionListener(   EditFileForm editFileForm)
    {
        myEditFileForm = editFileForm;
    }
    
    @Override
	public void widgetDefaultSelected( SelectionEvent g)
    {
        widgetSelected(g);
    }
    
    @Override
	public void widgetSelected( SelectionEvent e)
    {
    }
    
}

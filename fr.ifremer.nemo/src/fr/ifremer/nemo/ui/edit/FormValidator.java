/* ============================================================================
 * Nom du fichier	: ValidForm.java
 * Auteur			: sbregent
 * Date				: 20 janv. 11
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: validateur d'onglet - voir les classes surcharge pour chaque onglet
 * @author sbregent
 *
 */
public class FormValidator
{
	protected static Logger guiLogger;
    public FormValidator()
    {
    	guiLogger = LogManager.getLogger("nemoGui");
    }
    
    public void validate()
    {
        
    }
    protected void onSuccess()
    {
        Utils.messageOKAppend(Messages.success_etape);
    }
    
}

/* ============================================================================
 * Nom du fichier	: ValidForm.java
 * Auteur			: sbregent
 * Date				: 20 janv. 11
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit;

import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: validateur d'onglet - voir les classes surcharge pour chaque onglet
 * @author sbregent
 *
 */
public class FormValidator
{

    public FormValidator()
    {
        
    }
    
    public void validate()
    {
        
    }
    protected void onSuccess()
    {
        Utils.messageOK(Messages.success_etape);
    }
    
}

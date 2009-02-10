/* ============================================================================
 * Nom du fichier	: UIobject.java
 * Auteur			: dboulang
 * Date				: 23 janv. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.object;

import org.eclipse.swt.widgets.Composite;

/**
 * Description: interface définissant ce qui spécifie un objet graphique NEMO
 * @author dboulang
 *
 */
public interface UIobject
{
    
    public abstract void draw(Composite compo);
    
    public abstract void dispose();
    
    public abstract void razValues();
    
    public abstract void setEnabled(boolean enable);
}

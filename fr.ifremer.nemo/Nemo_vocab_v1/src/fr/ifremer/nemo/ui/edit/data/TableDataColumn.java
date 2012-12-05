/*
 * ============================================================================
 * Nom du fichier : TableDataColumn.java
 * Auteur : sbregent
 * Date : 21 avr. 2011
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.data;

/**
 * Description: Classe permettant de décrire une colonne du tableau des
 * parametres
 * 
 * @author sbregent
 * 
 */
public class TableDataColumn
{
	public final String id;
	public final int pos; // position
	public String label;
	public int width;
	public boolean writable;
	private final int normalWidth;
	private boolean isMandatory;

	public TableDataColumn(String id, int pos, String label, int width, boolean writable, boolean mandatory)
	{
		this.id = id;
		this.pos = pos;
		this.label = label;
		this.width = width;
		normalWidth = width;
		this.writable = writable;
		isMandatory= mandatory;
		if (label!=null){
            if(isMandatory && !label.startsWith("* "))
                this.label="* "+ label;
            
            if(!isMandatory && label.startsWith("* "))
                this.label= label.substring(2, label.length()-1);
        }
	}

	public void enable(boolean enable)
	{
		setWritable(enable);
		setWidth(enable);
	}

	public void setWritable(boolean writable)
	{
		this.writable = writable;
	}

	public void setWidth(boolean enable)
	{
		if (enable)
		{
			this.width = normalWidth;
		} else
		{
			this.width = 0;
		}
	}
	
	public boolean isMandatory()
	{
	    return isMandatory;
	}
	
	public void setMandatory(boolean mandatory)
	{
	    isMandatory=mandatory;
	    if (label!=null){
	        if(isMandatory && !label.startsWith("* "))
	            label="* "+ label;
	        
	        if(!isMandatory && label.startsWith("* "))
	            label= label.substring(2, label.length()-1);
	    }
	}
}

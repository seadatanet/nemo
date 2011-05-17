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
	public final String label;
	public int width;
	public boolean writable;
	private final int normalWidth;

	public TableDataColumn(String id, int pos, String label, int width, boolean writable)
	{
		this.id = id;
		this.pos = pos;
		this.label = label;
		this.width = width;
		normalWidth = width;
		this.writable = writable;
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
}

package fr.ifremer.nemo.ui.edit.data;

import sdn.vocabulary.interfaces.IElement;

public class TypeStandardName extends Object {

    
    private String term, unitKey;

	public TypeStandardName(String term, String termAbbr, String untiDef, String unitKey) {
        this.setTerm(term);
        // FAE 6542
        this.setUnitKey(unitKey);
	}

    /**
     * @param record
     */
    public TypeStandardName(IElement record)
    {
        this.unitKey = record.getKey();
        this.term = record.getLabel();
    }
    public String getTerm()
    {
        return term;
    }

    public void setTerm(String term)
    {
        this.term = term;
    }

// FAE 6542
    public String getUnitKey()
    {
        return unitKey;
    }
    public void setUnitKey(String unitKey)
    {
        this.unitKey = unitKey;
    }
    
}

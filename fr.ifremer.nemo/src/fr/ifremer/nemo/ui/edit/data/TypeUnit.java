package fr.ifremer.nemo.ui.edit.data;

import sdn.vocabulary.interfaces.IElement;

public class TypeUnit extends Object {

    
    private String term, untiDef, unitKey, termAbbr;

	public TypeUnit(String term, String termAbbr, String untiDef, String unitKey) {
        this.setTerm(term);
        this.setTermAbbr(termAbbr);
        this.setUntiDef(untiDef);
        // FAE 6542
        this.setUnitKey(unitKey);
	}

    /**
     * @param record
     */
    public TypeUnit(IElement record)
    {
        this.unitKey = record.getKey();
        this.term = record.getLabel();
        this.termAbbr = record.getShortLabel();
        this.untiDef = "";
    }
    public String getTerm()
    {
        return term;
    }

    public void setTerm(String term)
    {
        this.term = term;
    }

    public String getTermAbbr()
    {
        return termAbbr;
    }

    public void setTermAbbr(String termAbbr)
    {
        this.termAbbr = termAbbr;
    }

    public String getUntiDef()
    {
        return untiDef;
    }

    public void setUntiDef(String untiDef)
    {
        this.untiDef = untiDef;
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

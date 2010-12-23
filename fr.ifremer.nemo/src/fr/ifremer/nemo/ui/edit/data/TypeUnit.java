package fr.ifremer.nemo.ui.edit.data;

public class TypeUnit extends Object {

    
    private String term, termAbbr, untiDef, unitKey;

	public TypeUnit(String term, String termAbbr, String untiDef, String unitKey) {
        this.setTerm(term);
        this.setTermAbbr(termAbbr);
        this.setUntiDef(untiDef);
        // FAE 6542
        this.setUnitKey(unitKey);
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

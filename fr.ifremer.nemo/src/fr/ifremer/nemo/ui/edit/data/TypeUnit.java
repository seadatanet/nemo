package fr.ifremer.nemo.ui.edit.data;

public class TypeUnit extends Object {

    
    private String term, termAbbr, untiDef;

	public TypeUnit(String term, String termAbbr, String untiDef) {
        this.setTerm(term);
        this.setTermAbbr(termAbbr);
        this.setUntiDef(untiDef);
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
    
}

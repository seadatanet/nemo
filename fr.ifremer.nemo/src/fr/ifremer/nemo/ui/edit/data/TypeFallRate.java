package fr.ifremer.nemo.ui.edit.data;

import sdn.vocabulary.interfaces.IElement;

public class TypeFallRate extends Object {

    
    private String term, def, key, termAbbr;

	public TypeFallRate(String term, String termAbbr, String def, String key) {
        this.setTerm(term);
        this.setTermAbbr(termAbbr);
        this.setDef(def);
        this.setKey(key);
	}

    /**
     * @param record
     */
    public TypeFallRate(IElement record)
    {
        this.key = record.getKey();
        this.term = record.getLabel();
        this.termAbbr = record.getShortLabel();
        this.def = "";
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

    public String getDef()
    {
        return def;
    }

    public void setDef(String def)
    {
        this.def = def;
    }
    public String getKey()
    {
        return key;
    }
    public void setKey(String key)
    {
        this.key = key;
    }
    
}

package fr.ifremer.nemo.ui.edit.data;

import fr.ifremer.nemo.util.Global;

public class TypeData extends Object {

	private String code;
    
    private String unite, conversion, format, entree, sortie, test, nom;
    
	private int position, debut, fin;
	
	private String debutFlag, finFlag, testIn, testOut;

	private String abbr;
    
	private Boolean sensor;

	public static final String DEFAULT_CONVERT = "x*1"; //$NON-NLS-1$

    
    private String codeP011;
    private boolean isP011;
    private String nomP011;
    

	public Boolean getSensor() {
		return sensor;
	}

	public void setSensor(Boolean sensor) {
		this.sensor = sensor;
	}

	public TypeData(String code, String unite, String conversion, String debut,
			String fin, String format, String entree, String sortie, String test) {

		this.setCode(code);
		this.setConversion(conversion);
		this.setEntree(entree);
		this.setFormat(format);
		this.setSortie(sortie);
		this.setUnite(unite);
		this.setNom(Global.EMPTY_STRING);
        this.setNomP011(Global.EMPTY_STRING);
        this.setCodeP011(Global.EMPTY_STRING);
        this.setAbbr(Global.EMPTY_STRING);
        this.setIsP011(false);
	}

	public TypeData(TypeData typeData) {

		this.setCode(typeData.code);
		this.setConversion(typeData.conversion);
		this.setEntree(typeData.entree);
		this.setFormat(typeData.format);
		this.setSortie(typeData.sortie);
		this.setUnite(typeData.unite);
		this.setNom(typeData.nom);
        this.setNomP011(typeData.nomP011);
        this.setCodeP011(typeData.codeP011);
        this.setAbbr(typeData.abbr);
        this.setIsP011(typeData.isP011);
	}

	public TypeData() {

		this.setCode("Code"); //$NON-NLS-1$
		this.setConversion(DEFAULT_CONVERT);
		this.setEntree("Entree"); //$NON-NLS-1$
		this.setFormat("Format"); //$NON-NLS-1$
		this.setSortie("Sortie"); //$NON-NLS-1$
		this.setUnite("Unite"); //$NON-NLS-1$
		this.setTest(Global.EMPTY_STRING);
		this.setNom(Global.EMPTY_STRING);
        this.setNomP011(Global.EMPTY_STRING);
        this.setCodeP011(Global.EMPTY_STRING);
        this.setAbbr(Global.EMPTY_STRING);
        this.setIsP011(false);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getConversion() {
		return conversion;
	}

	public void setConversion(String conversion) {
		this.conversion = conversion;
	}

	public int getDebut() {
		return debut;
	}

	public void setDebut(int debut) {
		this.debut = debut;
	}

	public String getEntree() {
		return entree;
	}

	public void setEntree(String entree) {
		this.entree = entree;
	}

	public int getFin() {
		return fin;
	}

	public void setFin(int fin) {
		this.fin = fin;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getSortie() {
		return sortie;
	}

	public void setSortie(String sortie) {
		this.sortie = sortie;
	}

	public String getUnite() {
		return unite;
	}

	public void setUnite(String unite) {
		this.unite = unite;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getDebutFlag() {
		if (debutFlag == null) {
			return Global.EMPTY_STRING;
		}
		return debutFlag;
	}

	public void setDebutFlag(String debutFlag) {
		this.debutFlag = debutFlag;
	}

	public String getFinFlag() {
		if (finFlag == null) {
			return Global.EMPTY_STRING;
		}
		return finFlag;
	}

	public void setFinFlag(String finFlag) {
		this.finFlag = finFlag;
	}

	public String getTestIn() {
		return testIn;
	}

	public void setTestIn(String testIn) {
		this.testIn = testIn;
	}

	public String getTestOut() {
		return testOut;
	}

	public void setTestOut(String testOut) {
		this.testOut = testOut;
	}
    
    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }
    
    
    // P011************************************************
    public String getCodeP011() {
        return codeP011;
    }

    public void setCodeP011(String codeP011) {
        this.codeP011 = codeP011;
    }
    
    public String getNomP011() {
        return nomP011;
    }

    public void setNomP011(String nomP011) {
        this.nomP011 = nomP011;
    }
    
    public boolean isP011() {
        return isP011;
    }

    public void setIsP011(boolean isP011) {
        this.isP011 = isP011;
    }
    
}

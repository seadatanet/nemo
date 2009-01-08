package fr.ifremer.nemo.ui;

public class TypeData extends Object {

	private String code, unite, conversion, format, entree, sortie, test, nom;

	private int position, debut, fin;
	
	private String debutFlag, finFlag, testIn, testOut;

	private Boolean sensor;

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

	}

	public TypeData(TypeData typeData) {

		this.setCode(typeData.code);
		this.setConversion(typeData.conversion);
		this.setEntree(typeData.entree);
		this.setFormat(typeData.format);
		this.setSortie(typeData.sortie);
		this.setUnite(typeData.unite);

	}

	public TypeData() {

		this.setCode("Code");
		this.setConversion("x*1");
		this.setEntree("Entree");
		this.setFormat("Format");
		this.setSortie("Sortie");
		this.setUnite("Unite");
		this.setTest("");

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
			return "";
		}
		return debutFlag;
	}

	public void setDebutFlag(String debutFlag) {
		this.debutFlag = debutFlag;
	}

	public String getFinFlag() {
		if (finFlag == null) {
			return "";
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
}

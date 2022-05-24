package fr.ifremer.nemo.ui.actions;

public class EdmoChoices {

	private int IdEdmoRespCdi;
	private int IdEdmoRespCsr;
	
	
	private int CodeEdmoDonnees;
	private int IdEdmoRespDonnees;
	private int IdEdmoDistDonnees;

	public static int CHOICE_IdEdmoRespCdi = 0;
	public static int CHOICE_CodeEdmoDonnees = 1;
	public static int CHOICE_IdEdmoRespDonnees = 2;
	public static int CHOICE_IdEdmoDistDonnees = 3;
	public static int CHOICE_IdEdmoRespCsr = 4;

	public EdmoChoices() {

	}

	public int getIdEdmoRespCdi() {
		return IdEdmoRespCdi;
	}

	public void setIdEdmoRespCdi(int idEdmoRespCdi) {
		IdEdmoRespCdi = idEdmoRespCdi;
	}

	public int getCodeEdmoDonnees() {
		return CodeEdmoDonnees;
	}

	public void setCodeEdmoDonnees(int codeEdmoDonnees) {
		CodeEdmoDonnees = codeEdmoDonnees;
	}

	public int getIdEdmoRespDonnees() {
		return IdEdmoRespDonnees;
	}

	public void setIdEdmoRespDonnees(int idEdmoRespDonnees) {
		IdEdmoRespDonnees = idEdmoRespDonnees;
	}

	public int getIdEdmoDistDonnees() {
		return IdEdmoDistDonnees;
	}

	public void setIdEdmoDistDonnees(int idEdmoDistDonnees) {
		IdEdmoDistDonnees = idEdmoDistDonnees;
	}

	public int getIdEdmoRespCsr() {
		return IdEdmoRespCsr;
	}

	public void setIdEdmoRespCsr(int idEdmoRespCsr) {
		IdEdmoRespCsr = idEdmoRespCsr;
	}
}

package fr.ifremer.nemo.ui.edit.survey;


public class TypeSurvey extends Object {

	private String code, qc;

	private int nombre;

	private Boolean sensor;

	public Boolean getSensor() {
		return sensor;
	}

	public void setSensor(Boolean sensor) {
		this.sensor = sensor;
	}

	public TypeSurvey(String code, int nombre, String qc) {

		this.setCode(code);
		this.setNombre(nombre);
		this.setQc(qc);
	}

	public TypeSurvey() {

		this.setCode("Code"); //$NON-NLS-1$
		this.setNombre(0);
		this.setQc("QC"); //$NON-NLS-1$
		this.setSensor(false);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getNombre() {
		return nombre;
	}

	public void setNombre(int nombre) {
		this.nombre = nombre;
	}

	public String getQc() {
		return qc;
	}

	public void setQc(String qc) {
		this.qc = qc;
	}

}

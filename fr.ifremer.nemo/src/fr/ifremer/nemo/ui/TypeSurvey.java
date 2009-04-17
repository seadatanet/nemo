package fr.ifremer.nemo.ui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import fr.ifremer.nemo.util.Messages;

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

		this.setCode("Code");
		this.setNombre(0);
		this.setQc("QC");
		this.setSensor(false);
//		this.listSensor();

	}
	
	public void listSensor() {
		BufferedReader lecteurAvecBuffer = null;
		String ligneLue = ""; 
		int numLigne = 0;
		ArrayList<String> listSensor = new ArrayList<String>();
				
		try {
			lecteurAvecBuffer = new BufferedReader(new FileReader(
					"resources/liste_capteur.txt"));
			
			// Lecture des lignes d'en-tete
			while ((ligneLue = lecteurAvecBuffer.readLine()) != null) {
				numLigne++;
				if (numLigne == 2) {
					break;
				}
			}
			
			// Lecture des lignes
			while ((ligneLue = lecteurAvecBuffer.readLine()) != null) {
				listSensor.add(ligneLue.substring(0,3));
			}
			
		} catch (IOException exc) {
			Utils.messageErreur(Messages.error);
		} 
		
		this.setSensor(listSensor.contains(this.getCode()));
		
//		System.out.println("");
//		for (String temp : listSensor)
//		{
//			System.out.println(temp);
//		}
//		return listSensor;
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

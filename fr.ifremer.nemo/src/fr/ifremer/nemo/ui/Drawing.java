package fr.ifremer.nemo.ui;

import java.util.ArrayList;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

public class Drawing {


public static Composite sectionClientType;
private static Group grou;
//private static Text lign, debu, fi;
	
public static void sectionManuelLigneDebutFinInject(String nomSection, String description, Section section, Text ligne, Text debut, Text fin, Text manuel, Button radio){
	
final String nomSec;
final Text manu;
final Text lign, debu, fi;
final Button inject;

nomSec = nomSection;
//manu = manuel;


section.setParent(EditDesc.formEdition);

	section = ViewEdit.toolkitEdition.createSection(EditDesc.formEdition.getBody(), 
			  Section.DESCRIPTION|Section.TITLE_BAR|
			  Section.TWISTIE|Section.COMPACT);
	
System.out.println("HOP");
	
	  section.addExpansionListener(new ExpansionAdapter() {
			  public void expansionStateChanged(ExpansionEvent e) {
				  EditDesc.formEdition.reflow(true);
			  }
			 });
	  section.setText(nomSection);
	  Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL );
	  section.setFont(font);
	  section.setDescription(description);
			 Composite sectionClient = ViewEdit.toolkitEdition.createComposite(section);
			 sectionClient.setLayout(new GridLayout());
			GridData gd = new GridData();
			  gd.horizontalSpan = 2;
			  
			  //saisie manuelle
			  //faire groupe et radios
			 
			  //bouton radio "saisie manuelle"
			  //c'est lui qui est passé en ppar
			  
			  radio.setParent(sectionClient);
			  radio.setText("Saisie manuelle");
			  
			  //Button saisieManuelle = ViewEdit.toolkitEdition.createButton(sectionClient, "Saisie manuelle", SWT.RADIO);
			
				 
			
			  
			  manuel = new Text(sectionClient, SWT.BORDER);
			  manu = manuel;
			  manuel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			  
			 
			  
			  //bouton radio "position"
			 //radio.setParent(sectionClient);
			  //radio.setText("Position");
			  Button position = ViewEdit.toolkitEdition.createButton(sectionClient, "Position", SWT.RADIO);
				
			  
			 
			  
			  GridLayout gridLayout1 = new GridLayout();
			  gridLayout1.numColumns = 4;
			Group groupe = new Group(sectionClient, SWT.NONE);
				 groupe.setText("Localisation");
				 groupe.setLayout(gridLayout1);
			  grou = groupe;
			  
			  //ligne
			  Label label1 = new Label(groupe, SWT.NULL);
			  label1.setText("Ligne");
			  ligne.setParent(groupe);
			  //ligne = new Text(groupe, SWT.BORDER);
			  ligne.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
			  //debut
			  Label label2 = new Label(groupe, SWT.NULL);
			  label2.setText("Début");
			  debut.setParent(groupe);
			  //= new Text(groupe, SWT.BORDER);
			  debut.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			  
			  //fin
			  Label label3 = new Label(groupe, SWT.NULL);
			  label3.setText("Fin");
			  fin.setParent(groupe);
			  //= new Text(groupe, SWT.BORDER);
			  fin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			  
			  //groupe non visible de base
			  groupe.setVisible(true);
			  
			  
			  //bouton d'injection de la position des valeurs
			  	Button buttonInject = new Button(groupe, SWT.NONE);
				buttonInject.setText("Injecter");
				inject = buttonInject;
			
				section.setClient(sectionClient);		
			  
				// une fois les éléments initialisés, on les passe en static pour y accéder à 
				//partir des listeners
				lign = ligne; 
				debu = debut;
				fi = fin;
				
			  
			  //listeners
			  
			  //radio saisie
			  radio
				 .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
						public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
							System.out.println("saisie manuelle section " + nomSec); // TODO Auto-generated Event stub widgetSelected()
							//raz de la valeur manuelle
							manu.setEnabled(true);
							//grou.setVisible(false);
							lign.setEnabled(false);
							debu.setEnabled(false);
							fi.setEnabled(false);
							inject.setEnabled(false);
						}
					});
			  
			  //radio position
			  position
				 .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
						public void widgetSelected(org.eclipse.swt.events.SelectionEvent f) {
							System.out.println("position dans fichier section " + nomSec); // TODO Auto-generated Event stub widgetSelected()
							//raz de la valeur manuelle
							
							manu.setText("");
							manu.setEnabled(false);
							//grou.setVisible(true);
							
							//raz valeurs manuelles
//							raz des valeurs injectees
							lign.setText("");
							debu.setText("");
							fi.setText("");
							lign.setEnabled(true);
							debu.setEnabled(true);
							fi.setEnabled(true);
						}
					});
	
			  
			  //bouton inject
			  buttonInject
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(org.eclipse.swt.events.SelectionEvent g) {
						System.out.println("bouton injection"); // TODO Auto-generated Event stub widgetSelected()
						//System.out.println("valeur de ligne lign==> " + lign.getText());
						//System.out.println("valeur de ligne de ViewEdit.ligneTest ==> " + ViewEdit.ligneTest.getText());
					Utils.inject(lign, debu, fi, ViewFile.textAreaDescription);
					
					}
				});
			  
			  
			  
			  //saisie par défaut et groupe désactivé
			  radio.setSelection(true);
			  lign.setEnabled(false);
				debu.setEnabled(false);
				fi.setEnabled(false);
				inject.setEnabled(false);
			  
			  //ajout de la section dans la liste des sections de description
			  ajoutSectionDansListeDesc(section);
			  
			  
	
}	
	
	
public static void sectionManuelComboLigneDebutFin(ScrolledForm form,String nomSection, String description, Section section, Text ligne, Text debut, Text fin, Text manuel){
	
	
	final String nomSec;
	final ScrolledForm forme;
	final Text lign, debu, fi;
	final Button inject;
	
	forme = form;

	
	lign = ligne;
	debu = debut;
	fi = fin;
	
	nomSec = nomSection;

		section = ViewEdit.toolkitEdition.createSection(form.getBody(), 
				  Section.DESCRIPTION|Section.TITLE_BAR|
				  Section.TWISTIE|Section.COMPACT);
		
			
		  section.addExpansionListener(new ExpansionAdapter() {
				  public void expansionStateChanged(ExpansionEvent e) {
					  forme.reflow(true);
				  }
				 });
		  Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL );
		  section.setFont(font);
		  section.setText(nomSection);
		  section.setDescription(description);
				 Composite sectionClient = ViewEdit.toolkitEdition.createComposite(EditDesc.sectionDonnees);
				 sectionClient.setLayout(new GridLayout());
				GridData gd = new GridData();
				  gd.horizontalSpan = 2;
				  
				  //saisie manuelle
				  //faire groupe et radios
				 
				  //bouton radio "saisie manuelle"
				  Button saisieManuelle = ViewEdit.toolkitEdition.createButton(sectionClient, "Saisie manuelle", SWT.RADIO);
					 saisieManuelle
					 .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
							public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
								System.out.println("saisie manuelle section " + nomSec); // TODO Auto-generated Event stub widgetSelected()
								//raz de la valeur manuelle
								//grou.setVisible(false);
								lign.setEnabled(false);
								debu.setEnabled(false);
								fi.setEnabled(false);
							}
						});
					 
				  
				//combo list	 
					 Combo combo = new Combo(section, SWT.READ_ONLY);
					 
					 
				//saisie manuelle 
				  manuel = new Text(sectionClient, SWT.BORDER);
				  manuel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				  
				  
				  
				  //bouton radio "position"
				  Button position = ViewEdit.toolkitEdition.createButton(sectionClient, "Position", SWT.RADIO);
					 position
					 .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
							public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
								System.out.println("position dans fichier section " + nomSec); // TODO Auto-generated Event stub widgetSelected()
								//raz de la valeur manuelle
								
								//grou.setVisible(true);
								lign.setEnabled(true);
								debu.setEnabled(true);
								fi.setEnabled(true);
							}
						});
				  
				  
				  
				  GridLayout gridLayout1 = new GridLayout();
				  gridLayout1.numColumns = 2;
				Group groupe = new Group(sectionClient, SWT.NONE);
					 EditDesc.groupRuptureDonnees.setText("Localisation");
					 EditDesc.groupRuptureDonnees.setLayout(gridLayout1);
				  grou = groupe;
				  
				  //ligne
				  Label label1 = new Label(groupe, SWT.NULL);
				  label1.setText("Ligne");
				  ligne.setParent(groupe);
				  //= new Text(groupe, SWT.BORDER);
				  ligne.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
				  //debut
				  Label label2 = new Label(groupe, SWT.NULL);
				  label2.setText("Début");
				  debut.setParent(groupe);
				  //= new Text(groupe, SWT.BORDER);
				  debut.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				  
				  //fin
				  Label label3 = new Label(groupe, SWT.NULL);
				  label3.setText("Fin");
				  fin.setParent(groupe);
				  //= new Text(groupe, SWT.BORDER);
				  fin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				  
				  //sasie par défaut et groupe désactivé
				  saisieManuelle.setSelection(true);
				  	lign.setEnabled(false);
					debu.setEnabled(false);
					fi.setEnabled(false);
					
				  
				  
				  section.setClient(sectionClient);		
				  
	
		
		
		
	}		
	






public static void sectionComboLigneDebutFinInject(ScrolledForm form,String nomSection, String description, Section section, Text ligne, Text debut, Text fin, Button radio, Combo combo){
	
	final String nomSec;
	final Combo comboList;
	final Text lign, debu, fi;
	final ScrolledForm forme;
	final Button inject;

	forme = form;

	lign = ligne;
	debu = debut;
	fi = fin;
	nomSec = nomSection;



	section.setParent(form);

		section = ViewEdit.toolkitEdition.createSection(form.getBody(), 
				  Section.DESCRIPTION|Section.TITLE_BAR|
				  Section.TWISTIE|Section.COMPACT);
		
	System.out.println("sectionComboLigneDebutFinInject");
		
		  section.addExpansionListener(new ExpansionAdapter() {
				  public void expansionStateChanged(ExpansionEvent e) {
					  forme.reflow(true);
				  }
				 });
		  Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL );
		  section.setFont(font);
		  section.setText(nomSection);
		  section.setDescription(description);
				 Composite sectionClient = ViewEdit.toolkitEdition.createComposite(section);
				 sectionClient.setLayout(new GridLayout());
				GridData gd = new GridData();
				  gd.horizontalSpan = 2;
				  
				  //saisie manuelle
				  //faire groupe et radios
				 
				  //bouton radio "saisie manuelle"
				  //c'est lui qui est passé en ppar
				  
				  radio.setParent(sectionClient);
				  radio.setText("Sélection manuelle");
				  
				  //Button saisieManuelle = ViewEdit.toolkitEdition.createButton(sectionClient, "Saisie manuelle", SWT.RADIO);
				
					 
				  combo.setParent(sectionClient);
				  comboList = combo;
				 
				  			  
				  //bouton radio "position"
				 //radio.setParent(sectionClient);
				  //radio.setText("Position");
				  Button position = ViewEdit.toolkitEdition.createButton(sectionClient, "Position", SWT.RADIO);
				
				  GridLayout gridLayout1 = new GridLayout();
				  gridLayout1.numColumns = 6;
				  Group groupe = new Group(sectionClient, SWT.NONE);
				  groupe.setText("Localisation");
				  groupe.setLayout(gridLayout1);
				  grou = groupe;
				  
				  //ligne
				  Label label1 = new Label(groupe, SWT.NULL);
				  label1.setText("Ligne");
				  
				  //ligne = new Text(groupe, SWT.BORDER);
				  ligne.setParent(groupe);
				 
				  ligne.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
				  //debut
				  Label label2 = new Label(groupe, SWT.NULL);
				  label2.setText("Début");
				  
				  Boolean tmp =  debut.setParent(groupe);
				  System.out.println("parent ?====> " +tmp);
				  EditDesc.debutNbreMesures.setText("test");
				  
				  
				  //= new Text(groupe, SWT.BORDER);
				  debut.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				  
				  //fin
				  Label label3 = new Label(groupe, SWT.NULL);
				  label3.setText("Fin");
				  fin.setParent(groupe);
				  //= new Text(groupe, SWT.BORDER);
				  fin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				  
				  //groupe non visible de base
				//  groupe.setVisible(false);
				  
				  
				  //bouton d'injection de la position des valeurs
				  	Button buttonInject = new Button(groupe, SWT.NONE);
					buttonInject.setText("Injecter");
					inject = buttonInject;
				
					section.setClient(sectionClient);		
				  
					// une fois les éléments initialisés, on les passe en static pour y accéder à 
					//partir des listeners
				 
				  //listeners
				  
				  //radio saisie
				  radio
					 .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
							public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
								System.out.println("saisie manuelle section " + nomSec); // TODO Auto-generated Event stub widgetSelected()
								//raz de la valeur manuelle
								comboList.setEnabled(true);
								//grou.setVisible(false);
								lign.setEnabled(false);
								debu.setEnabled(false);
								fi.setEnabled(false);
								inject.setEnabled(false);
							}
						});
				  
				  //radio position
				  
				  
				  
				  
				  position
					 .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
							public void widgetSelected(org.eclipse.swt.events.SelectionEvent f) {
								System.out.println("position dans fichier : section " + nomSec); // TODO Auto-generated Event stub widgetSelected()
								//raz de la valeur manuelle
								
								comboList.setEnabled(false);
								
								grou.setVisible(true);
								//raz des valeurs injectees
								lign.setText("");
								debu.setText("");
								fi.setText("");
								lign.setEnabled(true);
								debu.setEnabled(true);
								fi.setEnabled(true);
								inject.setEnabled(true);
								
							}
						});
		
				  
				  //bouton inject
				  buttonInject
					.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
						public void widgetSelected(org.eclipse.swt.events.SelectionEvent g) {
							System.out.println("bouton injection"); // TODO Auto-generated Event stub widgetSelected()
							//System.out.println("valeur de ligne lign==> " + lign.getText());
							//System.out.println("valeur de ligne de ViewEdit.ligneTest ==> " + ViewEdit.ligneTest.getText());
						Utils.inject(lign, debu, fi, ViewFile.textAreaDescription);
						
						}
					});
				  
				  
				  //saisie manuelle par défaut et groupe désactivé
				  radio.setSelection(true);
				  lign.setEnabled(false);
				  debu.setEnabled(false);
				  fi.setEnabled(false);
				  inject.setEnabled(false);
				  
				  
				  //ajout de la section dans la liste des sections de description
				  ajoutSectionDansListeDesc(section);
				  
					  
		
	}


public static void sectionComboInjectNbreLignes(ScrolledForm form,String nomSection, String description, Section section, Text nbreLignes, Button radio, Combo combo){
	
	final String nomSec;
	final Combo comboList;
	final Text lign;
	final ScrolledForm forme;
	final Button inject;

	forme = form;

	
	nomSec = nomSection;



	section.setParent(form);

		section = ViewEdit.toolkitEdition.createSection(form.getBody(), 
				  Section.DESCRIPTION|Section.TITLE_BAR|
				  Section.TWISTIE|Section.COMPACT);
		
	System.out.println("sectionComboLigneDebutFinInject");
		
		  section.addExpansionListener(new ExpansionAdapter() {
				  public void expansionStateChanged(ExpansionEvent e) {
					  forme.reflow(true);
				  }
				 });
		  Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL );
		  section.setFont(font);
		  section.setText(nomSection);
		  section.setDescription(description);
				 Composite sectionClient = ViewEdit.toolkitEdition.createComposite(section);
				 sectionClient.setLayout(new GridLayout());
				GridData gd = new GridData();
				  gd.horizontalSpan = 2;
				  
				  //saisie manuelle
				  //faire groupe et radios
				 
				  //bouton radio "saisie manuelle"
				  //c'est lui qui est passé en ppar
				  
				  radio.setParent(sectionClient);
				  radio.setText("Indicateur manuel");
				  
				  //Button saisieManuelle = ViewEdit.toolkitEdition.createButton(sectionClient, "Saisie manuelle", SWT.RADIO);
				
					 
				  combo.setParent(sectionClient);
				  comboList = combo;
				 
				  			  
				  //bouton radio "position"
				 //radio.setParent(sectionClient);
				  //radio.setText("Position");
				  Button position = ViewEdit.toolkitEdition.createButton(sectionClient, "Données", SWT.RADIO);
				
				  GridLayout gridLayout1 = new GridLayout();
				  gridLayout1.numColumns = 6;
				  Group groupe = new Group(sectionClient, SWT.NONE);
				  groupe.setText("Nombre de lignes");
				  groupe.setLayout(gridLayout1);
				  grou = groupe;
				  
				 			  
				  //ligne = new Text(groupe, SWT.BORDER);
				  nbreLignes.setParent(groupe);
				 
				  nbreLignes.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				  lign = nbreLignes;
				  
				  
				  //bouton d'injection de la position des valeurs
				  	Button buttonInject = new Button(groupe, SWT.NONE);
					buttonInject.setText("Injecter");
					inject = buttonInject;
				
					section.setClient(sectionClient);		
				  
					// une fois les éléments initialisés, on les passe en static pour y accéder à 
					//partir des listeners
				 
				  //listeners
				  
				  //radio saisie
				  radio
					 .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
							public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
								System.out.println("saisie manuelle section " + nomSec); // TODO Auto-generated Event stub widgetSelected()
								//raz de la valeur manuelle
								comboList.setEnabled(true);
								//grou.setVisible(false);
								lign.setEnabled(false);
								
							}
						});
				  
				  //radio position
				  
				  
				  
				  
				  position
					 .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
							public void widgetSelected(org.eclipse.swt.events.SelectionEvent f) {
								System.out.println("position dans fichier : section " + nomSec); // TODO Auto-generated Event stub widgetSelected()
								//raz de la valeur manuelle
								
								comboList.setEnabled(false);
								
								grou.setVisible(true);
								//raz des valeurs injectees
								lign.setText("");
								lign.setEnabled(true);
								inject.setEnabled(true);
								
							}
						});
		
				  
				  //bouton inject
				  buttonInject
					.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
						public void widgetSelected(org.eclipse.swt.events.SelectionEvent g) {
							System.out.println("bouton injection"); // TODO Auto-generated Event stub widgetSelected()
							//System.out.println("valeur de ligne lign==> " + lign.getText());
							//System.out.println("valeur de ligne de ViewEdit.ligneTest ==> " + ViewEdit.ligneTest.getText());
						Utils.injectNbreLignes(lign, ViewFile.textAreaDescription);
						
						}
					});
				  
				  
				  //saisie manuelle par défaut et groupe désactivé
				  radio.setSelection(true);
				  lign.setEnabled(false);
				  inject.setEnabled(false);
				  
				  
				  //ajout de la section dans la liste des sections de description
				  ajoutSectionDansListeDesc(section);
				  
					  
		
	}







public static void sectionLabelText(ScrolledForm form,String nomSection, String description, Section section, Text manuel){
//creation d'une section simple avec 1 label description et une zone de texte
	
	final String nomSec;
	final ScrolledForm forme;
	final Text lign, debu, fi;

	forme = form;
	nomSec = nomSection;
		
		section.setParent(form);
		section = ViewEdit.toolkitEdition.createSection(form.getBody(), 
				  Section.DESCRIPTION|Section.TITLE_BAR|
				  Section.TWISTIE|Section.COMPACT);
		
		  section.addExpansionListener(new ExpansionAdapter() {
				  public void expansionStateChanged(ExpansionEvent e) {
					  forme.reflow(true);
				  }
				 });
		  Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL );
		  section.setFont(font);
		  section.setText(nomSection);
		  section.setDescription(description);
				 Composite sectionClient = ViewEdit.toolkitEdition.createComposite(section);
				 sectionClient.setLayout(new GridLayout());
				GridData gd = new GridData();
				  gd.horizontalSpan = 2;
				 
				  
				  //label avec le texte de "description"
				  Label label1 = new Label(section, SWT.NULL);
				  label1.setText(description);
					 
				//zone d'édition
				  manuel = new Text(sectionClient, SWT.BORDER);
				  manuel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				  
				  section.setClient(sectionClient);		
				  
				  
				  //ajout de la section dans la liste des sections de description
				  ajoutSectionDansListeDesc(section);  
			
		
	}	




public static void sectionRadioManuelRadioSimpleInjectLigneDebutFinInject(ScrolledForm form,String nomSection, String description, Section section, Text manuel, Button radio, String txtRadio1, String txtRadio2, Text ligne, Text debut, Text fin){
	
	final String nomSec;
	final ScrolledForm forme;
	final Button bouton, bouton2, inject, inject2;
	final Text manu;
	final Text lign, debu, fi;
	final Group group;
	
	
	manu = manuel;
	forme = form;
	nomSec = nomSection;
	bouton = radio;
	
	GridLayout layout = new GridLayout(4, false);
	
	
		section.setParent(form);
		section = ViewEdit.toolkitEdition.createSection(form.getBody(), 
				  Section.DESCRIPTION|Section.TITLE_BAR|
				  Section.TWISTIE|Section.COMPACT);
		
		  section.addExpansionListener(new ExpansionAdapter() {
				  public void expansionStateChanged(ExpansionEvent e) {
					  forme.reflow(true);
				  }
				 });
		  Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL );
		  section.setFont(font);
		  section.setText(nomSection);
		  section.setDescription(description);
				 Composite sectionClient = ViewEdit.toolkitEdition.createComposite(section);
				 sectionClient.setLayout(new GridLayout());
			
				 GridData gd = new GridData();
				  gd.horizontalSpan = 2;
				 
				  //radio manuel dont on garde le pointeur
				  radio.setParent(sectionClient);
				  radio.setText(txtRadio1);
				  
				  //zone d'edition manuelle
				  manuel.setParent(sectionClient);
				  manuel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				  
				  //bouton d'injection de la position des valeurs
				  	Button buttonInject = new Button(sectionClient, SWT.NONE);
					buttonInject.setText("Injecter");
				  
					inject = buttonInject;
				 
				  //radio 2 simple
				  Button radio2 = new Button(sectionClient, SWT.RADIO);
				  radio2.setText(txtRadio2);
				  
				  /////////////
				  

				  GridLayout gridLayout1 = new GridLayout();
				  gridLayout1.numColumns = 6;
				  Group groupe = new Group(sectionClient, SWT.NONE);
				  groupe.setText("Localisation");
				  groupe.setLayout(gridLayout1);
				  group = groupe;
				  
				  //ligne
				  Label label1 = new Label(groupe, SWT.NULL);
				  label1.setText("Ligne");
				  
				  //ligne = new Text(groupe, SWT.BORDER);
				  ligne.setParent(groupe);
				 
				  ligne.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
				  //debut
				  Label label2 = new Label(groupe, SWT.NULL);
				  label2.setText("Début");
				  
				  Boolean tmp =  debut.setParent(groupe);
				  System.out.println("parent ?====> " +tmp);
				  EditDesc.debutNbreMesures.setText("test");
				  
				  
				  //= new Text(groupe, SWT.BORDER);
				  debut.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				  
				  //fin
				  Label label3 = new Label(groupe, SWT.NULL);
				  label3.setText("Fin");
				  fin.setParent(groupe);
				  //= new Text(groupe, SWT.BORDER);
				  fin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				  
				  //groupe non visible de base
				//  groupe.setVisible(false);
				  
				  
				  //bouton d'injection de la position des valeurs
				  	Button buttonInject2 = new Button(groupe, SWT.NONE);
					buttonInject2.setText("Injecter");
					inject2 = buttonInject2;
				  
				  
				  groupe.setEnabled(false);
				  
				  ///////////////
				  
				  
				  section.setClient(sectionClient);		
			
				  //listener sur le premier radio global
				  
				  bouton2 = radio2;
				  
				  radio
				  .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
						public void widgetSelected(org.eclipse.swt.events.SelectionEvent h) {
							System.out.println("saisie manuel nbre lignes entete station"); // TODO Auto-generated Event stub widgetSelected()
							manu.setEnabled(true);
							inject.setEnabled(true);
							group.setEnabled(false);
							
							  
						
						
						}
					});
				  
				  
				  
				  radio2
				  .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
						public void widgetSelected(org.eclipse.swt.events.SelectionEvent h) {
							System.out.println("bouton nbre octets 1ere ligne entete station"); // TODO Auto-generated Event stub widgetSelected()
							manu.setText("");
							manu.setEnabled(false);
							inject.setEnabled(false);
							group.setEnabled(true); 
						
						
						}
					});
				  
				  
				  //bouton inject
				  buttonInject
					.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
						public void widgetSelected(org.eclipse.swt.events.SelectionEvent g) {
							System.out.println("bouton injection"); // TODO Auto-generated Event stub widgetSelected()
							//System.out.println("valeur de ligne lign==> " + lign.getText());
							//System.out.println("valeur de ligne de ViewEdit.ligneTest ==> " + ViewEdit.ligneTest.getText());
						Utils.injectNbreLignes(manu, ViewFile.textAreaDescription);
						
						
						}
					});
				  
				  lign = ligne;
				  debu = debut;
				  fi = fin;
				  
				  //bouton inject
				  buttonInject2
					.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
						public void widgetSelected(org.eclipse.swt.events.SelectionEvent g) {
							System.out.println("bouton injection"); // TODO Auto-generated Event stub widgetSelected()
							//System.out.println("valeur de ligne lign==> " + lign.getText());
							//System.out.println("valeur de ligne de ViewEdit.ligneTest ==> " + ViewEdit.ligneTest.getText());
						Utils.inject(lign, debu, fi, ViewFile.textAreaDescription);
						
						
						}
					});
				
				  
				  //saisie par défaut et groupe désactivé
				  // 	lign.setEnabled(true);
//					debu.setEnabled(true);
//					fi.setEnabled(true);
					inject.setEnabled(true);
					radio.setSelection(true);
					
				  //ajout de la section dans la liste des sections de description
				  ajoutSectionDansListeDesc(section);  
	
	
	
	
}



public static void sectionLabelTextInjectNbreLignes(ScrolledForm form,String nomSection, String description, Section section, Text manuel){
//	creation d'une section simple avec 1 label description et une zone de texte
		
		final String nomSec;
		final Text manu;
		final ScrolledForm forme;
		final Text lign, debu, fi;

		forme = form;
		nomSec = nomSection;
			
		  manu = manuel;
		
			section.setParent(form);
			section = ViewEdit.toolkitEdition.createSection(form.getBody(), 
					  Section.DESCRIPTION|Section.TITLE_BAR|
					  Section.TWISTIE|Section.COMPACT);
			
			  section.addExpansionListener(new ExpansionAdapter() {
					  public void expansionStateChanged(ExpansionEvent e) {
						  forme.reflow(true);
					  }
					 });
		
			  Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL );
			  section.setFont(font);
			  section.setText(nomSection);
			  section.setDescription(description);
					 Composite sectionClient = ViewEdit.toolkitEdition.createComposite(section);
					 GridLayout grid = new GridLayout();
					 grid.numColumns = 1;
					 sectionClient.setLayout(grid);
				
					 
					
					  
					  //label avec le texte de "description"
					  Label label1 = new Label(section, SWT.NULL);
					  label1.setText(description);
						 
					//zone d'édition
					  manuel.setParent(sectionClient);
					
					  //= new Text(sectionClient, SWT.BORDER);
					  manuel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					  
				
					  
					  //bouton d'injection de la position des valeurs
					  	Button buttonInject = new Button(sectionClient, SWT.NONE);
						buttonInject.setText("Injecter"); 
					 
					  section.setClient(sectionClient);		
					  
				
					  
					  //listener bouton inject
					  buttonInject
						.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
							public void widgetSelected(org.eclipse.swt.events.SelectionEvent h) {
								System.out.println("bouton injection nbre lignes"); // TODO Auto-generated Event stub widgetSelected()
								
								  
							Utils.injectNbreLignes(manu, ViewFile.textAreaDescription);
							
							}
						});
					  
				
					  
					  //ajout de la section dans la liste des sections de description
					  ajoutSectionDansListeDesc(section);  
				
			
		}	



public static void sectionTextInjectNbreLignesNbreOctets(ScrolledForm form, String nomSection, String description, Section section, Text manuel){
	
	
	
//		creation d'une section simple avec 1 label description et une zone de texte
			
			final String nomSec;
			final Text manu;
			final ScrolledForm forme;
			final Text lign, debu, fi;

			forme = form;
			nomSec = nomSection;
				
			  manu = manuel;
			
				section.setParent(form);
				section = ViewEdit.toolkitEdition.createSection(form.getBody(), 
						  Section.DESCRIPTION|Section.TITLE_BAR|
						  Section.TWISTIE|Section.COMPACT);
				
				  section.addExpansionListener(new ExpansionAdapter() {
						  public void expansionStateChanged(ExpansionEvent e) {
							  forme.reflow(true);
						  }
						 });
				  Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL );
				  section.setFont(font);
				  section.setText(nomSection);
				  section.setDescription(description);
						 Composite sectionClient = ViewEdit.toolkitEdition.createComposite(section);
						 sectionClient.setLayout(new GridLayout());
						GridData gd = new GridData();
						  gd.horizontalSpan = 2;
						 
						
						  
						  //label avec le texte de "description"
						  Label label1 = new Label(section, SWT.NULL);
						  label1.setText(description);
							 
						//zone d'édition
						  manuel.setParent(sectionClient);
						  
						
						  //= new Text(sectionClient, SWT.BORDER);
						  manuel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
						  
					
						  
						  //bouton d'injection de la position des valeurs
						  	Button buttonInject = new Button(sectionClient, SWT.NONE);
							buttonInject.setText("Injecter"); 
						 
						  section.setClient(sectionClient);		
						  
					
						  
						  //listener bouton inject
						  buttonInject
							.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
								public void widgetSelected(org.eclipse.swt.events.SelectionEvent h) {
									System.out.println("bouton injection nbre lignes"); // TODO Auto-generated Event stub widgetSelected()
									
									  
								Utils.injectNbreLignes(manu, ViewFile.textAreaDescription);
								
								}
							});
						  
					
						  
						  //ajout de la section dans la liste des sections de description
						  ajoutSectionDansListeDesc(section);  
					
	
	
	
}




public static void ajoutSectionDansListeDesc(Section section){
	
	EditDesc.listeSectionsDesc.add(section);
	System.out.println("ajout section ok");
	
}


public static void ajoutSectionDansListeSurvey(Section section){
	
	EditSurvey.listeSectionsSurvey.add(section);
	System.out.println("ajout section ok");
	
}


public static void sectionNlabelsNtext(Section section, String nomSection, ArrayList <Text> listeText, ArrayList <String> listeNoms, String descriptionSection){
	
	int nbre, i;	
	final String nomSec;
	final ScrolledForm forme;


	
	nbre = listeText.size();
	System.out.println("=================ok1===========<<<" + nbre);

	
	
	//affichage section
	section.setParent(ViewEdit.formEdition);
	section = ViewEdit.toolkitEdition.createSection(ViewEdit.formEdition.getBody(), 
			  Section.DESCRIPTION|Section.TITLE_BAR|
			  Section.TWISTIE|Section.COMPACT);
	

	
	  section.addExpansionListener(new ExpansionAdapter() {
			  public void expansionStateChanged(ExpansionEvent e) {
				  ViewEdit.formEdition.reflow(true);
			  }
			 });
	  section.setText(nomSection);
	  Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL );
	  section.setFont(font);
	  section.setDescription(descriptionSection);
			 Composite sectionClient = ViewEdit.toolkitEdition.createComposite(section);
			 sectionClient.setLayout(new GridLayout());
			GridData gd = new GridData();
			  gd.horizontalSpan = 2;
	
				System.out.println("=================ok1===========");
	for (i=0; i<nbre; i++){

		//label avec le texte de "description"
		Label label = new Label(sectionClient, SWT.NULL);
		label.setText(listeNoms.get(i));
		System.out.println("creation de" + listeNoms.get(i));
		
		
		//affichage zone texte dans la section
		listeText.get(i).setParent(sectionClient);
		listeText.get(i).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
	}
	
	section.setClient(sectionClient);
	 //ajout de la section dans la liste des sections de description
	  ajoutSectionDansListeSurvey(section);  
	  ViewEdit.formEdition.reflow(true);
	
}



public static void sectionNlabelsNtextListe(Section section, String nomSection, ArrayList <Text> listeText, ArrayList <String> listeNoms, String descriptionSection, List liste){
	
	int nbre, i;	
	final String nomSec;
	final ScrolledForm forme;

	System.out.println("=================ok1===========");
	
	nbre = listeText.size();
	
	
	
	//affichage section
	section.setParent(ViewEdit.formEdition);
	section = ViewEdit.toolkitEdition.createSection(ViewEdit.formEdition.getBody(), 
			  Section.DESCRIPTION|Section.TITLE_BAR|
			  Section.TWISTIE|Section.COMPACT);
	
	  section.addExpansionListener(new ExpansionAdapter() {
			  public void expansionStateChanged(ExpansionEvent e) {
				  ViewEdit.formEdition.reflow(true);
			  }
			 });
	  section.setText(nomSection);
	  Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL );
	  section.setFont(font);
	  section.setDescription(descriptionSection);
			 Composite sectionClient = ViewEdit.toolkitEdition.createComposite(section);
			 sectionClient.setLayout(new GridLayout());
			GridData gd = new GridData();
			  gd.horizontalSpan = 2;
	
	sectionClientType = sectionClient;
	
	for (i=0; i<nbre; i++){

		//label avec le texte de "description"
		
		Label label = new Label(sectionClient, SWT.NULL);
		label.setText(listeNoms.get(i));
		
		//affichage zone texte dans la section
		listeText.get(i).setParent(sectionClient);
		listeText.get(i).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
			
				  
	
	}
	
		
	//affichage d'une table
	TableDataSurvey.go();
	
	
		//ajout liste et boutons déplacement des éléments
	//mettre les listeners dessus !!!!!
		
	/*
	
//	remplissage des types
	
		EditSurvey.listeType.setParent(sectionClient);
		EditSurvey.listeType.setLayoutData(new GridData(GridData.FILL_BOTH));
			
		Button up = new Button(sectionClient, SWT.NULL);
		up.setText("Monter");
		Button down = new Button(sectionClient, SWT.NULL);
		down.setText("Descendre");
		Button del = new Button(sectionClient, SWT.NULL);
		del.setText("Supprimer");
		*/
		
	
		section.setClient(sectionClient);
	
	  //ajout de la section dans la liste des sections de description
	  ajoutSectionDansListeSurvey(section);  
	
}

public static void sectionTypeDonnees(Section section, String nomSection, ArrayList <Text> listeText, ArrayList <String> listeNoms, String descriptionSection, List liste){
	
	int nbre, i;	
	final String nomSec;
	final ScrolledForm forme;

	System.out.println("=================ok1===========");
	
	nbre = listeText.size();
	
	
	
	//affichage section
	section.setParent(ViewEdit.formEdition);
	section = ViewEdit.toolkitEdition.createSection(ViewEdit.formEdition.getBody(), 
			  Section.DESCRIPTION|Section.TITLE_BAR|
			  Section.TWISTIE|Section.COMPACT);
	
	  section.addExpansionListener(new ExpansionAdapter() {
			  public void expansionStateChanged(ExpansionEvent e) {
				  ViewEdit.formEdition.reflow(true);
			  }
			 });
	  section.setText(nomSection);
	  Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL );
	  section.setFont(font);
	  section.setDescription(descriptionSection);
			 Composite sectionClient = ViewEdit.toolkitEdition.createComposite(section);
			 sectionClient.setLayout(new GridLayout());
			GridData gd = new GridData();
			  gd.horizontalSpan = 2;
	
	sectionClientType = sectionClient;
	
	
		
	//affichage d'une table
	TableDataSurvey.go();
	
	
		//ajout liste et boutons déplacement des éléments
	//mettre les listeners dessus !!!!!
		
	/*
	
//	remplissage des types
	
		EditSurvey.listeType.setParent(sectionClient);
		EditSurvey.listeType.setLayoutData(new GridData(GridData.FILL_BOTH));
			
		Button up = new Button(sectionClient, SWT.NULL);
		up.setText("Monter");
		Button down = new Button(sectionClient, SWT.NULL);
		down.setText("Descendre");
		Button del = new Button(sectionClient, SWT.NULL);
		del.setText("Supprimer");
		*/
		
	
		section.setClient(sectionClient);
	
	  //ajout de la section dans la liste des sections de description
	  ajoutSectionDansListeSurvey(section);  
	
}

public static void sectionCommentaireSurvey(Section section, String nomSection, Text commentaire, String descriptionSection){
	
	
	//affichage section
	section.setParent(ViewEdit.formEdition);
	section = ViewEdit.toolkitEdition.createSection(ViewEdit.formEdition.getBody(), 
			  Section.DESCRIPTION|Section.TITLE_BAR|
			  Section.TWISTIE|Section.COMPACT);
	
	  section.addExpansionListener(new ExpansionAdapter() {
			  public void expansionStateChanged(ExpansionEvent e) {
				  ViewEdit.formEdition.reflow(true);
			  }
			 });
	  section.setText(nomSection);
	  Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL );
	  section.setFont(font);
	  section.setDescription(descriptionSection);
			 Composite sectionClient = ViewEdit.toolkitEdition.createComposite(section);
			 sectionClient.setLayout(new GridLayout());
			GridData gd = new GridData();
			  gd.horizontalSpan = 2;
			  gd.verticalSpan = 2;
			  
		
	//commentaire.setText("\n\n\n\n\n\n\n");
			  
	commentaire.setParent(sectionClient);
	commentaire.setLayoutData(new GridData(GridData.FILL_BOTH));
	commentaire.setTextLimit(50000);
	 {
		    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		    gridData.minimumHeight = 40;
		    gridData.minimumWidth = 50;
		    commentaire.setLayoutData(gridData);
		  }
	
//	commentaire = new Text(sectionClient, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | Window.getDefaultOrientation() | SWT.BORDER);
//	commentaire.setText("Mon commentaire.");
	
//	commentaire.setToolTipText("Commentaire");
	  {
	    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
	    gridData.minimumHeight = 40;
	    gridData.minimumWidth = 50;
	    commentaire.setLayoutData(gridData);
	  }
	
	
	section.setClient(sectionClient);	
	 //ajout de la section dans la liste des sections de description
	  ajoutSectionDansListeSurvey(section);  
	
	ViewEdit.formEdition.reflow(true);
}





	
}

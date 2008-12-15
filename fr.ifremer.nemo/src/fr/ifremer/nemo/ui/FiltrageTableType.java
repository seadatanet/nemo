package fr.ifremer.nemo.ui;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import fr.ifremer.nemo.config.EdmoHandler;
import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class FiltrageTableType {

	public static int listeOuverte;

	public static Composite top;

	public static Table filtreTableData, filtreTableSurvey;
	
	public static Combo comboRecherche;

	public static Text filtreText;

	public static Shell shell;

	public static Label labelText;

	public static Button filtreButtonSelectionner;

	public static Button filtreButtonCancel;
	
	public static void go(String table) {
		// raz message d'erreur
		final String tabOrigine = table;
		
		Utils.messageErreur(""); //$NON-NLS-1$

		if (listeOuverte == 1) {
			Utils.messageErreur(Messages.getString("FiltrageTableType.1"));
		} else {
			shell = new Shell(Application.display, SWT.BORDER | SWT.TITLE
					| SWT.APPLICATION_MODAL | SWT.SYSTEM_MODAL);
			shell.setSize(300, 500);

			if (tabOrigine.equals(Global.IDEDMO)) {
				shell.setText(Messages.getString("FiltrageTableType.2")); //$NON-NLS-1$
			} else if (tabOrigine.equals(Global.WSSURVEY)) {
				shell.setText(Messages.getString("FiltrageTableType.2")); //$NON-NLS-1$
			} else if (tabOrigine.equals(Global.DATATYPE)) {
				shell.setText(Messages.getString("FiltrageTableType.3")); //$NON-NLS-1$
			} else if (tabOrigine.equals(Global.PARAMETER)) {
				shell.setText(Messages.getString("FiltrageTableType.4")); //$NON-NLS-1$
			} else if (tabOrigine.equals(Global.SHIP)) {
				shell.setText(Messages.getString("FiltrageTableType.5")); //$NON-NLS-1$
			} else if (tabOrigine.equals(Global.DATACENTRE)) {
				shell.setText(Messages.getString("FiltrageTableType.6")); //$NON-NLS-1$
			} else if (tabOrigine.equals(Global.REGION)) {
				shell.setText(Messages.getString("FiltrageTableType.7")); //$NON-NLS-1$
			}

			shell.setLayout(new FillLayout());

			top = new Composite(shell, SWT.NONE);
			top.setLayout(null);

			if (tabOrigine.equals(Global.IDEDMO)) {
				EdmoHandler.listeFiltree = new ArrayList<String>();
				
				EdmoHandler.listeFiltree.addAll(EdmoHandler.listeOrigine);
			} else if (tabOrigine.equals(Global.SHIP)) {
				NemoLists.listeFiltree = new ArrayList<String>();
				
				NemoLists.listeFiltree.addAll(NemoLists.listeNavire);
			} else if (tabOrigine.equals(Global.DATACENTRE)) {
				NemoLists.listeFiltree = new ArrayList<String>();
				
				NemoLists.listeFiltree.addAll(NemoLists.listeCentreDonnees);
			} else if (tabOrigine.equals(Global.REGION)) {
				NemoLists.listeFiltree = new ArrayList<String>();
				
				NemoLists.listeFiltree.addAll(NemoLists.listeRegion);
			//} else if (tabOrigine.equals(Global.DATACENTRE)) {
			//	NemoLists.listeFiltree = new ArrayList<String>();
			//	
			//	NemoLists.listeFiltree.addAll(NemoLists.listeNavire);
			} else {
				// init des listes filtrées survey + data
				TableDataSurvey.listeFiltree = new ArrayList<TypeSurvey>();

				// init de la liste des references campagne à partir des
				// listesWS
				TableWS_Survey.surveysWS = NemoLists.getSurveys();
			

				// remplissage des noms + ref
				// liste filtree initialisée à surveysWS au depart
				TableWS_Survey.listeFiltree = new ArrayList<String>();
				TableWS_Survey.refsWS = new ArrayList<String>();
				for (int i = 0; i < TableWS_Survey.surveysWS.length; i++) {
					TableWS_Survey.refsWS.add(TableWS_Survey.surveysWS[i]
							.getReference()
							+ " - " + TableWS_Survey.surveysWS[i].getName()); //$NON-NLS-1$
					TableWS_Survey.listeFiltree.add(TableWS_Survey.surveysWS[i]
							.getReference()
							+ " - " + TableWS_Survey.surveysWS[i].getName()); //$NON-NLS-1$
				}

				// liste filtree initialisée à typesWS au depart
				try {
					int taille = TableDataSurvey.typesWS.size();
					
					for (int i = 0; i < taille; i++) {
						TableDataSurvey.listeFiltree
								.add(TableDataSurvey.typesWS.get(i));
					}

					// TableDataSurvey.listeFiltree = TableDataSurvey.typesWS;

					TableData.listeFiltree = new ArrayList<TypeData>();
					// liste filtree initialisée à typesWS au depart
					taille = TableData.typesWS.size();
					for (int i = 0; i < taille; i++) {
						TableData.listeFiltree.add(TableData.typesWS.get(i));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			filtreText = new Text(top, SWT.BORDER);
			filtreText.setBounds(new Rectangle(3, 21, 298, 19));
			filtreText.setText(""); //$NON-NLS-1$
			labelText = new Label(top, SWT.NONE);
			labelText.setBounds(new Rectangle(3, 4, 298, 13));
			labelText.setText(Messages.getString("FiltrageTableType.8")); //$NON-NLS-1$
			labelText.pack();

			filtreButtonSelectionner = new Button(top, SWT.NONE);
			filtreButtonSelectionner.setBounds(new Rectangle(62, 289, 65, 23));
			filtreButtonSelectionner.setText(Messages
					.getString("FiltrageTableType.9")); //$NON-NLS-1$
			filtreButtonSelectionner.pack();
			filtreButtonCancel = new Button(top, SWT.NONE);
			filtreButtonCancel.setBounds(new Rectangle(168, 289, 65, 23));
			filtreButtonCancel.setText(Messages
					.getString("FiltrageTableType.10")); //$NON-NLS-1$
			filtreButtonCancel.pack();

			// affichage des tables
			if (tabOrigine.equals(Global.IDEDMO)) {
				creationTableEdmo();
			} else if (tabOrigine.equals(Global.WSSURVEY)) {
				creationTableWS_Survey();
			} else if (tabOrigine.equals(Global.DATATYPE)) {
				creationTableSurvey();
			} else if (tabOrigine.equals(Global.PARAMETER)) {
				creationTableData();
			} else if (tabOrigine.equals(Global.SHIP) || 
					tabOrigine.equals(Global.DATACENTRE) || 
					tabOrigine.equals(Global.REGION)) {
				creationTableBODC();
			}

			filtreButtonCancel
					.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
						public void widgetSelected(
								org.eclipse.swt.events.SelectionEvent e) {
							// System.out.println("valider cancel liste");
							shell.close();
							listeOuverte = 0;
						}
					});

			filtreButtonSelectionner
					.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
						public void widgetSelected(
								org.eclipse.swt.events.SelectionEvent e) {

							listeOuverte = 0;

							if (tabOrigine.equals(Global.IDEDMO)) {
								Utils.selectComboValeur(
										comboRecherche,
										EdmoHandler.filtreTable.getItem(
												EdmoHandler.filtreTable
														.getSelectionIndex())
												.getText());
								shell.close();
							} else if (tabOrigine
									.equals(Global.WSSURVEY)) {

								// sélection d'une référence campagne
								int index = TableWS_Survey.filtreTable
										.getSelectionIndex();
								String reference = TableWS_Survey.listeFiltree
										.get(index);
								Pattern p = Pattern.compile(" - "); //$NON-NLS-1$

								String mots[] = p.split(reference);

								EditSurvey.webService(mots[0]);

								Utils.messageOK(Messages
										.getString("FiltrageTableType.21"));
								shell.close();

							} else if (tabOrigine
									.equals(Global.DATATYPE)) {

								// ajout d'un dataType dans l'onglet Survey
								TypeSurvey typeSurvey = new TypeSurvey();

								int index = TableDataSurvey.filtreTable
										.getSelectionIndex();
								typeSurvey = TableDataSurvey.listeFiltree
										.get(index);

								// test avant ajout pour doublons
								int taille = TableDataSurvey.types.size();
								Boolean found = false;

								if (taille != 0) {

									for (int i = 0; i < taille; i++) {

										// split sur " - "
										Pattern p = Pattern.compile(" - "); //$NON-NLS-1$
										String mots[] = p.split(typeSurvey
												.getCode());
										String codeActuel = mots[0];

										String mots2[] = p
												.split(TableDataSurvey.types
														.get(i).getCode());
										String codeModele = mots2[0];

										if (codeModele.matches(codeActuel)) {
											found = true;
										}
									}

									if (found == false) {
										TableDataSurvey.types.add(typeSurvey);
									} else {
										Utils
												.messageErreur(Messages
														.getString("FiltrageTableType.13")); //$NON-NLS-1$
									}
								} else {
									TableDataSurvey.types.add(typeSurvey);
								}

								TableDataSurvey.majTypeData();

								// razTableData
								TableDataSurvey.razTableData();

								// refresh de la table
								TableDataSurvey.creationTable();

								EditSurvey.sectionType.pack();
								EditSurvey.sectionType.redraw();

								Composite c = EditSurvey.sectionType;
								while (c != null) {
									c.setRedraw(false);
									c = c.getParent();
									if (c instanceof ScrolledForm) {
										break;
									}
								}
								c = EditSurvey.sectionType;
								while (c != null) {
									c.layout(true);
									c = c.getParent();
									if (c instanceof ScrolledForm) {
										((ScrolledForm) c).reflow(true);
										break;
									}
								}
								c = EditSurvey.sectionType;
								while (c != null) {
									c.setRedraw(true);
									c = c.getParent();
									if (c instanceof ScrolledForm) {
										break;
									}
								}

								shell.close();

							} else if (tabOrigine.equals(Global.PARAMETER)) {

								// ajout d'un parameter dans l'onglet Data

								TypeData typeData = new TypeData();

								int index = TableData.filtreTable
										.getSelectionIndex();
								typeData = TableData.listeFiltree.get(index);

								// test avant ajout pour doublons
								int taille = TableData.types.size();
								Boolean found = false;

								if (taille != 0) {

									for (int i = 0; i < taille; i++) {
										String codeModele, codeActuel;

										// split sur " - "
										Pattern p = Pattern.compile(" - "); //$NON-NLS-1$
										String mots[] = p.split(typeData
												.getCode());
										codeActuel = mots[0];

										String mots2[] = p
												.split(TableData.types.get(i)
														.getCode());
										codeModele = mots2[0];

										if (codeModele.matches(codeActuel)) {
											found = true;
										}
									}

									if (found == false) {
										TableData.types.add(new TypeData(
												typeData));
									} else {
										Utils
												.messageErreur(Messages
														.getString("FiltrageTableType.15")); //$NON-NLS-1$
									}
								} else {
									TableData.types.add(new TypeData(typeData));
								}

								// TableData.types.add(typeData);
								TableData.majTypeData();

								// razTableData
								TableData.razTableData();

								// refresh de la table
								TableData.creationTable();

								shell.close();
							} else if (tabOrigine.equals(Global.SHIP) ||
									tabOrigine.equals(Global.DATACENTRE) ||
									tabOrigine.equals(Global.REGION)) {
								Utils.selectComboValeur(
										comboRecherche,
										NemoLists.filtreTable.getItem(
												NemoLists.filtreTable
														.getSelectionIndex())
												.getText());
								shell.close();
							}

						} // ///////////////FIN

					});

			// listener keypressed sur le champ text
			filtreText.addListener(SWT.KeyUp, new Listener() {
				public void handleEvent(Event e) {
					if (filtreText.getText().matches("")) {} //$NON-NLS-1$
					else {
						if (tabOrigine.equals(Global.IDEDMO)) {
							filtrageListeEdmo(filtreText.getText());
						} else if (tabOrigine.equals(Global.WSSURVEY)) {
							filtrageListeWS_Survey(filtreText.getText());
						} else if(tabOrigine.equals(Global.DATATYPE)) {
							filtrageListeDataSurvey(filtreText.getText());
						} else if (tabOrigine.equals(Global.PARAMETER)){
							filtrageListeData(filtreText.getText());
						} else if (tabOrigine.equals(Global.SHIP) ||
								tabOrigine.equals(Global.DATACENTRE) ||
								tabOrigine.equals(Global.REGION)) {
							filtrageListeBODC(filtreText.getText(), tabOrigine);
						}
					}
				}
			});

			shell.pack();
			shell.open();
			listeOuverte = 1;
		}

	}

	public static void creationTableData() {
		final Table table;
		int nbre;

		TableData.filtreTable = new Table(top, SWT.FULL_SELECTION);

		table = TableData.filtreTable;
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setBounds(new Rectangle(3, 45, 298, 237));

		nbre = TableData.listeFiltree.size();
		// création des colonnes...
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(290);
		// taille = 60
		tableColumn.setText(Messages.getString("FiltrageTableType.17")); //$NON-NLS-1$
		// TableColumn tableColumn1 = new TableColumn(table, SWT.NONE);
		// tableColumn1.setWidth(300);
		// tableColumn1.setText("Nom");
		// TableColumn tableColumn2 = new TableColumn(table, SWT.NONE);
		// tableColumn2.setWidth(100);
		// tableColumn2.setText("QC");

		for (int i = 0; i < nbre; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			item
					.setText(new String[] { TableData.listeFiltree.get(i)
							.getCode() });

		}
	}
	
	public static void creationTableEdmo() {
		final Table table;
		int nbre;

		EdmoHandler.filtreTable = new Table(top, SWT.FULL_SELECTION);

		table = EdmoHandler.filtreTable;
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setBounds(new Rectangle(3, 45, 298, 237));

		nbre = EdmoHandler.listeFiltree.size();
		// création des colonnes...
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(280);
		// taille = 60
		tableColumn.setText(Messages.getString("FiltrageTableType.17")); //$NON-NLS-1$
		
		for (int i = 0; i < nbre; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(EdmoHandler.listeFiltree.get(i));

		}
	}
	
	public static void creationTableBODC() {
		final Table table;
		int nbre;

		NemoLists.filtreTable = new Table(top, SWT.FULL_SELECTION);

		table = NemoLists.filtreTable;
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setBounds(new Rectangle(3, 45, 298, 237));

		nbre = NemoLists.listeFiltree.size();
		// création des colonnes...
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(280);
		// taille = 60
		tableColumn.setText(Messages.getString("FiltrageTableType.17")); //$NON-NLS-1$
		
		for (int i = 0; i < nbre; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(NemoLists.listeFiltree.get(i));

		}
	}

	public static void creationTableSurvey() {
		final Table table;
		int nbre;

		TableDataSurvey.filtreTable = new Table(top, SWT.FULL_SELECTION);
		;
		nbre = TableDataSurvey.listeFiltree.size();

		table = TableDataSurvey.filtreTable;
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setBounds(new Rectangle(3, 45, 298, 237));

		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(100);
		// taille = 60
		tableColumn.setText(Messages.getString("FiltrageTableType.18")); //$NON-NLS-1$
		TableColumn tableColumn1 = new TableColumn(table, SWT.NONE);
		tableColumn1.setWidth(100);
		tableColumn1.setText(Messages.getString("FiltrageTableType.19")); //$NON-NLS-1$
		TableColumn tableColumn2 = new TableColumn(table, SWT.NONE);
		tableColumn2.setWidth(100);
		tableColumn2.setText(Messages.getString("FiltrageTableType.20")); //$NON-NLS-1$

		for (int i = 0; i < nbre; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] {
					TableDataSurvey.listeFiltree.get(i).getCode(),
					"" + TableDataSurvey.listeFiltree.get(i).getNombre(), //$NON-NLS-1$
					TableDataSurvey.listeFiltree.get(i).getQc() });

		}
	}

	public static void filtrageListeData(String chaine) {

		ArrayList<TypeData> origine, filtree;
		int nbre;
		String regexp;

		filtree = new ArrayList<TypeData>();
		origine = TableData.typesWS;

		nbre = origine.size();
		// System.out.println("Nombre de types = "+nbre);

		// remplissage de la liste filtree
		for (int i = 0; i < nbre; i++) {

			regexp = "^.*" + chaine + ".*$"; //$NON-NLS-1$ //$NON-NLS-2$

			Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(origine.get(i).getCode());

			if (m.matches()) {

				// ajout dans la liste filtrée
				filtree.add(origine.get(i));
			}
		}

		TableData.listeFiltree = filtree;
		// affichage de la liste filtree

		razTable(TableData.filtreTable);
		creationTableData();

	}
	
	public static void filtrageListeEdmo(String chaine) {

		ArrayList<String> origine, filtree;
		int nbre;
		String regexp;

		filtree = new ArrayList<String>();
		origine = EdmoHandler.listeOrigine;
		
		
		nbre = origine.size();
		// System.out.println("Nombre de types = "+nbre);

		// remplissage de la liste filtree
		for (int i = 0; i < nbre; i++) {

			regexp = "^.*" + chaine + ".*$"; //$NON-NLS-1$ //$NON-NLS-2$

			Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(origine.get(i));

			if (m.matches()) {

				// ajout dans la liste filtrée
				filtree.add(origine.get(i));
			}
		}

		EdmoHandler.listeFiltree = filtree;
		// affichage de la liste filtree

		razTable(EdmoHandler.filtreTable);
		creationTableEdmo();

	}
	
	public static void filtrageListeBODC(String chaine, String liste) {

		ArrayList<String> origine, filtree;
		int nbre;
		String regexp;

		filtree = new ArrayList<String>();
		origine = new ArrayList<String>();
		if (liste.equals(Global.SHIP)) {
			origine = NemoLists.listeNavire;
		} else if (liste.equals(Global.DATACENTRE)) {
			origine = NemoLists.listeCentreDonnees;
		} else if (liste.equals(Global.REGION)) {
			origine = NemoLists.listeRegion;
		}		
		
		nbre = origine.size();
		// System.out.println("Nombre de types = "+nbre);

		// remplissage de la liste filtree
		for (int i = 0; i < nbre; i++) {

			regexp = "^.*" + chaine + ".*$"; //$NON-NLS-1$ //$NON-NLS-2$

			Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(origine.get(i));

			if (m.matches()) {

				// ajout dans la liste filtrée
				filtree.add(origine.get(i));
			}
		}

		NemoLists.listeFiltree = filtree;
		// affichage de la liste filtree

		razTable(NemoLists.filtreTable);
		creationTableBODC();
	}

	public static void filtrageListeDataSurvey(String chaine) {
		ArrayList<TypeSurvey> origine, filtree;
		int nbre;
		String regexp;

		filtree = new ArrayList<TypeSurvey>();
		origine = TableDataSurvey.typesWS;

		nbre = origine.size();
		// System.out.println("Nombre de types = "+nbre);

		// remplissage de la liste filtree
		for (int i = 0; i < nbre; i++) {

			regexp = "^.*" + chaine + ".*$"; //$NON-NLS-1$ //$NON-NLS-2$

			Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(origine.get(i).getCode());

			if (m.matches()) {

				// ajout dans la liste filtrée
				filtree.add(origine.get(i));
			}
		}

		TableDataSurvey.listeFiltree = filtree;
		// affichage de la liste filtree

		razTable(TableDataSurvey.filtreTable);
		creationTableSurvey();

	}

	public static void filtrageListeWS_Survey(String chaine) {
		ArrayList<String> origine, filtree;
		int nbre;
		String regexp;

		filtree = new ArrayList<String>();
		origine = TableWS_Survey.refsWS;

		nbre = origine.size();

		// remplissage de la liste filtree
		for (int i = 0; i < nbre; i++) {

			regexp = "^.*" + chaine + ".*$"; //$NON-NLS-1$ //$NON-NLS-2$

			Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(origine.get(i));

			if (m.matches()) {
				// System.out.println("On ajoute le type " + origine.get(i));
				// //$NON-NLS-1$

				// ajout dans la liste filtrée
				filtree.add(origine.get(i));
			}
		}

		TableWS_Survey.listeFiltree = filtree;
		// affichage de la liste filtree

		razTable(TableWS_Survey.filtreTable);
		creationTableWS_Survey();
	}

	public static void razTable(Table table) {
		// maj
		table.dispose();
	}

	public static void creationTableWS_Survey() {
		final Table table;
		int nbre;

		TableWS_Survey.filtreTable = new Table(top, SWT.FULL_SELECTION);
		// filtreTableData = new Table(top, SWT.FULL_SELECTION);
		table = TableWS_Survey.filtreTable;
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setBounds(new Rectangle(3, 45, 298, 237));

		nbre = TableWS_Survey.listeFiltree.size();
		// création des colonnes...
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(290);
		// taille = 60
		tableColumn.setText(Messages.getString("FiltrageTableType.29")); //$NON-NLS-1$
		// TableColumn tableColumn1 = new TableColumn(table, SWT.NONE);
		// tableColumn1.setWidth(300);
		// tableColumn1.setText("Nom");
		// TableColumn tableColumn2 = new TableColumn(table, SWT.NONE);
		// tableColumn2.setWidth(100);
		// tableColumn2.setText("QC");

		for (int i = 0; i < nbre; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] { TableWS_Survey.listeFiltree.get(i) });

		}

		// ajout listeners sur la table
		table.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {

			}
		});

	}

}

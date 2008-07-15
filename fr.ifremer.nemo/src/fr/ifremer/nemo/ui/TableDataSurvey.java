package fr.ifremer.nemo.ui;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.ws.WSDataType;

public class TableDataSurvey {

	public static Table tableTypeSurvey;

	public static Table filtreTable;

	public static int ligneSelec, colonneSelec, debut, fin, ligneBidon;
	
	public static int[] lignesSelec;

	// public static String[][]tableTypes;
	public static ArrayList<TypeSurvey> types, listeFiltree, typesWS;

	public static void go() {
		initTypes();
		creationTable();
	}

	public static void creationTable() {
		final Table table;

		ligneBidon = 0;
		debut = 0;
		fin = 0;

		TableDataSurvey.tableTypeSurvey = new Table(Drawing.sectionClientType,
				SWT.MULTI | SWT.FULL_SELECTION);

		table = TableDataSurvey.tableTypeSurvey;

		table.setHeaderVisible(true);
		table.setLinesVisible(true);
				
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(100);
		// taille = 60
		tableColumn.setText(Messages.getString("TableDataSurvey.1")); //$NON-NLS-1$

		TableColumn tableColumn1 = new TableColumn(table, SWT.NONE);
		tableColumn1.setWidth(100);
		tableColumn1.setText(Messages.getString("TableDataSurvey.2")); //$NON-NLS-1$

		TableColumn tableColumn2 = new TableColumn(table, SWT.NONE);
		tableColumn2.setWidth(50);
		tableColumn2.setText("QC"); //$NON-NLS-1$

		TableColumn tableColumn3 = new TableColumn(table, SWT.READ_ONLY);
		// mise à zero de la largeur de la colonne avec isSensor() pour ne pas
		// qu'on puisse la modifier!!
		tableColumn3.setWidth(50);
		tableColumn3.setText(Messages.getString("TableDataSurvey.11")); //$NON-NLS-1$

		initTable();
	}

	public static void initTypes() {

		// initialisation de la ArrayList des types des données à partir du
		// datamodel
		types = new ArrayList<TypeSurvey>();
		typesWS = new ArrayList<TypeSurvey>();

		int taille = DataModel.listeWSDataTypes.length;

		for (int i = 0; i < taille; i++) {
			WSDataType wsDatatype = new WSDataType();
			wsDatatype = DataModel.listeWSDataTypes[i];

			// new type
			TypeSurvey type = new TypeSurvey();

			// paramètres
			type.setCode(wsDatatype.getCode()
							+ " - " + wsDatatype.getLibelle()); //$NON-NLS-1$
			type.setQc(Messages.getString("TableDataSurvey.5")); //$NON-NLS-1$

			type.setSensor(wsDatatype.isSensor());
			// ajout dans liste
			typesWS.add(type);
		}

	}

	public static void initTable() {

		/*
		 * Remplir la table avec les tableItems avant de linker les editors !
		 * 
		 */

		final Table table;
		table = TableDataSurvey.tableTypeSurvey;

		// REMPLISSAGE temporaire de la table
		initTableData();
		// remplirTableData();

		// LINK des editors

		// accès direct aux valeurs des colonnes des items

		final TableEditor editor = new TableEditor(table);

		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;

		Point size = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		table.setSize(size);
		table.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {

				// calcul du numéro de ligne sélectionné

				Point pt = new Point(event.x, event.y);
				TableItem item = table.getItem(pt);
				// System.out.println("===> "+item.getText());
				if (item == null)
					return;

				// édition directe de l'élément sélectionné

				Rectangle clientArea = table.getClientArea();
				// Point pt = new Point(event.x, event.y);
				int index = table.getTopIndex();

				if (item == null)
					return;
				for (int i = 0; i < table.getColumnCount(); i++) {
					Rectangle rect = item.getBounds(i);
					// System.out.println("rect===> "+rect);
					// System.out.println("calcul num ligne===> "+i);
					if (rect.contains(pt)) {
						int indexTmp = table.indexOf(item);
						// System.out.println("LigneSelec =>" + indexTmp + "-
						// ColonneSelec =>" + i);
						ligneSelec = indexTmp;
						colonneSelec = i;
					}
				}

				while (index < table.getItemCount()) {
					boolean visible = false;
					final TableItem itemTmp = table.getItem(index);

					for (int i = 0; i < table.getColumnCount(); i++) {
						Rectangle rect = itemTmp.getBounds(i);
						if (rect.contains(pt)) {
							final int column = i;
							final Text text = new Text(table, SWT.NONE);
							Listener textListener = new Listener() {
								public void handleEvent(final Event e) {
									switch (e.type) {
									case SWT.FocusOut:
										itemTmp.setText(column, text.getText());
										text.dispose();
										break;
									case SWT.Traverse:
										switch (e.detail) {
										case SWT.TRAVERSE_RETURN:
											itemTmp.setText(column, text
													.getText());

										case SWT.TRAVERSE_ESCAPE:
											text.dispose();
											e.doit = false;
										}
										break;
									}
								}
							};
							text.addListener(SWT.FocusOut, textListener);
							text.addListener(SWT.Traverse, textListener);
							editor.setEditor(text, itemTmp, i);
							text.setText(itemTmp.getText(i));
							text.selectAll();

							// invalidation de l'étape quand modif
							EditSurvey.okApresReset = false;
							if (!ViewFile.tabItem1.isDisposed()) {
								ViewFile.tabItem1.setText(Messages
										.getString("EditSurvey.4"));
							}
							EditStation.okApresReset = false;
							ViewFile.tabItem2.setText(Messages
									.getString("EditStation.1"));
							EditData.okApresReset = false;
							ViewFile.tabItem3.setText(Messages
									.getString("EditData.1"));

							text.setFocus();
							return;
						}
						if (!visible && rect.intersects(clientArea)) {
							visible = true;
						}
					}
					if (!visible)
						return;
					index++;
				}
			}
		});

		// création du menu
		Menu menu = new Menu(table.getShell(), SWT.POP_UP);

		// MenuItem itemNew = new MenuItem(menu, SWT.NONE);
		// itemNew.setText(Messages.getString("TableDataSurvey.6"));

		MenuItem itemList = new MenuItem(menu, SWT.NONE);
		itemList.setText(Messages.getString("TableDataSurvey.7")); //$NON-NLS-1$

		MenuItem itemUp = new MenuItem(menu, SWT.NONE);
		itemUp.setText(Messages.getString("TableDataSurvey.8")); //$NON-NLS-1$

		MenuItem itemDown = new MenuItem(menu, SWT.NONE);
		itemDown.setText(Messages.getString("TableDataSurvey.9")); //$NON-NLS-1$
		// MenuItem tmp = new Separator();

		MenuItem itemDel = new MenuItem(menu, SWT.NONE);
		itemDel.setText(Messages.getString("TableDataSurvey.10")); //$NON-NLS-1$

		table.setMenu(menu);

		// ajouter des listeners sur les menu items ^^

		/*
		 * itemNew.addListener(SWT.Selection, new Listener() { public void
		 * handleEvent(Event e) { // System.out.println("nouveau type");
		 * nouvelElt(); } });
		 */
		itemList.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				// System.out.println("sélection liste");
				FiltrageTableType.go(FiltrageTableType._table.DataType.name());

			}
		});

		itemUp.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				// System.out.println("up");
				monterEltTableData(ligneSelec);
			}
		});
		itemDown.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				// System.out.println("down");
				descendreEltTableData(ligneSelec);
			}
		});
		itemDel.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				// System.out.println("suppression");
				// supprEltTableData(ligneSelec);
				supprEltTableData(table.getSelectionIndices());
				// remplirTableData();
			}
		});

		table.setLayout(new FillLayout());
		table.redraw();
	}

	public static void initTableData() {
		// création des TableItems à partir de la ArrayList types

		int nbre;
		nbre = types.size();

		Table table;
		table = TableDataSurvey.tableTypeSurvey;

		// nouvelle création des items
		for (int i = 0; i < nbre; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] { types.get(i).getCode(),
					"" + types.get(i).getNombre(), //$NON-NLS-1$
					types.get(i).getQc(), "" + types.get(i).getSensor() });

		}

	}

	public static void razTableData() {
		// effacement de tous les items de la table

		Table table;

		table = TableDataSurvey.tableTypeSurvey;

		table.dispose();

	}

	public static void nouvelElt() {
		// rajoute une ligne en dernière position de la table

		TypeSurvey type = new TypeSurvey();
		types.add(type);

		majTypeData();

		// razTableData
		razTableData();

		// refresh de la table
		creationTable();

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

	}

	public static void monterEltTableData(int indice) {
		// on monte d'un cran l'élément à la position indice
		// swap (indice-1) <-> indice
		// 
		
		// On teste si ce n'est pas le premier élément
		if (indice > 0) {
			int nbre;
			ArrayList<TypeSurvey> listeTempo;
			nbre = types.size();
			listeTempo = new ArrayList();

			// mise à jour préalable
			majTypeData();

			// parcours de la liste
			for (int i = 0; i < (indice - 1); i++) {
				listeTempo.add(types.get(i));
			}
			// swap des 2
			listeTempo.add(types.get(indice));
			listeTempo.add(types.get(indice - 1));

			// fin de parcours
			for (int i = (indice + 1); i < nbre; i++) {
				listeTempo.add(types.get(i));
			}

			// effacement de la liste principale avant re-remplissage
			for (int i = 0; i < nbre; i++) {
				types.remove(0);
			}

			// re-remplissage de la liste principale
			nbre = listeTempo.size();
			for (int i = 0; i < nbre; i++) {
				types.add(listeTempo.get(i));
			}

			// razTableData
			razTableData();

			// refresh de la table
			// initTable();
			creationTable();
		}
	}

	public static void descendreEltTableData(int indice) {
		// on descend d'un cran l'élément à la position indice
		// swap (indice-1) <-> indice

		int nbre;
		nbre = types.size();
		
		if (indice < nbre - 1) {
			ArrayList<TypeSurvey> listeTempo;
			listeTempo = new ArrayList();

			// mise à jour préalable
			majTypeData();

			// parcours de la liste
			for (int i = 0; i < indice; i++) {
				listeTempo.add(types.get(i));
			}
			// swap des 2
			listeTempo.add(types.get(indice + 1));
			listeTempo.add(types.get(indice));

			// fin de parcours
			for (int i = (indice + 2); i < nbre; i++) {
				listeTempo.add(types.get(i));
			}

			// effacement de la liste principale avant re-remplissage
			for (int i = 0; i < nbre; i++) {
				types.remove(0);
			}

			// re-remplissage de la liste principale
			nbre = listeTempo.size();
			for (int i = 0; i < nbre; i++) {
				types.add(listeTempo.get(i));
			}

			// razTableData
			razTableData();

			// refresh de la table
			creationTable();
		}
	}

	public static void supprEltTableData(int[] indice) {
		// suppression de la ligne sélectionnée
		int i;
		
		// d'abord mise à jour des données
		majTypeData();

		// on supprime l'élément à l'indice donné
		for (i = indice.length - 1; i > -1; i--) {
			types.remove(indice[i]);
		}

		// razTableData
		razTableData();

		// refresh de la table
		creationTable();

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

	}

	public static void majTypeData() {
		// on met à jour tous les types de données dans l'ArrayList global
		// à partir des valeurs de la table

		int nbre;
		// types = null;
		// types = new ArrayList<TypeSurvey>();
		try {
			if (TableDataSurvey.tableTypeSurvey != null) {
				nbre = TableDataSurvey.tableTypeSurvey.getItemCount();
				for (int i = 0; i < nbre; i++) {

					types.get(i).setCode(
							TableDataSurvey.tableTypeSurvey.getItem(i).getText(
									0));
					if (TableDataSurvey.tableTypeSurvey.getItem(i).getText(1)
							.matches("")) {
						types.get(i).setNombre(0);
					} else {
						types.get(i).setNombre(
								Integer.valueOf(TableDataSurvey.tableTypeSurvey
										.getItem(i).getText(1)));
					}
					types.get(i).setQc(
							TableDataSurvey.tableTypeSurvey.getItem(i).getText(
									2));
					types
							.get(i)
							.setSensor(
									Boolean
											.parseBoolean(TableDataSurvey.tableTypeSurvey
													.getItem(i).getText(3)));

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// System.out.println("maj types data : ok");

	}

	public static void ajouterCode(int indice) {
		// 

	}

}

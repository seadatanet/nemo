package fr.ifremer.nemo.ui;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.ws.WSParameter;

public class TableData {

	public static Table filtreTable;

	public static int ligneSelec, colonneSelec, debut, fin, ligneBidon;

	public static ArrayList<TypeData> types, listeFiltree, typesWS;

	public static void go() {
		initTypes();
		creationTable();
	}

	public static void creationTable() {

		final Table table;

		ligneBidon = 0;
		debut = 0;
		fin = 0;

		ViewFile.tableData = new Table(ViewFile.compositeData,
				SWT.FULL_SELECTION | SWT.H_SCROLL);
		
		table = ViewFile.tableData;

		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(110);
		tableColumn.setText(Messages.getString("TableData.0")); //$NON-NLS-1$

		TableColumn tableColumn1 = new TableColumn(table, SWT.NONE);
		tableColumn1.setWidth(90);
		tableColumn1.setText(Messages.getString("TableData.1")); //$NON-NLS-1$

		TableColumn tableColumn2 = new TableColumn(table, SWT.NONE);
		tableColumn2.setWidth(100);
		tableColumn2.setText(Messages.getString("TableData.2")); //$NON-NLS-1$
		tableColumn2.setAlignment(SWT.RIGHT);

		TableColumn tableColumn8 = new TableColumn(table, SWT.NONE);
		tableColumn8.setWidth(50);
		tableColumn8.setText(Messages.getString("TableData.3")); //$NON-NLS-1$
		tableColumn8.setAlignment(SWT.RIGHT);

		TableColumn tableColumn3 = new TableColumn(table, SWT.NONE);
		tableColumn3.setWidth(50);
		tableColumn3.setText(Messages.getString("TableData.4")); //$NON-NLS-1$
		tableColumn3.setAlignment(SWT.RIGHT);

		TableColumn tableColumn4 = new TableColumn(table, SWT.NONE);
		tableColumn4.setWidth(50);
		tableColumn4.setText(Messages.getString("TableData.5")); //$NON-NLS-1$
		tableColumn4.setAlignment(SWT.RIGHT);

		TableColumn tableColumn5 = new TableColumn(table, SWT.NONE);
		tableColumn5.setWidth(100);
		tableColumn5.setText(Messages.getString("TableData.6")); //$NON-NLS-1$
		tableColumn5.setAlignment(SWT.RIGHT);

		TableColumn tableColumn6 = new TableColumn(table, SWT.NONE);
		tableColumn6.setWidth(100);
		tableColumn6.setText(Messages.getString("TableData.7")); //$NON-NLS-1$
		tableColumn6.setAlignment(SWT.RIGHT);

		TableColumn tableColumn7 = new TableColumn(table, SWT.NONE);
		tableColumn7.setWidth(100);
		tableColumn7.setText(Messages.getString("TableData.8")); //$NON-NLS-1$
		tableColumn7.setAlignment(SWT.RIGHT);

		initTable();

		// agrandissement de la table
		ViewFile.compositeData.getShell().layout();

	}
	
	public static void initTypes() {

		// initialisation de la ArrayList des types des données à partir du

		types = new ArrayList<TypeData>();
		typesWS = new ArrayList<TypeData>();

		int taille = DataModel.listeWSParameters.length;

		for (int i = 0; i < taille; i++) {
			WSParameter wsParameter = new WSParameter();
			wsParameter = DataModel.listeWSParameters[i];

			// new type
			TypeData type = new TypeData();

			// paramètres
			type.setCode(wsParameter.getCode() + " - " + wsParameter.getName()); //$NON-NLS-1$
			type.setUnite(wsParameter.getUnit());
			type.setFormat(wsParameter.getFormat());
			type.setEntree("");
			type.setSortie(wsParameter.getDefaultValue());

			// type.setQc("N");

			// ajout dans liste
			typesWS.add(type);

			// ajout systématique de PRES
			if (wsParameter.getCode().matches("PRES")) { //$NON-NLS-1$

//				if ((DataModel.ouverture == false)
//						&& (!ViewFile.checkBoxTempo.getSelection())) {
				if ((DataModel.ouverture == false)
						&& (!ViewFile.radSerieTempo.getSelection() || !ViewFile.radTrajectoire.getSelection())) {	
					// pas de modèle chargé donc on rajoute PRES
					types.add(type);
				}

			}

		}

	}

	public static void initTable() {

		/*
		 * Remplir la table avec les tableItems avant de linker les editors !
		 * 
		 */

		final Table table;
		table = ViewFile.tableData;

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

				// TableItem item = table.getItem(pt);
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
		MenuItem itemInject = new MenuItem(menu, SWT.NONE);
		itemInject.setText(Messages.getString("TableData.11")); //$NON-NLS-1$
		MenuItem itemMaj = new MenuItem(menu, SWT.NONE);
		itemMaj.setText(Messages.getString("TableData.12")); //$NON-NLS-1$
		MenuItem itemList = new MenuItem(menu, SWT.NONE);
		itemList.setText(Messages.getString("TableData.13")); //$NON-NLS-1$
		// itemNew.setText(Messages.getString("TableData.14")); //$NON-NLS-1$
		MenuItem itemUp = new MenuItem(menu, SWT.NONE);
		itemUp.setText(Messages.getString("TableData.15")); //$NON-NLS-1$
		MenuItem itemDown = new MenuItem(menu, SWT.NONE);
		itemDown.setText(Messages.getString("TableData.16")); //$NON-NLS-1$
		// MenuItem tmp = new Separator();
		MenuItem itemDel = new MenuItem(menu, SWT.NONE);
		itemDel.setText(Messages.getString("TableData.17")); //$NON-NLS-1$
		table.setMenu(menu);

		// ajouter des listeners sur les menu items ^^

		itemInject.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				// System.out.println("injection debut/fin");

				Utils.injectData(ViewFile.styledTextData);

				// debut
				table.getItems()[ligneSelec].setText(4, "" + debut); //$NON-NLS-1$

				// fin
				table.getItems()[ligneSelec].setText(5, "" + fin); //$NON-NLS-1$

				majTypeData();
				// razTableData
				razTableData();

				// refresh de la table
				creationTable();

			}
		});

		itemMaj.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				// System.out.println("Maj champ test");
				majChampTest();

			}
		});

		itemList.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				// System.out.println("sélection liste");
				FiltrageTableType.go(Global.PARAMETER);

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
				supprEltTableData(ligneSelec);
				// remplirTableData();
			}
		});

		// agrandissement de la table
		ViewFile.compositeData.getParent().layout(true, true);
		table.setLayout(new FillLayout());
		table.redraw();
	}

	public static void initTableData() {
		// création des TableItems à partir de la ArrayList types

		int nbre;
		nbre = types.size();

		Table table;
		table = ViewFile.tableData;

		// nouvelle création des items
		for (int i = 0; i < nbre; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			item
					.setText(new String[] {
							types.get(i).getCode(),
							types.get(i).getUnite(),
							types.get(i).getConversion(),
							types.get(i).getTest(),
							"" + types.get(i).getDebut(), //$NON-NLS-1$
							"" + types.get(i).getFin(), types.get(i).getFormat(), types.get(i).getEntree(), types.get(i).getSortie() }); //$NON-NLS-1$

		}

	}

	public static void razTableData() {
		// effacement de tous les items de la table

		ViewFile.tableData.dispose();

	}

	public static void nouvelElt() {
		// rajoute une ligne en dernière position de la table

		TypeData type = new TypeData();
		types.add(type);

		// razTableData
		razTableData();

		// refresh de la table
		// initTable();
		creationTable();

	}

	public static void monterEltTableData(int indice) {
		// on monte d'un cran l'élément à la position indice
		// swap (indice-1) <-> indice
		// 

		Utils.messageErreur("");
		int nbre;
		ArrayList<TypeData> listeTempo;
		nbre = types.size();

		// si l'indice est 0, on ne fait rien
		if (indice == 0) {
		}

		else {

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
			creationTable();

		}

	}

	public static void descendreEltTableData(int indice) {
		// on descend d'un cran l'élément à la position indice
		// swap (indice-1) <-> indice

		int nbre;
		ArrayList<TypeData> listeTempo;
		nbre = types.size();

		// verif si c'est PRES
		TypeData type_a_Deplacer = types.get(indice);
		// split sur " - "
		Pattern p = Pattern.compile(" - ");
		String mots[] = p.split(type_a_Deplacer.getCode());
		String code = mots[0];

//		if (!code.matches("PRES") || ViewFile.checkBoxTempo.getSelection()) {
		if (!code.matches("PRES") || ViewFile.radSerieTempo.getSelection() || ViewFile.radTrajectoire.getSelection()) {
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

		} else {
			// si c'est PRES qu'on veut descendre alors c'est non
			Utils.messageErreurAppend(Messages.getString("TableData.19"));
		}
	}

	public static void supprEltTableData(int indice) {
		// suppression de la ligne sélectionnée

		// d'abord mise à jour des données
		majTypeData();

		// on supprime l'élément à l'indice donné
		// split sur " - "
		Pattern p = Pattern.compile(" - ");
		String mots[] = p.split(types.get(indice).getCode());
		String code = mots[0];

		// pas forcément de - dans le nom du type
//		if (!code.matches("PRES") || ViewFile.checkBoxTempo.getSelection()) {
		if (!code.matches("PRES") || ViewFile.radSerieTempo.getSelection() || ViewFile.radTrajectoire.getSelection()) {
			types.remove(indice);
		} else {
			Utils.messageErreur(Messages.getString("TableData.18"));
		}

		// razTableData
		razTableData();

		// refresh de la table
		creationTable();

	}

	public static void majChampTest() {

		// maj préventive de tout
		majTypeData();

		int nbre;
		String selection, ligne, resultat;
		double resultJEP;

		nbre = ViewFile.tableData.getItemCount();
		for (int i = 0; i < nbre; i++) {

			if ((!ViewFile.tableData.getItem(i).getText(4).matches("")) && (!ViewFile.tableData.getItem(i).getText(2).matches(""))) { //$NON-NLS-1$ //$NON-NLS-2$
				// on a sélectionné un début et une fin et il y a une conversion
				// à faire

				// on récupère le data
				ligne = Utils.lignesBrutes.get(Integer
						.valueOf(EditDesc.lignesEnteteFichier.getText())
						+ Integer.valueOf(EditDesc.lignesEnteteStation
								.getText())) + 1;
				selection = ligne.substring(
						Integer.valueOf(ViewFile.tableData.getItem(i)
								.getText(4)) - 1,
						Integer.valueOf(ViewFile.tableData.getItem(i)
								.getText(5))).trim();

				// on formate puis on reaffiche la liste
				try {
					if (selection.matches(ViewFile.tableData.getItem(i).getText(7))) {
						// entrée par défaut, le test est la valeur de sortie par
						// défaut formatée
						resultat = String.format(ViewFile.tableData.getItem(i)
								.getText(6), ViewFile.tableData.getItem(i).getText(
								8));
						
					} else {
						// valeur normale, on applique la formule puis formatage
	
						org.nfunk.jep.JEP parser = new org.nfunk.jep.JEP();
						parser.addStandardFunctions();
						parser.addStandardConstants();
						parser.addVariable("x", Float.valueOf(selection)); //$NON-NLS-1$
						parser.parseExpression(ViewFile.tableData.getItem(i)
								.getText(2));
						resultJEP = parser.getValue();
	
						resultat = String.format(ViewFile.tableData.getItem(i)
								.getText(6), resultJEP);
					}
				} catch (Exception e) {
					resultat = Messages.getString("TableData.20");
					e.printStackTrace();
				}

				types.get(i).setTest(resultat);

				// System.out.println("resultat conversion = "+resultat);
			}

			// System.out.println("maj types data : ok");
		}

		// razTableData
		razTableData();

		// refresh de la table
		creationTable();

	}

	public static void majTypeData() {
		// on met à jour tous les types de données dans l'ArrayList global
		// à partir des valeurs de la table

		int nbre;

		nbre = ViewFile.tableData.getItemCount();
		for (int i = 0; i < nbre; i++) {

			// System.out.println("CODE de l'item ##########
			// ==>"+ViewFile.tableData.getItem(i).getText(0));
			// System.out.println("unite de l'item
			// ==>"+ViewFile.tableData.getItem(i).getText(1));
			// System.out.println("conversion de l'item
			// ==>"+ViewFile.tableData.getItem(i).getText(2));
			// System.out.println("test de l'item
			// ==>"+ViewFile.tableData.getItem(i).getText(3));
			// System.out.println("debut de l'item
			// ==>"+ViewFile.tableData.getItem(i).getText(4));
			// System.out.println("fin de l'item
			// ==>"+ViewFile.tableData.getItem(i).getText(5));
			// System.out.println("format de l'item
			// ==>"+ViewFile.tableData.getItem(i).getText(6));
			// System.out.println("entree de l'item
			// ==>"+ViewFile.tableData.getItem(i).getText(7));
			// System.out.println("sortie de l'item
			// ==>"+ViewFile.tableData.getItem(i).getText(8));

			types.get(i).setCode(ViewFile.tableData.getItem(i).getText(0));
			types.get(i).setUnite(ViewFile.tableData.getItem(i).getText(1));
			types.get(i)
					.setConversion(ViewFile.tableData.getItem(i).getText(2));
			types.get(i).setTest(ViewFile.tableData.getItem(i).getText(3));
			types.get(i).setDebut(
					Integer.valueOf(ViewFile.tableData.getItem(i).getText(4)));
			types.get(i).setFin(
					Integer.valueOf(ViewFile.tableData.getItem(i).getText(5)));
			types.get(i).setFormat(ViewFile.tableData.getItem(i).getText(6));
			types.get(i).setEntree(ViewFile.tableData.getItem(i).getText(7));
			types.get(i).setSortie(ViewFile.tableData.getItem(i).getText(8));
		}

		// System.out.println("maj types data : ok");

	}

	public static void ajouterCode(int indice) {
		//

	}

}

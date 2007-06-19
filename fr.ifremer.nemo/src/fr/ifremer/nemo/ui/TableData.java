package fr.ifremer.nemo.ui;





import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;

import fr.ifremer.nemo.ws.WSDataType;
import fr.ifremer.nemo.ws.WSParameter;




public class TableData {
	
	public static Table filtreTable;
	public static int ligneSelec, colonneSelec, debut, fin, ligneBidon;
	//public static String[][]tableTypes;
	public static ArrayList <TypeData> types, listeFiltree, typesWS;
	

public static void go(){
	initTypes();
	creationTable();
	
}
	
public static void creationTable(){
	
	  
	 final Table table;
	 
	 ligneBidon = 0;
	 debut = 0;
	 fin = 0;
	 
//	 ViewFile.compositeData.dispose();
//	 ViewFile.compositeData = new Composite(ViewFile.composite4, SWT.NONE);
//	 ViewFile.compositeData.setLayout(new FillLayout());
	 ViewFile.tableData = new Table(ViewFile.compositeData, SWT.FULL_SELECTION | SWT.H_SCROLL); 
	 
	 table =  ViewFile.tableData;
	  
	   
	  table.setHeaderVisible(true);
	  table.setLinesVisible(true);  
	  TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(110);
		//taille = 60
		tableColumn.setText("CODE");
		TableColumn tableColumn1 = new TableColumn(table, SWT.NONE);
		tableColumn1.setWidth(90);
		tableColumn1.setText("Unité");
		TableColumn tableColumn2 = new TableColumn(table, SWT.NONE);
		tableColumn2.setWidth(100);
		tableColumn2.setText("Conversion");
		TableColumn tableColumn8 = new TableColumn(table, SWT.NONE);
		tableColumn8.setWidth(50);
		tableColumn8.setText("Test");
		TableColumn tableColumn3 = new TableColumn(table, SWT.NONE);
		tableColumn3.setWidth(50);
		tableColumn3.setText("Début");
		TableColumn tableColumn4 = new TableColumn(table, SWT.NONE);
		tableColumn4.setWidth(50);
		tableColumn4.setText("Fin");
		TableColumn tableColumn5 = new TableColumn(table, SWT.NONE);
		tableColumn5.setWidth(100);
		tableColumn5.setText("Format");
		TableColumn tableColumn6 = new TableColumn(table, SWT.NONE);
		tableColumn6.setWidth(100);
		tableColumn6.setText("Entrée");
		TableColumn tableColumn7 = new TableColumn(table, SWT.NONE);
		tableColumn7.setWidth(100);
		tableColumn7.setText("Sortie");
	
	initTable();
	
	//ViewFile.compositeData.pack(true);
	//ViewFile.tableData.getShell().pack();
	
//	Shell shell = new Shell();
//	table.setParent(shell);
	System.out.println("table cachée => ok");
	
//	table.setParent(ViewFile.compositeData);
	
	
	
//	ViewFile.compositeData.setVisible(false);
	
//	ViewFile.compositeData.update();
//	ViewFile.compositeData.pack();
//	ViewFile.compositeData.redraw();
//	
	
//	ViewFile.compositeData.setSize(ViewFile.compositeData.getSize());
	//ViewFile.compositeData.setSize(1000, 1000);
	//ViewFile.compositeData.setVisible(true);
	//ViewFile.composite4.re
	
	
//	ViewFile.compositeData.getShell().setRedraw(true);
//	ViewFile.compositeData.getShell().redraw();
	ViewFile.compositeData.getShell().layout();

	//ViewFile.compositeData.getShell().;
	
	System.out.println("table visible => ok");
	
	//ViewFile.top.getv
	//ViewFile.top.getParent().getDisplay().update();
}	
	
	
public static void initTypes(){
	
	//initialisation de la ArrayList des types des données à partir du datamodel
	types = new ArrayList<TypeData>();
	typesWS = new ArrayList<TypeData>();
	
	//temporairement création manuelle
//	TypeData type1 = new TypeData("H2O","m^3","", "","","","9999","9999", "");
//	TypeData type2 = new TypeData("H5N1","mmol/l","", "", "","","9999","999", "");
//	TypeData type3 = new TypeData("CO2","m/l","","","","","999","99", "");
//	TypeData type4 = new TypeData("B8WXT12","unité","","","","","9999","-99999", "");
//	
//	
//	types.add(type1);
//	types.add(type2);
//	types.add(type3);
//	types.add(type4);
	
	
	int taille = DataModel.listeWSParameters.length;
	
	for (int i =0; i<taille; i++){
		WSParameter wsParameter = new WSParameter();
		wsParameter = DataModel.listeWSParameters[i];
		
		//new type
		TypeData type = new TypeData();

		//paramètres
		type.setCode(wsParameter.getCode()+" - "+wsParameter.getName());
		type.setUnite(wsParameter.getUnit());
		type.setFormat(wsParameter.getFormat());
		type.setEntree(wsParameter.getDefaultValue());
		type.setSortie(wsParameter.getDefaultValue());
		
		//type.setQc("N");
		
				
		//ajout dans liste
		typesWS.add(type);
		
		
		//ajout systématique de PRES
		if (wsParameter.getCode().matches("PRES")){
						
			if ((DataModel.ouverture == false)&&(!ViewFile.checkBoxTempo.getSelection())){
				//pas de modèle chargé donc on rajoute PRES
				types.add(type);
			}	
			
		}
		
		//test pour ajout de YEAR, MNTH, DAYX, TIME
		if (ViewFile.checkBoxTempo.getSelection() && ViewFile.textTempoHeureRef.getText().matches("")){
			//si serie temporelle et que pas de date/heure de ref alors on ajoute les 4 paramètres
			if (wsParameter.getCode().matches("YEAR")||wsParameter.getCode().matches("MNTH")||wsParameter.getCode().matches("DAYX")||
					wsParameter.getCode().matches("TIME")){
				
				if (DataModel.ouverture == false){
					//pas de modèle chargé donc on rajoute pression de base
					types.add(type);
				}	
				
			}
			
			
			
		}
		
				
		
	}
	
	

	
	
	
}
	
public static void initTable(){
	 
	  /*
	   * Remplir la table avec les tableItems avant de linker les editors !
	   *  
	   */
	  
	final Table table;
	table = ViewFile.tableData;
	  
	/*
		//menu
		MenuManager popupMenu = new MenuManager();
	    IAction newRowAction = new NewRowAction();
	    popupMenu.add(newRowAction);
	    Menu menu = popupMenu.createContextMenu(table);
	    table.setMenu(menu);
	*/
		
		//REMPLISSAGE temporaire de la table
		initTableData();
		//remplirTableData();
		
		//LINK des editors
		
	    //accès direct aux valeurs des colonnes des items
	   // table.getItems()[2].setText(4, "ça marche super!");
	    
		final TableEditor editor = new TableEditor(table);
		  
	    
	    editor.horizontalAlignment = SWT.LEFT;
	    editor.grabHorizontal = true;
	
	    
	    Point size = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
	    table.setSize(size);
	    table.addListener(SWT.MouseDown, new Listener() {
		      public void handleEvent(Event event) {
		    	  
		  	  
		    	//calcul du numéro de ligne sélectionné
		    	  
		    	  Point pt = new Point(event.x, event.y);
		          TableItem item = table.getItem(pt);
		          //System.out.println("===> "+item.getText());
		          if (item == null)
		            return;
		          for (int i = 0; i < table.getColumnCount(); i++) {
		            Rectangle rect = item.getBounds(i);
		            System.out.println("calcul num ligne===> "+i);
		            if (rect.contains(pt)) {
		              int index = table.indexOf(item);
		              System.out.println("Elément " + index + "-" + i);
		            }
		          }
		    	  
		     
		    	  
		    	//édition directe de l'élément sélectionné  
		    	  
		        Rectangle clientArea = table.getClientArea();
		      // Point pt = new Point(event.x, event.y);
		        int index = table.getTopIndex();
		        
//		        System.out.println("index= " + index + "table.getItemCount()= " + table.getItemCount());
//		        table.getItem(6).setText("coucou");
//		        System.out.println("items de la table [6] ==>"+ table.getItems());
		       
		        
		        //TableItem item = table.getItem(pt);
		        if (item == null)
		            return;
		        for (int i = 0; i < table.getColumnCount(); i++) {
		            Rectangle rect = item.getBounds(i);
		            System.out.println("rect===> "+rect);
		            System.out.println("calcul num ligne===> "+i);
		            if (rect.contains(pt)) {
		              int indexTmp = table.indexOf(item);
		              System.out.println("LigneSelec =>" + indexTmp + "- ColonneSelec =>" + i);
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
		                      itemTmp
		                          .setText(column, text
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
		
		
		//création du menu
		Menu menu = new Menu(table.getShell(), SWT.POP_UP);
		MenuItem itemInject = new MenuItem(menu, SWT.NONE);
	    itemInject.setText("Injecter début/fin");
	    MenuItem itemMaj = new MenuItem(menu, SWT.NONE);
	    itemMaj.setText("Maj. champ test");
	    MenuItem itemList = new MenuItem(menu, SWT.NONE);
	    itemList.setText("Sélection liste");
		MenuItem itemNew = new MenuItem(menu, SWT.NONE);
	    itemNew.setText("Nouveau type");
	    MenuItem itemUp = new MenuItem(menu, SWT.NONE);
	    itemUp.setText("Monter type sélectionné");
	    MenuItem itemDown = new MenuItem(menu, SWT.NONE);
	    itemDown.setText("Descendre type sélectionné");
	    //MenuItem tmp = new Separator();
	    MenuItem itemDel = new MenuItem(menu, SWT.NONE);
	    itemDel.setText("Supprimer type sélectionné");
		table.setMenu(menu);
		
		//ajouter des listeners sur les menu items ^^
		
		final MenuItem item = itemInject;
		
		itemInject.addListener(SWT.Selection, new Listener() {
	          public void handleEvent(Event e) {
	            System.out.println("injection debut/fin");
	           
	           
	            Utils.injectData(ViewFile.styledTextData);
	            	
	            //debut
	            table.getItems()[ligneSelec].setText(4, ""+debut);
	            
	            //fin
	            table.getItems()[ligneSelec].setText(5, ""+fin);
	            
	            majTypeData();
	            //razTableData
	   		 razTableData();
	   		 
	   		 //refresh de la table
	   		 //initTable();
	   		 creationTable();
	            
	          } 
	        });
		
		
		itemMaj.addListener(SWT.Selection, new Listener() {
	          public void handleEvent(Event e) {
	            System.out.println("Maj champ test");
	            majChampTest();
	            
	          }
	        });
		
		
		itemList.addListener(SWT.Selection, new Listener() {
	          public void handleEvent(Event e) {
	            System.out.println("sélection liste");
	            FiltrageTableType.go();
	            
	          }
	        });
		
		itemNew.addListener(SWT.Selection, new Listener() {
	          public void handleEvent(Event e) {
	            System.out.println("nouveau type");
	            nouvelElt();
	          }
	        });
		itemUp.addListener(SWT.Selection, new Listener() {
	          public void handleEvent(Event e) {
	            System.out.println("up");
	            monterEltTableData(ligneSelec);
	          }
	        });
		itemDown.addListener(SWT.Selection, new Listener() {
	          public void handleEvent(Event e) {
	            System.out.println("down");
	            descendreEltTableData(ligneSelec);
	          }
	        });
		itemDel.addListener(SWT.Selection, new Listener() {
	          public void handleEvent(Event e) {
	            System.out.println("suppression");
	            supprEltTableData(ligneSelec);
	            //remplirTableData();
	          }
	        });
		

	ViewFile.compositeData.getParent().layout(true, true);
	table.setLayout(new FillLayout());
	table.redraw();
  }	
	

  public static void initTableData(){
	  //création des TableItems à partir de la ArrayList types
	  
	  int nbre;
	  nbre = types.size();
	  
	  Table table;
	  table = ViewFile.tableData;
	  
	  //nouvelle création des items
	  for (int i = 0; i < nbre; i++) {
	      TableItem item = new TableItem(table, SWT.NONE);
	      item.setText(new String[] { types.get(i).getCode(), types.get(i).getUnite() , types.get(i).getConversion(),types.get(i).getTest() ,""+types.get(i).getDebut(),
	    		  ""+types.get(i).getFin(), types.get(i).getFormat(), types.get(i).getEntree(), types.get(i).getSortie() });
	       
	    }  
	
  }

  
  
 public static void razTableData(){
	 //effacement de tous les items de la table
	 
	  Table table;
	 
	  table = ViewFile.tableData;
	/*  
	  //effacement de l'intérieur de la table
	  items = table.getItems();
	  
	  int nbre = items.length;
	  
	  //on supprime tous les items
	  for (int i=0; i<nbre; i++){
		  items[i].dispose();
	  }
	 */
	 
	  
	 ViewFile.tableData.dispose();
	 
	 //effacement et recréation du composite4 qui porte tableData
//	 ViewFile.compositeData.dispose();
//	 ViewFile.compositeData = new Composite(ViewFile.composite4, SWT.NONE);
//	 ViewFile.compositeData.setLayout(new FillLayout());
//	 ViewFile.compositeData.pack();
	 
 } 
  
  public static void nouvelElt(){
	  //rajoute une ligne en dernière position de la table
	  
	  TypeData type = new TypeData();
	  types.add(type);
	  
	  //razTableData
		 razTableData();
		 
		 //refresh de la table
		 //initTable();
		 creationTable();
	  
	  
	  
  }
 
 public static void monterEltTableData(int indice){
	 //on monte d'un cran l'élément à la position indice
	 //swap (indice-1) <-> indice
	 // 
	 int nbre;
	 ArrayList <TypeData> listeTempo;
	 TypeData typeTempo;
	 Table table, tableTempo;
	 //TableItem[] items, itemsTempo;
	 
	 nbre = types.size();
	 listeTempo = new ArrayList();
	 
	 //mise à jour préalable
	 majTypeData();
	 
	 //parcours de la liste
	 for (int i=0; i<(indice-1);i++){
		listeTempo.add(types.get(i));
		  }
	 //swap des 2
	 listeTempo.add(types.get(indice));
	 listeTempo.add(types.get(indice-1));
	 
	 //fin de parcours
	 for (int i=(indice+1); i<nbre;i++){
			listeTempo.add(types.get(i));
			  }
	 
	 
	 //effacement de la liste principale avant re-remplissage
	 for (int i =0; i<nbre; i++)
	 {
		 types.remove(0);
	 }
	 
	 //re-remplissage de la liste principale
	 nbre = listeTempo.size();
	 for (int i =0; i<nbre; i++)
	 {
		 types.add(listeTempo.get(i));
	 }
	 
	 
	 //razTableData
	 razTableData();
	 
	 //refresh de la table
	 //initTable();
	 creationTable();
	 
	
	  
	 
 }
 
 
 
 public static void descendreEltTableData(int indice){
	 //on descend d'un cran l'élément à la position indice
	 //swap (indice-1) <-> indice
	
	 int nbre;
	 ArrayList <TypeData> listeTempo;
	 TypeData typeTempo;
	 Table table, tableTempo;
	 //TableItem[] items, itemsTempo;
	 
	 nbre = types.size();
	 listeTempo = new ArrayList();
	 
	 //mise à jour préalable
	 majTypeData();
	 
	 //parcours de la liste
	 for (int i=0; i<indice;i++){
		listeTempo.add(types.get(i));
		  }
	 //swap des 2
	 listeTempo.add(types.get(indice+1));
	 listeTempo.add(types.get(indice));
	 
	 //fin de parcours
	 for (int i=(indice+2); i<nbre;i++){
			listeTempo.add(types.get(i));
			  }
	 
	 
	 //effacement de la liste principale avant re-remplissage
	 for (int i =0; i<nbre; i++)
	 {
		 types.remove(0);
	 }
	 
	 //re-remplissage de la liste principale
	 nbre = listeTempo.size();
	 for (int i =0; i<nbre; i++)
	 {
		 types.add(listeTempo.get(i));
	 }
	 
	 
	 //razTableData
	 razTableData();
	 
	 //refresh de la table
	 //initTable();
	 creationTable();
	 
	 
 }
 
 
 public static void supprEltTableData(int indice){
	 //suppression de la ligne sélectionnée
	 
	 //d'abord mise à jour des données
	 majTypeData();
	 
	 //on supprime l'élément à l'indice donné
	 types.remove(indice);
	 
	 //razTableData
	 razTableData();
	 
	 //refresh de la table
	 //initTable();
	 creationTable();
	 
 }
 
 
 
 public static void majChampTest(){
	 
	 //maj préventive de tout
	 majTypeData();
	 
	 int nbre;
	 String selection, ligne, resultat;
	 double resultJEP;
	
	 
	 nbre = ViewFile.tableData.getItemCount();
	 for (int i = 0; i <nbre; i++){
		 
		 if (!ViewFile.tableData.getItem(i).getText(4).matches("")){
		//on a sélectionné un début et une fin
			 
		//on récupère le data
	ligne = Utils.lignesBrutes.get(Integer.valueOf(EditDesc.lignesEnteteFichier.getText())+Integer.valueOf(EditDesc.lignesEnteteStation.getText()))+1;
	selection = ligne.trim().substring(Integer.valueOf(ViewFile.tableData.getItem(i).getText(4)), Integer.valueOf(ViewFile.tableData.getItem(i).getText(5)));
	
		//on formate puis on reaffiche la liste
	if (selection.matches(ViewFile.tableData.getItem(i).getText(7))){
		//entrée par défaut, le test est la valeur de sortie par défaut formatée
		resultat = String.format(ViewFile.tableData.getItem(i).getText(6), ViewFile.tableData.getItem(i).getText(8));
		}
	else {
		//valeur normale, on applique la formule puis formatage
		
	org.nfunk.jep.JEP parser = new org.nfunk.jep.JEP();
	parser.addStandardFunctions();
	parser.addStandardConstants();
	parser.addVariable("x", Float.valueOf(selection));
	parser.parseExpression(ViewFile.tableData.getItem(i).getText(2));
	resultJEP = parser.getValue();
	
	resultat = String.format(ViewFile.tableData.getItem(i).getText(6), resultJEP);}
	
	types.get(i).setTest(resultat);
	
		 }
	  
	 System.out.println("maj types data : ok");
	 }
	 
	 //razTableData
	 razTableData();
	 
	 //refresh de la table
	 //initTable();
	 creationTable();
	 
	
	 
 }
  
  public static void majTypeData(){
	  //on met à jour tous les types de données dans l'ArrayList global
	  //à partir des valeurs de la table
	  // table.getItems()[2].setText(4, "ça marche super!");
	 	 
	 int nbre;
	 
	 nbre = ViewFile.tableData.getItemCount();
	 for (int i = 0; i <nbre; i++){
		 
		 
		 System.out.println("CODE de l'item ########## ==>"+ViewFile.tableData.getItem(i).getText(0));
		 System.out.println("unite de l'item ==>"+ViewFile.tableData.getItem(i).getText(1));
		 System.out.println("conversion de l'item ==>"+ViewFile.tableData.getItem(i).getText(2));
		 System.out.println("debut de l'item ==>"+ViewFile.tableData.getItem(i).getText(3));
		 System.out.println("fin de l'item ==>"+ViewFile.tableData.getItem(i).getText(4));
		 System.out.println("format de l'item ==>"+ViewFile.tableData.getItem(i).getText(5));
		 System.out.println("entree de l'item ==>"+ViewFile.tableData.getItem(i).getText(6));
		 System.out.println("sortie de l'item ==>"+ViewFile.tableData.getItem(i).getText(7));
		 
		types.get(i).setCode(ViewFile.tableData.getItem(i).getText(0));
		types.get(i).setUnite(ViewFile.tableData.getItem(i).getText(1));
		types.get(i).setConversion(ViewFile.tableData.getItem(i).getText(2));
		types.get(i).setTest(ViewFile.tableData.getItem(i).getText(3));
		types.get(i).setDebut(Integer.valueOf(ViewFile.tableData.getItem(i).getText(4)));
		types.get(i).setFin(Integer.valueOf(ViewFile.tableData.getItem(i).getText(5)));
		types.get(i).setFormat(ViewFile.tableData.getItem(i).getText(6));
		types.get(i).setEntree(ViewFile.tableData.getItem(i).getText(7));
		types.get(i).setSortie(ViewFile.tableData.getItem(i).getText(8));
		 }
	  
	 System.out.println("maj types data : ok");
	  
  } 
  
  
  public static void ajouterCode(int indice){
	  //TODO à compléter
	  
	  
  }
  
  
  
  
  
}

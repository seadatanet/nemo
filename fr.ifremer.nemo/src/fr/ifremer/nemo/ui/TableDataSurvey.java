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

import fr.ifremer.nemo.ws.WSDataType;

public class TableDataSurvey {

	public static Table tableTypeSurvey, filtreTable;
	public static int ligneSelec, colonneSelec, debut, fin, ligneBidon;
	//public static String[][]tableTypes;
	public static ArrayList <TypeSurvey> types, listeFiltree, typesWS;

public static void go(){
	initTypes();
	creationTable();
	
}
	
public static void creationTable(){
	
	  
	 final Table table;
	 
	 ligneBidon = 0;
	 debut = 0;
	 fin = 0;
	 
	 TableDataSurvey.tableTypeSurvey = new Table(Drawing.sectionClientType, SWT.FULL_SELECTION | SWT.FILL); 
	 
	 
	 
	 table =  TableDataSurvey.tableTypeSurvey;
		System.out.println("=========creationTable()=======");
	   
	  table.setHeaderVisible(true);
	  table.setLinesVisible(true);  
	  TableColumn tableColumn = new TableColumn(table, SWT.NONE);
	  tableColumn.setWidth(100);
		//taille = 60
		tableColumn.setText("CODE");
		TableColumn tableColumn1 = new TableColumn(table, SWT.NONE);
		tableColumn1.setWidth(100);
		tableColumn1.setText("Nombre");
		TableColumn tableColumn2 = new TableColumn(table, SWT.NONE);
		tableColumn2.setWidth(100);
		tableColumn2.setText("QC");
		
	
	initTable();
	
}	
	
	
public static void initTypes(){
	
	System.out.println("=========initTypes()=======");
	
	//initialisation de la ArrayList des types des données à partir du datamodel
	types = new ArrayList<TypeSurvey>();
	typesWS = new ArrayList<TypeSurvey>();

	
	//temporairement création manuelle
//	TypeSurvey type1 = new TypeSurvey("H2O",35, "P");
//	TypeSurvey type2 = new TypeSurvey("H5N1",12,"N");
//	TypeSurvey type3 = new TypeSurvey("BCWXTZ",15,"N");
//	TypeSurvey type4 = new TypeSurvey("X12",150,"X");
//	
//	
//	types.add(type1);
//	types.add(type2);
//	types.add(type3);
//	types.add(type4);
	
	int taille = DataModel.listeWSDataTypes.length;
	
	for (int i =0; i<taille; i++){
		WSDataType wsDatatype = new WSDataType();
		wsDatatype = DataModel.listeWSDataTypes[i];
		
		//new type
		TypeSurvey type = new TypeSurvey();

		//paramètres
		type.setCode(wsDatatype.getCode()+" - "+wsDatatype.getLibelle());
		type.setQc("N");
			
		//ajout dans liste
		typesWS.add(type);
	}
	

	
	
}
	
public static void initTable(){
	 
	  /*
	   * Remplir la table avec les tableItems avant de linker les editors !
	   *  
	   */
	  
	final Table table;
	table = TableDataSurvey.tableTypeSurvey;
	  
	System.out.println("=========initTable()=======");
	
	
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
				
	    MenuItem itemNew = new MenuItem(menu, SWT.NONE);
	    itemNew.setText("Nouveau type");
	   
	    MenuItem itemList = new MenuItem(menu, SWT.NONE);
	    itemList.setText("Sélection liste");
	    
	    MenuItem itemUp = new MenuItem(menu, SWT.NONE);
	    itemUp.setText("Monter type sélectionné");
	    
	    MenuItem itemDown = new MenuItem(menu, SWT.NONE);
	    itemDown.setText("Descendre type sélectionné");
	    //MenuItem tmp = new Separator();
	    
	    MenuItem itemDel = new MenuItem(menu, SWT.NONE);
	    itemDel.setText("Supprimer type sélectionné");
		table.setMenu(menu);
		
		//ajouter des listeners sur les menu items ^^
		
	
		itemNew.addListener(SWT.Selection, new Listener() {
	          public void handleEvent(Event e) {
	            System.out.println("nouveau type");
	            nouvelElt();
	          
	          }
	        });
		
		itemList.addListener(SWT.Selection, new Listener() {
	          public void handleEvent(Event e) {
	            System.out.println("sélection liste");
	            FiltrageTableType.go();
	            
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
		

//	ViewFile.compositeData.redraw();
//	ViewFile.compositeData.getDisplay().update();
	table.setLayout(new FillLayout());
	table.redraw();
  }	
	

  public static void initTableData(){
	  //création des TableItems à partir de la ArrayList types
	  
	  int nbre;
	  nbre = types.size();
	
		System.out.println("=========initTableData()=======");
	  
	  Table table;
	  table = TableDataSurvey.tableTypeSurvey;
	  
	  //nouvelle création des items
	  for (int i = 0; i < nbre; i++) {
	      TableItem item = new TableItem(table, SWT.NONE);
	      item.setText(new String[] { types.get(i).getCode(), ""+types.get(i).getNombre(),
	    		  types.get(i).getQc() });
	       
	    }  
	
  }

  
  
 public static void razTableData(){
	 //effacement de tous les items de la table
	 
	  Table table;
	 
	  table = TableDataSurvey.tableTypeSurvey;
	/*  
	  //effacement de l'intérieur de la table
	  items = table.getItems();
	  
	  int nbre = items.length;
	  
	  //on supprime tous les items
	  for (int i=0; i<nbre; i++){
		  items[i].dispose();
	  }
	 */
	 
	 table.dispose();
	 
	 
 } 
  
  public static void nouvelElt(){
	  //rajoute une ligne en dernière position de la table
	  
	  TypeSurvey type = new TypeSurvey();
	  types.add(type);
	  
	  majTypeData();
	  
	  //razTableData
		 razTableData();
		 
		 //refresh de la table
		 //initTable();
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
 
 public static void monterEltTableData(int indice){
	 //on monte d'un cran l'élément à la position indice
	 //swap (indice-1) <-> indice
	 // 
	 int nbre;
	 ArrayList <TypeSurvey> listeTempo;
	 TypeSurvey typeTempo;
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
	 ArrayList <TypeSurvey> listeTempo;
	 TypeSurvey typeTempo;
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
 
  
  public static void majTypeData(){
	  //on met à jour tous les types de données dans l'ArrayList global
	  //à partir des valeurs de la table
	  // table.getItems()[2].setText(4, "ça marche super!");
	 	 
	 int nbre;
	 
	 nbre = TableDataSurvey.tableTypeSurvey.getItemCount();
	 for (int i = 0; i <nbre; i++){
		 

		 
		types.get(i).setCode(TableDataSurvey.tableTypeSurvey.getItem(i).getText(0));
		types.get(i).setNombre(Integer.valueOf(TableDataSurvey.tableTypeSurvey.getItem(i).getText(1)));
		types.get(i).setQc(TableDataSurvey.tableTypeSurvey.getItem(i).getText(2));
		
		 }
	  
	 System.out.println("maj types data : ok");
	  
  } 	

  
    
  
  public static void ajouterCode(int indice){
	  //TODO à compléter
	  
	  
  }
	
}

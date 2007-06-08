package fr.ifremer.nemo.ui;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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

import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.ws.WSSurveyDescription;

public class FiltrageTableType {
	
	public static int listeOuverte;
	public static Composite top;
	public static Table filtreTableData, filtreTableSurvey;
	public static Text filtreText;
	public static Shell shell;
	public static Label labelText;
	public static Button filtreButtonSelectionner;
	public static Button filtreButtonCancel;
	

	
public static void go(){
//	raz message d'erreur
	
	Utils.messageErreur("");
	
	
	if (listeOuverte == 1){Utils.messageErreur("Vous avez déjà ouvert une liste de sélection");}
	else{
	
	shell = new Shell(Application.display, SWT.BORDER | SWT.TITLE |SWT.APPLICATION_MODAL | SWT.SYSTEM_MODAL);
	shell.setSize(300, 500);
	
	
if (ViewFile.affiche == 1){
	if (EditSurvey.ws){
//		onglet survey et selection ref. par WS
		shell.setText("Sélection de la référence campagne");
	}
	else{
		shell.setText("Sélection des Types de Données");}
	
if (ViewFile.affiche ==3){
	
	
		shell.setText("Sélection des Paramètres mesurés");
	}
}
	//else {}
	
	
	shell.setLayout(new FillLayout());
	
	top = new Composite(shell, SWT.NONE);
	top.setLayout(null);
	
//	init des listes filtrées survey + data
	
	TableDataSurvey.listeFiltree = new ArrayList <TypeSurvey>();
	
	//init de la liste des references campagne à partir des listesWS
	TableWS_Survey.surveysWS = NemoLists.getSurveys();
		
		//remplissage des noms + ref
	//liste filtree initialisée à surveysWS au depart
	TableWS_Survey.listeFiltree = new ArrayList<String>();
	TableWS_Survey.refsWS = new ArrayList<String>();
	for (int i=0;i<TableWS_Survey.surveysWS.length;i++){
		TableWS_Survey.refsWS.add(TableWS_Survey.surveysWS[i].getReference() + " - "+TableWS_Survey.surveysWS[i].getName());
		TableWS_Survey.listeFiltree.add(TableWS_Survey.surveysWS[i].getReference() + " - "+TableWS_Survey.surveysWS[i].getName());
	}
	
	//liste filtree initialisée à typesWS au depart
	int taille = TableDataSurvey.typesWS.size();
	for (int i =0; i<taille;i++){
		TableDataSurvey.listeFiltree.add(TableDataSurvey.typesWS.get(i));
	}
	
	
	//TableDataSurvey.listeFiltree = TableDataSurvey.typesWS;
	
	TableData.listeFiltree =  new ArrayList <TypeData>();
	//liste filtree initialisée à typesWS au depart
	taille = TableData.typesWS.size();
	for (int i =0; i<taille;i++){
		TableData.listeFiltree.add(TableData.typesWS.get(i));
	}

	
	
	filtreText = new Text(top, SWT.BORDER);
	filtreText.setBounds(new Rectangle(3, 21, 298, 19));
	filtreText.setText("");
	labelText = new Label(top, SWT.NONE);
	labelText.setBounds(new Rectangle(3, 4, 298, 13));
	labelText.setText("Tapez le nom à filter");
	
	filtreButtonSelectionner = new Button(top, SWT.NONE);
	filtreButtonSelectionner.setBounds(new Rectangle(62, 289, 65, 23));
	filtreButtonSelectionner.setText("Sélectionner");
	filtreButtonSelectionner.pack();
	filtreButtonCancel = new Button(top, SWT.NONE);
	filtreButtonCancel.setBounds(new Rectangle(168, 289, 65, 23));
	filtreButtonCancel.setText("Annuler");
	filtreButtonCancel.pack();
	
//	affichage des tables
	if (ViewFile.affiche == 1){
		
		if (EditSurvey.ws){
			creationTableWS_Survey();	
		}else{
		creationTableSurvey();}
	}
	else {
		
		creationTableData();
		}
	
	filtreButtonCancel.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
		public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
			 System.out.println("valider cancel liste");
	          shell.close();
	          listeOuverte = 0;
	          
	          if (EditSurvey.ws){EditSurvey.ws = false;}
	
		}
	});
	
	
	filtreButtonSelectionner.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
		public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
			 System.out.println("valider sélection liste");
	         
	          listeOuverte = 0;
	          if (ViewFile.affiche == 1){
	        	  
	        	  if (EditSurvey.ws){
	  	      		//sélection d'une référence campagne
	  	      			 
	  	      			int index = TableWS_Survey.filtreTable.getSelectionIndex();
	  	 	 	        String reference = TableWS_Survey.listeFiltree.get(index);	
	  	 	 	        Pattern p  = Pattern.compile(" - ");
	  		    		
	  	 	 	        String mots[] = p.split(reference);
	  		    		System.out.println("Lancement WS sur la référence :"+mots[0]);
	  		    		EditSurvey.webService(reference);
	  		    		System.out.println("Fin de WS sur la référence :"+mots[0]);
	  		    		
	  		    	  shell.close(); 
	  		        if (EditSurvey.ws){
	  	      			EditSurvey.ws = false;}

	  		    		
	  	      		} 
	        	  else {
	        	  
	      		//ajout d'un dataType dans l'onglet Survey
	        TypeSurvey typeSurvey = new TypeSurvey();
	        
	        System.out.println("INDEX =>"+TableDataSurvey.filtreTable.getSelectionIndex());
	        int index = TableDataSurvey.filtreTable.getSelectionIndex();
	        typeSurvey = TableDataSurvey.listeFiltree.get(index);
	        
	        
	        
	        //test avant ajout pour doublons
	        int taille = TableDataSurvey.types.size();
	        Boolean found = false;
	        
	        if (taille !=0){
	        
	        for (int i=0; i<taille;i++){
	        	
//	        	split sur " - "
	    		Pattern p  = Pattern.compile(" - ");
	    		String mots[] = p.split(typeSurvey.getCode());
	    		String codeActuel = mots[0];
	    		
	    		String mots2[] = p.split(TableDataSurvey.types.get(i).getCode());
	    		String codeModele = mots2[0];
	        
	        if (codeModele.matches(codeActuel)){found = true;}
	        }
	        
	        if (found == false){	        
	        TableDataSurvey.types.add(typeSurvey);}
	        else{
	        Utils.messageErreur("Le type de données choisi existe déjà. Veuillez en choisir un autre...");	
	        }
	        }else{
	        	TableDataSurvey.types.add(typeSurvey);
	        }
	        
	        TableDataSurvey.majTypeData();
	    	   	 
	   	 //razTableData
	        TableDataSurvey.razTableData();
	   	 
	   	 //refresh de la table
	   	 //initTable();
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
	        if (EditSurvey.ws){
      			EditSurvey.ws = false;}

	          } }//fin if ws
	          
	          
	      	else {
	      		
	      		
	      		//ajout d'un parameter dans l'onglet Data
	      		
	      		
	      		 TypeData typeData = new TypeData();
	 	        
	 	        //System.out.println("INDEX =>"+TableDataSurvey.filtreTable.getSelectionIndex());
	 	        int index = TableData.filtreTable.getSelectionIndex();
	 	        typeData = TableData.listeFiltree.get(index);
	 	        
	 	       	        
	 	       //test avant ajout pour doublons
		        int taille = TableData.types.size();
		        Boolean found = false;
		        
		        if (taille !=0){
		        	
		        for (int i=0; i<taille;i++){
		        String codeModele, codeActuel;
		        
		     	//		split sur " - "
	    		Pattern p  = Pattern.compile(" - ");
	    		String mots[] = p.split(typeData.getCode());
	    		codeActuel = mots[0];
	    		
	    		String mots2[] = p.split(TableData.types.get(i).getCode());
	    		codeModele = mots2[0];

		        if (codeModele.matches(codeActuel)){found = true;}
		        }
		        
		        if (found == false){	        
		        	TableData.types.add(typeData);}
		        else{
		        Utils.messageErreur("Le paramètre choisi existe déjà. Veuillez en choisir un autre...");	
		        }
		        }
		        else{
		        	TableData.types.add(typeData);
		        }
		        	
	 	       	 	        
	 	       // TableData.types.add(typeData);
	 	        TableData.majTypeData();
	 	    	   	 
	 	   	 //razTableData
	 	        TableData.razTableData();
	 	   	 
	 	   	 //refresh de la table
	 	   	 //initTable();
	 	        TableData.creationTable();
	 	        
	      		shell.close();
	      		
	      		if (EditSurvey.ws){
	      			EditSurvey.ws = false;}

	      
	      	}	/////////////////FIN	
		}
		 
	});
	
	
	
	//listener keypressed sur le champ text
	filtreText.addListener(SWT.KeyUp, new Listener() {
        public void handleEvent(Event e) {
          System.out.println("touche appuyée");
          System.out.println("texte courant =>"+ filtreText.getText());
          
          if (filtreText.getText().matches("")){}
          else
          {
          
          if (ViewFile.affiche == 1){
        	  if (EditSurvey.ws){
        		  filtrageListeWS_Survey(filtreText.getText());
        	  }else{
        		  
        	  filtrageListeDataSurvey(filtreText.getText());}
      		}
      	else {           
          filtrageListeData(filtreText.getText());}
      	
          }
        }
      });
    
	 shell.pack();
	 shell.open();
	listeOuverte = 1; 
	}
	
}
	
public static void creationTableData(){
	final Table table;
	int nbre;
	
	TableData.filtreTable = new Table(top, SWT.FULL_SELECTION);
	//filtreTableData = new Table(top, SWT.FULL_SELECTION);
	table = TableData.filtreTable;
	table.setHeaderVisible(true);
	table.setLinesVisible(true);
	table.setBounds(new Rectangle(3, 45, 298, 237));
	
	nbre = TableData.listeFiltree.size();
	//création des colonnes...
	TableColumn tableColumn = new TableColumn(table, SWT.NONE);
	 tableColumn.setWidth(290);
		//taille = 60
	 tableColumn.setText("CODE - NOM");
//	 TableColumn tableColumn1 = new TableColumn(table, SWT.NONE);
//	 tableColumn1.setWidth(300);
//	 tableColumn1.setText("Nom");
//	 TableColumn tableColumn2 = new TableColumn(table, SWT.NONE);
//	 tableColumn2.setWidth(100);
//	 tableColumn2.setText("QC");
	
	  for (int i = 0; i < nbre; i++) {
	      TableItem item = new TableItem(table, SWT.NONE);
	      item.setText(new String[] { TableData.listeFiltree.get(i).getCode() });
	    
	    }  
	 
	 //ajout listeners sur la table
	  table.addListener(SWT.MouseDown, new Listener() {
	      public void handleEvent(Event event) {
	    	  //int tmp =table.getSelectionIndex();
	    	  
	     //  System.out.println("index sélectionné ="+ tmp);
	       
	      }
	    });

}
	

public static void creationTableSurvey(){
	final Table table;
	int nbre;
	
	TableDataSurvey.filtreTable = new Table(top, SWT.FULL_SELECTION);;
	nbre = TableDataSurvey.listeFiltree.size();
	
	table = TableDataSurvey.filtreTable;
	table.setHeaderVisible(true);
	table.setLinesVisible(true);
	table.setBounds(new Rectangle(3, 45, 298, 237));
	
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
	
	  for (int i = 0; i < nbre; i++) {
	      TableItem item = new TableItem(table, SWT.NONE);
	      item.setText(new String[] { TableDataSurvey.listeFiltree.get(i).getCode(), ""+TableDataSurvey.listeFiltree.get(i).getNombre(),
	    		  TableDataSurvey.listeFiltree.get(i).getQc() });
	    
	    }  
	 
	 //ajout listeners sur la table
	  table.addListener(SWT.MouseDown, new Listener() {
	      public void handleEvent(Event event) {
	    	//  int tmp =table.getSelectionIndex();
	    	  
	      // System.out.println("index sélectionné ="+ tmp);
	       
	      }
	    });


}




public static void filtrageListeData(String chaine){
	
	ArrayList <TypeData> origine, filtree;
	int nbre;
	String regexp;
	
	
	filtree = new ArrayList <TypeData>();
	origine = TableData.typesWS;
	
	nbre = origine.size();
	System.out.println("Nombre de types = "+nbre);
	
	//remplissage de la liste filtree
	for (int i=0;i<nbre;i++){
		
		//Pattern.matches(chaine,origine.get(i).getCode() )
		//(Pattern.matches("^[1-9][0-9]*$", valeur))
		regexp = "^.*"+chaine+".*$";
		System.out.println(""+chaine+"<=>"+origine.get(i).getCode());
		System.out.println(Pattern.matches(regexp,origine.get(i).getCode()));
		

		Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(origine.get(i).getCode());
				
		if (m.matches()){
		
		//if (Pattern.matches(regexp,origine.get(i).getCode())){
			System.out.println("On ajoute le type "+origine.get(i).getCode());

			//ajout dans la liste filtrée
			filtree.add(origine.get(i));
		}
	}
	
	TableData.listeFiltree = filtree;
	//affichage de la liste filtree
	
	System.out.println(">>>>>>>>>"+TableData.filtreTable);
	
	razTable(TableData.filtreTable);
	creationTableData();
	
	
}


public static void filtrageListeDataSurvey(String chaine){
	ArrayList <TypeSurvey> origine, filtree;
	int nbre;
	String regexp;
	
	
	filtree = new ArrayList <TypeSurvey>();
	origine = TableDataSurvey.typesWS;
	
	nbre = origine.size();
	System.out.println("Nombre de types = "+nbre);
	
	//remplissage de la liste filtree
	for (int i=0;i<nbre;i++){
		
		//Pattern.matches(chaine,origine.get(i).getCode() )
		//(Pattern.matches("^[1-9][0-9]*$", valeur))
		regexp = "^.*"+chaine+".*$";
		System.out.println(""+chaine+"<=>"+origine.get(i).getCode());
		System.out.println(Pattern.matches(regexp,origine.get(i).getCode()));
		
		Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(origine.get(i).getCode());
		
		//if (Pattern.matches(regexp,origine.get(i).getCode())){
		if (m.matches()){
			System.out.println("On ajoute le type "+origine.get(i).getCode());

			//ajout dans la liste filtrée
			filtree.add(origine.get(i));
		}
	}
	
	TableDataSurvey.listeFiltree = filtree;
	//affichage de la liste filtree
	
	System.out.println(">>>>>>>>>"+TableDataSurvey.filtreTable);
	
	razTable(TableDataSurvey.filtreTable);
	creationTableSurvey();
	
}


public static void filtrageListeWS_Survey(String chaine){
	ArrayList <String> origine, filtree;
	int nbre;
	String regexp;
	
	filtree = new ArrayList <String>();
	origine = TableWS_Survey.refsWS;
	
	nbre = origine.size();
	System.out.println("Nombre de refs = "+nbre);
	
	//remplissage de la liste filtree
	for (int i=0;i<nbre;i++){
		
		//Pattern.matches(chaine,origine.get(i).getCode() )
		//(Pattern.matches("^[1-9][0-9]*$", valeur))
		regexp = "^.*"+chaine+".*$";
		
		
		Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(origine.get(i));
		
		//if (Pattern.matches(regexp,origine.get(i).getCode())){
		if (m.matches()){
			System.out.println("On ajoute le type "+origine.get(i));

			//ajout dans la liste filtrée
			filtree.add(origine.get(i));
		}
	}
	
	TableWS_Survey.listeFiltree = filtree;
	//affichage de la liste filtree
	
	System.out.println(">>>>>>>>>"+TableWS_Survey.filtreTable);
	
	razTable(TableWS_Survey.filtreTable);
	creationTableWS_Survey();
}



public static void razTable(Table table){
	//maj
	table.dispose();
}

public static void majListeTypes(){
	
}


public static void creationTableWS_Survey(){
	final Table table;
	int nbre;
	
	TableWS_Survey.filtreTable = new Table(top, SWT.FULL_SELECTION);
	//filtreTableData = new Table(top, SWT.FULL_SELECTION);
	table = TableWS_Survey.filtreTable;
	table.setHeaderVisible(true);
	table.setLinesVisible(true);
	table.setBounds(new Rectangle(3, 45, 298, 237));
	
	nbre = TableWS_Survey.listeFiltree.size();
	//création des colonnes...
	TableColumn tableColumn = new TableColumn(table, SWT.NONE);
	 tableColumn.setWidth(290);
		//taille = 60
	 tableColumn.setText("REFERENCE - NOM");
//	 TableColumn tableColumn1 = new TableColumn(table, SWT.NONE);
//	 tableColumn1.setWidth(300);
//	 tableColumn1.setText("Nom");
//	 TableColumn tableColumn2 = new TableColumn(table, SWT.NONE);
//	 tableColumn2.setWidth(100);
//	 tableColumn2.setText("QC");
	
	  for (int i = 0; i < nbre; i++) {
	      TableItem item = new TableItem(table, SWT.NONE);
	      item.setText(new String[] { TableWS_Survey.listeFiltree.get(i)});
	    
	    }  
	 
	 //ajout listeners sur la table
	  table.addListener(SWT.MouseDown, new Listener() {
	      public void handleEvent(Event event) {
	    	  //int tmp =table.getSelectionIndex();
	    	  
	     //  System.out.println("index sélectionné ="+ tmp);
	       
	      }
	    });

}




}

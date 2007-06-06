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
import fr.ifremer.nemo.ws.WSSurveyDescription;

public class TableWS_Survey {

	/**
	 * @param args
	 */
	
	

	public static Table tableRefSurvey, filtreTable;
	public static int ligneSelec, colonneSelec, debut, fin, ligneBidon;
	//public static String[][]tableTypes;
	public static ArrayList <String> refs, listeFiltree, refsWS;
	//public static ArrayList <>
	public static WSSurveyDescription[] surveysWS;


	
	public static void main(String[] args) {
	

	}

}

package fr.ifremer.nemo.ui;


import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Composite;

import fr.ifremer.nemo.ui.tables.AbstractFilteredTable;
import fr.ifremer.nemo.ui.tables.DatacentreFilteredTable;
import fr.ifremer.nemo.ui.tables.DatatypeFilteredTable;
import fr.ifremer.nemo.ui.tables.ParameterFilteredTable;
import fr.ifremer.nemo.ui.tables.RegionFilteredTable;
import fr.ifremer.nemo.ui.tables.ShipFilteredTable;
import fr.ifremer.nemo.ui.tables.WSSurveyFilteredTable;
import fr.ifremer.nemo.util.Global;

public class FilteredTableFactory{
    

    public FilteredTableFactory()
    {
    }
    public  AbstractFilteredTable go(Composite parent, String table, CCombo comboRech){
        AbstractFilteredTable theTable=null;
        
//        if (table.equals(Global.IDEDMO)){
//            theTable =  new IdEDMOFilteredTable(comboRech);
//            theTable.open(parent);
//        }
//        else
        	if (table.equals(Global.WSSURVEY)){
            theTable =  new WSSurveyFilteredTable(comboRech);
            theTable.open(parent);
            }
        else if (table.equals(Global.DATATYPE)){
            theTable =   new DatatypeFilteredTable(comboRech);
            theTable.open(parent);
            }
        else if (table.equals(Global.PARAMETER)){
            theTable = new ParameterFilteredTable(comboRech);
            }
        else if (table.equals(Global.SHIP)){
            theTable =   new ShipFilteredTable(comboRech);
            theTable.open(parent);
            }
        else if (table.equals(Global.DATACENTRE)){
            theTable =   new DatacentreFilteredTable(comboRech);
            theTable.open(parent);
            }
        else if (table.equals(Global.REGION)){
            theTable =   new RegionFilteredTable(comboRech);
            theTable.open(parent);
            }
        return theTable;
    }

}

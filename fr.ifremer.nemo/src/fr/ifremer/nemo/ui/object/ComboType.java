/* ============================================================================
 * Nom du fichier	: ComboType.java
 * Auteur			: dboulang
 * Date				: 23 janv. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.object;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Shell;

import fr.ifremer.nemo.ui.TableDataSurvey;
import fr.ifremer.nemo.ui.ViewFile;

/**
 * Description: enumération des types de Combo possibles
 * @author dboulang
 *
 */
public enum ComboType
{
    NONE, BLOCK, FRQ, DATE, TIME, LAT, LON, DATATYPE;
    
    public Combo createCombo(Shell shell){
        Combo valueCombo;
        switch (this){
            case DATE :
                valueCombo = new Combo(shell, SWT.BORDER);
                valueCombo.add("DD/MM/YY");                 //$NON-NLS-1$
                valueCombo.add("DD/MM/YYYY");               //$NON-NLS-1$
                valueCombo.add("DDMMYY");                   //$NON-NLS-1$
                valueCombo.add("DDMMYYYY");                 //$NON-NLS-1$
                valueCombo.add("MMM DD YYYY");              //$NON-NLS-1$
                valueCombo.add("YY/MM/DD");                 //$NON-NLS-1$
                valueCombo.add("YYMMDD");                   //$NON-NLS-1$
                valueCombo.add("YYYY/MM/DD");               //$NON-NLS-1$
                valueCombo.add("YYYYMMDD");                 //$NON-NLS-1$
                break;
            case TIME :
                valueCombo = new Combo(shell, SWT.BORDER);
                valueCombo.add("hh:mm PM");                 //$NON-NLS-1$
                valueCombo.add("hh:mm:ss PM");              //$NON-NLS-1$
                valueCombo.add("hh24:mm");                  //$NON-NLS-1$
                valueCombo.add("hh24:mm:ss");               //$NON-NLS-1$
                valueCombo.add("hh24mm");                   //$NON-NLS-1$
                valueCombo.add("hh24mmss");                 //$NON-NLS-1$
                valueCombo.add("hh24 mm");                  //$NON-NLS-1$
                valueCombo.add("hh24:cc");                  //$NON-NLS-1$
                valueCombo.add("ttttt");                    //$NON-NLS-1$
                break;
            case LAT :
                valueCombo = new Combo(shell, SWT.BORDER);
                valueCombo.add("+DD MM.mm");                //$NON-NLS-1$
                valueCombo.add("+DD.ddd");                  //$NON-NLS-1$
                valueCombo.add("+DDMMSS");                  //$NON-NLS-1$
                valueCombo.add("NDD MM.mm");                //$NON-NLS-1$
                valueCombo.add("NDD.ddd");                  //$NON-NLS-1$
                valueCombo.add("NDDMMSS");                  //$NON-NLS-1$
                break;
            case LON :
                valueCombo = new Combo(shell, SWT.BORDER);
                valueCombo.add("+DDD MM.mm");               //$NON-NLS-1$
                valueCombo.add("+DDD.ddd");                 //$NON-NLS-1$
                valueCombo.add("+DDDMMSS");                 //$NON-NLS-1$
                valueCombo.add("NDDD MM.mm");               //$NON-NLS-1$
                valueCombo.add("NDDD.ddd");                 //$NON-NLS-1$
                valueCombo.add("NDDDMMSS");                 //$NON-NLS-1$
                break;
            case DATATYPE :
                valueCombo = new Combo(shell, SWT.BORDER);
                // FAE022
                if (!ViewFile.isCollection()) {
                    int taille = TableDataSurvey.types.size();
                    for (int i = 0; i < taille; i++) {
                        // on n'ajoute que les sensors
                        if (TableDataSurvey.types.get(i).getSensor()) {
                            valueCombo.add(TableDataSurvey.types.get(i).getCode());
                        }
                    }
                }
                break;
            default:
                valueCombo = null;
                break;
        }
        return valueCombo;
    }
    
}

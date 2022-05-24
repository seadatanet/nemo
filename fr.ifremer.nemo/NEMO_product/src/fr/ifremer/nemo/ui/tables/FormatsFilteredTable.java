/* ============================================================================
 * Nom du fichier : ParameterFilteredTable.java
 * Auteur : ycaudal
 * Date : 22 déc. 09
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.tables;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.data.TableData;
import fr.ifremer.nemo.ui.edit.data.TypeFormat;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: TODO
 * 
 * @author sbregent
 */
public class FormatsFilteredTable extends AbstractFilteredTable
{

	/**
	 * reference sur la tableData (de l'onglet data) depuis laquelle cette table
	 * a été ouverte
	 */
	private final TableData myTableDataRef;
	/**
	 * copie de la liste des parametres typesWS pouvant être filtrée
	 */
	private ArrayList<TypeFormat> listeFiltree;

	// private HashMap<String, String> listeFormats;

	/**
	 * @param comboRecherche
	 */
	public FormatsFilteredTable()
	{
		myTableDataRef =
				Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createDataList()
	{
		reloadListeFormats();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createTable()
	{
		super.createTable();
//		myFiltreTable = new Table(top, SWT.FULL_SELECTION);
//
//		myFiltreTable.setHeaderVisible(true);
//		myFiltreTable.setLinesVisible(true);
//		myFiltreTable.setBounds(new Rectangle(3, 45, 298, 237));

		// creation des colonnes...
		TableColumn tableColumn = new TableColumn(myFiltreTable, SWT.NONE);
		tableColumn.setWidth(200);
		tableColumn.setText("format");

		TableColumn tableColumn2 = new TableColumn(myFiltreTable, SWT.NONE);
		tableColumn2.setWidth(81);
		tableColumn2.setText("example");

		populateTableWithFilteredList();

	}
	@Override
    protected void populateTableWithFilteredList(){
		for (int i = 0; i < listeFiltree.size(); i++)
		{
			TableItem item = new TableItem(myFiltreTable, SWT.NONE);
            item.setText(new String[] {listeFiltree.get(i).getFormat(),
					listeFiltree.get(i).getExample(), });
		}
	}
	/**
	 * recharge la liste des paramètres filtrée (= annule le filtre)
	 */
	private void reloadListeFormats()
	{
		listeFiltree = new ArrayList<TypeFormat>();
		String fileName =
				Intern.dir_resources + File.separator + "parametersFormats"
						+ Global.EXT_XML;
		File file = new File(fileName);
		if (file.exists())
		{

			try
			{
				// création d'une fabrique de documents
				DocumentBuilderFactory fabrique =
						DocumentBuilderFactory.newInstance();

				// création d'un constructeur de documents
				DocumentBuilder constructeur = fabrique.newDocumentBuilder();

				// lecture du contenu d'un fichier XML avec DOM
				org.w3c.dom.Document document = constructeur.parse(file);
				Element racine = document.getDocumentElement();
				System.out.println(racine.getNodeName());

				String tag = "format";
				NodeList listeFormats = racine.getElementsByTagName(tag);

				for (int i = 0; i < listeFormats.getLength(); i++)
				{

					Node oneFormat = listeFormats.item(i);
					Element oneFormatEl = (Element) oneFormat;
					String example = oneFormatEl.getAttribute("example");
					String format = oneFormatEl.getAttribute("format");
					// System.out.println(" -- " + format + " -- " + example);
					listeFiltree.add(new TypeFormat(format, example));
				}
				myTableDataRef.setTypesFormat(listeFiltree);
            }
            catch (ParserConfigurationException pce)
			{
				System.out.println("Erreur de configuration du parseur DOM");
				System.out
						.println("lors de l'appel à fabrique.newDocumentBuilder();");
            }
            catch (SAXException se)
			{
				System.out.println("Erreur lors du parsing du document");
				System.out.println("lors de l'appel à construteur.parse(xml)");
            }
            catch (IOException ioe)
			{
				System.out.println("Erreur d'entrée/sortie");
				System.out.println("lors de l'appel à construteur.parse(xml)");
			}

		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void filterAction(String chaine)
	{

		ArrayList<TypeFormat> origine;
		ArrayList<TypeFormat> filtree;
		String regexp;

		filtree = new ArrayList<TypeFormat>();
		origine = myTableDataRef.getTypesFormat();

		// remplissage de la liste filtree
		for (int i = 0; i < origine.size(); i++)
		{
			regexp = "^.*" + chaine + ".*$"; //$NON-NLS-1$ //$NON-NLS-2$
			Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(origine.get(i).getFormat());
			if (m.matches())
			{
				// ajout dans la liste filtree
				filtree.add(origine.get(i));
			}
		}
		listeFiltree = filtree;
		// affichage de la liste filtree
//		razTable(myFiltreTable);
//		createTable();
		refreshTable();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getDialogTitle()
	{
		return Messages.edit_data_P061;
	}

	/**
	 * action appelée lorsqu'un parametre est selectionné dans le panneau
	 * ParameterFilteredTable -> ajoute la parametre au tableau des data ou
	 * ouvre la liste des SubParameters {@inheritDoc}
	 */
	@Override
	protected void selectAction()
	{

		TypeFormat typeFormat;

		int index = myFiltreTable.getSelectionIndex();
		typeFormat = listeFiltree.get(index);
		// reinit validation
		Utils.invalidEtapes();

		myTableDataRef.setFormat(typeFormat);
		myTableDataRef.majTypeData();

		// refresh de la table
		myTableDataRef.razAndRecreateTable();
		shell.close();
	}
}

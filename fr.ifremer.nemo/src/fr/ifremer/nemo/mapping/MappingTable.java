/* ============================================================================
 * Nom du fichier	: MappingTable.java
 * Auteur			: jollivie
 * Date				: 30 janv. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.mapping;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.util.Messages;



/**
 * Description: MappingTable
 * @author jollivie
 *
 */
@SuppressWarnings("nls")
public class MappingTable
{
	/*EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("nemoPU");
    @éContext
    EntityManager em = emf.createEntityManager(); */


	public List<Mapping> findAll() {
		EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("nemoPU"); 
		EntityManager em = emf.createEntityManager(); 
		return em.createQuery("SELECT m FROM Mapping m").getResultList();

	}

	public boolean importer(String filename)
	{
		//On determine l'extension du fichier, qui certe, est filtré via la boite d'ouverture 
		//de fichier, mais qui peut etre changé dans la ligne texte
		String fileExtension = (filename.lastIndexOf(".")==-1)?"":filename.substring(filename.lastIndexOf(".")+1,filename.length()); 
		System.out.println(Messages.CouplingTableImport_1 + fileExtension + "\n\r");

		if(fileExtension.matches("txt") || fileExtension.matches("Txt") || fileExtension.matches("TXT"))
		{
			try {
				InputStreamReader correctFile = new InputStreamReader(new FileInputStream(filename));
				LineNumberReader correctFileLine = new LineNumberReader(correctFile); 
				String ligne = correctFileLine.readLine();   
				String[] ligneSplit = new String[5];

				Date datelignefichier = new Date();
				Mapping m = new Mapping();

				fr.ifremer.nemo.ui.Utils.messageOK(Messages.CouplingTableImport_2+"\n\r");

				try {
					while ( (ligne = correctFileLine.readLine()) != null) 
					{ 

						ligneSplit = ligne.split(";");

						m = new Mapping(ligneSplit[0],ligneSplit[2]);

						m.setModus(ligneSplit[1]);
						m.setFilename(ligneSplit[3]);

						m.setDate(datelignefichier);

						try
						{
							this.persist(m);
						} catch (javax.persistence.EntityExistsException e)
						{
							fr.ifremer.nemo.ui.Utils.messageErreurAppend(Messages.CouplingTableImport_3+ligne+"\n\r");

						}

					}
					fr.ifremer.nemo.ui.Utils.messageOKAppend(Messages.CouplingTableImport_4+"\n\r");
				} catch (Exception e)
				{
					if(e.toString().contains("EntityExistsException")) //$NON-NLS-1$
					{
						fr.ifremer.nemo.ui.Utils.messageErreurAppend(Messages.CouplingTableImport_5+"\n\r");
					} else
					{
						fr.ifremer.nemo.ui.Utils.messageErreurAppend(Messages.CouplingTableImport_6+"\n\r");
					}

					System.out.println(e.toString());
					System.out.println("ma cause" + e.getCause().toString());


				}
				correctFile.close();


			}catch(FileNotFoundException e) { 
				fr.ifremer.nemo.ui.Utils.messageErreurAppend(Messages.CouplingTableImport_7+"\n\r");

			}
			catch(IOException e) { 
				System.out.println("exception IOException : " + e);
			}

		} else
		{
			fr.ifremer.nemo.ui.Utils.messageErreurAppend(Messages.CouplingTableImport_8);
		}

		return true;
	}

	public boolean exporter(String filename)
	{
		try
		{
			java.io.File fichier = new java.io.File(filename);

			fichier.createNewFile(); // Cette fonction doit être appelée au sein d'un bloc TRY

			//Ensuite on peut construire l'object FileOutputStream avec le fichier
			try
			{
				java.io.FileOutputStream monFluxFichier = new java.io.FileOutputStream(filename); // Doit être utilisé dans un bloc TRY

				try 
				{
					monFluxFichier.write("LOCAL_CDI_ID;MODUS;FORMAT;FILENAME".getBytes());    
					String ligne = new String();
					for(Mapping mapping : this.findAll())
					{
						ligne = "\r\n"+mapping.mappingPK.getLocalCdiId()+";"+mapping.getModus()+";"+mapping.mappingPK.getFormat()+";"+mapping.getFilename(); 
						monFluxFichier.write(ligne.getBytes()); 

					}

					monFluxFichier.flush();
					if (!Nemo.getInstance().isBatchMode()){
						fr.ifremer.nemo.ui.Utils.messageOKAppend(Messages.CouplingTableExport_1+"\n\r");
					}
				}
				catch (IOException e)
				{
				} 
				finally
				{
					monFluxFichier.close(); // Toujours dans un TRY

				}

			}
			catch (FileNotFoundException e)
			{
				System.out.println(Messages.CouplingTableImport_7);
			}
		}
		catch (IOException e)
		{
			System.out.println(Messages.CouplingTableExport_2+"\n\r");
		}




		return true;
	}


	public void persist(Mapping object)
	{
		EntityManagerFactory emf;
		EntityManager em;
		try{
			System.out.println("start persist "+ object.getMappingPK().getLocalCdiId());
			emf = javax.persistence.Persistence.createEntityManagerFactory("nemoPU"); 
			System.out.println("emf.isOpen() "+ emf.isOpen());
			em = emf.createEntityManager();

			em.getTransaction().begin();
		}catch(Exception e){
			System.out.println("can not open coupling Nemo HSQL Database");
			e.printStackTrace();  
			return;
		}
		try 
		{  
			try
			{
				em.merge(object); 
				em.getTransaction().commit();  
			} catch (javax.persistence.EntityExistsException e)
			{
				fr.ifremer.nemo.ui.Utils.messageErreurAppend("Already exist: "+object.mappingPK.getLocalCdiId()+ " en "+ object.mappingPK.getFormat());
			}
		} catch (Exception e) 
		{
			System.out.println("rollback");
			e.printStackTrace();  
			em.getTransaction().rollback();  
		} finally 
		{  
			em.close();  
		}  
	}   
}

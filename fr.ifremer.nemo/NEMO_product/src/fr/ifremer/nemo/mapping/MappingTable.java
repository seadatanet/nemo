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

	private EntityManagerFactory emf ;
	private EntityManager em ;

	public EntityManagerFactory getEmf() {
		if (emf==null){
			emf = javax.persistence.Persistence.createEntityManagerFactory("nemoPU"); 
		}
		return emf;
	}
	public EntityManager getEm() {
		getEmf();
		if (em==null){
			em = emf.createEntityManager(); 
		}
		return em;
	}
	public List<Mapping> findAll() {

		return getEm().createQuery("SELECT m FROM Mapping m").getResultList();

	}

	public boolean importer(String filename)
	{
		//On determine l'extension du fichier, qui certe, est filtré via la boite d'ouverture 
		//de fichier, mais qui peut etre changé dans la ligne texte
		String fileExtension = (filename.lastIndexOf(".")==-1)?"":filename.substring(filename.lastIndexOf(".")+1,filename.length()); 
//		System.out.println(Messages.CouplingTableImport_1 + fileExtension + "\n\r");
		Nemo.getInstance().getLogger().debug(Messages.CouplingTableImport_1 + fileExtension + "\n\r");
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
					Nemo.getInstance().getLogger().error(e.toString());
//					System.out.println(e.toString());
//					System.out.println("ma cause" + e.getCause().toString());
					Nemo.getInstance().getLogger().error(e.getCause().toString());

				}
				correctFile.close();


			}catch(FileNotFoundException e) { 
				fr.ifremer.nemo.ui.Utils.messageErreurAppend(Messages.CouplingTableImport_7+"\n\r");

			}
			catch(IOException e) { 
//				System.out.println("exception IOException : " + e);
				Nemo.getInstance().getLogger().error(e.getMessage());
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
				Nemo.getInstance().getLogger().error(Messages.CouplingTableImport_7);
				Nemo.getInstance().getLogger().error(e.getMessage());
			}
		}
		catch (IOException e)
		{
//			System.out.println(Messages.CouplingTableExport_2+"\n\r");
			Nemo.getInstance().getLogger().error(Messages.CouplingTableExport_2+"\n\r");
			Nemo.getInstance().getLogger().error(e.getMessage());
		}




		return true;
	}


	public void persist(Mapping object)
	{
		try{
			emf = javax.persistence.Persistence.createEntityManagerFactory("nemoPU"); 
			em = emf.createEntityManager(); 
			em.getTransaction().begin();

//			System.out.println("transaction active: "+em.getTransaction().isActive());
			Nemo.getInstance().getLogger().info("transaction active: "+em.getTransaction().isActive());
		}catch(Exception e){
//			System.out.println("can not open coupling Nemo HSQL Database");
			Nemo.getInstance().getLogger().error("can not open coupling Nemo HSQL Database");
			e.printStackTrace();  
			return;
		}
		try 
		{  
			try
			{
//				System.out.println("merge");
				Nemo.getInstance().getLogger().debug("merge");
				em.merge(object); 
//				System.out.println("commit");
				Nemo.getInstance().getLogger().debug("commit");
				em.getTransaction().commit();
//				System.out.println("transaction active: "+em.getTransaction().isActive());
				Nemo.getInstance().getLogger().debug("transaction active: "+em.getTransaction().isActive());
			} catch (javax.persistence.EntityExistsException e)
			{
				fr.ifremer.nemo.ui.Utils.messageErreurAppend("Already exist: "+object.mappingPK.getLocalCdiId()+ " en "+ object.mappingPK.getFormat());
			}
		} catch (Exception e) 
		{
			//System.out.println("rollback");
			Nemo.getInstance().getLogger().error("rollback");
			e.printStackTrace();  
			em.getTransaction().rollback();  
		} finally 
		{  
			em.close();  
		}  
	}   
}

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
import javax.persistence.PersistenceContext;



/**
 * Description: TODO
 * @author jollivie
 *
 */
public class MappingTable
{
    /*EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("nemoPU");   //$NON-NLS-1$
    @PersistenceContext
    EntityManager em = emf.createEntityManager(); */
    
    


  /*  public Mapping add(Mapping mapping) {
        em.persist(mapping);
        return mapping;
    }

    public Mapping update(Mapping entity) {
        return em.merge(entity);
    }

    public void delete(Mapping entity) {
        em.remove(entity);
    }*/

    public List<Mapping> findAll() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("nemoPU");   //$NON-NLS-1$
        EntityManager em = emf.createEntityManager(); 
            return em.createQuery("SELECT m FROM Mapping m").getResultList(); //$NON-NLS-1$
            
    }
    
    public boolean importer(String filename)
    {
        //On determine l'extension du fichier, qui certe, est filtré via la boite d'ouverture 
        //de fichier, mais qui peut etre changé dans la ligne texte
        String fileExtension = (filename.lastIndexOf(".")==-1)?"":filename.substring(filename.lastIndexOf(".")+1,filename.length()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        System.out.println("L'extension du fichier est ." + fileExtension + "\n\r"); //$NON-NLS-1$ //$NON-NLS-2$

        if(fileExtension.matches("txt") || fileExtension.matches("Txt") || fileExtension.matches("TXT")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        {
            try {
                    InputStreamReader correctFile = new InputStreamReader(new FileInputStream(filename));
                    LineNumberReader correctFileLine = new LineNumberReader(correctFile); 
                    String ligne = correctFileLine.readLine();   
                    String[] ligneSplit = new String[5];

                    Date datelignefichier = new Date();
                    Mapping m = new Mapping();



                    try {
                        while ( (ligne = correctFileLine.readLine()) != null) 
                        { 
                             System.out.println(correctFileLine.getLineNumber()-1+" : "+ligne); //$NON-NLS-1$

                             ligneSplit = ligne.split(";"); //$NON-NLS-1$

                              m = new Mapping(ligneSplit[0],ligneSplit[2]);

                              m.setModus(ligneSplit[1]);
                              m.setFilename(ligneSplit[3]);

                              m.setDate(datelignefichier);

                              try
                              {
                                  this.persist(m);
                              } catch (javax.persistence.EntityExistsException e)
                              {
                                  System.out.println("Ligne en doublon: "+ligne); //$NON-NLS-1$
                              }

                        }
                   } catch (Exception e)
                   {
                      if(e.toString().contains("EntityExistsException")) //$NON-NLS-1$
                      {
                          System.out.println("Le fichier de correction est invalide: des données sont dupliquées"); //$NON-NLS-1$
                      } else
                      {
                          System.out.println("Le fichier est invalide."); //$NON-NLS-1$
                      }

                      System.out.println(e.toString());
                      System.out.println("ma cause" + e.getCause().toString()); //$NON-NLS-1$


                     }
                    correctFile.close();
             
  
            }catch(FileNotFoundException e) { 
                System.out.println("Fichier non trouvé");  //$NON-NLS-1$
        
            }
            catch(IOException e) { 
                System.out.println("exception IOException : " + e); //$NON-NLS-1$
            }

        } else
        {
           // fr.ifremer.nemo.ui.Utils.messageErreurAppend("Le fichier n'est pas au format .txt\n\r");  //$NON-NLS-1$ //$NON-NLS-2$
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
                        ligne = "\r\n"+mapping.mappingPK.getLocalCdiId()+";"+mapping.getModus()+";"+mapping.mappingPK.getFormat()+";"+mapping.getFilename(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                        monFluxFichier.write(ligne.getBytes()); 
                        
                    }

                    monFluxFichier.flush();
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
                System.out.println("Impossible de trouver le fichier");
            }
        }
        catch (IOException e)
        {
            System.out.println("Impossible de créer le fichier");
        }



 
        return true;
    }
    
    
    public void persist(Mapping object)
    {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("nemoPU");   //$NON-NLS-1$
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();  
        try 
        {  
            try
            {
                em.merge(object); 
                em.getTransaction().commit();  
            } catch (javax.persistence.EntityExistsException e)
            {
                //fr.ifremer.nemo.ui.Utils.messageErreurAppend("Already exist: "+object.mappingPK.getLocalCdiId()+ " en "+ object.mappingPK.getFormat());  //$NON-NLS-1$ //$NON-NLS-2$
            }
        } catch (Exception e) 
        {
            e.printStackTrace();  
            em.getTransaction().rollback();  
        } finally 
        {  
            em.close();  
        }  

        
        
    }   
    
    
}

/* ============================================================================
 * Nom du fichier	: TestP.java
 * Auteur			: jollivie
 * Date				: 29 janv. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.mapping;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;



/**
 * Description: TODO
 * @author jollivie
 *
 */
public class TestP
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {

          Mapping m = new Mapping("format2","localCdiId" ); //$NON-NLS

          m.setModus("1"); //$NON-NLS
          m.setFilename("le filename");
          m.setDate(new Date());
        
          TestP test = new TestP();
          test.persist(m);
    }
    
    public void persist(Object object)
    {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("nemoPU");  
            EntityManager entityManager = emf.createEntityManager();  
            entityManager.getTransaction().begin();  
        try 
        {  
            entityManager.persist(object);  
            entityManager.getTransaction().commit();  
        } catch (Exception e) 
        {  
            e.printStackTrace();  
            entityManager.getTransaction().rollback();  
        } finally 
        {  
            entityManager.close();  
        }  

        
        
    }

}

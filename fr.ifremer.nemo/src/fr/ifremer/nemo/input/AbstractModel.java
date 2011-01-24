package fr.ifremer.nemo.input;

import java.io.OutputStream;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.ago.xml.PathElementHandler;
import com.ago.xml.RootSAXConsumer;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;


/**
 * Class AbstractModel.java
 * Description: Schéma de classe issue d'une description XML
 * @author Delphine BOULANGER 
 */
public abstract class AbstractModel implements Cloneable, PathElementHandler, RootSAXConsumer
{    
    /**
     * nom du tag principal de l'élément
     */
    protected String _tagName;
    
    /**
     * affecte les valeurs par defaut
     */
    public void initValues(){
        _tagName = ModelDescription.nodetitle_default;
    }

    
    /**
     * Methode de creation d'instance a partir des informations d'un element XML
     * @param a_URI :  Le Namespace (SAX 2)
     * @param a_LocalName : Le nom du Tag
     * @param a_QName : Le nom qualifie du tag. Non utilise ici
     * @param a_Attributes : Les attributs de l'element
     * @return SAXBaseConsumer
     */
    static public SAXConsumer fromElement(String a_URI, 
                                   String a_LocalName,
                                   String a_QName, 
                                   Attributes a_Attributes){
        // à implementer dans les classes filles
        return null;
    }

    /**
     * Un nouvel element XML a ete rencontre - traitement des attributs
     * @param a_URI : Le Namespace (SAX 2)
     * @param a_LocalName : Le nom du Tag
     * @param a_QName : Le nom qualifie du tag. Non utilise ici
     * @param a_Attributes : Les attributs de l'element
     */
    abstract public void buildFromElement(String a_URI, 
                                 String a_LocalName,
                                 String a_QName, 
                                 Attributes a_Attributes);

    /**
     * Un nouvel element XML a ete rencontre - traitement des elements fils
     * @param a_URI : Le Namespace (SAX 2)
     * @param a_LocalName : Le nom du Tag
     * @param a_QName : Le nom qualifie du tag. Non utilise ici
     * @param a_Attributes : Les attributs de l'element
     * @return SAXConsumer
     */
    abstract public SAXConsumer consumeElement(String a_URI, 
                                      String a_LocalName,
                                      String a_QName, 
                                      Attributes a_Attributes);

    /**
     * Un champ de type PCDATA a ete rencontre
     * @param a_Characters : Valeur du champ PCDATA
     */
    final public void consumeCharacters(String a_Characters){
        // non utilisé
    }

    /**
     * Redirige l'instanciation des objets fils en fonction du chemin defini
     * @param path : le chemin
     * @param a_URI : Le Namespace (SAX 2)
     * @param a_LocalName : Le nom du Tag
     * @param a_QName : Le nom qualifie du tag. Non utilise ici
     * @param a_Attributes : Les attributs de l'element
     * @return SAXConsumer
     */
    abstract public SAXConsumer handlePathElement(String path, 
                                         String a_URI,
                                         String a_LocalName, 
                                         String a_QName, 
                                         Attributes a_Attributes) throws SAXException;

    /**
     * Genere la definition XML du contenu de l'objet dans un XMLOutputWriter
     * @param a_Writer : XMLOutputWriter contenant la definition XML globale
     */
    abstract public void writeXMLBodyOn(XMLOutputWriter a_Writer);

    /**
     * Genere la definition XML de l'objet dans un XMLOutputWriter
     * @param a_Writer : XMLOutputWriter contenant la definition XML globale
     */
    final public void writeXMLOn(XMLOutputWriter a_Writer){
        a_Writer.openTag(_tagName);
        writeXMLBodyOn(a_Writer);
        a_Writer.closeTag();
    }

    /**
     * Genere la definition XML de l'objet à partir d'un OutputStream
     * @param a_Stream : OutputStream contenant la definition XML
     */
    final public void writeXMLOn(OutputStream a_Stream){
        XMLOutputWriter writer = new XMLOutputWriter(a_Stream);
        writeXMLOn(writer);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        AbstractModel copy = null;
        copy = (AbstractModel) super.clone();
        return copy;
    }
    
    /**
     * Reinitialise l'objet avec les valeurs par defaut
     */
    final public void reset(){
        release();
        initValues();
    }

    /**
     * Supprime les associations avec d'autres objets
     */
    abstract public void release();


    /**
     * Getter pour _tagName.
     * @return String
     * @see AbstractModel.java#_tagName
     */
    public String getTagName(){
        return _tagName;
    }

    /**
     * Setter pour _tagName
     * @param x : nouvelle valeur pour tagName
     * @see AbstractModel.java#_tagName
     */
    public void setTagName(String x){
        _tagName = x;
    }
}
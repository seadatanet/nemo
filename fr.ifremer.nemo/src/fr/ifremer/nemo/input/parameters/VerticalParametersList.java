/* ============================================================================
 * Nom du fichier	: VerticalParameters.java
 * Auteur			: sbregent
 * Date				: 25 janv. 11
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.input.parameters;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.ago.xml.Element2Field;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.input.AbstractModel;
import fr.ifremer.nemo.input.ModelDescription;
import fr.ifremer.nemo.util.Global;

/**
 * Description: classe de lecture/ecriture dans le modele XML pour le noeud verticalParametersList
 * ce noeud contient 3 fils indiquant les valeurs de 3 parametres de reference verticale possibles
 * (peuvent être vides)
 * @author sbregent
 *
 */
public class VerticalParametersList extends AbstractModel
{

    private String _p_depthBSBed;
    private String _p_depthBSSurf;
    private String _p_pressure;
    /**
     * 
     */
    public VerticalParametersList()
    {
        _tagName = ModelDescription.node_verticalParametersList;
        _p_depthBSBed = Global.EMPTY_STRING;
        _p_depthBSSurf = Global.EMPTY_STRING;
        _p_pressure = Global.EMPTY_STRING;
    }
    /**
     * @see AbstractModel.java#fromElement
     */
    public static SAXConsumer fromElement(String a_URI, 
                                          String a_LocalName,
                                          String a_QName, 
                                          Attributes a_Attributes){
        VerticalParametersList el = new VerticalParametersList();
        el.buildFromElement(a_URI, a_LocalName, a_QName, a_Attributes);
        return el;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void buildFromElement(String a_URI, String a_LocalName,
            String a_QName, Attributes a_Attributes)
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SAXConsumer consumeElement(String a_URI, String a_LocalName,
            String a_QName, Attributes a_Attributes)
    {
        if (a_LocalName.equals(ModelDescription.node_p_depthBSBed))
        {
            Element2Field setter =
                new Element2Field(this, ModelDescription.setter_p_depthBSBed);
            return setter;
        }
        if (a_LocalName.equals(ModelDescription.node_p_depthBSSurf))
        {
            Element2Field setter =
                new Element2Field(this, ModelDescription.setter_p_depthBSSurf);
            return setter;
        }
        if (a_LocalName.equals(ModelDescription.node_p_pressure))
        {
            Element2Field setter =
                new Element2Field(this, ModelDescription.setter_p_pressure);
            return setter;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SAXConsumer handlePathElement(String path, String a_URI,
            String a_LocalName, String a_QName, Attributes a_Attributes)
            throws SAXException
    {
        // not used
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void release()
    {
        // aucun lien à supprimer
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeXMLBodyOn(XMLOutputWriter a_Writer)
    {
        // liste des references de parametres verticaux
        a_Writer.putTag(ModelDescription.node_p_depthBSBed, getP_depthBSBed());
        a_Writer.putTag(ModelDescription.node_p_depthBSSurf, getP_depthBSSurf());
        a_Writer.putTag(ModelDescription.node_p_pressure, getP_pressure());
    }

    public String getP_depthBSBed()
    {
        return _p_depthBSBed;
    }

    public void setP_depthBSBed(String bed)
    {
        _p_depthBSBed = bed;
    }

    public String getP_depthBSSurf()
    {
        return _p_depthBSSurf;
    }

    public void setP_depthBSSurf(String surf)
    {
        _p_depthBSSurf = surf;
    }

    public String getP_pressure()
    {
        return _p_pressure;
    }

    public void setP_pressure(String p_pressure)
    {
        _p_pressure = p_pressure;
    }

}

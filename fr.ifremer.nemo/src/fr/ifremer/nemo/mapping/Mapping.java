/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.ifremer.nemo.mapping;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author jollivie
 */
@Entity
@Table(name = "MAPPING",schema = "PUBLIC")
@NamedQueries({@NamedQuery(name = "Mapping.findByLocalCdiId", query = "SELECT m FROM Mapping m WHERE m.mappingPK.localCdiId = :localCdiId"), @NamedQuery(name = "Mapping.findByModus", query = "SELECT m FROM Mapping m WHERE m.modus = :modus"), @NamedQuery(name = "Mapping.findByFormat", query = "SELECT m FROM Mapping m WHERE m.mappingPK.format = :format"), @NamedQuery(name = "Mapping.findByFilename", query = "SELECT m FROM Mapping m WHERE m.filename = :filename")})
public class Mapping implements Serializable {
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected MappingPK mappingPK;
    @Column(name = "MODUS")
    private Character modus;
    @Column(name = "FILENAME")
    private String filename;

    public Mapping() {
    }

    public Mapping(MappingPK mappingPK) {
        this.mappingPK = mappingPK;
    }

    public Mapping(String localCdiId, String format) {
        this.mappingPK = new MappingPK(localCdiId, format);
    }

    public MappingPK getMappingPK() {
        return mappingPK;
    }

    public void setMappingPK(MappingPK mappingPK) {
        this.mappingPK = mappingPK;
    }

    public Character getModus() {
        return modus;
    }

    public void setModus(Character modus) {
        Character oldModus = this.modus;
        this.modus = modus;
        changeSupport.firePropertyChange("modus", oldModus, modus);
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        String oldFilename = this.filename;
        this.filename = filename;
        changeSupport.firePropertyChange("filename", oldFilename, filename);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (mappingPK != null ? mappingPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Mapping)) {
            return false;
        }
        Mapping other = (Mapping) object;
        if ((this.mappingPK == null && other.mappingPK != null) || (this.mappingPK != null && !this.mappingPK.equals(other.mappingPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mappingdatabaseedit.Mapping[mappingPK=" + mappingPK + "]";
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

}

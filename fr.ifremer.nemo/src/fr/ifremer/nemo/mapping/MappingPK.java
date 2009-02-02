/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.ifremer.nemo.mapping;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/**
 *
 * @author jollivie
 */
@Embeddable
public class MappingPK implements Serializable {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "LOCAL_CDI_ID", nullable = false)
    private String localCdiId;
    @GeneratedValue(strategy = GenerationType.AUTO)    
    @Column(name = "FORMAT", nullable = false)
    private String format;

    public MappingPK() {
    }

    public MappingPK(String localCdiId, String format) {
        this.localCdiId = localCdiId;
        this.format = format;
    }

    public String getLocalCdiId() {
        return localCdiId;
    }

    public void setLocalCdiId(String localCdiId) {
        this.localCdiId = localCdiId;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (localCdiId != null ? localCdiId.hashCode() : 0);
        hash += (format != null ? format.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MappingPK)) {
            return false;
        }
        MappingPK other = (MappingPK) object;
        if ((this.localCdiId == null && other.localCdiId != null) || (this.localCdiId != null && !this.localCdiId.equals(other.localCdiId))) {
            return false;
        }
        if ((this.format == null && other.format != null) || (this.format != null && !this.format.equals(other.format))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mappingdatabaseedit.MappingPK[localCdiId=" + localCdiId + ", format=" + format + "]";
    }

}

package xtremweb.core.poxwo;

/**
 * Describe class Poxwo here.
 *
 *
 * Created: Sun Sep 24 09:39:32 2006
 *
 * @author <a href="mailto:fedak@xtremciel.gillus.net">Gilles Fedak</a>
 * @version 1.0
 */

import xtremweb.core.db.*;

public abstract class Poxwo {

    /**
     * Creates a new <code>Poxwo</code> instance.
     *
     */
    public Poxwo() {
    }

    public Poxwo(boolean persistFlag) {
	if (persistFlag) 
	    persist();
    }
    //A FAIRE remote

    public abstract String getuid();
    public abstract void setuid(String uid);
    public void persist() {
	PoxwoManager.localPersist(this);
    }

    public void localPersist() {
	persist();
    }
}

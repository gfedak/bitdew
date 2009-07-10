package xtremweb.serv.ds;


import xtremweb.core.obj.ds.Attribute;

/**
 * Describe class <code>AttributeType</code> here.
 *
 * @author <a href="mailto:fedak@xtremciel.gillus.net">Gilles Fedak</a>
 * @version 1.0
 */
public class AttributeType {

    //replication,  fault-tolerance, placement, lifetime, distribution
    /**
     * constant <code>REPLICAT</code> : number of data copies in the system
     *
     */
    public static final int REPLICAT =1;

    /**
     *  constant <code>FT</code> : resilience to machine crash
     *
     */
    public static final int FT =2;

    /**
     *  constant <code>AFFINITY</code> : placement affinity to other data
     *
     */
    public static final int AFFINITY = 4;

    /**
     *  constant <code>LFTABS</code> : absolute lifetime
     *
     */
    public static final int LFTABS =8;

    /**
     *  constant <code>LFTREL</code> : relative lifetime 
     *
     */
    public static final int LFTREL =16;

    /**
     *  constant <code>OOB</code> : out-of-band transfer protocol
     *
     */
    public static final int OOB =32;

    /**
     *  constant <code>DISTRIB</code>: maximal number of data
     *
     */
    public static final int DISTRIB = 64; 

    /**
     *  <code>setAttributeTypeOn</code> toggle the specified Attribute object the flag (AttributeType constant)
     *
     * @param attr an <code>Attribute</code> value
     * @param type an <code>int</code> value
     */
    public static void setAttributeTypeOn(Attribute attr, int type) {
	attr.setmask(attr.getmask() | type);
    }


    /**
     *  <code>setAttributeTypeOff</code> untoggle the specified Attribute object the flag (AttributeType constant)
     *
     * @param attr an <code>Attribute</code> value
     * @param type an <code>int</code> value
     */
    public static void setAttributeTypeOff(Attribute attr, int type) {
	attr.setmask(  attr.getmask() & (~type) );
    }

    /**
     *  <code>isAttributeTypeSet</code>  test if the specified Attribute object has the flag (AttributeType constant) set
     *
     * @param attr an <code>Attribute</code> value
     * @param type an <code>int</code> value
     * @return a <code>boolean</code> value
     */
    public static boolean isAttributeTypeSet(Attribute attr, int type) {
	return ((attr.getmask() & type) == type);
    }

}

package xtremweb.serv.dt.ssh;

import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dc.Locator;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.obj.dt.Transfer;
import xtremweb.serv.dt.BlockingOOBTransferImpl;

/**
 * This class represents a ssh transfer, is the base for specialization classes
 * like ScpTransfer or SftpTransfer
 * 
 * @author jsaray
 * 
 */
public abstract class SSHTransfer extends BlockingOOBTransferImpl {

    /**
     * Path to the public key ex. /home/user/.ssh/id_rsa.pub
     */
    protected String publicKeyPath;

    /**
     * Path to a private key ex. /home/user/.ssh/id_rsa
     */
    protected String privateKeyPath;

    /**
     * Passphrase of the private key (if any private key)
     */
    protected String passphrase;

    /**
     * Known hosts file
     */
    protected String knownHosts;

    /**
     * ssh connection user name
     */
    protected String userName;

    /**
     * ssh connection password
     */
    protected String password;

    /**
     * SSH transfer constructor
     * 
     * @param d
     *            the Data
     * @param t
     *            the transfer
     * @param rl
     *            the remote locator
     * @param ll
     *            the local locator
     * @param rp
     *            the remote protocol
     * @param lp
     *            the local protocol
     */
    public SSHTransfer(Data d, Transfer t, Locator rl, Locator ll, Protocol rp,
	    Protocol lp) {
	super(d, t, rl, ll, rp, lp);
    }

    /**
     * gets the public key path
     * 
     * @return the public key path
     */
    public String getPublicKeyPath() {
	return publicKeyPath;
    }

    /**
     * set a public key path
     * 
     * @param publicKeyPath
     */
    public void setPublicKeyPath(String publicKeyPath) {
	this.publicKeyPath = publicKeyPath;
    }

    /**
     * gets a private key path
     * 
     * @return the private key path
     */
    public String getPrivateKeyPath() {
	return privateKeyPath;
    }

    /**
     * sets a private key path
     * 
     * @param privateKeyPath
     */
    public void setPrivateKeyPath(String privateKeyPath) {
	this.privateKeyPath = privateKeyPath;
    }

    /**
     * gets a passphrase
     * 
     * @return the passphrase
     */
    public String getPasshphrase() {
	return passphrase;
    }

    /**
     * sets a passphrase
     * 
     * @param passhphrase
     */
    public void setPasshphrase(String passhphrase) {
	this.passphrase = passhphrase;
    }

    /**
     * gets the known host file
     * 
     * @return the known hosts file
     */
    public String getKnownHosts() {
	return knownHosts;
    }

    /**
     * sets the known hosts file
     * 
     * @param knownHosts
     *            the known hosts file
     */
    public void setKnownHosts(String knownHosts) {
	this.knownHosts = knownHosts;
    }
}
package xtremweb.serv.dt.ssh;

import xtremweb.core.obj.dc.Data;
import xtremweb.core.obj.dc.Locator;
import xtremweb.core.obj.dr.Protocol;
import xtremweb.core.obj.dt.Transfer;
import xtremweb.serv.dt.BlockingOOBTransfer;
import xtremweb.serv.dt.BlockingOOBTransferImpl;

public abstract class SSHTransfer extends BlockingOOBTransferImpl{

	
	protected String publicKeyPath;
	protected String privateKeyPath;
	protected String passphrase;
	protected String knownHosts;
	
	public SSHTransfer(Data d, Transfer t, Locator rl, Locator ll, Protocol rp,
			Protocol lp) {
		super(d, t, rl, ll, rp, lp);
		// TODO Auto-generated constructor stub
	}
	
	public String getPublicKeyPath() {
		return publicKeyPath;
	}
	public void setPublicKeyPath(String publicKeyPath) {
		this.publicKeyPath = publicKeyPath;
	}
	public String getPrivateKeyPath() {
		return privateKeyPath;
	}
	public void setPrivateKeyPath(String privateKeyPath) {
		this.privateKeyPath = privateKeyPath;
	}
	public String getPasshphrase() {
		return passphrase;
	}
	public void setPasshphrase(String passhphrase) {
		this.passphrase = passhphrase;
	}
	public String getKnownHosts() {
		return knownHosts;
	}
	public void setKnownHosts(String knownHosts) {
		this.knownHosts = knownHosts;
	}
	
}

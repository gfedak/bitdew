package xtremweb.role.examples.obj;

import java.io.Serializable;

public class SongBitdew implements Serializable{
	
	private String filename;
	
	private String md5;
	
	public SongBitdew(String filename, String md5)
	{
		this.filename = filename;
		this.md5 = md5;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}
}

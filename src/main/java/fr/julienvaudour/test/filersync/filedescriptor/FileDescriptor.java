package fr.julienvaudour.test.filersync.filedescriptor;

import java.util.Date;

public class FileDescriptor {
	
	private final String fileName;
	private final Date lastUpdate;
	private final int size;
	
	public FileDescriptor(final String fileName, final Date lastUpdate, final int size){
		this.fileName = fileName;
		this.lastUpdate = lastUpdate;
		this.size = size;
	}

	public String getFileName() {
		return fileName;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public int getSize() {
		return size;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof FileDescriptor){
			FileDescriptor fs2 = (FileDescriptor)obj;
			return fileName.equals(fs2.fileName)&&lastUpdate.equals(fs2.lastUpdate)&&(size == fs2.size);
		}
		return false;
	}
	
	

}

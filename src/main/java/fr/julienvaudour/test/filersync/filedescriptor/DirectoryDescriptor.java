package fr.julienvaudour.test.filersync.filedescriptor;

import java.util.HashMap;
import java.util.Map;

public class DirectoryDescriptor {
	
	private final String dirName;
	
	private final Map<String,DirectoryDescriptor> subDirectories;
	
	private final Map<String,FileDescriptor> files;
	
	public DirectoryDescriptor(final String dirName){
		this.dirName = dirName;
		this.subDirectories = new HashMap<String, DirectoryDescriptor>();
		this.files = new HashMap<String, FileDescriptor>();
	}

	public String getDirName() {
		return dirName;
	}

	public Map<String, DirectoryDescriptor> getSubDirectories() {
		return subDirectories;
	}

	public Map<String, FileDescriptor> getFiles() {
		return files;
	}

	

}

package fr.julienvaudour.test.filersync;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import org.jdom.JDOMException;

import fr.julienvaudour.test.filersync.filedescriptor.DirectoryDescriptor;
import fr.julienvaudour.test.filersync.filedescriptor.FileDescriptor;
import fr.julienvaudour.test.filersync.filedescriptor.FileSystem;
import fr.julienvaudour.test.filersync.xmlparser.Parser;
import fuse.Filesystem3;
import fuse.FilesystemConstants;
import fuse.FuseException;
import fuse.FuseOpen;


public class RSync {
	
	private final Parser parser;

	public RSync(final Parser parser){
		this.parser= parser;
	}

	public void rsync(String sourceDescription, Filesystem3 sourceFS, String destinationDescription, Filesystem3 destinationFS) 
			throws JDOMException, IOException, ParseException, FuseException{
		
		FileSystem source = parser.parse(sourceDescription);
		FileSystem destination = parser.parse(destinationDescription);
		
		rsync("",source.getRoot(),sourceFS,destination.getRoot(),destinationFS);
		
	}

	private void rsync(String previousDir, DirectoryDescriptor sourceDir,
			Filesystem3 sourceFS, DirectoryDescriptor destDir,
			Filesystem3 destinationFS) throws FuseException {
		String currentDir = previousDir+"/"+sourceDir.getDirName();
		Set<String> destinationFiles = new HashSet<String>(destDir.getFiles().keySet());
		for(FileDescriptor fd : sourceDir.getFiles().values()){
			destinationFiles.remove(fd.getFileName());
			FileDescriptor destFd = destDir.getFiles().get(fd.getFileName());
			if(!fd.equals(destFd)){
				syncFile(currentDir, fd,sourceFS,destinationFS,destFd==null);
			}
		}
		deleteFile(currentDir, destinationFiles,destinationFS);
		Set<DirectoryDescriptor> destinationDirs = new HashSet<DirectoryDescriptor>(destDir.getSubDirectories().values());
		for(DirectoryDescriptor dd : sourceDir.getSubDirectories().values()){
			destinationDirs.remove(dd);
			DirectoryDescriptor destDd = destDir.getSubDirectories().get(dd.getDirName());
			if(destDd == null){
				destDd = mkdir(currentDir,dd.getDirName(),destinationFS);
			}
			rsync(currentDir,dd,sourceFS,destDd,destinationFS);
		}
		deleteDirs(currentDir,destinationDirs,destinationFS);
		
	}

	private DirectoryDescriptor mkdir(String currentDir, String dirName,
			Filesystem3 destinationFS) throws FuseException {
		// as nothing was said on rights I assume we will use 777 for everything
		destinationFS.mkdir(currentDir+"/"+dirName, 0777);
		return new DirectoryDescriptor(dirName);
	}

	private void deleteDirs(String currentDir, Set<DirectoryDescriptor> destinationDirs,
			Filesystem3 destinationFS) throws FuseException {
		for(DirectoryDescriptor dirName : destinationDirs){
			deleteDir(currentDir+"/"+dirName.getDirName(), dirName,destinationFS);
		}
	
	}

	private void deleteDir(String currentDir, DirectoryDescriptor dirName,
			Filesystem3 destinationFS) throws FuseException {
		for (String file : dirName.getFiles().keySet()){
			destinationFS.unlink(currentDir+"/"+file);
		}
		for(DirectoryDescriptor dir : dirName.getSubDirectories().values()){
			deleteDir(currentDir+"/"+dir.getDirName(), dir, destinationFS);
		}
		destinationFS.rmdir(currentDir);
	}

	private void syncFile(String currentDir, FileDescriptor fd,
			Filesystem3 sourceFS, Filesystem3 destinationFS, boolean shouldcreate) throws FuseException {
		String fullName = currentDir+"/"+fd.getFileName();
		FuseOpen sourceSetter = new FuseOpen();
		Object sourceCallback = new Object();
		sourceSetter.setFh(sourceCallback);
		sourceFS.open(fullName, FilesystemConstants.O_RDONLY, sourceSetter);
		FuseOpen destSetter = new FuseOpen();
		Object destCallback = new Object();
		destSetter.setFh(destCallback);
		if(shouldcreate){
			destinationFS.mknod(fullName,0777 , 0); 
		}
		destinationFS.open(fullName, FilesystemConstants.O_RDWR, destSetter);
		
		destinationFS.truncate(fullName, 0l);
		
		//Not the more elegant, more ever if we have big files
		ByteBuffer byteBuffer = ByteBuffer.allocate(fd.getSize()); 
		sourceFS.read(fullName, sourceSetter.fh, byteBuffer, 0l);
		destinationFS.write(fullName, destSetter.fh, false, byteBuffer, 0l);
		destinationFS.flush(fullName, destSetter.fh);
		destinationFS.release(fullName, destSetter.fh, FilesystemConstants.O_RDWR);
		sourceFS.release(fullName, sourceSetter.fh, FilesystemConstants.O_RDONLY);
		
	}

	private void deleteFile(String currentDir, Set<String> destinationFiles,
			Filesystem3 destinationFS) throws FuseException {
		for(String file : destinationFiles){
			destinationFS.unlink(currentDir+"/"+file);
		}
		
	}
	
	
	
	
}

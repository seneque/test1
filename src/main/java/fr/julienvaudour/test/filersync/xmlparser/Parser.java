package fr.julienvaudour.test.filersync.xmlparser;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import fr.julienvaudour.test.filersync.filedescriptor.DirectoryDescriptor;
import fr.julienvaudour.test.filersync.filedescriptor.FileDescriptor;
import fr.julienvaudour.test.filersync.filedescriptor.FileSystem;

public class Parser {
	
	private final DateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'HHmm");

	
	
	public FileSystem parse(final String input) throws JDOMException, IOException, ParseException{
		FileSystem fs = new FileSystem();
		SAXBuilder saxBuilder = new SAXBuilder();
		Document doc = saxBuilder.build(new StringReader(input));
		Element rootElement = doc.getRootElement();
		parseDirerctory(fs.getRoot(),rootElement);
		
		return fs;
	}

	@SuppressWarnings("unchecked")
	private void parseDirerctory(final DirectoryDescriptor root, final Element dirElement) throws ParseException {
		treatSubdirectories(root,(List<Element>)dirElement.getChildren("tree"));
		treatFiles(root,(List<Element>)dirElement.getChildren("file"));
	}

	private void treatSubdirectories(final DirectoryDescriptor parent, final List<Element> children) throws ParseException {
		for(Element element : children){
			String directoryName = element.getAttributeValue("name");
			DirectoryDescriptor child = new DirectoryDescriptor(directoryName);
			parent.getSubDirectories().put(directoryName, child);
			parseDirerctory(child, element);
		}
	}
	
	private void treatFiles(final DirectoryDescriptor parent, final List<Element> children) throws ParseException {
		for(Element element: children){
//			<file name="a.txt" modif-date="20120102T1030" size="5032"/>
			String fileName = element.getAttributeValue("name");
			int size = Integer.parseInt(element.getAttributeValue("size"));
			Date lastUpdate = formatter.parse(element.getAttributeValue("modif-date"));
			FileDescriptor child = new FileDescriptor(fileName, lastUpdate, size);
			parent.getFiles().put(fileName, child);
		}
	}
	
	
	

}

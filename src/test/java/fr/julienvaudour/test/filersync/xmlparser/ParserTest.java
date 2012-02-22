package fr.julienvaudour.test.filersync.xmlparser;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.jdom.JDOMException;
import org.junit.Assert;
import org.junit.Test;

import fr.julienvaudour.test.filersync.filedescriptor.DirectoryDescriptor;
import fr.julienvaudour.test.filersync.filedescriptor.FileDescriptor;
import fr.julienvaudour.test.filersync.filedescriptor.FileSystem;
import fr.julienvaudour.test.filersync.xmlparser.Parser;

public class ParserTest {
	
	@Test
	public void testEmptyFileSystem() throws JDOMException, IOException, ParseException {
		String input = "<tree name=\"root\">\n</tree>";
		Parser parser = new Parser();
		FileSystem fs = parser.parse(input);
		Assert.assertEquals(true, fs.getRoot().getFiles().isEmpty());
		Assert.assertEquals(true, fs.getRoot().getSubDirectories().isEmpty());
		
	}
	
	@Test
	public void testWithOneDirectory() throws JDOMException, IOException, ParseException {
		String input = "<tree name=\"root\">\n<tree name=\"home\">\n</tree>\n</tree>";
		Parser parser = new Parser();
		FileSystem fs = parser.parse(input);
		Assert.assertEquals(true, fs.getRoot().getFiles().isEmpty());
		Assert.assertEquals(false, fs.getRoot().getSubDirectories().isEmpty());
		Assert.assertTrue(fs.getRoot().getSubDirectories().containsKey("home"));
		Assert.assertTrue(fs.getRoot().getSubDirectories().get("home").getSubDirectories().isEmpty());
		Assert.assertTrue(fs.getRoot().getSubDirectories().get("home").getFiles().isEmpty());

	}
	
	@Test
	public void testWithOneDirectoryWithOneFile() throws JDOMException, IOException, ParseException {
		String input = "<tree name=\"root\">\n<tree name=\"home\">\n<file name=\"a.txt\" modif-date=\"20120102T1030\" size=\"5032\"/>\n</tree>\n</tree>";
		Parser parser = new Parser();
		FileSystem fs = parser.parse(input);
		Assert.assertEquals(true, fs.getRoot().getFiles().isEmpty());
		Assert.assertEquals(false, fs.getRoot().getSubDirectories().isEmpty());
		Assert.assertTrue(fs.getRoot().getSubDirectories().containsKey("home"));
		Assert.assertTrue(fs.getRoot().getSubDirectories().get("home").getSubDirectories().isEmpty());
		Assert.assertEquals(1,fs.getRoot().getSubDirectories().get("home").getFiles().size());
		Assert.assertTrue(fs.getRoot().getSubDirectories().get("home").getFiles().containsKey("a.txt"));
		FileDescriptor fd = fs.getRoot().getSubDirectories().get("home").getFiles().get("a.txt");
		Assert.assertEquals("a.txt", fd.getFileName());
		Assert.assertEquals(5032, fd.getSize());
		GregorianCalendar cal = new GregorianCalendar();
		cal.clear();
		cal.set(2012, Calendar.JANUARY, 2, 10, 30, 0);
		Assert.assertEquals(cal.getTime(), fd.getLastUpdate());
	}
	
	@Test
	public void testMoreComplex() throws JDOMException, IOException, ParseException {
		String input = "<tree name=\"root\">" +
				"<tree name=\"etc\">" +
					"<file name=\"resolv.conf\" modif-date=\"20120102T1031\" size=\"5031\"/>" +
				"</tree>" +
				"<tree name=\"home\">" +
					"<tree name=\"jvaudour\">" +
						"<file name=\"titi\" modif-date=\"20110102T1031\" size=\"1031\"/>" +
						"<file name=\"toto\" modif-date=\"20110102T1032\" size=\"1032\"/>" +
					"</tree>" +
				"</tree>" +
				"</tree>";
		Parser parser = new Parser();
		FileSystem fs = parser.parse(input);
		DirectoryDescriptor root = fs.getRoot();
		Assert.assertTrue(root.getFiles().isEmpty());
		Assert.assertEquals(2, root.getSubDirectories().size());
		Assert.assertTrue(root.getSubDirectories().containsKey("etc"));
		DirectoryDescriptor etc = root.getSubDirectories().get("etc");
		Assert.assertEquals(1, etc.getFiles().size());
		Assert.assertTrue(etc.getSubDirectories().isEmpty());
		Assert.assertTrue(etc.getFiles().containsKey("resolv.conf"));
		Assert.assertEquals(5031, etc.getFiles().get("resolv.conf").getSize());
		GregorianCalendar cal = new GregorianCalendar();
		cal.clear();
		cal.set(2012, Calendar.JANUARY, 2, 10, 31, 0);
		Assert.assertEquals(cal.getTime(), etc.getFiles().get("resolv.conf").getLastUpdate());
		
		Assert.assertTrue(root.getSubDirectories().containsKey("home"));
		Assert.assertTrue(root.getSubDirectories().get("home").getFiles().isEmpty());
		Assert.assertEquals(1,root.getSubDirectories().get("home").getSubDirectories().size());
		DirectoryDescriptor jvaudour = root.getSubDirectories().get("home").getSubDirectories().get("jvaudour");
		Assert.assertTrue(jvaudour.getSubDirectories().isEmpty());
		Assert.assertEquals(2, jvaudour.getFiles().size());
		FileDescriptor titi = jvaudour.getFiles().get("titi");
		Assert.assertEquals("titi", titi.getFileName());
		Assert.assertEquals(1031, titi.getSize());
		cal.clear();
		cal.set(2011, Calendar.JANUARY, 2, 10, 31, 0);
		Assert.assertEquals(cal.getTime(), titi.getLastUpdate());
		FileDescriptor toto = jvaudour.getFiles().get("toto");
		Assert.assertEquals("toto", toto.getFileName());
		Assert.assertEquals(1032, toto.getSize());
		cal.clear();
		cal.set(2011, Calendar.JANUARY, 2, 10, 32, 0);
		Assert.assertEquals(cal.getTime(), toto.getLastUpdate());
		
		
	}

}

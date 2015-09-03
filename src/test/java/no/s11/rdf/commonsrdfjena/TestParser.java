package no.s11.rdf.commonsrdfjena;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.simple.SimpleRDFTermFactory;
import org.junit.Before;
import org.junit.Test;

import no.s11.rdf.commonsrdfjena.Parser;
import no.s11.rdf.commonsrdfjena.impl.JenaParser;

public class TestParser {

	static SimpleRDFTermFactory factory = new SimpleRDFTermFactory();

	IRI s1 = factory.createIRI("http://example.com/s1");
	IRI s2 = factory.createIRI("http://example.com/s2");
	IRI s3 = factory.createIRI("http://example.com/s3");
	IRI p1 = factory.createIRI("http://example.com/p1");
	IRI p2 = factory.createIRI("http://example.com/p2");
	IRI p3 = factory.createIRI("http://example.com/p3");
	IRI p4 = factory.createIRI("http://example.com/p4");
	IRI o1 = factory.createIRI("http://example.com/o1");

	private Path examplePath;

	Parser pf = new JenaParser();

	@Test(expected=IllegalStateException.class)
	public void parseNoSource() throws Exception {
		pf.parse();
	}
	
	
	@Test
	public void parseFile() throws Exception {
		Parser pfWithPath = pf.path(examplePath);
		Graph g1 = pfWithPath.parse();
		checkParsedGraph(g1);
		Graph g2 = pfWithPath.parse();
		assertNotSame(g1, g2);
	}

	@Test
	public void parseFileToGraph() throws Exception {
		Graph g = factory.createGraph();
		Graph g2 = pf.graph(g).path(examplePath).parse();
		assertSame(g2, g);
		checkParsedGraph(g2);
	}
	
	@Before
	public void writeExample() throws IOException {
		examplePath = Files.createTempFile("test", ".nt");
		InputStream testStream = getClass().getResourceAsStream("/test.ntriples");
		Files.copy(testStream, examplePath, StandardCopyOption.REPLACE_EXISTING);				
	}

	@Test
	public void parseFileWithContentType() throws Exception {
		Graph g = pf.path(examplePath).contentType(Parser.NTRIPLES).parse();
		checkParsedGraph(g);
	}
	
	@Test
	public void parseFileWithAlternateContentType() throws Exception {
		// Ntriples can be parsed as Turtle (but not vice versa)
		Graph g = pf.path(examplePath).contentType(Parser.TURTLE).parse();
		checkParsedGraph(g);
	}
	
	@Test(expected=Exception.class)
	public void parseFileWithWrongType() throws Exception {
		pf.path(examplePath).contentType(Parser.RDFXML).parse();
	}
	
	private void checkParsedGraph(Graph g) {
		assertEquals(4l, g.size());
		assertTrue(g.contains(s1, p1, o1));
		assertTrue(g.contains(s2, p2, factory.createLiteral("String")));
		RDFTerm b1 = g.getTriples(s3, p3, null).findFirst().get().getObject();
		assertTrue(b1 instanceof BlankNode);
		assertEquals(s1, g.getTriples((BlankNode) b1, p4, null).findFirst().get().getObject());
	}
}

package no.s11.rdf.commonsrdfjena;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.simple.SimpleRDFTermFactory;
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

	@Test
	public void testName() throws Exception {
		//JenaParser p = new JenaParser();
		Graph g = factory.createGraph();
		Path path = Files.createTempFile("test", ".nt");
		System.out.println(path);
		InputStream testStream = getClass().getResourceAsStream("/test.ntriples");
		Files.copy(testStream, path, StandardCopyOption.REPLACE_EXISTING);
		Parser pf = new JenaParser();
		g = pf.graph(g).path(path).parse();
		assertEquals(4l, g.size());

		assertTrue(g.contains(s1, p1, o1));
		assertTrue(g.contains(s2, p2, factory.createLiteral("String")));
		RDFTerm b1 = g.getTriples(s3, p3, null).findFirst().get().getObject();
		assertTrue(b1 instanceof BlankNode);
		assertEquals(s1, g.getTriples((BlankNode) b1, p4, null).findFirst().get().getObject());

	}
}

package no.s11.rdf.commonsrdfjena;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTermFactory;

public interface ParserFactory extends Cloneable {
	
	String JSONLD = "application/ld+json";
	String TURTLE = "text/turtle";
	String NQUADS = "application/n-quads";
	String NTRIPLES = "application/n-triples";
	String RDFXML = "application/rdf+xml";
	String TRIG = "application/trig";
	
	IRI getBase();

	Path getFile();

	String getContentType();

	RDFTermFactory getRDFTermFactory();

	URL getUrl();

	InputStream getInputStream();

	ParserFactory clone();

	Graph getGraph();

	ParserFactory rdfTermFactory(RDFTermFactory rdfTermFactory);

	ParserFactory base(IRI base);

	ParserFactory path(Path file);

	ParserFactory contentType(String contentType);

	ParserFactory inputStream(InputStream inputStream);

	ParserFactory base(String base);

	ParserFactory url(URL url);

	ParserFactory url(String url);

	ParserFactory graph(Graph graph);

	Graph parse() throws IOException;
	
}
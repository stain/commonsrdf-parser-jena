package no.s11.rdf.commonsrdfjena;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTermFactory;

public interface Parser extends Cloneable {
	
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

	Parser clone();

	Graph getGraph();

	Parser rdfTermFactory(RDFTermFactory rdfTermFactory);

	Parser base(IRI base);

	Parser path(Path file);

	Parser contentType(String contentType);

	Parser inputStream(InputStream inputStream);

	Parser base(String base);

	Parser url(URL url);

	Parser url(String url);

	Parser graph(Graph graph);

	Graph parse() throws IOException;
	
}
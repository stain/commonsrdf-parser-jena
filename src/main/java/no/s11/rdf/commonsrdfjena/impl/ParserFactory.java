package no.s11.rdf.commonsrdfjena.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.RDFTermFactory;
import org.apache.commons.rdf.simple.SimpleRDFTermFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.ReaderRIOT;
import org.apache.jena.riot.system.StreamRDF;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.sparql.util.Context;


public class ParserFactory {

	public static final String JSONLD = "application/ld+json";
	public static final String TURTLE = "text/turtle";
	public static final String NQUADS = "application/n-quads";
	public static final String NTRIPLES = "application/n-triples";
	public static final String RDFXML = "application/rdf+xml";
	public static final String TRIG = "application/trig";
	
	private RDFTermFactory rdfTermFactory;
	private IRI base;
	private Path file;
	private String contentType;
	private URL url;
	private InputStream inputStream;
	private Graph graph;

	public IRI getBase() {
		return base;
	}

	public Path getFile() {
		return file;
	}

	public String getContentType() {
		return contentType;
	}

	public RDFTermFactory getRDFTermFactory() {
		return rdfTermFactory;
	}
	
	public URL getUrl() {
		return url;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public ParserFactory clone() {
		try {
			return (ParserFactory) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}		
	}
	
	
	public Graph getGraph() {
		return graph;
	}
	
	public ParserFactory rdfTermFactory(RDFTermFactory rdfTermFactory) {
		ParserFactory f = clone();
		f.rdfTermFactory = rdfTermFactory;
		return f;
	}

	public ParserFactory base(IRI base) {
		ParserFactory f = clone();
		f.base = base;
		return f;
	}
	
	public ParserFactory path(Path file) {
		ParserFactory f = clone();
		f.resetInputs();
		f.file = file;
		return f;
	}

	private void resetInputs() {
		this.file = null;
		this.url = null;
		this.inputStream = null;
	}

	public ParserFactory contentType(String contentType) {
		ParserFactory f = clone();
		f.contentType = contentType;
		return f;
	}

	public ParserFactory inputStream(InputStream inputStream) {
		ParserFactory f = clone();
		f.resetInputs();
		f.inputStream = inputStream;
		return f;
	}
	
	
	public ParserFactory base(String base) {
		ParserFactory f = clone();
		f.base = rdfTermFactory.createIRI(base);
		return f;
	}


	public ParserFactory url(URL url) {
		ParserFactory f = clone();
		f.resetInputs();
		f.url = url;
		return f;
	}

	public ParserFactory url(String url) {
		try {
			return url(new URL(url));
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
	}	
	
	public ParserFactory graph(Graph graph) {
		ParserFactory f = clone();
		f.graph = graph;
		return f;
	}

	public Graph parse() throws IOException {
		if (rdfTermFactory == null) {
			rdfTermFactory = new SimpleRDFTermFactory();
		}
		final Graph g;
		if (graph == null) {
			g = graph;
		} else {
			g = rdfTermFactory.createGraph();
		}
		String baseUrl;
		if (base != null) {
			baseUrl = base.getIRIString();
		} else if (url != null) {
			baseUrl = url.toExternalForm();
		} else if (file != null) {
			baseUrl = file.toUri().toString();
		} else {
			baseUrl = "app://" + UUID.randomUUID() + "/";
		}
		
		boolean mustClose = false;				
		if (inputStream == null) {
			if (url != null) {
				inputStream = url.openStream();
				mustClose = true;
			} else if (file != null) {
				inputStream = Files.newInputStream(file);
				mustClose = true;
			} else {
				throw new IllegalStateException("Must set url(), file() or inputStream()");
			}
		} 

		URLConnection urlCon = null;
		Lang lang = null;
		if (contentType != null) {
			lang = RDFLanguages.contentTypeToLang(contentType);
			if (lang == null) {
				throw new IllegalStateException("Unsupported contentType: " + contentType);
			}
		} else {
			if (url != null) {
				urlCon = url.openConnection();
				String ct = urlCon
						.getContentType();				
				if (ct != null) {					
					lang = RDFLanguages.contentTypeToLang(ct);
				} else {
					lang = RDFLanguages.resourceNameToLang(url.toExternalForm());
				}
			} else if (file != null) {
				lang = RDFLanguages.filenameToLang(file.toString());
			}
		}
		if (lang == null) {
			throw new IllegalStateException("Can't determine RDF language");
		}
		
		ReaderRIOT reader = RDFDataMgr.createReader(lang);
		if (reader == null) {
			throw new IllegalArgumentException("No RDF reader for language " + lang);
		}
		try (InputStream in = Files.newInputStream(file)) {
			StreamRDF streamRDF = new StreamRDF() {
				
				public void triple(Triple triple) {
					g.add(toCommonsRDF(triple));					
				}				
				public void start() {
				}				
				public void quad(Quad quad) {
					// TODO: How should quads be handled?
					g.add(toCommonsRDF(quad.asTriple()));
				}				
				public void prefix(String prefix, String ns) {
				}				
				public void finish() {
				}				
				public void base(String base) {
				}
			};
			Context arg4 = new Context();
			reader.read(in, file.toUri().toASCIIString(), lang.getContentType(), streamRDF, arg4);
			return g;
		}		
	}


	protected org.apache.commons.rdf.api.Triple toCommonsRDF(Triple triple) {
		return rdfTermFactory.createTriple((BlankNodeOrIRI) toCommonsRDF(triple.getSubject()),
				(IRI) toCommonsRDF(triple.getPredicate()),
				toCommonsRDF(triple.getObject()));
	}

	private RDFTerm toCommonsRDF(Node node) {
		if (node.isURI()) {
			return rdfTermFactory.createIRI(node.getURI());			
		}
		if (node.isBlank()) { 
			return rdfTermFactory.createBlankNode(node.getBlankNodeId().getLabelString());
		}
		if (node.isLiteral()) {
			String lexicalForm = node.getLiteralLexicalForm();
			if (node.getLiteralLanguage() != null && ! node.getLiteralLanguage().isEmpty()) {
				return rdfTermFactory.createLiteral(lexicalForm, node.getLiteralLanguage());
			} else if (node.getLiteralDatatypeURI() != null) {
				return rdfTermFactory.createLiteral(lexicalForm, 
						rdfTermFactory.createIRI(node.getLiteralDatatypeURI()));				
			} else {
				return rdfTermFactory.createLiteral(lexicalForm);
			}		
		}
		throw new IllegalArgumentException("Can't convert to commonsRDF RDFNode: " + node);
	}
}

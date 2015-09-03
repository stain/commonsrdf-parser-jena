package no.s11.rdf.commonsrdfjena.impl;

import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.UUID;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.simple.SimpleRDFTermFactory;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.ReaderRIOT;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.util.Context;

public class JenaParser extends AbstractParser {

	@Override
	public Graph parse() throws IOException {
		// Clone so we can modify rdfTermFactory/graph etc
		return ((JenaParser)clone()).parseImpl();
	}
	
	private Graph parseImpl() throws IOException {
		if (rdfTermFactory == null) {
			rdfTermFactory = new SimpleRDFTermFactory();
		}
		if (graph == null) {
			graph = rdfTermFactory.createGraph();
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
		try {
			StreamRDF streamRDF = new StreamRDF() {
				
				public void triple(Triple triple) {
					graph.add(toCommonsRDF(triple));					
				}				
				public void start() {
				}				
				public void quad(Quad quad) {
					// TODO: How should quads be handled?
					graph.add(toCommonsRDF(quad.asTriple()));
				}				
				public void prefix(String prefix, String ns) {
				}				
				public void finish() {
				}				
				public void base(String base) {
				}
			};
			Context ctx = new Context();
			reader.read(inputStream, baseUrl, lang.getContentType(), streamRDF, ctx);
			return graph;
		} finally {
			if (mustClose) { 
				inputStream.close();
			}
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

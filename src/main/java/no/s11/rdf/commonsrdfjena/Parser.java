package no.s11.rdf.commonsrdfjena;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.RDFTermFactory;
import org.apache.commons.rdf.simple.SimpleRDFTermFactory;
import org.apache.jena.atlas.web.ContentType;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.ReaderRIOT;
import org.apache.jena.riot.system.StreamRDF;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.sparql.util.Context;

public class Parser {
	public void parseInto(Path file, final Graph g) throws IOException {
		factory.remove();
		ReaderRIOT reader = RDFDataMgr.createReader(Lang.NTRIPLES);
		try (InputStream in = Files.newInputStream(file)) {
			ContentType contentType = Lang.NTRIPLES.getContentType();
			StreamRDF streamRDF = new StreamRDF() {
				
				public void triple(Triple triple) {
					g.add(toCommonsRDF(triple));					
				}				
				public void start() {
				}				
				public void quad(Quad quad) {
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
			reader.read(in, file.toUri().toASCIIString(), contentType, streamRDF, arg4);
			
		} finally {
			factory.remove();
		}		
	}

	private ThreadLocal<RDFTermFactory> factory = new ThreadLocal<RDFTermFactory>(){
		protected RDFTermFactory initialValue() {
			return new SimpleRDFTermFactory();
		};
	};

	protected org.apache.commons.rdf.api.Triple toCommonsRDF(Triple triple) {
		return factory.get().createTriple((BlankNodeOrIRI) toCommonsRDF(triple.getSubject()),
				(IRI) toCommonsRDF(triple.getPredicate()),
				toCommonsRDF(triple.getObject()));
	}

	private RDFTerm toCommonsRDF(Node node) {
		if (node.isURI()) {
			return factory.get().createIRI(node.getURI());			
		}
		if (node.isBlank()) { 
			return factory.get().createBlankNode(node.getBlankNodeId().getLabelString());
		}
		if (node.isLiteral()) {
			String lexicalForm = node.getLiteralLexicalForm();
			if (node.getLiteralLanguage() != null && ! node.getLiteralLanguage().isEmpty()) {
				return factory.get().createLiteral(lexicalForm, node.getLiteralLanguage());
			} else if (node.getLiteralDatatypeURI() != null) {
				return factory.get().createLiteral(lexicalForm, 
						factory.get().createIRI(node.getLiteralDatatypeURI()));				
			} else {
				return factory.get().createLiteral(lexicalForm);
			}		
		}
		throw new IllegalArgumentException("Can't convert to commonsRDF RDFNode: " + node);
	}
}

package no.s11.rdf.commonsrdfjena.impl;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTermFactory;
import org.apache.commons.rdf.simple.SimpleRDFTermFactory;

import no.s11.rdf.commonsrdfjena.ParserFactory;

public abstract class AbstractParserFactory implements ParserFactory {

	protected RDFTermFactory rdfTermFactory;
	protected IRI base;
	protected Path file;
	protected String contentType;
	protected URL url;
	protected InputStream inputStream;
	protected Graph graph;

	public AbstractParserFactory() {
		super();
	}

	@Override
	public IRI getBase() {
		return base;
	}

	@Override
	public Path getFile() {
		return file;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public RDFTermFactory getRDFTermFactory() {
		return rdfTermFactory;
	}

	@Override
	public URL getUrl() {
		return url;
	}

	@Override
	public InputStream getInputStream() {
		return inputStream;
	}
	
	@Override
	public AbstractParserFactory clone() {
		try {
			return (AbstractParserFactory) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}		
	}

	@Override
	public Graph getGraph() {
		return graph;
	}

	@Override
	public ParserFactory rdfTermFactory(RDFTermFactory rdfTermFactory) {
		AbstractParserFactory f = clone();
		f.rdfTermFactory = rdfTermFactory;
		return f;
	}

	@Override
	public ParserFactory base(IRI base) {
		AbstractParserFactory f = clone();
		f.base = base;
		return f;
	}

	@Override
	public ParserFactory path(Path file) {
		AbstractParserFactory f = clone();
		f.resetInputs();
		f.file = file;
		return f;
	}

	@Override
	public ParserFactory contentType(String contentType) {
		AbstractParserFactory f = clone();
		f.contentType = contentType;
		return f;
	}

	@Override
	public ParserFactory inputStream(InputStream inputStream) {
		AbstractParserFactory f = clone();
		f.resetInputs();
		f.inputStream = inputStream;
		return f;
	}

	@Override
	public ParserFactory base(String base) {
		AbstractParserFactory f = clone();
		if (f.rdfTermFactory != null) {
			f.base = f.rdfTermFactory.createIRI(base);			
		} else {
			f.base = new SimpleRDFTermFactory().createIRI(base);
		}
		return f;
	}

	@Override
	public ParserFactory url(URL url) {
		AbstractParserFactory f = clone();
		f.resetInputs();
		f.url = url;
		return f;
	}

	@Override
	public ParserFactory url(String url) {
		try {
			return url(new URL(url));
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public ParserFactory graph(Graph graph) {
		AbstractParserFactory f = clone();
		f.graph = graph;
		return f;
	}

	protected void resetInputs() {
		this.file = null;
		this.url = null;
		this.inputStream = null;
	}
	
}
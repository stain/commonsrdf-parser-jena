package no.s11.rdf.commonsrdfjena.impl;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTermFactory;
import org.apache.commons.rdf.simple.SimpleRDFTermFactory;

import no.s11.rdf.commonsrdfjena.Parser;

public abstract class AbstractParser implements Parser {

	protected RDFTermFactory rdfTermFactory;
	protected IRI base;
	protected Path file;
	protected String contentType;
	protected URL url;
	protected InputStream inputStream;
	protected Graph graph;

	public AbstractParser() {
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
	public AbstractParser clone() {
		try {
			return (AbstractParser) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}		
	}

	@Override
	public Graph getGraph() {
		return graph;
	}

	@Override
	public Parser rdfTermFactory(RDFTermFactory rdfTermFactory) {
		AbstractParser f = clone();
		f.rdfTermFactory = rdfTermFactory;
		return f;
	}

	@Override
	public Parser base(IRI base) {
		AbstractParser f = clone();
		f.base = base;
		return f;
	}

	@Override
	public Parser path(Path file) {
		AbstractParser f = clone();
		f.resetInputs();
		f.file = file;
		return f;
	}

	@Override
	public Parser contentType(String contentType) {
		AbstractParser f = clone();
		f.contentType = contentType;
		return f;
	}

	@Override
	public Parser inputStream(InputStream inputStream) {
		AbstractParser f = clone();
		f.resetInputs();
		f.inputStream = inputStream;
		return f;
	}

	@Override
	public Parser base(String base) {
		AbstractParser f = clone();
		if (f.rdfTermFactory != null) {
			f.base = f.rdfTermFactory.createIRI(base);			
		} else {
			f.base = new SimpleRDFTermFactory().createIRI(base);
		}
		return f;
	}

	@Override
	public Parser url(URL url) {
		AbstractParser f = clone();
		f.resetInputs();
		f.url = url;
		return f;
	}

	@Override
	public Parser url(String url) {
		try {
			return url(new URL(url));
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public Parser graph(Graph graph) {
		AbstractParser f = clone();
		f.graph = graph;
		return f;
	}

	protected void resetInputs() {
		this.file = null;
		this.url = null;
		this.inputStream = null;
	}
	
}
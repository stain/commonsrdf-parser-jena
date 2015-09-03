# commonsrdf-parser-jena
Parsers for Apache Commons RDF, backed by Jena

* Author: [Stian Soiland-Reyes](http://orcid.org/0000-0001-9842-9718)
* License: [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0)

This is a small experiment to create an RDF `Parser` interface for
[Apache Commons RDF](http://commonsrdf.incubator.apache.org/), implemented 
by using [Apache Jena](https://jena.apache.org/documentation/io/)'s 
[RiotReader](https://jena.apache.org/documentation/javadoc/arq/org/apache/jena/riot/RiotReader.html)
parsers as discovered through 
[RDFDataMgr](https://jena.apache.org/documentation/javadoc/arq/org/apache/jena/riot/RDFDataMgr.html).

## Building

    mvn clean install
    
## Usage

The [Parser](src/main/java/no/s11/rdf/commonsrdfjena/Parser.java) interface works by the factory 
pattern to set various parser options. Note that each option returns a new, modified 
instance of the Parser, which is therefore effectively immutable and reusable 
for multiple parser sessions.

You must minimally call one of the methods `path`, `url` or `inputStream` to provide the source
of the RDF resource to be parsed. After setting all options, call `parse`.

Example:

```java
Parser parser = new JenaParser();
Path filePath = Paths.get("/tmp/file.rdf");
Graph g = parser.contentType(Parser.RDFXML).path(examplePath).parse();
```

## Contributions

Feel free to contribute by adding a [pull request](https://github.com/stain/commonsrdf-parser-jena/pulls) or
[raise an issue](https://github.com/stain/commonsrdf-parser-jena/issues).

For any discussion or comments, use the [dev@commonsrdf](http://commonsrdf.incubator.apache.org/mail-lists.html)
mailing list.

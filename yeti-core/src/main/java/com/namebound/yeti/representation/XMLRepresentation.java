package com.namebound.yeti.representation;

import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 *
 * @author Laurian Gridinoc
 */
public class XMLRepresentation extends Representation {

    private Document document;

    public XMLRepresentation(Representation representation) {
        super(representation);
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        setDocument(document(factory), representation.mediaType());
    }

    public XMLRepresentation(JSONRepresentation jsonRepresentation) {
        super();
        setDocument(document(new StAXSource(jsonRepresentation.xmlReader()), null), null);
    }

    public XMLRepresentation(Document document) {
        this(document, null);
    }

    public XMLRepresentation(Document document, String mediaType) {
        super();
        setDocument(document, mediaType);
    }

    public XMLRepresentation(Representation representation, DocumentBuilderFactory factory) {
        this(representation, factory, null);
    }

    public XMLRepresentation(Representation representation, DocumentBuilderFactory factory, String mediaType) {
        this(representation);
        setDocument(document(factory), mediaType);
    }

    public XMLRepresentation(Representation representation, XMLReader reader) {
        this(representation, reader, null);
    }

    public XMLRepresentation(Representation representation, XMLReader reader, String mediaType) {
        this(representation);
        setDocument(document(reader), mediaType);
    }

    public XMLRepresentation(Representation representation, Representation xsl) {
        this(representation, xsl, null);
    }

    public XMLRepresentation(Representation representation, Representation xsl, String mediaType) {
        this(representation);
        setDocument(document(xsl), mediaType);
    }

    public XMLRepresentation(Representation representation, XMLReader reader, Representation xsl, String mediaType) {
        this(representation);
        setDocument(document(reader, xsl), mediaType);
    }

    public XMLRepresentation(Representation representation, Source source, Representation xsl, String mediaType) {
        this(representation);
        setDocument(document(source, xsl), mediaType);
    }

    private Document setDocument(Document document, String mediaType) {
        if (document == null) return this.document;
        
        this.document = document;
        
        sync();

        if (mediaType == null) mediaType = "application/xml";
        ((ByteArrayEntity) this.entity).setContentType(mediaType);

        return document;
    }

    private void sync() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Source source = new DOMSource(document);
        Result result = new StreamResult(buffer);
        Transformer transformer;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(source, result);
        } catch (Exception ex) {
            Logger.getLogger(XMLRepresentation.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.entity = new ByteArrayEntity(buffer.toByteArray());
    }

    public Document document() {
        return document;
    }

    @Override
    public HttpEntity entity() {
        sync();
        return this.entity;
    }

    private Document document(DocumentBuilderFactory factory) {
        Document doc = null;
        try {
            doc = factory.newDocumentBuilder().parse(super.entity().getContent());
        } catch (Exception ex) {
            Logger.getLogger(XMLRepresentation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return doc;
    }

    private Document document(XMLReader reader) {
        return document(reader, null);
    }

    private Document document(Representation xsl) {
        try {
            return document(new StreamSource(this.entity().getContent()), xsl);
        } catch (Exception ex) {
            Logger.getLogger(XMLRepresentation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private Document document(XMLReader reader, Representation xsl) {
        try {
            return document(new SAXSource(reader, new InputSource(super.entity().getContent())), xsl);
        } catch (Exception ex) {
            Logger.getLogger(XMLRepresentation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    protected Document document(Source source, Representation xsl) {
        Document doc = null;
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer;

            if (xsl != null) {
                Templates template = factory.newTemplates(new StreamSource(xsl.entity().getContent()));
                transformer = template.newTransformer();
            } else {
                transformer = factory.newTransformer();
            }

            DOMResult result = new DOMResult();
            transformer.transform(source, result);
            doc = result.getNode().getOwnerDocument();

        } catch (Exception ex) {
            Logger.getLogger(XMLRepresentation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return doc;
    }
    
}
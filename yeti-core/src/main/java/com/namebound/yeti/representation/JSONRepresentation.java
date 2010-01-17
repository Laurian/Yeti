package com.namebound.yeti.representation;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Result;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.mapped.MappedXMLStreamReader;

/**
 *
 * @author Laurian Gridinoc
 */
public class JSONRepresentation extends Representation {

    private JSONObject json;

    public JSONRepresentation(Representation representation) {
        super(representation);
        try {
            setJson(new JSONObject(EntityUtils.toString(representation.entity())));
        } catch (Exception ex) {
            Logger.getLogger(JSONRepresentation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public JSONRepresentation(XMLRepresentation xml) {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Templates template = factory.newTemplates(new StreamSource(
                        this.getClass().getClassLoader().getResourceAsStream("xml2json-xslt/xml2json.xslt")
                    ));
            Transformer transformer = template.newTransformer();

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            Result result = new StreamResult(buffer);
            transformer.transform(new DOMSource(xml.document()), result);

            setJson(new JSONObject(new String(buffer.toByteArray())));
        } catch (Exception ex) {
            Logger.getLogger(JSONRepresentation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public JSONRepresentation(String content) {
        try {
            setJson(new JSONObject(content));
        } catch (JSONException ex) {
            Logger.getLogger(JSONRepresentation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private JSONObject setJson(JSONObject json) {
        if (json == null) return this.json;

        this.json = json;

        sync();
        return json;
    }

    private void sync() {
        try {
            this.entity = new StringEntity(json.toString());
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(JSONRepresentation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public HttpEntity entity() {
        sync();
        return this.entity;
    }
    
    public JSONObject json() {
        if (json != null) return json;
        try {
            return setJson(new JSONObject(EntityUtils.toString(super.entity())));
        } catch (Exception ex) {
            Logger.getLogger(JSONRepresentation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    protected XMLStreamReader xmlReader() {
        try {
            return new MappedXMLStreamReader(json);
        } catch (Exception ex) {
            Logger.getLogger(JSONRepresentation.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

}

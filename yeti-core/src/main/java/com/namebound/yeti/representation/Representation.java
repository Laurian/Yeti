package com.namebound.yeti.representation;

import com.namebound.yeti.resource.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Laurian Gridinoc
 */
public class Representation implements Cloneable {

    //TODO add contentID, sha1, ETag?
    protected Representation wrapped;
    protected Resource resource;

    protected HttpEntity entity;
    protected HttpResponse response;

    public Representation() {
    }

    public Representation(Representation representation) {
        this.wrap(representation);
    }

    public Representation(Resource resource) {
        this.resource = resource;
    }

    public Representation(Resource resource, HttpEntity entity) {
        this(resource, entity, false);
    }

    public Representation(Resource resource, HttpEntity entity, boolean buffer) {
        this(resource);
        this.setEntity(entity, buffer);
    }

    public Representation(Resource resource, HttpResponse response) {
        this(resource, response, false);
    }

    public Representation(Resource resource, HttpResponse response, boolean buffer) {
        this(resource);
        this.response = response;
        this.setRepresentationOf(response);
        this.setEntity(response.getEntity(), buffer);
    }

    //string

    public Representation(String content) {
        this(content, "text/plain");
    }

    public Representation(String content, String mediaType) {
        try {
            this.entity = new StringEntity(content, null);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Representation.class.getName()).log(Level.SEVERE, null, ex);
        }
        ((StringEntity) this.entity).setContentType(mediaType);
    }

    public Representation(String content, String mediaType, String charset) {
        try {
            this.entity = new StringEntity(content, charset);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Representation.class.getName()).log(Level.SEVERE, null, ex);
        }
        ((StringEntity) this.entity).setContentType(mediaType);
    }

    // bytes

    public Representation(byte[] bytes) {
        this.entity = new ByteArrayEntity(bytes);
    }

    public Representation(byte[] bytes, String mediaType) {
        this(bytes);
        ((ByteArrayEntity) this.entity).setContentType(mediaType);
    }

    public Representation(byte[] bytes, String mediaType, String encoding) {
        this(bytes, mediaType);
        ((ByteArrayEntity) this.entity).setContentEncoding(encoding);
    }

    //file

    public Representation(File file, String contentType) {
        this.entity = new FileEntity(file, contentType);
    }







    protected Representation setRepresentationOf(Resource resource) {
        this.resource = resource;
        return this;
    }

    //TODO hmmm, overide resource?
    public Representation setRepresentationOf(HttpResponse response) {
        if (response.getFirstHeader("Location") == null) return this;

        Logger.getLogger(Representation.class.getName()).log(Level.INFO,
                    "[Header] Location: " + response.getFirstHeader("Location").getValue()
                );
        this.resource = new Resource(response.getFirstHeader("Location").getValue());
        return this;
    }

    public Resource representationOf() {
        if (wrapped != null) {
            return wrapped.representationOf();
        }
        return resource;
    }

    public Representation wrap(Representation representation) {
        if (resource == null) {
            this.wrapped = representation;
        } else {
            throw new RuntimeException("Can wrapp only once!"); //TODO what about object reuse?
        }
        return this;
    }

    public Representation wrapped() {
        return wrapped;
    }

    public <T extends Representation> T wrapWith(T wrapper) {
        return (T) wrapper.wrap(this);
    }

    public Representation tee(Representation wrapper) {
        wrapper.wrap(this);
        return this;
    }

    public HttpResponse response() {
        if (wrapped != null) {
            return ((Representation) wrapped).response();
        }
        return response;
    }

    /*protected Representation response(HttpResponse response) {
    if (wrapped != null) {
    ((Representation) wrapped).response(response);
    } else {
    this.response = response;
    }
    return this;
    }*/
    public HttpEntity entity() {
        if (wrapped != null) {
            return ((Representation) wrapped).entity();
        }
        return entity;
    }

    public String string() {
        String content = null;
        try {
            content = EntityUtils.toString(entity());
        } catch (Exception ex) {
            Logger.getLogger(Representation.class.getName()).log(Level.SEVERE, null, ex);
        }

        return content;
    }

    public byte[] bytes() {
        try {
            return EntityUtils.toByteArray(entity());
        } catch (IOException ex) {
            Logger.getLogger(Representation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Representation copy(Representation copy) {
        copy = this;
        return this;
    }
    
    private Representation setEntity(HttpEntity entity, boolean buffer) {
        if (buffer && !(entity instanceof BufferedHttpEntity)) {//FIXME move this to another class
            try {
                entity = new BufferedHttpEntity(entity);
            } catch (IOException ex) {
                Logger.getLogger(Representation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (wrapped != null) {
            ((Representation) wrapped).setEntity(entity, buffer);
        } else {
            this.entity = entity;
        }        
        return this;
    }

    public String mediaType() {
        if (entity().getContentType() != null && entity().getContentType().getValue().indexOf('/') != -1) {
            String contentType = entity().getContentType().getValue();

            int i = contentType.indexOf(';');
            if (i != -1) {
                contentType = contentType.substring(0, i);
            }

            return contentType;
        } else {
            return "application/octet-stream";
        }
    }

    public String charset() {
        return EntityUtils.getContentCharSet(entity());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //TODO, deep clonning?
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Representation) {
            return entity().equals(
                    ((Representation) object).entity()); //todo etag, sha1?
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.entity != null ? this.entity.hashCode() : 0);//FIXME
        return hash;
    }

    @Override
    public String toString() {
        return "representationOf " + representationOf().toString();
    }

    public Representation buffer() {
        return buffer(true);
    }
    
    public Representation buffer(boolean b) {
        setEntity(entity(), true);
        return this;
    }

    public Representation tee(OutputStream out) {
        this.buffer(true);
        try {
            entity().writeTo(out);
        } catch (IOException ex) {
            Logger.getLogger(Representation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this;
    }

    public Representation tee(File file) {
        //return new FileRepresentation(this, file);
        try {
            FileOutputStream out = new FileOutputStream(file);
            this.entity().writeTo(out);
            out.close();

            this.entity = new FileEntity(file, this.mediaType());
        } catch (Exception ex) {
            Logger.getLogger(Representation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this;
    }

    public Representation bufferToFile() {
        try {
            File tempFile = File.createTempFile("Yeti", ".tmp");
            tempFile.deleteOnExit();
            return tee(tempFile);
        } catch (IOException ex) {
            Logger.getLogger(Representation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}

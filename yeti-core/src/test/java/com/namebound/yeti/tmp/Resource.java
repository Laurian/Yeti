package com.namebound.yeti.tmp;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Laurian Gridinoc
 */
public class Resource {

    private URL url;

    private Resource parent;

    public Resource() {
        try {
            this.url = new URL("http://www.w3.org/1999/02/22-rdf-syntax-ns#nil");
        } catch (MalformedURLException ex) {
            Logger.getLogger(Resource.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Resource (URL url) {
        this.url = url;
    }

    public Resource (String url) throws MalformedURLException {
        this(new URL(url));
    }

    public Resource (Resource context, String spec) throws MalformedURLException {
        this(new URL(context.url, spec));
        this.parent = context;
    }

    protected String parameter(String name, String value, String delimitator) throws UnsupportedEncodingException {
        return ((value != null)?(delimitator + name + "=" + URLEncoder.encode(value, "UTF-8")):"");
    }

    protected String parameter(String name, int greaterThanZero, String delimitator) throws UnsupportedEncodingException {
        if(greaterThanZero > 0){
            return parameter(name, Integer.toString(greaterThanZero), delimitator);
        }
        return "";
    }
    
    public Resource parent() {
        if (this.parent == null) {
            return new Resource();
        }
        return this.parent;
    }

    public URL url() {
        return url;
    }

    public Resource augmentWith(Store store, String xsl, String contentType) throws MalformedURLException, UnsupportedEncodingException {
        return store.augment(this, xsl, contentType);
    }

    public Representation representation(Accessor accessor) {
        return get(accessor);
    }

    public Representation get(Accessor accessor) {
        return null;
    }

    public Representation post(Accessor accessor) {
        return null;
    }

    public Representation put(Accessor accessor) {
        return null;
    }

    public Representation delete(Accessor accessor) {
        return null;
    }


}

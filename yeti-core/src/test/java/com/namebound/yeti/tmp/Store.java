package com.namebound.yeti.tmp;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

/**
 *
 * @author Laurian Gridinoc
 */
public class Store extends Resource {

    private static final String PREFIX      = "http://api.talis.com/stores/";
    private static final String METABOX     = "/meta";
    private static final String CONTENTBOX  = "/items";

    private String name;

    public Store (String name) throws MalformedURLException {
        super(PREFIX + name);
        this.name = name;
    }

    public Metabox metabox() throws MalformedURLException {
        return new Metabox(this, name + METABOX);
    }

    public Contentbox contentbox() throws MalformedURLException {
        return new Contentbox(this, name +CONTENTBOX);
    }

    public Resource augment(Resource resource, String xsl, String contentType) throws MalformedURLException, UnsupportedEncodingException {
        return new Resource(this, name + "/services/augment"
                    + parameter("data-uri", resource.url().toExternalForm(), "?")
                    + parameter("xsl", xsl, "&")
                    + parameter("content-type", contentType, "&")
                );
    }
    
}

package com.namebound.yeti.tmp;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

/**
 *
 * @author Laurian Gridinoc
 */
public class Contentbox extends Resource {

    public Contentbox(Resource context, String spec) throws MalformedURLException {
        super(context, spec);
    }

    public Resource query(String query, int max, int offset, String sort, String xsl, String contentType) throws MalformedURLException, UnsupportedEncodingException {
        return new Resource(this, 
                  parameter("query", query, "?")
                + parameter("max", max, "&")
                + parameter("offset", offset, "&")
                + parameter("sort", sort, "&")
                + parameter("xsl", xsl, "&")
                + parameter("contentType", contentType, "&")
                );
    }

    public Resource item(String id) throws MalformedURLException {
        return new Resource(this, "items/" + id);
    }
    

}

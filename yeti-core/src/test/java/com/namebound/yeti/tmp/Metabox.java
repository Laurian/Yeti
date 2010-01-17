package com.namebound.yeti.tmp;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

/**
 *
 * @author Laurian Gridinoc
 */
public class Metabox extends Resource {

    public Metabox(Resource context, String spec) throws MalformedURLException {
        super(context, spec);
    }

    public Resource about(Resource resource) throws MalformedURLException, UnsupportedEncodingException {
        return this.about(resource, null);
    }

    public Resource about(Resource resource, String output) throws MalformedURLException, UnsupportedEncodingException {
        return new Resource(this, "meta"
                + parameter("about", resource.url().toExternalForm(), "?")
                + parameter("output", output, "&")
                );
    }

    public Resource graphs() throws MalformedURLException {
        return new Resource(this, "meta/graphs");
    }

    public Resource graph() throws MalformedURLException {
        return this.graph(null);
    }
    
    public Resource graph(String id) throws MalformedURLException {
        if (id == null) {
            return new Graph(this, "");
        }
        return new Graph(this, "meta/graphs/" + id);
    }

    void post(Accessor accessor, Representation repr) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    void add(Accessor accessor, Representation repr) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


}

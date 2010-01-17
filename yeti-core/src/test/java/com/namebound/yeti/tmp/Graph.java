package com.namebound.yeti.tmp;

import java.net.MalformedURLException;

/**
 *
 * @author Laurian Gridinoc
 */
class Graph extends Resource {

    public Graph(Resource context, String spec) throws MalformedURLException {
        super(context, spec);
    }

    public Resource changesets() throws MalformedURLException {
        return new Resource(this, "graphs/ID/changesets"); //FIXME
    }
}

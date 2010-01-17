package com.namebound.yeti.tmp;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

/**
 *
 * @author Laurian Gridinoc
 */
public class Example {

    public static void main(String args[]) throws Exception {

        /*
         * The idea is to have two views over the API, a service level one,
         * following the REST API paths (/{store}/meta => store.metabox()),
         * and a resource/representation one, that allows URL composition and
         * permits dereferencing at any moment.
         */

        // a simple resource
        Resource example = new Resource("http://example.com/");

        // a store is just another resource, but it 'knows' how to construct
        // other sub-resources such as meta/contentbox and augment service
        Store store1 = new Store("lgridinoc-dev1");
        print(store1);

        
        Metabox meta = store1.metabox();
        print(meta);

        Contentbox content = store1.contentbox();
        print(content);

        // simple composition
        Resource about = meta.about(example);
        print(about);

        // more complex composition, augmenting the CBD of example resource
        Store store2 = new Store("lgridinoc-dev2");
        Resource augmented = about.augmentWith(store2, "some.xsl", "text/plain");
        print(augmented);
        // you can also do it the other way around
        augmented = store2.augment(about, "some.xsl", "text/plain");

        // at any time you can get to the logical 'parent', which is not '..'
        // for example /meta?about=uri has as parent /meta, where the form is.
        print(about.parent());

        // dereferencing a resource would yield a representation, the dereference
        // is performed by an accessor (a la NetKernel), think of HttpClient as
        // an HttpAccessor
        Representation repr = augmented.representation(new Accessor());
        // whis is an alias to GET
        repr = augmented.get(new Accessor());
        // and there is also post, put and delete.

        // now we can add data to the store
        meta.add(new Accessor(), repr);
        // which is an alias to POST
        meta.post(new Accessor(), repr);

        // now representations can be typed by the mime-type
        // imagine an RDFRepresentation that covers rdf/xml, turtle and ntriples
        // and when added to a metabox, the metabox can generate the changeset
        // acctually I would go for the NK transreptor idea
        // have an 'textual' representation parsed to a Jena model, now you
        // have an RDFRepresentation, which can pe transformed into a
        // ChangeSetRepresentation which is a subclass of an XMLRepresentation
       


    }

    public static void print(Resource resource) {
        Example.print(resource.url().toExternalForm());
    }

    public static void print(String line) {
        System.out.println(line);
    }


}

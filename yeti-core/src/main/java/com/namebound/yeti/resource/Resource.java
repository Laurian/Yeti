package com.namebound.yeti.resource;

import org.ontoware.rdf2go.model.node.URI;
import com.namebound.yeti.Context;
import com.namebound.yeti.representation.Representation;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

/**
 *
 * @author Laurian Gridinoc
 */
public class Resource extends URIImpl {

    protected Context context;
    protected Resource referrer;

    public Resource(Context context, String uri) {
        super(uri);
        this.context = context;
    }

    public Resource(Context context, URI uri) {
        this(context, uri.asJavaURI().toString());
    }

    public Resource(Resource resource) {
        this(resource.context(), resource);
    }

    public Resource(Resource resource, URI uri) {
        this(resource.context(), uri);
    }

    public Resource(URI uri) {
        this(new Context(), uri);
    }

    public Resource(String uri) {
        this(new Context(), uri);
    }

    public Resource(Context context, URI uri, String relative) {
        this(context, uri.asJavaURI().resolve(relative).toString());
        if (uri instanceof Resource) {
            referrer = (Resource) uri;
        } else {
            referrer = new Resource(context, uri);
        }
    }

    public Resource(Resource resource, URI uri, String relative) {
        this(resource.context(), uri, relative);
    }

    public Resource(Resource resource, String relative) {
        this(resource.context(), resource.asURI(), relative);
    }

    public Resource(URI uri, String relative) {
        this(new Context(), uri, relative);
    }

    public Resource context(Context context) {
        this.context = context;
        return this;
    }

    public Context context() {
        return context;
    }

    public Resource referrer() {
        return referrer;
    }

    public Resource referrer(Resource referrer) {
        this.referrer = referrer;
        return this;
    }

    public Representation representation(HttpUriRequest request) {
        if (referrer != null) request.setHeader("Referer", referrer.toString());
        try {
            return new Representation(this, context.httpClient().execute(request));
        } catch (IOException exception) {
            Logger.getLogger(Resource.class.getName()).log(Level.SEVERE, null, exception);
            return null;
        }
    }

    public Representation head() {
        HttpHead request = new HttpHead(super.toString());
        request.setHeader("Accept", "*/*");
        return head(request);
    }

    public Representation head(HttpHead request) {
        return representation(request);
    }

    public Representation get() {
        HttpGet request = new HttpGet(super.toString());
        request.setHeader("Accept", "*/*");
        return get(request);
    }

    public Representation get(HttpGet request) {
        return representation(request);
    }

    public Representation put(HttpPut request) {
        return representation(request);
    }

    public Representation put(Resource resource) {
        return put(resource.get());
    }

    public Representation put(Representation representation) {
        HttpPut request = new HttpPut(super.toString());
        request.setEntity(representation.entity());
        return put(request);
    }

    public Representation put(HttpPut request, Resource resource) {
        return put(request, resource.get());
    }

    public Representation put(HttpPut request, Representation representation) {
        request.setEntity(representation.entity());
        return put(request);
    }

    public Representation post(HttpPost request) {
        return representation(request);
    }

    public Representation post(Resource resource) {
        return post(resource.get());
    }

    public Representation post(Representation content) {
        HttpPost request = new HttpPost(super.toString());
        request.setEntity(content.entity());

        Representation representation = post(request);

        //set resource from from Location
        content.setRepresentationOf(representation.response());

        return representation;
    }

    public Representation post(HttpPost request, Representation representation) {
        request.setEntity(representation.entity());
        return post(request);
    }

    public Representation delete() {
        HttpDelete request = new HttpDelete(super.toString());
        request.setHeader("Accept", "*/*");
        return delete(request);
    }

    public Representation delete(HttpDelete request) {
        return representation(request);
    }

    public Resource tee(Resource copy) {
        copy = this;
        return this;
    }

    public Resource resolve(String relative) {
        return new Resource(this, relative);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new Resource(this);
    }
}

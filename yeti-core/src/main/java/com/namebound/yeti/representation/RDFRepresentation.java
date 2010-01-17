package com.namebound.yeti.representation;

import com.namebound.yeti.rdf.ModelProxy;
import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;

/**
 *
 * @author Laurian Gridinoc
 */
public class RDFRepresentation extends Representation {

    protected Model proxy;

    public Model model() {
        if (proxy != null) return proxy;

        ModelFactory modelFactory = RDF2Go.getModelFactory();
        Model model = modelFactory.createModel();
        try {
            model.open();
            if (super.entity() != null){
                model.readFrom(super.entity().getContent());
            }
        } catch (Exception ex) {
            Logger.getLogger(RDFRepresentation.class.getName()).log(Level.SEVERE, null, ex);
        }

        return model(model);
    }

    @Override
    public HttpEntity entity() {
        sync();
        return this.entity;
    }

    private Model model(Model model) {
        if (model instanceof ModelProxy) {
            this.proxy = model;
        } else {
            this.proxy = ModelProxy.newInstance(model);
        }
        return model();
    }

    private void sync() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            proxy.writeTo(buffer);
        } catch (Exception ex) {
            Logger.getLogger(RDFRepresentation.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.entity = new ByteArrayEntity(buffer.toByteArray());
    }
}

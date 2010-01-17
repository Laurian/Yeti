package com.namebound.yeti.rdf;

import com.namebound.yeti.resource.Resource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;

/**
 *
 * @author Laurian Gridinoc
 */
public class ModelProxy implements InvocationHandler {

    private Model model;

    public static Model newInstance(Model model) {
        return (Model) java.lang.reflect.Proxy.newProxyInstance(
                model.getClass().getClassLoader(),
                model.getClass().getInterfaces(),
                new ModelProxy(model));
    }

    private ModelProxy(Model model) {
        this.model = model;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        Object result;
        try {
            result = method.invoke(model, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        } catch (Exception e) {
            throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
        }

        if (result instanceof URI) {
            return new Resource((URI) result);

        } else if (result instanceof Statement){
            return StatementProxy.newInstance((Statement) result);
            
        } else if (result instanceof ClosableIterable) {
            final ClosableIterator<? extends Statement> iterator = ((ClosableIterable<? extends Statement>) result).iterator();
            return new ClosableIterable<Statement>() {
                @Override
                public ClosableIterator<Statement> iterator() {
                    return new ClosableIterator<Statement>() {

                        @Override
                        public boolean hasNext() {
                            return iterator.hasNext();
                        }

                        @Override
                        public Statement next() {
                            return StatementProxy.newInstance(iterator.next());
                        }

                        @Override
                        public void remove() {
                            iterator.remove();
                        }

                        @Override
                        public void close() {
                            iterator.close();
                        }
                    };
                }
            };
        }

        return result;
    }
}
